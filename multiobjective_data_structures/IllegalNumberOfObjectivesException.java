package multiobjective_data_structures;

/**
 * IllegalNumberOfObjectives class -- to be thrown when using an incompatible
 * number of objectives on an archive or solution instance
 * 
 * @author Jonathan Fieldsend
 * @version 1.0
 */
public class IllegalNumberOfObjectivesException extends Exception
{
    public IllegalNumberOfObjectivesException() {
        super();
    }
    
    public IllegalNumberOfObjectivesException(String message) {
        super(message);
    }
}
