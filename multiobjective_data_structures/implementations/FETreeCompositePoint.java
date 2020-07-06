package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;


import java.util.HashSet;

/**
 * Write a description of class CompositePoint here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FETreeCompositePoint implements Solution, Comparable<FETreeCompositePoint>
{
    private FETreeSolutionWrapper[] solutions;
    private int[] deepNodeSolutions; // only used in head
    private FETreeCompositePoint previous;
    private FETreeCompositePoint next;
    private int numberOfStoredSolutions=0;
    
    FETreeCompositePoint(int numberOfObjectives) {
        solutions = new FETreeSolutionWrapper[numberOfObjectives];
    }

    FETreeCompositePoint(FETreeSolutionWrapper s, boolean dominatedTreeNode) {
        solutions = new FETreeSolutionWrapper[s.getNumberOfObjectives()];
        //for (int i=0; i< solutions.length; i++)
        // all other entries null, as only generation from constructor like this is 
        // for head of tree or for tail
        solutions[0] = s; 
        numberOfStoredSolutions = 1;
        deepNodeSolutions = new int[s.getNumberOfObjectives()]; // track that index 0 feeds all criteria 
        deepNodeSolutions[0]=-1;
        for (int i=1; i< solutions.length; i++) // set up links for deep node solutions
            deepNodeSolutions[i] = 0;
        
        if (dominatedTreeNode) {
            s.setDominatedTreeCompositeMember(this);
            //for (int i=0; i< solutions.length; i++)
            //    solutions[i] = s;
            //numberOfStoredSolutions = s.getNumberOfObjectives();
        }
    }

    
    FETreeCompositePoint(FETreeSolutionWrapper s, int index, boolean dominatedTreeNode) {
        solutions = new FETreeSolutionWrapper[s.getNumberOfObjectives()];
        //for (int i=0; i< solutions.length; i++)
        // all other entries null, as only generation from constructor like this is 
        // for head of tree or for tail
        solutions[index] = s; 
        numberOfStoredSolutions = 1;
        deepNodeSolutions = new int[s.getNumberOfObjectives()]; // track that index feeds all criteria 
        deepNodeSolutions[index]=-1;
        for (int i=0; i< solutions.length; i++) // set up links for deep node solutions
            if (i != index)
                deepNodeSolutions[i] = index;
        
        if (dominatedTreeNode) {
            s.setDominatedTreeCompositeMember(this);
        }
    }
    
    void instantiateDeepNodeSolutions() {
        deepNodeSolutions = new int[solutions.length]; // make space if new head is being formed 
    }
    
    void activateDeepNodeSolution(int index, FETreeSolutionWrapper s) {
        setElement(index, s);
        deepNodeSolutions[index] = -1;
    }
    
    void setDeepNodeSolution(int index, int deepIndex) {
        solutions[index] = null;
        deepNodeSolutions[index] = deepIndex;
    }
    
    int getNumberOfStoredSolutions() {
        return numberOfStoredSolutions;
    }
    
    void cleanDeepLinks() {
        deepNodeSolutions = null;
    }
    
    int getDeepIndex(int index) {
        return deepNodeSolutions[index];
    }
    
    void setPrevious(FETreeCompositePoint previous) {
        this.previous = previous;
    }

    void setNext(FETreeCompositePoint next) {
        this.next = next;
    }

    FETreeCompositePoint getPrevious() {
        return previous;
    }

    FETreeCompositePoint getNext() {
        return next;
    }
    
    boolean activeElement(int index) {
        return solutions[index]!=null;
    }
    
    /*
     * Will only compare to physically stored elements in this node, if using backwards reference 
     * will not compare to these, as will assume already checked
     */
    boolean anyElementWeaklyDominates(Solution s) {
        for (int i=0; i<solutions.length; i++)
            if (solutions[i]!=null) // if element contributed by a better composite point, no need to check
                if (solutions[i].weaklyDominates(s))
                    return true;
            
        return false;
    }

    
    void setElement(int index, FETreeSolutionWrapper s) {
        if (solutions[index]==null) {
            numberOfStoredSolutions++;
        }
        solutions[index] = s;
    }
    
    
    
    void inferElementFromPrevious(int index) {
        if (solutions[index]!=null) {
            numberOfStoredSolutions--;
            solutions[index] = null;
        }
    }

    /** 
     * Returns null if the element is contributed by a previous node
     */
    FETreeSolutionWrapper getElement(int index) {
        return solutions[index];
    }

    
    /*FETreeSolutionWrapper getDeepElement(int index) {
        return solutions[ deepNodeSolutions[index] ];
    }*/
    
    private FETreeSolutionWrapper getDeepElement(int index) {
        if (solutions[index]==null) {
            if (previous == null) // special case where at head of tree
                return solutions[ deepNodeSolutions[index] ];
            else
                return previous.getDeepElement(index);
        }
        return solutions[index];
    }

    /*
     * Returns true if dominator used to replace solution at all
     */
    boolean cleanAndReplaceDominatedTree(LLDominatedTree tree, FETreeSolutionWrapper toRemove, FETreeSolutionWrapper dominator) {
        //System.out.println("IN CLEAN AND REPLACE >>>>>>>>>>>>");
        if (previous!=null) { // if not at head
            int index=0;
            for (int i=0; i<solutions.length; i++) {
                if (solutions[i] == toRemove) {
                    inferElementFromPrevious(i);
                    tree.decrementActiveCount(i);
                    break; // should only appear once, so can break here
                }
            }
            
            if (numberOfStoredSolutions==0) { // remove any node if duplicate
                //System.out.println("~~~~DUPLICATE REMOVAL DT");
                if (this == tree.getWorst()) // if tail
                    tree.removeWorst();
                else
                    tree.removeComponent(this);
            } 
            return false; // dominator not inserted
        } 
        // AT HEAD
        
        //System.out.println("REMOVING FROM HEAD AND INSERTING");
        // this composite point is the head of the list
        // System.out.println("CP HEAD "+ this);
        
        // need to process any deep links here as the removed point may be responsible
        
        
        int numberOfElements = 1;
        int[] indicesCoveredByRemoved = new int[solutions.length];
        int minCovered = Integer.MAX_VALUE;
        int index = -1;
        
        for (int i=0; i<solutions.length; i++) {
            if (solutions[i] == toRemove) {
                indicesCoveredByRemoved[0] = i;
                break;
            }
        }
        
        
        for (int i=0; i<solutions.length; i++) 
            if (solutions[i]==null)
                if (deepNodeSolutions[i] == indicesCoveredByRemoved[0]) 
                    indicesCoveredByRemoved[numberOfElements++] = i; 
        
        int[] active = tree.getNonNullElementsOnEachObjective();
        for (int i=0; i<numberOfElements; i++) {
            if (active[ indicesCoveredByRemoved[i] ] < minCovered)  {
                index = indicesCoveredByRemoved[i];
                minCovered = active[ index ];
            }
        }
        solutions[ indicesCoveredByRemoved[0] ] = null;
        solutions[index] = dominator; 
        deepNodeSolutions[index] = -1;
        // redirect deep links
        for (int i=0; i<numberOfElements; i++) 
            if (indicesCoveredByRemoved[i] != index)  
                deepNodeSolutions[ indicesCoveredByRemoved[i] ] = index;
            
        tree.decrementActiveCount(indicesCoveredByRemoved[0]); // where deleted point was
        tree.incrementActiveCount(index); // where new point inserted
        // any deep node index links are  now pointing to dominator, so nothing further to be done
            
        dominator.setDominatedTreeCompositeMember(this);
        return true; // dominator now inserted
    }

    @Override 
    public String toString() {
        String s = "Objective Values-- ";
        for (int i=0; i<getNumberOfObjectives(); i++) {
            if (solutions[i]!=null)
                s+= " : " + getFitness(i);
            else
                s+= " : null";
        }
        return s;    
    }

    @Override
    public double getFitness(int index){
        /*for (int i=0; i< solutions.length; i++) {
            System.out.println(i);
            System.out.println(deepNodeSolutions[i]);
            System.out.println(solutions[i]);
        }
        System.out.println();*/
        if (solutions[index] != null)
            return solutions[index].getFitness(index); 
        if (previous == null) // special case where at head of tree
            return solutions[ deepNodeSolutions[index] ].getFitness(index);
        return previous.getFitness(index);
    }

    @Override
    public void setFitness(int index, double value){
        if (solutions[index]!=null)
            solutions[index].setFitness(index,value);
        else
            previous.setFitness(index,value); // cascade up down towards head to find a non-null to replace
    }

    @Override
    public void setFitness(double[] fitnesses) {
        for (int i=0; i<solutions.length; i++)
            this.setFitness(i,fitnesses[i]);
    }

    @Override
    public int getNumberOfObjectives() {
        return solutions.length;
    }

    /**
     * Returns -1 if this dominates the argument (or objectives the same but the internal solutions are 'lower'), 
     * 0 if they are the same including all the same solutions and 1 if this is 
     * dominated by the argument (or objectives same but internal solutions are higher). Assumes that the composite points you have constructed in 
     * your dominated or non-dominated tree are correctly formed, in that none should be
     * mutually non-dominating
     */
    @Override
    public int compareTo(FETreeCompositePoint c) {
        if (this==c)
            return 0;
        //System.out.println("this comparitor" + this);
        //System.out.println("argument comparitor" +  c);
        /*if (this.queryPoint){ // could do by type...
        if (this.strictlyDominates(c))
        return -1;
        return 1;
        }*/

        if (this.dominates(c))
            return -1;
        if (c.dominates(this))
            return 1;    
        if (this.isFitnessTheSame(c)) {
            // same objective values, but still need to order unless identical solutions inside
            for (int i=0; i<solutions.length; i++) {
                int hc1 = getDeepElement(i).hashCode();
                int hc2 = c.getDeepElement(i).hashCode();
                if (hc1<hc2)
                    return -1;
                if (hc1>hc2)
                    return 1;
            }
            return 0;  
        }
        return 0; // mutually non-dominating -- returning 0 as will be needed when dentifying ranges
    }
}
