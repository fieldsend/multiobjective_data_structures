import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Write a description of class MFrontTreeManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MFrontTreeManager// implements ParetoSetManager
{
    /* private int numberOfObjectives;

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

        boolean nonDominated = false;
        if (potentialMember.weaklyDominates(ref))
            nonDominated = true;
        HashSet<MFrontSolution> upper = new HashSet<>(); // In other work a stack is suggested better
        HashSet<MFrontSolution> lower = new HashSet<>(); // NEED TO CHECK

        MFrontSolution toInsert = new MFrontSolution(potentialSolution);
        MFrontSolution it;
        for (int i=0; i< numberOfObjectives; i++) {
            it = ref;
            if (potentialSolution.getFitness(i)==ref.getFitness(i)) { // insert new before ref
                toInsert.setPrevious(i, ref.getPrevious());
                ref.setPrevious(i,toInsert);
                toInsert.setNext(i,ref);
            } else if (potentialSolution.getFitness(i)<ref.getFitness(i)){
                while (it.getNext(i).getFitness(i)==ref.getFitness(i))
                    it = it.getNext(i); // increment it to the last position where it is equal on objective with ref
                while (it.getFitness(i) >= potentialSolution.getFitness(i)) {
                    lower.add(it);
                    it = it.getPrevious(i);
                }
                // insert new right after it
                toInsert.setPrevious(i,it);
                toInsert.setNext(i,it.getNext(i));
                it.setNext(i,toInsert);
            } else if (potentialSolution.getFitness(i)>ref.getFitness(i)){
                //decrement it to the last position where f_i(it) == f_i(ref)
                while (it.getPrevious(i).getFitness(i)==ref.getFitness(i))
                    it = it.getPrevious(i); // increment it to the last position where it is equal on objective with ref
                while (it.getFitness(i) <= potentialSolution.getFitness(i)) {
                    upper.add(it);
                    it = it.getNext(i);
                }
                // inssert new right before it
                toInsert.setNext(i,it);
                toInsert.setPrevious(i,it.getPrevious(i));
                it.setPrevious(i,toInsert);
            }
        }
        // no need to insert potentialSolution into an attribute H, as mapping stored in MFrontSolution object
        for (MFrontSolution s : lower) {
            if (potentialSolution.dominates(s)) {
                s.remove();
                R.add(s);
                nonDOminated = true;
            }
        }
        if (nonDominated)
            return R;
        for (MFrontSolution s : upper) {
            if (s.weaklyDominates(potentialSolution)) {
                toInsert.remove();
                R.add(toInsert);
                return R;
            }
        }  
        return R;
	}*/
}

