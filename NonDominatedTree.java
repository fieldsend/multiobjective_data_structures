import java.util.HashSet;
import java.util.TreeSet;
import java.util.NavigableSet;
import java.util.Collections;
/**
 * Write a description of class NonDominatedTree here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NonDominatedTree
{/*
    // tree set is ordered from most dominated (first) to that which dominates all others (last)
    private TreeSet<CompositePoint> tree = new TreeSet<>(Collections.reverseOrder()); 
    

    int size() {
        return tree.size();
    }
    
    HashSet<CompositePointTracker> getDominatedByInsertAndClean(Solution s) {
        HashSet<CompositePointTracker> dominatedSet = new HashSet<>();
        
        // first remove all Solutions contributing to composite points that are strictly dominated by s
        NavigableSet<CompositePoint> dominatedCompositePoints = tree.tailSet(new CompositePoint(new CompositePointTracker(s),true), false);
        for (CompositePoint c : dominatedCompositePoints) 
            dominatedSet.addAll(c.getUniqueElements());
        
        tree.removeAll(dominatedCompositePoints); // remove the composite points from the tree
        
        // now process from first through to compisite point which strictly dominates s
        loopThroughTree : for (CompositePoint c : tree) {
            if (c.strictlyDominates(s)) { //as impossible  for this or any first cp to have elements dominated by s 
                break loopThroughTree;
            } 
            for (CompositePointTracker p : c.getUniqueElements()) {
                if (s.weaklyDominates(p)) {
                    dominatedSet.add(p);
                    c.purge(p);
                }
                //if (c.
            }
        }
        return dominatedSet;
    }
    
    void add(CompositePointTracker trackedPoint) {
        
        if (tree.size()==0) {
            CompositePoint cp = new CompositePoint(trackedPoint);
            tree.add(cp);
            trackedPoint.addToNonDominatedTreeTracking(cp);
        } else {
            if (trackedPoint.weaklyDominates(tree.first())) {
                CompositePoint cp = new CompositePoint(trackedPoint);
                tree.add(cp);
                trackedPoint.addToNonDominatedTreeTracking(cp);
            } else {
                //get (weak)dominator of s which is directly below and to left of it
                CompositePoint node = tree.lower(new CompositePoint(trackedPoint));
                //construct a new composite point based on max elements of node and s
                CompositePoint insertedNode = new CompositePoint(trackedPoint.getNumberOfObjectives());
                for (int i=0; i<trackedPoint.getNumberOfObjectives(); i++){
                    if (node.getFitness(i) > trackedPoint.getFitness(i)) {
                        insertedNode.setElement(i, node.getElement(i));
                        node.getElement(i).addToNonDominatedTreeTracking(insertedNode);
                    } else
                        insertedNode.setElement(i, trackedPoint);
                    trackedPoint.addToNonDominatedTreeTracking(insertedNode);
                }
                tree.add(insertedNode);
            }
        }       
    }
    
    int deletePossiblyInsert(CompositePointTracker s, CompositePointTracker dominatingPoint) {
        TreeSet<CompositePoint> setToRemove = s.getNonDominatedTreeMembership();
        int numberEntered = 0;
        for (CompositePoint c : setToRemove) {
            CompositePoint node = tree.lower(c);

            if (node!=null) { // if there is a lower dominating node
                for (int i=0; i<s.getNumberOfObjectives(); i++) {
                    if (c.getElement(i)==s) {
                        c.setElement(i, node.getElement(i));
                        node.getElement(i).addToDominatedTreeTracking(c); //track use in composite point
                    }
                }
            } else { // c is the most dominating node, so have to change elements of c without pulling from a dominating cp
                if (c.getNumberOfUniqueElements() == 1) { //special case where c is entirely made of s, so simply remove
                    tree.remove(c);
                } else {
                    for (int i=0; i<s.getNumberOfObjectives(); i++) {
                        if (c.getElement(i)==s) {
                            double minValue = dominatingPoint.getFitness(i);
                            int k = -1;
                            // find another member of c to represent the ith objective
                            for (int j=0; j<s.getNumberOfObjectives(); j++) {
                                if (c.getElement(j)!=s) {
                                    if (c.getElement(j).getFitness(i) < minValue) {
                                        minValue = c.getElement(j).getFitness(i);
                                        k = j;
                                    }
                                }
                            }
                            if (k==-1) {
                                c.setElement(i, dominatingPoint);
                                dominatingPoint.addToNonDominatedTreeTracking(c);
                                numberEntered++;
                            } else
                                c.setElement(i, c.getElement(k));
                        }
                    }
                }
            }
            c.purge(s); // remove s from set maintained in c
        }
        return numberEntered;
    }
    */
}
