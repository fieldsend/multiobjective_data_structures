import java.util.Collection;
import java.util.ArrayList;
import java.util.Random;

/**
 * Write a description of class LinearListManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LinearListManager implements ParetoSetManager
{
    private final int NUMBER_OF_OBJECTIVES;
    private ArrayList<Solution> contents = new ArrayList<>();
    private Random randomNumberGenerator;
    
    private LinearListManager(long seed, int numberOfObjectives)
    {
        randomNumberGenerator = new Random(seed);
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
    }
    
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
        for (int i=contents.size()-1; i>=0; i--)
            if (s.dominates(contents.get(i)))
                contents.remove(i);
    }
    
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        for (Solution c : contents)
            if (c.weaklyDominates(s))
                return true;
        return false;        
    }
    
    public Collection<? extends Solution> getContents() 
    {
        return contents;
    }
    
    public Solution getRandomMember()
    {
        int r = randomNumberGenerator.nextInt(contents.size());
        return contents.get(r);
    }
    
    public int size(){
        return contents.size();
    }
    
    public void clean()
    {
        contents.clear();
    }
    
    public static ParetoSetManager managerFactory(long seed, int numberOfObjectives) throws IllegalNumberOfObjectivesException
    {
        //if (numberOfObjectives != 2)
        //    throw new IllegalNumberOfObjectives("BiObjectiveSetManager can only manage solutions with two objectives");
        return new LinearListManager(seed, numberOfObjectives);
    }
}
