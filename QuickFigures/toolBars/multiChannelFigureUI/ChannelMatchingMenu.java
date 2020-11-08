package multiChannelFigureUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import channelMerging.ChannelEntry;
import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.MultiChannelWrapper;
import standardDialog.SelectImageDialog;
import undo.ChannelDisplayUndo;
import undo.CompoundEdit2;

/**This class is the Match display ranges Menu that opens a dialog
 * letting the user select a list of multichannel images.
 * It can then match the display ranges, channel order and colors
 * of those images to the first one */

public class ChannelMatchingMenu extends ArrayList<JMenuItem> implements
ActionListener, DisplayRangeChangeListener  {
	static String minMaxCommand="Min/Max";
	static String WLCommand="Window/Level";
	private static String orderCommand="Match Order and Luts";
	private static String orderCommand2="Min, Max, order and luts";
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<MultiChannelWrapper> items;

	public ChannelMatchingMenu() {
		addItem(minMaxCommand);
		addItem(WLCommand);
		addItem(orderCommand);
		addItem(orderCommand2);
	}
	
	void addItem(String st) {
		JMenuItem i = new JMenuItem(st);
		i.setActionCommand(st);
		i.addActionListener(this);
		this.add(i);
	}
	
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		boolean includeChans = true;
		
		
		
		SelectImageDialog sd =SelectImageDialog.getSelectedMultis(includeChans,4);
		
		 items = sd.getList();
		 ArrayList<ChannelEntry> chans = sd.getChannelsChosen();
		
		 MultiChannelWrapper sourceDisplayRanges = items.get(0);
		 
		 
			CompoundEdit2 undo = ChannelDisplayUndo.createMany(items, this);
			
			if (arg0.getActionCommand().equals(minMaxCommand)) {
				WindowLevelDialog.showWLDialogs(chans,  sourceDisplayRanges , this, WindowLevelDialog.MIN_MAX , undo);
				
			}
			if (arg0.getActionCommand().equals(WLCommand)) {
				
				WindowLevelDialog.showWLDialogs(chans,  sourceDisplayRanges , this, WindowLevelDialog.WINDOW_LEVEL, undo );
				
			}
			
			if (arg0.getActionCommand().equals(orderCommand)) {
				
				new ChannelOrderAndLutMatching().matchOrder(sourceDisplayRanges , items, 2);
				
			}
			
			if (arg0.getActionCommand().equals(orderCommand2)) {
				
				new ChannelOrderAndLutMatching().matchOrder(sourceDisplayRanges, items, 2);
				for(int c=1; c<=sourceDisplayRanges.nChannels(); c++) {
					minMaxSet(c, sourceDisplayRanges.getChannelMin(c),sourceDisplayRanges.getChannelMax(c));
				}
			}
		 
	}

	@Override
	public void minMaxSet(int chan, double min, double max) {

		//ArrayList<MultiChannelWrapper> wraps = getAllWrappers() ;
		
		/**The real channel name will be checked against the channel names in each image
		  in the for loop. display ranges will be changed in either those with a match
		  or (if no match), those with the same number*/
		String realName=items.get(0).getRealChannelName(chan);
		
		
		setAllChannelMinMax(items, chan, realName, min, max);
		
		
	}

	public static void setAllChannelMinMax(ArrayList<MultiChannelWrapper> items, int chan, String realName, double min, double max) {
		for(MultiChannelWrapper w: items) {
			chan = ChannelSwapperToolBit2.getBestMatchToChannel(w, realName, chan);
			w.setChannelMin(chan, min);
			w.setChannelMax(chan, max);
			w.updateDisplay();
		}
	}
	
	@Override
	public void updateAllDisplaysWithRealChannel(String st) {
		for(MultiChannelWrapper w: items) {w.updateDisplay();};
		
	}

	/**When given a channel real name, attempts to find the index of that channel, returns given index if search fails*/
	public static int getRealIndex(int chan, String realName, MultiChannelWrapper w) {
		if (realName!=null) {
			int chanNum = w.getIndexOfChannel(realName);
		
			if (chanNum>0&&chanNum<=w.nChannels()) chan=chanNum;
		}
		return chan;
	}

	

}
