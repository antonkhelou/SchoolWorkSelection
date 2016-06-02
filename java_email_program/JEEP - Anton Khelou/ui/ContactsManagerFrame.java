/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: ContactsManagerFrame.java                                          
 * Short description of what's in this file: This class is used
 * to instantiate an instance of the Contacts Manager frame.                             
 */
package ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import data.JEEPDBManager;

@SuppressWarnings("serial")
public class ContactsManagerFrame extends JFrame implements ActionListener
{
	private JEEPDBManager connection = null;
	private ContactsTablePanel tablePanel = null;

	/**
	* Constructs the ContactsManagerFrame
	* 
	*/
	public ContactsManagerFrame(JEEPDBManager connection)
	{
		super();
		this.connection = connection;
		initialize();
		this.setTitle("Contacts Manager");
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
		this.add(tablePanel,getConstraints(0,0,2,3));
		
		this.add(createButtonPanel(),getConstraints(2,0,1,3));
	
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
	 * Creates the button panel for three buttons. This method will make sure that
	 * the buttons will be centered on the right side of the frame.
	 */
	private JPanel createButtonPanel() {

		JPanel buttonPanel = new JPanel(new GridBagLayout());

		JButton[] buttons = new JButton[3];
		buttons[0] = new JButton("Add");
		buttons[0].addActionListener(this);
		buttons[1] = new JButton("Remove");
		buttons[1].addActionListener(this);
		buttons[2] = new JButton("Edit");
		buttons[2].addActionListener(this);
		setButtonsWidthAndHeight(buttons);

		buttonPanel.add(buttons[0], getConstraints(0, 0, 1, 1));
		buttonPanel.add(buttons[1], getConstraints(0, 1, 1, 1));
		buttonPanel.add(buttons[2], getConstraints(0, 2, 1, 1));

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
	 * This method is triggered when the user click on any button on the frame
	 * 
	 */
	public void actionPerformed(ActionEvent e) 
	{
		//Casts the source of the ActionEvent to a JButton and gets the
		//text displayed on the button
		String buttonText = ((JButton) e.getSource()).getText();

		if (buttonText.equals("Add")) 
		{
			new AddEditContactFrame(connection,tablePanel);
		} 
		else if (buttonText.equals("Remove")) 
		{
			connection.removeContact(tablePanel.getTableModel().getContactData(tablePanel.getTable().getSelectedRow()));
			tablePanel.getTableModel().displayContacts();
		} 
		else if (buttonText.equals("Edit"))
		{
			new AddEditContactFrame(connection,
					tablePanel.getTableModel().getContactData(tablePanel.getTable().getSelectedRow()),tablePanel);
		}
	}
}
