package applicationAdapters;

/**An interface representing anything with a title and save path
  that can lead to a file. */
public interface OpenFileReference extends HasScaleInfo {
	public String getTitle();
	public void setTitle(String st);
	
	public String getPath();
	
	
	
	/**True if this is an image file*/
	public boolean containsImage();
	
	/**returns true if the given object represents the same image as this one*/
	public boolean isSameImage(Object o) ;
	
	/**if open files are tracked by id numbers, returns the number*/
	public int getID();
	
	
	
}
