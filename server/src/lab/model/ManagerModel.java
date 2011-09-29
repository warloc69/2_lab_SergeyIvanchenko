package lab.model;
import java.util.*;
import lab.TaskInfo;
import lab.model.bridge.*;
import java.io.*;
import lab.exception.*;
/**
 * Class Create model for the Task Manager
 */
public class ManagerModel implements  MannagerWrite, ModelGetInf{
    private Bridge sqlBridge;
    private Hashtable<Long,TaskInfo> taskMap;
    /**
    *    It's private class generates id for the task.
    */
    private static class IDGenerator {
        static long current = System.currentTimeMillis();
        static public synchronized long get(){
            return current++;
        }
    } 
    /**
    * Load task from BD
    * @throws DataAccessException if we can't have access to Data Base.
    */
    private void loadTasks(final String tableName) throws DataAccessException {
            taskMap = sqlBridge.getAll(tableName);
    }
    /**
    * Constructor create model object.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public ManagerModel(final String tableName) throws DataAccessException {
        sqlBridge = new SQLiteBridge(tableName);        
        loadTasks(tableName);
    }
    /**
     * Remove task.
     * @param id id removing task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id, final String tableName) throws DataAccessException{
        sqlBridge.removeTask(id, tableName);
        taskMap.remove(id);
    }
    /**
     * Add task
     * @param task reference on the adding task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void addTask(TaskInfo task, final String tableName) throws DataAccessException{
        task.setID(IDGenerator.get());
        sqlBridge.addTask(task, tableName);
        taskMap.put(task.getID(),task);
    }
    /**
    * Edit task
    * @param task reference on the edit task.
    * @param id id edits task.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task, final String tableName) throws DataAccessException {        
        sqlBridge.editTask(id,task,tableName);
        taskMap.put(id,task);        
    }
    /**
    *    Returns All tasks
    */
    @SuppressWarnings("unchecked")
    public Hashtable<Long,TaskInfo> getAllTasks() {
        return (Hashtable<Long,TaskInfo>) taskMap.clone();
    }
    /**
    *    Returns task.
    * @param id id returning task.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(long id, final String tableName) throws DataAccessException{
        return sqlBridge.getTask(id,tableName);
    }
    
}//end ManagerModel