package channelLabels;

import java.io.Serializable;
import java.util.ArrayList;

import channelMerging.ChannelEntry;

/**The channel label*/
public interface ChannelLabel extends Serializable {
	public ArrayList<ChannelEntry> getChanEntries();
	public void setParaGraphToChannels();
	public ChannelLabelProperties getChannelLabelproperties();
	public void setChannelLabelproperties(ChannelLabelProperties channelLabelproperties) ;
	
}
