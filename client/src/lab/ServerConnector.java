package lab;
import java.net.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;
import java.net.*;
/**
* class get all task from server.
*/
public class ServerConnector {
    public static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerConnector.class);
    private String xml = null;
    /**
    * @param in - socket in stream.
    * @param out - socket out stream.
    * @param hash - table name on the server.
    */
    public ServerConnector (BufferedReader in, PrintWriter out, String hash) {
        try {
            log.info(in.readLine());
            out.println(hash);
            xml = in.readLine();
        } catch (IOException e) {
            log.error(e);            
        }
    }
    /**
    * return all task from xml.
    */
    public Hashtable<Long,TaskInfo> parseXML() {
        Hashtable<Long,TaskInfo> taskMap = new Hashtable<Long,TaskInfo>();
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            log.error(e);
        }
            Element root = doc.getDocumentElement();            
            if (root.getTagName().equals("tasklist")) {
                NodeList children = root.getChildNodes();                
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child instanceof Element) {
                        Element ch = (Element)child;
                        NodeList tsList = ch.getChildNodes();
                        TaskInfo task = new TaskInfoImpl();
                        for (int j = 0; j < tsList.getLength(); j++) {
                            Node taskNode = tsList.item(j);                            
                            if (taskNode instanceof Element) {
                                Element taskNodeElement = (Element)taskNode;                                
                                    if (taskNodeElement.getTagName().equals("id")) {
                                        Text textNode = (Text)taskNodeElement.getFirstChild();
                                        String text = textNode.getData().trim();
                                        task.setID(Long.parseLong(text));
                                    }
                            
                                if (taskNodeElement.getTagName().equals("name")) {
                                    Text textNode = (Text)taskNodeElement.getFirstChild();
                                    String text = textNode.getData().trim();
                                    task.setName(text);
                                }
                                try {
                                    if (taskNodeElement.getTagName().equals("info")) {
                                        Text textNode = (Text)taskNodeElement.getFirstChild();
                                        String text = textNode.getData().trim();
                                        task.setInfo(text);
                                    }
                                } catch (NullPointerException e) {
                                    task.setInfo(" ");
                                }
                                try {
                                    if (taskNodeElement.getTagName().equals("exec")) {
                                        Text textNode = (Text)taskNodeElement.getFirstChild();
                                        String text = textNode.getData().trim();
                                        task.setExec(new File(text));
                                    }
                                } catch (NullPointerException e) {
                                    task.setExec(new File(" "));
                                }
                                if (taskNodeElement.getTagName().equals("date")) {
                                    Text textNode = (Text)taskNodeElement.getFirstChild();
                                    String text = textNode.getData().trim();
                                    task.setDate(new Date(Long.parseLong(text)));
                                }
                            }
                        }
                        taskMap.put(task.getID(),task);
                        
                    }
                }
            }
        return taskMap;
    }
}