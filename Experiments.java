import multiobjective_data_structures.*;
import multiobjective_data_structures.implementations.*;
import multiobjective_data_structures.implementations.tests.*;
import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Iterator; 
import java.util.ListIterator; 

/**
 * Class runs over the experiments on timings of the data structures in the paper
 * 
 * @author Jonathan Fieldsend
 * @version 13/01/2020;
 */
public class Experiments
{
    public static void main(String[] args) throws IllegalNumberOfObjectivesException, FileNotFoundException
    { 
        simulationExperiments(args);
    }

    /**
     * Method runs analytical function experiments and saves outputs to files. Loads analytical squenences from named files (see code for convention)
     */
    public static void simulationExperiments() throws IllegalNumberOfObjectivesException, FileNotFoundException {
        ParetoSetManager list[] = new ParetoSetManager[8];
        String []  dsNames =  {"BSPT", "DDT", "NDT", "QT1", "QT2", "QT3", "LL", "BI"};

        // Commented block when doing one specific combination rather 
        // than all combinations
        //int numberOfObjectives = Integer.parseInt(args[0]);
        //int c = Integer.parseInt(args[1]);
        //int d = Integer.parseInt(args[2]);
        //int nd = Integer.parseInt(args[3]);
        //int k = Integer.parseInt(args[4]);

        //int fold = 1;

        int iterations = (int) Math.pow(2,18);
        PrintWriter timeWriter;
        Scanner objectiveReader;
        int MAX_FOLDS = 10;
        int[] objs = new int[] {2,3,5,10};
        int[] c_vals = new int[] {1,2,3};
        int[] d_vals = new int[] {1,2};
        int[] Nd_vals = new int[] {1,2,3};

        for (int fold=1; fold<=MAX_FOLDS; fold++) { // for each fold
            for (int numberOfObjectives : objs){ // for each of the objective number considered
                for (int c : c_vals){ // for each index of c value used in analytical function (in file name)
                    for (int d : d_vals) { // for each index of d value used in analytical function (in file name)
                        for (int nd : Nd_vals){ // for each index of Nd value used in analytical function (in file name)
                            int numberOfDesignVariables = 0; 
                            list[0] = BSPTreeArchiveManager.managerFactory(numberOfObjectives,20); 
                            list[1] = DominanceDecisionTreeManager.managerFactory(numberOfObjectives);
                            list[2] = NDTree.managerFactory(numberOfObjectives);
                            list[3] = MTQuadTree1.managerFactory(numberOfObjectives);
                            list[4] = MTQuadTree2.managerFactory(numberOfObjectives);
                            list[5] = MTQuadTree3.managerFactory(numberOfObjectives);
                            list[6] = LinearListManager.managerFactory(0L,numberOfObjectives,true);
                            if (numberOfObjectives ==2) { // special case in two objectives when binary tree can also be used
                                list[7] = new BiObjectiveSetManager(0L);
                            }
                            double[] objectiveVector = new double[numberOfObjectives];
                            for (int k=0; k<8; k++) { // for each archiving approach
                                if ((k==7) && (numberOfObjectives!=2)) // break out for data structure index 7 if not a bi-objective problem
                                    break;

                                long runningTotal = 0, tic, toc;

                                timeWriter = new PrintWriter(new File("GECCO_2020_simulation_c_" + c + "_d_" + d + "_Nd_" + nd + "_obj_" + numberOfObjectives 
                                        + "_DS_" + dsNames[k] + "_fold" + fold+".csv")); 
                                String temp = "";//"../../../../Documents/MATLAB/gecco_2020_analytic_files/";        
                                objectiveReader = new Scanner(new File(temp+ "GECCO2020_analytical_fold_" + fold + "_objectives_" + numberOfObjectives + "_c_" + c + "_d_" + d 
                                        + "_Nd_"+ nd +".txt")); 

                                ThreadMXBean bean = ManagementFactory.getThreadMXBean(); // object to track timings

                                long[] nanosecondsToUpdate = new long[iterations];

                                runningTotal += 0;
                                //System.out.println(dsNames[k] + "_" + numberOfObjectives);
                                //evolve
                                for (int i=0; i<iterations; i++) {
                                    String s = objectiveReader.nextLine();
                                    //System.out.println(s);
                                    String[] vals = s.split(",");
                                    for (int m =0; m< numberOfObjectives; m++)
                                        objectiveVector[m] = Double.parseDouble(vals[m]);
                                    ProxySolution child = new ProxySolution(objectiveVector);

                                    // tic
                                    tic = bean.getCurrentThreadCpuTime();
                                    list[k].add(child);
                                    toc = bean.getCurrentThreadCpuTime();

                                    nanosecondsToUpdate[i] = toc-tic;
                                    runningTotal += nanosecondsToUpdate[i];
                                    writeSolution(timeWriter, ((k==0) ? list[k].size() : 0), nanosecondsToUpdate[i]); 

                                    //toc
                                    if (i%1000 == 0) {
                                        System.out.println(dsNames[k] + " num objs " + numberOfObjectives + " c: " + c + " d: " + d + " Nd: " + nd + " iteration: "+ i + ", archive size: " +  list[k].size() + ", secs: " + runningTotal/1000000000.0 + ", nanosecs: " + runningTotal);
                                        //if (k==0)
                                        //    System.out.println(((BSPTreeArchiveManager) list[k]).deepGetNumberCovered());
                                    }
                                }
                                timeWriter.close();
                                objectiveReader.close();
                                objectiveReader = null;
                                timeWriter = null;
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Method runs DTLZ experiments and saves outputs to files. 
     */
    public static void dtlzExperiments() throws IllegalNumberOfObjectivesException, FileNotFoundException{
        //int numberOfObjectives = Integer.parseInt(args[1]); 
        ParetoSetManager list[] = new ParetoSetManager[8];
        String []  dsNames =  {"BSPT", "DDT", "NDT", "QT1", "QT2", "QT3", "LL", "BI"};

        int iterations = 200000;
        PrintWriter timeWriter;
        int MAX_FOLDS = 30;
        int[] objs = new int[] {2,3,5,10};
        int[] prob = new int[] {1,2};

        for (int fold = 0; fold <= MAX_FOLDS; fold++) {
            for (int numberOfObjectives : objs) {
                for (int problem : prob){
                    int numberOfDesignVariables = numberOfObjectives-1+9; 
                    list[0] = BSPTreeArchiveManager.managerFactory(numberOfObjectives,20); 
                    list[1] = DominanceDecisionTreeManager.managerFactory(numberOfObjectives);
                    list[2] = NDTree.managerFactory(numberOfObjectives);
                    list[3] = MTQuadTree1.managerFactory(numberOfObjectives);
                    list[4] = MTQuadTree2.managerFactory(numberOfObjectives);
                    list[5] = MTQuadTree3.managerFactory(numberOfObjectives);
                    list[6] = LinearListManager.managerFactory(0L,numberOfObjectives,true);
                    if (numberOfObjectives == 2) {
                        list[7] = new BiObjectiveSetManager(0L);
                    }
                    for (int k=0; k<8; k++){
                        if ((k==7) &&(numberOfObjectives!=2)) // only consider bi-objective data struture for bi-objective problems
                            break;
                        Random rng = new Random((long)fold);

                        long runningTotal = 0;

                        timeWriter = new PrintWriter(new File("GECCO_2020_problem_" + problem + "_obj_" + numberOfObjectives 
                                + "_DS_" + dsNames[k] + "_fold" + fold+".csv")); 

                        ThreadMXBean bean = ManagementFactory.getThreadMXBean(); // object to track timings

                        long[] nanosecondsToUpdate = new long[iterations];
                        // make initial solution
                        DTLZSolution s = EvolutionStrategyTest.generateInitialSolution(numberOfObjectives, numberOfDesignVariables, rng, problem);

                        //tic
                        long tic = bean.getCurrentThreadCpuTime();
                        list[k].add(s);
                        long toc = bean.getCurrentThreadCpuTime();
                        //toc
                        nanosecondsToUpdate[0] = toc-tic;
                        runningTotal += nanosecondsToUpdate[0];
                        //evolve
                        for (int i=1; i<iterations; i++) {
                            DTLZSolution child = EvolutionStrategyTest.evolve(s,rng);
                            child.evaluate(problem);

                            // tic
                            tic = bean.getCurrentThreadCpuTime();
                            if (list[k].add(child)) {
                                toc = bean.getCurrentThreadCpuTime();
                                s = child;
                            } else {
                                toc = bean.getCurrentThreadCpuTime();
                            }
                            nanosecondsToUpdate[i] = toc-tic;
                            runningTotal += nanosecondsToUpdate[i];
                            writeSolution(timeWriter, ((k==0) ? list[k].size() : 0), nanosecondsToUpdate[i]); 

                            //toc
                            if (i%1000 == 0) {
                                System.out.println(dsNames[k] + " iteration: "+ i + ", archive size: " +  list[k].size() + ", secs: " + runningTotal/1000000000.0 + ", nanosecs: " + runningTotal);
                                //if (k==0)
                                //    System.out.println(((BSPTreeArchiveManager) list[k]).deepGetNumberCovered());
                            }
                        }
                        timeWriter.close();
                    }
                }
            }
        }

    }

    /**
     * Writes out to a file timeings and archive growth details
     */
    public static void writeSolution(PrintWriter timeWriter, int archiveSize, long time) 
    {
        // write solutions
        StringBuilder sb = new StringBuilder();

        sb = new StringBuilder();
        sb.append(time);
        sb.append(", ");
        sb.append(archiveSize);
        sb.append("\n");
        timeWriter.write(sb.toString());
    }

    /**
     * Writes out to a file memory and archive growth details
     */
    public static void writeSolution(PrintWriter memWriter, int archiveSize, double mem) 
    {
        // write solutions
        StringBuilder sb = new StringBuilder();

        sb = new StringBuilder();
        sb.append(mem);
        sb.append(", ");
        sb.append(archiveSize);
        sb.append("\n");
        memWriter.write(sb.toString());
    }
}
