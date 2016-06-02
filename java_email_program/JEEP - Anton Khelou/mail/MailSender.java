/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: MailReceiver.java                                              
 * Short description of what's in this file: This class sends mail
 * 	from either a standard SMTP server or from a GMail server.                  
 */
package mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

import beans.EmailBean;

public class MailSender 
{

	private MailProperties props;

	public MailSender(MailProperties props)
	{
		this.props = props;

	}
	/**
	 * Decide whether to use smtp or gmail
	 * 
	 * @param mmd
	 *            the MailMessageData
	 */
	public boolean sendMail(EmailBean email) {
		boolean retVal = true;

		if (props.getConfigurations().isGMailAccount()) 
		{
			retVal = gmailSend(email);
		} else {
			retVal = smtpSend(email);
		}
		return retVal;
	}

	/**
	 * Sent the message to a plain SMTP server like Waldo
	 * 
	 * @param mmd
	 *            the MailMessageData to send
	 * @return success or failure
	 */
	private boolean smtpSend(EmailBean email) {
		boolean retVal = true;

		try {
			// Create a properties object
			Properties smtpProps = new Properties();

			// Add mail configuration to the properties
			smtpProps.put("mail.smtp.host", props.getConfigurations().getSmtpServerURL());
			smtpProps.put("mail.smtp.port",  props.getConfigurations().getSmtpServerPortNumber());

			Session session = Session.getInstance(smtpProps, null);

			// Display the conversation between the client and server
			session.setDebug(true);

			// Create a new message
			MimeMessage msg = new MimeMessage(session);

			// Set the single from field
			msg.setFrom(new InternetAddress( props.getConfigurations().getEmailAddress()));

			// Set the To, CC, and BCC from their ArrayLists
			for (String emailAddress : email.getTOReceiver())
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						emailAddress, false));

			if (email.getCCReceiver() != null)
				for (String emailAddress : email.getCCReceiver())
					if(!emailAddress.equals(""))
						msg.addRecipient(Message.RecipientType.CC,
							new InternetAddress(emailAddress, false));

			if (email.getBCCReceiver() != null)
				for (String emailAddress : email.getBCCReceiver())
					if(!emailAddress.equals(""))
						msg.addRecipient(Message.RecipientType.BCC,
							new InternetAddress(emailAddress, false));

			// Set the subject
			msg.setSubject(email.getSubject());

			// Set the message body
			msg.setText(email.getBody());

			// Set some other header information
			msg.setHeader("X-Mailer", "Comp Sci Tech Mailer");
			msg.setSentDate(new Date());

			// Send the message
			Transport.send(msg);

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"There is no server at the SMTP address.",
					"SMTP-NoSuchProviderException", JOptionPane.ERROR_MESSAGE);
			retVal = false;
		} catch (AddressException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"There is an error in a recipient's address.",
					"SMTP-AddressException", JOptionPane.ERROR_MESSAGE);
			retVal = false;
		} catch (MessagingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"There is a problem with the message.",
					"SMTP-MessagingException", JOptionPane.ERROR_MESSAGE);
			retVal = false;
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"There has been an unknown error.",
					"SMTP-UnknownException", JOptionPane.ERROR_MESSAGE);
			retVal = false;
		}
		return retVal;
	}

	/**
	 * Sent the message to a Gmail account
	 * 
	 * @param email
	 *            the MailMessageData to send
	 * @return success or failure
	 */
	public boolean gmailSend(EmailBean email) {
		boolean retVal = true;
		Transport transport = null;

		try {
			// Create a properties object
			Properties smtpProps = new Properties();

			// Add mail configuration to the properties
			smtpProps.put("mail.transport.protocol", "smtps");
			smtpProps.put("mail.smtps.host", props.getConfigurations().getSmtpServerURL());
			smtpProps.put("mail.smtps.port", props.getConfigurations().getSmtpServerPortNumber());
			smtpProps.put("mail.smtps.auth", "true");
			smtpProps.put("mail.smtps.quitwait", "false");

			// Create a mail session
			Session mailSession = Session.getInstance(smtpProps);

			// Display the conversation between the client and server
			mailSession.setDebug(true);

			// Instantiate the transport object
			transport = mailSession.getTransport();

			// Create a new message
			MimeMessage msg = new MimeMessage(mailSession);

			// Set the To, CC, and BCC from their ArrayLists
			for (String emailAddress : email.getTOReceiver())
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						emailAddress, false));

			if (email.getCCReceiver() != null)
				for (String emailAddress : email.getCCReceiver())
					if(!emailAddress.equals(""))
						msg.addRecipient(Message.RecipientType.CC,
							new InternetAddress(emailAddress, false));

			if (email.getBCCReceiver() != null)
				for (String emailAddress : email.getBCCReceiver())
					if(!emailAddress.equals(""))
						msg.addRecipient(Message.RecipientType.BCC,
							new InternetAddress(emailAddress, false));

			// Set the subject line
			msg.setSubject(email.getSubject());
			// Set the message body
			msg.setContent(email.getBody(),"text/html");

			// Set some other header information
			msg.setHeader("X-Mailer", "Comp Sci Tech Mailer");
			msg.setSentDate(new Date());
			
			// Connect and authenticate to the server
			transport.connect(props.getConfigurations().getSmtpServerURL(),
					Integer.parseInt(props.getConfigurations().getSmtpServerPortNumber()),
					props.getConfigurations().getPop3ServerUserName(),
					props.getConfigurations().getPop3ServerPassword());

			// Send the message
			transport.sendMessage(msg, msg.getAllRecipients());

			// Close the connection
			transport.close();

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"There is no server at the SMTP address.",
					"Gmail-NoSuchProviderException", JOptionPane.ERROR_MESSAGE);
			retVal = false;
		} catch (AddressException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"There is an error in a recipient's address.",
					"Gmail-AddressException", JOptionPane.ERROR_MESSAGE);
			retVal = false;
		} catch (MessagingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"There is a problem with the message.",
					"Gmail-MessagingException", JOptionPane.ERROR_MESSAGE);
			retVal = false;
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"There has been an unknown error.",
					"Gmail-UnknownException", JOptionPane.ERROR_MESSAGE);
			retVal = false;
		}
		return retVal;
	}

}

/*
 * Settings for well known mail providers
 * 
 * Yahoo Incoming Mail Server - pop.mail.yahoo.com (POP3 - port 110) Outgoing
 * Mail Server - smtp.mail.yahoo.com (SMPTP - port 25)
 * 
 * Google GMail Incoming Mail Server - pop.gmail.com (POP3S SSL enabled, port
 * 995) Outgoing Mail Server - gmail.com (SMPTS SSL enabled, port 465)
 */

