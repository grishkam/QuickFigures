package channelLabels;

import java.util.ArrayList;

import channelMerging.ChannelEntry;

public interface ChannelLabelDisplay {
	/**the options for the merged image label*/
	 public static final int Merge=0, merge=1, merged=3, Merged=2, none=5, channels=4, only=6, channels2=7, Merge_Rag=8, channels2Only=9;



static String[] MergeLabelOptions= {"Merge",  "merge", "Merged", "merged", "Channel Labels", "No Merge Label ", "Only Label the Merge ", "Single Line Channel Labels", "Soni Style"}; 

public void setChannels(ArrayList<ChannelEntry> chans);

}
