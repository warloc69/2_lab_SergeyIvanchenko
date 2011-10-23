package lab.client.view;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.JOptionPane.*;
import java.util.*;
/**
* Make connection dialog.
*/
public class ConnectWindow extends JDialog{	
	private ManagerView mv = null;
	private boolean connect = false;
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ConnectWindow.class);
    public static final long serialVersionUID = 1243223323l;	
	public ConnectWindow (ManagerView mv) {
		super(mv, true);
		this.mv = mv;
		connectionWindow();
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
		if (!"".equals(ViewVariable.hash)){
			StringTokenizer st = new StringTokenizer(ViewVariable.hash,"|");
			tname.setText(st.nextToken());
			tPass.setText(st.nextToken());
		}
        boxPassword.add(lPass);
        boxPassword.add(tPass);
        JButton save = new JButton("Connect");
        save.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                    String name = tname.getText();
                    String pass = tPass.getText();
                    if ("".equals(name) || "".equals(pass)) {
                        JOptionPane.showMessageDialog(ConnectWindow.this, "name or password wrong","Warning",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
					ViewVariable.hash = name+"|"+pass;
				ConnectWindow.this.connect = true;
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
	* Show connect user or not.
	*/
    public boolean isConnected() {
		return connect;
	}
}