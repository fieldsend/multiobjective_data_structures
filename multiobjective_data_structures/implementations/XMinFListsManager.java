package multiobjective_data_structures.implementations;


import multiobjective_data_structures.*;
/**
 * Write a description of class MinFListsManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class XMinFListsManager// extends BalancedFListsManager
{
    /*private int numberOfNullLists;
    
    // objects to hodl interal processing items when adding
    private CriteriaNode itemBelowInsertionPoint;
    private CriteriaNode itemAboveInsertionPoint;
    private int listToInsertOn;
    private int[][] listBrackets;
    private CriteriaNode[][] processingPoints;
    
    
    private XMinFListsManager(int numberOfObjectives){
        super(numberOfObjectives);
        numberOfNullLists = numberOfObjectives; // track how many lists where Head (and therefore Tail) are null
        listBrackets = new int[numberOfObjectives][2];
        processingPoints = new CriteriaNode[numberOfObjectives][3];
    }
    
    public static XMinFListsManager managerFactory(int numberOfObjectives) {
        return new XMinFListsManager(numberOfObjectives);
    }
    
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException 
    {
        if (weaklyDominates(s))
            return false;
        removeDominated(s);
        addToShortestList(s);
        return true;
    }
    
    private void addToShortestList(Solution s) {
        //System.out.println(this);
        // ADD TO THE SHORTEST LIST TO REBALANCE 
        int listToInsertOn = -1;
        CriteriaNode nodeToInsert = new CriteriaNode(s);
        boolean notInserted = true;
        if (numberOfNullLists != 0) { //special case where there is an empty list
            // if there is an empty list, put the point in the first empty list found
            // and decrement empty list counter
            for (int i=0; i<numberOfObjectives; i++){
                if (linkedListHeads[i] == null) {
                    linkedListHeads[i] = nodeToInsert;
                    linkedListTails[i] = nodeToInsert;
                    numberOfNullLists--;
                    break;
                }
            }
        } else {
            // want to identify that list where the point to insert is closest to the
            // head, and insert it on that list
            
            // get nodes directly below and above insertion point
            parallelProcessLists(s);
            // itemBelowInsertionPoint, itemAboveInsertionPoint, listToInsertOn now all updated
            if (itemBelowInsertionPoint == null) { //next is a head
                linkedListHeads[listToInsertOn] = nodeToInsert;
            } else {
                nodeToInsert.prev = itemBelowInsertionPoint;
                itemBelowInsertionPoint.next = nodeToInsert;
            }
            nodeToInsert.next = itemAboveInsertionPoint;
            if (itemAboveInsertionPoint != null)
                itemAboveInsertionPoint.prev = nodeToInsert;
            else
                linkedListTails[listToInsertOn] = nodeToInsert;
        }
        
        //System.out.println(this);
        listLengths[listToInsertOn]++;
    }
    
    private void parallelProcessLists(Solution s) {
        boolean[] toProcess = new boolean[numberOfObjectives];
        int minInsertion = Integer.MAX_VALUE;
        for (int i=0; i<numberOfObjectives; i++){
            listBrackets[i][0] = 0;
            listBrackets[i][1] = listLengths[i];
            toProcess[i] = true;
            if (minInsertion > listLengths[i])
                minInsertion = listLengths[i];
            processingPoints[i][0] = linkedListHeads[i]; // bottom bracket
            processingPoints[i][2] = linkedListTails[i]; // top bracket: CriteriaNode[i][1] are mid 
        }
        boolean toRun;
        // step through binary search of each list, stop search on each list
        // when minimum brack value is above the maximum of another
        for (int i=0; i<numberOfObjectives; i++){
            do {
                toRun = false;
                if (toProcess[i]){
                    toProcess[i] = stepThroughList(i,s);
                    toRun = true;
                }
            } while (toRun);
        }
                    
        listToInsertOn = 0;
        for (int i=1; i<numberOfObjectives; i++) 
            if (listBrackets[i][0] < listBrackets[listToInsertOn][0]) 
                listToInsertOn = 0;
                    
        itemBelowInsertionPoint = CriteriaNode[listToInsertOn][0];
        itemAboveInsertionPoint = CriteriaNode[listToInsertOn][1];
    }
    
    private boolean stepThroughList(int index, Solution s) {
        if (listBrackets[index][0] - listBrackets[index][1] < 4) {
            // can step through
            while(true){
                if (processingPoints[i][0].cargo.better(s))
                    processingPoints[i][0] = processingPoints[i][0].next;
                    //if (processingPoints[i][0] == processingPoints[listToInsertOn][1])
                    
                }
        }
    }*/
    
}
