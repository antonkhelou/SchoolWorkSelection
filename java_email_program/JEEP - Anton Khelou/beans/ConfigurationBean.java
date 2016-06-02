/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: ConfigurationBean.java                                              
 * Short description of what's in this file: This class represents
 *  an instance of a configuration file.                     
 */
package beans;

public class ConfigurationBean 
{
	private String userName;
	private String emailAddress;
	private String pop3ServerURL;
	private String smtpServerURL;
	private String pop3ServerUserName;
	private String pop3ServerPassword;
	private String pop3ServerPortNumber;
	private String smtpServerPortNumber;
	private boolean isGMailAccount;
	private String mySQLdatabaseURL;
	private String mySQLdatabasePortNumber;
	private String mySQLdatabaseUserName;
	private String mySQLdatabasePassword;
	
	public ConfigurationBean()
	{
		super();
		this.userName = "";
		this.emailAddress = "";
		this.pop3ServerURL = "";
		this.smtpServerURL = "";
		this.pop3ServerUserName = "";
		this.pop3ServerPassword = "";
		this.pop3ServerPortNumber = "110";
		this.smtpServerPortNumber = "25";
		this.isGMailAccount = false;
		this.mySQLdatabaseURL = "";
		this.mySQLdatabasePortNumber = "3306";
		this.mySQLdatabaseUserName = "";
		this.mySQLdatabasePassword = "";
	}
	
	public ConfigurationBean(String userName, String emailAddress,
			String pop3ServerURL, String smtpServerURL,
			String pop3ServerUserName, String pop3ServerPassword,
			String pop3ServerPortNumber, String smtpServerPortNumber,
			boolean isGMailAccount, String mySQLdatabaseURL,
			String mySQLdatabasePortNumber, String mySQLdatabaseUserName,
			String mySQLdatabasePassword) 
	{
		super();
		this.userName = userName;
		this.emailAddress = emailAddress;
		this.pop3ServerURL = pop3ServerURL;
		this.smtpServerURL = smtpServerURL;
		this.pop3ServerUserName = pop3ServerUserName;
		this.pop3ServerPassword = pop3ServerPassword;
		this.pop3ServerPortNumber = pop3ServerPortNumber;
		this.smtpServerPortNumber = smtpServerPortNumber;
		this.isGMailAccount = isGMailAccount;
		this.mySQLdatabaseURL = mySQLdatabaseURL;
		this.mySQLdatabasePortNumber = mySQLdatabasePortNumber;
		this.mySQLdatabaseUserName = mySQLdatabaseUserName;
		this.mySQLdatabasePassword = mySQLdatabasePassword;
	}

	public String getUserName() 
	{
		return userName;
	}

	public void setUserName(String userName) 
	{
		this.userName = userName;
	}

	public String getEmailAddress() 
	{
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) 
	{
		this.emailAddress = emailAddress;
	}

	public String getPop3ServerURL() 
	{
		return pop3ServerURL;
	}

	public void setPop3ServerURL(String pop3ServerURL) 
	{
		this.pop3ServerURL = pop3ServerURL;
	}

	public String getSmtpServerURL()
	{
		return smtpServerURL;
	}

	public void setSmtpServerURL(String smtpServerURL)
	{
		this.smtpServerURL = smtpServerURL;
	}

	public String getPop3ServerUserName() 
	{
		return pop3ServerUserName;
	}

	public void setPop3ServerUserName(String pop3ServerUserName) 
	{
		this.pop3ServerUserName = pop3ServerUserName;
	}

	public String getPop3ServerPassword()
	{
		return pop3ServerPassword;
	}

	public void setPop3ServerPassword(String pop3ServerPassword)
	{
		this.pop3ServerPassword = pop3ServerPassword;
	}

	public String getPop3ServerPortNumber() 
	{
		return pop3ServerPortNumber;
	}

	public void setPop3ServerPortNumber(String pop3ServerPortNumber)
	{
		this.pop3ServerPortNumber = pop3ServerPortNumber;
	}

	public String getSmtpServerPortNumber() 
	{
		return smtpServerPortNumber;
	}

	public void setSmtpServerPortNumber(String smtpServerPortNumber) 
	{
		this.smtpServerPortNumber = smtpServerPortNumber;
	}

	public boolean isGMailAccount()
	{
		return isGMailAccount;
	}

	public void setGMailAccount(boolean isGMailAccount)
	{
		this.isGMailAccount = isGMailAccount;
	}

	public String getMySQLdatabaseURL() 
	{
		return mySQLdatabaseURL;
	}

	public void setMySQLdatabaseURL(String mySQLdatabaseURL) 
	{
		this.mySQLdatabaseURL = mySQLdatabaseURL;
	}

	public String getMySQLdatabasePortNumber() 
	{
		return mySQLdatabasePortNumber;
	}

	public void setMySQLdatabasePortNumber(String mySQLdatabasePortNumber)
	{
		this.mySQLdatabasePortNumber = mySQLdatabasePortNumber;
	}

	public String getMySQLdatabaseUserName()
	{
		return mySQLdatabaseUserName;
	}

	public void setMySQLdatabaseUserName(String mySQLdatabaseUserName)
	{
		this.mySQLdatabaseUserName = mySQLdatabaseUserName;
	}

	public String getMySQLdatabasePassword() 
	{
		return mySQLdatabasePassword;
	}

	public void setMySQLdatabasePassword(String mySQLdatabasePassword)
	{
		this.mySQLdatabasePassword = mySQLdatabasePassword;
	}

	@Override
	public String toString() {
		return "ConfigurationBean [emailAddress=" + emailAddress
				+ ", isGMailAccount=" + isGMailAccount
				+ ", mySQLdatabasePassword=" + mySQLdatabasePassword
				+ ", mySQLdatabasePortNumber=" + mySQLdatabasePortNumber
				+ ", mySQLdatabaseURL=" + mySQLdatabaseURL
				+ ", mySQLdatabaseUserName=" + mySQLdatabaseUserName
				+ ", pop3ServerPassword=" + pop3ServerPassword
				+ ", pop3ServerPortNumber=" + pop3ServerPortNumber
				+ ", pop3ServerURL=" + pop3ServerURL + ", pop3ServerUserName="
				+ pop3ServerUserName + ", smtpServerPortNumber="
				+ smtpServerPortNumber + ", smtpServerURL=" + smtpServerURL
				+ ", userName=" + userName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime * result + (isGMailAccount ? 1231 : 1237);
		result = prime
				* result
				+ ((mySQLdatabasePassword == null) ? 0 : mySQLdatabasePassword
						.hashCode());
		result = prime
				* result
				+ ((mySQLdatabasePortNumber == null) ? 0
						: mySQLdatabasePortNumber.hashCode());
		result = prime
				* result
				+ ((mySQLdatabaseURL == null) ? 0 : mySQLdatabaseURL.hashCode());
		result = prime
				* result
				+ ((mySQLdatabaseUserName == null) ? 0 : mySQLdatabaseUserName
						.hashCode());
		result = prime
				* result
				+ ((pop3ServerPassword == null) ? 0 : pop3ServerPassword
						.hashCode());
		result = prime
				* result
				+ ((pop3ServerPortNumber == null) ? 0 : pop3ServerPortNumber
						.hashCode());
		result = prime * result
				+ ((pop3ServerURL == null) ? 0 : pop3ServerURL.hashCode());
		result = prime
				* result
				+ ((pop3ServerUserName == null) ? 0 : pop3ServerUserName
						.hashCode());
		result = prime
				* result
				+ ((smtpServerPortNumber == null) ? 0 : smtpServerPortNumber
						.hashCode());
		result = prime * result
				+ ((smtpServerURL == null) ? 0 : smtpServerURL.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
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
		ConfigurationBean other = (ConfigurationBean) obj;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		if (isGMailAccount != other.isGMailAccount)
			return false;
		if (mySQLdatabasePassword == null) {
			if (other.mySQLdatabasePassword != null)
				return false;
		} else if (!mySQLdatabasePassword.equals(other.mySQLdatabasePassword))
			return false;
		if (mySQLdatabasePortNumber == null) {
			if (other.mySQLdatabasePortNumber != null)
				return false;
		} else if (!mySQLdatabasePortNumber
				.equals(other.mySQLdatabasePortNumber))
			return false;
		if (mySQLdatabaseURL == null) {
			if (other.mySQLdatabaseURL != null)
				return false;
		} else if (!mySQLdatabaseURL.equals(other.mySQLdatabaseURL))
			return false;
		if (mySQLdatabaseUserName == null) {
			if (other.mySQLdatabaseUserName != null)
				return false;
		} else if (!mySQLdatabaseUserName.equals(other.mySQLdatabaseUserName))
			return false;
		if (pop3ServerPassword == null) {
			if (other.pop3ServerPassword != null)
				return false;
		} else if (!pop3ServerPassword.equals(other.pop3ServerPassword))
			return false;
		if (pop3ServerPortNumber == null) {
			if (other.pop3ServerPortNumber != null)
				return false;
		} else if (!pop3ServerPortNumber.equals(other.pop3ServerPortNumber))
			return false;
		if (pop3ServerURL == null) {
			if (other.pop3ServerURL != null)
				return false;
		} else if (!pop3ServerURL.equals(other.pop3ServerURL))
			return false;
		if (pop3ServerUserName == null) {
			if (other.pop3ServerUserName != null)
				return false;
		} else if (!pop3ServerUserName.equals(other.pop3ServerUserName))
			return false;
		if (smtpServerPortNumber == null) {
			if (other.smtpServerPortNumber != null)
				return false;
		} else if (!smtpServerPortNumber.equals(other.smtpServerPortNumber))
			return false;
		if (smtpServerURL == null) {
			if (other.smtpServerURL != null)
				return false;
		} else if (!smtpServerURL.equals(other.smtpServerURL))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
}
