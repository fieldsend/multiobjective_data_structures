package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
/**
 * Write a description of class LLDominatedTree here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LLDominatedTree
{
    private FETreeCompositePoint head;
    private FETreeCompositePoint tail;
    private int size=0;
    private int numberOfObjectives;
    private int[] numberOfActiveElements;

    LLDominatedTree(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
        numberOfActiveElements = new int[numberOfObjectives];
    }

    void decrementActiveCount(int index) {
        numberOfActiveElements[index]--;
    }
    
    void incrementActiveCount(int index) {
        numberOfActiveElements[index]++;
    }
    
    FETreeCompositePoint getBest() {
        return head;
    }

    FETreeCompositePoint getWorst() {
        return tail;
    }

    int[] getNonNullElementsOnEachObjective() {
       /*int[] tracker = new int[numberOfObjectives+1];
       for (int i=0; i< tracker.length; i++)
            tracker[i] = 0;
            
       FETreeCompositePoint node = head;
       while (node!=null) {
           if (node.getNumberOfStoredSolutions()==0)
                tracker[numberOfObjectives]++;
           
           for (int i=0; i< numberOfObjectives; i++)
                if (node.getElement(i) != null)
                    tracker[i]++;
           node = node.getNext();
       }
       return tracker;*/
       return numberOfActiveElements;
    }
    
    
    void removeComponent(FETreeCompositePoint c) {
        c.getPrevious().setNext(c.getNext());
        c.getNext().setPrevious(c.getPrevious());
        size--;
    }

    void removeWorst() {
        //System.out.println("REMOVING TAIL");
        tail = tail.getPrevious();
        tail.setNext(null);
        size--;
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

    int size() {
        return size;
    }

    boolean memberDominates(Solution s) {
        FETreeCompositePoint node = head;
        while (node!=null) {
            //System.out.println("Comparing to a DTree node elements " +  node);
            if (s.strictlyDominates(node)) // reached a node worse on all criteria, so no contributor can dominate s
                return false;
            if (node.anyElementWeaklyDominates(s))
                return true;
            node = node.getNext();
        }
        return false;
    }

    
    /*
     * Method concertinas the data structure by removing as many null elements as possible
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
        // now replace current Dominated tree with compressed version, copy across to 
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
                    FETreeSolutionWrapper s = (listOfElements.get(i)).get(0);
                    //System.out.println("Setting: " + i + " " + s);
                    head.activateDeepNodeSolution(i, s);
                    s.setDominatedTreeCompositeMember(node); // need to keep track of which dominated tree composite point this solution is in 
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
                    else {
                        FETreeSolutionWrapper s = (listOfElements.get(i)).get(j-offset[i]);
                        node.setElement(i,s);
                        s.setDominatedTreeCompositeMember(node); // need to keep track of which dominated tree composite point this solution is in 
                    }
                }
                tail = node;
                node = node.getNext();
            }
            tail.setNext(null);
        }
    }
    
    int getMaxActiveElements() {
        int m = numberOfActiveElements[0];
        for (int i=1; i< numberOfObjectives; i++)
            if (numberOfActiveElements[i] > m)
                m = numberOfActiveElements[i];
        return m;
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
    
    void add(FETreeSolutionWrapper trackedPoint) {
        //System.out.println("In DTree add");
        if (size==0) {
            System.out.println("In DTree add, size =0");
            FETreeCompositePoint cp = new FETreeCompositePoint(trackedPoint,true);
            this.numberOfActiveElements[0] = 1;
            for (int i=1; i < numberOfObjectives; i++)
                this.numberOfActiveElements[i] = 0;
            head = cp;
            tail = cp;
        } else {
            System.out.println("In DTree add, size !=0");
            if (trackedPoint.weaklyDominates(head)) {
                System.out.println("tp weak doms head");
                int index = getMaxNullElement();
                FETreeCompositePoint cp = new FETreeCompositePoint(trackedPoint,index,true);
                this.numberOfActiveElements[index]++;
                head.setPrevious(cp);
                cp.setNext(head);
                head = cp;
            } else {
                System.out.println("tp not weak doms head");
                //get worst non-(weak)dominated by trackedPoint. If null, has at least one value worse than any previously seen
                FETreeCompositePoint lowerNode = getWorstNotWeaklyDominated(trackedPoint);
                if (lowerNode==null)
                    lowerNode = tail;
                FETreeCompositePoint insertedNode = new FETreeCompositePoint(numberOfObjectives);
                
                int indexToInsert = -1;
                int numberActive = Integer.MAX_VALUE;
                
                for (int i=0; i<numberOfObjectives; i++){
                    if (lowerNode.getFitness(i) <= trackedPoint.getFitness(i)) {
                        if (this.numberOfActiveElements[i] < numberActive) {
                            indexToInsert = i;
                            numberActive = numberOfActiveElements[i];
                        }
                    } // don't do anything otherwise, as these values will be inferred from the previous
                }
                // indsert into element with fewest active elements
                insertedNode.setElement(indexToInsert, trackedPoint);
                numberOfActiveElements[indexToInsert]++;
                trackedPoint.setDominatedTreeCompositeMember(insertedNode);
                insertedNode.setNext(lowerNode.getNext());
                insertedNode.setPrevious(lowerNode);
                lowerNode.setNext(insertedNode);
                if (lowerNode==tail)
                    tail = insertedNode;
                else
                    insertedNode.getNext().setPrevious(insertedNode);
            }
        } 
        size++;
        //System.out.println("CURRENT DTREE STATE: length " +  this.size() + ",  " + this);
        //System.out.println("DTREE DEEP CONTENTS: " +  this.stripOutContents());

        //System.out.println("SANITY CHECK PASS? " + sanityCheck());
        //System.out.println();       
    }

    private FETreeCompositePoint getWorstNotWeaklyDominated(FETreeSolutionWrapper trackedPoint) {
        FETreeCompositePoint node = head; // already checked head before this call, no need to check again
        while ((node.getNext()!=null) && (!trackedPoint.weaklyDominates(node.getNext())))
            node = node.getNext();
        // tracked point does not weakly dominated node, but it does weakly dominate the one after
        // do want to insert between node and node.next, with a modified version of node, replacing the
        // node values which are better than trackedPoint
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

    int deletePossiblyInsert(FETreeSolutionWrapper toRemove, FETreeSolutionWrapper dominator) {
        int v = 0;
        FETreeCompositePoint c = toRemove.getDominatedTreeMembership();
        
        //System.out.println("PROCESSING CP: " +  c);
        //System.out.println("TO REMOVE: " +  toRemove.getWrappedSolution());
        //System.out.println("CURRENT DTREE STATE: length" +  this.size() + ",  " + this);
        //System.out.println("SANITY CHECK PASS? " + sanityCheck());
        //if (!dominator.weaklyDominates(toRemove.getWrappedSolution()))
        //    System.out.println("ERROR -- dominating solution does not dominate wrapped solution!!");
        if (c.cleanAndReplaceDominatedTree(this,toRemove,dominator)) {
            v++;
        }
        //System.out.println("DTREE DEEP CONTENTS: " +  this.stripOutContents());

        return v;
    }

    /*public String stripOutContents() {
        String text = "";
        FETreeCompositePoint node = head;
        while (node!=null) {
            text += node.stripOutContents();
            node = node.getNext();
        }
        return text;
    }*/

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
    private boolean sanityCheck() {
        if (head==null){
            if (tail!=null) {
                System.out.println("head is null but tail isn't");
                return false;
            } else 
                return true;
        }
        if (tail==null){
            if (head!=null) {
                System.out.println("head is null but tail isn't");
                return false;
            } else 
                return true;
        }

        int l1=1;
        int l2=1;
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

        //now check size matched node number
        if (l1!=size){
            System.out.println("length from head to tail is not same as recorded size");
            return false;
        }

        return true;
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
