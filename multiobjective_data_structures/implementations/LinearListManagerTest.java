package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

/**
 * The test class LinearListManagerTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class LinearListManagerTest
{
    private ParetoSetManager list;
    private Random rng;
    /**
     * Default constructor for test class LinearListManagerTest
     */
    public LinearListManagerTest()
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
        list = LinearListManager.managerFactory(0L,2);
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
    
    
    @Test
    public void testAdd()
    throws IllegalNumberOfObjectivesException {
        int number = 1000;
        for (int i=0; i<number; i++){
            list.add(new ProxySolution(new double[]{rng.nextGaussian(), rng.nextGaussian() }));
        }
        
        System.out.println(list.size());
    }
}

