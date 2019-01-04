package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

/**
 * Write a description of class MTQuadTree1 here.
 * 
 * @author Jonathan Fieldsend
 * @version 0.1
 */
public class MTQuadTree1 implements ParetoSetManager
{
    MTQuadTreeNode root = null; // tree root
    int numberOfElements = 0; //current size of managed Pareto set
    public final int NUMBER_OF_OBJECTIVES; // number of objectives of solutions being managed
    int[] elementWeights;
    final int MAX_INDEX;
    final int MIN_INDEX=0;
    int lChildrenSet0IndicesLessThanK[][]; // holds array of indices for all indexs where corresonding 0 bits are in same place and index is lower
    int lChildrenSet1IndicesLessThanL[][]; // holds array of indices for all indexs where corresonding 1 bits are in same place and index is higher
    int lChildrenSet0Indices[][]; // holds array of indices for all indexs where corresonding 0 bits are in same place and index is lower
    int lChildrenSet1Indices[][]; // holds array of indices for all indexs where corresonding 1 bits are in same place and index is higher

    MTQuadTree1(int numberOfObjectives) { 
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
        elementWeights = new int[numberOfObjectives];
        for (int i = 0; i < numberOfObjectives; i++)
            elementWeights[i] = (int) Math.pow(2,i);
        MAX_INDEX = (int) Math.pow(2,numberOfObjectives)-1;
        lChildrenSet0IndicesLessThanK = new int[MAX_INDEX][];
        lChildrenSet1IndicesLessThanL = new int[MAX_INDEX][];
        lChildrenSet0Indices = new int[MAX_INDEX][];
        lChildrenSet1Indices = new int[MAX_INDEX][];
        MTHelperClass.setUpMatrices(MIN_INDEX, MAX_INDEX, lChildrenSet0IndicesLessThanK,lChildrenSet1IndicesLessThanL,lChildrenSet0Indices,lChildrenSet1Indices);
    }

    @Override
    public boolean weaklyDominates(Solution s){
        //throw new NullPointerException();
        int index = s.worseOrEqualIndex(root.getCargo(),elementWeights);
        if (index==MAX_INDEX) // solution dominated so discard
            return true;
        if (index==0) // solution dominating root, so definitely not dominated
            return false;
        return recursiveDominatedCheck(s,root);
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
            return recursivelyAddProcess(s, root);
        }
    }

    @Override
    public Solution getRandomMember() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }
    
    /* returns true if added, false otherwise */
    private boolean recursivelyAddProcess(Solution s, MTQuadTreeNode n) {
        // calculate k-sucessorship STEP 2 in M&T paper
        int index = s.worseOrEqualIndex(n.getCargo(),elementWeights);
        if (index==MAX_INDEX) // solution dominated so discard
            return false;

        if (index==MIN_INDEX){ // node is dominated, so will have to be removed and  all its children reinserted
            MTQuadTreeNode[] childrenOfDominatedNode = n.getChildren();
            n.setCargoAndCleanChildren(s); // one added and one removed, so no changed of numberOfElements necessary
            for (MTQuadTreeNode child : childrenOfDominatedNode){ 
                if (child!=null)
                    recursivelyReInsert(s,child,n,true); // add child (at it's children) from n as parent
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
                recursiveDominatesCheck(s,n,l);

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

    private void recursivelyReInsert(Solution potentialDominator, MTQuadTreeNode c, MTQuadTreeNode n, boolean checkChildren)
    {
        /*
         * If _reinserting_ rather than checking a completely new solution, then certain situations do 
         * not need checking for, e.g. that it dominates anything (as this will have been checked on its 
         * first insertion and acceptance in the tree, and all subsequant insertions cannot have been 
         * dominated by it)
         */
        // situation when first recursively sent for reinsertion, need to also reinsert all children and 
        // remove from overall counter tracking number of members in data structure 
        if (checkChildren) {
            for (int i = MIN_INDEX+1; i< MAX_INDEX; i++){ 
                if (c.getChild(i)!=null)
                    recursivelyReInsert(potentialDominator,c.getChild(i),n,true); // add child (and its children) from n as parent
            }
            numberOfElements--; //remove from count as (currently) no longer in tree    
        }
        //If got to this point then no children for node remaining to be processed
        c.cleanChildren(); // all children will have been recursively processed by now
        int index = c.getCargo().worseOrEqualIndex(potentialDominator,elementWeights);
        if (index==MAX_INDEX) // solution dominated by putative solution so discard
            return;
        index = c.getCargo().worseOrEqualIndex(n.getCargo(),elementWeights);
            
        int[] potentialDominators = getLchildrenSet0IndicesLessThanK(index);
        for (int l : potentialDominators) 
            if (n.getChild(l)!=null)
                if (recursiveDominatedCheck(c.getCargo(),n.getChild(l)))
                    return;
    
        if (n.getChild(index)==null){
            n.setChild(c,index);
            numberOfElements++;
            return;
        } else { //replace root with k-child, then compare c to this as the new root
            // last argument is false as at this point all c's orginal children have been stripped off
            // and checked, and the counter alreday updated with c's removal
            recursivelyReInsert(potentialDominator,c, n.getChild(index),false);
            return;
        }
    }

    int[] getLchildrenSet0IndicesLessThanK(int index)
    {
        return lChildrenSet0IndicesLessThanK[index];
    }

    int[] getLchildrenSet1IndicesLessThanL(int index)
    {
        return lChildrenSet1IndicesLessThanL[index];
    }

    int[] getLchildrenSet0Indices(int index)
    {
        return lChildrenSet0Indices[index];
    }

    int[] getLchildrenSet1Indices(int index)
    {
        return lChildrenSet1Indices[index];
    }

    
    /* returns true if s dominated by any sucessors of n*/
    boolean recursiveDominatedCheck(Solution s, MTQuadTreeNode n) {
        int k = s.worseOrEqualIndex(n.getCargo(),elementWeights);
        if (k==MAX_INDEX)
            return true;

        int[] potentialDominators = getLchildrenSet0Indices(k);
        for (int l : potentialDominators) 
            if (n.getChild(l)!=null)
                if (recursiveDominatedCheck(s, n.getChild(l)))
                    return true;
        return false;
    }

    /* remove any dominated nodes and re-add any non-dominated children*/
    private void recursiveDominatesCheck(Solution s, MTQuadTreeNode n, int index) {
        int k = s.worseOrEqualIndex(n.getChild(index).getCargo(),elementWeights);
        if (k==0) { // sucessor is dominated by s, so remove and reinsert all children from global root
            MTQuadTreeNode[] childrenOfDominatedNode = n.getChild(index).getChildren();
            n.removeChild(index);
            numberOfElements--;
            for (MTQuadTreeNode child : childrenOfDominatedNode){ 
                if (child!=null)
                    recursivelyReInsert(s,child,root,true); // add child (at it's children) from root
            }
            return;
        }
        int[] potentialDominated = getLchildrenSet1Indices(k);
        
        for (int l : potentialDominated) 
            if (n.getChild(index).getChild(l)!=null)
                recursiveDominatesCheck(s, n.getChild(index), l);
        return;
    }

    @Override
    public Collection<? extends Solution> getContents() {
        List<Solution> s = new ArrayList<Solution>(numberOfElements);
        if (root != null)
            this.recursivelyExtract(s,root);
        return s;
    }

    @Override
    public String toString() {
        String s = "";
        if (root != null)
            s+=recursivelyExtractAncestryInString(root) + "\n";
        return s;
    }

    private String recursivelyExtractAncestryInString(MTQuadTreeNode node) {
        String temp = "";
        for (int i = MIN_INDEX+1; i< MAX_INDEX; i++)
            if (node.getChild(i)!=null)
                temp+="- P: " +node.getCargo() +" C:" + node.getChild(i).getCargo()+ recursivelyExtractAncestryInString(node.getChild(i)) + "\n";

        return temp;    
    }

    private void recursivelyExtract(List<Solution> s, MTQuadTreeNode node) {
        s.add(node.getCargo());
        for (int i = MIN_INDEX+1; i< MAX_INDEX; i++)
            if (node.getChild(i)!=null)
                recursivelyExtract(s, node.getChild(i));

        return;    
    }


    @Override
    public int size() {
        return numberOfElements;
    }

    @Override
    public void clean() {
        root = null;
        numberOfElements = 0;
    }

    public static ParetoSetManager managerFactory(int numberOfObjectives) {
        return new MTQuadTree1(numberOfObjectives);
    }
}
