package multiobjective_data_structures.implementations.tests;
import multiobjective_data_structures.*;
import multiobjective_data_structures.implementations.BiObjectiveSetManager;

//import multiobjective_data_structures.implementations.tests.SharedTest;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import java.util.Collection;

/**
 * The test class BiObjectiveSetManagerTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class BiObjectiveSetManagerTest
{
    private int numberOfQueries = 2000;
    
    /**
     * Default constructor for test class LinearListManagerTest
     */
    public BiObjectiveSetManagerTest()
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
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }
    
    @Test
    public void testSize() throws IllegalNumberOfObjectivesException{
        ParetoSetManager list = BiObjectiveSetManager.managerFactory(0L);
        assertTrue(list.size()==0);
    }
    
    
    @Test(timeout=20000)
    public void testAdd2()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 2;
        SharedTest.exampleRun(BiObjectiveSetManager.managerFactory(0L),new Random(0L),objectiveNumber,numberOfQueries);      
    }
    
}
