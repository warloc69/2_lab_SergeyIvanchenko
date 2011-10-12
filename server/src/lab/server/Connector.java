package lab.server;
import java.util.*;
import lab.exception.*;
import java.net.*;
import java.io.*;
import lab.server.model.*;
public class Connector implements Runnable {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Connector.class);
	public static Integer port = 8189;
	public static Hashtable<Integer, UserConnector> userlist = new Hashtable<Integer, UserConnector>();
	private Thread t2 = null;
	private class Cleaner implements Runnable {
		Thread t1 = null;
		public Cleaner() {
			t1 = new Thread(this);
			t1.start();
		}
		public void run() {
			while(true) {
				Collection<UserConnector> col = Connector.userlist.values();
				for	(Integer i = 1; i <= Connector.userlist.size();i++) {
					if (Connector.userlist.containsKey(i)) { 
						if(!Connector.userlist.get(i).isStoped()) {
							log.info("cleaner "+userlist.remove(i));						
						}
					}
				}
				try {
					t1.sleep(500);
				} catch (InterruptedException e)  {
					log.error(e);
				}
				
			}
		}
	}
	public Connector() {
		t2 = new Thread(this);
		t2.setDaemon(true);
		t2.start();
		new Cleaner();
	}
	public void run() {
		try {
			ServerSocket s = new ServerSocket(Connector.port);			           
            ManagerModel model = new ManagerModel();
			int i = 1;
			while (true) {
				try {
					t2.sleep(100);
				} catch (InterruptedException e)  {
					log.error(e);
				}
				Socket soc = s.accept();
				soc.setSoTimeout(10000);
				log.info(i+ " connected user : "+ soc.getPort()); 
				userlist.put(i,new UserConnector(soc,i,model));
				i++;
			}
		} catch (IOException e) {
			log.error(e);
		} catch (DataAccessException e1) {
			log.error(e1);
		}
	}
}