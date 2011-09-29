package lab;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;
/**
* Class parse the xml string on the parts.
*/
public class XMLParser {
    private String xml = null;
    private Document doc = null;
    private String command = null;
    private TaskInfo task = null;
    private String tableName = null;
    private long taskID = 0L;
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(XMLParser.class);
    public XMLParser (String xml) {
        this.xml = xml;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
            Element root = doc.getDocumentElement();
            NodeList children = root.getChildNodes();
            task = new TaskInfoImpl();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    Element ch = (Element)child;
                    Text textNode = (Text)ch.getFirstChild();
                    String text = textNode.getData().trim();
                    if (ch.getTagName().equals("com")) {
                        command = text;                        
                    }
                    if (ch.getTagName().equals("hash")) {
                        tableName = text;
                    }
                    if (ch.getTagName().equals("id")) {
                        taskID = Long.parseLong(text);
                        task.setID(taskID);
                    }
                    if (ch.getTagName().equals("name")) {                        
                        task.setName(text);
                    }
                    if (ch.getTagName().equals("info")) {
                        task.setInfo(text);
                    }
                    if (ch.getTagName().equals("exec")) {
                        task.setExec(new File(text));
                    }
                    if (ch.getTagName().equals("date")) {
                        task.setDate(new Date(Long.parseLong(text)));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
    /**
    * return command from the xml.
    */
    public String getCommand() {
        return command;
    }
    /**
    * return task from the xml.
    */
    public TaskInfo getTask() {
        return task;
    }
    /**
    * return task ID from the xml.
    */
    public long getTaskId() {
        return taskID;
    }
}