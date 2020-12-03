package channelLabels;

import java.io.Serializable;
import java.util.ArrayList;

import channelMerging.ChannelEntry;

/**The channel label object must perform certain tasks*/
public interface ChannelLabel extends Serializable {
	public ArrayList<ChannelEntry> getChanEntries();
	public void setParaGraphToChannels();
	public ChannelLabelProperties getChannelLabelProperties();
	public void setChannelLabelproperties(ChannelLabelProperties channelLabelproperties) ;
	
}
