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
    double getFitness(int index);
    void setFitness(int index, double value);
    void setFitness(double[] fitnesses);
    int getNumberOfObjectives(); 
    
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
    
    default boolean weaklyDominates(Solution s){
        for (int i = 0; i < getNumberOfObjectives(); i++)
            if (getFitness(i) > s.getFitness(i))
                return false; // worse on an objective so can't dominate
        
        return true; // not worse on any objective, so must weakly dominate
    }
    
    static boolean weaklyDominates(double[] d, Solution s){
        for (int i = 0; i < s.getNumberOfObjectives(); i++)
            if (d[i] > s.getFitness(i))
                return false; // worse on an objective so can't dominate
        
        return true; // not worse on any objective, so must weakly dominate
    }
    
    static boolean weaklyDominates(Solution s, double[] d){
        for (int i = 0; i < s.getNumberOfObjectives(); i++)
            if (s.getFitness(i) > d[i])
                return false; // worse on an objective so can't dominate
        
        return true; // not worse on any objective, so must weakly dominate
    }
    
    /**
     * Returns true if this Solution is better on all objectives than s. See e.g. Knowles et al.
     * A tutorial on the Performance Assessment of Stochastic Multiobjective Optimizers, 
     * ETH Zurich TIK-Report 214, 2006 for Strict Dominance definition.
     * 
     */
    default boolean strictlyDominates(Solution s){
        for (int i = 0; i < getNumberOfObjectives(); i++)
            if (getFitness(i) >= s.getFitness(i))
                return false; // not better on an objective, so can't strictly dominate
        
        return true; // better on all objectives
    }
    
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
    
    default ArrayList<Integer> betterObjectives(Solution s){
        ArrayList<Integer> array = new ArrayList<>(getNumberOfObjectives());
        int j=0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) < s.getFitness(i))
                array.add(j++);
        return array;     
    }
    
    default ArrayList<Integer> equalObjectives(Solution s){
        ArrayList<Integer> array = new ArrayList<>(getNumberOfObjectives());
        int j=0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) == s.getFitness(i))
                array.add(j++);
        return array;     
    }
    
    default ArrayList<Integer> worseOrEqualObjectives(Solution s){
        ArrayList<Integer> array = new ArrayList<>(getNumberOfObjectives());
        int j=0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) >= s.getFitness(i))
                array.add(j++);
        return array;     
    }
    
    /**
     * calculates a weighted value depending on which objectives this Solution is greater 
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
     * calculates a weighted value depending on which objectives this Solution is greater 
     * or equal to argument s on
     */
    default int equalIndex(Solution s, int elementWeights[]) {
        int val = 0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) == s.getFitness(i))
                val+=elementWeights[i];
        
        return val; 
    }
    
    default boolean isFitnessTheSame(Solution s) {
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) != s.getFitness(i))
                return false;
        
        return true; 
    }
}
