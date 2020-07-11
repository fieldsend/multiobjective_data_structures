package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.ArrayList;

/**
 * Implementation of the Quad Tree variant 2, see 
 * 
 * Sanaz Mostaghim, J¨urgen Teich, and Ambrish Tyagi. 2002. 
 * Comparison of data structures for storing Pareto-sets in MOEAs. 
 * In IEEE Congress on Evolutionary Computation, 2002. CEC ‘02. IEEE
 *
 * and
 *
 * Sanaz Mostaghim and J¨urgen Teich. 2003. 
 * Quad-trees: A Data Structure for Storing Pareto-sets in Multi-objective Evolutionary Algorithms with Elitism. 
 * In Evolutionary Multiobjective Optimization, 
 * Ajith Abraham, Lakhmi Jain, and Robert Goldberg (Eds.). 
 * Springer, 81–104
 * 
 * @author Jonathan Fieldsend 
 * @version 1.0
 */
public class MTQuadTree2 extends MTQuadTree1
{
    private boolean dominationFlag;

    private MTQuadTree2(int numberOfObjectives) { 
        super(numberOfObjectives);
    }

    @Override
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException{
        if (s.getNumberOfObjectives()!=NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("Manager set up for " + NUMBER_OF_OBJECTIVES 
                + " objectives, however solution added has "+ s.getNumberOfObjectives());
        if (root == null){
            root = new MTQuadTreeNode(s,null);
            numberOfElements++;
            return true;
        } else {
            return recursivelyAddProcess(s,root, new ArrayList<MTQuadTreeNode>());
        }
    }

    /* returns true if added, false otherwise */
    private boolean recursivelyAddProcess(Solution s, MTQuadTreeNode n, ArrayList<MTQuadTreeNode> toReinsert) {
        //System.out.println("In RecAddProc "+ numberOfElements+" "+ s + " " +n.getCargo());
        // calculate k-sucessorship STEP 2 in M&T paper
        int index = s.worseOrEqualIndex(n.getCargo(),elementWeights);
        if (index==MAX_INDEX) // solution dominated so discard
            return false;

        if ((index==MIN_INDEX) || (s.equalIndex(n.getCargo(),elementWeights) == index)){ // node is dominated, so will have to be removed and  all its children reinserted
            dominationFlag = true;
            MTQuadTreeNode[] childrenOfDominatedNode = n.getChildren();
            n.setCargoAndCleanChildren(s); // one added and one removed, so no changed of numberOfElements necessary
            // numberOfElements decreases and increases by one, so net change is 0
            ArrayList<MTQuadTreeNode> toReinsertExtracted = new ArrayList<>();
            for (MTQuadTreeNode child : childrenOfDominatedNode){ 
                if (child!=null)
                    recursivelyExtractToReInsert(toReinsertExtracted,s,child,n); // add child (at it's children) from n as parent
            }
            reInsert(toReinsertExtracted,n);
            if (toReinsert.size()>0) {
                reInsert(toReinsert,root);
            }
            return true;
        }
        //check if dominated by any sucessors STEP 3 in M&T paper
        int[] potentialDominators = getLchildrenSet0IndicesLessThanK(index);
        for (int l : potentialDominators) 
            if (n.getChild(l)!=null)
                if (recursiveDominatedCheck(s,n.getChild(l)))
                    return false;

        // check if proposed solution dominates any sucessors, if so delete it and reinsert all its children 
        // STEP 4 in M&T paper
        int[] potentialDominated = getLchildrenSet1IndicesLessThanL(index);
        for (int l : potentialDominated) 
            if (n.getChild(l)!=null)
                recursiveDominatesCheck(toReinsert,s,n.getChild(l),n,l,false);

        //check if dominates by sucessor at matching index, and add if necessary
        // STEP 5 in M&T paper
        if (n.getChild(index)==null){
            n.setChild(new MTQuadTreeNode(s,n),index);
            numberOfElements++;
            if (toReinsert.size()>0) {
                reInsert(toReinsert,root);
            }
            return true;
        } else { //replace root with k-child, then compare s to this new root
            return recursivelyAddProcess(s, n.getChild(index),toReinsert);
        }

    }

    private void reInsert(ArrayList<MTQuadTreeNode> toReinsert, MTQuadTreeNode n) {
        for (MTQuadTreeNode c : toReinsert) {
            c.cleanChildren(); // all children will have been recursively processed by now
            //System.out.println("REINSERTING "+ numberOfElements+" " +  c.getCargo());
            reInsert(c,n);
        }
        dominationFlag = false;
    }

    private void reInsert(MTQuadTreeNode c, MTQuadTreeNode n) {
        int index = c.getCargo().worseOrEqualIndex(n.getCargo(),elementWeights);

        if (n.getChild(index)==null){
            n.setChild(c,index);
            numberOfElements++;
            return;
        } else { //replace root with k-child, then compare c to this as the new root
            //last argument is false as at this point all c's orginal children have been stripped off
            //and checked, and the counter already updated with c's removal
            reInsert(c, n.getChild(index));
            return;
        }
    }

    private void recursivelyExtractToReInsert(ArrayList<MTQuadTreeNode> toReinsert, Solution potentialDominator, MTQuadTreeNode c, MTQuadTreeNode n)
    {
        
        for (int i = MIN_INDEX+1; i< MAX_INDEX; i++){ 
            if (c.getChild(i)!=null)
                recursivelyExtractToReInsert(toReinsert, potentialDominator,c.getChild(i),n); // add child (and its children) from n as parent
        }
        numberOfElements--; //remove from count as (currently) no longer in tree    
        //System.out.println("In RecExtractCheck "+ numberOfElements+" " +potentialDominator + " " + c.getCargo());
        
        //If got to this point then no children for node remaining to be processed
        //int index = potentialDominator.worseOrEqualIndex(c.getCargo(),elementWeights);
        //if ((index==MIN_INDEX) || (potentialDominator.equalIndex(c.getCargo(),elementWeights) == index)) // solution dominated by putative solution so discard
        if (potentialDominator.dominates(c.getCargo()))    
            return;
        toReinsert.add(c);  
    }

    /* remove any dominated nodes and add any non-dominated children to list for reinsertion*/
    private void recursiveDominatesCheck(ArrayList<MTQuadTreeNode> toReinsert, Solution s, MTQuadTreeNode child, MTQuadTreeNode parent, int index, boolean dominatedPath) {
        //System.out.println("In RecDomsCheck " + numberOfElements+" "+s + " " + child.getCargo() + " dom path "+ dominatedPath);
        
        //int k = s.worseOrEqualIndex(child.getCargo(),elementWeights);
        if (s.dominates(child.getCargo())) { // sucessor is dominated by s, so remove and check all children
            dominationFlag = true;
            parent.removeChild(index);
            numberOfElements--;
            //System.out.println("REMOVING DOMINATED " + child.getCargo());
            for (int l=MIN_INDEX+1; l<MAX_INDEX; l++)
                if (child.getChild(l)!=null)
                    recursiveDominatesCheck(toReinsert,s,child.getChild(l),child,l,true);

            return;
        }
        // child is not dominated
        int k = s.worseOrEqualIndex(child.getCargo(),elementWeights);
        if (dominatedPath) {
            //System.out.println("REMOVING FOR REINSERTION " + child.getCargo());
            toReinsert.add(child); // track for reinsertion if a parent in the path has been marked for removal
            numberOfElements--;
            for (int l=MIN_INDEX+1; l<MAX_INDEX; l++)
                if (child.getChild(l)!=null)
                    recursiveDominatesCheck(toReinsert,s,child.getChild(l),child,l,dominatedPath);
            return;
        } else {
            int[] potentialDominated = getLchildrenSet1Indices(k);

            for (int l : potentialDominated) 
                if (child.getChild(l)!=null)
                    recursiveDominatesCheck(toReinsert, s, child.getChild(l), child, l, dominatedPath);
            return;
        }
    }

    /**
     * Factory method to return and instance of ParetoSetManager to maintain the
     * solutions with the number of objectives passed as an argument
     */
    public static ParetoSetManager managerFactory(int numberOfObjectives) {
        return new MTQuadTree2(numberOfObjectives);
    }
}
