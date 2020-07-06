package multiobjective_data_structures;

import java.util.Collection;
import java.io.FileNotFoundException;

/**
 * ParetoSetManager. Describes methods that all managers of Pareto sets need to provide.
 * 
 * All implementations should provide a no argument constructor that provides an empty
 * initial ParetoSetManager.
 * 
 * @author Jonathan Fieldsend
 * @version 1.0
 */
public interface ParetoSetManager
{
    /**
     * Attempts to add the argument to the current set. Will return <code>true<code> 
     * if the solution is added (non-weakly dominated by the set), returns false if it 
     * is not added (as it is weakly dominated by the set).  
     */
    boolean add(Solution s) throws IllegalNumberOfObjectivesException;
    
    /**
     * returns true if this pareto set weakly dominates s
     */
    boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException;
    
    /**
     * Returns contents of the set in an array.
     */
    Collection<? extends Solution> getContents();
    
    /**
     * Returns the number of elements of the set (the number of non-dominated solutions). 
     */
    int size();
    
    /**
     * Cleans the set. Removes all elements.
     */
    void clean();
    
    /**
     * Optional method to get a random member of the maintained archive. 
     * Throws an UnsupportedOperationException instance if this operation is not supported
     * by an implementation
     */
    Solution getRandomMember() throws UnsupportedOperationException;
  
    
    /**
     * Optional method to write to a file a GraphViz '.dot' file representation of the current
     * state of the data structure representing this archive. 
     * Throws an UnsupportedOperationException instance if this operation is not supported
     * by an implementation
     */
    void writeGraphVizFile(String filename) throws FileNotFoundException, UnsupportedOperationException;
    
    
    /**
     * Replaces the contents of this set, with that mantained in the argument.
     */
    default void replace(ParetoSetManager m) throws IllegalNumberOfObjectivesException {
        this.clean();
        for (Solution s : m.getContents())
            this.add(s);
    }
    
    
}
