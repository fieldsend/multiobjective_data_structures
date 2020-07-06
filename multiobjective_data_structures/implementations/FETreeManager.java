package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Write a description of class FETreeManager, utilises the dominated and non-domianted tree
 * structures of Fieldsend et al.
 * 
 * @author Jonathan Fieldsend
 * @version 1.0
 */

public class FETreeManager implements ParetoSetManager
{
    private LLDominatedTree dominatedTree;
    private LLNonDominatedTree nonDominatedTree;
    private int numberOfObjectives;
    private int numberStored = 0;
    private LLObjectiveTree[] sortedListsOfObjectives; 
    private boolean currentLinkedListInsertFlag = false;

    private FETreeManager(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
        sortedListsOfObjectives = new LLObjectiveTree[numberOfObjectives];
        for (int i=0; i<numberOfObjectives; i++)
            sortedListsOfObjectives[i] = new LLObjectiveTree();
        dominatedTree = new LLDominatedTree(numberOfObjectives,sortedListsOfObjectives);
        nonDominatedTree = new LLNonDominatedTree(numberOfObjectives,sortedListsOfObjectives); 
    }

    private int bestTreeSize() {
        return numberStored/numberOfObjectives + 1;
    }
    
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException {
        /*if (numberStored>=1) {
        //System.out.println(nonDominatedTree);
        //System.out.println(dominatedTree);
        System.out.println("NDT size: " + nonDominatedTree.size());
        System.out.println("DT size: " + dominatedTree.size());
        int[] checker = nonDominatedTree.getActiveElementsOnEachObjective();
        for (int i : checker)
        System.out.print(i + ", ");
        System.out.println();
        checker = dominatedTree.getNonNullElementsOnEachObjective();
        for (int i : checker)
        System.out.print(i + ", ");
        System.out.println("-----------------");
        System.out.println(nonDominatedTree);
        System.out.println(dominatedTree);
        }*/
        // if over twice as long as minimum possible, then reduce
        
        if ((nonDominatedTree.size() > 1.5 * bestTreeSize()) && (numberStored > numberOfObjectives)){
            System.out.println("remaking tree...");
            currentLinkedListInsertFlag = nonDominatedTree.remakeTreeFromSortedLists(numberStored, currentLinkedListInsertFlag);
            System.out.println("ND tree remade");
        }
        
        if ((dominatedTree.size() > 1.5 * bestTreeSize()) && (numberStored > numberOfObjectives)){
            System.out.println("remaking tree...");
            currentLinkedListInsertFlag = dominatedTree.remakeTreeFromSortedLists(numberStored, currentLinkedListInsertFlag);
            System.out.println("D tree remade");
        }
        
        /*if (nonDominatedTree.size() > 1.2*nonDominatedTree.getMaxActiveElements()){
            //System.out.println("NDTREE, length " +nonDominatedTree.size() + "ceil 2x opt length" + Math.ceil(2*(numberStored/(double) numberOfObjectives)));
            //System.out.println("NDTree: "+ nonDominatedTree);
            //System.out.println("NDTree: "+ nonDominatedTree.toCompleteString());
            //System.out.println("COMPRESSING NDT");
            //System.out.println("-----DTREE, length " +dominatedTree.size());
            //System.out.println("NDTREE, length " +nonDominatedTree.size());
            nonDominatedTree.compress();
            //System.out.println("COMPRESSED NDTREE, length " +nonDominatedTree.size());
            //System.out.println("NDTree: "+ nonDominatedTree);
            //System.out.println("NDTree: "+ nonDominatedTree.toCompleteString());
        }*/
        /*System.out.println("NDTREE, length " +nonDominatedTree.size());
        System.out.println("DTREE, length " +dominatedTree.size());
        System.out.println(nonDominatedTree);
        System.out.println(dominatedTree);    
        System.out.println("NDTree: "+ nonDominatedTree.toCompleteString());
        System.out.println("DTree: "+ dominatedTree.toCompleteString());
         */
        //int[] actElem = nonDominatedTree.getActiveElementsOnEachObjective();
        //for (int i : actElem)
        //    System.out.print("elem " + i + ", ");
        //System.out.println();    
        System.out.println("NDTREE, length " +nonDominatedTree.size() +", DTREE, length " +dominatedTree.size());
        // if over twice as long as minimum possible, then reduce
    /*    if (dominatedTree.size() > 1.2*dominatedTree.getMaxActiveElements()){
            //System.out.println("COMPRESSING DT");
            dominatedTree.compress();
        }*/
        //System.out.println("NDTree: "+ nonDominatedTree.toCompleteString());
        //System.out.println("DTree: "+ dominatedTree.toCompleteString());
        if (s.getNumberOfObjectives() != numberOfObjectives)
            throw new IllegalNumberOfObjectivesException();
        if (!weaklyDominates(s)) {
            // clean dominated from trees
            FETreeSolutionWrapper trackedPoint = new FETreeSolutionWrapper(s,currentLinkedListInsertFlag);
            HashSet<FETreeSolutionWrapper> dominatedSet = new HashSet<>(); // will hold all dominated solutions, with references to the cp they are in
            boolean inserted = nonDominatedTree.getDominatedByInsertAndClean(trackedPoint, dominatedSet);
            dominatedSet.remove(null); //clean any null entry
            numberStored -= dominatedSet.size();
            //System.out.println("CURRENT LENGTH OF NDT, "+nonDominatedTree.size());
            if (!inserted) {
                nonDominatedTree.add(trackedPoint);
                System.out.println("NDTree: ADD");
            }
            //System.out.println("UPDATED NDTREE, length " +nonDominatedTree.size());
            //System.out.println("NDTree: "+ nonDominatedTree);
            int insertedCount = 0;    
            for (FETreeSolutionWrapper c : dominatedSet)
                insertedCount += dominatedTree.deletePossiblyInsert(c,trackedPoint);

            // now remove dominated members from the sorted lists    
            for (FETreeSolutionWrapper c : dominatedSet)
                c.detachFromSortedLists(sortedListsOfObjectives);    
            //System.out.println("DTree: "+ dominatedTree);

            if (insertedCount == 0) {
                //System.out.println("ADDING..." + trackedPoint);
                dominatedTree.add(trackedPoint);
            }
            // update linked list of each objective

            if (sortedListsOfObjectives[0].size==0) {
                //special case where lists are empty
                for (int i = 0; i < numberOfObjectives; i++)
                    sortedListsOfObjectives[i].addFirst(trackedPoint, i);
            } else {

                // get the composite point the new design is in
                FETreeCompositePoint dominatedCompositePoint = trackedPoint.getDominatedTreeMembership() ;
                FETreeSolutionWrapper[] startSet = new FETreeSolutionWrapper[numberOfObjectives];
                double[] distances = new double[numberOfObjectives];
                boolean empty = true;
                for (int i=0; i< numberOfObjectives; i++) {
                    FETreeSolutionWrapper e = dominatedCompositePoint.getElement(i);
                    if ((e != trackedPoint) && (e!= null)) {
                        if (empty) {
                            empty = false;
                            for (int j=0; j< numberOfObjectives; j++){
                                startSet[j] = e;
                                distances[j] = Math.abs(trackedPoint.getFitness(j)-e.getFitness(j));
                            }
                        } else {
                            for (int j=0; j< numberOfObjectives; j++) {
                                double dist = Math.abs(e.getFitness(j) - trackedPoint.getFitness(j));
                                if (dist < distances[j]){
                                    startSet[j] = e;
                                    distances[j] = dist;
                                }
                            }
                        }
                    }
                }
                // now startSet holds closest members in the same composite point on each objectove
                // which are good starting points to traverse the sorted linked lists for insertion
                if (empty) {
                    // only member of composite point, so insert from tail 
                    for (int j=0; j< numberOfObjectives; j++) 
                        sortedListsOfObjectives[j].insertFromTail(trackedPoint,j);
                } else {
                    for (int j=0; j< numberOfObjectives; j++) 
                        sortedListsOfObjectives[j].insertFromExisting(trackedPoint,startSet[j],j);

                }
            }

            // add new member to length
            numberStored++;
            //System.out.println("NDTree after: "+ nonDominatedTree.toCompleteString());
            //System.out.println("DTree after: "+ dominatedTree.toCompleteString());
            //System.out.println("NDTree: "+ nonDominatedTree);
            return true;   
        }
        return false;
    }

    public int getNumberOfNodesInNonDominatedTree() {
        return nonDominatedTree.size();
    }

    public int getNumberOfNodesInDominatedTree() {
        return dominatedTree.size();
    }

    @Override
    public Solution getRandomMember() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException {
        if (dominatedTree.size() == 0)
            return false;
        //System.out.println("DTree not empty, comparing to relevant members");
        FETreeCompositePoint node = dominatedTree.getBest();//tree.lower(new CompositePoint(queryPoint));
        //System.out.println("TREE BEST:" + node);
        if (node.weaklyDominates(s)) // best dominated composite point (weakly) dominates query, so reject query
            return true;
        //System.out.println("DTree best does not dominate");
        // not dominated by best composite point in dominated tree, so now
        // look through all points up to that dominated by s, and check if any members dominate
        if (dominatedTree.memberDominates(s)) 
            return true;
        //System.out.println("DTree no possible dominating members dominate");

        return false;    
    }

    public Collection<? extends Solution> getContents() {
        return nonDominatedTree.getContents();
    }

    public int size(){
        return numberStored;
    }

    public void clean() {
        numberStored = 0;
        sortedListsOfObjectives = new LLObjectiveTree[numberOfObjectives];
        dominatedTree = new LLDominatedTree(numberOfObjectives, sortedListsOfObjectives);
        nonDominatedTree = new LLNonDominatedTree(numberOfObjectives, sortedListsOfObjectives);
    }

    @Override
    public String toString() {
        String text = "";
        text += "Dominated Tree, size "+dominatedTree.size() + " : "+ dominatedTree +
        "\nNon-Dominated Tree, size "+nonDominatedTree.size() + " : "+ nonDominatedTree +"\n";
        return text;
    }

    public static FETreeManager managerFactory(int numberOfObjectives) {
        return new FETreeManager(numberOfObjectives);
    }
}
