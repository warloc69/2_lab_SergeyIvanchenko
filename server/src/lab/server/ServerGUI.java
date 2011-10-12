package lab.server;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import org.apache.log4j.*;
import lab.exception.*;
import java.util.*;
import lab.*;
import java.text.*;
public class ServerGUI extends AppenderSkeleton {	
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerGUI.class);
    JTextArea tname = null;
    GUI gui = null;
    public ServerGUI () {
    }
    private class GUI extends JFrame{	
        public static final long serialVersionUID = 123312452l;		
		private Connector con = null;
		public GUI () {
            super();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            showGUI();
        }
        public void showGUI () {
			try {
				Container cGui = getContentPane();
				Box allBoxes = Box.createVerticalBox();
				cGui.add(allBoxes);
				tname = new JTextArea();
				JScrollPane scr = new JScrollPane(tname);
				allBoxes.add(scr);
				add(allBoxes);
				setSize(800,600);
				Box boxButton = Box.createHorizontalBox();
				final JButton stopServer = new JButton("Stop");
				final JButton startServer = new JButton("Start");
				MaskFormatter formP = new MaskFormatter("####");			
				final JFormattedTextField port = new JFormattedTextField(formP);
				port.setMaximumSize(new Dimension(50,30));
				JLabel lPort = new JLabel("Port:");
				port.setValue(Connector.port.toString());
				startServer.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							Connector.port = Integer.parseInt(((String)port.getValue()));
							con  = new Connector();
							stopServer.setEnabled(true);
							startServer.setEnabled(false);
						}
					}
				);
			   stopServer.setEnabled(false);
				stopServer.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							stopServer.setEnabled(false);
							startServer.setEnabled(true);
							String s = JOptionPane.showInputDialog(GUI.this,"server kill","kill",JOptionPane.QUESTION_MESSAGE);
							Collection<UserConnector> col = Connector.userlist.values();
							for	(UserConnector uc : col) {
								uc.getOutStream().println(
									XMLUtil.packager("disconnect",0,"good|by","Server stoped. " + s,new TaskInfoImpl(),null));
							}
							log.info(s);
							con = null;
							System.exit(0);
						}
					}
				);
				boxButton.add(startServer);
				boxButton.add(stopServer);
				boxButton.add(lPort);
				boxButton.add(port);
				allBoxes.add(boxButton);
				setVisible(true);
			} catch (ParseException e) {
			log.warn(e);
			}
        }
    }
    @Override
    protected void append(org.apache.log4j.spi.LoggingEvent event) {
        if (gui == null) {
            gui = new GUI();
        }
		try {
			tname.setText(tname.getText()+"\n"+event.getMessage().toString());
		} catch (NullPointerException e) {
			log.error("apender error "+ e);
		}
    }
    @Override
    public void close() {
    }
    @Override
    public boolean requiresLayout() {
        return false;
    }
    
}