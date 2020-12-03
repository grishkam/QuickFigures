package applicationAdapters;

/**an interface for any objects that contain a canvas with a raster of pixels (of some sort).
  some implementations contain a raster of pixels with width and height some implementations 
  just contain objects*/
public interface PixelContainer {
	
	public PixelWrapper getPixelWrapper();// if the object has a raster of pixels

	
	/**returns the dimensions*/
	public int width();
	public int height();

}
