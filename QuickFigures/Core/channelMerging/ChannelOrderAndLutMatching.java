package channelMerging;

import java.util.ArrayList;

/**Sometimes, the channel order of different images
  in the same figure will be different
  This class contains code to reorder the channels of several images to match one another.
  Also possible to match the colors and the display ranges of the channel*/
public class ChannelOrderAndLutMatching {

	public static final int ORDER_ONLY=0;
	public static final int lUT_ONLY=1;
	public static final int ORDER_AND_COLOR=2;
	public static final int DISPLAY_RANGE=3;
	
	/**returns a string describing what */
	public String matchStrategy(int mode) {
		if (mode==ORDER_ONLY) return "Order of channels will be corrected to match first image";
		if (mode== ORDER_AND_COLOR) return "Order and Color of channels will be corrected to match first image";
		if (mode== lUT_ONLY) return "Color of channels will be corrected to match first image";
		
		return "Display range";
	}
	

	/** will alter the channel order of many edited multichannel images to that of the reference.
	  If the last argument is set to anything other than orderOnly, this method also match the channel colors
	 */
	public void matchChannels(MultiChannelImage o1, ArrayList<MultiChannelImage> others, int command) {
		
		for(MultiChannelImage edited: others) {
			matchChannels(o1, edited, command);
		}
		
		
	}

	/** will alter the channel order of the edited multichannel image to that of the reference.
	  If the last argument is set to anything other than orderOnly, this method also match the channel colors
	  */
	public void matchChannels(MultiChannelImage reference, MultiChannelImage edited, int command) {
		ArrayList<String> ChannelNamesA = getAllRealChannelNames(reference);
		ArrayList<String> ChannelNamesB= getAllRealChannelNames(edited);
		
		for (int i=0; i<ChannelNamesA.size()&& i<ChannelNamesB.size();i++) {
			
			
			String channelI=ChannelNamesA.get(i);
			int channelOfRef=reference.getIndexOfChannel(channelI);
			int channelToEdit=edited.getIndexOfChannel(channelI);
			
			
			
			if (command!=ORDER_ONLY) edited.getChannelSwapper().setChannelColor(reference.getChannelColor(channelOfRef),channelToEdit);
			if (command==ORDER_ONLY||command==ORDER_AND_COLOR)edited.getChannelSwapper().swapChannelsOfImage( channelOfRef, channelToEdit);
			
		}
	}
	
	/** will alter the display range and channel order of the edited multichannel image to that of the reference. */
	public void matchDisplayRangeLUTandOrder(MultiChannelImage reference, MultiChannelImage edited) {
		matchChannels(reference,edited , ChannelOrderAndLutMatching.ORDER_AND_COLOR);
		matchDisplayRange(reference, edited);
	}
	
	/** will alter the display range of the edited multichannel image to that of the reference. of every channel*/
	public void matchDisplayRange(MultiChannelImage reference, MultiChannelImage edited) {
		ArrayList<String> ChannelNamesA = getAllRealChannelNames(reference);
		ArrayList<String> ChannelNamesB= getAllRealChannelNames(edited);
		
		for (int i=0; i<ChannelNamesA.size()&& i<ChannelNamesB.size();i++) {
			
			
			String channelI=ChannelNamesA.get(i);
			int channelToEdit=edited.getIndexOfChannel(channelI);
			setMinMaxOfParticular(channelI, (int)reference.getChannelMin(i+1), (int)reference.getChannelMax(i+1), channelToEdit, edited);
			
		}
	}
	
	
	/**lists the real channel names. Example: eGFP, texasred, Cy5*/
	ArrayList<String> getAllRealChannelNames(MultiChannelImage m1) {
		ArrayList<String> names1=new ArrayList<String>();
		for(int i=1; i<=m1.nChannels(); i++) {
			names1.add(m1.getRealChannelName(i));
		}
		return names1;
	}
	
	/**Sets the display range of each multi channel image. If no channels with the given 
	   channel name are located, does not change anything*/
	public void setAllMinMax(Iterable<MultiChannelImage> wraps, String realName, int min, int max ) {
		int chan=0;
		for(MultiChannelImage w: wraps) {
			chan = setMinMaxOfParticular(realName, min, max, chan, w);
			
		}
	}

	/**Sets the min max of a single channel. If the String realName is not null
	  attempt to identify which channel has that name to set the display range of that channel
	  If the channel is not found, sets the display range of channel number c.
	  returns the channel number that was actually used. only channel numbers 1 and above are valid */
	private int setMinMaxOfParticular(String realName, int min, int max, int c, MultiChannelImage w) {
		if (realName!=null) {
			int chanNum = w.getIndexOfChannel(realName);
		
			if (chanNum>0&&chanNum<=w.nChannels()) c=chanNum;
		}
		
		if (c>0) {
			w.setChannelMin(c, min);
			w.setChannelMax(c, max);
			w.updateDisplay();
		}
		return c;
	}
	
}
