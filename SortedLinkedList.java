import java.util.LinkedList;
import java.util.List;


/**
 * SortedLinkedList.
 * 
 * @author Jonathan Fieldsend
 * @version 1
 */
public class SortedLinkedList
{
    private List<SolutionWrapper> list = new LinkedList<>();
    private static final int LINEAR_LIMIT = 5;
    
    /**
     * Constructor for objects of class SortedLinkedList
     */
    SortedLinkedList()
    {
        
    }
    
    public void add(SolutionWrapper item) {
        if (list.size()==0)
            list.add(item);
        else {
            // binary search to find index for insertion
            list.add(findInsertionPointIndex(item),item);
        }
    }
    
    public void remove(SolutionWrapper item) {
        list.remove(findItemIndex(item));        
    }
    
    public boolean contains(SolutionWrapper item) {
        if (findItemIndex(item)!=-1)
            return true;
        return false;
    }
    
    /*
     * Returns index item should be inserted in, bracketed by a lower value below (or at bottom if none lower
     * and higher or equal value above (or at list max index +1 if none larger) 
     */
    private int findInsertionPointIndex(SolutionWrapper item) {
        if (list.size() < LINEAR_LIMIT){ // if list small, just linear search 
            int index = 0;
            for (SolutionWrapper s : list){
                if (s.getValue() > item.getValue())
                    return index;
                index++;
            }
            return index;
        }
        return binarySearchInsert(0, list.size(), item);
    }
    
    private int binarySearchInsert(int lowerInclusiveLimit, int upperExclusiveLimit, SolutionWrapper item){
        if ((upperExclusiveLimit-lowerInclusiveLimit)==1){
            if (list.get(lowerInclusiveLimit).getValue() > item.getValue())
                return lowerInclusiveLimit;
            else 
                return upperExclusiveLimit;
        }
        int midPoint = lowerInclusiveLimit + (upperExclusiveLimit-lowerInclusiveLimit)/2; // use integer division
        if (list.get(midPoint).getValue() > item.getValue()) 
            return binarySearchInsert(lowerInclusiveLimit, midPoint, item);
        else
            return binarySearchInsert(midPoint+1, upperExclusiveLimit, item);
    }
    
    /*
     * Returns index of item in list, returns -1 if not in list
     */
    private int findItemIndex(SolutionWrapper item){
        if (list.size() < LINEAR_LIMIT){ // if list small, just linear search 
            int index = 0;
            for (SolutionWrapper s : list){
                if (s.getValue() == item.getValue()){
                    if (item.getWrappedSolution().equals(s.getWrappedSolution()))
                        return index;
                }
                index++;
            }
            return -1;
        }
        return binarySearch(0, list.size(), item);
    }
    
    private int binarySearch(int lowerInclusiveLimit, int upperExclusiveLimit, SolutionWrapper item){
        if ((upperExclusiveLimit-lowerInclusiveLimit)==1){
            if (list.get(lowerInclusiveLimit).getValue() > item.getValue())
                return lowerInclusiveLimit;
            else 
                return -1;
        }
        int midPoint = lowerInclusiveLimit + (upperExclusiveLimit-lowerInclusiveLimit)/2; // use integer division
        if (item.getWrappedSolution().equals(list.get(midPoint).getWrappedSolution())) 
            return binarySearchInsert(lowerInclusiveLimit, midPoint, item);
        else
            return binarySearchInsert(midPoint+1, upperExclusiveLimit, item);
    }
}
