/*
 * Project  name: JEEP - Java Exclusive Email Program                                             
 * Project short description: A java built email client program.                                                                 
 * Programmer name: Anton Khelou                                                
 * Date completed: December 9, 2009                                             
 * Date modified: N/A                                             
 * Modification: N/A                                                                 
 * File name: FolderBean.java                                              
 * Short description of what's in this file: This class represents
 *  an instance of a folder.                     
 */
package beans;

@SuppressWarnings("unchecked")
public class FolderBean implements Comparable
{
	private String folderName;
	
	public FolderBean()
	{
		super();
		this.folderName = "";
	}

	public FolderBean(String folderName) 
	{
		super();
		this.folderName = folderName;
	}

	public String getFolderName() 
	{
		return folderName;
	}

	public void setFolderName(String folderName) 
	{
		this.folderName = folderName;
	}

	@Override
	public String toString() {
		return "FolderBean [folderName=" + folderName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((folderName == null) ? 0 : folderName.hashCode());
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
		FolderBean other = (FolderBean) obj;
		if (folderName == null) {
			if (other.folderName != null)
				return false;
		} else if (!folderName.equals(other.folderName))
			return false;
		return true;
	}

	public int compareTo(Object obj) 
	{
		return folderName.compareTo(((FolderBean)obj).getFolderName());
	}
	
}
