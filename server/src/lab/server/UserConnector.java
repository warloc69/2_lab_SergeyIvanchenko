package lab.server;
import java.net.*;
import java.io.*;
import lab.server.model.*;
import lab.exception.*;
import lab.*;
/**
* Connect new user to the server
*/
public class UserConnector implements Runnable {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(UserConnector.class);
    private Socket soc = null;
    private String exitMsg = " ";
    private int uid = -1; 
    private boolean running = true;
    private boolean stoped = false;
    private ManagerWriter model = null;
    private Thread t = null;
    public UserConnector(Socket soc, ManagerWriter model) {
        this.soc = soc;
        this.model = model;
        t = new Thread(this);
        t.setDaemon(true);
        t.start();        
    }
    public void run () {
        BufferedReader in = null;
        PrintWriter out = null;
        InputStream is = null;
        try {
            is  = soc.getInputStream();
            in = new BufferedReader (
                new InputStreamReader(is));
            out = new PrintWriter (
                soc.getOutputStream(),true);
            ParsedInfo pInfo = XMLUtil.parser(in.readLine()); 
            uid = model.connectNewUser(pInfo.getUserName(),pInfo.getUserPass());
            String s = XMLUtil.packager("sendAll",uid,pInfo.getUserName()+"|"+pInfo.getUserPass()," ",null,model.getAllTasks(uid));
            if (log.isInfoEnabled()) {
                log.info(s);
            }
            out.println(s);  // send xml package all tasks of the client
            while (running) {
                Thread.yield();
                int i = is.available();
                if (i == 0)  {                        
                    continue;
                }
                String line = in.readLine();
                if (log.isInfoEnabled()) {
                    log.info(line);
                }
                if (line != null) {
                    ParsedInfo pInfo1 = XMLUtil.parser(line);
                    if (uid != pInfo1.getUserID()) {
                        out.println(XMLUtil.packager("error",uid,"warning|warning","User ID is wrong. You was disconnected.",null,null));
                        break;
                    }
                    if ("remove".equals(pInfo1.getCommand())) {
                            model.removeTask(Long.parseLong(pInfo1.getMessage()),pInfo1.getUserID()); 
                            out.println(line); 
                            continue;
                    }
                    if("disconnect".equals(pInfo1.getCommand())) {
                        break;
                    }
                    if ("add".equals(pInfo1.getCommand())) {
                        TaskInfo task = model.addTask(pInfo1.getTask(),pInfo1.getUserID());                                
                        out.println(XMLUtil.packager("add",uid,pInfo.getUserName()+"|"+pInfo.getUserPass()," ",task,null));
                        continue;
                    }
                    if ("edit".equals(pInfo1.getCommand())) {
                        model.editTask(pInfo1.getTask().getID(),pInfo1.getTask(),pInfo1.getUserID());
                        out.println(line);
                        continue;
                    }
                }
            }
        } catch (UserAuthFailedException e) {
            out.println(XMLUtil.packager("error",uid,"warning|warning","name or password is wrong",null,null));
        } catch (DataAccessException e) {
            out.println(XMLUtil.packager("error",uid,"error|error","error",null,null));
        } catch (IOException e1) {
            log.error(e1);
        } finally {
            try {
                if(!"".equals(exitMsg)) {
                    out.println(XMLUtil.packager("disconnect",0,"good|by","Server stoped. " + exitMsg,null,null));
                }
                if ( in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                soc.close();
            } catch (IOException e2) {
                log.error(e2);
                stoped = true;
                t = null;
            }                
                stoped = true;
                t = null;
                
        }            
    }
    /**
    * Check thread stop or not.
    */
    public boolean isStoped() {
        return stoped;
    }
    /**
    * Safe stops the UserConnector thread and disconnectd client.
    * @param msg it's message that sends to the client before disconnect.
    */
    public void stop(String msg) {
        this.exitMsg = msg;
        this.running = false;
    }
}