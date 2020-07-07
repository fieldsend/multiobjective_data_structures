package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;


/**
 * MTQuadTreeNode defines nodes used by the quad tree implementations.
 * 
 * @author Jonathan Fieldsend
 * @version 1.0
 */
public class MTQuadTreeNode
{
    private MTQuadTreeNode[] children;
    private MTQuadTreeNode parent;
    private Solution cargo;
    private int numberOfChildren = 0; // standard default, but I like to be explicit!
    
    MTQuadTreeNode(Solution cargo, MTQuadTreeNode parent) {
        this.parent = parent;
        this.cargo = cargo;
        children = new MTQuadTreeNode[(int) Math.pow(2,cargo.getNumberOfObjectives())];
    }
    
    boolean isAParent() {
        return numberOfChildren > 0;
    }
    
    Solution getCargo() {
        return cargo;
    }
    
    void setCargo(Solution cargo) {
        this.cargo=cargo;
    }
    
    void setCargoAndCleanChildren(Solution cargo) {
        this.cargo = cargo;
        cleanChildren();
    }
    
    void cleanChildren() {
        children = new MTQuadTreeNode[(int) Math.pow(2,cargo.getNumberOfObjectives())];
        numberOfChildren = 0;
    }
    
    MTQuadTreeNode getChild(int index) {
        return children[index];
    }
    
    MTQuadTreeNode[] getChildren() {
        return children;
    }
    
    /*
     * do not pass null in -- use removeChild
     */
    void setChild(MTQuadTreeNode node, int index) {
        if (this.children[index]==null)
            numberOfChildren++;
        this.children[index] = node;
    }
    
    void removeChild(int index) {
        this.children[index] = null;
        numberOfChildren--;
    }
}

