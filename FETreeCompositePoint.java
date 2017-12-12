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
    private FETreeCompositePoint previous;
    private FETreeCompositePoint next;
    private int numberOfStoredSolutions=0;
    
    FETreeCompositePoint(int numberOfObjectives) {
        solutions = new FETreeSolutionWrapper[numberOfObjectives];
    }

    FETreeCompositePoint(FETreeSolutionWrapper s, boolean dominatedTreeNode) {
        solutions = new FETreeSolutionWrapper[s.getNumberOfObjectives()];
        for (int i=0; i< solutions.length; i++)
            solutions[i] = s;  
        numberOfStoredSolutions = s.getNumberOfObjectives();
        if (dominatedTreeNode)
            s.setDominatedTreeCompositeMember(this);
    }

    int getNumberOfStoredSolutions() {
        return numberOfStoredSolutions;
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
        }
        solutions[index] = null;
    }

    /** 
     * Returns null if the element is contributed by a previous node
     */
    FETreeSolutionWrapper getElement(int index) {
        return solutions[index];
    }

    private FETreeSolutionWrapper getDeepElement(int index) {
        if (solutions[index]==null)
            return previous.getDeepElement(index);
        return solutions[index];
    }

    /*
     * Returns true if dominator used to replace solution at all
     */
    boolean cleanAndReplaceDominatedTree(LLDominatedTree tree, FETreeSolutionWrapper toRemove, FETreeSolutionWrapper dominator) {
       if (previous!=null) {
            int index=0;
            for (int i=0; i<solutions.length; i++) 
                if (solutions[i] == toRemove) 
                    inferElementFromPrevious(i);
            
            if (numberOfStoredSolutions==0) { // remove any node if duplicate
                System.out.println("~~~~DUPLICATE REMOVAL");
                if (this == tree.getWorst()) // if tail
                    tree.removeWorst();
                else
                    tree.removeComponent(this);
            } 
            return false;
        } 
        System.out.println("REMOVING FROM HEAD");
        // this composite point is the head of the list
        System.out.println("CP HEAD "+ this);
        for (int i=0; i<solutions.length; i++)
            if (solutions[i] == toRemove) 
                solutions[i] = dominator;
        dominator.setDominatedTreeCompositeMember(this);
                
        return true;
    }

    @Override 
    public String toString() {
        String s = "Objective Values-- ";
        for (int i=0; i<getNumberOfObjectives(); i++) 
            s+= " : " + getFitness(i);
        return s;    
    }

    @Override
    public double getFitness(int index){
        if (solutions[index]!=null)
            return solutions[index].getFitness(index); 
        return previous.getFitness(index);
    }

    @Override
    public void setFitness(int index, double value){
        if (solutions[index]!=null)
            solutions[index].setFitness(index,value);
        else
            previous.setFitness(index,value);
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
        System.out.println("this comparitor" + this);
        System.out.println("argument comparitor" +  c);
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
