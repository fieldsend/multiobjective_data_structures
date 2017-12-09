

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import java.util.Collection;


/**
 * The test class DominatedTreeTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class DominatedTreeTest
{
    private DominatedTree tree;
    final int OBJECTIVE_NUMBER = 3;
    private Random rng = new Random(0L);
    /**
     * Default constructor for test class DominatedTreeTest
     */
    public DominatedTreeTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
        tree = new DominatedTree();
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
        tree = null;
    }
    
    @Test 
    public void testGetBest() {
        CompositePointTracker s = new CompositePointTracker(new ProxySolution(new double[]{0.0, 1.0, 0.0}));
        tree.add(s);
        assertEquals(s, tree.getBest().getElement(0));
    }
    
    @Test 
    public void testGetBest2(){
        double val = 0.0;
        int n = 1000;
        for (int i=0; i<n; i++){
            CompositePointTracker s = new CompositePointTracker(new ProxySolution(new double[]{val, val, val}));
            tree.add(s);
            assertEquals(s, tree.getBest().getElement(0));
            val = val-0.1;
        }
        System.out.println(tree);
        assertEquals(tree.size(),n);
    }
    
    @Test 
    public void testAdd(){
        double val = 0.0;
        int n = 1000;
        for (int i=0; i<n; i++){
            CompositePointTracker s = new CompositePointTracker(new ProxySolution(new double[]{val, val, val}));
            tree.add(s);
            assertEquals(s, tree.getBest().getElement(0));
            val = val-0.1;
        }
        CompositePointTracker s = new CompositePointTracker(new ProxySolution(new double[]{val, val, val}));
        tree.add(s);
        assertEquals(tree.size(),n);
    }
    
    
    
}
