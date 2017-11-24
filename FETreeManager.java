import java.util.Collection;
import java.util.ArrayList;

/**
 * Write a description of class FETreeManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FETreeManager implements ParetoSetManager
{
    public boolean add(Solution s) throws IllegalNumberOfObjectivesException {
        return true;
    }
    
    
    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException {
        return true;
    }
    
    public Collection<? extends Solution> getContents() {
        return new ArrayList<Solution>();
    }
    
    public int size(){
        return 0;
    }
    
    public void clean() {
        
    }
}
