package multiobjective_data_structures.implementations;

import multiobjective_data_structures.*;
import java.util.Collection;
import java.util.ArrayList;

/**
 * ParetoSetManager. Describes methods that all managers of Pareto sets need to provide.
 * 
 * All implementations should provide a no argument constructor that provides an empty
 * initial ParetoSetManager.
 * 
 * @author Jonathan Fieldsend
 * @version 1.0
 */
public class BalancedFListsManager extends FListsManager
{
    
    BalancedFListsManager(int numberOfObjectives) 
    {
        super(numberOfObjectives);
    }
    
    public static BalancedFListsManager managerFactory(int numberOfObjectives) 
    {
        return new BalancedFListsManager(numberOfObjectives);
    }

    void trackList(CriteriaNode processed, int numberProcessed, int listIndex) 
    {
        // select list which is shortest
        listToInsertOn = 0;
        for (int i = 1; i < NUMBER_OF_OBJECTIVES; i++)
            if (listLengths[i] < listLengths[listToInsertOn])
                listToInsertOn = i;
    }
}
