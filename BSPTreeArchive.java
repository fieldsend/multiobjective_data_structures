import java.util.ArrayList;
import java.util.Collection;

/**
 * Write a description of class BSPTreeArchive here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BSPTreeArchive implements ParetoSetManager
{
    private BSPTreeNode root;
    private int maxLeafSize;
    private final int NUMBER_OF_OBJECTIVES;
    
    BSPTreeArchive(int numberOfObjectives, int maxLeafSize) {
        root = new BSPTreeNode(new ArrayList<Solution>(maxLeafSize+1));
        this.maxLeafSize = maxLeafSize;
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
    }
    
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (s.getNumberOfObjectives() != NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("ARchive maintains solutions with " 
            + NUMBER_OF_OBJECTIVES + " number of objectives, not " + s.getNumberOfObjectives());
        if (checkDominance(root,s,0,0) < 0 )
            return false;
        BSPNode N =  root;
        while (N.isInteriorNode()) {
            N.incrementNumberCovered();
            N = N.getChild(s);
        }
        N.addToSet(S,maxLeafSize);
    }
    
    private boolean checkDominance() {
        
    }
    
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        return true;
    }
    
    public Collection<? extends Solution> getContents() {
        return new ArrayList<Solution>();
    }
    
    public Solution getRandomMember(){
         return null;
    }
    
    
    public int size() {
        return root.getNumberCovered();
    }
    
    
    public void clean() {
        root = null;
    }
    
    public static ParetoSetManager managerFactory(int numberOfObjectives, int maxLeafSize) {
        return new BSPTreeArchive(numberOfObjectives,maxLeafSize);
    }
    
    public static ParetoSetManager managerFactory(int numberOfObjectives) {
        return new BSPTreeArchive(numberOfObjectives,1);
    }
}
