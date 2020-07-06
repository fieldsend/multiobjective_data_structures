package multiobjective_data_structures.implementations.tests;

import multiobjective_data_structures.ParetoSetManager;
import multiobjective_data_structures.Solution;
import multiobjective_data_structures.IllegalNumberOfObjectivesException;
import multiobjective_data_structures.implementations.LinearListManager;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import java.util.Collection;
import java.util.Random; 

/**
 * Write a description of class EvolutionStrategyTest here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class EvolutionStrategyTest
{
    /*private DTLZSolution prevChild;
    private ParetoSetManager linearList;
    private Random rng;
    private ParetoSetManager list; 
    private int numberOfObjectives; 
    private int numberOfDesignVariables; 
    private int problem; 
    private int iterations;*/
    
    /*public EvolutionStrategyTest(ParetoSetManager list, Random rng, int numberOfObjectives, int numberOfDesignVariables, int problem, int iterations) {
        this.list = list;
        this.rng = rng;
        this.numberOfObjectives = numberOfObjectives;
        this.numberOfDesignVariables = numberOfDesignVariables;
        this.problem = problem;
        this.iterations = iterations;
    }*/
    
    
    public static DTLZSolution generateInitialSolution(int numberOfObjectives, int numberOfDesignVariables, Random rng, int problem) {
        DTLZSolution s = new DTLZSolution(numberOfObjectives, numberOfDesignVariables, rng);
        s.evaluate(problem);
        for (int j=0; j<numberOfObjectives; j++)
            System.out.print(s.getFitness(j) + ", ");
        return s;
    }
    
    public static void exampleRunDTLZ(ParetoSetManager list, Random rng, int numberOfObjectives, int numberOfDesignVariables, int problem, int iterations) throws IllegalNumberOfObjectivesException {
        ParetoSetManager linearList = LinearListManager.managerFactory(0L,numberOfObjectives, true);
        DTLZSolution s = generateInitialSolution(numberOfObjectives, numberOfDesignVariables, rng, problem);
        System.out.println();    
        list.add(s);
        linearList.add(s);
        checkEqualStates(list, linearList);
        //evolve
        for (int i=1; i<iterations; i++) {
            DTLZSolution child = evolve(s,rng);
            child.evaluate(problem);
            
            System.out.print("Child : ");
            for (int j=0; j<numberOfObjectives; j++)
                System.out.print(child.getFitness(j) + ", ");
            System.out.println();
            
            if (list.add(child)) {
                assertTrue(linearList.add(child));
                s = child;
            } else {
                assertFalse(linearList.add(child));
            }
            System.out.println("iteration: "+ i + ", archive size: " +  list.size());
            checkEqualStates(list, linearList);
        }
        
        System.out.println(list.size());
        assertTrue(true);
    }
    
    private static void checkEqualStates(ParetoSetManager list, ParetoSetManager linearList){
        Collection<? extends Solution> set1 = list.getContents();
        Collection<? extends Solution> set2 = linearList.getContents();
        
        /*System.out.println("Archive contents:");
        for (Solution s : set1){
            for (int j=0; j<s.getNumberOfObjectives(); j++){
                System.out.print(s.getFitness(j) + ", ");
            }
            System.out.println();
        }
        
        System.out.println("Linear list contents:");
        for (Solution s : set2){
            for (int j=0; j<s.getNumberOfObjectives(); j++){
                System.out.print(s.getFitness(j) + ", ");
            }
            System.out.println();
        }*/
        
        assertTrue("list size is: "+ list.size() +", linear list size is: "+ linearList.size(), list.size()==linearList.size());
        assertTrue("list (set) size is: "+ set1.size() +", linear list (set) size is: "+ set2.size(), set1.size()==set2.size());
        assertTrue(set2.containsAll(set1));
        assertTrue(set1.containsAll(set2));
    }
    
    
    
    public static DTLZSolution evolve(DTLZSolution s,Random rng) {
        // select dimension at random
        DTLZSolution child = new DTLZSolution(s,rng);
        int dimension = rng.nextInt(child.designVariables.length);
        // perturb
        do {
            child.designVariables[dimension] = s.designVariables[dimension];
            child.designVariables[dimension] += rng.nextGaussian()*0.1;
        } while ((child.designVariables[dimension] <0.0) || (child.designVariables[dimension]>1.0));

        return child;
    }
}
