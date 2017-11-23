import java.util.ArrayList;
import java.util.Collection;

/**
 * Write a description of class DominanceDecisionTreeManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DominanceDecisionTreeManager implements ParetoSetManager
{
    private DDTNode root;
    public final int NUMBER_OF_OBJECTIVES; // number of objectives of solutions being managed
    
    DominanceDecisionTreeManager(int numberOfObjectives) {
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
    }
    
    @Override
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (s.getNumberOfObjectives()!=NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("Manager set up for " + NUMBER_OF_OBJECTIVES 
                + " objectives, however solution added has "+ s.getNumberOfObjectives());
        
        if (root==null) 
            root = new DDTNode(s,null,-1);
        else {
            if (detectDomination(root,s))
                return false;
            // not dominated, so need to remove any and all current members than are dominated
            deleteDominated(root,s);
        }
    }
    
    private boolean detectDomination(DDTNode node,Solution s) {
        if (node.getCargo().weaklyDominates(s))
            return true;
        DDTNode[] children = node.getChildren();    
        for (int i=0; i<NUMBER_OF_OBJECTIVES; i++ ) 
            if (children[i] != null)
                if (node.getCargo().getFitness(i)<=s.getFitness(i))
                    return detectDomination(children[i],s);
            
        return false;
    }
    
    private void deleteDominated(DDTNode node,Solution s) {
        
        DDTNode[] children = node.getChildren();    
        for (int i=0; i<NUMBER_OF_OBJECTIVES; i++ ) {
            if (children[i] != null)
                deleteDominated(children[i],s);
            if (s.getFitness(i)>node.getCargo().getFitness(i))
                break;
        }
        if (s.weaklyDominates(node.getCargo())) {
            if (node.isLeaf()) {
                node.delete();
                return;
            }
            int j=0;
            for (; j<NUMBER_OF_OBJECTIVES; j++)
                if (children[j] != null)
                    break;
            DDTNode parent = node.getParent;        
            node.deleteAndReplace(children[j]);  
            for (j++; j<NUMBER_OF_OBJECTIVES; j++)
                if (children[j] != null)
                    treeInsert(parent,children[j]); //resinsert node    
        }
    }
    
    void treeInsert(DDTnode baseParent, DDTnode subtree) {
        
    }
    
    @Override
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (s.getNumberOfObjectives()!=NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("Manager set up for " + NUMBER_OF_OBJECTIVES 
                + " objectives, however solution added has "+ s.getNumberOfObjectives());
        
        if (root==null) 
            return false;
        else {
            return detectDomination(root,s);
        }
    }
    
    @Override
    public Collection<? extends Solution> getContents() {
        return new ArrayList<Solution>();
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public void clean() {
        
    }
}
