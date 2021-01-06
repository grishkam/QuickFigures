/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package multiChannelFigureUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import channelMerging.ChannelEntry;
import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.MultiChannelImage;
import figureEditDialogs.SelectImageDialog;
import undo.ChannelDisplayUndo;
import undo.CombinedEdit;

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
	private ArrayList<MultiChannelImage> items;

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
		
		 MultiChannelImage sourceDisplayRanges = items.get(0);
		 
		 
			CombinedEdit undo = ChannelDisplayUndo.createMany(items, this);
			
			if (arg0.getActionCommand().equals(minMaxCommand)) {
				WindowLevelDialog.showWLDialogs(chans,  sourceDisplayRanges , this, WindowLevelDialog.MIN_MAX , undo);
				
			}
			if (arg0.getActionCommand().equals(WLCommand)) {
				
				WindowLevelDialog.showWLDialogs(chans,  sourceDisplayRanges , this, WindowLevelDialog.WINDOW_LEVEL, undo );
				
			}
			
			if (arg0.getActionCommand().equals(orderCommand)) {
				
				new ChannelOrderAndLutMatching().matchChannels(sourceDisplayRanges , items, 2);
				
			}
			
			if (arg0.getActionCommand().equals(orderCommand2)) {
				
				new ChannelOrderAndLutMatching().matchChannels(sourceDisplayRanges, items, 2);
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

	public static void setAllChannelMinMax(ArrayList<MultiChannelImage> items, int chan, String realName, double min, double max) {
		for(MultiChannelImage w: items) {
			chan = ChannelPanelEditingMenu.getBestMatchToChannel(w, realName, chan);
			w.setChannelMin(chan, min);
			w.setChannelMax(chan, max);
			w.updateDisplay();
		}
	}
	
	@Override
	public void updateAllDisplaysWithRealChannel(String st) {
		for(MultiChannelImage w: items) {w.updateDisplay();};
		
	}

	/**When given a channel real name, attempts to find the index of that channel, returns given index if search fails*/
	public static int getRealIndex(int chan, String realName, MultiChannelImage w) {
		if (realName!=null) {
			int chanNum = w.getIndexOfChannel(realName);
		
			if (chanNum>0&&chanNum<=w.nChannels()) chan=chanNum;
		}
		return chan;
	}

	

}
