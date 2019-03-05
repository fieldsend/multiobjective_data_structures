package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
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
    private int[] numberOfActiveElements;

    LLNonDominatedTree(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
        numberOfActiveElements = new int[numberOfObjectives];
        for (int i=0; i<numberOfObjectives; i++)
            numberOfActiveElements[i] = 0;
    }

    private int getMaxNullElement() {
        int i = 0;
        int me = numberOfActiveElements[i];
        for (int j=1; j<numberOfActiveElements.length; j++) {
            if (numberOfActiveElements[j] < me) {
                i = j;
                me = numberOfActiveElements[j];
            }
        }
        return i;
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

    /*
     * Method concentinas the data structure by removing as many null elements as possible
     */
    void compress() {
        List<List<FETreeSolutionWrapper>> listOfElements = new ArrayList<>(numberOfObjectives);
        for (int i=0; i< numberOfObjectives; i++){
            List<FETreeSolutionWrapper> elements = new ArrayList<>(size);
            FETreeCompositePoint node = head; 
            while (node!=null) {
                if (node.getElement(i) != null)
                    elements.add(node.getElement(i));
                node = node.getNext();
            }
            listOfElements.add(elements);
        } 
        // now replace current Non-dominated tree with compressed version, copy across to 
        int maxLength = listOfElements.get(0).size();
        for (int i=1; i< numberOfObjectives; i++)
            if ((listOfElements.get(i)).size() > maxLength) 
                maxLength = (listOfElements.get(i)).size();
        size = maxLength;
        // keep track of how many null entries there needs to be        
        int[] offset = new int[numberOfObjectives];       
        for (int i=0; i< numberOfObjectives; i++)
            offset[i] = maxLength-(listOfElements.get(i)).size();
        // reform tree
        FETreeCompositePoint node = head;
        // Process first node and replace any deep links that need to be removed
        for (int i=0; i < numberOfObjectives; i++){
            // deal with special case where the head current has a deep link to another element, but needs to replace
            // with a compressed location from further up the old tree
            if (!head.activeElement(i)) {
                if ((listOfElements.get(i)).size()>0){
                    head.activateDeepNodeSolution(i,(listOfElements.get(i)).get(0));
                }
            }
        }
        // now do rest of the tree
        node = node.getNext();
        if (node!=null){
            for (int j=1; j<maxLength; j++){
                for (int i=0; i < numberOfObjectives; i++){
                    if (j < offset[i]+1)
                        node.inferElementFromPrevious(i);
                    else
                        node.setElement(i,(listOfElements.get(i)).get(j-offset[i]));
                }
                tail = node;
                node = node.getNext();
            }
            tail.setNext(null);
        }
        // keep track of total null entries to balance tree building later
        for (int i=0; i < numberOfObjectives; i++)
            this.numberOfActiveElements[i] = (listOfElements.get(i)).size();
        
    }

    int[] getActiveElementsOnEachObjective() {
        return numberOfActiveElements;
    }

    int getMaxActiveElements() {
        int m = numberOfActiveElements[0];
        for (int i=1; i< numberOfObjectives; i++)
            if (numberOfActiveElements[i] > m)
                m = numberOfActiveElements[i];
        return m;
    }
    
    void add(FETreeSolutionWrapper trackedPoint) {
        
        if (size==0) {
            //System.out.println("tree empty, new head and tail");
            FETreeCompositePoint cp = new FETreeCompositePoint(trackedPoint,false);
            this.numberOfActiveElements[0] = 1;
            for (int i=1; i < numberOfObjectives; i++)
                this.numberOfActiveElements[i] = 0;
            
            head = cp;
            tail = cp;
            size++;
            //trackedPoint.addToNonDominatedTreeTracking(cp);
        } else { // find first cp which does not weakly dominate argument
            FETreeCompositePoint higherNode = getFirstNonWeaklyDominating(trackedPoint);
            //System.out.println("higher node to use " + higherNode);
            if (higherNode==null)
                insertNewPointAtTail(trackedPoint);
            else
                insertNewPointInBody(trackedPoint,higherNode);
        }

        /*if (sanityCheck()==false)
        throw new RuntimeException("at end of add");
         */
    }

    ArrayList<Solution> getContents() {
        HashSet<Solution> contents = new HashSet<>();
        FETreeCompositePoint node = head;
        while (node!=null) {
            for (int i=0; i<numberOfObjectives; i++)
                if (node.getElement(i)!=null)
                    contents.add(node.getElement(i).getWrappedSolution());
            node = node.getNext();
        }
        return new ArrayList<Solution>(contents);
    }

    private void insertNewPointAtTail(FETreeSolutionWrapper trackedPoint) {
        int index = getMaxNullElement();
        FETreeCompositePoint insertedNode = new FETreeCompositePoint(trackedPoint,index,false);
        this.numberOfActiveElements[index]++;
        //System.out.println("INSERTED TAIL:");
        insertedNode.setPrevious(tail);
        tail.setNext(insertedNode);
        tail = insertedNode;
        //System.out.println("Error checking "+ sanityCheck());
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
        int indexToInsert = -1;
        int numberActive = Integer.MAX_VALUE;
            
        if(higherNode == head) {
            //boolean inserted = false;
            //if (insertedCriterion==-1)
            //System.out.println("Insert in new head...............");
            insertedNode.instantiateDeepNodeSolutions(); // lazy allocation of space  
            //int trackedIndex = -1;
            int numberBetterOn = 0;
            int[] indicesBetterOn = new int[numberOfObjectives];
            boolean[] oldHeadIsBetter = new boolean[numberOfObjectives];
            int[] deepLinkTransferedLocations = new int[numberOfObjectives];
            int[] indexOfResponsibleElement = new int[numberOfObjectives];
                    // first identify which indices need to be moved down from head, and 
                    // which are deep copies that need resolving
            for (int i=0; i<numberOfObjectives; i++) {
                if (head.getFitness(i) < trackedPoint.getFitness(i)){
                    oldHeadIsBetter[i] = true;
                    if (head.activeElement(i)){
                        indexOfResponsibleElement[i] = i;
                    } else {
                        indexOfResponsibleElement[i] = head.getDeepIndex(i);
                        // track in numberOfActiveElements that things are shifting
                    }
                } else {
                    oldHeadIsBetter[i] = false;
                }
            }
            // now reconfigure insertedNode (the new head) and Head (the old head)
            for (int i=0; i<numberOfObjectives; i++) {
                if (oldHeadIsBetter[i]){
                    //System.out.println("\nINFERRING/MOVING : " + i);
                    if (indexOfResponsibleElement[i]==i) {
                        // shift to new head at element i
                        insertedNode.setElement(i, head.getElement(i));
                        // get old head to reference down to new head on indexOfInferred, so element
                        // now only exisits in new head
                        head.inferElementFromPrevious(i);
                    } else { // deep link to be handled
                        if (oldHeadIsBetter[ indexOfResponsibleElement[i] ]) { // deep link is also being transferred down at other index
                            // so link across
                            // shift to new head at element i
                            insertedNode.setDeepNodeSolution(i, indexOfResponsibleElement[i] );
                            // get old head to reference down to new head on indexOfInferred, so element
                            // now only exisits in new head
                            head.inferElementFromPrevious(i);
                        } else { // deep link not being transferred down as worse on objective it is being actively stored for in head
                            //System.out.println("SWITCHING DEEP LINK..............." + i + " " + indexOfResponsibleElement[i]);
                    
                            // so need to do some rearranging...
                            if (head.activeElement(indexOfResponsibleElement[i])) { // not yet shifted
                                insertedNode.setElement(i, head.getElement( indexOfResponsibleElement[i] ));
                                head.inferElementFromPrevious( indexOfResponsibleElement[i] );
                                head.inferElementFromPrevious(i);
                                deepLinkTransferedLocations[ indexOfResponsibleElement[i] ] = i; // keep track of where it has moved
                                this.numberOfActiveElements[i]++;
                                this.numberOfActiveElements[ indexOfResponsibleElement[i] ]--;
                            } else { // the old guide in head has already shifted to a different index in the new head
                                insertedNode.setDeepNodeSolution(i, deepLinkTransferedLocations[ indexOfResponsibleElement[i] ] );
                                head.inferElementFromPrevious(i);
                            }
                        }
                    }
                } else { // track which criteria need replacing by new point as worse in old head
                    //System.out.println("REPLACING : " + i + " na " + numberActive);
                    if (this.numberOfActiveElements[i] < numberActive) {
                        indexToInsert = i;
                        numberActive = numberOfActiveElements[i];
                    }
                    indicesBetterOn[numberBetterOn] = i;
                    numberBetterOn++;
                }
            }

            // choose which critria to insert into --  all other criteria to be replaced deep reference this one
            insertedNode.setElement(indexToInsert, trackedPoint);
            this.numberOfActiveElements[indexToInsert]++;
            for (int i=0; i<numberBetterOn; i++)
                if (indexToInsert != indicesBetterOn[i])
                    insertedNode.setDeepNodeSolution(indicesBetterOn[i], indexToInsert);
            
            
            insertedNode.setNext(head);
            insertedNode.setPrevious(null);
            head.setPrevious(insertedNode);
            head.cleanDeepLinks();
            FETreeCompositePoint node = head;
            head = insertedNode;
            if (node.getNumberOfStoredSolutions()==0) { // no contents unqiue to this node
                removeDuplicate(node);
            }
        } else {
            //System.out.println("In insertNewPointInBody, body");
            for (int i=0; i<numberOfObjectives; i++){
                if (higherNode.getFitness(i) < trackedPoint.getFitness(i)) {
                    //System.out.println("\nINFERRING/MOVING : " + i);
                    
                    if (higherNode.getElement(i)!=null) {// not going to infer from lower node, but move solution down from higher
                        insertedNode.setElement(i, higherNode.getElement(i));
                        higherNode.inferElementFromPrevious(i);
                    } // if not infering from lower, don't need to do anything...
                } else {
                    //System.out.println("\nREPLACING : " + i + " na " + numberActive);
                    
                    if (this.numberOfActiveElements[i] < numberActive) {
                        indexToInsert = i;
                        numberActive = numberOfActiveElements[i];
                    }
                }
            }
            // choose which critria to insert into --  all other criteria can be null and inferred from previous node
            insertedNode.setElement(indexToInsert, trackedPoint);
            this.numberOfActiveElements[indexToInsert]++;
                
            insertedNode.setNext(higherNode);
            insertedNode.setPrevious(higherNode.getPrevious());
            higherNode.setPrevious(insertedNode);
            insertedNode.getPrevious().setNext(insertedNode);
        }
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
        //if (sanityCheck()==false)
        //    throw new RuntimeException("Sanity Check fail at start of insert and clean, tail disconnected");

        if (size==0)
            return false;
        // first clean all composite points which are strictly dominated
        while (trackedPoint.strictlyDominates(tail)) {
            for (int i=0; i<numberOfObjectives; i++) {
                if (tail.getElement(i)!=null) {
                    dominatedSet.add(tail.getElement(i));
                    this.numberOfActiveElements[i]--;
                }
            }
            tail = tail.getPrevious();
            size--;
            if (tail==null) // tail was head, so tree cleaned out
                return false;
            tail.setNext(null);
            //System.out.println("tail removed..." + tail);
        }
        // now process all composite points which do not strictly dominate trackedPoint
        boolean inserted = false;
        FETreeCompositePoint node = head;
        // COULD BINARY SEARCH HERE
        while (node.strictlyDominates(trackedPoint))  {
            if (node.getNext()!=null)
                node = node.getNext();
            else
                return false; // got to tail and tail does not strictly dominate trackedPoint
        }
        int insertedCriterion=-1;
        // node does not strictly dominate
        do {
            //System.out.println("In check loop:");
            for (int i=0; i<numberOfObjectives; i++) {
                if (node.getElement(i)!=null) { // if null, already checked in a lower node, so no need to pull
                    if (trackedPoint.weaklyDominates(node.getElement(i))) {
                        //System.out.println("DOMINATED:  " +node.getElement(i).getWrappedSolution());
                        dominatedSet.add(node.getElement(i));
                        this.numberOfActiveElements[i]--;
                        if (node.getPrevious()!=null){ // if node is not head
                            node.inferElementFromPrevious(i);
                        } else { // node is head, point is dominated so tracked point must have better value
                            if (inserted==false){
                                node.setElement(i, trackedPoint);
                                inserted = true;
                                insertedCriterion = i;
                                this.numberOfActiveElements[i]++;
                            } else {
                                //System.out.println("DOMINATED:  " +node.getElement(i).getWrappedSolution());
                                node.setDeepNodeSolution(i,insertedCriterion);
                                //System.out.println("REPLACED:  " +node.getElement(i).getWrappedSolution());
                            }
                        }
                    }
                }
            }
            if (node.getNumberOfStoredSolutions()==0) { // no contents unqiue to this node
                node = removeDuplicate(node);
            } else {
                node = node.getNext();
            }
        } while (node!=null);

        //if (sanityCheck()==false)
        //    throw new RuntimeException("Sanity Check fail at end of insert and clean, tail disconnected " + size);
        return inserted;
    }

    private FETreeCompositePoint removeDuplicate(FETreeCompositePoint node){
        //System.out.println("REMOVING DUPLICATE: NDT ");
        if (node == tail) {
            tail = tail.getPrevious();
            tail.setNext(null);
            //System.out.println("DETACHING TAIL");
            node = null;
        } else {
            node.getPrevious().setNext(node.getNext());
            node.getNext().setPrevious(node.getPrevious());
            node = node.getNext();
        }
        size--;
        return node;
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
    
    public String toCompleteString() {
        String text = "";
        FETreeCompositePoint node = head;
        while (node!=null) {
            text += " -- V :";
            for (int i=0; i<numberOfObjectives; i++) 
                text += node.getFitness(i) + ", ";
            node = node.getNext();
        }
        return text;
    }
}
