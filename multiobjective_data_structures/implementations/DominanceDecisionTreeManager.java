package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.ArrayList;
import java.util.Collection;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * DominanceDecisionTreeManager class implements the Dominance decision tree of
 * 
 * Oliver Schütze. 2003. 
 * A New Data Structure for the Nondominance Problem in Multi-objective Optimization. 
 * In International Conference on Evolutionary Multi-Criterion Optimization, EMO 2003 
 * (LNCS), Vol. 2632. Springer, 509–518
 * 
 * @author Jonathan Fieldsend
 * @version 29/04/2019
 */
public class DominanceDecisionTreeManager implements ParetoSetManager
{
    private DDTNode root;
    public final int NUMBER_OF_OBJECTIVES; // number of objectives of solutions being managed
    private int size = 0;
    
    /*
     * Constructor used by factory method
     */
    private DominanceDecisionTreeManager(int numberOfObjectives) {
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
    }

    @Override
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (s.getNumberOfObjectives()!=NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("Manager set up for " + NUMBER_OF_OBJECTIVES 
                + " objectives, however solution added has "+ s.getNumberOfObjectives());

        if (root==null) {
            root = new DDTNode(s,null,-1);
            size = 1;
        } else {
            if (detectDomination(root,s))
                return false;
            // not dominated, so need to remove any and all current members than are dominated
            deleteDominated(root,s);
            if (size>0) 
                insert(root,new DDTNode(s));
            else // special case when entire tree has been removed
                root = new DDTNode(s,null,-1);
            size++;
        }
        return true;
    }

    @Override
    public Solution getRandomMember() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }
    
    private boolean detectDomination(DDTNode node,Solution s) {
        //System.out.println("compare " + s + " to " + node.getCargo());
        if (node.getCargo().weaklyDominates(s))
            return true;
        DDTNode[] children = node.getChildren();    
        for (int i=0; i<NUMBER_OF_OBJECTIVES; i++ ) 
            if (children[i] != null)
                if (node.getCargo().getFitness(i)<=s.getFitness(i))
                    if(detectDomination(children[i],s))
                        return true;

        return false;
    }

    private void deleteDominated(DDTNode node,Solution s) {

        DDTNode[] children = node.getChildren();    
        for (int i=0; i<NUMBER_OF_OBJECTIVES; i++ ) {
            if (children[i] != null)
                deleteDominated(children[i],s);
            if (s.getFitness(i)>node.getCargo().getFitness(i))
                break;
        }
        // all children of node have been processed by this point, and any dominated removed
        children = node.getChildren();    
        if (s.weaklyDominates(node.getCargo())) {
            if (node.isLeaf()) {
                node.detachAndReplace(null);
                size--;
                //System.out.println("detaching leaf" + node.getCargo());
                return;
            }
            int j=0;
            // find first valid offspring of node
            for (; j<NUMBER_OF_OBJECTIVES; j++) 
                if (children[j] != null)
                    break;
            DDTNode parent = node.getParent();  
            //if (parent!=null)
            //    System.out.println(parent.getCargo() + "replacing " + node.getCargo() + "by its subtree" + children[j].getCargo());
            size--;

            if (parent==null) {// special case when deleted node is top of tree
                //System.out.println("DELETING root, new root: " + children[j].getCargo());
                root = children[j];
                root.setParent(null,-1);
                parent = root;
            } else 
                parent.setChild(node.getIndexAtParent(),children[j]);
            
            for (j++; j<NUMBER_OF_OBJECTIVES; j++)
                if (children[j] != null)
                    treeInsert(parent,children[j]); //resinsert node

        }
    }

    private void treeInsert(DDTNode baseParent, DDTNode node) {
        node.detach();
        DDTNode[] children = node.getChildren();    
        for (DDTNode child : children) //strip and process children
            if (child!=null)
                treeInsert(baseParent,child);
        insert(baseParent,node);
    }

    private void insert(DDTNode baseParent, DDTNode node) {
        int i=0;
        for (; i<NUMBER_OF_OBJECTIVES; i++ ) 
            if (node.getCargo().getFitness(i) > baseParent.getCargo().getFitness(i))
                break;
        //System.out.println(node.getCargo() + " -- " + baseParent.getCargo() + " -- "+ i);        
        if (baseParent.getChild(i)!=null)        
            insert(baseParent.getChild(i),node);
        else {
            baseParent.setChild(i,node);
        }

    }

    @Override
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (s.getNumberOfObjectives()!=NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("Manager set up for " + NUMBER_OF_OBJECTIVES 
                + " objectives, however solution added has "+ s.getNumberOfObjectives());

        if (root==null) 
            return false;
        else {
            return detectDomination(root,s);
        }
    }

    @Override
    public Collection<? extends Solution> getContents() {
        ArrayList<Solution> contents = new ArrayList<>();
        recursivelyExtract(root,contents);
        return contents;
    }

    private void recursivelyExtract(DDTNode node, ArrayList<Solution> contents) {
        contents.add(node.getCargo());
        for (DDTNode child : node.getChildren())
            if (child != null)
                recursivelyExtract(child, contents);
    }

    /**
     * Factory method to return and instance of ParetoSetManager to maintain the
     * solutions with the number of objectives passed as an argument
     */
    public static DominanceDecisionTreeManager managerFactory(int numberOfObjectives) {
        return new DominanceDecisionTreeManager(numberOfObjectives);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clean() {
        root = null;
        size = 0;
    }
    
    @Override
    public String toString() {
        String s="Root :";
        if (root==null) 
            s += "empty";
        else 
            s += recursivelyGetParentAndChildDetails(root);
        
        return s;
    }
    
    private String recursivelyGetParentAndChildDetails(DDTNode node) {
        String s = " " + node.getCargo();
        DDTNode[] children =  node.getChildren();
        for (int i=0; i<NUMBER_OF_OBJECTIVES; i++)
            if (children[i]!=null)
                s += "C" +i + " "+ recursivelyGetParentAndChildDetails(children[i]);
        return s + "\n";
    }
    
   
    @Override
    public void writeGraphVizFile(String filename) throws FileNotFoundException, UnsupportedOperationException {
        StringBuilder sb = new StringBuilder();

        StringBuilder nodes = new StringBuilder();
        StringBuilder graph = new StringBuilder();
        
        sb.append("digraph D {\n");
        
        int index = 0;
        // link nodes
        if (root != null) {
            graphVizLinkToChildren(0, 1, root,graph);
        }
        
        // define nodes    
        for (int i=0; i<size(); i++){
            nodes.append(i +" [shape=box fillcolor=yellow]\n");
        }
        
        sb.append(nodes);
        sb.append(graph);
        sb.append("}");
        PrintWriter pw = new PrintWriter(new File(filename));
        pw.write(sb.toString());
        pw.close();
    }  
    
    private int graphVizLinkToChildren(int parentIndex, int currentIndex, DDTNode current, StringBuilder sb) {
        
        if (!current.isLeaf()) {
            DDTNode[] children = current.getChildren();
            for (DDTNode child : children) {
                if (child != null) {
                    sb.append(parentIndex + " -> " + currentIndex + "\n");
                    currentIndex = graphVizLinkToChildren(currentIndex, currentIndex+1, child,sb);
                }
            }
        }
        return currentIndex;
    }
    
}
