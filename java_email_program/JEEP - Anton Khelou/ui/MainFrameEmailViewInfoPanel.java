/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MainFrameEmailViewInfoPanel.java                                          
 * Short description of what's in this file: This is class creates
 * 	the Panel that displays all the email information on the main
 *  frame.              
 */
package ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class MainFrameEmailViewInfoPanel extends JPanel
{
	private static final int FONT_SIZE = 12;
	private GridBagLayout gridBagLayout = null;
	private GridBagConstraints[] constraints = null;
	private JLabel[] infoLabels = null;
	private JTextField[] textFields = null;
	private String[] infoLabelsText = {"Subject:","From:","Date:","To:","CC:","BCC:"};
	
	/**
	 * Constructs the MainFrameEmailViewInfoPanel
	 * 
	 */
	public MainFrameEmailViewInfoPanel()
	{
		super();
		initialize();
	}

	/**
	 * Create the GUI 
	 */
	private void initialize() 
	{
		gridBagLayout = new GridBagLayout();
		// Sets the layout
		this.setLayout(gridBagLayout);
		
		initializeConstraints();
		// Creates the labels and places on display
		createAndPlaceInfoLabels();
		createAndPlaceTextFields();
		// them with the other buttons on the display
		this.setSize(gridBagLayout.preferredLayoutSize(this));
		this.setPreferredSize(gridBagLayout.preferredLayoutSize(this));
		this.setMinimumSize(gridBagLayout.preferredLayoutSize(this));
		this.setMaximumSize(gridBagLayout.preferredLayoutSize(this));
	}
	
	/**
	 * Sets the text in the Subject text field
	 * 
	 */
	public void setSubject(String subject)
	{
		textFields[0].setText(subject);
	}
	
	/**
	 * Sets the text in the From text field
	 * 
	 */
	public void setFrom(String from)
	{
		textFields[1].setText(from);
	}
	
	/**
	 * Sets the text in the Date text field
	 * 
	 */
	public void setDate(String date)
	{
		textFields[2].setText(date);
	}
	
	/**
	 * Sets the text in the Subject text field
	 * 
	 */
	public void setTo(String[] toArray)
	{
		String toString = createSingleString(toArray);
		
		textFields[3].setText(toString);
	}
	
	/**
	 * Sets the text in the Subject text field
	 * 
	 */
	public void setCC(String[] ccArray)
	{
		String ccString = createSingleString(ccArray);
		
		textFields[4].setText(ccString);
	}
	
	/**
	 * Sets the text in the Subject text field
	 * 
	 */
	public void setBCC(String[] bccArray)
	{
		String bccString = createSingleString(bccArray);

		textFields[5].setText(bccString);
	}
	
	/**
	 * Clears all the text fields
	 * 
	 */
	public void clearTextFields()
	{
		for(JTextField l:textFields)
			l.setText("");
	}

	/**
	 * Private method used to make all the contents of a String array
	 * into 1 string. Note that each element extracted from the String
	 * array will be delimited by a ;
	 */
	private String createSingleString(String[] array)
	{
		String string = "";
		
		for(int i=0;i<array.length;i++)
			if(!array[i].equals(""))
				if(i==array.length-1)
					string += array[i];
				else
					string += array[i] + ";";
		
		return string;
	}
	
	/**
	 * Private method used to create and add all the text fields
	 * for this panel.
	 */
	private void createAndPlaceTextFields() 
	{
		textFields = new JTextField[6];
		for(int i = 0; i <6; i++)
		{
			textFields[i] = new JTextField(50);
			//By sending null, it will remove the borders of the text fields.
			textFields[i].setBorder(null);
			textFields[i].setEditable(false);
			textFields[i].setFont(new Font(Font.DIALOG, Font.PLAIN, FONT_SIZE));
			textFields[i].setText("");
			add(textFields[i], constraints[i+6]);
		}
	}

	/**
	 * Private method used to create and add all the panels
	 * for this panel.
	 */
	private void createAndPlaceInfoLabels() 
	{
		infoLabels = new JLabel[6];
		for(int i = 0; i < 6; i++)
		{
			infoLabels[i] = new JLabel(infoLabelsText[i]);
			infoLabels[i].setFont(new Font(Font.DIALOG, Font.BOLD, FONT_SIZE));
			add(infoLabels[i], constraints[i]);
		}
	}

	/**
	 * Initialize the GridBagConstraints
	 */
	private void initializeConstraints() 
	{
		constraints = new GridBagConstraints[12];
		int numOfCurrentConstraint = 0;
		
		// All buttons have the same insets
		for (int i = 0; i < 6; i++) 
		{
			constraints[numOfCurrentConstraint] = new GridBagConstraints();
			constraints[numOfCurrentConstraint].insets = new Insets(1, 1, 1, 1);
			constraints[numOfCurrentConstraint].gridx = 0;
			constraints[numOfCurrentConstraint].gridy = i;
			constraints[numOfCurrentConstraint].gridwidth = 1;
			constraints[numOfCurrentConstraint].weightx = 0.01;
			constraints[numOfCurrentConstraint].weighty = 1.0;
			constraints[numOfCurrentConstraint].anchor = GridBagConstraints.EAST;
			numOfCurrentConstraint++;
		}
		for (int i = 0; i < 6; i++) 
		{
			constraints[numOfCurrentConstraint] = new GridBagConstraints();
			constraints[numOfCurrentConstraint].insets = new Insets(1, 1, 1, 1);
			constraints[numOfCurrentConstraint].gridx = 1;
			constraints[numOfCurrentConstraint].gridy = i;
			constraints[numOfCurrentConstraint].gridwidth = 1;
			constraints[numOfCurrentConstraint].weightx = 1.0;
			constraints[numOfCurrentConstraint].weighty = 1.0;
			constraints[numOfCurrentConstraint].anchor = GridBagConstraints.WEST;
			numOfCurrentConstraint++;
		}
	}
}
