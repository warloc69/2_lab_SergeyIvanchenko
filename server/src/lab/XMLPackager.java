package lab;
import java.util.*;
/**
* class convert the data to the xml string.
*/
public class XMLPackager {
    private String xml = null;
    public XMLPackager(Hashtable<Long,TaskInfo> tasks) {
        Collection<TaskInfo> col = tasks.values();
        StringBuffer sb = new StringBuffer();
        sb.append("<tasklist>");
        for (TaskInfo ts: col) {
            sb.append("<task>");
            sb.append("<id>");
            sb.append(ts.getID());
            sb.append("</id>");
            sb.append("<name>");
            if (!ts.getName().equals("")) {
                sb.append(ts.getName());
            } else { 
                sb.append(" ");
            }
            sb.append("</name>");
            sb.append("<info>");
            sb.append(ts.getInfo());
            sb.append("</info>");
            sb.append("<exec>");
            if ( (ts.getExec() != null) && !ts.getExec().getPath().equals(" ")) {
                sb.append(ts.getExec().getPath());
            } else {
                sb.append(" ");
            }
            sb.append("</exec>");
            sb.append("<date>");
            sb.append(ts.getDate().getTime());
            sb.append("</date>");
            sb.append("</task>");
        }
        sb.append("</tasklist>");
        xml = sb.toString();
    }
    public XMLPackager(String com, TaskInfo ts) {
        StringBuffer sb = new StringBuffer();
        sb.append("<command>");
        sb.append("<com>");
        sb.append(com);
        sb.append("</com>");
        if (com.equals("remove")) {
            sb.append("<id>");
            sb.append(ts.getID());
            sb.append("</id>");
            sb.append("</command>");
        } else {        
            sb.append("<id>");
            sb.append(ts.getID());
            sb.append("</id>");
            sb.append("<name>");
            sb.append(ts.getName());
            sb.append("</name>");
            sb.append("<info>");
            sb.append(ts.getInfo());
            sb.append("</info>");
            sb.append("<exec>");
            if ( ts.getExec() != null && !ts.getExec().getPath().equals("")) {
                sb.append(ts.getExec().getPath());
            } else {
                sb.append("null");
            }
            sb.append("</exec>");
            sb.append("<date>");
            sb.append(ts.getDate().getTime());
            sb.append("</date>");
            sb.append("</command>");
        }
        xml = sb.toString();
        
    }
    /**
    * return xml
    */
    public String getXML() {
        return xml;
    }
}