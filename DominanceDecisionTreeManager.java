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
    private int size = 0;
    DominanceDecisionTreeManager(int numberOfObjectives) {
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
    }

    @Override
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (s.getNumberOfObjectives()!=NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("Manager set up for " + NUMBER_OF_OBJECTIVES 
                + " objectives, however solution added has "+ s.getNumberOfObjectives());

        if (root==null) {
            root = new DDTNode(s,null,-1);
            size = 1;
        } else {
            if (detectDomination(root,s))
                return false;
            // not dominated, so need to remove any and all current members than are dominated
            deleteDominated(root,s);
            if (size>0) 
                insert(root,new DDTNode(s));
            else // special case when entire tree has been removed
                root = new DDTNode(s,null,-1);
            size++;
        }
        return true;
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
        // all children of node have been processed by this point, and any dominated removed
        if (s.weaklyDominates(node.getCargo())) {
            if (node.isLeaf()) {
                node.detach();
                size--;
                System.out.println("detaching leaf");
                return;
            }
            int j=0;
            // find first valid offspring of node
            for (; j<NUMBER_OF_OBJECTIVES; j++) 
                if (children[j] != null)
                    break;
            DDTNode parent = node.getParent();        
            size--;

            if (parent==null) {// special case when deleted node is top of tree
                System.out.println("DELETING root");
                root = children[j];
                root.setParent(null,-1);
                parent = root;
            } else 
                parent.setChild(j,children[j]);
            
            for (j++; j<NUMBER_OF_OBJECTIVES; j++)
                if (children[j] != null)
                    treeInsert(parent,children[j]); //resinsert node

            //             if (node.detachAndReplace(children[j])) { // detached not is not at top of tree
            //                 for (j++; j<NUMBER_OF_OBJECTIVES; j++)
            //                     if (children[j] != null)
            //                         treeInsert(parent,children[j]); //resinsert node  
            //             } else { // detached node is top of tree, so need to be careful on reinsertion 
            //                 boolean rootReset = false;
            //                 for (j++; j<NUMBER_OF_OBJECTIVES; j++) {
            //                     if (children[j] != null) {
            //                         if (rootReset)
            //                             treeInsert(parent,children[j]); //resinsert node 
            //                         else {
            //                             root = children[j];
            //                             rootReset=true;
            //                         }
            //                     }
            //                 }
            //             }
        }
    }

    private void treeInsert(DDTNode baseParent, DDTNode node) {
        node.detach();
        DDTNode[] children = node.getChildren();    
        for (DDTNode child : children) //strip and process children
            if (child!=null)
                treeInsert(baseParent,child);
        insert(baseParent,node);
    }

    private void insert(DDTNode baseParent, DDTNode node) {
        int i=0;
        for (; i<NUMBER_OF_OBJECTIVES; i++ ) 
            if (node.getCargo().getFitness(i) > baseParent.getCargo().getFitness(i))
                break;
        if (baseParent.getChild(i)!=null)        
            insert(baseParent.getChild(i),node);
        else {
            baseParent.setChild(i,node);
            node.setParent(baseParent,i);
        }

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
        ArrayList<Solution> contents = new ArrayList<>();
        recursivelyExtract(root,contents);
        return contents;
    }

    private void recursivelyExtract(DDTNode node, ArrayList<Solution> contents) {
        contents.add(node.getCargo());
        for (DDTNode child : node.getChildren())
            if (child != null)
                recursivelyExtract(child, contents);
    }

    public static DominanceDecisionTreeManager managerFactory(int numberOfObjectives) {
        return new DominanceDecisionTreeManager(numberOfObjectives);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clean() {
        root = null;
        size = 0;
    }
}
