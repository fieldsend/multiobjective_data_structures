package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Implementation of the BSPTreeArchiveManager, based on:
 * 
 * Tobias Glasmachers. 2017. 
 * A Fast Incremental BSP Tree Archive for Nondominated Points. 
 * In International Conference on Evolutionary Multi-Criterion Optimization, EMO 2017 (LNCS), 
 * H. Trautmann et al. (Ed.), Vol. 10173. Springer, 252--266
 * 
 * @author Jonathan Fieldsend
 * @version 1.0
 */
public class BSPTreeArchiveManager implements ParetoSetManager
{
    private BSPTreeNode root;
    private int maxLeafSize = 20;
    private final int NUMBER_OF_OBJECTIVES;
    private double rebalanceFactor = 6; // called 'z' in the original paper

    /*
     * Constructor to be called by factory method
     */
    private BSPTreeArchiveManager(int numberOfObjectives) {
        root = new BSPTreeNode(new ArrayList<Solution>(maxLeafSize+1), numberOfObjectives, null);
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
    }
    
    /*
     * Constructor to be called by factory method
     */
    private BSPTreeArchiveManager(int numberOfObjectives, int maxLeafSize) {
        root = new BSPTreeNode(new ArrayList<Solution>(maxLeafSize+1), numberOfObjectives, null);
        this.maxLeafSize = maxLeafSize;
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
    }

    @Override
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (s.getNumberOfObjectives() != NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("Archive maintains solutions with " 
                + NUMBER_OF_OBJECTIVES + " number of objectives, not " + s.getNumberOfObjectives());

        //System.out.println(".");        
        if (checkDominance(root,s,new TreeSet<Integer>(),new TreeSet<Integer>()) < 0 )
            return false;

        BSPTreeNode node =  root;
        whileLoop: while (node.isInteriorNode()) {
            if (node.isImbalanced(rebalanceFactor)) { // check if node has reached a state needing rebalancing
                node.rebalance(maxLeafSize); // rebalance before processing further
                //node.printTreeBalance();
                if (!node.isInteriorNode())
                    break whileLoop; // rebalancing has now made node a leaf, so need to break out
                //node.incrementNumberCovered();
            }
            node.incrementNumberCovered();
            node = node.getChild(s);
        }
        node.addToSet(s,maxLeafSize);
        /*if (root.countInconsistentCoverage() > 0) {
        System.out.println("\n >>INCONSITENCY after total tree rebalance");
        root.printTreeBalance();
        throw new RuntimeException("inconsistencey");
        }*/
        return true;
    }

    /*
     * Helper method for dominance checks
     */
    private int processLeafForDominanceCheck(BSPTreeNode node, Solution s)
    {
        int k=0;
        Iterator<Solution> iterator = node.getCoveredSet().iterator();
        while (iterator.hasNext()) {
            Solution member  = iterator.next();
            //System.out.println("Comparing " + member +" to "+ s);
            if (member.weaklyDominates(s))
                return -1;
            /* Next line is effectively a dominates check, as would have returned 
             * if equal already, weakDom at this point is quicker
             */    
            if (s.weaklyDominates(member)) { 
                iterator.remove();
                node.decrementNumberCovered();
                k++;
            }
        }
        return k;
    }

    /*
     * Helper method for dominance checks
     */
    private int checkDominance(BSPTreeNode node, Solution s,  TreeSet<Integer> b, TreeSet<Integer> w) {
        int k = 0;
        if (!node.isInteriorNode()) {
            k = processLeafForDominanceCheck(node,s);
        } else { // continue down tree to a leaf
            TreeSet<Integer> wPrime;
            TreeSet<Integer> bPrime; 
            // update B to B' or W to W'
            if (s.getFitness(node.getObjectiveIndex()) < node.getTheta()) {
                bPrime  = new TreeSet<>(b); 
                bPrime.add(node.getObjectiveIndex());
                wPrime  = w;
            } else { 
                wPrime  = new TreeSet<>(w);
                wPrime.add(node.getObjectiveIndex());
                bPrime = b;
            }
            if (wPrime.size() == NUMBER_OF_OBJECTIVES)   
                return -1;
            else if (bPrime.size() == NUMBER_OF_OBJECTIVES) {
                k += node.getRight().getNumberCovered();
                node.getRight().removeAllCovered();
            } else {
                //System.out.println(wPrime.size() + " "+ w.size() +  " " + bPrime.size() + " "+ b.size());
                if ((wPrime.size()==0) || (b.size()==0)) {
                    //System.out.println("Check left index " + node.getObjectiveIndex() + " < " + node.getTheta() + " " + wPrime.size() + " "+ b.size());
                    int v = checkDominance(node.getLeft(),s,b,wPrime);
                    if (v<0) {
                        //System.out.println("DOMINATED");
                        return -1;
                    }
                    k+=v;    
                }
                if ((bPrime.size()==0) || (w.size()==0)) {
                    //System.out.println("Check right index " + node.getObjectiveIndex() + " >= " + node.getTheta() + " "+ w.size() + " " +bPrime.size());
                    int v = checkDominance(node.getRight(),s,bPrime,w);
                    if (v<0) {
                        //System.out.println("DOMINATED");
                        return -1;
                    }
                    k+=v; 
                }
            }
            node.decrementNumberCovered(k);

            //check if left has contents and right is empty, as can rerrange and detach
            if ((node.getLeft().getNumberCovered() > 0) && (node.getRight().getNumberCovered() == 0)) {
                node.replaceWithLeftChild();
            }
            // check if right has contents and left is empty, as can rearrange and detach
            else if ((node.getRight().getNumberCovered() > 0) && (node.getLeft().getNumberCovered() == 0)) {
                node.replaceWithRightChild();
            }
        }
        return k;
    }

   /*
    * Helper method to check for dominance without changing the state of the tree
    */
    private int checkForDominanceWithoutChangingState(BSPTreeNode node, Solution s,  TreeSet<Integer> b, TreeSet<Integer> w) {
        int k = 0;
        if (!node.isInteriorNode()) {
            k = processLeafForDominanceCheck(node,s);
        }
        if (node.isInteriorNode()) {
            TreeSet<Integer> wPrime;
            TreeSet<Integer> bPrime; 
            // update B to B' and W to W'
            if (s.getFitness(node.getObjectiveIndex()) < node.getTheta()) {
                bPrime  = new TreeSet<>(b); 
                b.add(node.getObjectiveIndex());
                wPrime  = w;
            } else { 
                wPrime  = new TreeSet<>(w);
                w.add(node.getObjectiveIndex());
                bPrime = b;
            }
            if (w.size() == NUMBER_OF_OBJECTIVES)   
                return -1;
            else if (b.size() == NUMBER_OF_OBJECTIVES) {
                k += node.getRight().getNumberCovered();
                node.getRight().removeAllCovered();
            } else {
                if ((wPrime.size()==0) || (b.size()==0)) {
                    int v = checkForDominanceWithoutChangingState(node.getLeft(),s,b,wPrime);
                    if (v<0)
                        return -1;
                    k+=v;
                }
                if ((bPrime.size()==0) || (w.size()==0)) {
                    int v = checkForDominanceWithoutChangingState(node.getRight(),s,bPrime,w);
                    if (v<0)
                        return -1;
                    k+=v;
                }
            }
        }
        return k;
    }

    @Override
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        return (checkForDominanceWithoutChangingState(root,s,new TreeSet<Integer>(),new TreeSet<Integer>())<0);
    }

    @Override
    public Collection<? extends Solution> getContents() {
        ArrayList<Solution> contents = new ArrayList<Solution>(this.size());
        recursivelyFillWithContents(root, contents);
        return contents;
    }

    /*
     * Helper method to recusrively filled the arraylist argument with the node contents, and the subtree rooted at it
     */
    private void recursivelyFillWithContents(BSPTreeNode node, ArrayList<Solution> contents) {
        if (node.isInteriorNode()) {
            recursivelyFillWithContents(node.getLeft(), contents);
            recursivelyFillWithContents(node.getRight(), contents);
        } else {
            contents.addAll(node.getCoveredSet());
        }
    }

    /*
     *  Get number of deep covered 
     */
    private int deepGetNumberCovered() {
        return root.getDeepCovered();
    }

    @Override
    public int size() {
        return root.getNumberCovered();
    }

    @Override
    public void clean() {
        root = null;
    }

    @Override
    public String toString() {
        return rescursivelyAddToString(root," ");
    }

    @Override
    public Solution getRandomMember() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /*
     * Helper method for string representation of node 
     */
    private String rescursivelyAddToString(BSPTreeNode node, String s) {
        if (node.isInteriorNode()) {
            s += " index " + node.getObjectiveIndex() + " < " + node.getTheta() + " " + rescursivelyAddToString(node.getLeft(), s);
            s += " index " + node.getObjectiveIndex() + " >= " + node.getTheta() + " " + rescursivelyAddToString(node.getRight(), s);
        } else {
            s += "\n contents: ";
            Collection<? extends Solution> temp = node.getCoveredSet();
            for (Solution member : temp)
                s += " " + member + ", ";
        }
        return s;
    }

    
    @Override
    public void writeGraphVizFile(String filename) throws FileNotFoundException, UnsupportedOperationException {
        StringBuilder sb = new StringBuilder();

        StringBuilder nodes = new StringBuilder();
        StringBuilder graph = new StringBuilder();
        
        sb.append("digraph D {\n");
        
        int index = 0;
        ArrayList<Integer> interiorIndices = new ArrayList<>();
        ArrayList<Integer> leafIndices = new ArrayList<>();
        interiorIndices.add(0); 
        // link nodes
        if (root != null) {
            graphVizLinkToChildren(0, 1, root,graph,leafIndices,interiorIndices);
        }
        
        // define nodes    
        for (int i : leafIndices){
            nodes.append(i +" [shape=box fillcolor=yellow]\n");
        }
        for (int i : interiorIndices){
            nodes.append(i +" [shape=oval fillcolor=red]\n");
        }
        
        sb.append(nodes);
        sb.append(graph);
        sb.append("}");
        PrintWriter pw = new PrintWriter(new File(filename));
        pw.write(sb.toString());
        pw.close();
    }  
    
    /*
     * Helper method for GrpahViz file construction
     */
    private int graphVizLinkToChildren(int parentIndex, int currentIndex, BSPTreeNode current, StringBuilder sb, ArrayList<Integer> leafIndices, ArrayList<Integer> interiorIndices) {
        
        if (current.isInteriorNode()) {
            sb.append(parentIndex + " -> " + currentIndex + "\n");
            interiorIndices.add(currentIndex); 
            currentIndex = graphVizLinkToChildren(currentIndex, currentIndex+1, current.getLeft(),sb,leafIndices,interiorIndices);
            sb.append(parentIndex + " -> " + currentIndex + "\n");
            interiorIndices.add(currentIndex); 
            currentIndex = graphVizLinkToChildren(currentIndex, currentIndex+1,current.getRight(),sb,leafIndices,interiorIndices);
        } else {
            for (int i = 0; i < current.getNumberCovered(); i++) {
                leafIndices.add(currentIndex);
                sb.append(parentIndex + " -> " + (currentIndex++) + "\n");
            }
        }
        return currentIndex;
    }
    
    /**
     * Returns instance of the manager for the number of objectives argument and
     * max leaf size passed
     */
    public static ParetoSetManager managerFactory(int numberOfObjectives, int maxLeafSize) {
        return new BSPTreeArchiveManager(numberOfObjectives,maxLeafSize);
    }

    /**
     * Returns instance of the manager for the number of objectives argument, with the default
     * max leaf size of 20
     */
    public static ParetoSetManager managerFactory(int numberOfObjectives) {
        return new BSPTreeArchiveManager(numberOfObjectives);
    }
}
