package lab;
import java.util.*;
/**
* Create wrapper for the xml packager.
*/
public class ParsedInfo {
    private String userName = null;
    private String userPass = null;
    private int pingTimeOut = 0;
	private int size = 0;
    private int userID = 0;
    private String command = null;
    private String message = null;
    private Hashtable<Long,TaskInfo> tasks = new Hashtable<Long,TaskInfo>();
    private long countTask = 1l;
    public void setUserName (String name) {
        userName = name;
    }
	/**
	* Set user password
	*/
    public void setUserPass(String pass) {
        userPass = pass;
    }
    public void setPingTimeOut(int time) {
        pingTimeOut = time;
    }
	/**
	* Set user identifier.
	*/
    public void setUserID(int uid) {
        userID = uid;
    }
	/**
	* Set command.
	*/
    public void setCommand(String com) {
        command = com;
    }
	/**
	* Set message.
	*/
    public void setMessage(String mes) {
        message = mes;
    }
	/**
	* Set task.
	*/
    public void setTask(TaskInfo ts) {
        tasks.put(countTask,ts);
        countTask++;
    }
	/**
	* Set task count.
	*/
	public void setSize(int size) {
		this.size = size;
	}
	/**
	* Get user name.
	*/
    public String getUserName() {
        return userName;
    }
	/**
	* Get user password.
	*/
    public String getUserPass() {
        return userPass;
    }
    public int getPingTimeOut() {
        return pingTimeOut;
    }
	/**
	* Get user identifier.
	*/
    public int getUserID() {
        return userID;
    }
	/**
	* get command
	*/
    public String getCommand() {
        return command;
    }
	/**
	* Get message.
	*/
    public String getMessage() {
        return message;
    }
	/**
	* Get task list.
	*/
    public Hashtable<Long,TaskInfo> getAllTasks () {
        return tasks;
    }
	/**
	* get task.
	*/
    public TaskInfo getTask() {
        Collection<TaskInfo> col = tasks.values();
        for (TaskInfo ts: col )
        return ts;
        return null;
    }
	/**
	* get count.
	*/
	public int getSize() {
		return size;
	}
}