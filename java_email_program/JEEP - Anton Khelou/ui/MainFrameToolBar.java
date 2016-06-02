/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MainFrameToolBar.java                                          
 * Short description of what's in this file: This class is used to
 * 		to create the ToolBar for the Main Frame.                              
 */
package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import beans.EmailBean;
import mail.MailProperties;
import mail.MailReceiver;
import mail.MailSender;
import model.EmailTableModel;
import data.JEEPDBManager;

@SuppressWarnings("serial")
public class MainFrameToolBar extends JToolBar
{
	private JEEPDBManager connection = null;
	private MailProperties props = null;
	private EmailTableModel emailTableModel = null;
	private JTable emailTable = null;
	private JTree folderTree = null;
	private MainFrameEmailViewPanel emailViewPanel = null;
	private MailReceiver receiver = null;
	private MailSender sender = null;
	
	/**
	 * Constructs the MainFrameToolBar
	 * 
	 */
	public MainFrameToolBar(JEEPDBManager connection,MailProperties props, MainFramePanel mainPanel)
	{
		super();
		this.connection = connection;
		this.props = props;
		this.emailTableModel = mainPanel.getTablePanel().getTableModel();
		this.emailTable = mainPanel.getTablePanel().getTable();
		this.folderTree = mainPanel.getTreePanel().getTree();
		this.emailViewPanel = mainPanel.getEmailViewPanel();
		receiver = new MailReceiver(props);
		sender = new MailSender(props);
		initialize();
	}
	
	/**
	 * Creates the GUI
	 * 
	 */
	private void initialize()
	{
		JButton button = null;

        //first button
        button = makeToolBarButton("mail", "Get/Send mail", "Get/Send Mail");
        button.addActionListener(new GetSendEmailEventHandler());
        this.add(button);
        
        button = makeToolBarButton("mail_compose",
                "Compose a mail message",
                "Compose");
        button.addActionListener(new ComposeEmailEventHandler());
        this.add(button);
        
        this.addSeparator();

        button = makeToolBarButton("mail_forward",
                "Forward the current mail message",
                "Forward");
        button.addActionListener(new ForwardEmailEventHandler());
        this.add(button);
		
        button = makeToolBarButton("mail_reply",
                "Reply to the current mail message",
                "Reply");
        button.addActionListener(new ReplyEmailEventHandler());
        this.add(button);
        
        button = makeToolBarButton("mail_delete",
                "Delete a mail message",
                "Delete");
        button.addActionListener(new DeleteEmailEventHandler());
        this.add(button);
        
        button = makeToolBarButton("mail_junk",
                "Send mail message to junk mail",
                "Junk Mail");
        button.addActionListener(new SendToJunkEmailEventHandler());
        this.add(button);
        
        this.addSeparator();
        
        button = makeToolBarButton("contacts",
                "View contact manager",
                "Contacts");
        button.addActionListener(new ViewContactsEventHandler());
        this.add(button);
        
        button = makeToolBarButton("contacts_add",
                "Add a contact",
                "Contacts Add");
        button.addActionListener(new AddContactEventHandler());
        this.add(button);
 
        this.setFloatable(false);
        this.setRollover(true);
	}

	/**
	 * Create the buttons that will be placed in the tool bar
	 * 
	 * @param imageName
	 * @param actionCommand
	 * @param toolTipText
	 * @param altText
	 * @return
	 */
	private JButton makeToolBarButton(String imageName, String toolTipText, String altText) {

		// Look for the image.
		String imgLocation = "/images/" + imageName + ".png";
		URL imageURL = MainFrame.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setToolTipText(toolTipText);
		button.getAccessibleContext().setAccessibleDescription(toolTipText);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

		return button;
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Get/Send Button
	 * 
	 */
	class GetSendEmailEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			receiveMail();
			sendMail();
			if(!folderTree.isSelectionEmpty())
			{
				emailTableModel.displayEmailsInFolder(folderTree.getLastSelectedPathComponent().toString());
			}
		}
		
		private void receiveMail()
		{
			ArrayList<EmailBean> list = receiver.getMail();
			
			if(list.size()!=0)
			{
				for(EmailBean eb: list)
					connection.addEmail(eb);
			}

		}
		
		private void sendMail()
		{
			ArrayList<EmailBean> list = connection.getEmailsInFolder("Outbox");
			
			if(list.size()!=0)
			{
				for(EmailBean eb: list)
				{
					if(sender.sendMail(eb))
					{
						eb.setContainingFolderName("Sent");
						connection.editEmail(eb);
					}
				}
			}
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Compose Button
	 * 
	 */
	class ComposeEmailEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			new ComposeFrame(connection,props);
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Forward Button
	 * 
	 */
	class ForwardEmailEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{	
			int rowOfSelectedEmail = emailTable.getSelectedRow();
			
			if(rowOfSelectedEmail!=-1)
				new ComposeFrame(connection,props,emailTableModel.getEmailData(rowOfSelectedEmail),
						ComposeFrame.FORWARD);
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Reply Button
	 * 
	 */
	class ReplyEmailEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			int rowOfSelectedEmail = emailTable.getSelectedRow();
			if(rowOfSelectedEmail!=-1)
			{
				String emailsContainingFolder = emailTableModel.getEmailData(rowOfSelectedEmail)
													.getContainingFolderName();
				
				if(emailsContainingFolder.equals("Inbox")||emailsContainingFolder.equals("Junk"))
					new ComposeFrame(connection,props,emailTableModel.getEmailData(rowOfSelectedEmail),
							ComposeFrame.REPLY);
			}
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Delete Email Button
	 * 
	 */
	class DeleteEmailEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			int rowOfSelectedEmail = emailTable.getSelectedRow();
		
			if(rowOfSelectedEmail!=-1)
			{
				connection.removeEmail(emailTableModel.getEmailData(
						emailTable.getSelectedRow()));
				emailTableModel.displayEmailsInFolder(folderTree.getLastSelectedPathComponent().toString());
				emailViewPanel.clearEmailDisplay();
			}
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Send to Junk Mail Button
	 * 
	 */
	class SendToJunkEmailEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			int rowOfSelectedEmail = emailTable.getSelectedRow();
			
			if(rowOfSelectedEmail!=-1)
			{
				EmailBean editedEmail = emailTableModel.getEmailData(
						emailTable.getSelectedRow());
				editedEmail.setContainingFolderName("Junk");
				
				connection.editEmail(editedEmail);
				emailTableModel.displayEmailsInFolder(folderTree.getLastSelectedPathComponent().toString());
				emailViewPanel.clearEmailDisplay();
			}
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the View Contact Manager Button
	 * 
	 */
	class ViewContactsEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			new ContactsManagerFrame(connection);
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Add Contact Button
	 * 
	 */
	class AddContactEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			new AddEditContactFrame(connection,null);
		}
	}
}
