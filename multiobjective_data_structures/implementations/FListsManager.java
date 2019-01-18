package multiobjective_data_structures.implementations;

import multiobjective_data_structures.*;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Write a description of class MinFlistsManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class FListsManager implements ParetoSetManager
{
    final int NUMBER_OF_OBJECTIVES;
    int listToInsertOn;
    int[] numberProcessedBetter;
    CriteriaNode[] processedBetter;
    CriteriaNode[] linkedListHeads;
    CriteriaNode[] linkedListTails;
    int[] listLengths;

    // numberOfObjectives lists of doubly-linked CriteriaNodes
    // ordered by each criteria. Each solution is in only *one* of these lists
    // head of list is lowest (best) stored on that criteria. Tail of list is 
    // highest (worst) on that criteria

    class CriteriaNode {
        CriteriaNode prev;
        CriteriaNode next;
        Solution cargo;

        boolean better(Solution s, int activeCriterion) {
            return cargo.getFitness(activeCriterion) < s.getFitness(activeCriterion);
        }

        boolean betterOrEqual(Solution s, int activeCriterion) {
            return cargo.getFitness(activeCriterion) <= s.getFitness(activeCriterion);
        }

        boolean equals(Solution s, int activeCriterion) {
            return cargo.getFitness(activeCriterion) == s.getFitness(activeCriterion);
        }
        
        CriteriaNode(Solution cargo) {
            this.cargo = cargo;
        }
        
        @Override
        public String toString() 
        {
            String text = "Tree: ";
            CriteriaNode node = this;
            do {
                text += "  -- " +node.cargo;
                node = node.next;
            } while(node != null);
            return text;
        }
    }

    FListsManager(int numberOfObjectives) {
        this.NUMBER_OF_OBJECTIVES = numberOfObjectives;
        this.linkedListHeads = new CriteriaNode[numberOfObjectives];
        this.linkedListTails = new CriteriaNode[numberOfObjectives];
        this.processedBetter = new CriteriaNode[numberOfObjectives];
        this.listLengths = new int[numberOfObjectives];
        this.numberProcessedBetter = new int[numberOfObjectives];
    }
    
    
    private int getIndexOfShortestList() {
        int index = 0;
        int shortest = listLengths[0];
        for (int i = 1; i< NUMBER_OF_OBJECTIVES; i++) {
            if (listLengths[i] < shortest){
                shortest = listLengths[i];
                index = i;
            }
        }
        return index;
    }

    public boolean add(Solution s) throws IllegalNumberOfObjectivesException 
    {
        if (weaklyDominates(s))
            return false;
        removeDominated(s);
        
        addToLowestList(s);
        return true;
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
        this.linkedListHeads = new CriteriaNode[NUMBER_OF_OBJECTIVES];
        this.linkedListTails = new CriteriaNode[NUMBER_OF_OBJECTIVES];
        this.listLengths = new int[NUMBER_OF_OBJECTIVES];
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
   
    abstract void trackList(CriteriaNode processed, int numberProcessed, int listIndex);

    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        //System.out.println("CHECKING: " + s);
            
        // go from best value up to first worse on each criteria in each list
        // check if cargo dominates the argument 
        for (int i = 0; i < NUMBER_OF_OBJECTIVES; i++){
            //System.out.println(linkedListHeads[i]+ " :LL: " + listLengths[i]);
            numberProcessedBetter[i] = 0;
            processedBetter[i] = linkedListHeads[i];
            // go through all points with better values on objective i
            while (processedBetter[i]!=null){ // if null, then reached tail, so move to next list
                if (processedBetter[i].better(s,i)){
                    if (processedBetter[i].cargo.weaklyDominates(s)){
                        return true;
                    }
                    processedBetter[i] = processedBetter[i].next;
                    numberProcessedBetter[i]++;
                } else {
                    // reached a point now equal or worse on criteria
                    break; 
                }
            }
            CriteriaNode processed = processedBetter[i];
            // now go through all points equal on objective i
            while (processed!=null){ // if null, then reached tail, so move to next list
                if (processed.equals(s,i)){
                    if (processed.cargo.weaklyDominates(s)){
                        return true;
                    }
                    processed = processed.next;
                } else {
                    // reached a point now worse on criteria, so this cargo and rest of 
                    // this list cannot possibly weakly dominate
                    break; 
                }
            }
        }
        // no possible stored point dominates
        return false;
    }

    private void removeDominated(Solution s) 
    {
        // REMOVE DOMINATED BY NEW POINT
        // go from the tail of each list toward the head, checking each member to see 
        // if dominated by s and if so remove, stop processing list when value reached 
        // which is better on objective
        for (int i = 0; i < NUMBER_OF_OBJECTIVES; i++) {
            CriteriaNode node = processedBetter[i]; // set to first node found that wasn't better on objective
            if (node!=null)
                processedBetter[i] = node.prev; // now points toward last node to be better -- needed by addToLowestList
            while (node!=null){ // could have a for here, as know length from first not better to end
                if (s.weaklyDominates(node.cargo)){
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
                node = node.next;
            }
        }
    }

    private void addToLowestList(Solution s) {
        //System.out.println(this);
        // ADD TO LIST where objective value is lowest (in terms of number of elements better)
        
        
        // make node to insert        
        CriteriaNode nodeToInsert = new CriteriaNode(s);
        // check point wasn't top
        if (listLengths[listToInsertOn] == 0) { //special case at start
            //System.out.println("Adding at head and tail "+ listToInsertOn);
            linkedListHeads[listToInsertOn] = nodeToInsert;
            linkedListTails[listToInsertOn] = nodeToInsert;
        } else {
            //node directly before where we want to insert
            CriteriaNode node = processedBetter[listToInsertOn];
            if (node == null) {
                if (numberProcessedBetter[listToInsertOn] == 0) { // no member of list is better, so add at head
                    //System.out.println("Adding at head " + listToInsertOn);
                    linkedListHeads[listToInsertOn].prev = nodeToInsert;
                    nodeToInsert.next = linkedListHeads[listToInsertOn];
                    linkedListHeads[listToInsertOn] = nodeToInsert;
                } else { //null because tail was reached and all were better on the selected abjective
                    //System.out.println("Adding at tail " + listToInsertOn);
                    linkedListTails[listToInsertOn].next = nodeToInsert;
                    nodeToInsert.prev = linkedListTails[listToInsertOn];
                    linkedListTails[listToInsertOn] = nodeToInsert;
                }
            } else {
                CriteriaNode oldNext = node.next;
                nodeToInsert.prev = node;
                node.next = nodeToInsert;
                nodeToInsert.next = oldNext;
                if (oldNext == null)
                    linkedListTails[listToInsertOn] = nodeToInsert; //DON't THINK THIS IS REQUIRED< NEED TO CHECK
                else
                    oldNext.prev = nodeToInsert;
            } 
        }
        //System.out.println(this);
        listLengths[listToInsertOn]++;
    }
}
