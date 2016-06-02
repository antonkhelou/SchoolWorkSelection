/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MainFrameMenuBar.java                                          
 * Short description of what's in this file: This is class creates
 * 	the MenuBar for the Main Frame.           
 */
package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.net.URL;
import java.util.ArrayList;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import beans.EmailBean;
import beans.FolderBean;
import data.JEEPDBManager;
import mail.MailProperties;
import model.EmailTableModel;
import application.JEEPApp;

@SuppressWarnings("serial")
public class MainFrameMenuBar extends JMenuBar
{
	private HelpSet helpSet = null;
	private HelpBroker helpBroker = null;
	private MailProperties props = null;
	private JEEPDBManager connection = null;
	private EmailTableModel emailTableModel = null;
	private JTable emailTable = null;
	private MainFrameTreePanel treePanel = null;
	private MainFrame gui = null;
	private JMenuItem editMenuItem = null;
	private JMenuItem forwardMenuItem = null;
	private JMenuItem replyMenuItem = null;
	private JMenuItem printMenuItem = null;
	private JMenu moveSubMenu = null;
	
		/**
		 * Constructs the MainFrameEmailViewInfoPanel
		 * 
		 */
	   public MainFrameMenuBar(JEEPDBManager connection, MailProperties props,
			   MainFramePanel mainPanel, MainFrame gui) 
	   {
		  super();
		  this.connection = connection;
		  this.props = props;
		  
		  //gets all the pieces it needs for the listeners from the mainPanel
		  this.emailTableModel = mainPanel.getTablePanel().getTableModel();
		  this.emailTable = mainPanel.getTablePanel().getTable();
		  this.treePanel = mainPanel.getTreePanel();
		  
		  this.gui = gui;
		  initialize();
	   }
	   
		/**
		 * Create the GUI 
		 */
	   private void initialize()
	   {
		   configureAndAddFileMenu();
		   configureAndAddMessageMenu();
		   configureAndAddToolsMenu();
		   configureAndAddHelpMenu();    
	   }
	   
		 /**
		  * Sets up the for the File menu in the menu bar
		  */
	   private void configureAndAddFileMenu()
	   {
		   JMenu fileMenu = new JMenu("File");
		   JMenu fileNewSubMenu = new JMenu("New");
		   
		   JMenuItem newMessageMenuItem = new JMenuItem("Message",'M');
		   newMessageMenuItem.addActionListener(new NewListener());
		   newMessageMenuItem.getAccessibleContext().setAccessibleDescription("The new message menu item");
		   fileNewSubMenu.add(newMessageMenuItem);
		   
		   JMenuItem newFolderMenuItem = new JMenuItem("Folder",'o');
		   newFolderMenuItem.addActionListener(new NewListener());
		   newFolderMenuItem.getAccessibleContext().setAccessibleDescription("The new folder menu item");
		   fileNewSubMenu.add(newFolderMenuItem);
		   
		   JMenuItem newContactMenuItem = new JMenuItem("Contact",'t');
		   newContactMenuItem.addActionListener(new NewListener());
		   newContactMenuItem.getAccessibleContext().setAccessibleDescription("The new contact menu item");
		   fileNewSubMenu.add(newContactMenuItem);
		   
		   fileNewSubMenu.setMnemonic(KeyEvent.VK_N);
		   fileNewSubMenu.getAccessibleContext().setAccessibleDescription("The new sub-menu");
	   
		   fileMenu.add(fileNewSubMenu);
		   
		   printMenuItem = new JMenuItem("Print",'P');
		   printMenuItem.addActionListener(new PrintListener());
		   printMenuItem.getAccessibleContext().setAccessibleDescription("The print menu item");
		   
		   fileMenu.add(printMenuItem);
		   
		   JMenuItem exitMenuItem = new JMenuItem("Exit",'x');
		   exitMenuItem.addActionListener(new ExitListener());
		   exitMenuItem.getAccessibleContext().setAccessibleDescription("The exit menu item");
		   
		   fileMenu.add(exitMenuItem);
		   
		   fileMenu.addMenuListener(new FileMenuListener());
		   fileMenu.setMnemonic(KeyEvent.VK_F);
		   fileMenu.getAccessibleContext().setAccessibleDescription("The file menu");
		   
		   this.add(fileMenu);
		   
	   }
	   
		 /**
		  * Sets up the for the Message menu in the menu bar
		  */
	   private void configureAndAddMessageMenu()
	   {
		   JMenu messageMenu = new JMenu("Message");
		   
	       JMenuItem newMessageMenuItem = new JMenuItem("New Message",'N');
	       newMessageMenuItem.addActionListener(new NewMessageListener());
	       newMessageMenuItem.getAccessibleContext().setAccessibleDescription("The new message menu item");
	       messageMenu.add(newMessageMenuItem);
	       
	       replyMenuItem = new JMenuItem("Reply",'R');
	       replyMenuItem.addActionListener(new ReplyListener());
	       replyMenuItem.getAccessibleContext().setAccessibleDescription("The reply menu item");
	       messageMenu.add(replyMenuItem);
	       
	       forwardMenuItem = new JMenuItem("Forward",'F');
	       forwardMenuItem.addActionListener(new ForwardListener());
	       forwardMenuItem.getAccessibleContext().setAccessibleDescription("The forward menu item");
	       messageMenu.add(forwardMenuItem);
	       
	       editMenuItem = new JMenuItem("Edit Message",'E');
	       editMenuItem.addActionListener(new EditMessageListener());
	       editMenuItem.getAccessibleContext().setAccessibleDescription("The edit menu item");
	       messageMenu.add(editMenuItem);
		   
	       // This sub menu will be populated though the Listener
	       moveSubMenu = new JMenu("Move");
	       moveSubMenu.addMenuListener(new MoveSubMenuListener());
	       moveSubMenu.getAccessibleContext().setAccessibleDescription("The move menu");
	       moveSubMenu.setMnemonic(KeyEvent.VK_O);
	       messageMenu.add(moveSubMenu);
	       
	       messageMenu.insertSeparator(3);
	       messageMenu.setMnemonic(KeyEvent.VK_M);
	       messageMenu.addMenuListener(new MessageMenuListener());
	       messageMenu.getAccessibleContext().setAccessibleDescription("The message menu");
	       
	       this.add(messageMenu);
	   }
	   
		 /**
		  * Sets up the for the Tools menu in the menu bar
		  */
	   private void configureAndAddToolsMenu()
	   {
		   JMenu toolsMenu = new JMenu("Tools");
		   
		   JMenuItem contactManagerMenuItem = new JMenuItem("Contact Manager...",'C');
		   contactManagerMenuItem.addActionListener(new ContactManagerListener());
		   contactManagerMenuItem.getAccessibleContext().setAccessibleDescription("The contact manager menu item");
	       toolsMenu.add(contactManagerMenuItem);
	       
	       JMenuItem optionsMenuItem = new JMenuItem("Configuration...",'O');
	       optionsMenuItem.addActionListener(new ConfigurationListener());
	       optionsMenuItem.getAccessibleContext().setAccessibleDescription("The configuration menu item");
	       toolsMenu.add(optionsMenuItem);
	       
	       toolsMenu.setMnemonic(KeyEvent.VK_T);
	       toolsMenu.getAccessibleContext().setAccessibleDescription("The tools menu");
	       
	       this.add(toolsMenu);
	   }
	   
		 /**
		  * Sets up the for the Help menu in the menu bar
		  */
	   private void configureAndAddHelpMenu()
	   {
			// Find the HelpSet file and create the HelpSet object:
			String helpHS = "hs/main.hs";
			ClassLoader cl = JEEPApp.class.getClassLoader();
			try {
				URL hsURL = HelpSet.findHelpSet(cl, helpHS);
				helpSet = new HelpSet(null, hsURL);
			} catch (Exception ee) {
				// Say what the exception really is
				System.out.println("HelpSet " + ee.getMessage());
				System.out.println("HelpSet " + helpHS + " not found");
				return;
			}
			// Create a HelpBroker object:
			helpBroker = helpSet.createHelpBroker();
			
		   JMenu helpMenu = new JMenu("Help");
		   
	       JMenuItem aboutMenuItem = new JMenuItem("Launch Help...",'u');
	       aboutMenuItem.addActionListener(new CSH.DisplayHelpFromSource(helpBroker));
	       aboutMenuItem.getAccessibleContext().setAccessibleDescription("The launch help menu item");
	       helpMenu.add(aboutMenuItem);
	       
	       helpMenu.setMnemonic(KeyEvent.VK_H);
	       helpMenu.getAccessibleContext().setAccessibleDescription("The help menu");
	       
	       this.add(helpMenu);
	   }
	   ///////////////////////////////////////////////////////////////

		/**
		 * This is a MenuListener for the fileMenu and its purpose
		 * is to enable or disable the print menu item upon click
		 * 
		 */
	   class FileMenuListener implements MenuListener
	   {
		public void menuCanceled(MenuEvent e) 
		{}

		public void menuDeselected(MenuEvent e) 
		{}

		public void menuSelected(MenuEvent e) 
		{
			int selRow = emailTable.getSelectedRow();

			if (selRow != -1) 
			{
				printMenuItem.setEnabled(true);
			}
			else
			{
				printMenuItem.setEnabled(false);
			}
		}
		   
	   }
	   
		/**
		 * Inner Class
		 * 
		 * Action listener for all the menu item in the newSubMenu under fileMenu
		 * 
		 */
	    class NewListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e)
			{	
				String menuItemText = ((JMenuItem) e.getSource()).getText();
				
				if(menuItemText.equals("Message"))
				{
					new ComposeFrame(connection,props);
				}
				else if(menuItemText.equals("Contact"))
				{
					new AddEditContactFrame(connection,null);
				}
				else if(menuItemText.equals("Folder"))
				{
					//creates an anonymous ActionEvent object based on an anonymous JMenuItem
					//in order to trigger the actionPerformed method of a folder in the treePanel,
					//which results in the creation of a new folder
					ActionEvent action = new ActionEvent(new JMenuItem("Add New Folder"),1,null);
					treePanel.actionPerformed(action);
				}
			}
		}
	    
		/**
		 * Inner Class
		 * 
		 * Action listener for the Print menu item on this menu bar
		 * 
		 */
	    class PrintListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e)
			{	
				int selRow = emailTable.getSelectedRow();
				EmailBean email = emailTableModel.getEmailData(selRow);

				String printString = "From: " + email.getfromSender() + "\n" +
				"Date: " + email.getDate() + "\n" +
				"Subject: " + email.getSubject() + "\n" +
				"To: " + createSingleString(email.getTOReceiver()) + "\n" +
				"CC: " + createSingleString(email.getCCReceiver()) + "\n" +
				"BCC: " + createSingleString(email.getBCCReceiver()) + "\n\n" +
				email.getBody();
				
				//the string must be stored in a container in order
				//to print
				JEditorPane emailPrintPane =  new JEditorPane();
				emailPrintPane.setText(printString);
				try {
					emailPrintPane.print();
				} catch (PrinterException e1) 
				{
					e1.printStackTrace();
				}
			}
			
			/**
			 * Private method used to make all the contents of a String array
			 * into 1 string. Note that each element extracted from the String
			 * array will be delimited by a ;
			 */
			public String createSingleString(String[] stringArray)
			{
				String string = "";
				
				for(String to: stringArray)
					if(!to.equals(""))
					 string += to + "; ";
				
				return string;
			}
		}

		/**
		 * Inner Class
		 * 
		 * Action listener for the exit menu item on this menu bar
		 * 
		 */
	   class ExitListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.doExit();
			}
		}
	   ///////////////////////////////////////////////////
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
					menuItem.addActionListener(new MoveListener());
					moveSubMenu.add(menuItem);
				}	
			}
			
		}
		
		/**
		 * This is a MenuListener for the Message menu
		 * 
		 */
	   class MessageMenuListener implements MenuListener
		{
			public void menuCanceled(MenuEvent e) 
			{}

			public void menuDeselected(MenuEvent e) 
			{}

			public void menuSelected(MenuEvent e)
			{
				int selRow = emailTable.getSelectedRow();

				//verifies if an email is selected in the table
				if (selRow != -1) 
				{
					//enables all the email manipulation menu item
					editMenuItem.setEnabled(true);
					forwardMenuItem.setEnabled(true);
					replyMenuItem.setEnabled(true);
					moveSubMenu.setEnabled(true);
					
					String selectedEmailContainingFolder = 
						emailTableModel.getEmailData(selRow).getContainingFolderName();
					
					//if the selected email's containing folder is one
					//of the three listed below, the edit menu item
					//will be disabled
					if(selectedEmailContainingFolder.equals("Inbox")||
							selectedEmailContainingFolder.equals("Sent")||
							selectedEmailContainingFolder.equals("Junk"))
					{
						editMenuItem.setEnabled(false);
					}

					//if the selected email's containing folder is one
					//of the three listed below, the reply menu item
					//will be disabled
					if(selectedEmailContainingFolder.equals("Outbox")||
							selectedEmailContainingFolder.equals("Sent")||
							selectedEmailContainingFolder.equals("Drafts"))
					{
						replyMenuItem.setEnabled(false);
					}
				}
				else
				{
					//if no email is selected, then all the email manipulation
					//menu items will be disabled
					editMenuItem.setEnabled(false);
					forwardMenuItem.setEnabled(false);
					replyMenuItem.setEnabled(false);
					moveSubMenu.setEnabled(false);
				}
			}
		}
	   
		/**
		 * Inner Class
		 * 
		 * Action listener for the New Message menu item under the Message Menu
		 * 
		 */
	   class NewMessageListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e)
			{	
				new ComposeFrame(connection,props);
			}
		}
	   
		/**
		 * Inner Class
		 * 
		 * Action listener for the Reply menu item under the Message Menu
		 * 
		 */
	   class ReplyListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e)
			{	
				int rowOfSelectedEmail = emailTable.getSelectedRow();
				String emailsContainingFolder = emailTableModel.getEmailData(rowOfSelectedEmail)
													.getContainingFolderName();
				
				if(emailsContainingFolder.equals("Inbox")||emailsContainingFolder.equals("Junk"))
					new ComposeFrame(connection,props,emailTableModel.getEmailData(rowOfSelectedEmail),
							ComposeFrame.REPLY);
			}
		}
	   
		/**
		 * Inner Class
		 * 
		 * Action listener for the Forward menu item under the Message Menu
		 * 
		 */
	   class ForwardListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e)
			{	
				int rowOfSelectedEmail = emailTable.getSelectedRow();	

				new ComposeFrame(connection,props,emailTableModel.getEmailData(rowOfSelectedEmail),
						ComposeFrame.FORWARD);
			}
		}
	   
		/**
		 * Inner Class
		 * 
		 * Action listener for the Edit menu item under the Message Menu
		 * 
		 */
	   class EditMessageListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e)
			{	
				int selRow = emailTable.getSelectedRow();
				
				new ComposeFrame(connection,props,emailTableModel.getEmailData(selRow),
						ComposeFrame.EDIT);
			}
		}
	   
		/**
		 * Inner Class
		 * 
		 * Action listener for all the menu item under the Move SubMenu
		 * 
		 */
	   class MoveListener implements ActionListener 
		{
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
	   ////////////////////////////////////
		/**
		 * Inner Class
		 * 
		 * Action listener for the Contact Manager menu item under the Tools Menu
		 * 
		 */
	   class ContactManagerListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e)
			{	
				new ContactsManagerFrame(connection);
			}
		}
	   
		/**
		 * Inner Class
		 * 
		 * Action listener for the Configurations menu item under the Tools Menu
		 * 
		 */
	   class ConfigurationListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e)
			{	
				//opens up the Configuration panel in a modal way.
				//which means the user is forced to exit the Configurations
				//panel in order to return to the Main Frame
				JDialog dlg = new JDialog();
				dlg.setTitle("Configuration");
				dlg.add(new ConfigurationPanel(props));
				dlg.setModal(true);
				dlg.pack();
				dlg.setVisible(true);
				
			}
		}
}
