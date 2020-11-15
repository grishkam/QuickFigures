package channelMerging;

import applicationAdapters.DisplayedImage;

public interface MultiChannelDisplayWrapper extends DisplayedImage {

	public  MultiChannelWrapper getMultiChannelWrapper();
	public int getCurrentChannel();
	public int getCurrentFrame();
	public int getCurrentSlice();
}
