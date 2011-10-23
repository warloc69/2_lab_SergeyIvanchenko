package lab;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import lab.*;
/**
*    <pre>
*    Class convert the data to / from  the xml string.
*    <b>Protocol.</b>
*    The Server and a client sends some xml package.
*    When the client connects to the server, client send package:
*         &lt command &gt
*             &lt com &gt getAll&lt /com &gt 
*             &lt authInfo usrID="0" hash="t|s"/ &gt 
*             &lt msg &gt msg&lt /msg &gt 
*             &lt tasklist &gt 
*               &lt size &gt 0&lt /size &gt 
*             &lt /tasklist &gt 
*         &lt /command &gt 
*   The <b>hash</b> parameter contains some user name and password.
*   The <b>usrID</b> contains user identifier. This parameter helps to get necessary info of the server.
*   The server validates user name and password and sends the package with the list tasks or the  
*   error authorization user package.
*   The tasks list package example:
*          &lt command &gt 
*              &lt com &gt sendAll&lt /com &gt 
*              &lt authInfo usrID="1" hash="t|s"/ &gt 
*              &lt msg &gt msg&lt /msg &gt 
*              &lt tasklist &gt  
*                  &lt size &gt 2&lt /size &gt 
*                  &lt task &gt 
*                      &lt id &gt 1319121244236&lt /id &gt 
*                      &lt name &gt ccc&lt /name &gt 
*                      &lt info &gt  &lt /info &gt 
*                      &lt exec &gt &lt /exec &gt 
*                      &lt date &gt 1319127840000&lt /date &gt 
*                  &lt /task &gt 
*                  &lt task &gt 
*                      &lt id &gt 1319121244237&lt /id &gt 
*                      &lt name &gt zzz&lt /name &gt 
*                      &lt info &gt  &lt /info &gt  
*                      &lt exec &gt &lt /exec &gt  
*                      &lt date &gt 1319127840000&lt /date &gt 
*                  &lt /task &gt 
*              &lt /tasklist &gt 
*          &lt /command &gt 
*    The <b>size</b>  shows how many tasks into the list. 
*    The <b>com</b> contains the command, it’s can be “getAll”, “sendAll”,  “disconnect”, “add”, ”edit”, “remove”, 
*    “error”.
*    The <b>msg</b> contains the help information, for example when send command “disconnect” or “error” we can 
*    show some reason.
*    When the user disconnecting or when server is shutdown it’s one send  “disconnect” package.
*    &lt command &gt 
*    &lt com &gt disconnect&lt /com &gt 
*    &lt authInfo usrID="1" hash="t|s"/ &gt 
*    &lt msg &gt msg&lt /msg &gt 
*    &lt tasklist &gt &lt size &gt 0&lt /size &gt &lt /tasklist &gt 
*    &lt /command &gt
*    When the user send “add” or “edit” package to the server :
*    &lt command &gt 
*          &lt com &gt add&lt /com &gt  
*          &lt authInfo usrID="1" hash="t|s"/ &gt  
*          &lt msg &gt msg&lt /msg &gt 
*          &lt tasklist &gt 
*              &lt size &gt 1&lt /size &gt 
*                  &lt task &gt 
*                  &lt id &gt 0&lt /id &gt 
*                  &lt name &gt fdd&lt /name &gt 
*                  &lt info &gt  &lt /info &gt 
*                  &lt exec &gt  &lt /exec &gt 
*                  &lt date &gt 1319192940000&lt /date &gt 
*              &lt /task &gt 
*          &lt /tasklist &gt 
*    &lt /command &gt
*    Server add or edit task and send this package to back, bat if command “add” before sender the server 
*    generate task id and add to the package.
*    The remove package contains only the task id in the <b>msg</b> and user id:
*        &lt command &gt 
*            &lt com &gt remove&lt /com &gt 
*            &lt authInfo usrID="2" hash="tw|1"/ &gt 
*            &lt msg &gt 1319371627338&lt /msg &gt 
*            &lt tasklist &gt &lt size &gt 0&lt /size &gt &lt /tasklist &gt 
*        &lt /command &gt 
*    </pre>
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
    public static String packager(String com,long usrID,String hash,String msg, TaskInfo ts1, Hashtable<Long,TaskInfo> tasks) {
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
        if ("remove".equals(com) ) {
            size = 0;
        }
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
        sb.append("<" +ProtocolConst.listSize+ ">");
        sb.append(size);
        sb.append("</" +ProtocolConst.listSize+ ">");
        if (size != 0) {
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
        }
        sb.append("</" +ProtocolConst.list+ ">");
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