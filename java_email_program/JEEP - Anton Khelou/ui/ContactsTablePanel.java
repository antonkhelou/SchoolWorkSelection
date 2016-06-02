/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: ContactsTablePanel.java                                          
 * Short description of what's in this file: This class is used
 * as the container of the Contacts Table.                      
 */
package ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import data.JEEPDBManager;
import model.ContactsTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;


@SuppressWarnings("serial")
public class ContactsTablePanel extends JPanel
{
	private JTable contactsTable = null;
	private ContactsTableModel contactsTableModel = null;
	private JEEPDBManager connection = null;

	/**
	* Constructs the ContactsTablePanel
	* 
	*/
	public ContactsTablePanel(JEEPDBManager connection) 
	{
		super(new BorderLayout());
		this.connection = connection;
		
		initialize();
	}
	
	/**
	 * Create the GUI
	 */
	public void initialize() 
	{
		//Creates a TableModel for the contactsTable
		contactsTableModel = new ContactsTableModel(connection);
		
		contactsTable = new JTable(contactsTableModel);
		contactsTable.setPreferredScrollableViewportSize(new Dimension(250, 96));
		contactsTable.setFillsViewportHeight(true);
		
		contactsTable.getTableHeader().setDefaultRenderer(new HeaderRenderer());
		contactsTable.getTableHeader().setReorderingAllowed(false);
		
		// Set column header height
		contactsTable.getTableHeader()
				.setPreferredSize(
						new Dimension(contactsTable.getColumnModel()
								.getTotalColumnWidth(), 20));
		
		contactsTable.setColumnSelectionAllowed(false);
		contactsTable.setRowSelectionAllowed(true);
		contactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(contactsTable);

		// Displays the contacts
		contactsTableModel.displayContacts();
		
		// Add the scroll pane to this panel.
		add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the Table Model associated with the table
	 * 
	 */
	public ContactsTableModel getTableModel()
	{
		return contactsTableModel;
	}
	
	/**
	 * Returns the Contacts Table
	 * 
	 */
	public JTable getTable()
	{
		return contactsTable;
	}
	
	/**
	 * This class draws the column header of the table
	 * Allows for change in font, foreground and 
	 * background colour, border and text alignment.
	 * 
	 * Found at web site:
	 * http://www.chka.de/swing/table/faq.html
	 * 
	 * @author Christian Kaufhold (swing@chka.de)
	 *
	 */
	class HeaderRenderer extends DefaultTableCellRenderer 
	{
		public HeaderRenderer() {
			setHorizontalAlignment(SwingConstants.LEFT);
			setOpaque(true);

			// This call is needed because DefaultTableCellRenderer calls
			// setBorder()
			// in its constructor, which is executed after updateUI()
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		}

		public void updateUI() {
			super.updateUI();
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focused, int row,
				int column) {
			JTableHeader h = table != null ? table.getTableHeader() : null;

			if (h != null) {
				setEnabled(h.isEnabled());
				setComponentOrientation(h.getComponentOrientation());

				setForeground(h.getForeground());
				setBackground(h.getBackground());
				
				Font originalFont = h.getFont();
				Font boldFont = new Font(originalFont.getName(), Font.BOLD,
						originalFont.getSize());
				h.setFont(boldFont);
				setFont(h.getFont());
			} else {
				/*
				 * Use sensible values instead of random leftover values from
				 * the last call
				 */
				setEnabled(true);
				setComponentOrientation(ComponentOrientation.UNKNOWN);

				setForeground(UIManager.getColor("TableHeader.foreground"));
				setBackground(UIManager.getColor("TableHeader.background"));
				setFont(UIManager.getFont("TableHeader.font"));
			}

			setValue(value);

			return this;
		}
	}
	

}
