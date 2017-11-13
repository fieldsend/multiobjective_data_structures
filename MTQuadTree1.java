import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Write a description of class MTQuadTree1 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MTQuadTree1 implements ParetoSetManager
{
    private MTQuadTreeNode root = null;
    private int numberOfElements = 0;
    //private HashMap<ArrayList<Boolean>,Integer> kSucessorMap = new HashMap<ArrayList<Boolean>,Integer>();
    //private HashMap<Integer,int[]> k0SetMap = new HashMap<>();
    //private HashMap<Integer,int[]> k1SetMap = new HashMap<>();
    private final int NUMBER_OF_OBJECTIVES;
    private int[] elementWeights;
    private final int MAX_INDEX;
    private final int MIN_INDEX=0;
    public MTQuadTree1(int numberOfObjectives) { 
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
        elementWeights = new int[numberOfObjectives];
        for (int i = 0; i < numberOfObjectives; i++)
            elementWeights[i] = (int) Math.pow(2,i);
        MAX_INDEX = elementWeights[elementWeights.length-1];
    }

    @Override
    public boolean weaklyDominates(Solution s){
        //throw new NullPointerException();
        return true;
    }
    
    private int[] getLchildrenIndicesLessthanK(int k) {
        return new int[] {};
    }
    
    @Override
    public boolean add(Solution s) throws IllegalNumberOfObjectives{
        if (s.getNumberOfObjectives()!=NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectives("Manager set up for " + NUMBER_OF_OBJECTIVES 
            + " objectives, however solution added has "+ s.getNumberOfObjectives());
        if (root == null){
            root = new MTQuadTreeNode(s,null);
            numberOfElements++;
            this.setUpMap(s.getNumberOfObjectives()); // set up indexing map on first use
            return true;
        } else {
            return recursivelyAddProcess(s, root);
        }
    }
    /* returns true if added, false otherwise */
    private boolean recursivelyAddProcess(Solution s, MTQuadTreeNode n) {
        // calculate k-sucessorship
        int index = s.betterOrEqualIndex(n.getCargo(),elementWeights);
        if (index==MAX_INDEX) // solution dominated so discard
            return false;
        MTQuadTreeNode[] children = n.getChildren();
            
        if (index==MIN_INDEX){ // node is dominated, so will have to be removed and  all its children reinserted
            n.setCargoAndCleanChildren(s);
            
            for (MTQuadTreeNode child : children) 
                if (child!=null)
                    recursivelyReInsert(child,n); // add child (at it's children) from n as parent
            return true;
        }
        //check if dominated by any sucessors
        if (recursiveDominatedCheck(s,n,index))
            return false;
        // check if dominates any sucessors
        recursiveDominatesCheck(s,n,index);
        //check if dominates by sucessor at matching index, and add if necessary
        if (children[index]==null){
            children[index] = new MTQuadTreeNode(s,n);
            return true;
        } else { //replace root with k-child, then compare s to this new root
            n.setCargo(children[index].getCargo());
            n.setChildren(children[index].getChildren());
            return recursivelyAddProcess(s, n);
        }
        
    }
    
    private void recursivelyReInsert(MTQuadTreeNode c, MTQuadTreeNode n)
    {
        /*
         * If reinserting rather thank checking a new solution, then certain situations do not need
         * checking for, e.g. that it dominates anything
         */
        MTQuadTreeNode[] children = n.getChildren();
        for (MTQuadTreeNode child : children) 
                if (child!=null)
                    recursivelyReInsert(child,n); // add child (at it's children) from n as parent
            
        int index = c.getCargo().betterOrEqualIndex(n.getCargo(),elementWeights);
        if (index==MAX_INDEX) // solution dominated so discard
            return;
        
        //check if dominated by any sucessors
        if (recursiveDominatedCheck(c.getCargo(),n,index))
            return;
            
        //check if dominated by sucessor at matching index, and add if necessary
        if (children[index]==null){
            children[index] = new MTQuadTreeNode(c.getCargo(),n);
            return;
        } else { //replace root with k-child, then compare s to this new root
            n.setCargo(children[index].getCargo());
            n.setChildren(children[index].getChildren());
            recursivelyReInsert(c, n);
            return;
        }
    }
    
    private int[] getLchildrenSet0IndicesLessThanK(int index)
    {
        return new int[]{};
    }
    
    private int[] getLchildrenSet1IndicesLessThanL(int index)
    {
        return new int[]{};
    }
    
    /* returns true if s dominated by any sucessors of n*/
    private boolean recursiveDominatedCheck(Solution s, MTQuadTreeNode n, int index) {
        MTQuadTreeNode[] children = n.getChildren();
        int[] potentialDominators = getLchildrenSet0IndicesLessThanK(index);
        for (int l : potentialDominators) {
            if (children[l]!=null){
                int kIndex = s.betterOrEqualIndex(children[l].getCargo(),elementWeights);
                if (kIndex==MAX_INDEX) 
                    return true;
                
                if (recursiveDominatedCheck(s, children[l], kIndex))
                    return true;
            }
        }
        return false;
    }
    
    /* remove any dominated nodes and re-add any non-dominated children*/
    private void recursiveDominatesCheck(Solution s, MTQuadTreeNode n, int index) {
        MTQuadTreeNode[] children = n.getChildren();
        int[] potentialDominated = getLchildrenSet1IndicesLessThanL(index);
        for (int l : potentialDominated) {
            if (children[l]!=null){
                int kIndex = s.betterOrEqualIndex(children[l].getCargo(),elementWeights);
                if (kIndex==0) 
                    return;
                
                recursiveDominatesCheck(s, children[l], kIndex);
            }
        }
        return;
    }
    
    
    private void setUpMap(int m) {
        
        // go through all permutations of objective comparisons to propogate
        // map between boolean arrays of quality comparison, and k-successorship
        /*
        int numberOfDistinctVectors = (int) Math.pow(2,m);
        for (int j = 0; j < numberOfDistinctVectors; j++) {
            ArrayList<Boolean> array = new ArrayList<Boolean>(m);
            int k = 0;
            for (int i = 0; i< m; i++) {
                int val = m*i+j;
                int ret  = (1 & (val >>> i));
                array.add(ret != 0);
                k += (array.get(i)) ? (int) Math.pow(2,m-1) : 0;
                kSucessorMap.put(array,new Integer(k));
            }
        }
        */
    }
    
    @Override
    public Set<Solution> getContents() {
        Set<Solution> s = new HashSet<Solution>(numberOfElements);
        if (root != null)
            this.recursivelyExtract(s,root);
        return s;
    }
    
    private void recursivelyExtract(Set<Solution> s, MTQuadTreeNode node) {
        s.add(node.getCargo());
        for (MTQuadTreeNode child: node.getChildren())
            this.recursivelyExtract(s, child);
        return;    
    }
    
    @Override
    public Solution getRandomMember() {
        Solution s = null;
        
        return s;
    }
    
    
    @Override
    public int size() {
        return numberOfElements;
    }
    
    @Override
    public void clean() {
        root = null;
    }
}
