/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MailProperties.java                                              
 * Short description of what's in this file: This class is used to manipulate
 * 	configurations data. It revolves around the ConfigurationBean.java class.                       
 */
package mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import beans.ConfigurationBean;

public class MailProperties {

	private String propFileName;
	private Properties prop = null;
	private ConfigurationBean configBean = null;

	/**
	 * Constructor
	 */
	public MailProperties() {
		super();
		// SMTP properties
		this.propFileName = "MailConfig.properties";
		
		prop = new Properties();
		loadProperties();
	}

	/**
	 * Load the properties into the MailConfig object
	 * 
	 * @return if successful or not
	 */
	private void loadProperties() {

		configBean = new ConfigurationBean();
		FileInputStream propFileStream = null;
		File propFile = new File(propFileName);

		// File must exist
		if (propFile.exists()) {
			try {
				propFileStream = new FileInputStream(propFile);
				prop.load(propFileStream);
				propFileStream.close();
				
				// Store the properties in a mailConfigData object
				configBean.setUserName(prop.getProperty("userName"));
				configBean.setEmailAddress(prop
						.getProperty("userEmailAddress"));
				configBean.setPop3ServerURL(prop.getProperty("urlPOP3server"));
				configBean.setSmtpServerURL(prop.getProperty("urlSMTPserver"));
				configBean.setPop3ServerUserName(prop
						.getProperty("loginPOP3server"));
				configBean.setPop3ServerPassword(prop
						.getProperty("passwordPOP3server"));
				configBean.setPop3ServerPortNumber(prop
						.getProperty("portNumberPOP3"));
				configBean.setSmtpServerPortNumber(prop
						.getProperty("portNumberSMTP"));

				// parseBoolean returns false if the string is not "true"
				// so no need for an exception handler
				configBean.setGMailAccount(Boolean.parseBoolean(prop
						.getProperty("isGmail")));
				configBean.setMySQLdatabaseURL(prop
						.getProperty("mySQLdatabaseURL"));
				configBean.setMySQLdatabasePortNumber(prop
						.getProperty("mySQLdatabasePortNumber"));
				configBean.setMySQLdatabaseUserName(prop
						.getProperty("mySQLdatabaseUserName"));
				configBean.setMySQLdatabasePassword(prop
						.getProperty("mySQLdatabasePassword"));

			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null,
						"The properties file has not been found.",
						"Missing Properties File", JOptionPane.ERROR_MESSAGE);
				configBean = null;
				e.printStackTrace();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"There was an error reading the Properties file.",
						"Properties File Read Error", JOptionPane.ERROR_MESSAGE);
				configBean = null;
				e.printStackTrace();
			}
		} else
			configBean = null;

	}
	
	public ConfigurationBean getConfigurations()
	{
		return configBean;
	}


	/**
	 * Diagnostic method to display the properties
	 */
	public void displayProperties() {
		prop.list(System.out);
	}

}
