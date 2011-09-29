import lab.model.*;
import lab.*;
import lab.exception.*;
import java.net.*;
import java.io.*;
/**
*    Main class in TaskManager
*/
public class TaskManager{
    public static void main (String[] arrg){  
        org.apache.log4j.PropertyConfigurator.configure("log\\log4j.properties");
        try {
            ServerSocket s = new ServerSocket(8189);
            int i = 1;
            while (true) {
                Socket soc = s.accept();
                new UserConnector(soc,i);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}