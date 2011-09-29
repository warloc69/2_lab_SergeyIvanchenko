import lab.view.*;
import lab.*;
/**
*    Main class in TaskManager
*/
public class TaskManager{
    public static void main (String[] arrg){  
        org.apache.log4j.PropertyConfigurator.configure("log\\log4j.properties");
        ManagerView view = new ManagerView();
        view.loadView();
    }
}