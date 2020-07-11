package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;
import multiobjective_data_structures.implementations.tests.ProxySolution;

import java.util.Random;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.io.FileNotFoundException;

/**
 * BiObjectiveSetManager -- implementation of ParetoSetManager that is optimised for bi-objective problems
 * given the special ordering properties of these (sorting in one objective is equivalent to reverse sorting
 * by the other). As such finding if dominated is e.g. O(log(n))
 * 
 * 
 * @author Jonathan Fieldsend 
 * @version 1.0
 */
public class BiObjectiveSetManager implements ParetoSetManager
{
    private final int NUMBER_OF_OBJECTIVES = 2;
    private TreeSet<ComparableSolutionSingleValue> contents = new TreeSet<>();
    private Random randomNumberGenerator;
    
    public BiObjectiveSetManager(long seed)
    {
        randomNumberGenerator = new Random(seed);
    }
    
    @Override
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (s.getNumberOfObjectives()!=NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("BiObjectiveSetManager can only manage solutions with two objectives");
        
        Solution element = contents.floor(new ComparableSolutionSingleValue(s,0)); // get element in set less than or equal to s on first objective with greatest first objective value    
        if (element!=null)
            return element.weaklyDominates(s);
           
        return false;
    }
            
    @Override
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException
    {
        // first check not weakly dominated
        if (weaklyDominates(s))
            return false;
        // argument s is not dominated, so now need to remove all dominated elements of the set
        ComparableSolutionSingleValue n = new ComparableSolutionSingleValue(s,0);
        // extract substree from first potential dominated point onwards
        SortedSet<ComparableSolutionSingleValue> subTree = contents.tailSet(n);
        Iterator<ComparableSolutionSingleValue> iterator = subTree.iterator();
        // now iterator over this, removing each dominated element in turn, until first non-dominated element 
        // reached, in which case break out
        while (iterator.hasNext()){
            ComparableSolutionSingleValue e = iterator.next();
            if (s.dominates(e))
                iterator.remove();
            else
                break;    
        }
        // now add    
        return contents.add(n);
    }
    
    @Override
    public Collection<? extends Solution> getContents()
    {
        ArrayList<Solution> list = new ArrayList<>(contents.size());
        for (ComparableSolutionSingleValue s : contents)
            list.add(s.getDecoratedSolution());
        return list;
    }
    
    @Override
    public Solution getRandomMember() throws UnsupportedOperationException
    {
        if (contents.size()==0)
            return null;
        if (contents.size()==1)
            return contents.first();
        double d = randomNumberGenerator.nextDouble();
        ComparableSolutionSingleValue head = contents.first();
        ComparableSolutionSingleValue tail = contents.last();
        double range = tail.getFitness(0)-head.getFitness(0);
        double offset = 1.0/contents.size();
        // get query solution to find next set member larger on first criteria
        // need to have chance of getting minimial solution on first criteria, so generates random value between
        // minimum -(1/n*range of criteria) and maximum
        Solution mock = new ProxySolution(new double[]{(d+offset)*range+head.getFitness(0)-offset*range, 0});
        
        return contents.higher(new ComparableSolutionSingleValue(mock,0)); //return contents.size()==0 ? null : contents.get(Random.nextInt(contents.size()));
    }
    
    @Override
    public int size()
    {
        return contents.size();
    }
    
    @Override
    public void clean()
    {
        contents = new TreeSet<>();
    }
    
    @Override
    public void writeGraphVizFile(String filename) throws FileNotFoundException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }  
    
    /**
     * Factory method to return and instance of ParetoSetManager to maintain the
     * solutions. Takes a seed used by the class when returning a random member
     */
    public static ParetoSetManager managerFactory(long seed) throws IllegalNumberOfObjectivesException
    {
        return new BiObjectiveSetManager(seed);
    }
    
    
    
}
