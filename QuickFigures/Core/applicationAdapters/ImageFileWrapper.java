package applicationAdapters;


public interface ImageFileWrapper extends HasScaleInfo {
	public String getTitle();
	public void setTitle(String st);
	
	public String getPath();
	
	public int getID();
	
	public boolean containsImage();
	
	/**returns true if the given object represents the same image as this one*/
	public boolean isSameImage(Object o) ;
	
	
	
}
