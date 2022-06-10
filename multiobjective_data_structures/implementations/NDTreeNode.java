package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;


import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


/**
 * NDTreeNode defines nodes used in the NDTree.
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
    private static double MIN_VALUE_FOR_VOLUME_CALCULATIONS = 0.00000001;
    
    /**
     * Node constructor -- only used to make root as doesn't connect upwards to a parent
     */
    NDTreeNode(int maxListSize, int numberOfChildren) {
        if (maxListSize < numberOfChildren){
            System.out.println("Maximum list size must be at least as big as the number of children");
            numberOfChildren = maxListSize;
        }
        MAX_LIST_SIZE = maxListSize;
        NUMBER_OF_CHILDREN = numberOfChildren;
        list = new ArrayList<Solution>(MAX_LIST_SIZE+1);
    }
    
    /*
     * Construct a node connected to parent
     */
    private NDTreeNode(int maxListSize, int numberOfChildren, NDTreeNode parent) {
        this(maxListSize, numberOfChildren);
        this.parent = parent;
    }
    
    /**
     * Returns a list of the (NDTreeNode) children of this node
     */
    List<NDTreeNode> getChildren() {
        return children;
    }
    
    /**
     * Adds a solution covered by this node and updates the Ideal and Nadir points correspondingly
     */
    void add(Solution solution){
        list.add(solution);
        if (list.size()==1)
            setIdealNadir(solution);
        else
            updateIdealNadir(solution);
    }
    
    /*
     * Helper method to convert double array to a string representation of contents
     */
    private String stringConvert(double[] x){
        String s = "";
        if (x==null)
            return s;
        for (double d : x)
            s += (d +", ");
        return s;
    }
    
    /**
     * Returns a String representation of the ideal point of this node
     */
    String idealAsString() {
        return stringConvert(idealPointEstimate);
    }
    
    /**
     * Returns a String representation of the nadir point of this node
     */
    String nadirAsString() {
        return stringConvert(nadirPointEstimate);
    }
    
    /**
     * Returns a String representation of the mid point of this node
     */
    String midpointAsString() {
        return stringConvert(midpoint);
    }
    
    /*
     * Helper method to set the ideal and nadir based on solution argument
     */
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
    
    /* 
     * Helper method to update the ideal and nadir based on solution argument
     */
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
    
    /**
     * Return true if node list is empty (i.e. this is an internal node) otherwise
     * returns false (i.e. this is a leaf node)
     */
    boolean isEmpty(){
        if (list == null)
            return false; // if list is null, then is an internal node with children
        return list.isEmpty(); 
    }
    
    /**
     * Returns true if this is a leaf node, false otherwise
     */
    boolean isLeaf() {
        return children == null;
    }
    
    /**
     * Returns true if this is the root node, false otherwise
     */
    boolean isRoot() {
        return parent == null;
    }
    
    /**
     * Checks if a solution is weakly-dominated by the archive -- added functionality to meet 
     * requirements of archive management interface
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
                        return false; // existing member dominated (can't be equal given previous if check), so archive does not weakly-dominate
                }
            } else 
                for (NDTreeNode n : children) 
                    if (n.weaklyDominates(solution) == true)
                        return true; // if it is wekly-dominatated further down tree, return false and stop processing further
        }
        return false; // not weakly-dominated by any solution ith node
    }
    
    /**
     * Checks if a solution is dominated by the archive
     */
    boolean dominates(Solution solution) {
        if (Solution.dominates(nadirPointEstimate,solution))
            return true;
        if (Solution.weaklyDominates(solution,idealPointEstimate)){
            return false;
        }
        if (Solution.dominates(idealPointEstimate,solution) || Solution.dominates(solution,nadirPointEstimate)){ // short-circuit or
            if (this.isLeaf()) {
                Iterator<Solution> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Solution member = iterator.next();
                    if (member.dominates(solution))
                        return true;
                    if (solution.weaklyDominates(member))
                        return false; // existing member weakly-dominated, so archive does not dominate
                }
            } else 
                for (NDTreeNode n : children) 
                    if (n.weaklyDominates(solution) == true)
                        return true; // if it is wekly-dominatated further down tree, return false and stop processing further
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
                    this.children = child.children;
                    if (children != null)
                        for (NDTreeNode c : children)
                            c.parent = this;
                } else if (children.size()==0) {
                    // special case if all child nodes cleared out
                    children = null; // make a leaf
                    list = new ArrayList<Solution>(MAX_LIST_SIZE+1);
                }
            }
        }
        //System.out.println("Lies outside hyper-rectangle of node, so accept");
        return true; // not dominated by any solution ith node
    }    
    
    /**
     * Inserts solution in this node or futher down tree
     */
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
    
    /*
     * Helper method to split this node  -- used when leaf capacity reached
     */
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
    
    /*
     * Helper method to get closest child node for solution
     */
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
    
    /*
     * Helper method to get squared distances of objective vectors of two solutions
     */
    private static double squaredDistance(Solution a, Solution b) {
        double distance = 0.0;
        for (int i=0; i<a.getNumberOfObjectives(); i++)
            distance += Math.pow(a.getFitness(i)-b.getFitness(i),2);
        return distance;
    }
    
    /*
     * Helper method to get squared distances of objective vector and a solutions
     */
    private static double squaredDistance(double[] a, Solution b) {
        double distance = 0.0;
        for (int i=0; i<a.length; i++)
            distance += Math.pow(a[i]-b.getFitness(i),2);
        return distance;
    }
    
    /**
     * Returns the number of solutions covered by the (sub)tree rooted at this node
     */
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
    
    
    /** 
     * Returns the bucket (list) of solutions at a random leaf. Traversal down the 
     * tree is proportional to the (est) volume bewteen the nadir and ideal defined by a node
     */
    List<Solution> getRandomLeaf(Random rng){
        if  (this.isLeaf()) 
            return list;
        double totalVolume = 0.0;
        double[] cumulativeVolumes = new double[children.size()]; 
        int j=0;
        for (NDTreeNode c : children) {
            double tempVolume = 1.0;
            // need small value to prevent zero volume where all objectives the same for one dimension
            for (int i=0; i<c.idealPointEstimate.length; i++){
                tempVolume *= Math.max(c.nadirPointEstimate[i]-c.idealPointEstimate[i],MIN_VALUE_FOR_VOLUME_CALCULATIONS);
                totalVolume += tempVolume;
            }
            cumulativeVolumes[j] = (j>0) ? totalVolume + cumulativeVolumes[j-1] : totalVolume;
            j++;
        }
        // now draw a real value on the range and go to the respective child
        double draw = rng.nextDouble() * cumulativeVolumes[j-1];
        j=0;
        for (NDTreeNode c : children) {
            if (cumulativeVolumes[j] >= draw){
                return c.getRandomLeaf(rng);
            }
            j++;
        }
        return null; // should never reach here
    }
    
    /**
     * Returns a member of the (sub)tree rooted at this node uniformly at random
     */
    Solution getRandom(Random rng){
        if  (this.isLeaf()) 
            return list.get(rng.nextInt(list.size()));
        
        int num = rng.nextInt(coverage());
        int total = 0;
        for (NDTreeNode c : children) {
            total += c.coverage();
            if (total >= num){
                return c.getRandom(rng);
            }    
        }
        return null; // should never occur
    }
    
    /**
     * Recursively extracts all solutions covered by the (sub)tree rooted at this node
     */
    public void recursivelyExtract(List<Solution> a){
        if  (!this.isLeaf()) 
           for (int i=0; i<children.size(); i++) 
                children.get(i).recursivelyExtract(a);
        else
            for (Solution s : list)
                a.add(s);
    }
    
    /**
     * Fills the solutions list with solutions which approximately extremise the index
     * objective
     */
    void getExtremeMember(ArrayList<Solution> solutions,int index) 
    {
        if  (this.isLeaf()) {
            Solution c = list.get(0);
            for (int i = 1; i<list.size(); i++)
                if (list.get(i).getFitness(index) < c.getFitness(index))
                    c = list.get(i);
            solutions.add(c);      
            return;
        }
        for (NDTreeNode c : children) {
            if (idealPointEstimate[index] >= c.idealPointEstimate[index]){
                c.getExtremeMember(solutions,index); 
            }    
        }
        return;
    }
    
    public double[] getEstimatedIdeal() {
        return idealPointEstimate;    
    }
    
    public double[] getEstimatedNadir() {
        return nadirPointEstimate;    
    }
    
    public double[] getMidpoint() {
        return midpoint;    
    }
}
