package lab.server.model;
import java.util.Hashtable;
import lab.TaskInfo;
import lab.exception.*;
/**
* interface list method for the reading information from model about tasks.
*/
public interface ModelGetInf {
    /**
    *    Returns All tasks
    */
    public Hashtable<Long,TaskInfo> getAllTasks(int uid);
    /**
    *    Returns task.
    * @param id returning task.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(long id, int uid) throws DataAccessException;
}