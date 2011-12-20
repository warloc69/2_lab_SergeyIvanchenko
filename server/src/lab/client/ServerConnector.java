package lab.client;
import java.net.*;
import lab.*;
import lab.exception.*;
import java.util.*;
import java.io.*;
import lab.client.view.*;
import lab.client.conntroller.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
/**
* Creates new coonection. 
*/
public class ServerConnector implements ManagerControllerInterface{
    public static final long serialVersionUID = 124322332l;
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerConnector.class);
    private DataOutputStream out = null;
    private ManagerView mv = null;
    private DataInputStream in = null;
    private TaskInfo task = null;    
    private Socket s = null;
    private Commander com = null;
    private InputStream is = null;
    /**
    * Class gets and sends packages from the server end changes the clien information.
    */
    private class Commander implements Runnable{
        private Thread thread = null;
        private Sender sender = null;
        private Pinger pinger = null;
        private Stack<String>  sendCommands  = new Stack<String>();     //queue send xml packages
        private Stack<String> incomingCommands = new Stack<String>();    // queue incoming xml packages
        public boolean stop = true;
        public boolean stoped = false;
        public Commander() throws lab.exception.ConnectException{
            connect();
            sender = new Sender();
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
            pinger = new Pinger();
        }
		/**
		* Create new connection, open stream.
		* @param hash user name and password String;
		* @throws lab.exception.ConnectException if connetion is not complete.
		*/
        public void connect() throws lab.exception.ConnectException{
            try {
                Socket s = new Socket(ViewVariable.ip,ViewVariable.port);
                ServerConnector.this.s = s;
                is = s.getInputStream();
                in = new DataInputStream(is);
                out = new DataOutputStream(s.getOutputStream());
                out.writeUTF(XMLUtil.packager("getAll",0,ViewVariable.userName,ViewVariable.hashPass,null, null, null));
            } catch (IOException e) {
               try {
                   if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e1) {
                    log.error(e1);
                }
                stop = false;
                log.error(e);
                throw new lab.exception.ConnectException(e);
            }
        }
        /**
        * Class create ping's package.
        */
		private class Pinger implements Runnable {
            private boolean run = true;
            public Pinger () {
                Thread t1 = new Thread(this);
                t1.setDaemon(true);
                t1.start();
            }
            public void run () {
                while(run) {
                    try {
                        synchronized (sendCommands) {                       
                            sendCommands.push(XMLUtil.ping(ViewVariable.timeOut,ViewVariable.uid));
                            sendCommands.notify();
                        }
                        Thread.sleep(ViewVariable.timeOut*1000/3);
                    } catch (InterruptedException e) {
                        log.info("Pinger stoped " + e);
                    }
                }
            }
            public void stop () {
                run = false;
            }
        }
        /**
		* class makes new thread that send packages of the server.
		*/
        private class Sender implements Runnable {
            private Thread thread = null;
            private boolean run = true;
            private boolean stop = false;
            public Sender () {
                thread = new Thread(this);
                thread.setDaemon(true);
                thread.start();
            }
            public void run() {
                while (run) {					
                    if( out == null) {
                        continue;
                    }
                    synchronized (sendCommands) {
                        while(!sendCommands.empty()) {
                            try {
                                out.writeUTF(sendCommands.pop());
                            } catch (IOException e1) {
                                log.error("sender ioerror"+e1);
                                run = false;
                                break;
                            }
                        }
                        try {
                            if (run) {
                                sendCommands.wait();
                            }
                        } catch (InterruptedException e) {
                            log.error(e);
                        }
                    }                        
                }
                stop = true;
            }
			/**
			* Safe stop Sender thread.
			*/
            public void stop() {
                run = false;
                synchronized (sendCommands) {
                    sendCommands.notify();
                }
                
            }
			/**
			* Checked stop or not Sender.
			*/
            public boolean isStoped() {
                return stop;
            }
        }   
        /**
        *    Create new thread, look through incoming packages. 
        */
        public void run() {
            Long pingTime = System.currentTimeMillis();
            while (stop) { 	
                try {
                    Thread.sleep(50);
                    if (thread.isInterrupted()) {
                        throw new InterruptedException();
                    }
                    if( in == null) {
                        continue;
                    }
                    while( is.available() != 0) {
                        String s = in.readUTF();
                        if (s == null) {
                            continue;
                        }
                        incomingCommands.push(s);
                    }
                    if (System.currentTimeMillis()-pingTime > ViewVariable.timeOut*1000) {
                        log.info("ping time out");
                        mv.notifyDisconnect("ping time out","Ping");
                        stop = false;
                        break;
                    }
                    while(!incomingCommands.empty()) {                    
                        ParsedInfo pars = XMLUtil.parser(incomingCommands.pop());
                        String com = pars.getCommand();
                        if ("sendAll".equals(com)) {
                            mv.notifyGetAll(pars);
                        }
                        if ("disconnect".equals(com)) {
                            mv.notifyDisconnect(pars.getMessage(),pars.getCommand());
							stop = false;
                            break;
                        }
                        if ("ping".equals(com)) {
                            pingTime = System.currentTimeMillis();
                        }
                        if ("add".equals(com)) {
                            mv.notifyAdd(pars.getTask());
                        }
                        if ("edit".equals(com)) {
                            mv.notifyEdit(pars.getTask());
                        }
                        if ("remove".equals(com)) {
                            mv.notifyRemove(pars.getTask().getID());
                        }
                        if ("error".equals(com)) {
                            mv.notifyDisconnect(pars.getMessage(),pars.getCommand());
                            stop = false;
                            break;
                        }
                    } 
                } catch (InterruptedException e) {
                    log.info("comander is stop");
                    stop = false;
                } catch (IOException e) {
                    stop = false;
                    log.error(e);
                if (!s.isClosed()) {
                    mv.notifyDisconnect("Server error","ERROR");
                }                
                }                
            }
            stop();
        }
    
        /**
         * Add task
         * @throws DataAccessException if we can't have access to Data Base.
         * @throws BadTaskException if task is invalide.
         * @param task reference on the add task.
         */
        public void addTask(TaskInfo task) throws DataAccessException {
            String s = XMLUtil.packager("add",ViewVariable.uid,ViewVariable.userName,ViewVariable.hashPass,null,task,null);
            synchronized (sendCommands) {
                sendCommands.push(s);
                sendCommands.notify();
            }
            
        }
         /**
         * Remove task.
         * @param id remove task.
         * @throws DataAccessException if we can't have access to Data Base.
         */
        public void delTask(long id) throws DataAccessException {
            TaskInfo ts = new TaskInfoImpl();
            ts.setID(id);
            String s = XMLUtil.packager("remove",ViewVariable.uid,ViewVariable.userName,ViewVariable.hashPass,null,ts,null);
            synchronized (sendCommands) {
                sendCommands.push(s);
                sendCommands.notify();
            }
        }
        /**
        * Edit task
        * @throws DataAccessException if we can't have access to Data Base.
        * @throws BadTaskException if task is invalide.
        * @param task reference on the edit task.
        */
        public void editTask(long id, TaskInfo task) throws DataAccessException {
            String s = XMLUtil.packager("edit",ViewVariable.uid,ViewVariable.userName,ViewVariable.hashPass,null,task,null);
            synchronized (sendCommands) {
                sendCommands.push(s);
                sendCommands.notify();
            }
        }
		/**
		* Safe stop Commander thread.
		*/
        private void stop() {
            pinger.stop();
            try {
                if (out != null) {
                    out.writeUTF(XMLUtil.packager("disconnect",ViewVariable.uid,ViewVariable.userName,ViewVariable.hashPass,null, null, null));                    
                    sender.stop();
                    while(!sender.isStoped()) {
                    }
                    in.close();
                    out.close();
                    s.close();
                }
            } catch (IOException e) {
                log.error(e);
                sender.stop();
            }
			if (com != null) {
				com.stoped = true; 
			}
        }
        /**
		* Checked stop or not Commander.
		*/
		public boolean isStoped() {
            return stoped;
        }
    }
	/**
	* Mades new ServerConnector object.
	*/
    public ServerConnector(ManagerView mv) {
        this.mv = mv;
    }
	/**
	* Start thread that look through send or get xml packager.
	* @param hash user name and password hash.
	* @throws lab.exception.ConnectException if connetion is not complete.
	*/
    public ManagerControllerInterface startCommander() throws lab.exception.ConnectException{
        com = new Commander();
        return this;
    }
	/**
	* Stop connection thread.
	*/
    public void stop() {
        com.stop = false;
    }
    /**
     * Add task
	 * @param task reference on the add task.
     * @throws DataAccessException if we can't have access to Data Base.
     * @throws BadTaskException if task is invalide.
     */
    public void addTask(TaskInfo task) throws DataAccessException, BadTaskException {
        taskValidation(task);
        com.addTask(task);
    }
     /**
     * Remove task.
     * @param id removeing task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void delTask(long id)throws DataAccessException {
        com.delTask(id);
    }
    /**
    * Edit task
    * @throws DataAccessException if we can't have access to Data Base.
    * @throws BadTaskException if task is invalide.
    * @param task reference on the edit task.
    */
    public void editTask(long id, TaskInfo task) throws DataAccessException, BadTaskException {
        taskValidation(task);
        com.editTask(id,task);
    }
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
                if (file.equals("")) {return;}
                if(!file.regionMatches(true,file.length()-3,"exe",0,3)) {
                    throw new BadTaskException("Chouse file incorrect file= "+ file);
                }
            }
            if (task.getName().length() == 0) {
                throw new BadTaskException("Name incorrect");
            }
        }
}