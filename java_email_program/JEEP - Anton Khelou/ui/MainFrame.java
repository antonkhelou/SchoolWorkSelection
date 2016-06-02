/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MainFrame.java                                          
 * Short description of what's in this file: This is class creates
 * 	the Main Frame of the application. An instance of this class is created
 * 	in the application class(JEEPApp.java).                    
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import data.JEEPDBManager;
import mail.MailProperties;


@SuppressWarnings("serial")
public class MainFrame extends JFrame 
{
	private MainFramePanel emailPanel = null;
	private MainFrameToolBar toolBar = null;
	private JEEPDBManager connection = null;
	private MailProperties props = null;
	
	/**
	 * Constructs the MainFrame
	 * 
	 */
	public MainFrame(JEEPDBManager connection, MailProperties props)
	{
		super();
		this.connection = connection;
		this.props = props;
		
		initialize();
		
		//Maximizes the size of the frame
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setTitle("JEEP - Java Exclusive Email Program");
	    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    this.addWindowListener(new Terminator());
	    this.setJMenuBar(new MainFrameMenuBar(connection, props, emailPanel, this));
	    this.setVisible(true);
	}

	/**
	 * Create the GUI 
	 */
	private void initialize() 
	{
		emailPanel = new MainFramePanel(connection,props);
		toolBar = new MainFrameToolBar(connection,props,emailPanel);
		
		// JFrame method for adding a menu bar to a frame
		add(toolBar, BorderLayout.NORTH);
		add(emailPanel, BorderLayout.CENTER);	
		this.setMinimumSize(new Dimension(700,700));
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
	 * Dialog box to confirm application exit
	 */
	private class Terminator extends WindowAdapter {
		
		/**
		 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
		 */
		public void windowClosing(WindowEvent e) {
			int i = javax.swing.JOptionPane.showConfirmDialog(null,
					"Exit Application?", "Exit",
					javax.swing.JOptionPane.YES_NO_OPTION);
			//If yes, close the program and the connection
			if (i == javax.swing.JOptionPane.YES_OPTION)
			{
				connection.closeConnection();
				System.exit(1);
			}
		}//End windowClosing
	}//End Terminator
}
