package lab.server.model.bridge;
import lab.*;
import lab.server.model.*;
import java.io.File;
import com.almworks.sqlite4java.*;
import java.util.Date;
import java.util.*;
import lab.exception.*;
/**
* This class gives access to the Data Base
*/
public class SQLiteBridge implements Bridge{
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SQLiteBridge.class);
    private SQLiteQueue queue = null;
    /**
    * Chang string character number 39 (') for the adding into the Database.
    * @param str string for changing.
    */
    private String escapeString(String str) {
        StringBuilder strNew = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == 39) {
                strNew.append("''");
            } else {
                strNew.append(ch);
            }
        }
        return strNew.toString();
    }
    /**
    * Creates a connection to an in-memory temporary database.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public SQLiteBridge() throws DataAccessException {
        queue = new SQLiteQueue(new File(ModelConst.nameBD));
        queue.start();
        SQLiteJob<Integer> job = queue.execute(new SQLiteJob<Integer>() {
            protected Integer job(SQLiteConnection connection) 
                throws SQLiteException {
                    connection.exec("BEGIN TRANSACTION;CREATE TABLE IF NOT EXISTS user (uid INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, pass TEXT);COMMIT");
                    connection.exec("BEGIN TRANSACTION;CREATE TABLE IF NOT EXISTS tasks (id LONG, name TEXT, info TEXT, file TEXT, data INTEGER, uid INTEGER, FOREIGN KEY (uid) REFERENCES user(uid));COMMIT");
                return -1;
            }                
        });
        if (job.complete() == null) {
            log.error(job.getError());
            throw new DataAccessException("DataBase create table error",job.getError());
        } else {
            if (log.isInfoEnabled()) {
                log.info("open DataBase, create or open table");
            }
        }
    }
	/**
	* Add the new user to the data base.
	* @param user username.
	* @param pass user password.	
    * @throws DataAccessException if we can't have access to Data Base.
	*/
    public void addNewUser(String user, String pass) throws DataAccessException{
        StringBuffer sb = new StringBuffer("BEGIN TRANSACTION;INSERT INTO user(name,pass) VALUES('");
        sb.append(escapeString(user));
        sb.append("','");
        sb.append(escapeString(pass));
        sb.append("');COMMIT;");
        final String command = sb.toString();
        SQLiteJob<Integer> job = null;
        synchronized (queue) {
            job = queue.execute(new SQLiteJob<Integer>() {
                protected Integer job(SQLiteConnection connection) 
                    throws SQLiteException {
                        connection.exec(command);
                    return -1;
                }
            });
        }
        if (job.complete() == null) {
            log.error("user " + user +job.getError());
            throw new DataAccessException("Add user Error user "+ user,job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, add user " + user );
        }
    }
    /**
     * Add task
     * @param task reference on the add task.
	 * @param uid user identifier.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void addTask(TaskInfo task, int uid)throws DataAccessException {
        StringBuffer sb = new StringBuffer("BEGIN TRANSACTION;INSERT INTO tasks VALUES('");
        sb.append(task.getID());
        sb.append("','");
        sb.append(escapeString(task.getName()));
        sb.append("','");
        sb.append(escapeString(task.getInfo()));
        sb.append("','");
        if ( task.getExec() != null ) {
            sb.append(escapeString(task.getExec().getPath()));
        } else {
            sb.append("null");
        }
        sb.append("','");
        sb.append(task.getDate().getTime());
        sb.append("','");
        sb.append(uid);
        sb.append("');COMMIT;");
        final String command = sb.toString();
        SQLiteJob<Integer> job = null;
        synchronized (queue) {
            job = queue.execute(new SQLiteJob<Integer>() {
                protected Integer job(SQLiteConnection connection) 
                    throws SQLiteException {
                        connection.exec(command);
                    return -1;
                }
            });
        }
        if (job.complete() == null) {
            log.error("Add task Error, UID = "+ uid + job.getError()+ "command = "+command);
            throw new DataAccessException("Add task Error, UID = "+ uid,job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, add task UID = "+ uid);
        }
    }
     /**
     * Remove All task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeAll() throws DataAccessException{
        final String command  = "BEGIN TRANSACTION;DELETE * FROM tasks;COMMIT;";
        SQLiteJob<Integer> job = null;
        synchronized (queue) {
            job = queue.execute(new SQLiteJob<Integer>() {
                protected Integer job(SQLiteConnection connection) 
                    throws SQLiteException {
                        connection.exec(command);
                    return -1;
                }
            });
        }
        if (job.complete() == null) {
            log.error(job.getError());
            throw new DataAccessException("Remove all tasks Error",job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, remove all task");
        }
    }
     /**
     * Remove task.
     * @param id id for the removes task.	 
	 * @param uid user identifier.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id, int uid) throws DataAccessException{
        StringBuffer sb = new StringBuffer("BEGIN TRANSACTION;DELETE FROM tasks WHERE id = ");
        sb.append(id);
        sb.append(" AND uid = ");
        sb.append(uid);
        sb.append(";COMMIT;");
        final String command = sb.toString();
        SQLiteJob<Integer> job = null;
        synchronized (queue) {    
            job = queue.execute(new SQLiteJob<Integer>() {
                protected Integer job(SQLiteConnection connection) 
                    throws SQLiteException {
                        connection.exec(command);                    
                    return -1;
                }
            });
        }
        if (job.complete() == null) {
            log.error("UID = "+ uid + job.getError());
            throw new DataAccessException("Remove task Error UID = " + uid,job.getError());            
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, remove task UID = "+ uid);
        }
    }
    /**
    * Get task from file.
    * @param id id for the gets task.	
	* @param uid user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(final long id ,final int uid) throws DataAccessException{
        StringBuffer sb = new StringBuffer("SELECT * FROM tasks WHERE id = ");
        sb.append(id);
        sb.append(" AND uid = ");
        sb.append(uid);
        sb.append(";COMMIT;");
        final String command = sb.toString();
        SQLiteJob<TaskInfo> job = null;
        synchronized (queue) {
            job = queue.execute(new SQLiteJob<TaskInfo>() {
                protected TaskInfo job(SQLiteConnection connection) throws SQLiteException {
                    SQLiteStatement st = connection.prepare(command);
                    try {
                        if (!st.step()) {
                            st.dispose();
                            return null;
                        } else {
                            TaskInfo taskTemp = new TaskInfoImpl();
                            if (st.hasRow()) {
                                taskTemp.setID((Long)st.columnValue(0));
                                taskTemp.setName((String)st.columnValue(1));
                                taskTemp.setInfo((String)st.columnValue(2));
                                String s = (String)st.columnValue(3);
                                if ("null".equals(s)) {
                                    taskTemp.setExec(null);
                                } else {
                                    taskTemp.setExec(new File(s));
                                }
                                taskTemp.setDate(new Date((Long) st.columnValue(4)));
                                return taskTemp;
                            } else {
                                st.dispose();
                                return null;
                            }
                        }               
                    } finally {
                        st.dispose();
                    }
                }
            });
        }
        if (job.complete() == null) {
            log.error("UID = " + uid +job.getError());
            throw new DataAccessException("Get Task Error UID = " + uid,job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, get task UID = " + uid);
        }
        return job.complete();
    }
    /**
    * Load the user all tasks from file.	
	* @param uid user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public Hashtable<Long,TaskInfo> getAll(final int uid) throws DataAccessException {
        StringBuffer sb = new StringBuffer("SELECT * FROM tasks WHERE uid = ");
        sb.append(uid);
        sb.append(";COMMIT;");
        final String command = sb.toString();
        SQLiteJob<Hashtable<Long,TaskInfo>> job = null;
        synchronized (queue) {    
            job =  queue.execute(new SQLiteJob<Hashtable<Long,TaskInfo>>() {
                protected Hashtable<Long,TaskInfo> job(SQLiteConnection connection) throws SQLiteException {
                    SQLiteStatement st = connection.prepare(command);
                    try {
                        Hashtable<Long,TaskInfo> h = new Hashtable<Long,TaskInfo>();
                        while (st.step()) { 
                            TaskInfo taskTemp = new TaskInfoImpl();
                            if (st.hasRow()) {                                    
                                taskTemp.setID((Long)st.columnValue(0));
                                taskTemp.setName((String)st.columnValue(1));
                                taskTemp.setInfo((String)st.columnValue(2));
                                String s = (String)st.columnValue(3);
                                if ("null".equals(s)) {
                                    taskTemp.setExec(null);
                                } else {
                                    taskTemp.setExec(new File(s));
                                }
                                taskTemp.setDate(new Date((Long) st.columnValue(4)));
                                h.put(taskTemp.getID(),taskTemp);
                            } else {
                                st.dispose();
                                break;
                            }
                        }
                        return h;
                    } finally {
                        st.dispose();
                    }
                }
            });
        }
        if (job.complete() == null) {
            log.error("UID = " + uid + job.getError());
            throw new DataAccessException("Get Task Error UID = "+ uid,job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, get all tasks UID = " + uid);
        }
        return job.complete();
    }
    /**
    * Edit task
    * @param id id for the edits task.
    * @param task reference on the add task.
	* @param uid user identifier.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task, final int uid) throws DataAccessException {
        StringBuffer sb = new StringBuffer("BEGIN TRANSACTION;UPDATE tasks SET name='");
        sb.append(escapeString(task.getName()));
        sb.append("', info='");
        sb.append(escapeString(task.getInfo()));
        sb.append("', file='");
        if ( task.getExec() != null ) {
            sb.append(escapeString(task.getExec().getPath()));
        } else {
            sb.append("null");
        }
        sb.append("',data=");
        sb.append(task.getDate().getTime());
        sb.append(" WHERE id = "+ id +" AND uid = "+ uid +";COMMIT;");
        final String command = sb.toString();
        SQLiteJob<Integer> job = null;
        synchronized (queue) {    
            job = queue.execute(new SQLiteJob<Integer>() {
                protected Integer job(SQLiteConnection connection) 
                    throws SQLiteException {
                        connection.exec(command);
                    return -1;
                }
            });
        }
        if (job.complete() == null ) {
            log.error("UID = "+ uid + job.getError());
            throw new DataAccessException("Edit task Error UID = " + uid,job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, edit task UID = " + uid);
        }
    }
	/**
	* Return user identifier.
	* @param userName username.
	* @param pass user password.
	* @throws DataAccessException if we can't have access to Data Base.
	*/
    public Integer getUID(final String userName, final String pass) throws DataAccessException {
        SQLiteJob<Integer> job = null;
        synchronized (queue) {    
            job =  queue.execute(new SQLiteJob<Integer>() {
                protected Integer job(SQLiteConnection connection) throws SQLiteException {
                    SQLiteStatement st = connection.prepare("SELECT * FROM user WHERE user.name = \""+ userName +"\" AND pass = \""+ pass +"\"");
                    Integer uid = -1;
                    try {
                        while (st.step()) {
                            if (st.hasRow()) {                                    
                                uid = (Integer)st.columnValue(0);
                            } else {
                                st.dispose();
                                break;
                            }
                        }
                        return uid;
                    } finally {
                        st.dispose();
                    }
                }
            });
        }
        if (job.complete() == null) {
            log.error(job.getError());
            throw new DataAccessException("Get UID Error",job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, get UID");
        }
        return job.complete();
    }
}