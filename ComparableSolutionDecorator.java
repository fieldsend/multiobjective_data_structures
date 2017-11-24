
/**
 * Abstract class ComparableSolutionDecorator - write a description of the class here
 * 
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class ComparableSolutionDecorator implements Solution, Comparable<Solution>
{
    private Solution solution;
    
    ComparableSolutionDecorator(Solution solution)
    {
        this.solution = solution;
    }
    /*@Override
    public double[] getFitness()
    {
        return solution.getFitness();
    }*/
    @Override
    public double getFitness(int index)
    {
        return solution.getFitness(index);
    }
    @Override
    public void setFitness(int index, double value)
    {
        solution.setFitness(index, value);
    }
    @Override
    public void setFitness(double[] fitnesses)
    {
        solution.setFitness(fitnesses);
    }
    @Override
    public int getNumberOfObjectives()
    {
        return solution.getNumberOfObjectives();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Solution) {
            return isFitnessTheSame((Solution) o);
        }
        return false;
    }
    
    public Solution getDecoratedSolution()
    {
        return solution;
    }
}
