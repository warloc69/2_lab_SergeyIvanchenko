package lab.server.model;
import java.util.*;
import lab.TaskInfo;
import lab.server.model.bridge.*;
import java.io.*;
import lab.exception.*;
/**
 * Class Create model for the Task Manager
 */
public class ManagerModel implements  ManagerWriter {
    private Bridge sqlBridge;
    private Hashtable<Integer,Hashtable<Long,TaskInfo>> list = new  Hashtable<Integer,Hashtable<Long,TaskInfo>>();
    /**
    *    It's private class generates id for the task.
    */
    private static class IDGenerator {
        static long current = System.currentTimeMillis();
        static public synchronized long get(){
            return current++;
        }
    }
    public ManagerModel()  throws DataAccessException {
        sqlBridge = new SQLiteBridge();
    }
	/**
	* Add the new user to the data base.
	* @param user username.
	* @param pass user password.	
    * @throws DataAccessException if we can't have access to Data Base.
	*/
    public int addNewUser(String user, String pass) throws DataAccessException, UserAuthFailedException {
        synchronized (sqlBridge) {
            int uid = sqlBridge.getUID(user,pass);
            if (uid == -1) {
                sqlBridge.addNewUser(user,pass);
                return sqlBridge.getUID(user,pass);
            } else     { 
                return uid;
            }
        }
    }
    /**
    * Load task from BD
	* @param uid user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    private void loadTasks(final int uid) throws DataAccessException {
        synchronized (sqlBridge) {
            Hashtable<Long,TaskInfo> task = sqlBridge.getAll(uid);
            if (task != null) {
                list.put(uid,task);    
            } else {
                list.put(uid, new Hashtable<Long,TaskInfo>());
            }
        }
    }
	/**
	* connect new user to the data base.
	* @param user username.
	* @param pass user password.
	* @return user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
	*/
    public int connectNewUser(String user, String pass)  throws DataAccessException, UserAuthFailedException {
        int uid = addNewUser(user,pass);
        loadTasks(uid);
        return uid;
    }
    /**
     * Remove task.
     * @param id id removing task.
	 * @param uid user identifier.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id, final int uid) throws DataAccessException{
        synchronized (sqlBridge) {
            sqlBridge.removeTask(id, uid);
            list.get(uid).remove(id);
        }
    }
    /**
     * Add task
     * @param task reference on the adding task.
	 * @param uid user identifier.
	 * @return added task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public TaskInfo addTask(TaskInfo task,int uid) throws DataAccessException{
        task.setID(IDGenerator.get());
        synchronized (sqlBridge) {
            sqlBridge.addTask(task, uid);
            try {
                list.get(uid).put(task.getID(),task);
            } catch (NullPointerException e) {
                list.put(uid,new Hashtable<Long,TaskInfo>());
                list.get(uid).put(task.getID(),task);
            }
            return task;
        }
    }
    /**
    * Edit task
    * @param task reference on the edit task.
	* @param uid user identifier.
    * @param id id edits task.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task, int uid) throws DataAccessException {        
        synchronized (sqlBridge) {
            sqlBridge.editTask(id,task,uid);
            list.get(uid).put(id,task); 
        }
    }
    /**
    * Returns All tasks
	* @param uid user identifier.
    */
    @SuppressWarnings("unchecked")
    public synchronized Hashtable<Long,TaskInfo> getAllTasks(int uid) {
        return (Hashtable<Long,TaskInfo>) list.get(uid).clone();
    }
    /**
    * Returns task.
    * @param id id returning task.
	* @param uid user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(long id, int uid) throws DataAccessException{
        TaskInfo ts = null;
        synchronized (sqlBridge) {
            ts = sqlBridge.getTask(id,uid);
        }
        return ts;
    }
    
}//end ManagerModel