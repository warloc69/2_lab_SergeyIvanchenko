package lab.server.model.bridge;
import lab.TaskInfo;
import java.util.*;
import lab.exception.*;
/**
 * interface list all method for the access to the Data Base.
 */
public interface Bridge {
     /**
     * Remove task.
     * @param id remove task.
	 * @param uid user identifier.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id, int uid) throws DataAccessException;
	/**
	* Add the new user to the data base.
	* @param user username.
	* @param pass user password.	
    * @throws DataAccessException if we can't have access to Data Base.
	*/
	public void addNewUser(String user, String pass) throws DataAccessException;
    /**
     * Add task
	 * @param task reference on the add task.
	 * @param uid user identifier.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void addTask(TaskInfo task, int uid) throws DataAccessException;
    /**
    * Get task from file.
	* @param id id for the gets task.
	* @param uid user identifier.
	* @return getted task
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(final long id ,final int uid) throws DataAccessException;
     /**
     * Remove All task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeAll() throws DataAccessException; 
    /**
    * Load all tasks from file.
	* @param uid user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public Hashtable<Long,TaskInfo> getAll(final int uid) throws DataAccessException;
    /**
    * Edit task
    * @param id id for the edits task.
    * @param task reference on the add task.
	* @param uid user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task, final int uid) throws DataAccessException;
	/**
	* Return user identifier.
	* @param userName username.
	* @param pass user password.
	* @throws DataAccessException if we can't have access to Data Base.
	*/
	public Integer getUID(final String userName, final String pass) throws DataAccessException, UserAuthFailedException;
}