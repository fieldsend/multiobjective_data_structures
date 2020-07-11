package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.ArrayList;

/**
 * Implementation of the Quad Tree variant 3, see 
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
public class MTQuadTree3 extends MTQuadTree1
{

    /** 
     * Constrcutor to be called by factory method
     */
    private MTQuadTree3(int numberOfObjectives) { 
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
            return recursivelyAddProcess(s,root);
        }
    }

    /*
     * Method replaces cargo in node n in tree with s, and manages reinsertion if required 
     * of subtree members
     */
    private void replace(MTQuadTreeNode n, Solution s) {
        //System.out.println("In Replace "+ numberOfElements+" "+ s + " " +n.getCargo());
        
        n.setCargo(s);
        MTQuadTreeNode[] potentialDominated = n.getChildren();
        n.cleanChildren();
        for (MTQuadTreeNode child : potentialDominated) 
            if (child!=null)
                reconsider(n,child);
    }

    private void reconsider(MTQuadTreeNode c, MTQuadTreeNode t) {
        //System.out.println("In Reconsider "+ numberOfElements+" "+ c.getCargo() + " " +t.getCargo());
        
        MTQuadTreeNode[] potentialDominated = t.getChildren();
        t.cleanChildren();
        for (MTQuadTreeNode child : potentialDominated) 
            if (child!=null)
                reconsider(c,child);
        int index = t.getCargo().worseOrEqualIndex(c.getCargo(),elementWeights);
        if (index==MAX_INDEX) { // t is dominated so discard
            numberOfElements--;
            return;
        }
        if (c.getChild(index)==null) {
            c.setChild(t,index);
        } else {
            reinsert(c.getChild(index),t);
        }
    }

    private void reinsert(MTQuadTreeNode c, MTQuadTreeNode t) {
        //System.out.println("In Reinsert "+ numberOfElements+" "+ c.getCargo() + " " +t.getCargo());
        
        for (MTQuadTreeNode child : t.getChildren()) 
            if (child!=null)
                reinsert(c,child);
        t.cleanChildren();        
        int index = t.getCargo().worseOrEqualIndex(c.getCargo(),elementWeights);
        if (c.getChild(index)==null) {
            c.setChild(t,index);
        } else {
            reinsert(c.getChild(index),t);
        }
    }

    /* returns true if added, false otherwise */
    private boolean recursivelyAddProcess(Solution s, MTQuadTreeNode n) {
        //System.out.println("In RecAddProc "+ numberOfElements+" "+ s + " " +n.getCargo());
        // calculate k-sucessorship STEP 2 in M&T paper
        int index = s.worseOrEqualIndex(n.getCargo(),elementWeights);
        if (index==MAX_INDEX) // solution dominated so discard
            return false;

        if ((index==MIN_INDEX) || (s.equalIndex(n.getCargo(),elementWeights)==index)){ // node is dominated, so will have to be removed and  all its children reinserted
            replace(n,s);
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
                recursiveDominatesCheck(s,n.getChild(l),n,l);

        //check if dominates by sucessor at matching index, and add if necessary
        // STEP 5 in M&T paper
        if (n.getChild(index)==null){
            n.setChild(new MTQuadTreeNode(s,n),index);
            numberOfElements++;
            return true;
        } else { //replace root with k-child, then compare s to this new root
            return recursivelyAddProcess(s, n.getChild(index));
        }

    }

    /* remove any dominated nodes */
    private void recursiveDominatesCheck(Solution s, MTQuadTreeNode n,MTQuadTreeNode parent,int indexOfChild) {
        //System.out.println("In RecDomsCheck "+ numberOfElements+" "+ s + " " +n.getCargo());
        
        int index = s.worseOrEqualIndex(n.getCargo(),elementWeights);
        int[] potentialDominated;
        if ((index==MIN_INDEX) || (s.equalIndex(n.getCargo(),elementWeights)==index)) {// solution dominates so discard
            delete(n,parent,indexOfChild);
            // the indexOfChild node child of parent has now changed, 
            // potentially also dominated
            if (parent.getChild(indexOfChild)!=null)
                recursiveDominatesCheck(s, parent.getChild(indexOfChild),parent,indexOfChild);
        }
        else 
            for (int l : getLchildrenSet1Indices(index)) 
                if (n.getChild(l)!=null)
                    recursiveDominatesCheck(s, n.getChild(l),n,l);
        return;
    }

    /*
     * Returns reference to node which has replace n at indexOfChild with the parent
     */
    private void delete(MTQuadTreeNode n,MTQuadTreeNode parent,int indexOfChild) {
        //System.out.println("In delete "+ numberOfElements+" " +n.getCargo());
        
        for (int i=MIN_INDEX+1; i<MAX_INDEX; i++) {
            if (n.getChild(i)!=null) {
                numberOfElements--;
                parent.setChild(n.getChild(i),indexOfChild);
                for (int k = i+1; k <MAX_INDEX; k++) 
                    if (n.getChild(k)!=null)
                        reinsert(n.getChild(i),n.getChild(k));
                return;
            }
        }
        // n has no children, so simply detach from its parent
        numberOfElements--;
        parent.removeChild(indexOfChild);
    }
    
    /**
     * Factory method to return and instance of ParetoSetManager to maintain the
     * solutions with the number of objectives passed as an argument
     */
    public static ParetoSetManager managerFactory(int numberOfObjectives) {
        return new MTQuadTree3(numberOfObjectives);
    }
}
