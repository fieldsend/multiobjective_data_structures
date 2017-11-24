
/**
 * Write a description of class CompositePoint here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CompositePoint implements Solution, Comparable<CompositePoint>
{
    private Solution[] solutions;
    
    CompositePoint(int numberOfObjectives) {
        solutions = new Solution[numberOfObjectives];
    }

    CompositePoint(Solution s) {
        solutions = new Solution[s.getNumberOfObjectives()];
        for (int i=0; i< solutions.length; i++)
            solutions[i] = s;
    }
    
    
    void setElement(int index, Solution s) {
        solutions[index] = s;
    }
    
    @Override
    public double getFitness(int index){
        return solutions[index].getFitness(index); 
    }
    
    @Override
    public void setFitness(int index, double value){
        solutions[index].setFitness(index,value);
    }
    
    @Override
    public void setFitness(double[] fitnesses) {
        for (int i=0; i<solutions.length; i++)
            solutions[i].setFitness(i,fitnesses[i]);
    }
    
    @Override
    public int getNumberOfObjectives() {
        return solutions.length;
    }
    
    /**
     * Returns -1 if this dominates the argument (or objectives the same but the internal solutions are 'lower'), 
     * 0 if they are the same including all the same solutions and 1 if this is 
     * dominated by the argument (or objectives same but internal solutions are higher). Assumes that the composite points you have constructed in 
     * your dominated or non-dominated tree are correctly formed, in that none should be
     * mutually non-dominating
     */
    @Override
    public int compareTo(CompositePoint c) {
        if (this.dominates(c))
            return -1;
        if (this.isFitnessTheSame(c)){
            // same objective values, but still need to order unless identical solutions inside
            for (int i=0; i<solutions.length; i++) {
                int hc1 = solutions[i].hashCode();
                int hc2 = c.solutions[i].hashCode();
                if (hc1<hc2)
                    return -1;
                if (hc1>hc2)
                    return 1;
            }
            return 0; 
        }
        return 1; // c dominates this
    }
}
