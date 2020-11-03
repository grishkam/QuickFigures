package applicationAdapters;

public interface PixelContainer {
	
	public PixelWrapper getPixelWrapper();
	/**resizes the Canvas filling all the newly added space with white*/
	public void CanvasResizePixelsOnly(int width, int height, int xOff, int yOff);
	public int width();
	public int height();
	
	
	

}
