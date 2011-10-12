package lab;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import lab.*;
/**
* class convert the data to / from  the xml string.
*/
public class XMLUtil {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(XMLUtil.class);
	/**
	* return xml
    */
    public static String packager(String com,long usrID,String hash,String msg, TaskInfo ts1, Hashtable<Long,TaskInfo> tasks) {
        String xml = null;
		if ( tasks == null) {
            tasks = new Hashtable<Long,TaskInfo>();
            tasks.put(1l,ts1);
        }
        Collection<TaskInfo> col = tasks.values();
        StringBuffer sb = new StringBuffer();
        sb.append("<" +ProtocolConst.rootTeg+ ">");
        sb.append("<" +ProtocolConst.com+ ">");
        sb.append(com);
        sb.append("</" +ProtocolConst.com+ ">");
        sb.append("<authInfo usrID=\"" + usrID);
        sb.append("\" hash=\""+hash);
        sb.append("\"/>");
        sb.append("<" +ProtocolConst.msg+ ">");
        sb.append(msg);
        sb.append("</" +ProtocolConst.msg+ ">");
        sb.append("<" +ProtocolConst.list+ ">");
        for (TaskInfo ts: col) {
            sb.append("<" +ProtocolConst.listElement+ ">");
            sb.append("<"+ProtocolConst.elementID+">");
            sb.append(ts.getID());
            sb.append("</"+ProtocolConst.elementID+">");
            sb.append("<"+ProtocolConst.elementName+">");
            if (!ts.getName().equals("")) {
                sb.append(ts.getName());
            } else { 
                sb.append(" ");
            }
            sb.append("</"+ProtocolConst.elementName+">");
            sb.append("<"+ProtocolConst.elementInfo+">");
            if (!ts.getInfo().equals("")) {
                sb.append(ts.getInfo());
            } else { 
                sb.append(" ");
            }
            sb.append("</"+ProtocolConst.elementInfo+">");
            sb.append("<"+ProtocolConst.runProgram+">");
            if ( ts.getExec() != null && !ts.getExec().getPath().equals(" ")) {
                sb.append(ts.getExec().getPath());
            } else {
                sb.append(" ");
            }
            sb.append("</"+ProtocolConst.runProgram+">");
            sb.append("<"+ProtocolConst.elementDate+">");
            sb.append(ts.getDate().getTime());
            sb.append("</"+ProtocolConst.elementDate+">");
            sb.append("</" +ProtocolConst.listElement+ ">");
        }
        sb.append("</" +ProtocolConst.list+ ">");
        sb.append("</" +ProtocolConst.rootTeg+ ">");
        xml = sb.toString();
		return xml;
    }
	public static ParsedInfo parser (String xml) {
	    ParsedInfo pInfo = new ParsedInfo();
		Document doc = null;
        //log.info(xml);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            log.error(e);
        }
            Element root = doc.getDocumentElement();
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    Element ch1 = (Element)child;
                    if (ch1.getTagName().equals(ProtocolConst.com)) {
                        Text textNode1 = (Text)ch1.getFirstChild();
                        String text1 = textNode1.getData().trim();
                        pInfo.setCommand(text1);
                    }
                    if (ch1.getTagName().equals(ProtocolConst.msg)) {
                        Text textNode1 = (Text)ch1.getFirstChild();
                        String text1 = textNode1.getData().trim();
                        pInfo.setMessage(text1);
                    }
                    if (ch1.getTagName().equals("authInfo")) {
                       pInfo.setUserID(Integer.parseInt(ch1.getAttribute("usrID")));
                       StringTokenizer st = new StringTokenizer(ch1.getAttribute("hash"),"|");
                        pInfo.setUserName(st.nextToken());
                        pInfo.setUserPass(st.nextToken());
                    }
                    if (ch1.getTagName().equals(ProtocolConst.list)) {
                        NodeList tchildren = ch1.getChildNodes();                
                        for (int k = 0; k < tchildren.getLength(); k++) {
                            Node tchild = tchildren.item(k);
                            if (tchild instanceof Element) {
                                Element ch = (Element)tchild;
                                NodeList tsList = ch.getChildNodes();
                                TaskInfo task = new TaskInfoImpl();
                                for (int j = 0; j < tsList.getLength(); j++) {
                                    Node taskNode = tsList.item(j);                            
                                    if (taskNode instanceof Element) {
                                        Element taskNodeElement = (Element)taskNode;                                
                                            if (taskNodeElement.getTagName().equals(ProtocolConst.elementID)) {
                                                Text textNode = (Text)taskNodeElement.getFirstChild();
                                                String text = textNode.getData().trim();
                                                task.setID(Long.parseLong(text));
                                            }
                                    
                                        if (taskNodeElement.getTagName().equals(ProtocolConst.elementName)) {
                                            Text textNode = (Text)taskNodeElement.getFirstChild();
                                            String text = textNode.getData().trim();
                                            task.setName(text);
                                        }
                                        try {
                                            if (taskNodeElement.getTagName().equals(ProtocolConst.elementInfo)) {
                                                Text textNode = (Text)taskNodeElement.getFirstChild();
                                                String text = textNode.getData().trim();
                                                task.setInfo(text);
                                            }
                                        } catch (NullPointerException e) {
                                            task.setInfo(" ");
                                        }
                                        try {
                                            if (taskNodeElement.getTagName().equals(ProtocolConst.runProgram)) {
                                                Text textNode = (Text)taskNodeElement.getFirstChild();
                                                String text = textNode.getData().trim();
                                                task.setExec(new File(text));
                                            }
                                        } catch (NullPointerException e) {
                                            task.setExec(new File(" "));
                                        }
                                        if (taskNodeElement.getTagName().equals(ProtocolConst.elementDate)) {
                                            Text textNode = (Text)taskNodeElement.getFirstChild();
                                            String text = textNode.getData().trim();
                                            task.setDate(new Date(Long.parseLong(text)));
                                        }
                                    }
                                }
                                pInfo.setTask(task);
                            }
                        }
                    }
                }
            }
        return pInfo;
    }
}