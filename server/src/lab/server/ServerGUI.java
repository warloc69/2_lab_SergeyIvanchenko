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
/**
* Class create new gui appender for the log4j.
*/
public class ServerGUI extends AppenderSkeleton {	
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerGUI.class);
    private JTextArea tname = null;
    private GUI gui = null;
    private class GUI extends JFrame{	
        public static final long serialVersionUID = 123312452l;		
		private Connector con = null;
		public GUI () {
            super();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            showGUI();
        }
		/**
		* show server gui
		*/
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
				port.setValue("8189");
				startServer.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							con  = new Connector(Integer.parseInt(((String)port.getValue())));
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
							String s = " ";
							 s += JOptionPane.showInputDialog(GUI.this,"server kill","kill",JOptionPane.QUESTION_MESSAGE);
							con.stop(s);
							if (log.isInfoEnabled()) {
								log.info(s);
							}
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
	/**
	* get message from the logger and send message to the gui.
	*/
    @Override
    protected void append(org.apache.log4j.spi.LoggingEvent event) {
        if (gui == null) {
            gui = new GUI();
        }
		tname.setText(tname.getText()+"\n"+event.getMessage().toString());
    }
	/**
	* close appender.
	*/
    @Override
    public void close() {
    }
    @Override
    public boolean requiresLayout() {
        return false;
    }
    
}