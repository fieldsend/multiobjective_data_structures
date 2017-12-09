import java.util.TreeSet;

/**
 * Write a description of class CompositePointTracker here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
class CompositePointTracker implements Solution
{
    private TreeSet<CompositePoint> dominatedTreeMembership = new TreeSet<>();
    //private TreeSet<CompositePoint> nonDominatedTreeMembership = new TreeSet<>();
    private Solution trackedSolution;

    CompositePointTracker(Solution trackedSolution) {
        this.trackedSolution = trackedSolution;
    }

    void addToDominatedTreeTracking(CompositePoint p) {
        dominatedTreeMembership.add(p);
    }

    /*void addToNonDominatedTreeTracking(CompositePoint p) {
        nonDominatedTreeMembership.add(p);
    }*/

    TreeSet<CompositePoint> getDominatedTreeMembership() {
        return dominatedTreeMembership;
    }
    
    /*TreeSet<CompositePoint> getNonDominatedTreeMembership() {
        return nonDominatedTreeMembership;
    }*/
    
    Solution getWrappedSolution() {
        return trackedSolution;
    }
    
    public double getFitness(int index) {
        return trackedSolution.getFitness(index);
    }
    
    public void setFitness(int index, double value) {
        trackedSolution.setFitness(index,value);
    }
    
    public void setFitness(double[] fitnesses) {
        trackedSolution.setFitness(fitnesses);
    }
    
    public int getNumberOfObjectives() {
        return trackedSolution.getNumberOfObjectives();
    }
}
