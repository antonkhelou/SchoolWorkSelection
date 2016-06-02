/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: EmailBean.java                                              
 * Short description of what's in this file: This class represents
 *  an instance of a email.                     
 */
package beans;


import java.util.Arrays;
import java.util.Date;

@SuppressWarnings("unchecked")
public class EmailBean implements Comparable
{
	private int id;
	private String fromSender;
	private String[] TOReceiver;
	private String[] CCReceiver;
	private String[] BCCReceiver;
	private String subject;
	private String body;
	private String containingFolderName;
	private Date date;
	
	public EmailBean()
	{
		super();
		this.fromSender = "";
		this.TOReceiver = null;
		this.CCReceiver = null;
		this.BCCReceiver = null;
		this.subject = "";
		this.body = "";
		this.containingFolderName = "";
		this.date = new Date();
	}
	
	public EmailBean(String fromSender,String[] TOReceiver, String[] CCReceiver, String[] BCCReceiver,
			String subject, String body, String containingFolderName, Date date)
	{
		super();
		this.fromSender = fromSender;
		this.TOReceiver = TOReceiver;
		this.CCReceiver = CCReceiver;
		this.BCCReceiver = BCCReceiver;
		this.subject = subject;
		this.body = body;
		this.containingFolderName = containingFolderName;
		this.date = date;
	}
	
	public EmailBean(int id, String fromSender,String[] TOReceiver, String[] CCReceiver, String[] BCCReceiver,
			String subject, String body, String containingFolderName, Date date)
	{
		super();
		this.id = id;
		this.fromSender = fromSender;
		this.TOReceiver = TOReceiver;
		this.CCReceiver = CCReceiver;
		this.BCCReceiver = BCCReceiver;
		this.subject = subject;
		this.body = body;
		this.containingFolderName = containingFolderName;
		this.date = date;
	}

	public int getId() {
		return id;
	}
	
	
	
	public String getfromSender() {
		return fromSender;
	}

	public void setfromSender(String fromSender) {
		this.fromSender = fromSender;
	}

	public String[] getTOReceiver() {
		return TOReceiver;
	}

	public void setTOReceiver(String[] TOReceiver) {
		this.TOReceiver = TOReceiver;
	}

	public String[] getCCReceiver() {
		return CCReceiver;
	}

	public void setCCReceiver(String[] CCReceiver) {
		this.CCReceiver = CCReceiver;
	}

	public String[] getBCCReceiver() {
		return BCCReceiver;
	}

	public void setBCCReceiver(String[] BCCReceiver) {
		this.BCCReceiver = BCCReceiver;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String getContainingFolderName() {
		return containingFolderName;
	}

	public void setContainingFolderName(String containingFolderName) {
		this.containingFolderName = containingFolderName;
	}
	
	public Date getDate() 
	{
		return date;
	}

	public void setDate(Date date) 
	{
		this.date = date;
	}
	
	public String toString() 
	{
		return "EmailBean [BCCReceiver=" + Arrays.toString(BCCReceiver)
				+ ", CCReceiver=" + Arrays.toString(CCReceiver)
				+ ", TOReceiver=" + Arrays.toString(TOReceiver) + ", body="
				+ body + ", containingFolderName=" + containingFolderName
				+ ", date=" + date + ", fromSender=" + fromSender + ", id="
				+ id + ", subject=" + subject + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(BCCReceiver);
		result = prime * result + Arrays.hashCode(CCReceiver);
		result = prime * result + Arrays.hashCode(TOReceiver);
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime
				* result
				+ ((containingFolderName == null) ? 0 : containingFolderName
						.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((fromSender == null) ? 0 : fromSender.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailBean other = (EmailBean) obj;
		if (!Arrays.equals(BCCReceiver, other.BCCReceiver))
			return false;
		if (!Arrays.equals(CCReceiver, other.CCReceiver))
			return false;
		if (!Arrays.equals(TOReceiver, other.TOReceiver))
			return false;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (containingFolderName == null) {
			if (other.containingFolderName != null)
				return false;
		} else if (!containingFolderName.equals(other.containingFolderName))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (fromSender == null) {
			if (other.fromSender != null)
				return false;
		} else if (!fromSender.equals(other.fromSender))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}


	public int compareTo(Object obj)
	{
		return date.compareTo(((EmailBean)obj).getDate());
	}	
}
