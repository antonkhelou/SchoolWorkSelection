/* Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MainFrameTreePanel.java                                          
 * Short description of what's in this file: This class is used to
 * 		to create the Folder Tree Panel for the Main Frame.                           
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import beans.FolderBean;
import data.JEEPDBManager;
import model.EmailTableModel;

@SuppressWarnings("serial")
public class MainFrameTreePanel extends JPanel implements TreeSelectionListener, ActionListener
{
	private JTree tree = null;
	private DefaultMutableTreeNode top = null;
	private DefaultTreeModel treeModel = null;
	private JScrollPane treeView = null;
    private EmailTableModel emailTableModel = null;
    private JEEPDBManager connection = null;
    private int newNodeSuffix = 1;
    private String folderNameBeforeEdit = null;
    private Toolkit toolKit = null;

	/**
	 * Constructs the MainFrameTreePanel
	 * 
	 */
	public MainFrameTreePanel(JEEPDBManager connection, EmailTableModel emailTableModel) 
	{
		super(new BorderLayout());	
		this.connection = connection;
		this.emailTableModel = emailTableModel;
		toolKit = Toolkit.getDefaultToolkit();
		initialize();
		this.setMinimumSize(new Dimension(135,1000));
	}
	
	/**
	 * Creates the GUI
	 */
	public void initialize() 
	{
		createTree();
		populateTree();
		add(treeView, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the Jtree
	 * 
	 */
	public JTree getTree()
	{
		return tree;
	}
	
	/**
	 * Build the JTree by:
	 *  1: Create the top node
	 *  2: Create all the leaf nodes, adding them one at a time to the top node
	 *  3: Instantiate the tree with the top node
	 *  4: Set the selection mode such as single
	 *  4a: Change the icon for the leaf
	 *  5: Add the selection listener
	 *  5a: Hide the root node
	 *  6: Optionally, change the line style
	 *  7: Add to the panel
	 */
	private void createTree() 
	{
		//Create the top node
        top =  new DefaultMutableTreeNode("Folders");
        
        
        treeModel = new DefaultTreeModel(top);
        tree = new JTree(treeModel);
        
        //Set the icon for leaf nodes.
        ImageIcon leafIcon = createImageIcon("/images/folder-icon.png");
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(leafIcon);
        tree.setCellRenderer(renderer);
        
        NonRootEditor editor  = new NonRootEditor(tree,
                (DefaultTreeCellRenderer)tree.getCellRenderer());
        
        tree.setCellEditor(editor);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(this);
        
        treeModel.addTreeModelListener(new MyTreeModelListener());
        
        // Make the popup menu
		makeTreePopupMenu();

        treeView = new JScrollPane(tree);
        treeView.setPreferredSize(new Dimension(140,200));
	}
	
	/**
	 * Private method that populates the tree with folder data
	 * 
	 */
	private void populateTree()
	{
		ArrayList<String> list = emailTableModel.getFolderNames();
		for(String data: list)
			addObject(data);
		
	 }
	
	/** 
	 * Returns an ImageIcon, or null if the path was invalid. 
	 */
    private static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MainFrame.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
	
	/** 
	 * Add child to the currently selected node. 
	 * 
	 */
    public void addObject(Object child) 
    {
    	DefaultMutableTreeNode childNode = 
            new DefaultMutableTreeNode(child);
    	
    	treeModel.insertNodeInto(childNode, top, 
                top.getChildCount());
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
    }
	
    /** 
     * Remove the currently selected node. 
     * 
     */
    public void removeCurrentNode()
    {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        } 

    }
	
    /**
	 * Create the popup menu and attach it to the tree
	 */
	private void makeTreePopupMenu() 
	{
		JMenuItem menuItem;

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		menuItem = new JMenuItem("Add New Folder");
		menuItem.addActionListener(this);
		popup.add(menuItem);
		
		popup.addSeparator();
		
		menuItem = new JMenuItem("Edit Folder Name");
		menuItem.addActionListener(this);
		popup.add(menuItem);

		menuItem = new JMenuItem("Delete Folder");
		menuItem.addActionListener(this);
		popup.add(menuItem);

		// Add mouse listener to the text area so the popup menu can come up.
		MouseListener popupListener = new PopupListener(popup);
		
		// add listener to the tree
		tree.addMouseListener(popupListener);
	}
	
	/**
	 * This is a mouse listener that displays the popup menu
	 * 
	 * @author neon
	 * 
	 */
	class PopupListener extends MouseAdapter 
	{

		JPopupMenu popup;

		public PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) 
		{
			maybeShowPopup(e, false);
		}

		public void mouseReleased(MouseEvent e) 
		{
			maybeShowPopup(e, true);
		}

		private void maybeShowPopup(MouseEvent e, boolean pressed) {
			// If this is the right mouse button
			if (e.isPopupTrigger()) 
			{
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				if (selRow != -1) 
				{
					popup.show(e.getComponent(), e.getX(), e.getY());
					pressed = false;
				}
			} 
			else
			{
				// if this is the left mouse button
				if (pressed) 
				{
					// Determine which leaf (row) we are nearest to
					int selRow = tree.getRowForLocation(e.getX(), e.getY());
					// We are near enough to a leaf
					if (selRow != -1) {
						if (e.getClickCount() == 1) 
						{
							folderNameBeforeEdit = tree.getLastSelectedPathComponent().toString();
						} 
					}
				}
			}
		}
	}

   /* 
    * Event handler for selecting a node
    * 
    * (non-Javadoc)
    * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
    */
    public void valueChanged(TreeSelectionEvent e) 
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();
        
        if (node!=null && node.isLeaf()) 
        {
        	emailTableModel.displayEmailsInFolder(node.toString());
        }
    }

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		//Casts the source of the ActionEvent to a JButton and gets the
		//text displayed on the button
		String buttonText = ((JMenuItem) e.getSource()).getText();

		//verifies which menu item was clicked
		if (buttonText.equals("Add New Folder")) 
		{
			String newFolderName = "New Node " + newNodeSuffix++;
			FolderBean newFolder = new FolderBean(newFolderName);
			
			if(connection.addFolder(newFolder))
				addObject(newFolderName);
		}
		else if(buttonText.equals("Delete Folder"))
		{
			String deletedFolderName = tree.getLastSelectedPathComponent().toString();
			FolderBean deletedFolder = new FolderBean(deletedFolderName);
			
			//verifies if the folder isn't one of the root folders
			if(!deletedFolderName.equals("Inbox")&&
					!deletedFolderName.equals("Outbox")&&
					!deletedFolderName.equals("Sent")&&
					!deletedFolderName.equals("Junk")&&
					!deletedFolderName.equals("Drafts"))
			{
				if(connection.removeFolder(deletedFolder))
					removeCurrentNode();
			}
			else
			{
				//Emits a noise to indicate an error
				toolKit.beep();
			}
		}
		else if(buttonText.equals("Edit Folder Name"))
		{
			folderNameBeforeEdit = tree.getLastSelectedPathComponent().toString();
			
			//verifies if the folder isn't one of the root folders
			if(!folderNameBeforeEdit.equals("Inbox")&&
					!folderNameBeforeEdit.equals("Outbox")&&
					!folderNameBeforeEdit.equals("Sent")&&
					!folderNameBeforeEdit.equals("Junk")&&
					!folderNameBeforeEdit.equals("Drafts"))
			{
				tree.startEditingAtPath(tree.getSelectionPath());
			}
			else
			{
				//Emits a noise to indicate an error
				toolKit.beep();
			}
		}		
	}
	
	/**
	 * Inner Class
	 * 
	 * The purpose of this inner class is to commit the changes in the database
	 * when a user edits a folder.
	 */
    class MyTreeModelListener implements TreeModelListener 
    {
        public void treeNodesChanged(TreeModelEvent e)
        {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode)(node.getChildAt(index));
                 
            FolderBean originalFolder = new FolderBean(folderNameBeforeEdit);
            FolderBean editedFolder = new FolderBean(node.getUserObject().toString());
            
            connection.editFolder(originalFolder, editedFolder);
        }
        public void treeNodesInserted(TreeModelEvent e) {
        }
        public void treeNodesRemoved(TreeModelEvent e) {
        }
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }
    
	/**
	 * Inner Class
	 * 
	 * The purpose of this inner class is to keep the traditional
	 * "click + keep mouse over node for a second, highlight then edit"
	 * approach to editing nodes. In order to achieve this and keep
	 * the restrictions where the user cannot edit the native set of
	 * folders, some code from the java library was taken and modified accordingly.
	 * Note: I don't fully understand what is going on in this method.
	 */
    class NonRootEditor extends DefaultTreeCellEditor  
    {  
        public NonRootEditor(JTree tree, DefaultTreeCellRenderer renderer) 
        {
			super(tree, renderer);

		}

		public boolean isCellEditable(EventObject event)  
        {  
			boolean retValue = false;
	        boolean editable = false;

	        if (event != null) {
	            if (event.getSource() instanceof JTree) {
	                setTree((JTree)event.getSource());
	                if (event instanceof MouseEvent) {
	                    TreePath path = tree.getPathForLocation(
	                                         ((MouseEvent)event).getX(),
	                                         ((MouseEvent)event).getY());
	                    //the following is where I made a change to the code.
	                    //I added this code to disable the user from editing
	                    //the native folders through the traditional approach.
	                    //Note that I tried to achieve this using the .equals()
	                    //method instead of the indexOf approach, but for some odd
	                    //reason it did not work.
	                    editable = (lastPath != null && path != null &&
	                               lastPath.equals(path) 
	                               && lastPath.toString().indexOf("Inbox")<0
	                               && lastPath.toString().indexOf("Outbox")<0
	                               && lastPath.toString().indexOf("Sent")<0
	                               && lastPath.toString().indexOf("Junk")<0
	                               && lastPath.toString().indexOf("Drafts")<0);
			    if (path!=null) {
				lastRow = tree.getRowForPath(path);
				Object value = path.getLastPathComponent();
				boolean isSelected = tree.isRowSelected(lastRow);
				boolean expanded = tree.isExpanded(path);
				TreeModel treeModel = tree.getModel();
				boolean leaf = treeModel.isLeaf(value);
				determineOffset(tree, value, isSelected,
						expanded, leaf, lastRow);
			    }
	                }
	            }
	        }
		if(!realEditor.isCellEditable(event))
		    return false;
		if(canEditImmediately(event))
		    retValue = true;
		else if(editable && shouldStartEditingTimer(event)) {
		    startEditingTimer();
		}
		else if(timer != null && timer.isRunning())
		    timer.stop();
		if(retValue)
		    prepareForEditing();

		return retValue;
        }  
    }  
}
