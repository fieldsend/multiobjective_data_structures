import java.util.ArrayList;

/**
 * Write a description of class MTQuadTree3 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MTQuadTree3 extends MTQuadTree1
{

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
        n.setCargo(s);
        MTQuadTreeNode[] potentialDominated = n.getChildren();
        n.cleanChildren();
        for (MTQuadTreeNode child : potentialDominated) 
            if (child!=null)
                reconsider(n,child);
    }

    private void reconsider(MTQuadTreeNode c, MTQuadTreeNode t) {
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
        for (MTQuadTreeNode child : t.getChildren()) 
            if (child!=null)
                reinsert(c,child);
        int index = t.getCargo().worseOrEqualIndex(c.getCargo(),elementWeights);
        if (c.getChild(index)==null) {
            c.setChild(t,index);
        } else {
            reinsert(c.getChild(index),t);
        }
    }

    /* returns true if added, false otherwise */
    private boolean recursivelyAddProcess(Solution s, MTQuadTreeNode n) {
        System.out.println("In RecAddProc "+ numberOfElements+" "+ s + " " +n.getCargo());
        // calculate k-sucessorship STEP 2 in M&T paper
        int index = s.worseOrEqualIndex(n.getCargo(),elementWeights);
        if (index==MAX_INDEX) // solution dominated so discard
            return false;

        if (index==MIN_INDEX){ // node is dominated, so will have to be removed and  all its children reinserted
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
        int index = s.worseOrEqualIndex(n.getCargo(),elementWeights);
        if (index==0) {// solution dominates so discard
            delete(n,parent,indexOfChild);
            return;
        }
        int[] potentialDominated = getLchildrenSet1Indices(index);

        for (int l : potentialDominated) 
            if (n.getChild(l)!=null)
                recursiveDominatesCheck(s, n.getChild(l),n,l);
        return;
    }

    private void delete(MTQuadTreeNode n,MTQuadTreeNode parent,int indexOfChild) {
        for (int i=MIN_INDEX+1; i<MAX_INDEX; i++) {
            if (n.getChild(i)!=null) {
                numberOfElements--;
                parent.setChild(n.getChild(i),indexOfChild);
                for (int k = i+1; k <MAX_INDEX; k++) 
                    if (n.getChild(k)!=null)
                        reinsert(n.getChild(i),n.getChild(k));
                
            }
        }
        // n has no children, so simply detach from its parent
        numberOfElements--;
        parent.removeChild(indexOfChild);
    }
    public static ParetoSetManager managerFactory(int numberOfObjectives) {
        return new MTQuadTree3(numberOfObjectives);
    }
}
