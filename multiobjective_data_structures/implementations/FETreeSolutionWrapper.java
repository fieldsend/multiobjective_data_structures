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
    private FETreeCompositePoint compositeMemberDT; //which composite point it is in in the dominated tree
    //private TreeSet<CompositePoint> nonDominatedTreeMembership = new TreeSet<>();
    private Solution trackedSolution;
    private LLWrappedObjectiveNode[] sortedListLocations;
    boolean updateFlag;
    
    FETreeSolutionWrapper(Solution trackedSolution, boolean updateFlag) {
        this.trackedSolution = trackedSolution;
        sortedListLocations = new LLWrappedObjectiveNode[trackedSolution.getNumberOfObjectives()]; 
        this.updateFlag = updateFlag;
    }

    boolean getCurrentFlag() {
        return updateFlag;
    }
    
    void switchFlag() {
        updateFlag = !updateFlag;
    }
    
    void setFlag(boolean updateFlag) {
        this.updateFlag = updateFlag;
    }
    
    
    void setSortedListLocation(LLWrappedObjectiveNode node, int index) {
        sortedListLocations[index] = node;
    }
    
    LLWrappedObjectiveNode getSortedListLocation(int index) {
        return sortedListLocations[index];
    }
    
    void setDominatedTreeCompositeMember(FETreeCompositePoint compositeMember) {
        this.compositeMemberDT = compositeMember;
    }
    
    void detachFromSortedLists(LLObjectiveTree[] sortedListsOfObjectives){
        for (int i=0; i < sortedListLocations.length; i++) {
            sortedListLocations[i].detach(sortedListsOfObjectives[i]);
        }
    }
    
    FETreeCompositePoint getDominatedTreeMembership() {
        return compositeMemberDT;
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
