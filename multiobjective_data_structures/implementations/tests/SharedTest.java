package multiobjective_data_structures.implementations.tests;
import multiobjective_data_structures.*;
import multiobjective_data_structures.implementations.LinearListManager;


import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import java.util.Collection;
/**
 * Write a description of class SharedTest here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SharedTest
{
    public static void exampleRun(ParetoSetManager list, Random rng, int OBJECTIVE_NUMBER, int numberOfQueries) throws IllegalNumberOfObjectivesException {
        
        for (int i=0; i<numberOfQueries; i++){
            System.out.println("QUERY: " + i);
            
            double[] toAdd = new double[OBJECTIVE_NUMBER];
            for (int ii=0; ii < OBJECTIVE_NUMBER; ii++)
                toAdd[ii] = (double) rng.nextInt(100);
            /*System.out.print("Query point: ");
            for (int ii=0; ii < OBJECTIVE_NUMBER; ii++)
                System.out.print(toAdd[ii]+ "  ");
            
            System.out.println();*/
                //System.out.println(toAdd[0]+ "  " + toAdd[1]);
            System.out.println("Test manager added "+list.add(new ProxySolution(toAdd)));
            System.out.println("Manager size: " + list.size());
            
            
        }
        
        System.out.println(list.size());
    }
    
    public static void exampleRun2(ParetoSetManager list, Random rng, int OBJECTIVE_NUMBER, int numberOfQueries) throws IllegalNumberOfObjectivesException {
        ParetoSetManager linearList = LinearListManager.managerFactory(0L,OBJECTIVE_NUMBER);
        
        for (int i=0; i<numberOfQueries; i++){
            System.out.println("QUERY: " + i);
            
            double[] toAdd = new double[OBJECTIVE_NUMBER];
            for (int ii=0; ii < OBJECTIVE_NUMBER; ii++)
                toAdd[ii] = (double) rng.nextInt(100);
            /*System.out.print("Query point: ");
            for (int ii=0; ii < OBJECTIVE_NUMBER; ii++)
                System.out.print(toAdd[ii]+ "  ");
            
            System.out.println();*/
                //System.out.println(toAdd[0]+ "  " + toAdd[1]);
            System.out.println("Test manager added "+list.add(new ProxySolution(toAdd)));
            System.out.println("Linear list added "+linearList.add(new ProxySolution(toAdd)));
            System.out.println("Manager size: " + list.size());
            System.out.println("Linear list size: " + linearList.size());
            
            //System.out.println(list);
            Collection<? extends Solution> set1 = list.getContents();
            Collection<? extends Solution> set2 = linearList.getContents();
            
            /*for (Solution s : set1) {
                System.out.println("member Tree: " + s);
            }
            
            for (Solution s : set2) {
                System.out.println(s);
            }*/
            // now check contents match    
            System.out.println("Test manager extracted set size "+ set1.size());
            System.out.println("Linear list extracted set size "+set2.size());
            
            assertTrue(list.size()==linearList.size());
            assertTrue(set1.size()==set2.size());
            assertTrue(set2.containsAll(set1));
            assertTrue(set1.containsAll(set2));
        }
        
        System.out.println(list.size());
    }
}
