import java.util.HashSet;

/**
 * Write a description of class LLNonDominatedTree here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LLNonDominatedTree
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

    int size() {
        return size;
    }

    void add(CompositePointTracker trackedPoint) {
        System.out.println("In NDTree add");
        
        if (size==0) {
            System.out.println("tree empty, new head and tail");
            CompositePoint cp = new CompositePoint(trackedPoint);
            head = cp;
            tail = cp;
            size++;
            //trackedPoint.addToNonDominatedTreeTracking(cp);
        } else { // find first cp which does not weakly dominate argument
            CompositePoint higherNode = getFirstNonWeaklyDominating(trackedPoint);
            System.out.println("higher node to use " + higherNode);
            if (higherNode==null)
                insertNewPointAtTail(trackedPoint);
            else
                insertNewPointInBody(trackedPoint,higherNode);
        }
        
        if (sanityCheck()==false)
            throw new RuntimeException("at end of add");
    }

    private void insertNewPointAtTail(CompositePointTracker trackedPoint) {
        CompositePoint insertedNode = new CompositePoint(trackedPoint);
        System.out.println("INSERTED TAIL: " + insertedNode+ ", prev tail: "+ tail);
        insertedNode.setPrevious(tail);
        tail.setNext(insertedNode);
        tail = insertedNode;
        System.out.println("Error checking "+ sanityCheck());
        size++;
    }
    
    private boolean sanityCheck() {
        if (head==null){
            if (tail!=null) {
                System.out.println("head is null but tail isn't");
                return false;
            } else 
                return true;
        }
        int l1=0;
        int l2=0;
        CompositePoint node = head;
        while (node.getNext()!=null) {
            node = node.getNext();
            l1++;
        }
        if (node!=tail) {
            System.out.println("TAIL not at end");
            return false;
        }
        while (node.getPrevious()!=null) {
            node = node.getPrevious();
            l2++;
        }
        if (node!=head) {
            System.out.println("HEAD not at start");
            return false;
        }
        if (l1!=l2) {
            System.out.println("length from head to tail is not same as tail to head");
            return false;
        }
        return true;
    }
    
    private void insertNewPointInBody(CompositePointTracker trackedPoint, CompositePoint higherNode) {
        CompositePoint insertedNode = new CompositePoint(trackedPoint.getNumberOfObjectives());

        for (int i=0; i<trackedPoint.getNumberOfObjectives(); i++){
            if (higherNode.getFitness(i) < trackedPoint.getFitness(i)) {
                insertedNode.setElement(i, higherNode.getElement(i));
                //higherNode.getElement(i).addToNonDominatedTreeTracking(insertedNode);
            } else {
                insertedNode.setElement(i, trackedPoint);
            }
            //trackedPoint.addToNonDominatedTreeTracking(insertedNode);
        }
        insertedNode.setNext(higherNode);
        insertedNode.setPrevious(higherNode.getPrevious());
        higherNode.setPrevious(insertedNode);
        if (higherNode == head)
            head = insertedNode;
        else
            insertedNode.getPrevious().setNext(insertedNode);
        size++;
    }

    private CompositePoint getFirstNonWeaklyDominating(CompositePointTracker trackedPoint) {
        CompositePoint node = head; // already checked head before this call, no need to check again
        while ((node!=null) && (node.weaklyDominates(trackedPoint)))
            node = node.getNext();
        return node;
    }

    boolean getDominatedByInsertAndClean(CompositePointTracker trackedPoint, HashSet<CompositePointTracker> dominatedSet) {
        if (sanityCheck()==false)
            throw new RuntimeException("Sanity Check fail at start of insert and clean, tail disconnected");
        
        
        if (size==0)
            return false;
        // first clean all composite points which are strictly dominated
        while (trackedPoint.strictlyDominates(tail)) {
            dominatedSet.addAll(tail.getUniqueElements());
            for (CompositePointTracker t : tail.getUniqueElements())
                System.out.println("DOMINATED in CP:  " +t.getWrappedSolution());
            tail = tail.getPrevious();
            size--;
            if (tail==null) // tail was head, so tree cleaned out
                return false;
            tail.setNext(null);
        }
        // now process all those which do not strictly dominate s
        boolean inserted = false;
        CompositePoint node = head;
        while (node.strictlyDominates(trackedPoint)) {
            node = node.getNext();
        }
        // node does not strictly dominate
        do {
            System.out.println("In check loop:");
            for (int i=0; i<node.getNumberOfObjectives(); i++) {
                if (trackedPoint.weaklyDominates(node.getElement(i))) {
                    System.out.println("DOMINATED:  " +node.getElement(i).getWrappedSolution());
                    dominatedSet.add(node.getElement(i));
                    if (node.getPrevious()!=null){ // if node is not head
                        node.setElement(i, node.getPrevious().getElement(i));
                        //node.getPrevious().getElement(i).addToNonDominatedTreeTracking(node);
                    } else { // node is, point is dominated so tracked point must have better value
                        node.setElement(i, trackedPoint);
                        //trackedPoint.addToNonDominatedTreeTracking(node);
                        inserted = true;
                    }
                }
            }
            if (node.isDuplicatingPrevious()) { // remove any duplicate nodes
                if (node == tail) {
                    tail = tail.getPrevious();
                    tail.setNext(null);
                    System.out.println("DETACHING TAIL");
                    node = null;
                } else {
                    node.getPrevious().setNext(node.getNext());
                    node.getNext().setPrevious(node.getPrevious());
                    node = node.getNext();
                }
                size--;
            } else {
                node = node.getNext();
            }
        } while (node!=null);
        
        if (sanityCheck()==false)
            throw new RuntimeException("Sanity Check fail at end of insert and clean, tail disconnected " + size);
        return inserted;
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
}
