import java.util.TreeSet;

/**
 * Write a description of class CompositePointTracker here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
class XXCompositePointTracker implements Solution
{
    private TreeSet<XXCompositePoint> dominatedTreeMembership = new TreeSet<>();
    //private TreeSet<CompositePoint> nonDominatedTreeMembership = new TreeSet<>();
    private Solution trackedSolution;

    XXCompositePointTracker(Solution trackedSolution) {
        this.trackedSolution = trackedSolution;
    }

    void addToDominatedTreeTracking(XXCompositePoint p) {
        System.out.println("<<<TRACKING...ADDING " + p + " TO "+ this.getWrappedSolution());
        dominatedTreeMembership.add(p);
    }

    void removeFromToDominatedTreeTracking(XXCompositePoint p) {
        System.out.println("<<<TRACKING...REMOVING " + p + " FROM "+ this.getWrappedSolution());
        dominatedTreeMembership.remove(p);
    }
    
    void clearDominatedTreeTracking() {
        dominatedTreeMembership.clear();
    }
    
    /*void addToNonDominatedTreeTracking(CompositePoint p) {
        nonDominatedTreeMembership.add(p);
    }*/

    TreeSet<XXCompositePoint> getDominatedTreeMembership() {
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
