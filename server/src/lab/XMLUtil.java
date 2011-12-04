package lab;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import lab.*;
/**
*    <pre>
*    Class convert the data to / from  the xml string.
*/
public class XMLUtil {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(XMLUtil.class);
    /**
    * Create xml packager.
    * @param com it's command id. Can be : “getAll”, “sendAll”,  “disconnect”, “add”, ”edit”, “remove”, 
    * “error”.
    * @param usrID it's user identifier, generate on the server.
    * @param hash it's username and password string. 
    * @param msg can keeps some information.
    * @param ts1 it's task that keep in the packege, can be null.
    * @param tasks if we transfers some tasks we writes they into this Hashtable, can be null;
    * @return xml string.
    */
    public static String packager(String com,long usrID,String userName,String hashPass, String msg, TaskInfo ts1, Hashtable<Long,TaskInfo> tasks) {
        String xml = null;
        Integer size = 0;
        Collection<TaskInfo> col = null;
        if ( (tasks == null) && (ts1 != null) ) {
            tasks = new Hashtable<Long,TaskInfo>();
            tasks.put(1l,ts1);
            size = tasks.size();
        }
        if (tasks != null) {
            size = tasks.size();
            col = tasks.values();
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<" +ProtocolConst.rootTeg+ ">");
        sb.append("<" +ProtocolConst.com+ ">");
        sb.append(com);
        sb.append("</" +ProtocolConst.com+ ">");
        if (userName != null && hashPass != null) {
            sb.append("<authInfo usrID=\"" + usrID);
            sb.append("\" userName=\""+ userName);
            sb.append("\" hashPass=\""+hashPass+"\"/>");
        } else {
            sb.append("<authInfo usrID=\"" + usrID);
            sb.append("\" userName=\" ");
            sb.append("\" hashPass=\" \"/>");
        }
        if (("disconnect" .equals(com)&& (msg != null)) || "error".equals(com)) {
            sb.append("<" +ProtocolConst.msg+ ">");
            sb.append(msg);
            sb.append("</" +ProtocolConst.msg+ ">");
        } else { 
            sb.append("<" +ProtocolConst.msg+ "/>");
        }
        if ("sendAll" .equals(com) || "remove".equals(com) || "add".equals(com) || "edit".equals(com)) { 
            sb.append("<" +ProtocolConst.list+ ">");
            sb.append("<" +ProtocolConst.listSize+ ">");
            sb.append(size);
            sb.append("</" +ProtocolConst.listSize+ ">");
            if (size != 0) {
                for (TaskInfo ts: col) {
                    sb.append("<" +ProtocolConst.listElement+ ">");
                    sb.append("<"+ProtocolConst.elementID+">");
                    sb.append(ts.getID());
                    sb.append("</"+ProtocolConst.elementID+">");
                    if (!ts.getName().equals(" ")) {
                        sb.append("<"+ProtocolConst.elementName+">");
                        sb.append(ts.getName());
                        sb.append("</"+ProtocolConst.elementName+">");
                     } else { 
                        sb.append("<"+ProtocolConst.elementName+"/>");
                    }
                    if (!ts.getInfo().equals(" ")) {
                        sb.append("<"+ProtocolConst.elementInfo+">");
                        sb.append(ts.getInfo());
                        sb.append("</"+ProtocolConst.elementInfo+">");
                    } else { 
                        sb.append("<"+ProtocolConst.elementInfo+"/>");
                    }
                    if ( ts.getExec() != null && !ts.getExec().getPath().equals(" ")) {
                        sb.append("<"+ProtocolConst.runProgram+">");                    
                        sb.append(ts.getExec().getPath());                    
                        sb.append("</"+ProtocolConst.runProgram+">");
                    } else {
                        sb.append("<"+ProtocolConst.runProgram+"/>");
                    }
                    if (!"remove" .equals(com)) { 
                        sb.append("<"+ProtocolConst.elementDate+">");                    
                        sb.append(ts.getDate().getTime());
                        sb.append("</"+ProtocolConst.elementDate+">");
                    } else { 
                         sb.append("<"+ProtocolConst.elementDate+"/>");
                    }
                    sb.append("</" +ProtocolConst.listElement+ ">");
                }
            }
            sb.append("</" +ProtocolConst.list+ ">");
        } else {
            sb.append("<" +ProtocolConst.list+ "/>");
        }
        sb.append("</" +ProtocolConst.rootTeg+ ">");
        xml = sb.toString();
        log.info("packager :" + xml);
        return xml;
    }
    /**
    * Parsed xml package.
    * @param xml it's xml package.
    * @return ParsedInfo container.
    */
    public static ParsedInfo parser (String xml) {
        ParsedInfo pInfo = new ParsedInfo();
        Document doc = null;
        log.info("parser : " +xml);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
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
                        if (textNode1 != null) {
                            String text1 = textNode1.getData().trim();
                            pInfo.setMessage(text1);
                        }
                    }
                    if (ch1.getTagName().equals("authInfo")) {
                        pInfo.setUserID(Integer.parseInt(ch1.getAttribute("usrID")));
                        pInfo.setUserName(ch1.getAttribute("userName"));
                        pInfo.setUserPass(ch1.getAttribute("hashPass"));
                    }
                    if (ch1.getTagName().equals(ProtocolConst.list)) {
                        NodeList tchildren = ch1.getChildNodes();                
                        for (int k = 0; k < tchildren.getLength(); k++) {
                            Node tchild = tchildren.item(k);
                            if (tchild instanceof Element) {
                                Element ch = (Element)tchild;
                                if (ch.getTagName().equals(ProtocolConst.listSize)) {
                                    Text textNode = (Text)ch.getFirstChild();
                                    String text = textNode.getData().trim();
                                    pInfo.setSize(Integer.parseInt(text));
                                    continue;
                                }
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
                                            if (textNode != null) {
                                                String text = textNode.getData().trim();
                                                task.setName(text);
                                            }
                                        }
                                            if (taskNodeElement.getTagName().equals(ProtocolConst.elementInfo)) {
                                                Text textNode = (Text)taskNodeElement.getFirstChild();
                                                if (textNode != null) {
                                                    String text = textNode.getData().trim();
                                                    task.setInfo(text);
                                                }
                                            }
                                            if (taskNodeElement.getTagName().equals(ProtocolConst.runProgram)) {
                                                Text textNode = (Text)taskNodeElement.getFirstChild();
                                                if (textNode != null) {
                                                    String text = textNode.getData().trim();
                                                    task.setExec(new File(text));
                                                } else {
                                                    task.setExec(new File(" "));
                                                }
                                            }
                                        if (taskNodeElement.getTagName().equals(ProtocolConst.elementDate)) {
                                            Text textNode = (Text)taskNodeElement.getFirstChild();
                                            if (textNode != null) {
                                                String text = textNode.getData().trim();
                                                task.setDate(new Date(Long.parseLong(text)));
                                            }
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