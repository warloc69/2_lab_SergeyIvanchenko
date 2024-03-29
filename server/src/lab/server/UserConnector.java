package lab.server;
import java.net.*;
import java.io.*;
import lab.server.model.*;
import lab.exception.*;
import lab.*;
import java.util.*;
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
        int pingTimeout = 10000;
        DataInputStream in = null;
        DataOutputStream out = null;
        InputStream is = null;
        try {
             is  = soc.getInputStream();
             in = new DataInputStream(is);
             out = new DataOutputStream(soc.getOutputStream());
            ParsedInfo pInfo = XMLUtil.parser(in.readUTF()); 
            uid = model.connectNewUser(pInfo.getUserName(),pInfo.getUserPass());
            String s = XMLUtil.packager("sendAll",uid,pInfo.getUserName(),pInfo.getUserPass(),null,null,model.getAllTasks(uid));
            if (log.isInfoEnabled()) {
                log.info(s);
            }
            out.writeUTF(s);  // send xml package all tasks of the client
            Long pingTime = System.currentTimeMillis();
            while (running) {
                Thread.sleep(50);
                if (t.isInterrupted()) {
                    throw new InterruptedException();
                }
                int i = is.available();
                if (i == 0)  {                    
                    if (System.currentTimeMillis()-pingTime > pingTimeout) {
                        log.info("ping time out");
                        exitMsg = "timeOut";
                        break;
                    } else {
                        continue;
                    }
                }
                String line = in.readUTF();
                if (line != null) {
                    ParsedInfo pInfo1 = XMLUtil.parser(line);
                    if (!"ping".equals(pInfo1.getCommand())) {
                        if (uid != pInfo1.getUserID()) {
                            out.writeUTF(XMLUtil.packager("error",uid,null,null,"User ID is wrong. You was disconnected.",null,null));
                            break;
                        }
                    } else {
                        pingTimeout = pInfo1.getPingTimeOut()*1000;
                        pingTime = System.currentTimeMillis();
                        out.writeUTF(line);
                        continue;
                    }
                    if ("remove".equals(pInfo1.getCommand())) {
                        model.removeTask(pInfo1.getTask().getID(),pInfo1.getUserID()); 
                        out.writeUTF(line); 
                        continue;
                    }
                    if("disconnect".equals(pInfo1.getCommand())) {
                        break;
                    }
                    if ("add".equals(pInfo1.getCommand())) {
                        TaskInfo task = model.addTask(pInfo1.getTask(),pInfo1.getUserID());                                
                        out.writeUTF(XMLUtil.packager("add",uid,pInfo.getUserName(),pInfo.getUserPass(),null,task,null));
                        continue;
                    }
                    if ("edit".equals(pInfo1.getCommand())) {
                        model.editTask(pInfo1.getTask().getID(),pInfo1.getTask(),pInfo1.getUserID());
                        out.writeUTF(line);
                        continue;
                    }
                }
            }
        } catch (InterruptedException e) {
            log.info("user wrapper is stoped");
        } catch (UserAuthFailedException e) {
            try {
                out.writeUTF(XMLUtil.packager("error",uid,null,null,"name or password is wrong ",null,null));
                exitMsg = "AuthFailed";
            } catch (IOException e1) {
                log.error(e1);
            } 
        } catch (DataAccessException e) {
            try{
                out.writeUTF(XMLUtil.packager("error",uid,null,null,"Server error",null,null));
            } catch (IOException e1) {
                log.error(e1);
            } 
        } catch (IOException e1) {
            log.error(e1);
        } finally {
            try {
                if (!"AuthFailed".equals(exitMsg)) {
                    if(!"timeOut".equals(exitMsg)) {
                        if(!"".equals(exitMsg)) {
                            out.writeUTF(XMLUtil.packager("disconnect",0,null,null,"Server stoped. " + exitMsg,null,null));
                        }
                    }
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