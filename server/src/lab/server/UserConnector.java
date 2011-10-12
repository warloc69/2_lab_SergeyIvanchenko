package lab.server;
import java.net.*;
import java.io.*;
import lab.server.model.*;
import lab.exception.*;
import lab.*;
/**
* connect new user to the server
*/
public class UserConnector implements Runnable {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(UserConnector.class);
    private Socket soc = null;
    private int userCount = 0;
    private String tableName = null;
    private int uid = -1; 
    private String hash = "tasks";
	private boolean running = true;
    private String msg = "msg";
    private ManagerModel model = null;
	private PrintWriter out = null;
	private Thread t = null;
    public UserConnector(Socket soc, int userCount, ManagerModel model) {
        this.soc = soc;
        this.userCount = userCount;
		this.model = model;
        t = new Thread(this);
		t.start();
		
    }
    public void run () {
            BufferedReader in = null;
            PrintWriter out = null;
            String line = null;
            try {
                in = new BufferedReader (
                    new InputStreamReader(soc.getInputStream()));
                out = new PrintWriter (
                    soc.getOutputStream(),true);
				this.out = out;
            } catch (IOException e) {
            }            
            try {
                ParsedInfo pInfo = XMLUtil.parser(in.readLine());  
                uid = model.connectNewUser(pInfo.getUserName(),pInfo.getUserPass());
                String s = XMLUtil.packager("sendAll",uid,pInfo.getUserName()+"|"+pInfo.getUserPass(),msg,null,model.getAllTasks(uid));
                //log.info(s);
                out.println(s);   // send xml package all tasks of the client
				int  timecount = 0;
				while (running) {
					try {
						line = in.readLine();
					} catch (InterruptedIOException e) {						
						if (timecount > 20) {
							log.info("disconnected, timee out, user " + soc);
							out.println(XMLUtil.packager("disconnect",0,"good|by","are you sleep? good by, you was disconnected",new TaskInfoImpl(),null));
							running = false;
							break;
						}
						timecount++;
						try {
								t.sleep(100);
						} catch (InterruptedException e1)  {
								log.error(e1);
						}
						continue;
					}
					timecount = 0;
					if (line != null) {
						//log.info(line);
						ParsedInfo pInfo1 = XMLUtil.parser(line);
						if (pInfo1.getCommand().equals("remove")) {
								model.removeTask(pInfo1.getTask().getID(),pInfo1.getUserID()); 
								out.println(line); 
								continue;
						}
						if(pInfo1.getCommand().equals("disconnect")) {
							running = false;
							soc.close();
							return;
						}
						if (pInfo1.getCommand().equals("add")) {
								model.addTask(pInfo1.getTask(),pInfo1.getUserID());
								out.println(line);
								continue;
						}
						if (pInfo1.getCommand().equals("edit")) {
								model.editTask(pInfo1.getTask().getID(),pInfo1.getTask(),pInfo1.getUserID());
								out.println(line);
								continue;
						}
					}
				}
				running = false;
				soc.close();
        } catch (DataAccessException e) {
				running = false;
                log.error(e);
                try { 
					running = false;
                    out.println(XMLUtil.packager("error",uid,"error","error",new TaskInfoImpl(),null));
                    soc.close();
                } catch (IOException e2) {
					running = false;
					log.error(e2);
                }
        } catch (IOException e1) {
            try {	
					log.error(e1);
					running = false;
                    soc.close();
                } catch (IOException e2) {
					log.error(e2);
					running = false;
                }
        }
    }
	public PrintWriter getOutStream() {
		return this.out;
	}
	public boolean isStoped() {
		return running;
	}
}