package lab;
import java.util.*;
/**
* class convert the data to the xml string.
*/
public class XMLPackager {
    private String xml = null;
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
            xml = sb.toString();
            return;
        }
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
        if (!ts.getInfo().equals("")) {
            sb.append(ts.getInfo());
        } else { 
            sb.append(" ");
        }
        sb.append("</info>");
        sb.append("<exec>");
        if ( ts.getExec() != null && !ts.getExec().getPath().equals(" ")) {
            sb.append(ts.getExec().getPath());
        } else {
            sb.append(" ");
        }
        sb.append("</exec>");
        sb.append("<date>");
        sb.append(ts.getDate().getTime());
        sb.append("</date>");
        sb.append("</command>");
        xml = sb.toString();
    }
    /**
    * return xml
    */
    public String getXML() {
        return xml;
    }
}