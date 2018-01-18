
/**
 * Write a description of class MFrontSolution here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MFrontSolution implements Solution
{
    private Solution contents;
    private MFrontSolution[] next;
    private MFrontSolution[] previous;
    
    
    MFrontSolution(Solution contents) {
        this.contents = contents;
        next = new MFrontSolution[getNumberOfObjectives()];
        previous = new MFrontSolution[getNumberOfObjectives()];
    }
    
    MFrontSolution getNext(int index) {
        return next[index];
    }
    
    MFrontSolution getPrevious(int index) {
        return previous[index];
    }
    
    MFrontSolution[] getNext() {
        return next;
    }
    
    MFrontSolution[] getPrevious() {
        return previous;
    }
    
    void setNext(int index, MFrontSolution element) {
        next[index] = element;
    }
    
    void setNext(MFrontSolution[] next) {
        this.next = next;
    }
    
    void setPrevious(int index, MFrontSolution element) {
        next[index] = element;
    }
    
    void setPrevious(MFrontSolution[] previous) {
        this.previous = previous;
    }
    
    void remove() {
        for (int i=0; i<getNumberOfObjectives(); i++) {
            // need to check against null, as could be at head or tail of any of the lists
            if (previous[i]!=null)
                previous[i].setNext(i,next[i]);
            if (next[i]!=null)
                next[i].setPrevious(i,previous[i]);
        }
    }
    
    
    public double getFitness(int index) {
        return contents.getFitness(index);
    }
    
    public void setFitness(int index, double value) {
        contents.setFitness(index,value);
    }
    
    public void setFitness(double[] fitnesses) {
        contents.setFitness(fitnesses);
    }
    
    public int getNumberOfObjectives() {
        return contents.getNumberOfObjectives();
    }
    
}
