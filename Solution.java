 import java.util.ArrayList;
/**
 * Minimial interface for Solution to be manipulated by the data structures.
 * 
 * @author Jonathan Fieldsend
 * @version 0.1
 */
interface Solution {
    double[] getFitness(); 
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
    
    default ArrayList<Integer> worseOrEqualObjectives(Solution s){
        ArrayList<Integer> array = new ArrayList<>(getNumberOfObjectives());
        int j=0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) >= s.getFitness(i))
                array.add(j++);
        return array;     
    }
    
    default int betterOrEqualIndex(Solution s, int elementWeights[]) {
        int val = 0;
        for (int i = 0; i < getNumberOfObjectives(); i++) 
            if (getFitness(i) <= s.getFitness(i))
                val+=elementWeights[i];
        
        return val; 
    }
    
    
}
