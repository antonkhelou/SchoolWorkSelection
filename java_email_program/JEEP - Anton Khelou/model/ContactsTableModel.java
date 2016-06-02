/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: ContactsTableModel.java                                              
 * Short description of what's in this file: This class is basically the
 * backbone of the contactsTable in ContactsTablePanel.java. It contains
 * to manipulate the data stored in the contactTable.                           
 */
package model;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import beans.ContactBean;
import data.JEEPDBManager;


@SuppressWarnings("serial")
public class ContactsTableModel extends AbstractTableModel
{
	private ArrayList<String> columnNames = null;
	private ArrayList<ContactBean> data = null;
	private JEEPDBManager connection = null;

	/**
	 * Constructs the ContactsTableModel
	 * 
	 */
	public ContactsTableModel(JEEPDBManager connection)
	{
		this.connection = connection;
		columnNames = new ArrayList<String>();
		columnNames.add("Last Name");
		columnNames.add("First Name");
		data = new ArrayList<ContactBean>();
	}
	
	/**
	 * Method used to get contact data the database.
	 * When the data is received, this method issues a "refresh"
	 * on the contactsTable
	 * 
	 */
	public void displayContacts()
	{
		data = connection.getContactsData();
		fireTableDataChanged();
	}
	
	/**
	 * Method used to remove a row from the contactsTable
	 * and issues a "refresh" afterwards.
	 * Note that this method does not commit the changes in the
	 * database.
	 * 
	 */
	public void removeContactFromView(int row)
	{
		data.remove(row);
		fireTableDataChanged();
	}
	
	/**
	 * Return the mail message data bean at the specified row
	 * 
	 * @param row
	 * @return
	 */
	public ContactBean getMailMessageData(int row) 
	{
		if (data.size() > row)
			return data.get(row);
		else
			return null;
	}

	/**
	 * @param row
	 * @return
	 */
	public ContactBean getContactData(int row) {
		return data.get(row);
	}


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
					return data.get(row).getLastName();
				case 1:
					return data.get(row).getFirstName();
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
			 data.get(row).setLastName((String)value);
		case 1:
			 data.get(row).setFirstName((String)value);
		}

		// Let the JTable know a change has occurred
		fireTableCellUpdated(row, col);
		// Set the row for update
		//doUpdate.setElementAt(new Boolean(true), row);
	}
}
