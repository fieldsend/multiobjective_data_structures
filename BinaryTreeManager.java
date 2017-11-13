import java.util.Random;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Iterator;
import java.util.Set;

/**
 * BinaryTreeManager -- implementation of ParetoSetManager 
 * 
 * 
 * @author Jonathan Fieldsend 
 * @version 1.0
 */
public class BinaryTreeManager implements ParetoSetManager
{
    private final int NUMBER_OF_OBJECTIVES = 2;
    private TreeSet<ComparableSolutionSingleValue> contents = new TreeSet<>();
    private Random randomNumberGenerator;
    
    private BinaryTreeManager(long seed)
    {
        randomNumberGenerator = new Random(seed);
    }
    @Override
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectives
    {
        
        Solution element = contents.floor(new ComparableSolutionAllValues(s,0)); // get element in set less than or equal to s on first objective with greatest first objective value    
        if (element!=null)
            return element.weaklyDominates(s);
           
        return false;
    }
            
    @Override
    public boolean add(Solution s) throws IllegalNumberOfObjectives
    {
        // first check not weakly dominated
        if (weaklyDominates(s))
            return false;
        // argument s is not dominated, so now need to remove all dominated elements of the set
        ComparableSolutionSingleValue n = new ComparableSolutionSingleValue(s,0);
        // extract substree from first potential dominated point onwards
        SortedSet<ComparableSolutionSingleValue> subTree = contents.tailSet(n);
        Iterator<ComparableSolutionSingleValue> iterator = subTree.iterator();
        // now iterator over this, reoving each dominated element in turn, until first non-dominated element 
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
    public Set<? extends Solution> getContents()
    {
        return contents;
    }
    
    @Override
    public Solution getRandomMember()
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
    
    //@Override
    public static ParetoSetManager managerFactory(long seed) throws IllegalNumberOfObjectives
    {
        //if (numberOfObjectives != 2)
        //    throw new IllegalNumberOfObjectives("BiObjectiveSetManager can only manage solutions with two objectives");
        return new BiObjectiveSetManager(seed);
    }
    
}
