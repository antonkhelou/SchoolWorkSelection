/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: ComposeFrame.java                                          
 * Short description of what's in this file: This class is used to
 * 		to create a ComposeFrame(a frame where you can create an email).                              
 */
package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import javax.swing.*;
import data.JEEPDBManager;
import mail.MailProperties;
import beans.EmailBean;
import regex.RegexFormatter;

@SuppressWarnings("serial")
public class ComposeFrame extends JFrame
{
	private ComposeFrame me = null;
	private JEEPDBManager connection = null;
	private MailProperties props = null;
	private JFormattedTextField toField;
	private JFormattedTextField ccField;
	private JFormattedTextField bccField;
	private JTextField subjectField;
	private JEditorPane messageField;
	private JButton toButton;
	private JButton ccButton;
	private JButton bccButton;
	private JLabel subjectLabel;
	
	//constants used to define the difference purposes of the
	//compose frame.
	public static final int FORWARD = 1;
	public static final int REPLY = 2;
	public static final int EDIT = 3;

	/**
	 * Constructs the ComposeFrame for an "empty" new email
	 * 
	 */
	public ComposeFrame(JEEPDBManager connection, MailProperties props)
	{
		super();
		this.connection = connection;
		this.props = props;
		this.me = this;//used to access this class from inside an inner class
		initialize();
		this.setJMenuBar(new ComposeFrameMenuBar(connection, this, props));
		this.setTitle("Compose");
		this.pack();
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new Terminator());
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * Constructs the ComposeFrame for an edit, reply or forward of an existent email
	 * 
	 */
	public ComposeFrame(JEEPDBManager connection, MailProperties props, EmailBean email,int purpose) {
		super();
		this.connection = connection;
		this.props = props;
		
		//verifies what is the purpose of the Frame.
		//depending on what it is, it will build a particular GUI for that case.
		if(purpose == FORWARD)
		{
			initializeForward(email);
		}
		else if (purpose == REPLY)
		{
			initializeReply(email);
		}
		else if (purpose == EDIT)
		{
			initializeEdit(email);
		}
		
		this.me = this;//used to access this class from inside an inner class
		this.setJMenuBar(new ComposeFrameMenuBar(connection, this, props));
		this.setTitle("Compose");
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new Terminator());
		this.setLocationRelativeTo(null);
	}

	/**
	 * Create the GUI
	 */
	private void initialize() 
	{
		this.setLayout(new GridBagLayout());
		// Create the labels.
		toButton = new JButton("To:");
		toButton.addActionListener(new ToEventHandler());
		this.add(toButton, getConstraints(0, 0, 1, 1,GridBagConstraints.EAST));
		
		// Only accept valid email addresses
		String reg = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		Dimension textFieldSize = new Dimension(425,22);
		toField = new JFormattedTextField(new RegexFormatter(reg));
		toField.setColumns(40);
		toField.setMinimumSize(textFieldSize);
		this.add(toField, getConstraints(1, 0, 2, 1,GridBagConstraints.WEST));

		ccButton = new JButton("CC:");
		ccButton.addActionListener(new CCEventHandler());
		this.add(ccButton, getConstraints(0, 1, 1, 1,GridBagConstraints.EAST));
		
		// Only accept valid email addresses
		ccField = new JFormattedTextField(new RegexFormatter(reg));
		ccField.setColumns(40);
		ccField.setMinimumSize(textFieldSize);
		this.add(ccField, getConstraints(1, 1, 2, 1,GridBagConstraints.WEST));
		
		bccButton = new JButton("BCC:");
		bccButton.addActionListener(new BCCEventHandler());
		this.add(bccButton, getConstraints(0, 2, 1, 1,GridBagConstraints.EAST));
		
		// Only accept valid email addresses
		bccField = new JFormattedTextField(new RegexFormatter(reg));
		bccField.setColumns(40);
		bccField.setMinimumSize(textFieldSize);
		this.add(bccField, getConstraints(1, 2, 2, 1,GridBagConstraints.WEST));
		
		
		subjectLabel = new JLabel("Subject:");
		this.add(subjectLabel, getConstraints(0, 3, 1, 1,GridBagConstraints.EAST));

		subjectField = new JTextField(40);
		subjectField.setMinimumSize(textFieldSize);
		this.add(subjectField, getConstraints(1, 3, 2, 1,GridBagConstraints.WEST));

		messageField = new JEditorPane();
		messageField.setContentType("text/html;");
		messageField.setEditable(true);
		messageField
				.setPreferredSize(new Dimension(500,300));
		JScrollPane jb = new JScrollPane(messageField);
		jb.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jb.setMinimumSize(new Dimension(500,300));
		this.add(jb, getConstraints(0, 4, 3, 1,GridBagConstraints.CENTER));
		
		JButton[] receipientButtons = new JButton[3];
		receipientButtons[0] = toButton;
		receipientButtons[1] = ccButton;
		receipientButtons[2] = bccButton;
		setButtonsWidthAndHeight(receipientButtons);

		this.add(createButtonPanel(), getConstraints(0, 5, 3, 1,GridBagConstraints.CENTER));
	}
	
	/**
	 * Create the GUI for a reply
	 */
	private void initializeReply(EmailBean email) 
	{
		initialize();
		toField.setText(email.getfromSender());
		subjectField.setText("RE: " + email.getSubject());
		
		String messageText = "<br><br>" +
				"_________________________________________________________________<br>" +
				"Date: " + email.getDate() + "<br>" +
				"From: " + email.getfromSender() + "<br>" +
				"Subject: " + email.getSubject() + "<br>" +
				"To: " + createSingleString(email.getTOReceiver()) + "<br><br>" +
				email.getBody();
		
		messageField.setText(messageText);
	}
	
	/**
	 * Create the GUI for a forward
	 */
	private void initializeForward(EmailBean email) 
	{
		initialize();
		subjectField.setText("FW: " + email.getSubject());
		
		String messageText = "<br><br>" +
		"_________________________________________________________________<br>" +
		"Date: " + email.getDate() + "<br>" +
		"From: " + email.getfromSender() + "<br>" +
		"Subject: " + email.getSubject() + "<br>" +
		"To: " + createSingleString(email.getTOReceiver()) + "<br><br>" +
		email.getBody();
		
		messageField.setText(messageText);
	}
	
	/**
	 * Create the GUI for an edit
	 */
	private void initializeEdit(EmailBean email) 
	{
		initialize();
		toField.setText(createSingleString(email.getTOReceiver()));
		ccField.setText(createSingleString(email.getCCReceiver()));
		bccField.setText(createSingleString(email.getBCCReceiver()));
		subjectField.setText(email.getSubject());	
		messageField.setText(email.getBody());
	}
	
	/**
	 * Private method used to make all the contents of a String array
	 * into 1 string. Note that each element extracted from the String
	 * array will be delimited by a ;
	 */
	private String createSingleString(String[] stringArray)
	{
		String string = "";
		
		for(String to: stringArray)
			if(!to.equals(""))
			 string += to + "; ";
		
		return string;
	}

	/**
	 * Used so that the two buttons are centered at the bottom of the GUI place
	 * them in their own panel
	 * 
	 * @return JPanel
	 */
	private JPanel createButtonPanel() {

		JPanel buttonPanel = new JPanel(new GridBagLayout());

		JButton[] buttons = new JButton[2];
		buttons[0] = new JButton("Send");
		buttons[0].addActionListener(new SendEventHandler());
		buttons[1] = new JButton("Clear");
		buttons[1].addActionListener(new ClearEventHandler());
		setButtonsWidthAndHeight(buttons);

		buttonPanel.add(buttons[0], getConstraints(0, 0, 1, 1,GridBagConstraints.WEST));
		buttonPanel.add(buttons[1], getConstraints(1, 0, 1, 1,GridBagConstraints.EAST));

		return buttonPanel;
	}

	/**
	 * A method for setting grid bag constraints
	 * 
	 * @param gridx
	 * @param gridy
	 * @param gridwidth
	 * @param gridheight
	 * @param anchor
	 * @return
	 */
	private GridBagConstraints getConstraints(int gridx, int gridy,
			int gridwidth, int gridheight,int anchor) {
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.insets = new Insets(5, 5, 5, 5);
		constraint.gridx = gridx;
		constraint.gridy = gridy;
		constraint.gridwidth = gridwidth;
		constraint.gridheight = gridheight;
		constraint.anchor = anchor;
		return constraint;
	}

	
	/**
	 * This method determines which button is widest and uses that to make all
	 * buttons the same width and height
	 * 
	 * @param buttons
	 */
	private void setButtonsWidthAndHeight(JButton[] buttons) {

		Dimension newSize = buttons[0].getPreferredSize();
		Dimension currentSize = null;

		// Get the size of each button and preserve the widest
		for (int x = 0; x < buttons.length; x++) {
			currentSize = buttons[x].getPreferredSize();
			if (currentSize.width > newSize.width)
				newSize.width = currentSize.width;
		}

		// Set the new preferred size for each button
		for (int x = 0; x < buttons.length; x++) {
			buttons[x].setPreferredSize(newSize);
		}
	}

	/**
	 * Checks that all the required fields are filled out before a message is sent
	 * 
	 * @return success or failure
	 */
	private boolean checkFields() {
		boolean retVal = true;

		if (toField.getText().length() == 0) {
			retVal = false;
			JOptionPane
					.showMessageDialog(
							this,
							"You have not entered an email \naddress to send this message to.",
							"Missing Field", JOptionPane.ERROR_MESSAGE);
		} else if (retVal && subjectField.getText().length() == 0) {
			retVal = false;
			JOptionPane.showMessageDialog(this,
					"You have not entered a subject \nafor this message.",
					"Missing Field", JOptionPane.ERROR_MESSAGE);
		} else if (retVal && messageField.getText().length() == 0) {
			retVal = false;
			JOptionPane.showMessageDialog(this,
					"You have not entered a message \nafor this message.",
					"Missing Field", JOptionPane.ERROR_MESSAGE);
		}
		return retVal;
	}
	
	/**
	 * Returns the To text field
	 * 
	 */
	public JFormattedTextField getToField() 
	{
		return toField;
	}

	/**
	 * Returns the CC text field
	 * 
	 */
	public JFormattedTextField getCcField() 
	{
		return ccField;
	}

	/**
	 * Returns the BCC text field
	 * 
	 */
	public JFormattedTextField getBccField() 
	{
		return bccField;
	}

	/**
	 * Returns the Subject text field
	 * 
	 */
	public JTextField getSubjectField() 
	{
		return subjectField;
	}

	/**
	 * Returns the Message(body) editor pane
	 * 
	 */
	public JEditorPane getMessageField() 
	{
		return messageField;
	}
	
	/**
	 * Method used by the exit menu item of the menu bar
	 * 
	 */
	public void doExit()
	{
		//creates a WindowEvent and processes it
		//in order to trigger the custom windowClosing
		//coded below
		this.processWindowEvent(new WindowEvent(this,
                WindowEvent.WINDOW_CLOSING));
	}
	
	/**
	 * Inner Class
	 * 
	 * Dialog box to confirm frame exit
	 */
	private class Terminator extends WindowAdapter {
		
		/**
		 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
		 */
		public void windowClosing(WindowEvent e) {
			int i = javax.swing.JOptionPane.showConfirmDialog(null,
					"Are you sure you want to exit this frame?", "Exit",
					javax.swing.JOptionPane.YES_NO_OPTION);
			
			//If yes, dispose of the frame
			if (i == javax.swing.JOptionPane.YES_OPTION)
			{
				me.dispose();
			}
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Send button on this frame
	 * 
	 */
	class SendEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			if (checkFields())
			{
				//creates String arrays out of the contents inside the recipient 
				//text fields. Note: that this method uses a ; as a delimiter.
				String[] toData = toField.getText().split(";");
				String[] ccData = ccField.getText().split(";");
				String[] bccData = bccField.getText().split(";");
				
				//creates an email based on all the information inputed by the user
				EmailBean email = new EmailBean(props.getConfigurations().getEmailAddress(),
						toData, ccData, bccData, subjectField.getText(), messageField.getText(),
						"Outbox", new Date());
				
				//adds the email to the database and proceeds to clear all the fields
				if(connection.addEmail(email))
				{
					JOptionPane.showMessageDialog(me, "Email has been store in Outbox.");
					toField.setText("");
					ccField.setText("");
					bccField.setText("");
					subjectField.setText("");
					messageField.setText("");
				}
			}
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the Clear button on this frame
	 * 
	 */
	class ClearEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			toField.setText("");
			ccField.setText("");
			bccField.setText("");
			subjectField.setText("");
			messageField.setText("");
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the To button on this frame
	 * 
	 */
	class ToEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			//creates a ComposeContactsFrame and specifies that the contacts should be
			//added in the To text field of this form.
			new ComposeContactsFrame(me,connection,ComposeContactsFrame.TO);
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the CC button on this frame
	 * 
	 */
	class CCEventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) 
		{
			//creates a ComposeContactsFrame and specifies that the contacts should be
			//added in the CC text field of this form.
			new ComposeContactsFrame(me,connection,ComposeContactsFrame.CC);
		}
	}
	
	/**
	 * Inner Class
	 * 
	 * Action listener for the BCC button on this frame
	 * 
	 */
	class BCCEventHandler implements ActionListener 
	{

		public void actionPerformed(ActionEvent e) 
		{
			//creates a ComposeContactsFrame and specifies that the contacts should be
			//added in the BCC text field of this form.
			new ComposeContactsFrame(me,connection,ComposeContactsFrame.BCC);
		}
	}

}
