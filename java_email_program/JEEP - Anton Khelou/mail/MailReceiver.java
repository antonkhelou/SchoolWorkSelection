/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MailReceiver.java                                              
 * Short description of what's in this file: This class retrieves mail
 * 	from either a standard POP3 server or from a GMail server.                  
 */
package mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import beans.EmailBean;

import com.sun.mail.pop3.*;
import javax.swing.JOptionPane;


public class MailReceiver 
{
	private MailProperties props = null;

	/**
	 * Constructor Requires the configuration data loaded from the properties
	 * file
	 * 
	 * @param mailConfigData
	 */
	public MailReceiver(MailProperties props) {
		this.props = props;
	}
	/**
	 * Retrieve the mail If something goes wrong then return a null
	 * 
	 * @return an ArrayList of mail message data
	 */
	public ArrayList<EmailBean> getMail() {

		boolean retVal = true;
		ArrayList<EmailBean> mailMessageDataList = new ArrayList<EmailBean>();

		retVal = mailReceive( mailMessageDataList);
		if (!retVal)
			mailMessageDataList.clear();

		return mailMessageDataList;
	}

	/**
	 * Do the actual work to receive the mail
	 * 
	 * @return success or failure
	 */
	private boolean mailReceive(ArrayList<EmailBean> mailMessageDataList) {

		boolean retVal = true;

		Store store = null;
		Folder folder = null;
		Session session = null;

		Properties pop3Props = new Properties();

		try {
			if (props.getConfigurations().isGMailAccount()) { // Gmail config

				// Store configuration information for accessing the
				// server in the properties object
				pop3Props.put("mail.pop3.host",
						props.getConfigurations().getPop3ServerURL());
				pop3Props.setProperty("mail.user", 
						props.getConfigurations().getPop3ServerUserName());
				pop3Props.setProperty("mail.passwd",
						props.getConfigurations().getPop3ServerPassword());
				pop3Props.setProperty("mail.pop3.port",
						props.getConfigurations().getPop3ServerPortNumber());
				String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
				pop3Props.setProperty("mail.pop3.socketFactory.class",
						SSL_FACTORY);
				pop3Props.setProperty("mail.pop3.socketFactory.port",
						props.getConfigurations().getPop3ServerPortNumber());
				pop3Props.setProperty("mail.pop3.ssl", "true");
				pop3Props.setProperty("mail.pop3.socketFactory.fallback",
						"false");

				URLName url = new URLName("pop3://"
						+ pop3Props.getProperty("mail.user") + ":"
						+ pop3Props.getProperty("mail.passwd") + "@"
						+ pop3Props.getProperty("mail.pop3.host") + ":"
						+ pop3Props.getProperty("mail.pop3.port"));

				// Create a mail session
				session = Session.getDefaultInstance(pop3Props, null);

				// Get hold of a POP3 message store, and connect to it
				store = new POP3SSLStore(session, url);

			} else { // POP3 config
				// Create a mail session
				session = Session.getDefaultInstance(pop3Props, null);

				// Get hold of a POP3 message store, and connect to it
				store = session.getStore("pop3");
			}

			// Connect to server
			store.connect(props.getConfigurations().getPop3ServerURL(), 
					props.getConfigurations().getPop3ServerUserName(),
					props.getConfigurations().getPop3ServerPassword());

			// Get the default folder
			folder = store.getDefaultFolder();
			if (folder == null)
				throw new Exception(":No default folder");

			// Get the INBOX from the default folder
			folder = folder.getFolder("INBOX");
			if (folder == null)
				throw new Exception(":No POP3 INBOX");

			// Open the folder for read only, cannot delete messages
			// Some servers will still delete messages after they are delivered
			folder.open(Folder.READ_ONLY);

			// Open the folder for read/write, can delete messages
			// folder.open(Folder.READ_WRITE);

			// Get all the waiting messages
			Message[] msgs = folder.getMessages();

			// Process the messages into beans
			retVal = process(msgs,mailMessageDataList);

		} catch (NoSuchProviderException e) {
			JOptionPane.showMessageDialog(null,
					"There is no server at the POP3 address.",
					"POP3-NoSuchProviderException", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			retVal = false;
		} catch (MessagingException e) {
			JOptionPane.showMessageDialog(null,
					"There is a problem with the message.",
					"POP3-MessagingException", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			retVal = false;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"There has been an unknown error " + e.getMessage(),
					"POP3-UnknownException", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			retVal = false;
		} finally {
			// Close down nicely
			try {
				if (folder != null)
					folder.close(true); // true=expunge
				if (store != null)
					store.close();
			} catch (Exception ex2) {
				ex2.printStackTrace();
				JOptionPane
						.showMessageDialog(
								null,
								"There has been an error closing a folder\non the POP3 server.",
								"POP3-Folder Error", JOptionPane.ERROR_MESSAGE);
				// There are messages in the ArrayList so do not change retVal
				// to false
			}
		}
		return retVal;
	}

	/**
	 * Process the message to fill the mail message data bean
	 * 
	 * @param messages
	 *            messages to process
	 * @return success or failure
	 */
	private boolean process(Message[] messages,ArrayList<EmailBean> mailMessageDataList) {

		boolean retVal = true;
		EmailBean mmd = null;

		for (int msgNum = 0; msgNum < messages.length; msgNum++) {
			mmd = new EmailBean();
			try {

				// Get the From field
				// While it supports more than one sender we will accept only
				// the first. A blank address throws an exception so catch
				// it and set from to an empty string.
				String from = null;
				try 
				{
					from = ((InternetAddress) messages[msgNum].getFrom()[0])
								.getAddress();

					mmd.setfromSender(from);

				} catch (AddressException e) {
					from = "";
				}
				
				try 
				{
					Address[] addressArray = messages[msgNum].getAllRecipients();
					String[] toReceivers = new String[addressArray.length];
					for(int i=0;i<addressArray.length;i++)
						toReceivers[i]=((InternetAddress)addressArray[i]).getAddress();
					
					mmd.setTOReceiver(toReceivers);

				} 
				catch (AddressException e) 
				{
					e.printStackTrace();
				}
							
				// Get subject
				String subject = messages[msgNum].getSubject();
				mmd.setSubject(subject);

				// Get date sent
				Date date = messages[msgNum].getSentDate();
				mmd.setDate(date);
				
				mmd.setContainingFolderName("Inbox");
				mmd.setCCReceiver(null);
				mmd.setBCCReceiver(null);

				// Get the message part (i.e. the message itself)
				Part messagePart = messages[msgNum];

				// The message may be multipart which means there could
				// be attachments, images, and html
				int x = saveAttachments(messagePart);
				if (x > 0)
					JOptionPane.showMessageDialog(null, "There were " + x
							+ " attachments saved to disk.", "Attachments",
							JOptionPane.INFORMATION_MESSAGE);

				String msgText = getMessageText(messagePart);

				mmd.setBody(msgText);

				mailMessageDataList.add(mmd);

				// Delete message from server when folder is closed
				// valid only if folder opened in read/write mode
				messages[msgNum].setFlag(Flags.Flag.DELETED, true);

			} catch (MessagingException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"There is a problem reading a message.",
						"POP3-MessagingException", JOptionPane.ERROR_MESSAGE);
				retVal = false;
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"There has been an unknown error.",
						"POP3-UnknownException", JOptionPane.ERROR_MESSAGE);
				retVal = false;
			}
		}

		return retVal;
	}

	/**
	 * Search through the body content for an attachment
	 * 
	 * @param part
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	private int saveAttachments(Part part) throws MessagingException,
			IOException {
		int count = 0;
		// Check for MimeType that could hold an attachment
		if (part.isMimeType("multipart/mixed")) {
			Multipart localMultiPart = (Multipart) part.getContent();
			// Examine each part for an attachment
			for (int i = 0; i < localMultiPart.getCount(); i++) {
				Part localPart = localMultiPart.getBodyPart(i);
				String disposition = localPart.getDisposition();
				
				if (disposition != null)
					if (disposition.equals(Part.ATTACHMENT) || disposition
								.equals(Part.INLINE)) {
					// Save it to disk
					saveFile(localPart.getFileName(), localPart
							.getInputStream());
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Given an InputStream for an attachment,
	 * save it to disk with its original name
	 * 
	 * @param fileName
	 * @param inputStream
	 */
	private void saveFile(String fileName, InputStream inputStream) {
		
		File file = new File(fileName);
		int i = 0;
		while (file.exists())
			file = new File(fileName + i++);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			byte buf[] = new byte[1024];
			int len;

			while ((len = inputStream.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"File not found while saving attachment",
					"File Not found.", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error writing attachment to disk", "File Write Error.",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * This method examines a the message text and identifies all parts and
	 * recursively all sub parts. It is looking for the text/plain section that
	 * every email message will have.
	 * 
	 * @param part
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	private String getMessageText(Part part) throws MessagingException,
			IOException {

		if (part.isMimeType("text/*")) 
		{
			String content = (String) part.getContent();
			return content;
		}

		if (part.isMimeType("multipart/alternative")) 
		{
			// prefer plain text over html text
			Multipart multiPart = (Multipart) part.getContent();
			String text = null;
			for (int i = 0; i < multiPart.getCount(); i++) 
			{
				Part bodyPart = multiPart.getBodyPart(i);
				if (bodyPart.isMimeType("text/html")) 
				{
					continue;
				} 
				else if (bodyPart.isMimeType("text/plain")) 
				{
					String bodyPartMessageText = getMessageText(bodyPart);
					if (bodyPartMessageText != null)
						return bodyPartMessageText;
				} 
				else 
				{
					return getMessageText(bodyPart);
				}
			}
			return text;
		} 
		else if (part.isMimeType("multipart/*")) 
		{
			Multipart multiPart = (Multipart) part.getContent();
			for (int i = 0; i < multiPart.getCount(); i++) 
			{
				String bodyPartMessageText = getMessageText(multiPart
						.getBodyPart(i));
				if (bodyPartMessageText != null)
					return bodyPartMessageText;
			}
		}
		return null;
	}
}
