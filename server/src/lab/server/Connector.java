package lab.server;
import java.util.concurrent.*;
import lab.exception.*;
import java.net.*;
import java.io.*;
import lab.server.model.*;
import java.util.*;
/**
* Adds and removes new user.
*/
public class Connector implements Runnable {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Connector.class);
	private int port = 8189;
	private ServerSocket s = null;
	private ManagerWriter model = null;
	private Cleaner cl = null;
	private Thread t2 = null;
    private boolean run = true;
    private String msg ;
	public ConcurrentHashMap<Integer, UserConnector> userlist = new ConcurrentHashMap<Integer, UserConnector>();	
	/**
	* Remove disconnected user.
	*/
	private class Cleaner implements Runnable {
		private Thread t1 = null;
		public boolean run = true;
		public Cleaner() {
			t1 = new Thread(this);
			t1.setDaemon(true);
			t1.start();
		}
		public void run() {
			while(run) {
				Thread.yield();
				clear();
			}			
		}
		/**
		* Remove disconnected user.
		*/
		public synchronized void clear() {	
			Collection<UserConnector> col = userlist.values();
			for	(UserConnector con: col) {
				if(con.isStoped()) {
					if (log.isInfoEnabled()) {
						log.info("cleaner ...");
					}
					col.remove(con);
				}							
            }
		}		
	}
	/**
	* Connect new user.
	*/
	public Connector(int port) {
		this.port = port;
		t2 = new Thread(this);
		t2.setDaemon(true);
		t2.start();
		cl = new Cleaner();
	}
	public void run() {
		try {
			s = new ServerSocket(port);			           
            model = new ManagerModel();
			int i = 1;
			while (run) {
				Thread.yield();
                s.setSoTimeout(100);
                try {
                    Socket soc = s.accept();
				soc.setSoTimeout(1000);
				if (log.isInfoEnabled()){
					log.info(i+ " connected user : "+ soc.getPort());
				}
				UserConnector uc = new UserConnector(soc,model);
				userlist.put(i,uc);
                i++;
                } catch (SocketTimeoutException ex) {
                    if (!run) 
                        break;
                }
			}
        } catch (SocketException e2) {
            log.info(e2.getMessage());
		} catch (IOException e) {
			log.error(e);
		} catch (DataAccessException e1) {
			log.error(e1);
        } finally {
            try {
                if (!s.isClosed()) {
                    s.close();
                }
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
            model = null;
            Collection<UserConnector> col = userlist.values();
            for (UserConnector uc : col) {
                uc.stop(msg);
            }
           
            while ( userlist.size() != 0) {
               for (UserConnector uc : col) {
                   if(uc.isStoped()) {
                        col.remove(uc);
                    }
                }
            }
            if (log.isInfoEnabled()) {
                log.info("connector stop");
            }
        }
	}
	/**
	* Stop all user and stop cleaner after.
	* @param msg message thet will be send all user befor disconnect.
	*/
	public void stop() {
        cl.run = false;
        run = false;
	}
    public void setDisconMsg(String msg) {
        this.msg = msg;
    }
}