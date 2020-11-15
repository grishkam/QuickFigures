package applicationAdapters;

/**an interface for any objects that contain a canvas of some sort.
  some implementations contain a raster of pixels with width and height some implementations 
  just contain objects*/
public interface PixelContainer {
	
	public PixelWrapper getPixelWrapper();// if the object has a raster of pixels
	
	/**resizes the Canvas filling all the newly added space with white*/
	public void CanvasResizePixelsOnly(int width, int height, int xOff, int yOff);
	
	/**returns the dimensions*/
	public int width();
	public int height();

}
