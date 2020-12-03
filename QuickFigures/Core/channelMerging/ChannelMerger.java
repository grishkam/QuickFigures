package channelMerging;


import applicationAdapters.PixelWrapper;
import genericMontageKit.PanelListElement;

/**Implementations of ChannelMerger merges several channels together into an RGB image*/
public interface ChannelMerger {
	
	/**Merges the channels listed in entry to generate a merged RGB.
	  If there is only one channel, the second argument indicates whether to create a greyscale*/
	public PixelWrapper generateMergedRGB( PanelListElement entry, int ChannelsInGrayScale);

}
