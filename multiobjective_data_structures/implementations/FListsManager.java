package multiobjective_data_structures.implementations;

import multiobjective_data_structures.*;
import java.util.Collection;
import java.util.ArrayList;

/**
 * ParetoSetManager. Describes methods that all managers of Pareto sets need to provide.
 * 
 * All implementations should provide a no argument constructor that provides an empty
 * initial ParetoSetManager.
 * 
 * @author Jonathan Fieldsend
 * @version 1.0
 */
public class FListsManager implements ParetoSetManager
{
    private int numberOfObjectives;
    private CriteriaNode[] linkedListHeads;
    private CriteriaNode[] linkedListTails;
    private int[] listLengths;

    // numberOfObjectives lists of doubly-linked CriteriaNodes
    // ordered by each criteria. Each solution is in only *one* of these lists
    // head of list is lowest (best) stored on that criteria. Tail of list is 
    // highest (worst) on that criteria

    private class CriteriaNode {
        CriteriaNode prev;
        CriteriaNode next;
        Solution cargo;

        boolean better(Solution s, int activeCriterion) {
            return cargo.getFitness(activeCriterion) < s.getFitness(activeCriterion);
        }

        boolean betterOrEqual(Solution s, int activeCriterion) {
            return cargo.getFitness(activeCriterion) <= s.getFitness(activeCriterion);
        }

        CriteriaNode(Solution cargo) {
            this.cargo = cargo;
        }
    }

    
    private FListsManager(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
        this.linkedListHeads = new CriteriaNode[numberOfObjectives];
        this.linkedListTails = new CriteriaNode[numberOfObjectives];
        this.listLengths = new int[numberOfObjectives];
    }
    
    
    private int getIndexOfShortestList() {
        int index = 0;
        int shortest = listLengths[0];
        for (int i = 1; i< numberOfObjectives; i++) {
            if (listLengths[i] < shortest){
                shortest = listLengths[i];
                index = i;
            }
        }
        return index;
    }

    public static FListsManager managerFactory(int numberOfObjectives) {
        return new FListsManager(numberOfObjectives);
    }

    
    private void removedDominated(Solution s) 
    {
        // REMOVE DOMINATED BY NEW POINT
        // go from the tail of each list toward the head, checking each member to see 
        // if dominated by s and if so remove, stop processing list when value reached 
        // which is better on objective
        for (int i = 0; i< numberOfObjectives; i++) {
            CriteriaNode node = linkedListTails[i];
            while (node!=null){
                if (!node.better(s,i)){
                    //System.out.println("COMPARING: " + s + " to: " + node.cargo );
                    if (s.weaklyDominates(node.cargo)){
                        // PROCESS node.next
                        // check if removed is tail
                        if (node.next == null) {
                            // to be removed is tail, so reset tail
                            linkedListTails[i] = node.prev;
                            if (node.prev != null) // corner case check if list emptied
                                (node.prev).next = null; //disconnect dominated node from list
                        } else { // not tail, so remove status as previous from the next in line
                            (node.next).prev = node.prev;
                        }
                        // PROCESS node.prev
                        // check removed is head    
                        if (node.prev == null){
                            // to be removed is head, so reset head
                            linkedListHeads[i] = node.next;
                            // node.next being null has already been checked and compensated for
                            if (node.next != null)
                                (node.next).prev = null; //disconnect dominated node from list
                        } else {// not head, so remove status as next from previous in line
                            (node.prev).next = node.next;
                        }
                        listLengths[i]--; // track that list has shortened
                    }
                }
                node = node.prev;
            }
        }
    }
    
    private void addToShortestList(Solution s) {
        //System.out.println(this);
        // ADD TO THE SHORTEST LIST TO REBALANCE 
        int listToInsertOn = getIndexOfShortestList();
        CriteriaNode nodeToInsert = new CriteriaNode(s);
        boolean notInserted = true;
        if (linkedListHeads[listToInsertOn] == null) { //special case at start
            linkedListHeads[listToInsertOn] = nodeToInsert;
            linkedListTails[listToInsertOn] = nodeToInsert;
        } else {
            //System.out.println("Inserting into list number " + listToInsertOn);
            CriteriaNode node = linkedListHeads[listToInsertOn];
            while (node!=null){ // if null, then reached tail, so need to handle differently
                if (!node.better(s,listToInsertOn)){
                    // reached a node which holds a value not better than the new
                    // point, so can insert below it
                    CriteriaNode oldPrev = node.prev;
                    if (oldPrev==null)  {// used to be head
                        linkedListHeads[listToInsertOn] = nodeToInsert;
                    } else {
                        nodeToInsert.prev = oldPrev;
                        oldPrev.next = nodeToInsert;
                    }
                    nodeToInsert.next = node;
                    node.prev = nodeToInsert;
                    if (nodeToInsert.next == null)  // used to be tail
                        linkedListTails[listToInsertOn] = nodeToInsert;
                    notInserted = false;    
                    break;
                } 
                node = node.next;
            } 
            //reached tail or inserted
            if (notInserted){
                // replace tail with inserted node
                linkedListTails[listToInsertOn].next = nodeToInsert;
                nodeToInsert.prev = linkedListTails[listToInsertOn];
                linkedListTails[listToInsertOn] = nodeToInsert;
            }
        }
        //System.out.println(this);
        listLengths[listToInsertOn]++;
    }
    
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException 
    {
        if (weaklyDominates(s))
            return false;
        removedDominated(s);
        addToShortestList(s);
        return true;
    }

    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        // go from best value up to first worse on each criteria in each list
        // check if cargo dominates the argument 
        CriteriaNode node;
        for (int i = 0; i < numberOfObjectives; i++){
            node = linkedListHeads[i];
            while (node!=null){ // if null, then reached tail, so move to next list
                if (node.betterOrEqual(s,i)){
                    if (node.cargo.weaklyDominates(s)){
                        return true;
                    }
                } else {
                    // reached a point now worse on criteria, so this cargro and rest of 
                    // this list cannot possibly weakly dominate
                    break; 
                }
                node = node.next;
            }
        }

        return false;
    }

    public Collection<? extends Solution> getContents()
    {
        ArrayList<Solution> contents = new ArrayList<Solution>();
        for (CriteriaNode node : linkedListHeads) {
            while (node != null){
                contents.add(node.cargo);
                node = node.next;
            }
        }
        return contents;
    }

    public int size() {
        int size = 0;
        for (int i : listLengths)
            size += i;
        return size;
    }

    public void clean() {
        this.linkedListHeads = new CriteriaNode[numberOfObjectives];
        this.linkedListTails = new CriteriaNode[numberOfObjectives];
        this.listLengths = new int[numberOfObjectives];
    }

    public Solution getRandomMember() throws UnsupportedOperationException 
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        String text = "";
        for (CriteriaNode node : linkedListHeads) {
            while (node != null){
                text += "- item: " + node.cargo;
                node = node.next;
            }
            text += "\n";
        }
        text += "HEADS: ";
        for (CriteriaNode c : linkedListHeads){
            if (c != null)
                text += c.cargo + " - item: ";
            else
                text += "null - item: ";
        }
        text += "\nTAILS: ";
        for (CriteriaNode c : linkedListTails){
            if (c != null)
                text += c.cargo + " - item: ";
            else
                text += "null - item: ";
        }
                
        return text+"\n";
    }
    
}
