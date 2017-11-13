
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
    public double[] getFitness()
    {
        return solution.getFitness();
    }
    public double getFitness(int index)
    {
        return solution.getFitness(index);
    }
    public void setFitness(int index, double value)
    {
        solution.setFitness(index, value);
    }
    public void setFitness(double[] fitnesses)
    {
        solution.setFitness(fitnesses);
    }
    public int getNumberOfObjectives()
    {
        return solution.getNumberOfObjectives();
    }
}
