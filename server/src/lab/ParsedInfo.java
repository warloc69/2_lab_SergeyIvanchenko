package lab;
import java.util.*;
public class ParsedInfo {
    private String userName = null;
    private String userPass = null;
    private int userID = 0;
    private String command = null;
    private String message = null;
    private Hashtable<Long,TaskInfo> tasks = new Hashtable<Long,TaskInfo>();
    private long countTask = 1l;
    public void setUserName (String name) {
        userName = name;
    }
    public void setUserPass(String pass) {
        userPass = pass;
    }
    public void setUserID(int uid) {
        userID = uid;
    }
    public void setCommand(String com) {
        command = com;
    }
    public void setMessage(String mes) {
        message = mes;
    }
    public void setTask(TaskInfo ts) {
        tasks.put(countTask,ts);
        countTask++;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserPass() {
        return userPass;
    }
    public int getUserID() {
        return userID;
    }
    public String getCommand() {
        return command;
    }
    public String getMessage() {
        return message;
    }
    public Hashtable<Long,TaskInfo> getAllTasks () {
        return tasks;
    }
    public TaskInfo getTask() {
        Collection<TaskInfo> col = tasks.values();
        for (TaskInfo ts: col )
        return ts;
        return null;
    }
}