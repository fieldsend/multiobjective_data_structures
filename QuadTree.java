
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
/**
 * QuadTree.
 * 
 * @author Jonathan Fieldsend 
 * @version 1.0
 */
public class QuadTree
{
    private DominationTreeNode root = null;
    private int numberOfElements = 0;
    private HashMap<ArrayList<Boolean>,Integer> kSucessorMap = new HashMap<ArrayList<Boolean>,Integer>();
    public QuadTree() { }

    public boolean add(Solution s) {
        if (root == null){
            root = new DominationTreeNode(s,null);
            numberOfElements++;
            this.setUpMap(s.getNumberOfObjectives()); // set up indexing map on first use
            return true;
        } else {
            return false;
        }
    }
     
    void setUpMap(int m) {
        // go through all permutations of objective comparisons to propogate
        // map between boolean arrays of quality comparison, and k-successorship
        
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
    }
    
    public Set<Solution> getContents() {
        Set s = new HashSet<Solution>(numberOfElements);
        if (root != null)
            this.recursivelyExtract(s,root);
        return s;
    }
    
    private void recursivelyExtract(Set<Solution> s, DominationTreeNode node) {
        s.add(node.getCargo());
        for (DominationTreeNode child: node.getChildren())
            this.recursivelyExtract(s, child);
        return;    
    }
    
    public Solution getRandomMember() {
        Solution s = null;
        
        return s;
    }
    
    
    public int size() {
        return numberOfElements;
    }
    
    public void clean() {
        root = null;
    }
}
