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
    private MTQuadTreeNode root = null;
    private int numberOfElements = 0;
    //private HashMap<ArrayList<Boolean>,Integer> kSucessorMap = new HashMap<ArrayList<Boolean>,Integer>();
    //private HashMap<Integer,int[]> k0SetMap = new HashMap<>();
    //private HashMap<Integer,int[]> k1SetMap = new HashMap<>();
    private final int NUMBER_OF_OBJECTIVES;
    private int[] elementWeights;
    private final int MAX_INDEX;
    private final int MIN_INDEX=0;
    private int lChildrenSet0IndicesLessThanK[][]; // holds array of indices for all indexs where corresonding 0 bits are in same place and index is lower
    private int lChildrenSet1IndicesLessThanL[][]; // holds array of indices for all indexs where corresonding 1 bits are in same place and index is higher
    private int lChildrenSet0Indices[][]; // holds array of indices for all indexs where corresonding 0 bits are in same place and index is lower
    private int lChildrenSet1Indices[][]; // holds array of indices for all indexs where corresonding 1 bits are in same place and index is higher

    private MTQuadTree1(int numberOfObjectives) { 
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
        elementWeights = new int[numberOfObjectives];
        for (int i = 0; i < numberOfObjectives; i++)
            elementWeights[i] = (int) Math.pow(2,i);
        MAX_INDEX = (int) Math.pow(2,numberOfObjectives)-1;
        setUpMatrices();
    }

    private void setUpMatrices() {
        lChildrenSet0IndicesLessThanK = new int[MAX_INDEX][];
        lChildrenSet1IndicesLessThanL = new int[MAX_INDEX][];
        lChildrenSet0Indices = new int[MAX_INDEX][];
        lChildrenSet1Indices = new int[MAX_INDEX][];
        // Set up matrices of mappings
        for (int i = MIN_INDEX+1; i < MAX_INDEX; i++) {
            ArrayList<Integer> tempZeros = new ArrayList<>(); 
            for (int j = MIN_INDEX+1; j < i; j++){
                // use bitwise operators to check if has all zeros in same place;
                if ((~i & ~j) == ~i) {
                    tempZeros.add(j);
                }
            }
            lChildrenSet0IndicesLessThanK[i] = new int[tempZeros.size()];
            for (int j=0; j<tempZeros.size(); j++)
                lChildrenSet0IndicesLessThanK[i][j] = tempZeros.get(j);
            for (int j = i; j < MAX_INDEX; j++){
                // use bitwise operators to check if has all zeros in same place;
                if ((~i & ~j) == ~i) {
                    tempZeros.add(j);
                }
            }
            lChildrenSet0Indices[i] = new int[tempZeros.size()];
            for (int j=0; j<tempZeros.size(); j++)
                lChildrenSet0Indices[i][j] = tempZeros.get(j);    
                
                
            ArrayList<Integer> tempOnes = new ArrayList<>(); 
            for (int j = i + 1; j < MAX_INDEX; j++){
                // use bitwise operators to check if has all ones in same place;
                if ((i & j) == i) {
                    tempOnes.add(j);
                }
            }
            lChildrenSet1IndicesLessThanL[i] = new int[tempOnes.size()];
            for (int j=0; j<tempOnes.size(); j++)
                lChildrenSet1IndicesLessThanL[i][j] = tempOnes.get(j);
            for (int j = MIN_INDEX+1; j < i+1; j++){
                // use bitwise operators to check if has all ones in same place;
                if ((i & j) == i) {
                    tempOnes.add(j);
                }
            }
            lChildrenSet1Indices[i] = new int[tempOnes.size()];
            for (int j=0; j<tempOnes.size(); j++)
                lChildrenSet1Indices[i][j] = tempOnes.get(j);    
                  
        }

        printMatrices();
    }

    private void printMatrices() {
        System.out.println("LchildrenSet0IndicesLessThanK");
        for (int i = MIN_INDEX+1; i < MAX_INDEX; i++) {
            System.out.print("Index: " + i + "---");
            for (int j=0; j<lChildrenSet0IndicesLessThanK[i].length; j++)
                System.out.print(", " + lChildrenSet0IndicesLessThanK[i][j]);
            System.out.println();  
        }

        System.out.println("LchildrenSet1IndicesLessThanL");
        for (int i = MIN_INDEX+1; i < MAX_INDEX; i++) {
            System.out.print("Index: " + i + "---");
            for (int j=0; j<lChildrenSet1IndicesLessThanL[i].length; j++)
                System.out.print(", " + lChildrenSet1IndicesLessThanL[i][j]);
            System.out.println();  
        }
        
        System.out.println("LchildrenSet0Indices");
        for (int i = MIN_INDEX+1; i < MAX_INDEX; i++) {
            System.out.print("Index: " + i + "---");
            for (int j=0; j<lChildrenSet0Indices[i].length; j++)
                System.out.print(", " + lChildrenSet0Indices[i][j]);
            System.out.println();  
        }

        System.out.println("LchildrenSet1Indices");
        for (int i = MIN_INDEX+1; i < MAX_INDEX; i++) {
            System.out.print("Index: " + i + "---");
            for (int j=0; j<lChildrenSet1Indices[i].length; j++)
                System.out.print(", " + lChildrenSet1Indices[i][j]);
            System.out.println();  
        }
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

    /*private int[] getLchildrenIndicesLessthanK(int k) {
    return new int[] {};
    }*/

    @Override
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException{
        if (s.getNumberOfObjectives()!=NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("Manager set up for " + NUMBER_OF_OBJECTIVES 
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
        // calculate k-sucessorship STEP 2 in M&T paper
        int index = s.worseOrEqualIndex(n.getCargo(),elementWeights);
        if (index==MAX_INDEX) // solution dominated so discard
            return false;

        if (index==MIN_INDEX){ // node is dominated, so will have to be removed and  all its children reinserted
            MTQuadTreeNode[] childrenOfDominatedNode = n.getChildren();
            n.setCargoAndCleanChildren(s); // one added and one removed, so no changed of numberOfElements necessary
            System.out.println("Added into dominated slot");
            for (MTQuadTreeNode child : childrenOfDominatedNode){ 
                if (child!=null)
                    recursivelyReInsert(s,child,n,true); // add child (at it's children) from n as parent
            }
            return true;
        }
        //check if dominated by any sucessors STEP 3 in M&T paper
        int[] potentialDominators = getLchildrenSet0IndicesLessThanK(index);
        //System.out.println("index "+index + " "+ s + " "+ n);
        for (int l : potentialDominators) 
            if (n.getChild(l)!=null)
                if (recursiveDominatedCheck(s,n.getChild(l)))
                    return false;

        // check if proposed solution dominates any sucessors, if so delete it and reinsert all its children 
        // STEP 4 in M&T paper
        int[] potentialDominated = getLchildrenSet1IndicesLessThanL(index);
        //System.out.println("index "+index + " "+ s + " "+ n);
        //for (int l : potentialDominated)
        //    System.out.println("query index " + l);
        for (int l : potentialDominated) 
            if (n.getChild(l)!=null)
                recursiveDominatesCheck(s,n,l);

        //check if dominates by sucessor at matching index, and add if necessary
        // STEP 5 in M&T paper
        if (n.getChild(index)==null){
            n.setChild(new MTQuadTreeNode(s,n),index);
            numberOfElements++;
            System.out.println("Added into empty slot");
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
        System.out.println("In recursivelyReInsert, check: "+ checkChildren); 
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
        //if (index==MAX_INDEX) // solution dominated by node n so discard
        //    return;

            
        int[] potentialDominators = getLchildrenSet0IndicesLessThanK(index);
        //System.out.println("index "+index + " "+ s + " "+ n);
        for (int l : potentialDominators) 
            if (n.getChild(l)!=null)
                if (recursiveDominatedCheck(c.getCargo(),n.getChild(l)))
                    return;
    
        //check if dominated by any sucessors
        /*if (n.isAParent())
            if(recursiveDominatedCheck(c.getCargo(),n,index))
                return;
*/
        //check if dominated by sucessor at matching index, and add if necessary
        if (n.getChild(index)==null){
            n.setChild(c,index);
            numberOfElements++;
            return;
        } else { //replace root with k-child, then compare c to this as the new root
            // last argument is flase as at this point all c's orginal children have been stripped off
            // and checked, and the counter alreday updated with c's removal
            recursivelyReInsert(potentialDominator,c, n.getChild(index),false);
            return;
        }
    }

    private int[] getLchildrenSet0IndicesLessThanK(int index)
    {
        return lChildrenSet0IndicesLessThanK[index];
    }

    private int[] getLchildrenSet1IndicesLessThanL(int index)
    {
        return lChildrenSet1IndicesLessThanL[index];
    }

    private int[] getLchildrenSet0Indices(int index)
    {
        return lChildrenSet0Indices[index];
    }

    private int[] getLchildrenSet1Indices(int index)
    {
        return lChildrenSet1Indices[index];
    }

    
    /* returns true if s dominated by any sucessors of n*/
    private boolean recursiveDominatedCheck(Solution s, MTQuadTreeNode n) {
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
        System.out.println("COMPARING " + s + " to " + n.getChild(index).getCargo() + " child " + index);
        int k = s.worseOrEqualIndex(n.getChild(index).getCargo(),elementWeights);
        if (k==0) { // sucessor is dominated by s, so remove and reinsert all children from global root
            MTQuadTreeNode[] childrenOfDominatedNode = n.getChild(index).getChildren();
            n.removeChild(index);
            numberOfElements--;
            System.out.println("REMOVING DOMINATED SOLUTION");
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
        numberOfElements = 0;
    }

    public static ParetoSetManager managerFactory(int numberOfObjectives) {
        return new MTQuadTree1(numberOfObjectives);
    }
}
