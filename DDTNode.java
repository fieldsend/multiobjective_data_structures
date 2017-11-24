
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
        this(cargo);
        setParent(parent, indexAtParent);
    }
    
    DDTNode(Solution cargo) {
        this.cargo = cargo;
        children = new DDTNode[cargo.getNumberOfObjectives()];
    }
    
    void setParent(DDTNode parent, int indexAtParent) {
        this.parent = parent;
        this.indexAtParent = indexAtParent;
    }
    
    DDTNode[] getChildren() {
        return children;
    }
    
    DDTNode getChild(int index) {
        return children[index];
    }
    
    DDTNode getParent() {
        return parent;
    }
    
    void setChild(int index, DDTNode node) {
        children[index] = node;
    }
    
    Solution getCargo(){
        return cargo;
    }
    
    void detach() {
        if (parent!=null)
            parent.children[indexAtParent] = null;
    }
    
    /**
     * Returns true if detached node is not top of tree (has a parent)
     * Otherwise returns false
     */
    boolean detachAndReplace(DDTNode replacement) {
        if (parent!=null) {
            parent.children[indexAtParent] = replacement;
            return true;
        }
        return false;
    }
    
    // can do this more efficiently at a memory and tracking cost
    boolean isLeaf() {
        for (DDTNode node : children)
            if (node!=null)
                return false;
        return true;        
    }
}
