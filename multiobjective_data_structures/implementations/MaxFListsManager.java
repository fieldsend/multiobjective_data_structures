package multiobjective_data_structures.implementations;

import multiobjective_data_structures.*;

/**
 * Write a description of class MinFlistsManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MaxFListsManager extends FListsManager
{
    MaxFListsManager(int numberOfObjectives) 
    {
        super(numberOfObjectives);
    }
    
    public static MaxFListsManager managerFactory(int numberOfObjectives) 
    {
        return new MaxFListsManager(numberOfObjectives);
    }

    void trackList(CriteriaNode processed, int numberProcessed, int listIndex) 
    {
        // select list which is shortest
        listToInsertOn = 0;
        for (int i = 1; i < NUMBER_OF_OBJECTIVES; i++)
            if ((listLengths[i] - numberProcessedBetter[i]) < (listLengths[listToInsertOn] - numberProcessedBetter[listToInsertOn]))
                listToInsertOn = i;
    }
    
}
