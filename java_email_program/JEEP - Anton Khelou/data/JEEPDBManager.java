/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: JEEPDBManager.java                                              
 * Short description of what's in this file: This class is holds
 * 	all the methods used to manipulate data in the mySQL server.                      
 */
package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import beans.ContactBean;
import beans.EmailBean;
import beans.FolderBean;

/**
 * @author neon
 * 
 */
public class JEEPDBManager 
{

	private final boolean DEBUG = false;
	private Statement statement = null;
	private Connection connection = null;
	private String url = null;
	private String username = null;
	private String password = null;

	/**
	 * Constructs the JEEPDBManager
	 */
	public JEEPDBManager(String url, String username, String password) 
	{
		super();
		
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Connect to the database
	 * 
	 */
	private void establishConnection()
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(url, username, password);
		} 
		catch (ClassNotFoundException cnfex) 
		{
			System.err.println("Failed to load JDBC/ODBC driver.");
			cnfex.printStackTrace();
			System.exit(1); // terminate program
		} 
		catch (SQLException sqlex) 
		{
			System.err.println("Unable to connect");
			sqlex.printStackTrace();
		} 
		catch (Exception e) 
		{
			System.out.println("Error connecting to database.");
			System.exit(1);
		}
	}

	/**
	 * Get all the records in the Folders table and creates
	 * an ArrayList of FolderBean objects.
	 * 
	 */
	public ArrayList<FolderBean> getFoldersData()
	{
		
		ArrayList<FolderBean> folderData = new ArrayList<FolderBean>();
		establishConnection();
		try 
		{
			statement = connection.createStatement();
			String sql = "Select * from folders";	
			
			ResultSet resultSet = statement.executeQuery(sql);
			
			resultSet.beforeFirst();
			while (resultSet.next()) 
			{
				folderData.add(new FolderBean(resultSet.getString(1)));
			}
			
			statement.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}

		return folderData;
	}
	
	/**
	 * Adds a record to the folders table based
	 * on a FolderBean.
	 * 
	 */
	public boolean addFolder(FolderBean folder)
	{
		folder.setFolderName(precedeAllSingleQuotesWithBackslash(folder.getFolderName()));

		
		boolean isSuccessful = true;
		establishConnection();
		try 
		{
			statement = connection.createStatement();
			String sql = "INSERT INTO folders (folder_name)VALUES ('" + folder.getFolderName()+ "')";
				
			statement.execute(sql);			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	/**
	 * Edits a row in the folders table
	 * 
	 */
	public boolean editFolder(FolderBean originalFolder,FolderBean editedFolder)
	{
		originalFolder.setFolderName(precedeAllSingleQuotesWithBackslash(originalFolder.getFolderName()));
		editedFolder.setFolderName(precedeAllSingleQuotesWithBackslash(editedFolder.getFolderName()));

		
		boolean isSuccessful = true;		
		establishConnection();
		try 
		{
				statement = connection.createStatement();
				String sql = "UPDATE folders SET folder_name='" + editedFolder.getFolderName()
					+"' " + "WHERE folder_name='" + originalFolder.getFolderName() + "'";
				
				statement.execute(sql);		
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	/**
	 * Removes a row in the folders table
	 * 
	 */
	public boolean removeFolder(FolderBean folder)
	{
		folder.setFolderName(precedeAllSingleQuotesWithBackslash(folder.getFolderName()));
		
		boolean isSuccessful = true;
		establishConnection();
		try 
		{
			statement = connection.createStatement();
			String sql = "DELETE FROM folders WHERE folder_name='" + folder.getFolderName() + "'";
			
			statement.execute(sql);
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Get all the records in the Emails table that have the
	 * containing folder name as the one sent as a parameter
	 *  and creates an ArrayList of FolderBean objects.
	 * 
	 */
	public ArrayList<EmailBean> getEmailsInFolder(String folderName)
	{
		folderName = precedeAllSingleQuotesWithBackslash(folderName);
		ArrayList<EmailBean> emailData = new ArrayList<EmailBean>();
		establishConnection();
		try {
			// Send the SQL statement
			statement = connection.createStatement();
			String sql = "Select * from emails where containing_folder_name='" +
				folderName +"'";
			
			ResultSet resultSet = statement.executeQuery(sql);
			resultSet.beforeFirst();
			while(resultSet.next()) 
			{
				String[] toArray = resultSet.getString("to_receiver").split(";");
				String[] ccArray = resultSet.getString("cc_receiver").split(";");
				String[] bccArray = resultSet.getString("bcc_receiver").split(";");
				
				emailData.add(new EmailBean(resultSet.getInt("e_id"),resultSet.getString("sender"), toArray,
						ccArray, bccArray, resultSet.getString("subject"), resultSet
						.getString("body"), resultSet.getString("containing_folder_name"), resultSet.getDate("date")));
			} 

			statement.close();
		} 
		catch (SQLException sqlex) 
		{
			sqlex.printStackTrace();
		}
		
		return emailData;
	}
	
	/**
	 * Adds a record to the emails table based
	 * on a EmailBean.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public boolean addEmail(EmailBean email)
	{
		//The following is to replace all the single quotes in a string to
		// \' in order to avoid an exception thrown by SQL.
		//The backslash serves as an escape character both for Java String
		//literals and for the replacement string, so you have to use four of
		//them to make one appear in the output.
		email.setSubject(precedeAllSingleQuotesWithBackslash(email.getSubject()));
		email.setBody(precedeAllSingleQuotesWithBackslash(email.getBody()));
		
		boolean isSuccessful = true;
		String date = "" + (email.getDate().getYear()+1900) +"-" +
				(email.getDate().getMonth()+1) + "-" +
				email.getDate().getDate();
		
		String toString = "";
		String[] toArray = email.getTOReceiver();
		String ccString = "";
		String[] ccArray = email.getCCReceiver();
		String bccString = "";
		String[] bccArray = email.getBCCReceiver();

		if(toArray!=null)
		{
			for(int i=0;i<toArray.length;i++)
				if(!toArray[i].equals(""))
					if(i==toArray.length-1)
						toString += toArray[i];
					else
						toString += toArray[i] + ";";
		}
		else
		{
			toString = "";
		}
		
		if(ccArray!=null)
		{
			for(int i=0;i<ccArray.length;i++)
				if(!ccArray[i].equals(""))
					if(i==ccArray.length-1)
						ccString += ccArray[i];
					else
						ccString += ccArray[i] + ";";
		}
		else
		{
			ccString="";
		}
		
		if(bccArray!=null)
		{
			for(int i=0;i<bccArray.length;i++)
				if(!bccArray[i].equals(""))
					if(i==bccArray.length-1)
						bccString += bccArray[i];
					else
						bccString += bccArray[i] + ";";
		}
		else
		{
			bccString ="";
		}
					
		establishConnection();
		try 
		{
			statement = connection.createStatement();
			String sql = "INSERT INTO emails (sender,to_receiver,cc_receiver," +
					"bcc_receiver,subject,body,containing_folder_name,date)VALUES ('" +
					email.getfromSender()+ "','"+ toString+ "','"+ccString+"','"+ bccString +"','"+ email.getSubject()+ "','"+ email.getBody()+
					"','"+ email.getContainingFolderName()+"','"+date +"')";
				
			statement.execute(sql);			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	/**
	 * Adds a record from the emails table
	 * 
	 */
	public boolean removeEmail(EmailBean email)
	{
		boolean isSuccessful = true;
		
		establishConnection();
		try 
		{
			statement = connection.createStatement();
			String sql = "DELETE FROM emails WHERE e_id='" + email.getId() + "'";
			
			statement.execute(sql);
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	/**
	 * Edits a record in the emails table
	 * 
	 */
	public boolean editEmail(EmailBean email)
	{
		email.setSubject(precedeAllSingleQuotesWithBackslash(email.getSubject()));
		email.setBody(precedeAllSingleQuotesWithBackslash(email.getBody()));
		
		boolean isSuccessful = true;		

		establishConnection();
		try 
		{
			String toString = "";
			String[] toArray = email.getTOReceiver();
			String ccString = "";
			String[] ccArray = email.getCCReceiver();
			String bccString = "";
			String[] bccArray = email.getBCCReceiver();
			
			for(int i=0;i<toArray.length;i++)
				if(!toArray[i].equals(""))
					if(i==toArray.length-1)
						toString += toArray[i];
					else
						toString += toArray[i] + ";";
			
			for(int i=0;i<ccArray.length;i++)
				if(!ccArray[i].equals(""))
					if(i==ccArray.length-1)
						ccString += ccArray[i];
					else
						ccString += ccArray[i] + ";";
			
			for(int i=0;i<bccArray.length;i++)
				if(!bccArray[i].equals(""))
					if(i==bccArray.length-1)
						bccString += bccArray[i];
					else
						bccString += bccArray[i] + ";";
			
			statement = connection.createStatement();
			String sql = "UPDATE emails SET sender='" + email.getfromSender() +
				"',to_receiver='"+ toString +"',cc_receiver='" + ccString +
				"', bcc_receiver='" + bccString + "', subject='"+ email.getSubject()  +
				"', body='"+ email.getBody() + "', containing_folder_name='"+ email.getContainingFolderName() +
				"', date='"+ email.getDate() + "' "+
				"WHERE e_id='" + email.getId() + "'";
			
			statement.execute(sql);		
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Gets all the records in the Contacts table and creates
	 * an ArrayList of ContactBean objects.
	 * 
	 */
	public ArrayList<ContactBean> getContactsData()
	{
		establishConnection();
		ArrayList<ContactBean> contactsData = new ArrayList<ContactBean>();
		try 
		{
			statement = connection.createStatement();
			String sql = "Select * from contacts";	
			
			ResultSet resultSet = statement.executeQuery(sql);
			
			resultSet.beforeFirst();
			while (resultSet.next()) 
			{
				contactsData.add(new ContactBean(resultSet.getInt("c_id"),resultSet.getString("first_name"),
						resultSet.getString("last_name"),resultSet.getString("email_address"),
						resultSet.getString("telephone_number")));
			}
			
			statement.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}

		return contactsData;
	}
	
	/**
	 * This method looks if the contact passed as a parameter
	 * is in the database, if he is, this method issues an edit,
	 * if he isn't, it will add him to the Contacts table.
	 * 
	 */
	public boolean addOrEditContact(ContactBean contact)
	{
		contact.setFirstName(precedeAllSingleQuotesWithBackslash(contact.getFirstName()));
		contact.setLastName(precedeAllSingleQuotesWithBackslash(contact.getLastName()));
		
		boolean isSuccessful = true;
	
		establishConnection();
		try 
		{
			if(contact.getId()!=-1)
			{
				statement = connection.createStatement();
				String sql = "UPDATE contacts SET email_address='" + contact.getEmailAddress()
					+"',telephone_number='"+contact.getTelephoneNumber()+"',first_name='" + contact.getFirstName()
					+"', last_name='" + contact.getLastName() + "' "+
					"WHERE c_id='" + contact.getId() + "'";
				
				statement.execute(sql);
			}
			else
			{
				statement = connection.createStatement();
				String sql = "INSERT INTO contacts (first_name,last_name,email_address,telephone_number)VALUES ('" +
					contact.getFirstName()+ "','" + contact.getLastName() + "','" + contact.getEmailAddress() + "','"
					+ contact.getTelephoneNumber() + "')";
				
				statement.execute(sql);
			}
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	/**
	 * Removes a row from the Contacts table
	 * 
	 */
	public boolean removeContact(ContactBean contact)
	{	
		boolean isSuccessful = true;
		
		establishConnection();
		try 
		{
			statement = connection.createStatement();
			String sql = "DELETE FROM contacts WHERE c_id='" + contact.getId()+ "'";
			
			statement.execute(sql);
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	/**
	 * Close the database connection
	 */
	public void closeConnection() {
		try 
		{
			if (DEBUG)
				System.out.println("Closing connection");
			connection.close();
		} 
		catch (SQLException sqlex2) 
		{
			sqlex2.printStackTrace();
		}
	}
	
	/**The following is to replace all the single quotes in a string to
	 * \' in order to avoid an exception thrown by SQL.
	 *The backslash serves as an escape character both for Java String
	 *literals and for the replacement string, so you have to use four of
	 *them to make one appear in the output.
	 */
	private String precedeAllSingleQuotesWithBackslash(String inputString)
	{
		String string = inputString.replaceAll("'", "\\\\'");
		
		return string;
	}
}
