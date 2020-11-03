package channelLabels;

import java.util.ArrayList;

import channelMerging.ChannelEntry;

public interface ChannelLabel {
	public ArrayList<ChannelEntry> getChanEntries();
	public void setParaGraphToChannels();
	public ChannelLabelProperties getChannelLabelproperties();
	public void setChannelLabelproperties(ChannelLabelProperties channelLabelproperties) ;
	
}
