

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import java.util.Collection;
/**
 * The test class MTQuadTree3Test.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class MTQuadTree3Test
{
    private ParetoSetManager list;
    private Random rng;
    private int OBJECTIVE_NUMBER = 3;
    /**
     * Default constructor for test class LinearListManagerTest
     */
    public MTQuadTree3Test()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp() throws IllegalNumberOfObjectivesException
    {
        list = MTQuadTree3.managerFactory(OBJECTIVE_NUMBER);
        rng = new Random(0L);
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
        list = null;
    }
    
    @Test
    public void testSize(){
        assertTrue(list.size()==0);
    }
    
    
    @Test(timeout=20000)
    public void testAdd()
    throws IllegalNumberOfObjectivesException {
        ParetoSetManager linearList = LinearListManager.managerFactory(0L,OBJECTIVE_NUMBER);
        int number = 10000;
        for (int i=0; i<number; i++){
            System.out.println("adding: " + i);
            
            double[] toAdd = new double[OBJECTIVE_NUMBER];
            for (int ii=0; ii < OBJECTIVE_NUMBER; ii++)
                toAdd[ii] = rng.nextGaussian();
            System.out.println("Query point: "+ toAdd[0]+ "  " + toAdd[1]+ "  " + toAdd[2]);
            
                //System.out.println(toAdd[0]+ "  " + toAdd[1]);
            System.out.println("added "+linearList.add(new ProxySolution(toAdd)));
            System.out.println("added "+list.add(new ProxySolution(toAdd)));
            System.out.println(list.size());
            System.out.println(linearList.size());
            System.out.println(list);
            
            Collection<? extends Solution> set1 = list.getContents();
            Collection<? extends Solution> set2 = linearList.getContents();
            
            for (Solution s : set1) {
                toAdd = s.getFitness();
                System.out.println("member Quad Tree: " + s+" "+ toAdd[0]+ "  " + toAdd[1]+ "  " + toAdd[2]);
            }
            
            for (Solution s : set2) {
                toAdd = s.getFitness();
                System.out.println("member Linear List: "+ s+" "+ toAdd[0]+ "  " + toAdd[1]+ "  " + toAdd[2]);
            }
            // now check contents match    
            System.out.println(set1.size());
            System.out.println(set2.size());
            
            assertTrue(list.size()==linearList.size());
            
            assertTrue(set1.size()==set2.size());
            assertTrue(set2.containsAll(set1));
            assertTrue(set1.containsAll(set2)); //WHY CAUSING ERROR?
        }
        
        System.out.println(list.size());
    }
}
