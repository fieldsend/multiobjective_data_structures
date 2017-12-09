import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Write a description of class FETreeManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FETreeManager implements ParetoSetManager
{
    private LLDominatedTree dominatedTree = new LLDominatedTree();
    private LLNonDominatedTree nonDominatedTree = new LLNonDominatedTree();
    private int numberStored = 0;
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException {
        if (!weaklyDominates(s)) {
            // clean dominated from trees
            CompositePointTracker trackedPoint = new CompositePointTracker(s);
            HashSet<CompositePointTracker> dominatedSet = new HashSet<>();
            boolean inserted = nonDominatedTree.getDominatedByInsertAndClean(trackedPoint, dominatedSet);
            numberStored -= dominatedSet.size();
            if (!inserted)
                nonDominatedTree.add(trackedPoint);
            System.out.println("UPDATED NDTREE, length " +nonDominatedTree.size());
            System.out.println("NDTree: "+ nonDominatedTree);
            int insertedCount = 0;    
            for (CompositePointTracker c : dominatedSet) 
                insertedCount += dominatedTree.deletePossiblyInsert(c,trackedPoint);
            System.out.println("DTree: "+ dominatedTree);
            
            if (insertedCount == 0) {
                dominatedTree.add(trackedPoint);
                System.out.println("ADDING...");
            }
            // add new member to length
            numberStored++;
            return true;   
        }
        return false;
    }
    
    
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException {
        if (dominatedTree.size() == 0)
            return false;
        
        CompositePoint node = dominatedTree.getBest();//tree.lower(new CompositePoint(queryPoint));
        if (node.weaklyDominates(s)) // best dominated composite point (weakly) dominates query, so reject query
            return true;
        // not dominated by best composite point in dominated tree, so now
        // look through all points up to that dominated by s, and check if any members dominate
        if (dominatedTree.memberDominates(s)) 
            return true;
        return false;    
    }
    
    public Collection<? extends Solution> getContents() {
        return dominatedTree.getContents();
    }
    
    public int size(){
        return numberStored;
    }
    
    public void clean() {
        numberStored = 0;
        dominatedTree = new LLDominatedTree();
        nonDominatedTree = new LLNonDominatedTree();
    }
    
    @Override
    public String toString() {
        String text = "";
        text += "Dominated Tree, size "+dominatedTree.size() + " : "+ dominatedTree +
                "\nNon-Dominated Tree, size "+nonDominatedTree.size() + " : "+ nonDominatedTree +"\n";
        return text;
    }
    
    public static FETreeManager managerFactory(int numberOfObjectives) {
        return new FETreeManager();
    }
}
