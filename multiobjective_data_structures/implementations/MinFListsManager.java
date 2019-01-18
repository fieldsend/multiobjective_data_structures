package multiobjective_data_structures.implementations;

import multiobjective_data_structures.*;

/**
 * Write a description of class MinFlistsManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MinFListsManager extends FListsManager
{
    MinFListsManager(int numberOfObjectives) 
    {
        super(numberOfObjectives);
    }
    
    public static MinFListsManager managerFactory(int numberOfObjectives) 
    {
        return new MinFListsManager(numberOfObjectives);
    }

    void trackList(CriteriaNode processed, int numberProcessed, int listIndex) 
    {
        // select list which is shortest
        listToInsertOn = 0;
        for (int i = 1; i < NUMBER_OF_OBJECTIVES; i++)
            if (numberProcessedBetter[i] < numberProcessedBetter[listToInsertOn])
                listToInsertOn = i;
    }
    
    
    /*int[] numberProcessedBetter;
    CriteriaNode[] processedBetter;
    CriteriaNode[] processedEqual;

    MinFListsManager(int n) {
        super(n);
        numberProcessedBetter = new int[n];
        processedBetter = new CriteriaNode[n];
        processedEqual = new CriteriaNode[n];
    }

    public boolean add(Solution s) throws IllegalNumberOfObjectivesException 
    {
        if (weaklyDominates(s))
            return false;
        // numberProcessedBetter array now holds number processed in each list until 
        // a worse (larger) or equal solution found on the corresponding criteria
        // processedBetter holds this corresponding node
        // can process from this point onwards in removeDominated
        removeDominated(s);
        addToLowestList(s);
        //for (int i = 0; i < numberOfObjectives; i++)
        //    System.out.println(linkedListHeads[i] + " :LL: " + listLengths[i]);
        return true;
    }

    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        //System.out.println("CHECKING: " + s);
            
        // go from best value up to first worse on each criteria in each list
        // check if cargo dominates the argument 
        for (int i = 0; i < numberOfObjectives; i++){
            //System.out.println(linkedListHeads[i]+ " :LL: " + listLengths[i]);
            numberProcessedBetter[i] = 0;
            processedBetter[i]  = linkedListHeads[i];
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
            // now go through all points equal on objective i
            processedEqual[i] = processedBetter[i];
            while (processedEqual[i]!=null){ // if null, then reached tail, so move to next list
                if (processedEqual[i].equals(s,i)){
                    if (processedEqual[i].cargo.weaklyDominates(s)){
                        return true;
                    }
                    processedEqual[i] = processedEqual[i].next;
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
        for (int i = 0; i< numberOfObjectives; i++) {
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
        
        
        // get list index to insert on
        int listToInsertOn = 0;
        for (int i = 1; i< numberOfObjectives; i++) 
            if (numberProcessedBetter[i] < numberProcessedBetter[listToInsertOn])
                listToInsertOn = i;
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
    
    
    public static MinFListsManager managerFactory(int numberOfObjectives) {
        return new MinFListsManager(numberOfObjectives);
    }*/
}
