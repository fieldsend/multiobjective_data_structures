package multiobjective_data_structures.implementations;

import multiobjective_data_structures.ComparableSolutionDecorator;
import multiobjective_data_structures.Solution;


/**
 * SolutionWrapper wraps a solution so it can be compared on just one
 * objective for storage in e.g. sorted lists.
 * 
 * @author Jonathan Fieldsend 
 * @version 1
 */
public class ComparableSolutionSingleValue extends ComparableSolutionDecorator
{
    private int index;
    public ComparableSolutionSingleValue(Solution solution, int index){
        super(solution);
        this.index = index;
    }
    
    public double getValue(){
        return getFitness(index);
    }
    
    @Override
    public int compareTo(Solution o) {
        if (o == null)
            throw new NullPointerException();
        return getFitness(index)<o.getFitness(index) ? -1 :
               getFitness(index)>o.getFitness(index) ? 1 : 0;
    }
}
