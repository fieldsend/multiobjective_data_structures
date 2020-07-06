package multiobjective_data_structures.implementations.tests;

import multiobjective_data_structures.Solution;
import java.util.Random; 

/**
 * Write a description of class DTLZSolution here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DTLZSolution implements Solution {
    private double[] objectives;
    double designVariables[];
    Random rng;
    
    public DTLZSolution(double[] lowerBound, double[] upperBound, Random rng){
        this.rng = rng;
        objectives = new double[ lowerBound.length ];
        for (int i=0; i<lowerBound.length; i++)
            objectives[i] = lowerBound[i] + rng.nextDouble()*(upperBound[i] - lowerBound[i]);
    }

    public DTLZSolution(int numberOfObjectives, int numberOfDesignVariables, Random rng){
        this.rng = rng;
        objectives = new double[ numberOfObjectives ];
        designVariables = new double[ numberOfDesignVariables ];
        for (int i=0; i<numberOfDesignVariables; i++)
            designVariables[i] = rng.nextDouble();
    }

    public DTLZSolution(DTLZSolution toCopy, Random rng) {
        this.rng = rng;
        objectives = new double[ toCopy.objectives.length ];
        designVariables = new double[ toCopy.designVariables.length ];
        for (int i=0; i<toCopy.designVariables.length; i++)
            designVariables[i] = toCopy.designVariables[i];
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

    public int getNumberOfDesignVariables(){
        return designVariables.length;
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
    
    public void evaluate(int problem) {
        if (problem ==1)
            DTLZ1();
        else
            DTLZ2();
    }
    
    
    private void DTLZ1() {
        double[] f = new double[this.getNumberOfObjectives()];
        double g = 0.0;
        int k = this.designVariables.length - this.getNumberOfObjectives() + 1;

        for (int i = this.designVariables.length - k; i < this.designVariables.length; i++) {
            g += Math.pow(this.designVariables[i] - 0.5, 2.0)
            - Math.cos(20.0 * Math.PI * (this.designVariables[i] - 0.5));
        }
        g = 100.0 * (k + g);

        for (int i = 0; i < this.getNumberOfObjectives(); i++) {
            f[i] = 0.5 * (1.0 + g);

            for (int j = 0; j < this.getNumberOfObjectives() - i - 1; j++) 
                f[i] *= this.designVariables[j];

            if (i != 0) 
                f[i] *= 1 - this.designVariables[this.getNumberOfObjectives() - i - 1];

        }
        for (int i=0; i<this.getNumberOfObjectives(); i++)
            this.setFitness(i, f[i]);
    }

    private void DTLZ2() {
        double[] f = new double[this.getNumberOfObjectives()];
        double g = 0.0;
        int k = this.designVariables.length - this.getNumberOfObjectives() + 1;

        for (int i = this.designVariables.length- k; i < this.designVariables.length; i++) 
            g += Math.pow(this.designVariables[i] - 0.5, 2.0);

        for (int i = 0; i < this.getNumberOfObjectives(); i++) {
            f[i] = 1.0 + g;

            for (int j = 0; j < this.getNumberOfObjectives()- i - 1; j++) 
                f[i] *= Math.cos(0.5 * Math.PI * this.designVariables[j]);

            if (i != 0) 
                f[i] *= Math.sin(0.5 * Math.PI * this.designVariables[this.getNumberOfObjectives() - i - 1]);
        }
        for (int i=0; i<this.getNumberOfObjectives(); i++)
            this.setFitness(i, f[i]);
    }
}