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
    static void exampleRun(ParetoSetManager list, Random rng, int OBJECTIVE_NUMBER, int numberOfQueries) throws IllegalNumberOfObjectivesException {
        ParetoSetManager linearList = LinearListManager.managerFactory(0L,OBJECTIVE_NUMBER);
        
        for (int i=0; i<numberOfQueries; i++){
            System.out.println("QUERY: " + i);
            
            double[] toAdd = new double[OBJECTIVE_NUMBER];
            for (int ii=0; ii < OBJECTIVE_NUMBER; ii++)
                toAdd[ii] = (double) rng.nextInt(100);
            System.out.print("Query point: ");
            for (int ii=0; ii < OBJECTIVE_NUMBER; ii++)
                System.out.print(toAdd[ii]+ "  ");
            System.out.println();
                //System.out.println(toAdd[0]+ "  " + toAdd[1]);
            System.out.println("Test manager added "+list.add(new ProxySolution(toAdd)));
            System.out.println("Linear list added "+linearList.add(new ProxySolution(toAdd)));
            System.out.println(list.size());
            System.out.println(linearList.size());
            //System.out.println(list);
            Collection<? extends Solution> set1 = list.getContents();
            Collection<? extends Solution> set2 = linearList.getContents();
            
            /*for (Solution s : set1) {
                toAdd = s.getFitness();
                System.out.print("member Quad Tree: " + s+" ");
                for (int ii=0; ii < OBJECTIVE_NUMBER; ii++)
                    System.out.print(toAdd[ii]+ "  ");
                System.out.println();
            }*/
            /*
            for (Solution s : set2) {
                toAdd = s.getFitness();
                System.out.println("member Linear List: "+ s+" "+ toAdd[0]+ "  " + toAdd[1]);
            }*/
            // now check contents match    
            System.out.println(set1.size());
            System.out.println(set2.size());
            assertTrue(set1.size()==set2.size());
            assertTrue(set2.containsAll(set1));
            assertTrue(set1.containsAll(set2)); //WHY CAUSING ERROR?
            }
        
        System.out.println(list.size());
    }
}
