/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: AddEditContactFrame.java                                              
 * Short description of what's in this file: This class is called when the user
 *  wants to add or edit a contact.                                   
 */
package ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import beans.ContactBean;

import regex.RegexFormatter;

import data.JEEPDBManager;

@SuppressWarnings("serial")
public class AddEditContactFrame extends JFrame implements ActionListener
{
	private JEEPDBManager connection = null;
	private ContactsTablePanel contactsTable = null;
	private JLabel firstNameLabel = null;
	private JLabel lastNameLabel = null;
	private JLabel emailAddressLabel = null;
	private JLabel telephoneNumberLabel = null;
	private JTextField firstNameTextField = null;
	private JTextField lastNameTextField = null;
	private JTextField emailAddressTextField = null;
	private JTextField telephoneNumberTextField = null;
	private int idHolderForEdit = -1;
	
	/**
	 * Constructs the AddEditContactFrame for adding a new contact
	 * 
	 */
	public AddEditContactFrame(JEEPDBManager connection,ContactsTablePanel contactsTable)
	{
		super();
		this.connection = connection;
		this.contactsTable = contactsTable;
		initialize();
		this.setTitle("Add/Edit Contact");
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * Constructs the AddEditContactFrame for editing an existent contact
	 * 
	 */
	public AddEditContactFrame(JEEPDBManager connection ,ContactBean contact,ContactsTablePanel contactsTable)
	{
		super();
		this.connection = connection;
		this.contactsTable = contactsTable;
		initialize(contact);
		this.setTitle("Add/Edit Contact");
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * Create the GUI for adding a new contact
	 */
	private void initialize() 
	{
		this.setLayout(new GridBagLayout());

		firstNameLabel = new JLabel("First Name");
		this.add(firstNameLabel,getConstraints(0,0,1,1,GridBagConstraints.EAST));
		
		firstNameTextField = new JTextField(20);
		this.add(firstNameTextField,getConstraints(1,0,1,1,GridBagConstraints.WEST));
		
		lastNameLabel = new JLabel("Last Name");
		this.add(lastNameLabel,getConstraints(0,1,1,1,GridBagConstraints.EAST));
		
		lastNameTextField = new JTextField(20);
		this.add(lastNameTextField,getConstraints(1,1,1,1,GridBagConstraints.WEST));
		
		emailAddressLabel = new JLabel("Email Address");
		this.add(emailAddressLabel,getConstraints(0,2,1,1,GridBagConstraints.EAST));
		
		//creates the regex string used to validate what can be typed in the text fields
		String reg = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		emailAddressTextField = new JFormattedTextField(new RegexFormatter(reg));
		emailAddressTextField.setColumns(20);
		this.add(emailAddressTextField,getConstraints(1,2,1,1,GridBagConstraints.WEST));

		telephoneNumberLabel = new JLabel("Phone Number");
		this.add(telephoneNumberLabel,getConstraints(0,3,1,1,GridBagConstraints.EAST));
		
		telephoneNumberTextField = new JTextField(15);
		this.add(telephoneNumberTextField,getConstraints(1,3,1,1,GridBagConstraints.WEST));
		
		this.add(createButtonPanel(), getConstraints(0, 5, 3, 1,GridBagConstraints.CENTER));

	}
	
	/**
	 * Create the GUI for editing an existent contact
	 * 
	 */
	private void initialize(ContactBean contact) 
	{
		//calls the initialize method for a new contact and proceeds to fill in the data into the fields.
		initialize();
		firstNameTextField.setText(contact.getFirstName());
		lastNameTextField.setText(contact.getLastName());
		emailAddressTextField.setText(contact.getEmailAddress());
		telephoneNumberTextField.setText(contact.getTelephoneNumber());	
		idHolderForEdit = contact.getId();//gets the id for the ActionListener
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
			int gridwidth, int gridheight, int anchor) {
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
	 * Used so that the two buttons are centered at the bottom of the GUI place
	 * them in their own panel
	 * 
	 * @return JPanel
	 */
	private JPanel createButtonPanel() {

		JPanel buttonPanel = new JPanel(new GridBagLayout());

		JButton[] buttons = new JButton[2];
		buttons[0] = new JButton("Save");
		buttons[0].addActionListener(this);
		buttons[1] = new JButton("Clear");
		buttons[1].addActionListener(this);
		setButtonsWidthAndHeight(buttons);

		buttonPanel.add(buttons[0], getConstraints(0, 0, 1, 1,GridBagConstraints.WEST));
		buttonPanel.add(buttons[1], getConstraints(1, 0, 1, 1,GridBagConstraints.EAST));

		return buttonPanel;
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
	 * Inner Class
	 * 
	 * Action listener for the JButtons on this frame
	 * 
	 */
	public void actionPerformed(ActionEvent e) 
	{

		String buttonText = ((JButton) e.getSource()).getText();

		if (buttonText.equals("Save")) 
		{
			if(checkFields())
			{
				ContactBean contact = null;
				
				//verifies if it is an email that is being edited
				if(idHolderForEdit==-1)
				{
					contact = new ContactBean(firstNameTextField.getText(),
							lastNameTextField.getText(),emailAddressTextField.getText(),
							telephoneNumberTextField.getText());
				}
				else
				{
					contact = new ContactBean(idHolderForEdit,firstNameTextField.getText(),
							lastNameTextField.getText(),emailAddressTextField.getText(),
							telephoneNumberTextField.getText());
				}
				
				//if the add or editing of the contact is successful, then
				//clear all the text fields and display a message
				if(connection.addOrEditContact(contact))
				{
					firstNameTextField.setText("");
					lastNameTextField.setText("");
					emailAddressTextField.setText("");
					telephoneNumberTextField.setText("");
					JOptionPane.showMessageDialog(this, "Contact has been added/edited.");
				}
				
				//updates the contactsTable if it is opened.
				if(contactsTable != null)
				{
					contactsTable.getTableModel().displayContacts();
				}
			}
		} 
		else if (buttonText.equals("Clear")) 
		{
			firstNameTextField.setText("");
			lastNameTextField.setText("");
			emailAddressTextField.setText("");
			telephoneNumberTextField.setText("");
		}
	}
	
	/**
	 * Checks that all fields are filled out before a message is sent
	 * 
	 * @return success or failure
	 */
	private boolean checkFields()
	{
		boolean retVal = true;

		if (firstNameTextField.getText().length() == 0) 
		{
			retVal = false;
			JOptionPane
					.showMessageDialog(
							this,
							"You have not entered a first name \nfor this contact.",
							"Missing Field", JOptionPane.ERROR_MESSAGE);
		} 
		else if (lastNameTextField.getText().length() == 0) 
		{
			retVal = false;
			JOptionPane.showMessageDialog(this,
					"You have not entered a last name \nfor this contact.",
					"Missing Field", JOptionPane.ERROR_MESSAGE);
		} 
		else if (emailAddressTextField.getText().length() == 0) 
		{
			retVal = false;
			JOptionPane.showMessageDialog(this,
					"You have not entered an email address \nfor this contact.",
					"Missing Field", JOptionPane.ERROR_MESSAGE);
		}
		else if (telephoneNumberTextField.getText().length() == 0) 
		{
			retVal = false;
			JOptionPane.showMessageDialog(this,
					"You have not entered a phone number \nfor this contact.",
					"Missing Field", JOptionPane.ERROR_MESSAGE);
		}
		return retVal;
	}
	
}
