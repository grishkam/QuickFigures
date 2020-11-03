package channelMerging;

import applicationAdapters.DisplayedImageWrapper;

public interface MultiChannelDisplayWrapper extends DisplayedImageWrapper {

	public  MultiChannelWrapper getMultiChannelWrapper();
	public int getCurrentChannel();
	public int getCurrentFrame();
	public int getCurrentSlice();
}
