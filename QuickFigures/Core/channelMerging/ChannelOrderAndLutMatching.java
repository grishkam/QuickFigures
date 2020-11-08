package channelMerging;

import java.util.ArrayList;

/**simple code to reorder the channels of several images to match another*/
public class ChannelOrderAndLutMatching {

	public static final int orderOnly=0;
	public static final int lutOnly=1;
	public static final int orderAndLutOnly=2;
	public static final int displayRange=3;
	
	public String matchStrategy(int mode) {
		if (mode==orderOnly) return "Order of channels will be corrected to match first image";
		if (mode== orderAndLutOnly) return "Order and Color of channels will be corrected to match first image";
		if (mode== lutOnly) return "Color of channels will be corrected to match first image";
		
		return "Display range";
	}
	
	public void matchOrder(MultiChannelWrapper o1, ArrayList<MultiChannelWrapper> others, int command) {
		
		for(MultiChannelWrapper edited: others) {
			matchOrder(o1, edited, command);
		}
		
		
	}

	
	public void matchOrder(MultiChannelWrapper reference, MultiChannelWrapper edited, int command) {
		ArrayList<String> ChannelNamesA = getAllRealChannelNames(reference);
		ArrayList<String> ChannelNamesB= getAllRealChannelNames(edited);
		
		for (int i=0; i<ChannelNamesA.size()&& i<ChannelNamesB.size();i++) {
			
			
			String channelI=ChannelNamesA.get(i);
			int channelOfRef=reference.getIndexOfChannel(channelI);
			int channelToEdit=edited.getIndexOfChannel(channelI);
			
			
			
			if (command>0) edited.getChannelSwapper().setChannelColor(reference.getChannelColor(channelOfRef),channelToEdit);
			if (command==0||command==2)edited.getChannelSwapper().swapChannelsOfImage( channelOfRef, channelToEdit);
			
		}
	}
	
	
	public void matchDisplayRangeLUTandOrder(MultiChannelWrapper reference, MultiChannelWrapper edited) {
		matchOrder(reference,edited , ChannelOrderAndLutMatching.orderAndLutOnly);
		matchDisplayRange(reference, edited, ChannelOrderAndLutMatching.orderAndLutOnly);
	}
	
	/** will match display ranges of every channel*/
	public void matchDisplayRange(MultiChannelWrapper reference, MultiChannelWrapper edited, int command) {
		ArrayList<String> ChannelNamesA = getAllRealChannelNames(reference);
		ArrayList<String> ChannelNamesB= getAllRealChannelNames(edited);
		
		for (int i=0; i<ChannelNamesA.size()&& i<ChannelNamesB.size();i++) {
			
			
			String channelI=ChannelNamesA.get(i);
			int channelOfRef=reference.getIndexOfChannel(channelI);
			int channelToEdit=edited.getIndexOfChannel(channelI);
			setMinMaxOfParticular(channelI, (int)reference.getChannelMin(i+1), (int)reference.getChannelMax(i+1), channelToEdit, edited);
			
		}
	}
	
	
	
	/**lists the real channel names. Example: eGFP, texasred, Cy5*/
	ArrayList<String> getAllRealChannelNames(MultiChannelWrapper m1) {
		ArrayList<String> names1=new ArrayList<String>();
		
		for(int i=1; i<=m1.nChannels(); i++) {
			names1.add(m1.getRealChannelName(i));
		}
		return names1;
	}
	
	public void setAllMinMax(Iterable<MultiChannelWrapper> wraps, String realName, int min, int max ) {
		
		int chan=0;
		for(MultiChannelWrapper w: wraps) {
			chan = setMinMaxOfParticular(realName, min, max, chan, w);
			
		}
	}

	private int setMinMaxOfParticular(String realName, int min, int max, int chan, MultiChannelWrapper w) {
		if (realName!=null) {
			int chanNum = w.getIndexOfChannel(realName);
		
			if (chanNum>0&&chanNum<=w.nChannels()) chan=chanNum;
		}
		if (chan>0) {
			w.setChannelMin(chan, min);
			w.setChannelMax(chan, max);
			w.updateDisplay();
		}
		return chan;
	}
	
}
