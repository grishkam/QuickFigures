package channelMerging;


import applicationAdapters.PixelWrapper;
import genericMontageKit.PanelListElement;

public interface ChannelMerger {
	
	/**Used to generate a merged RGB*/
	public PixelWrapper generateMergedRGB( PanelListElement entry, int ChannelsInGrayScale);

}
