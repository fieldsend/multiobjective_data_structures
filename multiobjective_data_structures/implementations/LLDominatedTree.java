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

    LLDominatedTree(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

    FETreeCompositePoint getBest() {
        return head;
    }

    FETreeCompositePoint getWorst() {
        return tail;
    }

    int[] getNonNullElementsOnEachObjective() {
       int[] tracker = new int[numberOfObjectives+1];
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
       return tracker;
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
            if (s.strictlyDominates(node)) //node larger on all criteria, so no contributor can dominate s
                return false;
            if (node.anyElementWeaklyDominates(s))
                return true;
            node = node.getNext();
        }
        return false;
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
        // skip the very first node, as this will always remain unchanged
        node = node.getNext();
        if (node!=null){
            for (int j=1; j<maxLength; j++){
                for (int i=0; i < numberOfObjectives; i++){
                    if (j < offset[i]+1)
                        node.inferElementFromPrevious(i);
                    else {
                        FETreeSolutionWrapper s = (listOfElements.get(i)).get(j-offset[i]);
                        node.setElement(i,s);
                        s.setDominatedTreeCompositeMember(node); // need to keep track of which DT CP this solution is in 
                    }
                }
                tail = node;
                node = node.getNext();
            }
            tail.setNext(null);
        }
    }

    
    
    
    
    void add(FETreeSolutionWrapper trackedPoint) {
        if (size==0) {
            FETreeCompositePoint cp = new FETreeCompositePoint(trackedPoint,true);
            head = cp;
            tail = cp;
        } else {
            if (trackedPoint.weaklyDominates(head)) {
                FETreeCompositePoint cp = new FETreeCompositePoint(trackedPoint,true);
                head.setPrevious(cp);
                cp.setNext(head);
                head = cp;
            } else {
                //get worst non-(weak)dominated by trackedPoint. If null, has at least one value worse than any previously seen
                FETreeCompositePoint lowerNode = getWorstNotWeaklyDominated(trackedPoint);
                if (lowerNode==null)
                    lowerNode = tail;
                FETreeCompositePoint insertedNode = new FETreeCompositePoint(numberOfObjectives);
                for (int i=0; i<numberOfObjectives; i++){
                    if (lowerNode.getFitness(i) <= trackedPoint.getFitness(i)) {
                        insertedNode.setElement(i, trackedPoint);
                    } // don't do anything otherwise, as these values will be inferred from the previous
                }
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

}
