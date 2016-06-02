/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: ContactBean.java                                              
 * Short description of what's in this file: This class represents
 *  an instance of a contact.                     
 */
package beans;

@SuppressWarnings("unchecked")
public class ContactBean implements Comparable
{
	private int id;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String telephoneNumber;
	
	public ContactBean()
	{
		super();
		this.id= -1;
		this.firstName = "";
		this.lastName = "";
		this.emailAddress = "";
		this.telephoneNumber = "5141234567";
	}
	
	public ContactBean(String firstName, String lastName, String emailAddress,
			String telephoneNumber) 
	{
		super();
		this.id= -1;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.telephoneNumber = telephoneNumber;
	}

	public ContactBean(int id, String firstName, String lastName, String emailAddress,
			String telephoneNumber) 
	{
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.telephoneNumber = telephoneNumber;
	}	
	
	public int getId() 
	{
		return id;
	}

	public String getFirstName() 
	{
		return firstName;
	}

	public void setFirstName(String firstName) 
	{
		this.firstName = firstName;
	}

	public String getLastName() 
	{
		return lastName;
	}

	public void setLastName(String lastName) 
	{
		this.lastName = lastName;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) 
	{
		this.emailAddress = emailAddress;
	}

	public String getTelephoneNumber() 
	{
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) 
	{
		this.telephoneNumber = telephoneNumber;
	}

	@Override
	public String toString() {
		return "ContactBean [emailAddress=" + emailAddress + ", firstName="
				+ firstName + ", id=" + id + ", lastName=" + lastName
				+ ", telephoneNumber=" + telephoneNumber + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
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
		ContactBean other = (ContactBean) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}

	public int compareTo(Object obj) 
	{
		return lastName.compareTo(((ContactBean)obj).getLastName());
	}


}
