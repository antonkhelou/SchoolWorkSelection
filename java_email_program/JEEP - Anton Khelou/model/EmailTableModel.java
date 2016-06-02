/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: EmailTableModel.java                                              
 * Short description of what's in this file: This class is basically the
 * backbone of the emailTable in MainFrameTablePanel.java. It contains
 * to manipulate the data stored in the emailTable.                           
 */
package model;

import java.sql.Date;
import java.util.ArrayList;


import javax.swing.table.AbstractTableModel;

import data.JEEPDBManager;

import beans.EmailBean;
import beans.FolderBean;


@SuppressWarnings("serial")
public class EmailTableModel extends AbstractTableModel
{
	private ArrayList<String> columnNames = null;
	private ArrayList<EmailBean> data = null;
	private JEEPDBManager connection = null;

	/**
	 * Constructs the EmailTableModel
	 * 
	 */
	public EmailTableModel(JEEPDBManager connection)
	{
		this.connection = connection;
		columnNames = new ArrayList<String>();
		columnNames.add("Subject");
		columnNames.add("Sender");
		columnNames.add("Date");
		data = new ArrayList<EmailBean>();
	}
		
	/**
	 * Method used to get email data from a particular folder in the
	 * database. When the data is received, this method issues a "refresh"
	 * on the emailTable
	 * 
	 */
	public void displayEmailsInFolder(String folderName)
	{
		data = connection.getEmailsInFolder(folderName);
		fireTableDataChanged();
	}
	
	/**
	 * Method used to get the folder names in the database.
	 * 
	 */
	public ArrayList<String> getFolderNames()
	{
		ArrayList<FolderBean> folderList = connection.getFoldersData();
		ArrayList<String> folderNamesList = new ArrayList<String>();
		for(FolderBean data: folderList)
			folderNamesList.add(data.getFolderName());
		
		return folderNamesList;
	}
	
	/**
	 * Return the mail message data bean at the specified row
	 * 
	 * @param row
	 * @return
	 */
	public EmailBean getMailMessageData(int row) 
	{
		if (data.size() > row)
			return data.get(row);
		else
			return null;
	}

//	/**
//	 * @return
//	 */
//	public Vector<Boolean> getUpdateVector() {
//		return doUpdate;
//	}
//
//	/**
//	 * @param row
//	 * @return
//	 */
//	public boolean getUpdateStatus(int row) {
//		return doUpdate.elementAt(row);
//	}

	/**
	 * @param row
	 * @return
	 */
	public EmailBean getEmailData(int row) {
		return data.get(row);
	}

	/**
	 * @param row
	 */
//	public void clearUpdate(int row) {
//		doUpdate.setElementAt(new Boolean(true), row);
//
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnNames.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int col) {
		return (String) columnNames.get(col);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() 
	{
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) {

			switch (col) 
			{
				case 0:
					return data.get(row).getSubject();
				case 1:
					return data.get(row).getfromSender();
				case 2:
					return data.get(row).getDate();
			}
		
		// Should throw exception since this must never happen
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	public Class<? extends Object> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int col) 
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 * int, int)
	 */
	public void setValueAt(Object value, int row, int col) {

		switch (col) {
		case 0:
			 data.get(row).setSubject((String)value);
		case 1:
			 data.get(row).setfromSender((String)value);
		case 2:
			 data.get(row).setDate((Date)value);
		}

		// Let the JTable know a change has occurred
		fireTableCellUpdated(row, col);
		// Set the row for update
		//doUpdate.setElementAt(new Boolean(true), row);
	}
}
