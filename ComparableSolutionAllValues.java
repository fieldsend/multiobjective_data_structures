
class ComparableSolutionAllValues extends ComparableSolutionDecorator
{
    private int index;
    ComparableSolutionAllValues(Solution solution, int index){
        super(solution);
        this.index = index;
    }
    
    double getValue(){
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