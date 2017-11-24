import java.util.TreeSet;


/**
 * Write a description of class DominatedTree here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DominatedTree
{
    private TreeSet<CompositePoint> tree = new TreeSet<>(); 
    void add(Solution s) {
        if (tree.size()==0) {
            tree.add(new CompositePoint(s));
        } else {
            if (s.weaklyDominates(tree.first())) {
                tree.add(new CompositePoint(s));
            } else {
                CompositePoint node = tree.lower(new CompositePoint(s)); //get highest dominator of s
                
            }
        }
            
    }
    
    
}
