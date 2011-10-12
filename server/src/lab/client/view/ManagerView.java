package lab.client.view;
import lab.*;
import lab.exception.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.text.*;
import javax.swing.JOptionPane.*;
import java.net.*;
import lab.client.conntroller.*;
import lab.client.*;
/**
 * Class Create user Interface
 */
public class ManagerView extends JFrame {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ManagerView.class);
    private Container pane = null;
	private ManagerControllerInterface controller = new ManagerController();;
    private int loadFirstView = 0;
    private TableModel tableModel;  
    private JTable table = new JTable();
    private JMenuBar menuBar = new JMenuBar();
    private JToolBar menuConection = new JToolBar();
    private JMenu menuCom = new JMenu("Command");
    private JMenu menuAbout = new JMenu("About");
    private JMenu menuOpt = new JMenu("Option");
    private JLabel total = null;
    private JLabel today = null;
    private JLabel tomorrow = null;
    private JLabel week = null;
	private ServerConnector sc = null;
    public static final long serialVersionUID = 123312332l;
    private class Notifier implements Runnable {
        private Thread thread = null;
        public Notifier() {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
        /**
        *    Create new thread look in all tasks, 
        *     and chooses the task for execution. 
        */
        public void run() {
            while (true) {    
                try {
                    thread.sleep(1000);
                } catch (InterruptedException e) {}
                if ((tableModel != null) && (tableModel.getRowCount() != 0)) {
                    if (tableModel.get(0) != null) {                   
                        if (tableModel.get(0).getDate().getTime() <= (new Date().getTime()+ViewVariable.offTime*1000*60)) {
                            new TaskWindow(ManagerView.this,tableModel.get(0),controller);
                        }
                    }
                }
            }
        }
    }
    /**
    *    Constructor creates ManagerView's object.
    */
    public ManagerView() {
        setJMenuBar(menuBar);
        menuCom.setEnabled(false);
        menuBar.add(menuCom);
        menuBar.add(menuOpt);
        menuBar.add(menuAbout);
        pane = getContentPane();
        pane.add(menuConection);
        setTitle("Task Manager");
        loadOption();
        setSize(ViewVariable.W,ViewVariable.H);
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    saveOption();
                    dispose();
                    System.exit(0);
                }
            }
        );    
        new Notifier();        
    }
    /**
    *    Load all swing element.
    */
    public void loadView() {
       //------- Create JTable
            table.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            viewEditTask();
                        }
                    }
                }
            );
        table.addKeyListener( 
            new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    int com = e.getKeyCode();
                    if (e.isControlDown()) {
                        switch (com) {
                            case KeyEvent.VK_V: {
                                viewViewTask();
                                break;
                            }
                        }
                    }
                }
            }
        );
            JScrollPane scr = new JScrollPane(table);
            Box b = Box.createVerticalBox();
            menuConection.setMaximumSize(new Dimension(130,30));
            JButton bConnect = new JButton( new ImageIcon("img\\connect.png"));
            bConnect.addActionListener( 
                new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        if (sc == null) {
                            sc = new ServerConnector(ManagerView.this);	
                            sc.startCommander();
							menuCom.setEnabled(true);
                        }
                    }
                }
            );
            bConnect.setToolTipText("Connection to the server");
            menuConection.add(bConnect);
            JButton bDisconnect = new JButton( new ImageIcon("img\\disconnect.png"));
            bDisconnect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev ) {
                    notifyError();
                }
            });
            bDisconnect.setToolTipText("Disconnect from the server");
            menuConection.add(bDisconnect);
            b.add(menuConection);
            b.add(scr);
            Box b1 = Box.createHorizontalBox();
            b.add(b1);
            total = new JLabel("TotalSize :" + 0); 
            total.setMaximumSize(new Dimension(100,30));
            b1.add(total);
            today = new JLabel("  Today :" + 0);
            today.setMaximumSize(new Dimension(100,30));
            b1.add(today);
            tomorrow = new JLabel("  Tomorrow :" + 0);
            tomorrow.setMaximumSize(new Dimension(100,30));
            b1.add(tomorrow);
            week = new JLabel("  This week :" + 0);
            week.setMaximumSize(new Dimension(100,30));
            b1.add(week);
            pane.add(b);
            //------- Menu item New Task
            ImageIcon add = new ImageIcon("img\\add.png");
           final JMenuItem bAddtask = new JMenuItem("New task",add);
           menuCom.add(bAddtask);
           bAddtask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
           bAddtask.setToolTipText("Add new task");
            bAddtask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        new TaskWindow(ManagerView.this, controller);
                    }
                }
            );
            //------- Menu item Remove Task
            ImageIcon remove = new ImageIcon("img\\remove.png");
            JMenuItem bRemovetask = new JMenuItem("Remove task",remove);
            menuCom.add(bRemovetask);
            bRemovetask.setToolTipText("Remove selected task");
            bRemovetask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,InputEvent.CTRL_MASK));
            bRemovetask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        int[] i = table.getSelectedRows();
                        if (i.length == 0) {return;}
                        long id = tableModel.getID(i[0]);
                        try {
							controller.delTask(id);
                        } catch (Exception e) {
                            log.error(e);
                            JOptionPane.showMessageDialog(ManagerView.this, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);                    
                        }
                    }
                }
            );
            //------ Menu item Edit Task
            ImageIcon edit = new ImageIcon("img\\edit.png");
            final JMenuItem bEdittask = new JMenuItem("Edit task",edit);
            menuCom.add(bEdittask);
            bEdittask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_MASK));
            bEdittask.setToolTipText("Edit selected task");
            bEdittask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewEditTask();
                    }
                }
            );
            //------ Menu Item View Task
            ImageIcon view = new ImageIcon("img\\view.png");
            final JMenuItem bViewtask = new JMenuItem("View task",view);
            menuCom.add(bViewtask);
            bViewtask.setToolTipText("View selected task");
            bViewtask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
            bViewtask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewViewTask();
                    }
                }
            );
            // action resized
            addComponentListener(
                new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        repaint();
                    }
                }
            );
            //------ Menu Item About program
            final JMenuItem bAbout = new JMenuItem("About program");
            menuAbout.add(bAbout);
            bAbout.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    aboutProgram();
                }
            }
            );
            //------ Menu Item Option
            final JMenuItem option = new JMenuItem("Option");
            menuOpt.add(option);
            option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
            option.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    optionProgram();
                }
            }
            );            
            setVisible(true);
    }
   /**
     *    Show task Edit dialog.
     */
    private void viewEditTask() {
       int[] i = table.getSelectedRows();
       if (i.length == 0) {return;}
       new TaskWindow(ViewVariable.comEdit,this, controller, tableModel.get(i[0]));
    }
    /**
     *    Show task View dialog.
     */
    private void viewViewTask() {
        int[] i = table.getSelectedRows();
        if (i.length == 0) {return;}
        new TaskWindow(ViewVariable.comView,this, controller, tableModel.get(i[0]));
    }
    /**
    * Shows about dialog.
    */
    private void aboutProgram() {
        JDialog  aboutD = new JDialog(this, true);
        aboutD.setTitle("About program");
        Box b = Box.createVerticalBox();
        aboutD.add(b);
        Border e = BorderFactory.createMatteBorder(10,10,10,10,Color.BLUE);
        aboutD.setSize(400,400);
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n    Task Manager"+"\n");
        sb.append("    Version 1.0"+"\n");
        sb.append("    Author: Sergey Ivanchenko"+"\n");
        sb.append("    This program is free software."+"\n");
        JTextArea aboutL = new JTextArea(sb.toString());
        b.add(aboutL);
        aboutL.setBorder(e);
        aboutL.setEnabled(false);
        aboutL.setBackground(new Color(100,0,0));
        aboutD.setVisible(true);
    }
    /**
    * Shows option dialog.
    */
    private void optionProgram() {
        try{
		final JDialog optionD = new JDialog(this,true);
        optionD.setTitle("Option");
        optionD.setSize(255, 125);
        Box b = Box.createVerticalBox();
		Box bIP = Box.createHorizontalBox();
        Box b1 = Box.createHorizontalBox();
        Box b2 = Box.createHorizontalBox();
        optionD.add(b);
        final JCheckBox check = new JCheckBox("Autorun program");
		
			MaskFormatter form = new MaskFormatter("###.###.###.###");
			MaskFormatter formP = new MaskFormatter("####");
			JLabel lIP = new JLabel("IP");
			JLabel lPort = new JLabel("Port:");
			form.setPlaceholderCharacter('0');
			final JFormattedTextField ip = new JFormattedTextField(form);
			final JFormattedTextField port = new JFormattedTextField(formP);
			ip.setMaximumSize(new Dimension(100,30));
			ip.setValue(ViewVariable.ip);
			port.setValue(ViewVariable.port.toString());
			bIP.add(lIP);
			bIP.add(ip);
			bIP.add(lPort);
			bIP.add(port);
			b.add(bIP);
		
        check.setSelected(ViewVariable.autoRun);
        check.setToolTipText("Set, if you want to run executable program automaticly in the task.");        
        JLabel l = new JLabel("Put off time:");
        JLabel m = new JLabel("Minits");
        final JTextField min = new JTextField();
        min.setText(ViewVariable.offTime+"");
        final JComboBox list = new JComboBox();
        list.addItem("0.5");
        list.addItem("1");
        list.addItem("2");
        list.addItem("5");
        list.addItem("6");
        list.addItem("10");
        list.addItem("12");
        list.addItem("15");
        list.addItem("24");
        JLabel h = new JLabel("H");
        list.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    Double d = Double.parseDouble((String) list.getSelectedItem())*60.;
                    Integer i = d.intValue();
                    min.setText(i.toString());
                }
            }
        );
        JButton bOk = new JButton("Ok");
        bOk.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {                
                        ViewVariable.autoRun = check.isSelected();
                        ViewVariable.H = getSize().height;
                        ViewVariable.W = getSize().width;
						ViewVariable.ip = (String)ip.getValue();
						ViewVariable.port = Integer.parseInt((String)port.getValue());
                    try {
                        ViewVariable.offTime = Integer.parseInt(min.getText());
                    } catch (NumberFormatException e3) {
                        return;
                    }
                    optionD.dispose();
                }
            }
        );
        JButton bCancel = new JButton("Cancel");
        bCancel.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    optionD.dispose();
                }
            }
        );
        b1.add(l);
        b1.add(Box.createGlue());
        b1.add(min);
        b1.add(m);
        b1.add(list);
        b1.add(h);
        b.add(b1);
        b.add(check);
        b.add(b2);        
        b2.add(bOk);
        b2.add(Box.createGlue());
        b2.add(bCancel);
        optionD.setResizable(false);        
        optionD.setVisible(true);
		} catch (ParseException e) {
			log.warn(e);
		}
    }
    /**
    * Loads option from the file.
    */
    private void loadOption() {
        try {
            File f = new File("option\\option.opt");
            if(!f.exists()) {
                return;
            }
            DataInputStream in = 
                new DataInputStream(
                    new FileInputStream(f));
            ViewVariable.offTime = in.readInt();
            ViewVariable.autoRun = in.readBoolean();
            ViewVariable.H = in.readInt();
            ViewVariable.W = in.readInt();
			ViewVariable.ip = in.readUTF();
			ViewVariable.port = in.readInt();
			ViewVariable.hash = in.readUTF();
        } catch (IOException e) {
            log.error("IO Exception, read option");
        }
    }
    /**
    * Saves option into the file.
    */
    private void saveOption() {
        File f = new File("option\\option.opt");
        DataOutputStream out = null;
        try {
            out = 
                new DataOutputStream(
                    new FileOutputStream(f));
            out.writeInt(ViewVariable.offTime);
            out.flush();
            out.writeBoolean(ViewVariable.autoRun);
            out.flush();
            out.writeInt(getSize().height);
            out.flush();
            out.writeInt(getSize().width);
            out.flush();
			out.writeUTF(ViewVariable.ip);
			out.flush();
			out.writeInt(ViewVariable.port);
            out.flush();
			out.writeUTF(ViewVariable.hash);
			out.flush();
            out.close();
        } catch (IOException e1){
            log.error("IO Exception, write option");
        }
    }
    /**
     * Update informatio about tasks
     */
    public void updateTable() {      
            total.setText("TotalSize :" + tableModel.total);
            today.setText("  Today :" + tableModel.today);
            tomorrow.setText("  Tomorrow :" + tableModel.tomorrow);
            week.setText("  This week :" + tableModel.week);
    }
    /**
    * update all task
    */
    public void notifyGetAll(ParsedInfo pars) {
    	controller.setOutStream(sc.getOut());
        ViewVariable.uid = pars.getUserID();
        tableModel = new TableModel(pars.getAllTasks());
        table.setModel(tableModel);
        updateTable();
    }
    /**
    * Add task into the table.
    * @param ts edit task.
    */
    public void notifyAdd(TaskInfo ts) {
        tableModel.addTask(ts);
        updateTable();
    }
    public void notifyError() {
		table.setModel(new TableModel(new Hashtable<Long,TaskInfo>()));                        
        menuCom.setEnabled(false);
		sc.stopCommander();
		sc = null;
	}
	/**
    * Edit task in the table.
    * @param ts edit task.
    */
    public void notifyEdit(TaskInfo ts) {
        tableModel.editTask(tableModel.getSelectedRowById(ts.getID()),ts);
        updateTable();
    }
    /**
    * Remove task from table.
    * @param id task id.
    */
    public void notifyRemove(long id) {
        tableModel.removeTask(tableModel.getSelectedRowById(id));
        updateTable();    
    }
}//end ManagerView