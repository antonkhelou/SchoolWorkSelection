/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MainFrameEmailViewPanel.java                                          
 * Short description of what's in this file: This is class creates
 * 	the Panel that displays all the contents of an email: this includes
 *  the  MainFrameEmailViewInfoPanel as well as a JEditorPane for the body
 *  of the email.     
 */
package ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import beans.EmailBean;

@SuppressWarnings("serial")
public class MainFrameEmailViewPanel extends JPanel
{
	private GridBagLayout gridBagLayout = null;
	private GridBagConstraints[] constraints = null;
	private MainFrameEmailViewInfoPanel infoPanel = null;
	private JEditorPane editorPane = null;
	
	/**
	 * Constructs the MainFrameEmailViewPanel
	 * 
	 */
	public MainFrameEmailViewPanel()
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
		this.setLayout(gridBagLayout);
		
		initializeConstraints();
		
		infoPanel = new MainFrameEmailViewInfoPanel();
		this.add(infoPanel, constraints[0]);
		
		editorPane = new JEditorPane();
		editorPane.setContentType("text/html;");
		editorPane.setEditable(false);
		editorPane.setPreferredSize(new Dimension(500,200));	
		editorPane.setText("");
		//this is used to set the cursor in the editor pane right
		//before the first character.
		editorPane.setCaretPosition(0); 
		this.add(new JScrollPane(editorPane),constraints[1]);
		
	}
	
	/**
	 * Method used to display the contents of an email
	 * 
	 */
	public void displayEmail(EmailBean email)
	{
		editorPane.setText(email.getBody());
		infoPanel.setSubject(email.getSubject());
		infoPanel.setFrom(email.getfromSender());
		infoPanel.setDate(email.getDate().toString());
		infoPanel.setTo(email.getTOReceiver());
		infoPanel.setCC(email.getCCReceiver());
		infoPanel.setBCC(email.getBCCReceiver());
	}
	
	/**
	 * Method used clear all the email information in this Panel
	 * 
	 */
	public void clearEmailDisplay()
	{
		infoPanel.clearTextFields();
		editorPane.setText("");
	}
	
	/**
	 * Initialize the GridBagConstraints
	 */
	private void initializeConstraints() 
	{
		constraints = new GridBagConstraints[2];
		
		constraints[0] = new GridBagConstraints();
		constraints[0].gridx = 0;
		constraints[0].gridy = 0;
		constraints[0].gridwidth = 1;
		constraints[0].weightx = 1.0;
		constraints[0].weighty = 0.05;
		constraints[0].fill = GridBagConstraints.BOTH;

		constraints[1] = new GridBagConstraints();
		constraints[1].gridx = 0;
		constraints[1].gridy = 1;
		constraints[1].gridwidth = 1;
		constraints[1].weightx = 1.0;
		constraints[1].weighty = 0.5;
		constraints[1].fill = GridBagConstraints.BOTH;
	}
}
