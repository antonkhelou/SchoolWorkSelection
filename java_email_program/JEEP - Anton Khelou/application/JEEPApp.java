/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: ContactBean.java                                              
 * Short description of what's in this file: This class contains
 *  the main method of the system.                   
 */
package application;

import ui.MainFrame;
import data.JEEPDBManager;
import mail.MailProperties;

public class JEEPApp {

	public static void main(String[] args)  
	{
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try 
				{
					MailProperties props = new MailProperties();
					String mySQLdatabaseURL = props.getConfigurations().getMySQLdatabaseURL();
					String mySQLdatabasePortNumber = props.getConfigurations().getMySQLdatabasePortNumber();
					String mySQLdatabaseUserName = props.getConfigurations().getMySQLdatabaseUserName();
					String mySQLdatabasePassword = props.getConfigurations().getMySQLdatabasePassword();
					
					JEEPDBManager connection = new JEEPDBManager(
							"jdbc:mysql://" + mySQLdatabaseURL +":" + mySQLdatabasePortNumber
							+"/" + mySQLdatabaseUserName,
							mySQLdatabaseUserName,
							mySQLdatabasePassword);			
					new MainFrame(connection,props);
				} 
				catch (Exception e) 
				{
					System.out.println(e.getClass());

					javax.swing.JOptionPane.showMessageDialog(null,
						e.getStackTrace()
						+ " \nCouldn't connect to MYSQL Server.");
				}
			}
		});
	}
}