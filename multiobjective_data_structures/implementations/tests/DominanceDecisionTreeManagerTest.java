package multiobjective_data_structures.implementations.tests;
import multiobjective_data_structures.*;
import multiobjective_data_structures.implementations.DominanceDecisionTreeManager;
//import multiobjective_data_structures.implementations.tests.SharedTest;
//import multiobjective_data_structures.implementations.tests.EvolutionStrategyTest;

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
public class DominanceDecisionTreeManagerTest
{
    private int numberOfQueries = 2000;
    
    /**
     * Default constructor for test class LinearListManagerTest
     */
    public DominanceDecisionTreeManagerTest()
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
    public void testSize(){
        ParetoSetManager list = DominanceDecisionTreeManager.managerFactory(2);
        assertTrue(list.size()==0);
    }
    
    
    @Test(timeout=200000)
    public void testAdd2()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 2;
        SharedTest.exampleRun(DominanceDecisionTreeManager.managerFactory(objectiveNumber),new Random(0L),objectiveNumber,numberOfQueries);      
    }
    
    @Test(timeout=200000)
    public void testAdd3()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 3;
        SharedTest.exampleRun(DominanceDecisionTreeManager.managerFactory(objectiveNumber),new Random(0L),objectiveNumber,numberOfQueries);      
    }
    
    @Test(timeout=200000)
    public void testAdd10()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 10;
        SharedTest.exampleRun(DominanceDecisionTreeManager.managerFactory(objectiveNumber),new Random(0L),objectiveNumber,numberOfQueries);      
    }
    
    @Test(timeout=200000)
    public void testAdd100()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 100;
        SharedTest.exampleRun(DominanceDecisionTreeManager.managerFactory(objectiveNumber),new Random(0L),objectiveNumber,numberOfQueries);      
    }
    
    @Test
    public void testES()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 5;
        EvolutionStrategyTest.exampleRunDTLZ(DominanceDecisionTreeManager.managerFactory(objectiveNumber), 
            new Random(0L),objectiveNumber,objectiveNumber-1+9, 2, 10000);  
    }
}
