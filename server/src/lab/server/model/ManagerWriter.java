package lab.server.model;
import lab.TaskInfo;
import lab.exception.*;
import java.util.*;
/**
* interface list method for the writing information to the model about tasks.
*/
public interface ManagerWriter {
    /**
     * Remove task.
     * @param id removeing task.
	 * @param uid user identifier.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id, int uid) throws DataAccessException;
    /**
     * Add task
     * @param taskinfo reference on the adding task.
	 * @param uid user identifier.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public TaskInfo addTask(TaskInfo taskinfo, int uid) throws DataAccessException;    
    /**
    * Edit task
    * @param task reference on the editing task.
    * @param id editing task.
	* @param uid user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task, int uid) throws DataAccessException;
	/**
    * Returns All tasks
	* @param uid user identifier.
    */
    public Hashtable<Long,TaskInfo> getAllTasks(int uid);
    /**
    * Returns task.
    * @param id returning task.
	* @param uid user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(long id, int uid) throws DataAccessException;
		/**
	* connect new user to the data base.
	* @param user username.
	* @param pass user password.
	* @return user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
	*/
    public int connectNewUser(String user, String pass)  throws DataAccessException, UserAuthFailedException;
}