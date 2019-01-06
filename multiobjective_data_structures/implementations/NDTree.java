package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * NDTree, implementation of ND tree by Jaszkiewicz and Lust.
 * 
 * @author Jonathan Fieldsend
 * @version 1
 */
public class NDTree implements ParetoSetManager
{
    private NDTreeNode root;
    private int maxListSizePerNode = 10;
    private int numberOfChildrenPerNode = 10;
    
    private NDTree() {
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
    
    
    public static ParetoSetManager managerFactory() {
        return new NDTree();
    }
}
