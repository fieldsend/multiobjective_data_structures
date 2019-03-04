package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;


import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
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
    private double[] idealPointEstimate;
    private double[] nadirPointEstimate;
    private double[] midpoint;
    private List<NDTreeNode> children;
    private NDTreeNode parent;
    private static int MAX_LIST_SIZE;
    private static int NUMBER_OF_CHILDREN;
    
    NDTreeNode(int maxListSize, int numberOfChildren) {
        if (maxListSize < numberOfChildren){
            System.out.println("Maximum list size must be at least as big as the number of children");
            numberOfChildren = maxListSize;
        }
        MAX_LIST_SIZE = maxListSize;
        NUMBER_OF_CHILDREN = numberOfChildren;
        list = new ArrayList<Solution>(MAX_LIST_SIZE+1);
    }
    
    private NDTreeNode(int maxListSize, int numberOfChildren, NDTreeNode parent) {
        this(maxListSize, numberOfChildren);
        this.parent = parent;
    }
    
    void add(Solution solution){
        list.add(solution);
        if (list.size()==1)
            setIdealNadir(solution);
        else
            updateIdealNadir(solution);
    }
    
    
    private String stringConvert(double[] x){
        String s = "";
        if (x==null)
            return s;
        for (double d : x)
            s += (d +", ");
        return s;
    }
    
    String idealAsString() {
        return stringConvert(idealPointEstimate);
    }
    
    String nadirAsString() {
        return stringConvert(nadirPointEstimate);
    }
    
    String midpointAsString() {
        return stringConvert(midpoint);
    }
    
    private void setIdealNadir(Solution solution){
        idealPointEstimate = new double[solution.getNumberOfObjectives()];
        nadirPointEstimate = new double[solution.getNumberOfObjectives()];
        midpoint = new double[solution.getNumberOfObjectives()];
        for (int i = 0; i< midpoint.length; i++) {
            idealPointEstimate[i] = solution.getFitness(i);
            nadirPointEstimate[i] = idealPointEstimate[i];
            midpoint[i] = idealPointEstimate[i];
        }
    }
    
    private void updateIdealNadir(Solution solution){
        for (int i = 0; i < solution.getNumberOfObjectives(); i++){
            if (solution.getFitness(i) < idealPointEstimate[i]){
                idealPointEstimate[i] = solution.getFitness(i);
                midpoint[i] = idealPointEstimate[i] + (nadirPointEstimate[i]-idealPointEstimate[i])/2;
            } else if (solution.getFitness(i) > nadirPointEstimate[i]){
                nadirPointEstimate[i] = solution.getFitness(i);
                midpoint[i] = idealPointEstimate[i] + (nadirPointEstimate[i]-idealPointEstimate[i])/2;
            }
        }
        if (parent != null) // got back up tree
            parent.updateIdealNadir(solution);
    }
    
    boolean isEmpty(){
        if (list == null)
            return false; // if list is null, then is an internal node with children
        return list.isEmpty(); 
    }
    
    boolean isLeaf() {
        return children == null;
    }
    
    boolean isRoot() {
        return parent == null;
    }
    
    /**
     * Checks if a solution is weakly-dominated by the archive -- added functionality to meet 
     * requirements of archive management interface
     * 
     */
    boolean weaklyDominates(Solution solution) {
        if (Solution.weaklyDominates(nadirPointEstimate,solution))
            return true;
        if (Solution.weaklyDominates(solution,idealPointEstimate)){
            return false;
        }
        if (Solution.weaklyDominates(idealPointEstimate,solution) || Solution.weaklyDominates(solution,nadirPointEstimate)){ // short-circuit or
            if (this.isLeaf()) {
                Iterator<Solution> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Solution member = iterator.next();
                    if (member.weaklyDominates(solution))
                        return true;
                    if (solution.weaklyDominates(member))
                        return false; // existing member dominated, so archive does not dominate
                }
            } else 
                for (NDTreeNode n : children) 
                    if (n.weaklyDominates(solution) == true)
                        return true; // if it is dominatated further down tree, return false and stop processing further
        }
        return false; // not dominated by any solution ith node
    }
    
    /**
     * returns false if solution is dominated, else removed all dominated solutions and 
     * returns true -- does not insert the argument though!
     */
    
    boolean updateNode(Solution solution, ListIterator<NDTreeNode> iteratorAbove){
        /*System.out.println("Ideal: " + idealAsString());
        System.out.println("Nadir: " + nadirAsString());
        System.out.println("Midpoint: " + midpointAsString());
        if (this.isLeaf())
            System.out.println("Leaf node size: " + list.size());
        else
            System.out.println("Interior node");
        */
        if (Solution.weaklyDominates(nadirPointEstimate,solution)){
            //System.out.println("Nadir dominates -- reject");
            return false;
        }
        if (Solution.weaklyDominates(solution,idealPointEstimate)){
            //System.out.println("Ideal dominated -- accept");
            if (parent != null)
                iteratorAbove.remove(); // detach this node and all subcomponents from tree
            else 
                list = new ArrayList<Solution>(MAX_LIST_SIZE+1); // clean this root node
            return true;
        }
        if (Solution.weaklyDominates(idealPointEstimate,solution) || Solution.weaklyDominates(solution,nadirPointEstimate)){ // short-circuit or
            //System.out.println("Lies inside hyper-rectangle of node, so checking relationship with composite nodes/designs");
            if (this.isLeaf()) {
                //System.out.println("Node is a leaf, so check against all designs");
                Iterator<Solution> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Solution member = iterator.next();
                    if (member.weaklyDominates(solution))
                        return false;
                    if (solution.weaklyDominates(member))
                        iterator.remove(); // existing member dominated, so remove
                }
            } else {
                //System.out.println("Node is interior, so check against hyper-rectangles of children");
                ListIterator<NDTreeNode> iter = children.listIterator();
                while(iter.hasNext()){// number may change in place if dominated, need to cope with concurrent update
                    NDTreeNode n = iter.next();
                    if (n.updateNode(solution,iter) == false)
                        return false; // if it is dominatated further down tree, return false and stop processing further
                    if (n.isEmpty()) {
                        //System.out.println("Remove empty node");
                        iter.remove(); // detach this node and all subcomponents from tree
                    }
                }
                
                
                /*for (NDTreeNode n : children) { // number may change in place if dominated, need to cope with concurrent update
                    if (n.updateNode(solution) == false)
                        return false; // if it is dominatated further down tree, return false and stop processing further
                    if (n.isEmpty()) {
                        System.out.println("Remove empty node");
                        children.remove(n); // detach this node and all subcomponents from tree
                    }
                }*/
                if (children.size()==1) { // replace current node state with child state, and detatch remaining child for gargage collection
                    //System.out.println("Replacing current state with remaining child state");
                    NDTreeNode child = children.get(0);
                    this.list = child.list;
                    this.idealPointEstimate = child.idealPointEstimate;
                    this.nadirPointEstimate = child.nadirPointEstimate;
                    this.midpoint = child.midpoint;
                    children = null;
                }
            }
        }
        //System.out.println("Lies outside hyper-rectangle of node, so accept");
        return true; // not dominated by any solution ith node
    }    
    
    //private void removeChild(NDTreeNode childToRemove){
    //    children.remove(childToRemove);
    //}
    
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
    
    private void split() {
        //System.out.println("Splitting node...");
        // find solution with highest average distance to all other solutions
        // highest average distance is equivalent to highest total distance -- no need for division
        double[][] distanceMatrix = new double[list.size()][list.size()];
        double[] distances = new double[list.size()];
        int[] indicesOfFirstChildren = new int[list.size()]; // for efficency will track first individuals in each child node 
        boolean[] added = new boolean[list.size()];
        for (int i=0; i<list.size(); i++) {
            distances[i]=0.0;
            added[i] = false;
            for (int j=0; j<list.size(); j++) {
                if (i!=j){ // no need to calcultae on diagonal as distance is zero
                    distanceMatrix[i][j] = squaredDistance(list.get(i),list.get(j));
                    distances[i] += distanceMatrix[i][j];
                } else {
                    distanceMatrix[i][j] = 0.0;
                }
            }
        }
        // get first child node
        int indexOfMostDistantChild = 0;
        double maxDistance = distances[0];
        for (int i=1; i<list.size(); i++) {
            if (distances[i] > maxDistance){
                maxDistance = distances[i];
                indexOfMostDistantChild = i;
            }
        }
        children = new ArrayList<>(NUMBER_OF_CHILDREN);
        NDTreeNode child = new NDTreeNode(MAX_LIST_SIZE,NUMBER_OF_CHILDREN,this);
        children.add(child);
        child.add(list.get(indexOfMostDistantChild));
        //children[0] = new NDTreeNode(MAX_LIST_SIZE,NUMBER_OF_CHILDREN,this);
        //children[0].add(list.get(indexOfMostDistantChild));
        indicesOfFirstChildren[0] = indexOfMostDistantChild;
        added[indexOfMostDistantChild] = true;
        // fill up remaining child nodes
        
        // first put one child in each subnode, based on max distance from existing subnodes
        for (int i=1; i< NUMBER_OF_CHILDREN; i++) { // for the total number of children to make
            maxDistance = -1.0;
            indexOfMostDistantChild = -1;
            for (int k=0; k<list.size(); k++) {
                if (added[k]==false) { // only check those not yet added
                    double distanceAccumulator = 0.0;
                    for (int j = 0; j<i; j++) { // go through child nodes already initialised
                        // find solution furtherest from current child nodes to make next node
                        distanceAccumulator += distanceMatrix[k][indicesOfFirstChildren[j]];
                    }
                    if (distanceAccumulator > maxDistance) {
                        maxDistance = distanceAccumulator;
                        indexOfMostDistantChild = k;
                    }
                 }
            }
            indicesOfFirstChildren[i] = indexOfMostDistantChild;
            child = new NDTreeNode(MAX_LIST_SIZE,NUMBER_OF_CHILDREN,this);
            children.add(child);
            child.add(list.get(indexOfMostDistantChild));
            //children[i] = new NDTreeNode(MAX_LIST_SIZE,NUMBER_OF_CHILDREN,this);
            //children[i].add(list.get(indexOfMostDistantChild));
            added[indexOfMostDistantChild] = true;
        }
        // now empty remaining list members into closest children
        for (int i=0; i<list.size(); i++) {
            if (!added[i]) {
                //System.out.println("emptying remaining list element " + i);
                NDTreeNode closestChild  = getClosestChild(list.get(i));
                closestChild.insert(list.get(i));
            }
        }
        // detach previous list for garbage collection, as list members now all transferred to containers in children
        list = null;
    }
    
    private NDTreeNode getClosestChild(Solution solution){
        double distance = NDTreeNode.squaredDistance(children.get(0).midpoint,solution);
        int closestChild = 0;
        for (int i=1; i<children.size(); i++) {
            double alternativeDistance = NDTreeNode.squaredDistance(children.get(i).midpoint,solution);
            if (alternativeDistance < distance) {
                closestChild = i;
                distance = alternativeDistance;
            }
        }
        return children.get(closestChild);
    }
    
    private static double squaredDistance(Solution a, Solution b) {
        double distance = 0.0;
        for (int i=0; i<a.getNumberOfObjectives(); i++)
            distance += Math.pow(a.getFitness(i)-b.getFitness(i),2);
        return distance;
    }
    
    
    private static double squaredDistance(double[] a, Solution b) {
        double distance = 0.0;
        for (int i=0; i<a.length; i++)
            distance += Math.pow(a[i]-b.getFitness(i),2);
        return distance;
    }
    
    public int coverage() {
        if  (this.isLeaf()) {
            return list.size();
        }
        else {
            int coverage = 0;
            for (int i=0; i<children.size(); i++) {
                coverage += children.get(i).coverage();
            }
            return coverage;
        }
    }
    
    public void recursivelyExtract(List<Solution> a){
        if  (!this.isLeaf()) 
           for (int i=0; i<children.size(); i++) 
                children.get(i).recursivelyExtract(a);
        else
            for (Solution s : list)
                a.add(s);
    }
}
