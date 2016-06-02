/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: ComposeFrameMenuBar.java                                          
 * Short description of what's in this file: This class is used to
 * 		to create a Menu Bar for the Compose Frame                              
 */
package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.util.Date;

import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import mail.MailProperties;
import beans.EmailBean;
import data.JEEPDBManager;


@SuppressWarnings("serial")
public class ComposeFrameMenuBar extends JMenuBar
{
	private ComposeFrame compFrame = null;
	private JEEPDBManager connection = null;
	private MailProperties props = null;
	private ComposeFrameMenuBar me =null;
	
	 /**
	  * Constructs the ComposeFrameMenuBar
	  * 
	  */
	  public ComposeFrameMenuBar(JEEPDBManager connection, ComposeFrame compFrame, MailProperties props) 
	  {
		  super();
		  this.connection = connection;
		  this.compFrame = compFrame;
		  this.props = props;
		  me = this;//used to access this class from inside an inner class
		  initialize();
	   }
	   
	  /**
	   * Create the GUI
	   */
	   private void initialize()
	   {
		   configureAndAddFileMenu(); 
	   }
	   
	  /**
	   * Sets up the for the File menu in the menu bar
	   */
	   private void configureAndAddFileMenu()
	   {
		   JMenu fileMenu = new JMenu("File");
		   
		   JMenuItem saveMenuItem = new JMenuItem("Save As Draft",'S');
		   saveMenuItem.addActionListener(new SaveListener());
		   saveMenuItem.getAccessibleContext().setAccessibleDescription("The save as draft menu item");
		   
		   fileMenu.add(saveMenuItem);
		   
		   JMenuItem printMenuItem = new JMenuItem("Print",'P');
		   printMenuItem.addActionListener(new PrintListener());
		   printMenuItem.getAccessibleContext().setAccessibleDescription("The print menu item");
		   
		   fileMenu.add(printMenuItem);
		   
		   JMenuItem exitMenuItem = new JMenuItem("Exit",'x');
		   exitMenuItem.addActionListener(new ExitListener());
		   exitMenuItem.getAccessibleContext().setAccessibleDescription("The exit menu item");
		   
		   fileMenu.add(exitMenuItem);
		   
		   fileMenu.setMnemonic(KeyEvent.VK_F);
		   fileMenu.getAccessibleContext().setAccessibleDescription("The file menu");
		   
		   this.add(fileMenu);
		   
	   }

		/**
		 * Inner Class
		 * 
		 * Action listener for the Save As Draft menu item on this menu bar
		 * 
		 */
	    class SaveListener implements ActionListener 
		{
			public void actionPerformed(ActionEvent e)
			{	
				//creates String arrays out of the contents inside the recipient 
				//text fields. Note: that this method uses a ; as a delimiter.
				String[] toData = compFrame.getToField().getText().split(";");
				String[] ccData = compFrame.getCcField().getText().split(";");
				String[] bccData = compFrame.getBccField().getText().split(";");
				
				//creates an email based on all the information inputed by the user
				EmailBean email = new EmailBean(props.getConfigurations().getEmailAddress(),
						toData, ccData, bccData,compFrame.getSubjectField().getText(),
						compFrame.getMessageField().getText(), "Drafts", new Date());
				
				//adds the email to the database
				if(connection.addEmail(email))
					JOptionPane.showMessageDialog(me, "Email has been stored in Drafts.");
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
				String printString = "From: " + props.getConfigurations().getEmailAddress() + "\n" +
				"Date: " + new Date() + "\n" +
				"Subject: " + compFrame.getSubjectField().getText() + "\n" +
				"To: " +  compFrame.getToField().getText() + "\n" +
				"CC: " + compFrame.getCcField().getText() + "\n" +
				"BCC: " + compFrame.getBccField().getText() + "\n\n" +
				compFrame.getMessageField().getText();
				
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
		}

		/**
		 * Inner Class
		 * 
		 * Action listener for the exit menu item on this menu bar
		 * 
		 */
	   class ExitListener implements ActionListener 
		{
			/**
			 * Send the button text to the input handler
			 * 
			 * @param e
			 *            the ActionEvent
			 */
			public void actionPerformed(ActionEvent e)
			{
				compFrame.doExit();
			}
		}
	  
}

