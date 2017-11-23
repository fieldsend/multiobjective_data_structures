
/**
 * Write a description of class DDTNode here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
class DDTNode
{
    private DDTNode parent;
    private DDTNode[] children;
    private Solution cargo;
    private int indexAtParent;
    
    DDTNode(Solution cargo, DDTNode parent, int indexAtParent) {
        this.cargo = cargo;
        children = new DDTNode[cargo.getNumberOfObjectives()];
        this.indexAtParent = indexAtParent;
    }
    
    DDTNode[] getChildren() {
        return children;
    }
    
    DDTNode getChild(int index) {
        return children[index];
    }
    
    void setChild(int index, DDTNode node) {
        children[index] = node;
    }
    
    Solution getCargo(){
        return cargo;
    }
    
    void delete() {
        parent.children[indexAtParent] = null;
    }
    
    void deleteAndReplace(DDTNode replacement) {
        parent.children[indexAtParent] = replacement;
    }
    
    // can do this more efficiently at a memory and tracking cost
    boolean isLeaf() {
        for (DDTNode node : children)
            if (node!=null)
                return false;
        return true;        
    }
}
