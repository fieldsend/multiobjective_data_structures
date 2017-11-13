
/**
 * Write a description of class MTQuadTreeNode here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MTQuadTreeNode
{
    private MTQuadTreeNode[] children;
    private MTQuadTreeNode parent;
    private Solution cargo;
    
    MTQuadTreeNode(Solution cargo, MTQuadTreeNode parent) {
        this.parent = parent;
        this.cargo = cargo;
        children = new MTQuadTreeNode[(int) Math.pow(2,cargo.getNumberOfObjectives())];
    }
    
    Solution getCargo() {
        return cargo;
    }
    
    void setCargo(Solution cargo) {
        this.cargo=cargo;
    }
    
    void setCargoAndCleanChildren(Solution cargo) {
        this.cargo = cargo;
        children = new MTQuadTreeNode[(int) Math.pow(2,cargo.getNumberOfObjectives())];
    }
    
    MTQuadTreeNode[] getChildren() {
        return children;
    }
    
    void setChildren(MTQuadTreeNode[] children) {
        this.children=children;
    }
}
