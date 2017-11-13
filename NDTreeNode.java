
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

/**
 * Write a description of class NDTreeNode here.
 * 
 * @author Jonathan Fieldsend
 * @version 1
 */
public class NDTreeNode
{
    private List<Solution> list;
    private Solution idealPointEstimate;
    private Solution nadirPointEstimate;
    private double[] midpoint;
    private NDTreeNode[] children;
    private NDTreeNode parent;
    private static final int MAX_LIST_SIZE = 20;
    private static int NUMBER_OF_CHILDREN;
    
    NDTreeNode(int maxListSize, int numberOfChildren) {
        MAX_LIST_SIZE = maxListSize;
        NUMBER_OF_CHILDREN = numberOfChildren;
        list = new Vector<Solution>(MAX_LIST_SIZE+1);
    }
    
    void add(Solution solution){
        list.add(solution);
        if (list.size()==1)
            setIdealNadir(solution);
        else
            updateIdealNadir(solution);
    }
      
    private void setIdealNadir(Solution solution){
        idealPointEstimate = new ProxySolution(solution.getFitness());
        nadirPointEstimate = new ProxySolution(solution.getFitness());
        midpoint = new double[idealPointEstimate.getNumberOfObjectives()];
        for (int i = 0; i< midpoint.length; i++)
            midpoint[i] = idealPointEstimate.getFitness(i) + (nadirPointEstimate.getFitness(i)-idealPointEstimate.getFitness(i))/2;
    }
    
    private void updateIdealNadir(Solution solution){
        for (int i = 0; i < solution.getNumberOfObjectives(); i++){
            if (solution.getFitness(i) < idealPointEstimate.getFitness(i)){
                idealPointEstimate.setFitness(i, solution.getFitness(i));
                midpoint[i] = idealPointEstimate.getFitness(i) + (nadirPointEstimate.getFitness(i)-idealPointEstimate.getFitness(i))/2;
            } else if (solution.getFitness(i) > nadirPointEstimate.getFitness(i)){
                nadirPointEstimate.setFitness(i, solution.getFitness(i));
                midpoint[i] = idealPointEstimate.getFitness(i) + (nadirPointEstimate.getFitness(i)-idealPointEstimate.getFitness(i))/2;
            }
        }
        if (parent!=null) // got back up tree
            parent.updateIdealNadir(solution);
    }
    
    boolean isEmpty(){
        return list.isEmpty();
    }
    
    boolean isLeaf() {
        return children == null;
    }
    
    boolean isRoot() {
        return parent == null;
    }
    
    /**
     * returns false if solution dominates, else return true
     */
    
    boolean updateNode(Solution solution){
        if (nadirPointEstimate.weaklyDominates(solution))
            return false;
        if (solution.weaklyDominates(idealPointEstimate)){
            parent.removeChild(this); // detach this node and all subcomponents from tree
            return true;
        }
        if(idealPointEstimate.weaklyDominates(solution) || solution.weaklyDominates(nadirPointEstimate)){ // short-circuit or
            if (this.isLeaf()) {
                Iterator<Solution> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Solution member = iterator.next();
                    if (member.weaklyDominates(solution))
                        return false;
                    if (solution.weaklyDominates(member))
                        iterator.remove(); // existing member dominated, so remove
                }
            } else {
                for (NDTreeNode n : children) {
                    if (n.updateNode(solution) == false)
                        return false; // if it is dominatated further down tree, return false and stop processing further
                    if (n.isEmpty()) {
                        removeChild(n); // detach this node and all subcomponents from tree
                    }
                }
            }
        }
        return true; // not dominated by any solution ith node
    }    
    
    void removeChild(NDTreeNode childToRemove){
        
        for (int i=0; i<children.length; i++) {
            if(children[i]==childToRemove) {
                children[i]=null; //detach for garbage collection
                break;
            }
        }
                
    }
    
    void insert(Solution solution) {
        if  (this.isLeaf()) {
            list.add(solution);
            this.updateIdealNadir(solution);
            if (list.size() > NDTreeNode.MAX_LIST_SIZE)
                this.split();
        } else {
            NDTreeNode closest = getClosestChild(solution);
            closest.insert(solution);
        }
    }
    
    void split() {
        // find solution with highest average distance to all other solutions
        // highest average distance is equivalent to highest total distance -- no need for division
        double[][] distanceMatrix = new double[list.size()][list.size()];
        double[] distances = new double[list.size()];
        int indicesOfFirstChildren = new int[list.size()]; // for efficency will track first individuals in each child node 
        for (int i=0; i<list.size(); i++) {
            distances[i]=0.0;
            for (int j=0; j<list.size(); j++) {
                if (i!=j){ // no need to calcultae on diagonal as distance is zero
                    distanceMatrix[i][j]= squaredDistance(list.get(i).getFitness(),list.get(j).getFitness());
                    distances[i]+=distance_matrix[i][j];
                } else {
                    distanceMatrix[i][j] =0.0;
                }
            }
        }
        // get first child node
        int indexOfMostDistantChild = 0;
        int maxDistance = distances[0];
        for (int i=1; i<list.size(); i++) {
            if (distances[i]>maxDistance){
                maxDistance = distances[i];
                indexOfMostDistantChild = i;
            }
        }
        children = new NDTreeNode[NUMBER_OF_CHILDREN];
        children[0] = new NDTreeNode(MAX_LIST_SIZE,NUMBER_OF_CHILDREN);
        chidren[0].add(list.get(indexOfMostDistantChild));
        indicesOfFirstChildren[0] = indexOfMostDistantChild;
        // fill up remaining child nodes
        
        // first put one child in each subnode, based on max distrance from existing subnodes
        for (int i=1; i< NUMBER_OF_CHILDREN; i++) { // for the total number of children to make
            maxDistance = -1.0;
            indexOfMostDistantChild = -1.0;
            for (int k=0; k<list.size(); k++) {
                int distanceAccumulator = 0.0;
                for (int j = 1; j<i; j++) { // go through child nodes allready initialised
                     // find solution furtherest from current child nodes to make next node
                     distanceAccumulator += distanceMatrix[i][indicesOfFirstChildren[k]];
                }
                if (distanceAccumulator > maxDistance) {
                    maxDistance = distanceAccumulator;
                    indexOfMostDistantChild = k;
                }
            }
            indicesOfFirstChildren[i] = indexOfMostDistantChild;
            children[i] = new NDTreeNode(MAX_LIST_SIZE,NUMBER_OF_CHILDREN);
            chidren[i].add(list.get(indexOfMostDistantChild));
        }
        // now empty remaining list members
        for (
        // detach previous list for garbage collection, as list members no all transferred to containers in children
        list = null;
    }
    
    NDTreeNode getClosestChild(Solution solution){
        double distance = NDTreeNode.squaredDistance(children[0].midpoint,solution.getFitness());
        int closestChild = 0;
        for (int i=1; i<children.length; i++) {
            double alternativeDistance = NDTreeNode.squaredDistance(children[i].midpoint,solution.getFitness());
            if (alternativeDistance < distance) {
                closestChild = i;
                distance = alternativeDistance;
            }
        }
        return children[closestChild];
    }
    
    private static double squaredDistance(double[] a, double[] b) {
        double distance = 0.0;
        for (int i=0; i<a.length; i++)
            distance += Math.pow(a[i]-b[i],2);
        return distance;
    }
    
}
