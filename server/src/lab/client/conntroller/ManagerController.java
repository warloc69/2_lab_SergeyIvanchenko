package lab.client.conntroller;
import lab.*;
import java.util.Date;
import lab.exception.*;
import java.io.*;
import lab.client.view.*;
/**
*  Class creates object controlling change in the model.
*/
public class ManagerController implements ManagerControllerInterface {
	private PrintWriter out = null;
    /**
    * validation task.
    * @param task reference on the validation task.
    * @throws BadTaskException if task is invalide.
    */
    private void taskValidation (TaskInfo task) throws BadTaskException {
        if (task.getDate().before(new Date())) {
            throw new BadTaskException("Date incorrect");
        }
        if (task.getExec() != null && !task.getExec().getName().equals(" ")) {
            String file = task.getExec().getPath();
            if(!file.regionMatches(true,file.length()-3,"exe",0,3)) {
                throw new BadTaskException("Chouse file incorrect");
            }
        }
        if (task.getName().length() == 0) {
            throw new BadTaskException("Name incorrect");
        }
    }
    /**
     * Add task
     * @throws DataAccessException if we can't have access to Data Base.
     * @throws BadTaskException if task is invalide.
     * @param task reference on the add task.
     */
    public void addTask(TaskInfo task) throws DataAccessException, BadTaskException {
        taskValidation(task);
        String s = XMLUtil.packager("add",ViewVariable.uid,ViewVariable.hash,ViewVariable.msg,task,null);
        out.println(s);
    }
     /**
     * Remove task.
     * @param id remove task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void delTask(long id) throws DataAccessException {
        TaskInfo ts = new TaskInfoImpl();
        ts.setID(id);	 		
		System.out.println("contr del out = "+ out);
        out.println(XMLUtil.packager("remove",ViewVariable.uid,ViewVariable.hash,ViewVariable.msg,ts,null));
    }
    /**
    * Edit task
    * @throws DataAccessException if we can't have access to Data Base.
    * @throws BadTaskException if task is invalide.
    * @param task reference on the edit task.
    */
    public void editTask(long id, TaskInfo task) throws DataAccessException, BadTaskException {
        taskValidation(task);
		out.println(XMLUtil.packager("edit",ViewVariable.uid,ViewVariable.hash,ViewVariable.msg,task,null));
    }
    /**
    * insert model into controller.
    * @param model reference of the model.
    */
    public void setOutStream(PrintWriter out) {
        this.out = out;
		System.out.println("contr set out = "+ out);
    }
}//end ManagerController