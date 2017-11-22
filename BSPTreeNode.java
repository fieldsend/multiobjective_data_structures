import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Collections;
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
    int objectiveIndex;
    private int numberOfObjectives;
    private double theta;
    private int numberCovered;
    private ArrayList<Solution> setCovered;


    BSPTreeNode(ArrayList<Solution> setCovered, int numberOfObjectives, BSPTreeNode parent) {
        this.setCovered = setCovered;
        this.numberOfObjectives = numberOfObjectives;
        this.parent = parent;
        this.numberCovered = setCovered.size();
    }
     
    
    void setParent(BSPTreeNode parent) {
        this.parent = parent;
    }
    
    void setRight(BSPTreeNode right) {
        this.right = right;
    }
    
    BSPTreeNode getLeft() {
        return left;
    }
    
    BSPTreeNode getRight() {
        return right;
    }
    
    BSPTreeNode getParent() {
        return parent;
    }
    
    void setLeft(BSPTreeNode Left) {
        this.left = left;
    }
    
    int getObjectiveIndex() {
        return objectiveIndex;
    }
    
    double getTheta() {
        return theta;
    }
    
    int getNumberCovered() {
        return numberCovered;
    }
    
    ArrayList<Solution> getCoveredSet() {
        return setCovered;
    }
    
    boolean isInteriorNode() {
        return setCovered==null; // interior nodes do not contain any solutions
    }

    void incrementNumberCovered() {
        numberCovered++;
    }

    void decrementNumberCovered() {
        numberCovered--;
    }
    
    void decrementNumberCovered(int k) {
        numberCovered-=k;
    }
    
    void removeAllCovered() {
        numberCovered=0;
    }
    
    BSPTreeNode getChild(Solution s) {
        if (s.getFitness(objectiveIndex) < theta)
            return left;
        return right;
    }

    void addToSet(Solution s, int maxLeafSize) {
        setCovered.add(s);
        numberCovered++;
        if (setCovered.size() > maxLeafSize) {
            setAppropriateThetaAndIndex(maxLeafSize);
            ArrayList<Solution> leftSet = new ArrayList<>(maxLeafSize+1);
            ArrayList<Solution> rightSet = new ArrayList<>(maxLeafSize+1);
            for (Solution member : setCovered) {
                if (member.getFitness(objectiveIndex) < theta)
                    leftSet.add(member);
                else
                    rightSet.add(member);    
            }
            left = new BSPTreeNode(leftSet,numberOfObjectives,this);
            right = new BSPTreeNode(rightSet,numberOfObjectives,this);
            setCovered = null; // detach previous covered set for cleaning
        }
        return;
    }

    private void setAppropriateThetaAndIndex(int maxLeafSize) {
        // first identify which objectives vary
        if (maxLeafSize>1) {
            int tempDist= -1;
            ArrayList<Integer> validObjectives = new ArrayList<>(numberOfObjectives);
            double[] v = setCovered.get(0).getFitness();
                
            loop1 : for (int i=0; i < numberOfObjectives; i++){
                for (int j=1; j<setCovered.size(); j++) {
                    if (v[i] != setCovered.get(j).getFitness(i)){
                        validObjectives.add(i);
                        continue loop1;
                    }
                }
            }
            for (Integer j : validObjectives) { // get closest distance to parent with each index 
                int distance = trackBackClosestIndex(j,1);
                if (distance > tempDist) {
                    tempDist = distance;
                    objectiveIndex = j;
                }
            }
        } else { //special case when only one solution max in each leaf
          int tempDist = -1;
          for (int i=0; i < numberOfObjectives; i++) { // get closest distance to parent with each index 
                int distance = trackBackClosestIndex(i,1);
                if (distance > tempDist) {
                    tempDist = distance;
                    objectiveIndex = i;
                }
          } 
        }
        // Set theta
        boolean moreThanOneValueInLeft = false;
        boolean moreThanOneValueInRight = false;
        PriorityQueue<Double> leftQueue = new PriorityQueue<>(maxLeafSize, Collections.reverseOrder()); // top of queue is maximimum element
        PriorityQueue<Double> rightQueue = new PriorityQueue<>(maxLeafSize); // top of queue is minimum element
        
        // split data to find median, but ensure that there are two different values at the top of the left and bottom of the right
        // to ensure data is partitioned
        double leftQueueMin = Integer.MAX_VALUE;
        for (Solution member : setCovered) {
            // if two queues are equal size 
            if (leftQueue.size() == rightQueue.size()) {
                if ((rightQueue.peek() != null) && member.getFitness(objectiveIndex) > rightQueue.peek()) { // if value larger than minimim in right
                    leftQueue.offer(rightQueue.poll());
                    rightQueue.offer(member.getFitness(objectiveIndex)); // max queue 1 longer, min queue same size as polled prev line
                } else {
                    leftQueue.offer(member.getFitness(objectiveIndex)); // max queue 1 longer
                    if (member.getFitness(objectiveIndex) < leftQueueMin)
                        leftQueueMin = member.getFitness(objectiveIndex);
                }
            } else { // right queue one less member than left queue
                if(member.getFitness(objectiveIndex) < leftQueue.peek()) { // if value smaller than maximum in left
                    rightQueue.offer(leftQueue.poll());
                    leftQueue.offer(member.getFitness(objectiveIndex)); // min queue 1 londer, max queue same size as polled prev line
                    if (member.getFitness(objectiveIndex) < leftQueueMin)
                        leftQueueMin = member.getFitness(objectiveIndex);
                } else {
                    rightQueue.offer(member.getFitness(objectiveIndex)); // min queue 1 longer
                }
            }
        }
        double topOfLeft = leftQueue.peek();
        double bottomOfRight = rightQueue.peek();
        if (topOfLeft == bottomOfRight) {
            //need to make sure left queue isn't completely full of same value, given to ends of queues are same!
            if (leftQueueMin==topOfLeft) { // all of left queue holds the same value, which is also minimum of right queue
                do {
                    topOfLeft = rightQueue.poll();
                    bottomOfRight = rightQueue.peek();
                } while (topOfLeft == bottomOfRight);
            }
        }
        theta = (topOfLeft + bottomOfRight)/2.0;
    }

    int trackBackClosestIndex(int j,int number) {
        if (parent==null)
            return Integer.MAX_VALUE;
        if (parent.objectiveIndex==j)
            return number;
        return parent.trackBackClosestIndex(j, number++);
    }
}
