package lab.client.conntroller;
import lab.*;
import lab.exception.*;
import java.io.*;
/**
* interface list method for the controller.
*/
public interface ManagerControllerInterface {
    /**
     * Add task
	 * @param task reference on the add task.
     * @throws DataAccessException if we can't have access to Data Base.
     * @throws BadTaskException if task is invalide.
     */
    public void addTask(TaskInfo task) throws DataAccessException, BadTaskException ;
     /**
     * Remove task.
     * @param id removeing task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void delTask(long id)throws DataAccessException ;
    /**
    * Edit task
    * @throws DataAccessException if we can't have access to Data Base.
    * @throws BadTaskException if task is invalide.
    * @param task reference on the edit task.
    */
    public void editTask(long id, TaskInfo task) throws DataAccessException, BadTaskException ;
    /**
    * insert model into controller.
    * @param model reference of the model.
    */
    public void setOutStream(PrintWriter out);

}