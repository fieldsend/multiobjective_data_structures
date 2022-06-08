package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Random;

/**
 * NDTree, implementation of ND tree by Jaszkiewicz and Lust. Based on
 * 
 * Andrzej Jaszkiewicz and Thibaut Lust. 2018. 
 * ND-Tree-Based Update: A Fast Algorithm for the Dynamic Nondominance Problem. 
 * IEEE Transactions on Evolutionary Computation 22, 5 (2018), 778--791
 * 
 * @author Jonathan Fieldsend
 * @version 1
 */
public class NDTree implements ParetoSetManager
{
    private NDTreeNode root;
    private int maxListSizePerNode = 20;
    private int numberOfChildrenPerNode;
    private Random rng = new Random();
    
    /*
     * Constructor -- called by factory method
     */
    private NDTree(int numberOfObjectives) {
        numberOfChildrenPerNode = numberOfObjectives + 1;
        root = new NDTreeNode(maxListSizePerNode,numberOfChildrenPerNode);
    }
    
    /*
     * Constructor -- called by factory method
     */
    private NDTree(int maxListSizePerNode, int numberOfChildrenPerNode) {
        this.maxListSizePerNode = maxListSizePerNode;
        this.numberOfChildrenPerNode = numberOfChildrenPerNode;
        root = new NDTreeNode(maxListSizePerNode,numberOfChildrenPerNode);
    }
    
    @Override
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
    
    @Override
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (root.isEmpty()) {
            return false;
        } else {
            return root.weaklyDominates(s);
        }
    }
    
    /**
     * Returns true if at least one member of the archive dominates the argument. If no members 
     * dominate, returns false
     */
    public boolean dominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (root.isEmpty()) {
            return false;
        } else {
            return root.dominates(s);
        }
    }
    
    @Override
    public Collection<? extends Solution> getContents()
    {
        List<Solution> s = new ArrayList<Solution>(this.size());
        if (root != null)
            root.recursivelyExtract(s);
        return s;
    }
    
    public Solution[] getContentsInArray()  {
        return getContents().toArray(new Solution[0]);
    }
    
    public double[] getEstimatedIdeal() {
        if (root == null)
            return null;
        return root.getEstimatedIdeal();    
    }
    
    public double[] getEstimatedNadir() {
        if (root == null)
            return null;
        return root.getEstimatedNadir();    
    }
    
    public double[] getMidpoint() {
        if (root == null)
            return null;
        return root.getMidpoint();    
    }
    
    @Override
    public int size() {
        return root.coverage();
    }
    
    @Override
    public void clean()
    {
        root = null;
    }
    
    @Override
    public Solution getRandomMember() throws UnsupportedOperationException
    {
        if (size() == 0)
            return null; //empty tree, so return null
        return root.getRandom(rng);
    }
    
    /**
     * Returns a list of solutions residing in one of the NDTree's leaves 
     */
    public List<Solution> getRandomLeaf() {
        if (size() == 0)
            return null; //empty tree, so return null
        return root.getRandomLeaf(rng);
    }
    
    
    /**
     * Returns the solution extremising the index objective
     */
    public Solution getExtremeMember(int index) 
    {
        if (size() == 0)
            return null; //empty tree, so return null
        ArrayList<Solution> solutions = new ArrayList<>();    
        root.getExtremeMember(solutions,index);
        Solution extreme = solutions.get(0);
        for (int i=1; i<solutions.size(); i++)
            if (solutions.get(i).getFitness(index) < extreme.getFitness(index))
                extreme = solutions.get(i);
        return extreme;
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
     * Helper method for the GraphViz file writer
     */
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
    
    /**
     * Returns an NDTree instance to maintain an archive with the number of objectives 
     * passed as an argument. Uses a default bin size of 20 in leaves
     */
    public static ParetoSetManager managerFactory(int numberOfObjectives) {
        return new NDTree(numberOfObjectives);
    }
    
    /**
     * Returns an NDTree instance to maintain an archive with the number of objectives 
     * passed as an argument, and the number of solutions binned per leaf node
     */
    public static ParetoSetManager managerFactory(int numberOfObjectives,int numberOfChildrenPerNode) {
        return new NDTree(numberOfObjectives,numberOfChildrenPerNode);
    }
    
    /**
     * Returns an NDTree instance to maintain an archive with the number of objectives 
     * passed as an argument. Uses a default bin size of 20 in leaves
     */
    public static NDTree ndtreeFactory(int numberOfObjectives) {
        return new NDTree(numberOfObjectives);
    }    
}
