
/**
 * Write a description of class ProxySolution here.
 * 
 * @author Jonathan Fieldsend
 * @version 1
 */
public class ProxySolution implements Solution
{
    private double[] objectives;
    
    ProxySolution(double[] objectivesToCopy){
        objectives = new double[ objectivesToCopy.length ];
        for (int i=0; i< objectivesToCopy.length; i++)
            objectives[i] = objectivesToCopy[i];
    }
    
    @Override
    public double[] getFitness(){
        return objectives;
    }
    
    @Override
    public double getFitness(int index){
        return objectives[index];
    }
    
    @Override
    public void setFitness(int index, double value){
        objectives[index] = value;
    }
    
    @Override
    public void setFitness(double[] fitnesses){
        objectives = fitnesses;
    }
    
    @Override
    public int getNumberOfObjectives(){
        return objectives.length;
    }
    
}
