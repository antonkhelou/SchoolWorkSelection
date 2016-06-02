/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: ComposeContactsFrame.java                                          
 * Short description of what's in this file: This class is used by the 
 *  ComposeFrame when a user clicks on either one of the recipient buttons                                  
 */
package ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import beans.ContactBean;
import data.JEEPDBManager;

@SuppressWarnings("serial")
public class ComposeContactsFrame extends JFrame implements ActionListener
{
	private ComposeFrame compFrame = null;
	private ContactsTablePanel tablePanel = null;
	private JEEPDBManager connection = null;
	private int field = 0;
	public static final int TO = 1;
	public static final int CC = 2;
	public static final int BCC = 3;
	
	/**
	 * Constructs the ComposeContactsFrame
	 * 
	 */
	public ComposeContactsFrame(ComposeFrame compFrame, JEEPDBManager connection, int field)
	{
		super();
		//field is used to see which button is pressed
		this.field = field;
		this.compFrame = compFrame;
		this.connection = connection;
		initialize();
		this.setTitle("Contacts");
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

	/**
	 * Create the GUI
	 */
	private void initialize() 
	{
		this.setLayout(new GridBagLayout());
		
		tablePanel = new ContactsTablePanel(connection);
		this.add(tablePanel,getConstraints(0,0,1,1));
		
		JButton addButton = new JButton("Add Contact");
		addButton.addActionListener(this);
		this.add(addButton,getConstraints(0,1,1,1));
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
			int gridwidth, int gridheight) {
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.insets = new Insets(5, 5, 5, 5);
		constraint.gridx = gridx;
		constraint.gridy = gridy;
		constraint.gridwidth = gridwidth;
		constraint.gridheight = gridheight;
		return constraint;
	}
	
	/**
	 * This method is triggered when the user click on a button on the frame
	 * 
	 */
	public void actionPerformed(ActionEvent e) 
	{
		//gets the selected row from the table
		int selectedRow = tablePanel.getTable().getSelectedRow();
		
		if(selectedRow != -1)
		{
			ContactBean selectedContact = tablePanel.getTableModel().getContactData(selectedRow);
			
			//verifies which button was pressed
			if(field==TO)
			{
				String currentText = compFrame.getToField().getText();
				
				if(currentText.equals(""))
				{
					compFrame.getToField().setText(selectedContact.getEmailAddress()+ ";");
				}
				else
				{
					compFrame.getToField().setText(currentText +
							selectedContact.getEmailAddress()+";");
				}
			}
			else if(field==CC)
			{
				String currentText = compFrame.getCcField().getText();
				
				if(currentText.equals(""))
				{
					compFrame.getCcField().setText(selectedContact.getEmailAddress()+ ";");
				}
				else
				{
					compFrame.getCcField().setText(currentText +
							selectedContact.getEmailAddress()+ ";");
				}
			}
			else if(field==BCC)
			{
				String currentText = compFrame.getBccField().getText();
				
				if(currentText.equals(""))
				{
					compFrame.getBccField().setText(selectedContact.getEmailAddress()+ ";");
				}
				else
				{
					compFrame.getBccField().setText(currentText +
							selectedContact.getEmailAddress() + ";");
				}
			}
			//method used to simply remove the contact chosen from the table
			//temporarily. If another instance of this class is created, the 
			//previously removed contact will reappear.
			tablePanel.getTableModel().removeContactFromView(selectedRow);
		}
	}
	
}
