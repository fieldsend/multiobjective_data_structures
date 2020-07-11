# multiobjective_data_structures

<p>A package containing various specialised multiobjective data structures in Java. If you use this package in your work please cite</p>

<p>J. E. Fieldsend</br>
Data structures for non-dominated sets: implementations and empirical assessment of two decades of advances</br>
Proceedings of the 2020 Genetic and Evolutionary Computation Conference</br>
pages 489-497</br>
2020</p>

<p>and additionally the source of the base data structure employed (see reference list in the citation above for the various data structures, and/or API documentation generated from the codebase using the <code>javadoc</code> tool).</p>

<p>The packages are detailed below at a high level -- package documentation gives further details.</p>

## multiobjective_data_structures

<p>contains the various interfaces, abstract types and Exceptions core to the package. Primarily the <code>ParetoSetManager</code> interface which all of the data structures implement, and define their core functionality.</p>

## multiobjective_data_structures.implementations
 
<p>Contains the implementations of eight data structures from the liteature to store, query and update non-dominated sets of solutions (Pareto set approximations). The Linear List, the Balanced Binary Tree (for bi-objective problems only), Quad Tree variants 1, 2, and 3, the Dominance Decision Tree, The BSP Tree, and the ND Tree</p>

## multiobjective_data_structures.tests

<p>Contains the unit tests and supporting test classes for the package.</p>

## Experiments from GECCO 2020 paper

<p>The <code>Experiments</code> class contains methods to rerun the experiments from the GECCO 2020 paper.</p>

  <code>Experiments.dtlzExperiments()</code>

<p>Will run each data structure in the package 30 times on each of DTLZ1 and DTLZ2 using a (1+1)--ES for 200 000 generations, for 2, 3, 5 and 10 objective dimensions, and write out timings results to file for each combination. Note the performance of some data structures serious degrades as the archive size and/or number of objectives increases, so unless you have cycles to burn you might not want to run all combinations(!).</p>

  <code>Experiments.simulationExperiments()</code>

<p>Will run each data structure in the package on each the file generated from the analytical generator. Note that you will want therefore to generate these files first -- matlab files are provided in the reporsitor for this. Files generated should be named </p>

  <code>GECCO2020_analytical_fold_" + fold + "\_objectives\_" + numberOfObjectives + "\_c\_" + c + "\_d\_" + d + "\_Nd\_"+ nd +".txt"</code>

<p>Where <code>fold</code>, <code>c</code>, <code>d</code> and <code>nd</code> are as defined in the paper, in order for <code>Experiments.simulationExperiments()</code> to process them.</p>

# A few points to note:

<p>In an unorthadox approach the unit tests are in the package  <code>multiobjective_data_structures.implementations.tests</code> rather than in a parallel structure. This is deliberate -- many of the tests are written to the abstract interafces provided (e.g. <code>ParetoSetManager</code>), so should be useful for testing any data structure that you might want to develop in the farmework, and are therefore distrubuted with the package.</p>

<p>Most classes have JavaDoc for the methods -- I'll be tidying these up a bit post-GECCO, but often the internal variables are named according to the convention of the source paper (unless I made a decision that the name was too opaque).</p>

<p>For computational efficiency the QuadTree apporaches precomute an array of arrays of child indices, rather than reclaculating them on the fly each time. This saves wasted computation, however due to the power term (it is 2^numberOfObjectives by 2^numberOfObjectives) for a high numberOfObjectives this is likely to hit memory bounds. Other data structures don't have this issue, and I'll likely develop an 'on the fly' version in the next substantial release for the QuadTree.</p>





