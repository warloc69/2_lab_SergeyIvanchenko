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
	public ConcurrentHashMap<Integer, UserConnector> userlist = new ConcurrentHashMap<Integer, UserConnector>();	
	/**
	* Remove disconnected user.
	*/
	private class Cleaner implements Runnable {
		private Thread t1 = null;
		public boolean run = true;
		public boolean inClear = false;
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
			while (true) {
				Thread.yield();
				Socket soc = s.accept();
				soc.setSoTimeout(10000);
				if (log.isInfoEnabled()){
					log.info(i+ " connected user : "+ soc.getPort());
				}
				UserConnector uc = new UserConnector(soc,model);
				userlist.put(i,uc);
                i++;
			}
        } catch (SocketException e2) {
            log.info(e2.getMessage());
		} catch (IOException e) {
			log.error(e);
		} catch (DataAccessException e1) {
			log.error(e1);
        }
	}
	/**
	* Stop all user and stop cleaner after.
	* @param msg message thet will be send all user befor disconnect.
	*/
	public void stop(String msg) {
        cl.run = false;
		Collection<UserConnector> col = userlist.values();
		for (UserConnector uc : col) {
			uc.stop(msg);
		}
		userlist.clear();
		try {
            if (!s.isClosed()) {
                s.close();
            }
		} catch (IOException e) {
			log.warn(e.getMessage());
		}
		if (log.isInfoEnabled()) {
			log.info("cleaner stop");
		}
	}
}