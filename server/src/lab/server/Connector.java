package lab.server;
import java.util.*;
import lab.exception.*;
import java.net.*;
import java.io.*;
import lab.server.model.*;
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
	public Hashtable<Integer, UserConnector> userlist = new Hashtable<Integer, UserConnector>();	
	/**
	* Remove disconnected user.
	*/
	private class Cleaner implements Runnable {
		private Thread t1 = null;
		public boolean run = true;
		public boolean inClear = false;
		boolean stop = false;
		public Cleaner() {
			t1 = new Thread(this);
			t1.setDaemon(true);
			t1.start();
		}
		public void run() {
			while(run) {
				clear();
				try {
						t1.sleep(3000);
					} catch (InterruptedException e)  {
						log.error(e);
					}
				}
			stop = true;
		}
		/**
		* Remove disconnected user.
		*/
		public void clear() {	
			if(inClear) { 
				return;
			}
			inClear = true;
			try {
				synchronized (userlist) {			
					Collection<UserConnector> col = userlist.values();
					synchronized (col) {
						for	(UserConnector con: col) {
							synchronized (con) {
								if(con.isStoped()) {
									if (log.isInfoEnabled()) {
										log.info("cleaner ...");
									}
									col.remove(con);
								}
							}
						}
					}
				}
			} catch (ConcurrentModificationException e) {
				log.warn(e);
			}
			inClear = false;
		}
		/**
		* show stop cleaner or not.
		*/
		public boolean isStoped() {
			if (stop) {
				t1 = null;
			}
			return stop;
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
				try {
					t2.sleep(100);
				} catch (InterruptedException e)  {
					log.error(e);
				}
				Socket soc = s.accept();
				soc.setSoTimeout(10000);
				if (log.isInfoEnabled()){
					log.info(i+ " connected user : "+ soc.getPort());
				}
				UserConnector uc = new UserConnector(soc,model);
				synchronized (userlist) {
					userlist.put(i,uc);
				}
				i++;
			}
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
		Collection<UserConnector> col = userlist.values();
		for (UserConnector uc : col) {
			uc.stop(msg);
		}
		cl.run = false;
		while (userlist.size() != 0) {
			if(cl.isStoped()) {
				cl.clear();
			}
		}
		if (log.isInfoEnabled()) {
			log.info("cleaner stop");
		}
	}
}