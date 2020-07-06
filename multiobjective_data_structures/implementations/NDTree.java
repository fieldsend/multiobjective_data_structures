package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

/**
 * NDTree, implementation of ND tree by Jaszkiewicz and Lust.
 * 
 * @author Jonathan Fieldsend
 * @version 1
 */
public class NDTree implements ParetoSetManager
{
    private NDTreeNode root;
    private int maxListSizePerNode = 20;
    private int numberOfChildrenPerNode;
    
    private NDTree(int numberOfObjectives) {
        numberOfChildrenPerNode = numberOfObjectives + 1;
        root = new NDTreeNode(maxListSizePerNode,numberOfChildrenPerNode);
    }
    
    private NDTree(int maxListSizePerNode, int numberOfChildrenPerNode) {
        this.maxListSizePerNode = maxListSizePerNode;
        this.numberOfChildrenPerNode = numberOfChildrenPerNode;
        root = new NDTreeNode(maxListSizePerNode,numberOfChildrenPerNode);
    }
    
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException 
    {
        if (root.isEmpty()) {
            root.add(s);
            return true;
        } else {
            if (root.updateNode(s,null)) { // returns true if solution not covered by any member in tree
                //System.out.println("Proposal not dominated by ND-Tree");
                if (root.isEmpty())
                    root.add(s); // Special case where s has dominated and cleared tree entirely
                else
                    root.insert(s);
                return true;
            }
        }
        return false;
    }
    
    /**
     * returns true if this pareto set weakly dominates s
     */
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (root.isEmpty()) {
            return false;
        } else {
            return root.weaklyDominates(s);
        }
    }
    
    /**
     * Returns contents of the set in an array.
     */
    public Collection<? extends Solution> getContents()
    {
        List<Solution> s = new ArrayList<Solution>(this.size());
        if (root != null)
            root.recursivelyExtract(s);
        return s;
    }
    
    /**
     * Returns the number of elements of the set (the number of non-dominated solutions). 
     */
    public int size() {
        return root.coverage();
    }
    
    /**
     * Cleans the set. Removes all elements.
     */
    public void clean()
    {
        root = null;
    }
    
    public Solution getRandomMember() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
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
    
    private int graphVizLinkToChildren(int parentIndex, int currentIndex, NDTreeNode current, StringBuilder sb, ArrayList<Integer> leafIndices, ArrayList<Integer> interiorIndices) {
        
        if (!current.isLeaf()) {
            for (NDTreeNode child : current.getChildren()) {
                sb.append(parentIndex + " -> " + currentIndex + "\n");
                interiorIndices.add(currentIndex); 
                currentIndex = graphVizLinkToChildren(currentIndex, currentIndex+1, child,sb,leafIndices,interiorIndices);
            }
        } else {
            for (int i = 0; i < current.coverage(); i++) {
                leafIndices.add(currentIndex);
                sb.append(parentIndex + " -> " + (currentIndex++) + "\n");
            }
        }
        return currentIndex;
    }
    
    public static ParetoSetManager managerFactory(int numberOfObjectives) {
        return new NDTree(numberOfObjectives);
    }
}
