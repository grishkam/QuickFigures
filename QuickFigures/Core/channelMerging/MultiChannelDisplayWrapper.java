package channelMerging;

import applicationAdapters.DisplayedImage;

/**a subinterface of displayed image that is written to 
  contain a multichannel image*/
public interface MultiChannelDisplayWrapper extends DisplayedImage {

	public  MultiChannelImage getContainedMultiChannel();
	public int getCurrentChannel();
	public int getCurrentFrame();
	public int getCurrentSlice();
}
