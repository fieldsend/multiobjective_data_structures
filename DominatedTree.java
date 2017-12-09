import java.util.TreeSet;

/**
 * Write a description of class DominatedTree here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DominatedTree
{
    private TreeSet<CompositePoint> tree = new TreeSet<>(); 

    CompositePoint getBest() {
        return tree.first();
    }

    int size() {
        return tree.size();
    }

    boolean memberDominates(Solution s) {
        for (CompositePoint c : tree) {
            if (s.dominates(c)) {
                return false;
            }
            if (c.anyElementWeaklyDominates(s))
                return true;
        }
        return false;
    }

    void add(CompositePointTracker trackedPoint) {
        if (tree.size()==0) {
            CompositePoint cp = new CompositePoint(trackedPoint);
            tree.add(cp);
            trackedPoint.addToDominatedTreeTracking(cp);
        } else {
            if (trackedPoint.weaklyDominates(tree.first())) {
                CompositePoint cp = new CompositePoint(trackedPoint);
                tree.add(cp);
                trackedPoint.addToDominatedTreeTracking(cp);
            } else {
                //get (weak)dominated by trackedPoint
                CompositePoint queryPoint = new CompositePoint(trackedPoint);
                CompositePoint higherNode = tree.higher(queryPoint);
                CompositePoint lowerNode;
                if (higherNode!=null)
                    lowerNode = tree.lower(higherNode); // can do this more efficiently
                else
                    lowerNode = tree.last();
                //construct a new composite point based on max elements of node and s
                CompositePoint insertedNode = new CompositePoint(trackedPoint.getNumberOfObjectives());
                for (int i=0; i<trackedPoint.getNumberOfObjectives(); i++){
                    if (lowerNode.getFitness(i) < trackedPoint.getFitness(i)) {
                        insertedNode.setElement(i, trackedPoint);
                    } else {
                        insertedNode.setElement(i, lowerNode.getElement(i)); //add composite point to tracking of copied point
                        lowerNode.getElement(i).addToDominatedTreeTracking(insertedNode);
                    }
                }
                trackedPoint.addToDominatedTreeTracking(insertedNode); // add composite point to tracking of the new point
                tree.add(insertedNode);
            }
        }        
    }

    boolean deletePossiblyInsert(CompositePointTracker s, CompositePointTracker dominatingPoint) {
        TreeSet<CompositePoint> setToRemove = s.getDominatedTreeMembership();
        for (CompositePoint c : setToRemove) {
            CompositePoint node = tree.lower(new CompositePoint(s));

            if (node!=null) { // if there is a lower dominating node
                for (int i=0; i<s.getNumberOfObjectives(); i++) {
                    if (c.getElement(i)==s) {
                        c.setElement(i, node.getElement(i));
                        node.getElement(i).addToDominatedTreeTracking(c); //track use in composite point
                    }
                }
            } else { // c is the most dominating node
                if (c.getNumberOfUniqueElements() == 1) { //special case where c is entirely made of s, so simply remove
                    tree.remove(c);
                } else {
                    for (int i=0; i<s.getNumberOfObjectives(); i++) {
                        if (c.getElement(i)==s) {
                            double maxValue = Double.MIN_VALUE;
                            int k = -1;
                            // find another member of c to represent the ith objective
                            for (int j=0; j<s.getNumberOfObjectives(); j++) {
                                if (c.getElement(j)!=s) {
                                    if (c.getElement(j).getFitness(i) > maxValue) {
                                        maxValue = c.getElement(j).getFitness(i);
                                        k = j;
                                    }
                                }
                            }
                            c.setElement(i, c.getElement(k));
                        }
                    }
                }
            }
            c.purge(s); // remove s from set maintained in c
        }
        return true;
    }
    
    @Override 
    public String toString() {
        String s ="";
        for (CompositePoint c : tree)
            s+= c + "\n";
        return s;
    }
    
}
