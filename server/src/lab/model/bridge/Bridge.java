package lab.model.bridge;
import lab.TaskInfo;
import java.util.*;
import lab.exception.*;
/**
 * @author serg
 * @version 1.0
 * interface list all method for the access to the Data Base.
 */
public interface Bridge {
     /**
     * Remove task.
     * @param id remove task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id, final String tableName) throws DataAccessException; 
    /**
     * Add task
	 * @param task reference on the add task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void addTask(TaskInfo task, final String tableName) throws DataAccessException;
    /**
    * Get task from file.
	* @param id id for the gets task.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(long id, final String tableName) throws DataAccessException;
     /**
     * Remove All task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeAll(final String tableName) throws DataAccessException; 
    /**
    * Load all tasks from file.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public Hashtable<Long,TaskInfo> getAll(final String tableName) throws DataAccessException;
    /**
    * Edit task
    * @param id id for the edits task.
    * @param task reference on the add task.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task, final String tableName) throws DataAccessException;
}