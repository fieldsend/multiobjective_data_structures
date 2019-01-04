package multiobjective_data_structures;


public class ComparableSolutionAllValues extends ComparableSolutionDecorator
{
    private int index;
    public ComparableSolutionAllValues(Solution solution, int index){
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
        for (int i=0; i < getNumberOfObjectives(); i++) {   
            if (getFitness(index)<o.getFitness(index))
                return -1;
            if (getFitness(index)>o.getFitness(index)) 
                return 1;
        }
        return 0;
    }
}