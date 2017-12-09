import java.util.HashSet;
import java.util.ArrayList;
/**
 * Write a description of class LLDominatedTree here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LLDominatedTree
{
    private CompositePoint head;
    private CompositePoint tail;
    private int size=0;
    
    CompositePoint getBest() {
        return head;
    }
    
    CompositePoint getWorst() {
        return tail;
    }
    
    void removeWorst() {
        tail = tail.getPrevious();
        tail.setNext(null);
        size--;
    }
    
    ArrayList<Solution> getContents() {
        HashSet<Solution> contents = new HashSet<>();
        CompositePoint node = head;
        while (node!=null) {
            for (CompositePointTracker t : node.getUniqueElements())
                contents.add(t.getWrappedSolution());
            node = node.getNext();
        }
        return new ArrayList<Solution>(contents);
    }
    
    int size() {
        return size;
    }
    
    boolean memberDominates(Solution s) {
        CompositePoint node = head;
        while (node!=null) {
            if (s.strictlyDominates(node)) //c larger on all criteria, so no contributor can dominate s
                return false;
            if (node.anyElementWeaklyDominates(s))
                return true;
            node = node.getNext();
        }
        return false;
    }
    
    void add(CompositePointTracker trackedPoint) {
        if (size==0) {
            CompositePoint cp = new CompositePoint(trackedPoint);
            head = cp;
            tail = cp;
            trackedPoint.addToDominatedTreeTracking(cp);
        } else {
            if (trackedPoint.weaklyDominates(head)) {
                CompositePoint cp = new CompositePoint(trackedPoint);
                head.setPrevious(cp);
                cp.setNext(head);
                head = cp;
                trackedPoint.addToDominatedTreeTracking(cp);
            } else {
                //get worst non-(weak)dominated by trackedPoint. If null, has at least one value worse than any previously seen
                CompositePoint lowerNode = getWorstNotWeaklyDominated(trackedPoint);
                if (lowerNode==null)
                    lowerNode = tail;
                CompositePoint insertedNode = new CompositePoint(trackedPoint.getNumberOfObjectives());
                CompositePointTracker[] copied = new CompositePointTracker[trackedPoint.getNumberOfObjectives()];
                int numberCopied = 0;
                for (int i=0; i<trackedPoint.getNumberOfObjectives(); i++){
                    if (lowerNode.getFitness(i) <= trackedPoint.getFitness(i)) {
                        insertedNode.setElement(i, trackedPoint);
                    } else {
                        insertedNode.setElement(i, lowerNode.getElement(i));
                        copied[numberCopied++] = lowerNode.getElement(i);
                    }
                }
                trackedPoint.addToDominatedTreeTracking(insertedNode);
                for (int i=0; i<numberCopied; i++) // copy across into maintained set
                    copied[i].addToDominatedTreeTracking(insertedNode);
                
                insertedNode.setNext(lowerNode.getNext());
                insertedNode.setPrevious(lowerNode);
                lowerNode.setNext(insertedNode);
                if (lowerNode==tail)
                    tail = insertedNode;
            }
        }
        size++;
    }
    
    private CompositePoint getWorstNotWeaklyDominated(CompositePointTracker trackedPoint) {
        CompositePoint node = head.getNext(); // already checked head before this call, no need to check again
        while ((node!=null) && (!trackedPoint.weaklyDominates(node)))
            node = node.getNext();
        return node;
            
        /*if (size<10) { //just iterate through when small
            CompositePoint node = tail;
            while (node!=null) {
                if (!trackedPoint.weaklyDominates(node))
                    return node;
                node = node.getPrevious();    
            }
            return null;
        } else {
            int lower = 0;
            int upper = size-1;
            return binarySearch(lower, upper, trackedPoint, head, true);
        }*/
    }
    
    int deletePossiblyInsert(CompositePointTracker toRemove, CompositePointTracker dominator) {
        int v = 0;
        for (CompositePoint c : toRemove.getDominatedTreeMembership()) {
            System.out.println("Processing cp: " +  c);
            if (c.cleanAndReplaceDominatedTree(this,toRemove,dominator)) {
                v++;
            }
        }
        return v;
    }
    
    @Override
    public String toString() {
        String text = "";
        CompositePoint node = head;
        while (node!=null) {
            text += node + "...";
            node = node.getNext();
        }
        return text;
    }
    
    /*
    private CompositePoint binarySearch(int lower, int upper, CompositePointTracker trackedPoint, CompositePoint queryPoint, boolean up) {
        int diff = upper-lower;
        if (diff>1) {
            int midPoint = lower + diff/2;
            if (up) {
                for (int i=0; i< ; i++) 
                    queryPoint = queryPoint.getNext();
            } else {
                for (int i=0; i< midPoint; i++) 
                    queryPoint = queryPoint.getPrevious();
            }
            if (trackedPoint.weaklyDominates(node)) { // go backwards
                return binarySearch(lower, midPoint-1);
            } else {
                return binarySearch(midPoint+1, upper);
            }
        } else {
            if if (!trackedPoint.weaklyDominates(node))
            
        }
    }*/
    
}
