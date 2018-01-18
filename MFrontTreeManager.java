import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Write a description of class MFrontTreeManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MFrontTreeManager implements ParetoSetManager
{
    private int numberOfObjectives;
    
    MFrontTreeManager(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }
    
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException {
        return true;
    }

    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException {
        ArrayList<Solution> toRemove = insert(ref,s);
        
        return true;
    }
    
    public Collection<? extends Solution> getContents() {
        return null;
    }
    
    public int size() {
        return 0;
    }
    
    public void clean() {
        
    }
    
    private ArrayList<Solution> insert(MFrontSolution ref, Solution potentialMember) {
        ArrayList<Solution> R = new ArrayList<>(); // set of removed individuals from the M-list
        if (ref.weaklyDominates(potentialMember))
            return R;
        
        boolean dominating = false;
        if (potentialMember.weaklyDominates(ref))
            dominating = true;
        HashSet<MFrontSolution> upper = new HashSet<>();
        HashSet<MFrontSolution> lower = new HashSet<>();
            
        MFrontSolution toInsert = new MFrontSolution(potentialSolution);
        MFrontSolution it;
        for (int i=0; i< numberOfObjectives; i++) {
            if (potentialSolution.getFitness(i)==ref.getFitness(i)) { // insert new before ref
                toInsert.setPrevious(i, ref.getPrevious());
                ref.setPrevious(i,toInsert);
                toInsert.setNext(i,ref);
            } else if (potentialSolution.getFitness(i)<ref.getFitness(i)){
                it = ref;
                while (it.getNext(i).getFitness(i)==ref.getFitness(i))
                    it = it.getNext(i); // increment it to the last position where it is equal on objective with ref
                while (it.getFitness(i) >= potentialSolution.getFitness(i)) {
                    lower.add(it);
                    it = it.getPrevious(i);
                }
                // inssert new right after it
                toInsert.setPrev(i,it);
                toInsert.setNext(i,it.getNext(i));
                it.setNext(i,toInsert);
            } else if (potentialSolution.getFitness(i)>ref.getFitness(i)){
                
                
            }
            
        }
        
        
        return R;
    }
}
