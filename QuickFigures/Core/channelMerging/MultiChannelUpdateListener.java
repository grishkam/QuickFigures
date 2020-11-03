package channelMerging;

public interface MultiChannelUpdateListener {
	/**Called with any update to the pixels or display range*/
	public void onImageUpdated();
	
	/**Called when the image is first set up. In other words whenever it is
	  the first time this class gets an image*/
	public void onImageInitiated();
}
