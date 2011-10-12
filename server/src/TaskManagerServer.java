import lab.server.model.*;
import lab.*;
import lab.server.*;
/**
*    Main class in TaskManager
*/
public class TaskManagerServer{	
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TaskManagerServer.class);
    public static void main (String[] arrg) {  
        org.apache.log4j.PropertyConfigurator.configure("log\\log4jServer.properties");		
        log.info(" ");     
        new ServerGUI();       
    }
}