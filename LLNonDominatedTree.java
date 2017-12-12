import java.util.HashSet;

/**
 * Write a description of class LLNonDominatedTree here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LLNonDominatedTree
{
    private FETreeCompositePoint head;
    private FETreeCompositePoint tail;
    private int size=0;
    private int numberOfObjectives;

    LLNonDominatedTree(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

    FETreeCompositePoint getBest() {
        return head;
    }

    FETreeCompositePoint getWorst() {
        return tail;
    }

    int size() {
        return size;
    }

    void add(FETreeSolutionWrapper trackedPoint) {
        System.out.println("In NDTree add");

        if (size==0) {
            System.out.println("tree empty, new head and tail");
            FETreeCompositePoint cp = new FETreeCompositePoint(trackedPoint,false);
            head = cp;
            tail = cp;
            size++;
            //trackedPoint.addToNonDominatedTreeTracking(cp);
        } else { // find first cp which does not weakly dominate argument
            FETreeCompositePoint higherNode = getFirstNonWeaklyDominating(trackedPoint);
            System.out.println("higher node to use " + higherNode);
            if (higherNode==null)
                insertNewPointAtTail(trackedPoint);
            else
                insertNewPointInBody(trackedPoint,higherNode);
        }

        if (sanityCheck()==false)
            throw new RuntimeException("at end of add");
    }

    private void insertNewPointAtTail(FETreeSolutionWrapper trackedPoint) {
        FETreeCompositePoint insertedNode = new FETreeCompositePoint(trackedPoint,false);
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
        FETreeCompositePoint node = head;
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

    private void insertNewPointInBody(FETreeSolutionWrapper trackedPoint, FETreeCompositePoint higherNode) {
        FETreeCompositePoint insertedNode = new FETreeCompositePoint(numberOfObjectives);

        for (int i=0; i<numberOfObjectives; i++){
            if (higherNode.getFitness(i) < trackedPoint.getFitness(i)) {
                if (higherNode.getElement(i)!=null) {// not going to infer from lower node, but move solution from higher
                    insertedNode.setElement(i, higherNode.getElement(i));
                    higherNode.inferElementFromPrevious(i);
                } // if not infering from lower, don't need to do anything...
            } else {
                insertedNode.setElement(i, trackedPoint);
            }
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

    private FETreeCompositePoint getFirstNonWeaklyDominating(FETreeSolutionWrapper trackedPoint) {
        FETreeCompositePoint node = head; // already checked head before this call, no need to check again
        while ((node!=null) && (node.weaklyDominates(trackedPoint)))
            node = node.getNext();
        return node;
    }

    /**
     * Potential for dominatedSet to contain a null element
     */
    boolean getDominatedByInsertAndClean(FETreeSolutionWrapper trackedPoint, HashSet<FETreeSolutionWrapper> dominatedSet) {
        if (sanityCheck()==false)
            throw new RuntimeException("Sanity Check fail at start of insert and clean, tail disconnected");

        if (size==0)
            return false;
        // first clean all composite points which are strictly dominated
        while (trackedPoint.strictlyDominates(tail)) {
            for (int i=0; i<numberOfObjectives; i++)
                dominatedSet.add(tail.getElement(i));
            tail = tail.getPrevious();
            size--;
            if (tail==null) // tail was head, so tree cleaned out
                return false;
            tail.setNext(null);
        }
        // now process all composite points which do not strictly dominate s
        boolean inserted = false;
        FETreeCompositePoint node = head;
        // COULD BINARY SERACH HERE
        while (node.strictlyDominates(trackedPoint))  {
            if (node.getNext()!=null)
                node = node.getNext();
            else
                return false; // got to tail and tail does not strictly dominate trackedPoint
        }
        // node does not strictly dominate
        do {
            System.out.println("In check loop:");
            for (int i=0; i<numberOfObjectives; i++) {
                if (node.getElement(i)!=null) { // if null, already checked in a lower node, so no need to pull
                    if (trackedPoint.weaklyDominates(node.getElement(i))) {
                        System.out.println("DOMINATED:  " +node.getElement(i).getWrappedSolution());
                        dominatedSet.add(node.getElement(i));
                        if (node.getPrevious()!=null){ // if node is not head
                            node.inferElementFromPrevious(i);
                        } else { // node is, point is dominated so tracked point must have better value
                            node.setElement(i, trackedPoint);
                            inserted = true;
                        }
                    }
                }
            }
            if (node.getNumberOfStoredSolutions()==0) { // no contents unqiue to this node
                System.out.println("REMOVING DUPLICATE: "+ this);
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

                System.out.println("DUPLICATE REMOVED: "+ this);
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
        FETreeCompositePoint node = head;
        while (node!=null) {
            text += node + "...";
            node = node.getNext();
        }
        return text;
    }
}
