import lab.client.view.*;
import lab.*;
/**
*    Main class in TaskManager
*/
public class TaskManagerClient{
    public static void main (String[] arrg){  
        org.apache.log4j.PropertyConfigurator.configure("log\\log4jClient.properties");
        ManagerView view = new ManagerView();
        view.loadView();
    }
}