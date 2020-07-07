package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * LinearListManager stores and manages a non-dominated set as a linear list.
 * 
 * @author Jonathan Fieldsend
 * @version 1.0
 */
public class LinearListManager implements ParetoSetManager
{
    private final int NUMBER_OF_OBJECTIVES;
    private List<Solution> contents = new ArrayList<>();
    private Random randomNumberGenerator;
    
    /*
     * Constructor takes in seed for the random number generator used when
     * calling getRandomMember(), the number of objectives of solutions to store. 
     * Uses an ArrayList to store the archive
     */
    private LinearListManager(long seed, int numberOfObjectives)
    {
        randomNumberGenerator = new Random(seed);
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
        contents = new ArrayList<>();
    }
    
    /*
     * Constructor takes in seed for the random number generator used when
     * calling getRandomMember(), the number of objectives of solutions to store, and 
     * a flag for whether an array list (true) or linked list (false) is used. 
     * Empirically the array list option performs better on the sequences tested.
     */
    private LinearListManager(long seed, int numberOfObjectives, boolean array)
    {
        this(seed, numberOfObjectives);
        if (!array)
            contents = new LinkedList<>();
    }
    
    @Override
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException
    {
        // first check not weakly dominated
        if (weaklyDominates(s))
            return false;
        removeDominated(s);    
        return contents.add(s);
    }
    
    
    private void removeDominated(Solution s) throws IllegalNumberOfObjectivesException
    {
        for (int i=contents.size()-1; i>=0; i--) {
            if (s.dominates(contents.get(i))) {
                //System.out.println("Dominated: " + contents.get(i));
                contents.remove(i);
            }
        }
    }
    
    @Override
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        for (Solution c : contents)
            if (c.weaklyDominates(s))
                return true;
        return false;        
    }
    
    @Override
    public Collection<? extends Solution> getContents() 
    {
        return contents;
    }
    
    @Override
    public Solution getRandomMember() throws UnsupportedOperationException
    {
        int r = randomNumberGenerator.nextInt(contents.size());
        return contents.get(r);
    }
    
    @Override
    public int size(){
        return contents.size();
    }
    
    @Override
    public void clean()
    {
        contents.clear();
    }
    
    /**
     * Returns an instance of LinearList manager. Takes in seed for the random number generator used when
     * calling getRandomMember(), the number of objectives of solutions to store. 
     * Uses an ArrayList to store the archive
     */
    public static ParetoSetManager managerFactory(long seed, int numberOfObjectives) throws IllegalNumberOfObjectivesException
    {
        return new LinearListManager(seed, numberOfObjectives);
    }
    
    /**
     * Returns an instance of LinearList manager. Takes in seed for the random number generator used when
     * calling getRandomMember(), the number of objectives of solutions to store, and 
     * a flag for whether an array list (true) or linked list (false) is used. 
     * Empirically the array list option performs better on the sequences tested.
     */
    public static ParetoSetManager managerFactory(long seed, int numberOfObjectives, boolean array) throws IllegalNumberOfObjectivesException
    {
        return new LinearListManager(seed, numberOfObjectives, array);
    }
    
    @Override
    public void writeGraphVizFile(String filename) throws FileNotFoundException, UnsupportedOperationException {
        StringBuilder sb = new StringBuilder();

        sb = new StringBuilder();
        
        sb.append("digraph D {\n");
        // define nodes    
        for (int i=0; i< contents.size(); i++) {
            sb.append(i +" [shape=box fillcolor=yellow]\n");
        }
        // link nodes
        for (int i=0; i< contents.size()-1; i++) {
            sb.append(i + " -> " + (i+1) + "\n");
        }
        sb.append("}");
        PrintWriter pw = new PrintWriter(new File(filename));
        pw.write(sb.toString());
        pw.close();
    }
}
