package multiobjective_data_structures;

/**
 * Any abstract decorating class of the Solution interface
 * 
 * @author Jonathan Fieldsend
 * @version 1.0
 */
public abstract class ComparableSolutionDecorator implements Solution, Comparable<Solution>
{
    private Solution solution;
    
    /**
     * Solution to decorate
     */
    public ComparableSolutionDecorator(Solution solution)
    {
        this.solution = solution;
    }
    
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
    
    /**
     * Return decorated solution
     */
    public Solution getDecoratedSolution()
    {
        return solution;
    }
}
