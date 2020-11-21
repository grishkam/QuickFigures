package channelMerging;

import applicationAdapters.DisplayedImage;

public interface MultiChannelDisplayWrapper extends DisplayedImage {

	public  MultiChannelImage getMultiChannelWrapper();
	public int getCurrentChannel();
	public int getCurrentFrame();
	public int getCurrentSlice();
}
