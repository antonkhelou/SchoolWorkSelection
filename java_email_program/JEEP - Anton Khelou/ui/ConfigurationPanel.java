/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: ConfigurationPanel.java                                          
 * Short description of what's in this file: This class is used to
 * 	create a ConfigurationPanel. Originally, this class was suppose
 * 	to be a JFrame, but in order to make it Modal, it needs to be a
 * 	JPanel.                               
 */
package ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mail.MailProperties;


@SuppressWarnings("serial")
public class ConfigurationPanel extends JPanel implements ActionListener
{
	private JTextField userNameTextField = null;
	private JTextField emailAddressTextField = null;
	private JTextField pop3ServerURLTextField = null;
	private JTextField pop3ServerUserNameTextField = null;
	private JTextField pop3ServerPasswordTextField = null;
	private JTextField pop3ServerPortNumberTextField = null;
	private JTextField smtpServerURLTextField = null;
	private JTextField smtpServerPortNumberTextField = null;
	private JCheckBox isGMailAccountCheckBox = null;
	private JTextField mySQLdatabaseURLTextField = null;
	private JTextField mySQLdatabasePortNumberTextField = null;
	private JTextField mySQLdatabaseUserNameTextField = null;
	private JTextField mySQLdatabasePasswordTextField = null;
	private MailProperties props = null;

   /**
	* Constructs the ConfigurationPanel
	* 
	*/
	public ConfigurationPanel(MailProperties props)
	{
		super();
		this.props = props;
		initialize();
	}

	/**
	 * Create the GUI
	 */
	private void initialize() 
	{
		this.setLayout(new GridBagLayout());
		
		this.add(new JLabel("User Name: "), getConstraints(0, 0, 1, 1,GridBagConstraints.EAST));
		
		userNameTextField = new JTextField();
		userNameTextField.setColumns(20);
		userNameTextField.setText( props.getConfigurations().getUserName());
		this.add(userNameTextField, getConstraints(1, 0, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("Email Address: "), getConstraints(0, 1, 1, 1,GridBagConstraints.EAST));
		
		emailAddressTextField = new JTextField();
		emailAddressTextField.setColumns(20);
		emailAddressTextField.setText( props.getConfigurations().getEmailAddress());
		this.add(emailAddressTextField, getConstraints(1, 1, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("SMTP Server URL: "), getConstraints(0, 2, 1, 1,GridBagConstraints.EAST));
		
		smtpServerURLTextField = new JTextField();
		smtpServerURLTextField.setColumns(20);
		smtpServerURLTextField.setText( props.getConfigurations().getSmtpServerURL());
		this.add(smtpServerURLTextField, getConstraints(1, 2, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("SMTP Server Port Number: "), getConstraints(0, 3, 1, 1,GridBagConstraints.EAST));
		
		smtpServerPortNumberTextField = new JTextField();
		smtpServerPortNumberTextField.setColumns(20);
		smtpServerPortNumberTextField.setText( props.getConfigurations().getSmtpServerPortNumber());
		this.add(smtpServerPortNumberTextField, getConstraints(1, 3, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("POP3 Server URL: "), getConstraints(0, 4, 1, 1,GridBagConstraints.EAST));
		
		pop3ServerURLTextField = new JTextField();
		pop3ServerURLTextField.setColumns(20);
		pop3ServerURLTextField.setText( props.getConfigurations().getPop3ServerURL());
		this.add(pop3ServerURLTextField, getConstraints(1, 4, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("POP3 Server User Name: "), getConstraints(0, 5, 1, 1,GridBagConstraints.EAST));
		
		pop3ServerUserNameTextField = new JTextField();
		pop3ServerUserNameTextField.setColumns(20);
		pop3ServerUserNameTextField.setText( props.getConfigurations().getPop3ServerUserName());
		this.add(pop3ServerUserNameTextField, getConstraints(1, 5, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("POP3 Server Password: "), getConstraints(0, 6, 1, 1,GridBagConstraints.EAST));
		
		pop3ServerPasswordTextField = new JTextField();
		pop3ServerPasswordTextField.setColumns(20);
		pop3ServerPasswordTextField.setText( props.getConfigurations().getPop3ServerPassword());
		this.add(pop3ServerPasswordTextField, getConstraints(1, 6, 2, 1,GridBagConstraints.WEST));
		
		
		this.add(new JLabel("POP3 Server Port Number: "), getConstraints(0, 7, 1, 1,GridBagConstraints.EAST));
		
		pop3ServerPortNumberTextField = new JTextField();
		pop3ServerPortNumberTextField.setColumns(5);
		pop3ServerPortNumberTextField.setText( props.getConfigurations().getPop3ServerPortNumber());
		this.add(pop3ServerPortNumberTextField, getConstraints(1, 7, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("Is this an GMail account? "), getConstraints(0, 8, 1, 1,GridBagConstraints.EAST));
		
		isGMailAccountCheckBox = new JCheckBox();
		isGMailAccountCheckBox.setSelected(props.getConfigurations().isGMailAccount());
		this.add(isGMailAccountCheckBox,getConstraints(1, 8, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("mySQL Database URL: "), getConstraints(0, 9, 1, 1,GridBagConstraints.EAST));
		
		mySQLdatabaseURLTextField = new JTextField();
		mySQLdatabaseURLTextField.setColumns(20);
		mySQLdatabaseURLTextField.setText( props.getConfigurations().getMySQLdatabaseURL());
		this.add(mySQLdatabaseURLTextField, getConstraints(1, 9, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("mySQL Database Port Number: "), getConstraints(0, 10, 1, 1,GridBagConstraints.EAST));
		
		mySQLdatabasePortNumberTextField = new JTextField();
		mySQLdatabasePortNumberTextField.setColumns(5);
		mySQLdatabasePortNumberTextField.setText( props.getConfigurations().getMySQLdatabasePortNumber());
		this.add(mySQLdatabasePortNumberTextField, getConstraints(1, 10, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("mySQL Database User Name: "), getConstraints(0, 11, 1, 1,GridBagConstraints.EAST));
		
		mySQLdatabaseUserNameTextField = new JTextField();
		mySQLdatabaseUserNameTextField.setColumns(20);
		mySQLdatabaseUserNameTextField.setText( props.getConfigurations().getMySQLdatabaseUserName());
		this.add(mySQLdatabaseUserNameTextField, getConstraints(1, 11, 2, 1,GridBagConstraints.WEST));
		
		this.add(new JLabel("mySQL Database Password: "), getConstraints(0, 12, 1, 1,GridBagConstraints.EAST));
		
		mySQLdatabasePasswordTextField = new JTextField();
		mySQLdatabasePasswordTextField.setColumns(20);
		mySQLdatabasePasswordTextField.setText( props.getConfigurations().getMySQLdatabasePassword());
		this.add(mySQLdatabasePasswordTextField, getConstraints(1, 12, 2, 1,GridBagConstraints.WEST));
		
		this.add(createButtonPanel(), getConstraints(0, 13, 3, 1,GridBagConstraints.CENTER));
		
	}
	
	/**
	 * Creates an places the save button
	 * So that it is centered at the bottom of the GUI
	 * 
	 */
	private JPanel createButtonPanel() {

		JPanel buttonPanel = new JPanel(new GridBagLayout());

		JButton[] buttons = new JButton[1];
		buttons[0] = new JButton("Save");
		buttons[0].addActionListener(this);

		buttonPanel.add(buttons[0], getConstraints(0, 0, 1, 1,GridBagConstraints.CENTER));

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
	 * This method is triggered when the user click on the Save button on the panel
	 * 
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if(writeProperties())
			JOptionPane.showMessageDialog(null, "Save Successful.\n" +
					"Note: For the changes to take effect you will have to restart the application.");		
	}
	
	/**
	 * Write the contents of the MailConfig object to the properties file
	 *
	 * @param mailConfigData
	 * @return success or failure
	 */
	private boolean writeProperties() 
	{
		String propFileName="MailConfig.properties";
		Properties prop = new Properties();;
		boolean retVal = true;

		prop.setProperty("userName", userNameTextField.getText());
		prop.setProperty("userEmailAddress",emailAddressTextField.getText());
		prop.setProperty("urlPOP3server", pop3ServerURLTextField.getText());
		prop.setProperty("urlSMTPserver", smtpServerURLTextField.getText());
		prop.setProperty("loginPOP3server", pop3ServerUserNameTextField.getText());
		prop.setProperty("passwordPOP3server", pop3ServerPasswordTextField.getText());
		prop.setProperty("portNumberPOP3", pop3ServerPortNumberTextField.getText());
		prop.setProperty("portNumberSMTP", smtpServerPortNumberTextField.getText());
		prop.setProperty("isGmail", Boolean.toString(isGMailAccountCheckBox.isSelected()));
		prop.setProperty("mySQLdatabaseURL", mySQLdatabaseURLTextField.getText());
		prop.setProperty("mySQLdatabasePortNumber",mySQLdatabasePortNumberTextField.getText());
		prop.setProperty("mySQLdatabaseUserName",mySQLdatabaseUserNameTextField.getText());
		prop.setProperty("mySQLdatabasePassword", mySQLdatabasePasswordTextField.getText());

		FileOutputStream propFileStream = null;
		File propFile = new File(propFileName);
		try {
			propFileStream = new FileOutputStream(propFile);
			prop.store(propFileStream, "-- MailConfig Properties --");
			propFileStream.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,
					"The properties file has not been found.",
					"Missing Properties File", JOptionPane.ERROR_MESSAGE);
			retVal = false;
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"There was an error writing the Properties file.",
					"Properties File Write Error", JOptionPane.ERROR_MESSAGE);
			retVal = false;
			e.printStackTrace();
		}
		return retVal;
	}
}
