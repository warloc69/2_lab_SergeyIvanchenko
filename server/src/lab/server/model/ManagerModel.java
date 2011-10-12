package lab.server.model;
import java.util.*;
import lab.TaskInfo;
import lab.server.model.bridge.*;
import java.io.*;
import lab.exception.*;
/**
 * Class Create model for the Task Manager
 */
public class ManagerModel implements  MannagerWrite, ModelGetInf{
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
    public int addNewUser(String user, String pass) throws DataAccessException {
        int uid = sqlBridge.getUID(user,pass);
        if (uid == -1) {
            sqlBridge.addNewUser(user,pass);
            return sqlBridge.getUID(user,pass);
        } else     { 
            return uid;
        }
    }
    /**
    * Load task from BD
    * @throws DataAccessException if we can't have access to Data Base.
    */
    private void loadTasks(final int uid) throws DataAccessException {
        Hashtable<Long,TaskInfo> task = sqlBridge.getAll(uid);
        if (task != null) {
            list.put(uid,task);    
        } else {
            list.put(uid, new Hashtable<Long,TaskInfo>());
        }
    }
    public int connectNewUser(String user, String pass)  throws DataAccessException {
        int uid = addNewUser(user,pass);
        loadTasks(uid);
        return uid;
    }
    /**
     * Remove task.
     * @param id id removing task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id, final int uid) throws DataAccessException{
        sqlBridge.removeTask(id, uid);
        list.get(uid).remove(id);
    }
    /**
     * Add task
     * @param task reference on the adding task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void addTask(TaskInfo task,int uid) throws DataAccessException{
        task.setID(IDGenerator.get());
        sqlBridge.addTask(task, uid);
        try {
            list.get(uid).put(task.getID(),task);
        } catch (NullPointerException e) {
            list.put(uid,new Hashtable<Long,TaskInfo>());
            list.get(uid).put(task.getID(),task);
        }
    }
    /**
    * Edit task
    * @param task reference on the edit task.
    * @param id id edits task.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task, int uid) throws DataAccessException {        
        sqlBridge.editTask(id,task,uid);
        list.get(uid).put(id,task);        
    }
    /**
    *    Returns All tasks
    */
    @SuppressWarnings("unchecked")
    public Hashtable<Long,TaskInfo> getAllTasks(int uid) {
        //return new Hashtable<Long,TaskInfo>();
        return (Hashtable<Long,TaskInfo>) list.get(uid).clone();
    }
    /**
    *    Returns task.
    * @param id id returning task.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(long id, int uid) throws DataAccessException{
        return sqlBridge.getTask(id,uid);
    }
    
}//end ManagerModel