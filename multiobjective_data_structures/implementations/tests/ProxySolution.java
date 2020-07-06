package multiobjective_data_structures.implementations.tests;
import multiobjective_data_structures.*;

/**
 * Write a description of class ProxySolution here.
 * 
 * @author Jonathan Fieldsend
 * @version 1
 */
public class ProxySolution implements Solution
{
    private double[] objectives;
    
    public ProxySolution(double[] objectivesToCopy){
        objectives = new double[ objectivesToCopy.length ];
        for (int i=0; i< objectivesToCopy.length; i++)
            objectives[i] = objectivesToCopy[i];
    }
    
    /*@Override
    public double[] getFitness(){
        return objectives;
    }*/
    
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
    
    /**
     * Equal if fitness the same
     */
    @Override
    public boolean equals(Object o){
        //System.out.println("In equals check " + this + " "+ o);
        if (o instanceof Solution) {
          //  System.out.println(getFitness());
          //  System.out.println(((Solution) o).getFitness());
            return isFitnessTheSame((Solution) o);
        }
        return false;
    }
    
    @Override 
    public String toString() {
        String s = "Objective Values-- ";
        for (double d : objectives)
            s+= " : " + d;
        return s;    
    }
}
