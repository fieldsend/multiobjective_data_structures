package multiobjective_data_structures.implementations.tests;
import multiobjective_data_structures.*;
import multiobjective_data_structures.implementations.LinearListManager;

//import multiobjective_data_structures.implementations.tests.ProxySolution;
//import multiobjective_data_structures.implementations.tests.SharedTest;
//import multiobjective_data_structures.implementations.tests.EvolutionStrategyTest;


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
    private int numberOfQueries = 2000;
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
        list = LinearListManager.managerFactory(0L,2,true);
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
    
    
    @Test(timeout=200000)
    public void testAdd2()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 2;
        SharedTest.exampleRun(LinearListManager.managerFactory(0L,objectiveNumber,true),new Random(0L),objectiveNumber,numberOfQueries);      
    }
    
    
    @Test(timeout=200000)
    public void testAdd3()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 3;
        SharedTest.exampleRun(LinearListManager.managerFactory(0L,objectiveNumber,true),new Random(0L),objectiveNumber,numberOfQueries);      
    }
    
    @Test(timeout=200000)
    public void testAdd10()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 10;
        SharedTest.exampleRun(LinearListManager.managerFactory(0L,objectiveNumber,true),new Random(0L),objectiveNumber,numberOfQueries);      
    }
    
    @Test(timeout=200000)
    public void testAdd100()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 100;
        SharedTest.exampleRun(LinearListManager.managerFactory(0L,objectiveNumber,true),new Random(0L),objectiveNumber,numberOfQueries);      
    }
    
    @Test
    public void testES()
    throws IllegalNumberOfObjectivesException {
        int objectiveNumber = 10;
        EvolutionStrategyTest.exampleRunDTLZ(LinearListManager.managerFactory(0L,objectiveNumber,true), 
            new Random(0L),objectiveNumber,objectiveNumber-1+9, 2, 10000);  
    }
}

