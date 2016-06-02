/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MainFramePanel.java                                          
 * Short description of what's in this file: This is class creates
 * 	the "general" panel for the Main Frame.           
 */
package ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import data.JEEPDBManager;
import mail.MailProperties;

@SuppressWarnings("serial")
public class MainFramePanel extends JPanel 
{
	private MainFrameEmailViewPanel emailViewPanel = null; 
	private MainFrameEmailTablePanel emailTablePanel = null;
	private MainFrameTreePanel treePanel = null;
	private JSplitPane rightPanel = null;
	private JEEPDBManager connection = null;
	private MailProperties props = null;
	
	/**
	 * Constructs the MainFramePanel
	 * 
	 */
	public MainFramePanel(JEEPDBManager connection, MailProperties props)
	{
		super();
		this.connection = connection;
		this.props = props;
		initialize();
	}

	/**
	 * Creates the GUI
	 * 
	 */
	private void initialize() 
	{
		this.setLayout(new BorderLayout());
		
		setupRightPanel();
		treePanel = new MainFrameTreePanel(connection,emailTablePanel.getTableModel());
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		splitPane.setRightComponent(rightPanel);
        splitPane.setLeftComponent(treePanel);
        
        this.add(splitPane,BorderLayout.CENTER);
	}
	
	/**
	 * This method sets up the Right part of the Main Frame
	 * 
	 */
	private void setupRightPanel()
	{
		emailViewPanel  = new MainFrameEmailViewPanel();
		emailTablePanel = new MainFrameEmailTablePanel(connection, props, emailViewPanel);
		
        rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightPanel.setTopComponent(emailTablePanel);
        rightPanel.setBottomComponent(emailViewPanel);
        //this is to position the vertical split pane more towards the center
        rightPanel.setResizeWeight(0.4); 
	}
	
	/**
	 * Returns the emailViewPanel
	 * 
	 */
	public MainFrameEmailViewPanel getEmailViewPanel()
	{
		return emailViewPanel;
	}
	
	/**
	 * Returns the emailTablePanel
	 * 
	 */
	public MainFrameEmailTablePanel getTablePanel()
	{
		return emailTablePanel;
	}
	
	/**
	 * Returns the treePanel
	 * 
	 */
	public MainFrameTreePanel getTreePanel()
	{
		return treePanel;
	}
}
