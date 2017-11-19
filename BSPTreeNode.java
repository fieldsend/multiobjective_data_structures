import java.util.ArrayList;

/**
 * Write a description of class BSPTreeNode here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BSPTreeNode
{
    private Solution cargo;
    private BSPTreeNode left;
    private BSPTreeNode right;
    private BSPTreeNode parent;
    private int objectiveIndex;
    private double theta;
    private int numberCovered;
    private ArrayList<Solution> setCovered;
    
    BSPTreeNode(ArrayList<Solution> setCovered) {
        this.setCovered = setCovered;
    }
    
    int getNumberCovered() {
        return numberCovered;
    }
    
    boolean isInteriorNode() {
        return setCovered==null; // interior nodes do not contain any solutions
    }
    
    void incrementNumberCovered() {
        numberCovered++;
    }
    
    BSPTreeNode getChild(Solution s) {
        if (s.getFitness(objectiveIndex) < theta)
            return left;
        return right;
    }
    
    void addToSet(Solution s, int maxLeafSize) {
        setCovered.add(s);
        if (setCovered.size() > maxLeafSize) {
            setAppropriateThetaAndIndex();
            this.setCovered = null;
            ArrayList<Solution> leftSet = new ArrayList(maxLeafSize+1);
            ArrayList<Solution> rightSet = new ArrayList(maxLeafSize+1);
            for (Solution member : setCovered) {
                if (member.getFitness(objectiveIndex) < theta)
                    leftSet.add(member);
                else
                    rightSet.add(member);    
            }
            left = new BSPTreeNode(leftSet);
            right = new BSPTreeNode(rightSet);
            setCovered = null; // detach previous covered set for cleaning
        }
        return;
    }
    
    void setAppropriateThetaAndIndex() {
        // TODO
    }
}
