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
    private PrintWriter out = null;
    private ManagerView mv = null;
    private BufferedReader in = null;
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
        private Stack<String>  sendCommands  = new Stack<String>();     //queue send xml packages
        private Stack<String> incomingCommands = new Stack<String>();    // queue incoming xml packages
        public boolean stop = true;
        public boolean stoped = false;
        public Commander(String hash) throws lab.exception.ConnectException{
            connect(hash);
            sender = new Sender();
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
		/**
		* Create new connection, open stream.
		* @param hash user name and password String;
		* @throws lab.exception.ConnectException if connetion is not complete.
		*/
        public void connect(String hash) throws lab.exception.ConnectException{
            try {
                Socket s = new Socket(ViewVariable.ip,ViewVariable.port);
                ServerConnector.this.s = s;
                is = s.getInputStream();
                in = new BufferedReader (
                    new InputStreamReader(is));
                out = new PrintWriter (
                    s.getOutputStream(),true);
                out.println(XMLUtil.packager("getAll",0,ViewVariable.hash,"msg", null, null));
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
                            out.println(sendCommands.pop());
                        }
                        try {
                            sendCommands.wait();
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
            while (stop) {    
                Thread.yield();				
                try {
                    if( in == null) {
                        continue;
                    }
                    while( is.available() != 0) {
                        String s = in.readLine();
                        if (s == null) {
                            continue;
                        }
                        incomingCommands.push(s);
                    }
                    while(!incomingCommands.empty()) {                    
                        ParsedInfo pars = XMLUtil.parser(incomingCommands.pop());
                        String com = pars.getCommand();
                        if ("sendAll".equals(com)) {                    
                            mv.notifyGetAll(pars);
                        }
                        if ("disconnect".equals(com)) {
							stop();
                            mv.notifyDisconnect(pars.getMessage(),pars.getCommand());
                        }
                        if ("add".equals(com)) {
                            mv.notifyAdd(pars.getTask());
                        }
                        if ("edit".equals(com)) {
                            mv.notifyEdit(pars.getTask());
                        }
                        if ("remove".equals(com)) {
                            mv.notifyRemove(Long.parseLong(pars.getMessage()));
                        }
                        if ("error".equals(com)) {
                            stop();                        
                            mv.notifyDisconnect(pars.getMessage(),pars.getCommand());
                        }
                    }    
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
        /**
         * Add task
         * @throws DataAccessException if we can't have access to Data Base.
         * @throws BadTaskException if task is invalide.
         * @param task reference on the add task.
         */
        public void addTask(TaskInfo task) throws DataAccessException, BadTaskException {
            taskValidation(task);
            String s = XMLUtil.packager("add",ViewVariable.uid,ViewVariable.hash,ViewVariable.msg,task,null);
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
            String s = XMLUtil.packager("remove",ViewVariable.uid,ViewVariable.hash,id+"",null,null);
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
        public void editTask(long id, TaskInfo task) throws DataAccessException, BadTaskException {
            taskValidation(task);
            String s = XMLUtil.packager("edit",ViewVariable.uid,ViewVariable.hash,ViewVariable.msg,task,null);
            synchronized (sendCommands) {
                sendCommands.push(s);
                sendCommands.notify();
            }
        }
		/**
		* Safe stop Commander thread.
		*/
        private void stop() {
            try {
                if (out != null) {
                    out.println(XMLUtil.packager("disconnect",ViewVariable.uid,ViewVariable.hash,"msg", null, null));
                    in.close();
                    sender.stop();
                    while(!sender.isStoped()) {
                    }
                    out.close();
                    s.close();
                }
            } catch (IOException e) {
                log.error(e);
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
    public ManagerControllerInterface startCommander(String hash) throws lab.exception.ConnectException{
        com = new Commander(hash);
        return this;
    }
	/**
	* Stop connection thread.
	*/
    public void stop() {
        com.stop = false;
      /*  while(!com.isStoped()) {
        }
        com = null;*/
    }
    /**
     * Add task
	 * @param task reference on the add task.
     * @throws DataAccessException if we can't have access to Data Base.
     * @throws BadTaskException if task is invalide.
     */
    public void addTask(TaskInfo task) throws DataAccessException, BadTaskException {
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
        com.editTask(id,task);
    }
}