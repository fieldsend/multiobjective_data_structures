
/**
 * NDTree, implementation of ND tree by Jaszkiewicz and Lust.
 * 
 * @author Jonathan Fieldsend
 * @version 1
 */
public class NDTree
{
    private NDTreeNode root = new NDTreeNode();
     
    
    NDTree() {
        
    }
    
    public void update(Solution solution){
        if (root.isEmpty()) {
            root.add(solution) ;
        } else {
            if (root.updateNode(solution)) { // returns true if solution not covered by any member in tree
                root.insert(solution);
            }
        }
    }
}
