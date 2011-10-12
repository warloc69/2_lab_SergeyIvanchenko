package lab.client;

import java.net.*;
import lab.*;
import lab.exception.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.swing.JOptionPane.*;
import lab.client.view.*;

public class ServerConnector extends JDialog {
	public static final long serialVersionUID = 124322332l;
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerConnector.class);
    private PrintWriter out = null;
    private BufferedReader in = null;
    private TaskInfo task = null;	
    private Socket s = null;
	private ManagerView mv = null;
	private Commander com = null;
	 /**
    * Class get command from the server end change clien info.
    */
    private class Commander implements Runnable {
        private Thread thread = null;
        public boolean stop = true;
        public Commander() {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
        /**
        *    Create new thread, look through the comman. 
        */
        public void run() {
            while (stop) {    
                try {
                    thread.sleep(1000);
                } catch (InterruptedException e) {}
                try {
                    if( in == null) {
                        continue;
                    }
                    String s = in.readLine();
                    log.info("commander : "+s);
					if (s == null) {
						continue;
					}
                    ParsedInfo pars = XMLUtil.parser(s);
                    String com = pars.getCommand();
					if (com.equals("sendAll")) {					
						mv.notifyGetAll(pars);
					}
					if (com.equals("disconnect")) {
						JOptionPane.showMessageDialog(mv,pars.getMessage(),pars.getCommand(),JOptionPane.ERROR_MESSAGE);
						mv.notifyError();
					}
                    if (com.equals("add")) {
                        mv.notifyAdd(pars.getTask());
                    }
                    if (com.equals("edit")) {
                        mv.notifyEdit(pars.getTask());
                    }
                    if (com.equals("remove")) {
                        mv.notifyRemove(pars.getTask().getID());
                    }
                    if (com.equals("error")) {
                        JOptionPane.showMessageDialog(mv,"Server error","ERROR",JOptionPane.ERROR_MESSAGE);
						mv.notifyError();
                    }
                } catch (IOException e) {
                    log.error(e);
                if (!s.isClosed()) {
                    JOptionPane.showMessageDialog(mv,"Server error","ERROR",JOptionPane.ERROR_MESSAGE);
					mv.notifyError();
                }
                
                }
            }
        }
    }
	
	public ServerConnector(ManagerView mv) {
		super(mv);
		this.mv = mv;
		connectionWindow();
	}
	public void startCommander() {
		com = new Commander();
	}
	public void stopCommander() {
		try {
			if (out != null) {
				out.println(XMLUtil.packager("disconnect",ViewVariable.uid,ViewVariable.hash,"msg", new TaskInfoImpl(), null));
				s.close();
			}
		} catch (IOException e) {
			log.error(e);
		}
		com.stop = false;
		com = null;
	}
	/**
    * Show conection dialog and connect to the server.
    */
    public void connectionWindow() {
        int W = 800;
        int H = 300;
        Box boxName = Box.createHorizontalBox();
        Box boxPassword = Box.createHorizontalBox();
        Box boxButton = Box.createHorizontalBox();
        Box allBoxes = Box.createVerticalBox();
        setSize(W/2,H/2);
        setBounds(W/4,H/4,W/2,H/2);
        final JLabel lname = new JLabel("Name:");
        boxName.add(lname);
        //----- JTextField
        final JTextField tname = new JTextField();
        tname.setToolTipText("Writes user name here");		
        tname.setMaximumSize(new Dimension(getSize().height,30));        
        boxName.add(tname);
        //----- JTextArea Info   
        final JLabel lPass = new JLabel("Password:"); 
        final JTextField tPass = new JTextField();
        tPass.setMaximumSize(new Dimension(getSize().height,30));  
        tPass.setToolTipText("Writes password here");
		StringTokenizer st = new StringTokenizer(ViewVariable.hash,"|");
		tname.setText(st.nextToken());
		tPass.setText(st.nextToken());
        boxPassword.add(lPass);
        boxPassword.add(tPass);
        JButton save = new JButton("Connect");
        save.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
				try {
                    String name = tname.getText();
                    String pass = tPass.getText();
                    if ("".equals(name) || "".equals(pass)) {
                        JOptionPane.showMessageDialog(ServerConnector.this, "name or password wrong","Warning",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Socket s = new Socket(ViewVariable.ip,ViewVariable.port);
                    ServerConnector.this.s = s;
                    in = new BufferedReader (
                        new InputStreamReader(s.getInputStream()));
                    out = new PrintWriter (
                        s.getOutputStream(),true);
					ViewVariable.hash = name+"|"+pass;
					out.println(XMLUtil.packager("getAll",0,ViewVariable.hash,"msg", new TaskInfoImpl(), null));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ServerConnector.this, "Server connect error","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dispose();
            }
        });
        save.setToolTipText("Connect to the server");
        setTitle("Connection");
        boxButton.add(save);
        allBoxes.add(boxName);
        allBoxes.add(boxPassword);
        allBoxes.add(boxButton);
        add(allBoxes);
        setVisible(true);
    }
   
	  /**
    * return socket Out stream.
    */
   public PrintWriter getOut () {
    return this.out;
   }
   /**
    * return socket In stream.
    */
   public BufferedReader getIn() {
    return in;
   }
   /**
    * return socket.
    */
   public Socket getSocket() {
    return s;
   }
}