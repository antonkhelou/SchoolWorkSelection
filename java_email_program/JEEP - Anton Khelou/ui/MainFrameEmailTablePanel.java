/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MainFrameEmailTablePanel.java                                          
 * Short description of what's in this file: This is class creates
 * 	the Panel that will contain the Emails Table.                 
 */
package ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import beans.EmailBean;
import beans.FolderBean;
import data.JEEPDBManager;
import mail.MailProperties;
import model.EmailTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MainFrameEmailTablePanel extends JPanel
{

	private JTable emailTable = null;
	private EmailTableModel emailTableModel = null;
	private JEEPDBManager connection = null;
	private MailProperties props = null;
	private JMenuItem editMenuItem = null;
	private int currentRow;
	private MainFrameEmailViewPanel emailViewPanel = null;
	private JMenu moveSubMenu = null;

	/**
	 * Constructs the MainFrameEmailTablePanel
	 * 
	 */
	public MainFrameEmailTablePanel(JEEPDBManager connection, MailProperties props,
			MainFrameEmailViewPanel emailViewPanel) 
	{
		super(new BorderLayout());
		this.connection = connection;
		this.emailViewPanel = emailViewPanel;
		this.props = props;
		
		initialize();
	}
	
	/**
	 * Create the GUI 
	 */
	public void initialize() 
	{
		//Creates a TableModel for the emailTable
		emailTableModel = new EmailTableModel(connection);
		
		emailTable = new JTable(emailTableModel);
		emailTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		emailTable.setFillsViewportHeight(true);
		
		emailTable.getTableHeader().setDefaultRenderer(new HeaderRenderer());
		emailTable.getTableHeader().setReorderingAllowed(false);
		
		// Set column header height
		emailTable.getTableHeader()
				.setPreferredSize(
						new Dimension(emailTable.getColumnModel()
								.getTotalColumnWidth(), 20));
		
		emailTable.setColumnSelectionAllowed(false);
		emailTable.setRowSelectionAllowed(true);
		emailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		emailTable.setShowGrid(false);
		
        // Make the popup menu
		makeTablePopupMenu();
		
		ListSelectionModel rowSM = emailTable.getSelectionModel();
		rowSM.addListSelectionListener(new RowListener());

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(emailTable);

		// Add the scroll pane to this panel.
		add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Private method that is in charge of creating the PopupMenu
	 */
	private void makeTablePopupMenu() 
	{
		JMenuItem menuItem;

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		
		// This sub menu will be populated though the Listener
		moveSubMenu = new JMenu("Move Message");
		moveSubMenu.addMenuListener(new MoveSubMenuListener());
		popup.add(moveSubMenu);
		
		popup.addSeparator();
		
		editMenuItem = new JMenuItem("Edit Message");
		editMenuItem.addActionListener(new EditEmailListener());
		popup.add(editMenuItem);

		menuItem = new JMenuItem("Delete Message");
		menuItem.addActionListener(new DeleteEmailListener());
		popup.add(menuItem);

		// Add mouse listener to the text area so the popup menu can come up.
		MouseListener popupListener = new PopupListener(popup);
		
		// add listener to the tree
		emailTable.addMouseListener(popupListener);
		
	}

	/**
	 * Returns the Table Model associated with the table
	 * 
	 */
	public EmailTableModel getTableModel()
	{
		return emailTableModel;
	}
	
	/**
	 * Returns the Emails Table
	 * 
	 */
	public JTable getTable()
	{
		return emailTable;
	}
	
	/**
	 * Inner class that listens for row selection events
	 * 
	 */
	class RowListener implements ListSelectionListener 
	{
		public void valueChanged(ListSelectionEvent e) 
		{
			// Ignore extra messages.
			if (e.getValueIsAdjusting())
				return;

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (!lsm.isSelectionEmpty()) {
				currentRow = lsm.getMinSelectionIndex();
				// Send to the message panel the 
				// mail message data bean to display
				emailViewPanel.displayEmail(emailTableModel.getEmailData(currentRow));
			}
		}
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
	
	/**
	 * This is a mouse listener that displays the popup menu
	 * 
	 */
	class PopupListener extends MouseAdapter 
	{

		JPopupMenu popup;

		public PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) 
		{
			maybeShowPopup(e, false);
		}

		public void mouseReleased(MouseEvent e) 
		{
			maybeShowPopup(e, true);
		}

		private void maybeShowPopup(MouseEvent e, boolean pressed) {
			// If this is the right mouse button
			if (e.isPopupTrigger()) 
			{
				int selRow = emailTable.getSelectedRow();
				
				if (selRow != -1) 
				{
					String selectedEmailContainingFolder = 
						emailTableModel.getEmailData(selRow).getContainingFolderName();
					
					//verifies if the containing folder of the email is
					//the Inbox, Sent or Junk. If it is either one of those
					//three, the email will be uneditable
					if(selectedEmailContainingFolder.equals("Inbox")||
							selectedEmailContainingFolder.equals("Sent")||
							selectedEmailContainingFolder.equals("Junk"))
					{
						editMenuItem.setEnabled(false);
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
					else
					{
						editMenuItem.setEnabled(true);
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			} 
		}
	}
	
	/**
	 * This is a MenuListener for the moveSubMenu
	 * 
	 */
	class MoveSubMenuListener implements MenuListener
	{
		public void menuCanceled(MenuEvent e) 
		{}

		public void menuDeselected(MenuEvent e) 
		{}

		public void menuSelected(MenuEvent e) 
		{
			//removes all the items in the Menu to avoid concatenation
			moveSubMenu.removeAll();
			
			//gets all the folders and proceeds to add them to the menu
			ArrayList<FolderBean> folderData = connection.getFoldersData();
			for(FolderBean fb: folderData)
			{
				JMenuItem menuItem = new JMenuItem(fb.getFolderName());
				menuItem.addActionListener(new MoveEmailListener());
				moveSubMenu.add(menuItem);
			}	
		}
		
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Move menuItems on the popup menu
	 * 
	 */
	class MoveEmailListener implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			//Casts the source of the ActionEvent to a JButton and gets the
			//text displayed on the button
			String menuItemText = ((JMenuItem) e.getSource()).getText();
			int selRow = emailTable.getSelectedRow();
			
			//Gets the email based on the row selected from the table model
			EmailBean email = emailTableModel.getEmailData(selRow);
			//Retrieves the of the containing folder before the change
			String originalFolderName = email.getContainingFolderName();
	
			//Sets the emails containing folder to the new one based
			//on the text retrieved from the source of the ActionEvent
			email.setContainingFolderName(menuItemText);
			
			connection.editEmail(email);
			
			//displays the email in the folder in order to reflect the change visually
			emailTableModel.displayEmailsInFolder(originalFolderName);
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Edit menuItem on the popup menu
	 * 
	 */
	class EditEmailListener implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			int selRow = emailTable.getSelectedRow();
			EmailBean email = emailTableModel.getEmailData(selRow);
			
			new ComposeFrame(connection,props,email,ComposeFrame.EDIT);
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Delete menuItem on the popup menu
	 * 
	 */
	class DeleteEmailListener implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			int selRow = emailTable.getSelectedRow();
			EmailBean email = emailTableModel.getEmailData(selRow);
			
			connection.removeEmail(email);
			
			//displays the email in the folder in order to reflect the change visually
			emailTableModel.displayEmailsInFolder(email.getContainingFolderName());
		}
	}
}
