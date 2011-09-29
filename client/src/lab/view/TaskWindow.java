package lab.view;
import lab.*;
import lab.exception.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.swing.JOptionPane.*;
import java.net.*;

/**
* Class creats add, view , edit task dialog
*/
public class TaskWindow extends JDialog {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TaskWindow.class);
    public static final long serialVersionUID = 123322332l;
    private ManagerView mv = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private TaskInfo task = null;
    private boolean start = false; 
    private String name = null;
    private String pass = null;
    private Socket s = null;
    /**
    *    It's private class helps to show only programs
    */
    private class ExeFilter extends javax.swing.filechooser.FileFilter {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".exe") ||
                    f.isDirectory();
            }
            public String getDescription() {
                return "Program";
            }
        }
    /**
    * validation task.
    * @param task reference on the validation task.
    * @throws BadTaskException if task is invalide.
    */
    private void taskValidation (TaskInfo task) throws BadTaskException {
        if (task.getDate().before(new Date())) {
            throw new BadTaskException("Date incorrect");
        }
        if (task.getExec() != null && !task.getExec().getName().equals(" ")) {
            String file = task.getExec().getPath();
            if(!file.regionMatches(true,file.length()-3,"exe",0,3)) {
                throw new BadTaskException("Chouse file incorrect");
            }
        }
        if (task.getName().length() == 0) {
            throw new BadTaskException("Name incorrect");
        }
    }
    public TaskWindow(ManagerView mv) {
        super(mv,true);
        this.mv = mv;
    }
    /**
    * contructor used for create add dialog.
    * @param mv reference on the view.
    */
    public TaskWindow(ManagerView mv, PrintWriter out) {
        super(mv,true);
        this.mv = mv;
        this.out = out;
        task = new TaskInfoImpl();
        viewAddTask(ViewVariable.comADD);
    }
     /**
    * contructor used for create notification dialog.
    * @param mv reference on the view.
    * @param task reference on the views task.
    */
    public TaskWindow(ManagerView mv, TaskInfo task, PrintWriter out) {
        super(mv,true);
        this.out = out;
        this.task = task;      
        this.mv = mv;
        viewMassage(task);
    }
   /**
    * contructor used for edit, view dialog.
    * @param com command for the dialog (edit or view).
    * @param mv reference on the view.
    * @param task reference on the views or edits task.
    */
    public TaskWindow(int com, ManagerView mv, TaskInfo task, PrintWriter out) {
        super(mv,true);
        this.mv = mv;
        this.out = out;
        this.task = task;
        viewAddTask(com);        
    }
    /**
    *    Show add / edit / view task dialog.
    * @param com command for the dialog (edit, view, add).
    */
    private void viewAddTask(final int command) {
        int W = 1024;
        int H = 600;
        Box boxName = Box.createHorizontalBox();
        Box boxInfo = Box.createHorizontalBox();
        Box boxFile = Box.createHorizontalBox();
        Box boxButton = Box.createHorizontalBox();
        Box allBoxes = Box.createVerticalBox();
        setSize(W/2,H/2);
        setBounds(W/4,H/4,W/2,H/2);
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    dispose();
                }
            }
        );
        //----- JLabel name
        final JLabel lname = new JLabel("Name:");
        boxName.add(lname);
        //----- JTextField
        final JTextField tname = new JTextField();
        tname.setToolTipText("Writes task name here");
        
        tname.setMaximumSize(new Dimension(getSize().height,30));
        
        boxName.add(tname);
        
        //----- JLabel Info
        final JLabel linfo = new JLabel("Info:");
        //----- JTextArea Info        
        final JTextArea tinfo = new JTextArea();
        JScrollPane sinfo = new JScrollPane(tinfo);
        tinfo.setToolTipText("Writes information about task here");
        boxInfo.add(sinfo);
        //----- Date
        final JSpinner dateChooser = new JSpinner(new SpinnerDateModel());
        dateChooser.setToolTipText("Writes moment when task must run");
        dateChooser.setMaximumSize(new Dimension(110,30));
        boxName.add(Box.createRigidArea(new Dimension(10,30)));   
        final Box b = Box.createHorizontalBox();
        b.add(dateChooser);
        boxName.add(b);
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
        list.setMaximumSize(new Dimension(45,30));
        final JLabel h = new JLabel("H");
        list.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    Double d = Double.parseDouble((String) list.getSelectedItem())*60;
                    Long l = d.longValue();
                   
                    dateChooser.setValue(new Date(task.getDate().getTime()+l*1000*60));
                }
            }
        );
        boxName.add(list);
        boxName.add(h);
        //----- JLabel file
        final JLabel lFile = new JLabel("");
        //----- JChoose Execute file
        final JFileChooser exefile = new JFileChooser();   
        exefile.setFileFilter(new ExeFilter());
        JButton bexefile = new JButton("Run program");
        bexefile.setToolTipText("If you want run some program, you can choose it's program here ");  
        bexefile.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                exefile.showOpenDialog(TaskWindow.this);
                if (exefile.getSelectedFile() != null ) {
                    lFile.setText(exefile.getSelectedFile().getPath());
                    lFile.setForeground(Color.BLACK);
                }
            }
        });
        boxFile.add(Box.createRigidArea(new Dimension(10,30)));
        boxFile.add(bexefile);
        boxFile.add(lFile);
        //----- Button Save
        JButton save = null;
        switch (command) {
            case ViewVariable.comADD: { 
                save = new JButton("Save");
                save.setToolTipText("Save the task");
                setTitle("Add new task");
                break;
            }
            case ViewVariable.comEdit: {
                save = new JButton("Save");
                save.setToolTipText("Save the task");
                setTitle("Edit task");
                if (task.getDate().getTime() <= (new Date().getTime())) {
                    dateChooser.setValue(new Date());
                } else {
                    dateChooser.setValue(task.getDate());
                }
                tname.setText(task.getName());
                tinfo.setText(task.getInfo());
                if (task.getExec() != null && !task.getExec().getName().equals(" ")) {
                    lFile.setText("RunProgram: " + task.getExec().getPath());
                    exefile.setSelectedFile(task.getExec());
                }
                break;
            }
            case ViewVariable.comView: {
                save = new JButton("Ok");
                save.setToolTipText("Close dialog");
                setTitle("View task");
                if (task.getDate().getTime() <= (new Date().getTime())) {
                    dateChooser.setValue(new Date());
                } else {
                    dateChooser.setValue(task.getDate());
                }
                tname.setText(task.getName());
                tinfo.setText(task.getInfo());
                if (task.getExec() != null && !task.getExec().getName().equals(" ")) {
                    lFile.setText("RunProgram: " + task.getExec().getPath());
                    exefile.setSelectedFile(task.getExec());
                }
                break;
            }
        }      
        save.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (command == ViewVariable.comView) {
                    dispose();
                    return;
                }
                TaskInfo ts = new TaskInfoImpl();
                ts.setID(task.getID());
                ts.setDate((Date)dateChooser.getValue());
                ts.setName(tname.getText());
                ts.setInfo(tinfo.getText());
                if (exefile.getSelectedFile() != null) {
                    ts.setExec(exefile.getSelectedFile());
                } else {
                    ts.setExec(new File(" "));
                }
                if (command == ViewVariable.comEdit) {
                    try {
                        taskValidation(ts);
                        out.println(new XMLPackager("edit",ts).getXML());
                    } catch (BadTaskException e) {
                        log.warn(e);
                        JOptionPane.showMessageDialog(TaskWindow.this, e.getMessage(),"Warning",JOptionPane.WARNING_MESSAGE);
                        return;
                    } 
                } else {
                   try {
                        taskValidation(ts);
                        String s = new XMLPackager("add",ts).getXML();
                        System.out.println(s);
                        out.println(s);
                    } catch (BadTaskException e) {
                        log.warn(e);
                        JOptionPane.showMessageDialog(TaskWindow.this, e.getMessage(),"Warning",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                dispose();
            }
        });
        boxButton.add(save);
        //----- Button Cancel
        JButton cancel = null;
        if (command != ViewVariable.comView){
            cancel = new JButton("Cancel");
            save.setToolTipText("Close the dialog");
            cancel.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    dispose();
                }            
            });
            boxButton.add(cancel);   
        }
        
        setResizable(false);
        if (command == ViewVariable.comView) {
            tname.setEnabled(false);
            tinfo.setEnabled(false);
            dateChooser.setEnabled(false);
            boxFile.remove(bexefile);
            boxName.remove(list);
            boxName.remove(h);
        }
        allBoxes.add(boxName);
        allBoxes.add(Box.createRigidArea(new Dimension(10,10)));
        allBoxes.add(linfo, BorderLayout.WEST);        
        allBoxes.add(boxInfo);
        allBoxes.add(boxFile,BorderLayout.WEST);
        allBoxes.add(boxButton);
        add(allBoxes);
        setVisible(true);
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
        boxPassword.add(lPass);
        boxPassword.add(tPass);
        JButton save = new JButton("Connect");
        save.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    name = tname.getText();
                    pass = tPass.getText();
                    if ("".equals(name) || "".equals(pass)) {
                        JOptionPane.showMessageDialog(TaskWindow.this, "name or password wrong","Warning",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Socket s = new Socket("127.0.0.1",8189);
                    TaskWindow.this.s = s;
                    in = new BufferedReader (
                        new InputStreamReader(s.getInputStream()));
                    out = new PrintWriter (
                        s.getOutputStream(),true);
                    TaskWindow.this.out = out;
                    TaskWindow.this.in = in;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TaskWindow.this, "Server connect error","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                mv.notifyGetAll(in,out,name+pass);
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
    * Show execute dialog.
    * @param task reference on the views task.
    */
    private void viewMassage(final TaskInfo ts) {
        final Box boxName = Box.createHorizontalBox();
        Box boxInfo = Box.createHorizontalBox();
        Box boxFile = Box.createHorizontalBox();
        Box boxButton = Box.createHorizontalBox();
        Box allBoxes = Box.createVerticalBox();
        start = false;
        int W = Toolkit.getDefaultToolkit().getScreenSize().width;
        int H = Toolkit.getDefaultToolkit().getScreenSize().height-50;
        setSize(W/2,H/2);
        setBounds(W/4,H/4,W/2,H/2);
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    out.println(new XMLPackager("remove",ts).getXML());
                    dispose();
                }
            }
        );
        //----- JLabel name
        final JLabel lname = new JLabel("Name:");
        boxName.add(lname);
        //----- JTextField
        final JTextField tname = new JTextField();
        tname.setMaximumSize(new Dimension(getSize().height,30));        
        boxName.add(tname);
        tname.setText(ts.getName());
        tname.setEnabled(false);
        //----- JLabel Info
        final JLabel linfo = new JLabel("Info:");
        //----- JTextArea Info
        final JTextArea tinfo = new JTextArea();
        JScrollPane scrol = new JScrollPane(tinfo);
        boxInfo.add(scrol);
        tinfo.setText(ts.getInfo());
        tinfo.setEnabled(false);
        boxName.add(Box.createRigidArea(new Dimension(10,30)));
        if (ts.getExec() != null && !ts.getExec().getName().equals(" ")) {
            final JLabel lFile = new JLabel("Run program : " + ts.getExec().getName());
            boxFile.add(lFile);
        }
        JButton ok = new JButton("Ok");  
        ok.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                    if (!start) {
                        if (ts.getExec() != null && !ts.getExec().getName().equals(" ")){
                            Runtime r = Runtime.getRuntime();
                            try {
                                r.exec(ts.getExec().getPath());
                                start = true;
                            } catch (IOException e) {
                                log.error("Runtime error");
                            }
                        }
                    }
                out.println(new XMLPackager("remove",ts).getXML());
                dispose();                
            }
        });
        if (start) {
            return;
        }
        boxButton.add(ok);
        //-----Put off
        JButton cancel = new JButton("Postpone");
        cancel.setToolTipText("Put off task to the future time");
            cancel.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                  new TaskWindow(ViewVariable.comEdit,mv,ts,out);
                  dispose();
                }            
            });
        boxButton.add(cancel);
        if (ViewVariable.autoRun) {
            if (ts.getExec() != null && !ts.getExec().getName().equals(" ")){
                Runtime r = Runtime.getRuntime();
                try {
                    r.exec(ts.getExec().getPath());
                    start = true;
                } catch (IOException e) {
                    log.error("Runtime error");
                }
            }
        }
        allBoxes.add(boxName);
        allBoxes.add(Box.createRigidArea(new Dimension(10,10)));
        allBoxes.add(linfo, BorderLayout.WEST);        
        allBoxes.add(boxInfo);
        allBoxes.add(boxFile,BorderLayout.WEST);
        allBoxes.add(boxButton);
        add(allBoxes);
        setVisible(true);
    }
    /**
    * return socket Out stream.
    */
   public PrintWriter getOut () {
    return out;
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