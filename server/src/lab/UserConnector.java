package lab;
import java.net.*;
import java.io.*;
import lab.model.*;
import lab.exception.*;
/**
* connect new user to the server
*/
public class UserConnector implements Runnable {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(UserConnector.class);
    private Socket soc = null;
    private int userCount = 0;
    private String tableName = null;
    ManagerModel model = null;
    public UserConnector(Socket soc, int userCount) {
        this.soc = soc;
        this.userCount = userCount;
        new Thread(this).start();
    }
    public void run () {
            BufferedReader in = null;
            PrintWriter out = null;
            String line = null;
            XMLParser parser = null;
            try {
                in = new BufferedReader (
                    new InputStreamReader(soc.getInputStream()));
                out = new PrintWriter (
                    soc.getOutputStream(),true);                
                out.println("welcom");
            } catch (IOException e) {
            }            
            try {
                tableName = in.readLine();
                ModelConst.userlist.put(soc,tableName);
                model = new ManagerModel(tableName);
                out.println(new XMLPackager(model.getAllTasks()).getXML());   // send xml package all tasks of the client
            while ((line = in.readLine()) != null) {                
                log.info(line);                
                parser = new XMLParser(line);
                if (parser.getCommand().equals("remove")) {    
                        model.removeTask(parser.getTaskId(),tableName);
                        out.println(new XMLPackager("remove",parser.getTask()).getXML()); 
                        continue;
                }
                if (parser.getCommand().equals("add")) {
                        model.addTask(parser.getTask(),tableName);
                        out.println(new XMLPackager("add",parser.getTask()).getXML());
                        continue;
                }
                if (parser.getCommand().equals("edit")) {
                        model.editTask(parser.getTaskId(),parser.getTask(),tableName);
                        out.println(new XMLPackager("edit",parser.getTask()).getXML());
                        continue;
                }
            }
            soc.close();
        } catch (DataAccessException e) {
                e.printStackTrace();
                try { 
                    out.println(new XMLPackager("error",parser.getTask()).getXML());
                    ModelConst.userlist.remove(soc);
                    soc.close();
                } catch (IOException e2) {
                }
        } catch (IOException e1) {
            try {    
                    ModelConst.userlist.remove(soc);
                    soc.close();
                } catch (IOException e2) {
                }
        }
    }
}