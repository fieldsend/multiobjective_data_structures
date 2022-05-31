package multiobjective_data_structures;

import java.util.ArrayList;
/**
 * Minimial interface for Solution to be manipulated by the data structures.
 * 
 * @author Jonathan Fieldsend
 * @version 0.1
 */
public interface Solution {
    //double[] getFitness(); 
    
    /**
     * Gets the fitness (objective) value at the index element of this Solution
     */
    double getFitness(int index);
    
    /**
     * Sets the fitness (objective) value at the index element of this Solution
     */
    void setFitness(int index, double value);
    
    /**
     * Gets the fitness (objective) vector of this Solution
     */
    void setFitness(double[] fitnesses);
    
    /** 
     * Gets the number of objectives
     */
    int getNumberOfObjectives(); 
    
    /**
     * Returns true if this Solution dominates the argument s (has a lower or equal value on all objectives)
     * and strictly lower on at least one. Otherwise returns false
     */
    default boolean dominates(Solution s){
        int better = 0;
        for (int i = 0; i < getNumberOfObjectives(); i++){
            if (getFitness(i) < s.getFitness(i))
                better++;
            else if (getFitness(i) > s.getFitness(i))
                return false;
        }
        if (better > 0)
            return true;
        return false;     
    }
    
    /**
     * Returns the relative order of this Solution compared to the argument s. If returns -1 then 
     * this dominates or is equal to s in quality. If the method returns 1 this is dominated by s. 
     * If it returns 0 this Solution and s are mutually non-dominating. 
     */
    default int getParetoOrder(Solution s){
        boolean anyBetter=false;
        boolean anyWorse=false;
        int i = 0;
        firstLoop : for (; i < getNumberOfObjectives(); i++) {
            if (getFitness(i) < s.getFitness(i)) {
                anyBetter = true;
                break firstLoop;
            }
            if (getFitness(i) > s.getFitness(i)) {
                anyWorse = true;
                break firstLoop;
            }
        }
        if (anyBetter) { // found one better
            for (; i < getNumberOfObjectives(); i++) {
                if (getFitness(i) > s.getFitness(i)) {
                    return 0; // mutually non-dominating
                }
            }
            return -1;
        } else if (anyWorse) { //found one worse
            for (; i < getNumberOfObjectives(); i++) {
                if (getFitness(i) < s.getFitness(i)) {
                    return 0; // mutually non-dominating
                }
            }
            return 1;
        }
        return -1; // all same, so weakly dominates
    }
    
    /**
     * Returns a boolean array, whose elements are true if this Solution is better (has a lower value) than the
     * corresponding objective element as s, otherwise the element in the boolean array is false.
     */
    default boolean[] better(Solution s){
        boolean[] array = new boolean[getNumberOfObjectives()];
        for (int i = 0; i < getNumberOfObjectives(); i++) {
            if (getFitness(i) < s.getFitness(i))
                array[i] = true;
            else 
                array[i] = false;
        }
        return array;     
    }
    
    /**
     * Returns true if this solution dominates or is equal quality to s, otherwise returns false
     */
    default boolean weaklyDominates(Solution s){
        for (int i = 0; i < getNumberOfObjectives(); i++)
            if (getFitness(i) > s.getFitness(i))
                return false; // worse on an objective so can't dominate
        
        return true; // not worse on any objective, so must weakly dominate
    }
    
    /**
     * Returns true if the objective vector represented by d dominates or is equal quality to s, otherwise returns false
     */
    static boolean weaklyDominates(double[] d, Solution s){
        for (int i = 0; i < s.getNumberOfObjectives(); i++)
            if (d[i] > s.getFitness(i))
                return false; // worse on an objective so can't dominate
        
        return true; // not worse on any objective, so must weakly dominate
    }
    
    /**
     * Returns true if s dominates or is equal quality to the objective vector represented by d, otherwise returns false
     */
    static boolean weaklyDominates(Solution s, double[] d){
        for (int i = 0; i < s.getNumberOfObjectives(); i++)
            if (s.getFitness(i) > d[i])
                return false; // worse on an objective so can't dominate
        
        return true; // not worse on any objective, so must weakly dominate
    }
    
    /**
     * Returns true if the objective vector represented by d dominates s, otherwise returns false
     */
    static boolean dominates(double[] d, Solution s){
        int better = 0;
        for (int i = 0; i < s.getNumberOfObjectives(); i++) {
            if (d[i] < s.getFitness(i))
                better++;
            else if (d[i] > s.getFitness(i))
                return false; // worse on an objective so can't dominate
        }
        if (better > 0)
            return true; 
        return false;
    }
    
    /**
     * Returns true if s dominates the objective vector represented by d, otherwise returns false
     */
    static boolean dominates(Solution s, double[] d){
        int better = 0;
        for (int i = 0; i < s.getNumberOfObjectives(); i++) {
            if (s.getFitness(i) < d[i])
                better++;
            else if (s.getFitness(i) > d[i])
                return false; // worse on an objective so can't dominate
        
        }
        if (better > 0)
            return true; 
        return false;
    }
    
    /**
     * Returns true if this Solution is better on all objectives than s. See e.g. Knowles et al.
     * A tutorial on the Performance Assessment of Stochastic Multiobjective Optimizers, 
     * ETH Zurich TIK-Report 214, 2006 for Strict Dominance definition.
     */
    default boolean strictlyDominates(Solution s){
        for (int i = 0; i < getNumberOfObjectives(); i++)
            if (getFitness(i) >= s.getFitness(i))
                return false; // not better on an objective, so can't strictly dominate
        
        return true; // better on all objectives
    }
    
    
    /**
     * Returns a boolean array, whose elements are true if this Solution is better (has a lower value) or equal value to the
     * corresponding objective element in s, otherwise the element in the boolean array is false.
     */
    default boolean[] betterOrEqual(Solution s){
        boolean[] array = new boolean[getNumberOfObjectives()];
        for (int i = 0; i < getNumberOfObjectives(); i++) {
            if (getFitness(i) <= s.getFitness(i))
                array[i] = true;
            else 
                array[i] = false;
        }
        return array;     
    }
    
    /** 
     * Returns an ArrayList<Integer> holding the objective indices for which this Solution
     * is better than s
     */
    default ArrayList<Integer> betterObjectives(Solution s){
        ArrayList<Integer> array = new ArrayList<>(getNumberOfObjectives());
        int j=0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) < s.getFitness(i))
                array.add(j++);
        return array;     
    }
    
    /** 
     * Returns an ArrayList<Integer> holding the objective indices for which this Solution
     * is equal to s
     */
    default ArrayList<Integer> equalObjectives(Solution s){
        ArrayList<Integer> array = new ArrayList<>(getNumberOfObjectives());
        int j=0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) == s.getFitness(i))
                array.add(j++);
        return array;     
    }
    
    /** 
     * Returns an ArrayList<Integer> holding the objective indices for which this Solution
     * is worse or equal than s
     */
    default ArrayList<Integer> worseOrEqualObjectives(Solution s){
        ArrayList<Integer> array = new ArrayList<>(getNumberOfObjectives());
        int j=0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) >= s.getFitness(i))
                array.add(j++);
        return array;     
    }
    
    /**
     * Calculates a weighted value depending on which objectives this Solution is greater 
     * or equal to argument s on
     */
    default int worseOrEqualIndex(Solution s, int elementWeights[]) {
        int val = 0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) >= s.getFitness(i))
                val+=elementWeights[i];
        
        return val; 
    }
    
    /**
     * Calculates a weighted value depending on which objectives this Solution is greater 
     * or equal to argument s on
     */
    default int equalIndex(Solution s, int elementWeights[]) {
        int val = 0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) == s.getFitness(i))
                val+=elementWeights[i];
        
        return val; 
    }
    
    /**
     * Returns true if the objective vectors of this Solution and s hold the same values, otherwise returns false
     */
    default boolean isFitnessTheSame(Solution s) {
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) != s.getFitness(i))
                return false;
        
        return true; 
    }
}
