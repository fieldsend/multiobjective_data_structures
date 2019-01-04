package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

/**
 * Write a description of class CompositePointTracker here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
class FETreeSolutionWrapper implements Solution
{
    private FETreeCompositePoint compositeMember;
    //private TreeSet<CompositePoint> nonDominatedTreeMembership = new TreeSet<>();
    private Solution trackedSolution;

    FETreeSolutionWrapper(Solution trackedSolution) {
        this.trackedSolution = trackedSolution;
    }

    void setDominatedTreeCompositeMember(FETreeCompositePoint compositeMember) {
        this.compositeMember = compositeMember;
    }
    
    
    FETreeCompositePoint getDominatedTreeMembership() {
        return compositeMember;
    }
    
    Solution getWrappedSolution() {
        return trackedSolution;
    }
    
    @Override
    public double getFitness(int index) {
        return trackedSolution.getFitness(index);
    }
    
    @Override
    public void setFitness(int index, double value) {
        trackedSolution.setFitness(index,value);
    }
    
    @Override
    public void setFitness(double[] fitnesses) {
        trackedSolution.setFitness(fitnesses);
    }
    
    @Override
    public int getNumberOfObjectives() {
        return trackedSolution.getNumberOfObjectives();
    }
}
