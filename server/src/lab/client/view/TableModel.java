package lab.client.view;
import java.util.*;
import lab.TaskInfo;
import javax.swing.table.*;
import java.util.Collections.*;
/**
*    This class creates TableModel, and keep tasks list.
*/
public class TableModel extends AbstractTableModel {
    private ArrayList<TaskInfo> info = null; 
    private ArrayList<TaskInfo> removeInfo = null; 
    public static final long serialVersionUID = 213123123123l;
    public int total = 0;
    public int today = 0;
    public int tomorrow = 0;
    public int week = 0;
    /**
    *    Constructor creates TableModel's object and add task list into
    *    the table.
    */
    public TableModel(Hashtable<Long,TaskInfo> table) {
        info = new ArrayList<TaskInfo>(table.values());
        removeInfo = new ArrayList<TaskInfo>(table.values());
        Collections.sort(info);
        Collections.sort(removeInfo);
        recount();
    }
    /**
    * Returns the row's count.
    */
    public int getRowCount() {
        total = info.size();
        return total;
    }
    /**
    * Returns the column's count.
    */
    public int getColumnCount() {
        return 4;
    }
    /**
    * Returns the table's value.
    * @param r number of the row in the table.
    * @param c number of the column in the table.
    */
    public Object getValueAt(int r, int c) {
        TaskInfo t = info.get(r);
        switch (c) {
            case 0: {
                return t.getName();
            }
            case 1: {
                return t.getInfo();
            }
            case 2: {
                return t.getDate().toString();
            }
            case 3: {
                if (t.getExec() != null) {
                    return t.getExec().getName();    
                } else {
                    return " ";
                }
            } 
            case 4: {
                return t.getID();
            }
        }
        return "";
    }
    /**
    * Returns the column's name.
    * @param c number of the column in the table.
    */
    public String getColumnName(int c) {
        switch (c) {
            case 0: {
                return "Name";
            }
            case 1: {
                return "Information";
            }
            case 2: {
                return "Date";
            }
            case 3: {
                return "Executable program";
            }
            case 4: {
                return "ID";
            }
        }
        return "";            
    }
    /**
    * Disabled rows edit.
    * @param row number of the row in the table.
    * @param column number of the column in the table.
    */
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    /**
    * Returns taskID.
    * @param r number of the row in the table.
    */
    public long getID(int r) {
        return info.get(r).getID();
    }
    /**
    * add task to the table.
    * @param t reference on the add task.
    */
    public void addTask(TaskInfo t) {
        synchronized (info) {
            info.add(t);
            Collections.sort(info);
            recount();            
        }
        synchronized (removeInfo) {
            removeInfo.add(t);
            Collections.sort(removeInfo);
        }
        fireTableDataChanged();
    }
    /**
    * edit the task.
	* SuppressWarnings I used becose use unchecked cast in clone().
    * @param id id for the edits row.
    * @param t reference on the edit task.
    */
    @SuppressWarnings("unchecked")
    public synchronized void editTask(int id, TaskInfo t) {
        synchronized (info) {
            info.set(id,t);
            Collections.sort(info);
            recount();
        }
        synchronized (removeInfo) {
            if (info.size() == removeInfo.size()) {
                removeInfo.set(id,t);
            } else {
                removeInfo.clear();
                removeInfo = (ArrayList<TaskInfo>) info.clone();
            }
            Collections.sort(removeInfo);
        }
        fireTableDataChanged();
    }
    /**
    * return task;
    * @param r number of the row in the table.
    */
    public synchronized TaskInfo get(int r) {
        return info.get(r);
    }
    /**
    * Returns the selected row.
    * @param id id for the finds row.
    */
    public int getSelectedRowById (long id) {
        int i = 0;
        for (TaskInfo ts: info) {
            if( id == ts.getID()) {
                return i;
            }
            i++;
        }
        return -1;
    }
    /**
    * Remove the task from table.
    * @param id id for the removes task.
    */
    public void removeTask(int id) {        
        synchronized (info) {
            info.remove(id);
            Collections.sort(info);        
            recount();
        }
        fireTableDataChanged();
    }
    /**
    *    change info about count task
    */
    public void recount() {
        today = 0;
        tomorrow = 0;
        week = 0;
        total = 0;
         for (TaskInfo t: info) {
            Calendar cal1 = Calendar.getInstance();        
            cal1.setTime(t.getDate());
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(new Date());
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
                    today++;            
                }
                if ((cal1.get(Calendar.DAY_OF_MONTH) - cal2.get(Calendar.DAY_OF_MONTH)) == 1) {
                    tomorrow++;            
                }
                if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                    week++;            
                }
            }
            total++;
        }
    }
	/**
	* Return tasks list.
	*/
    public ArrayList<TaskInfo> getTableInfo() {
        return removeInfo;
    }

}