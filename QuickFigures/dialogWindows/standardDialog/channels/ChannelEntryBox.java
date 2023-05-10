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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 11, 2021
 * Version: 2023.2
 */
package standardDialog.channels;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JComboBox;
import channelMerging.ChannelEntry;

/**A combo box that allows the user to choose a channel. channel options will appear in their respective color*/
public class ChannelEntryBox extends JComboBox<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<ChannelEntry> theChannelentries;
	
	String nullString="no channel";//what to use as text if no channel entry is at that position in the list
	
	public ChannelEntryBox(ArrayList<ChannelEntry> cl) {
		this(cl, "none");
	}

	public ChannelEntryBox(int innitial, ArrayList<ChannelEntry> cl) {
		this(cl);
		
		this.setSelectedIndex(innitial);
	}
	
	
	/**creates a channel entry box with a specific text for index zero (which can be 'no channel', or 'merge all')*/
	public ChannelEntryBox(ArrayList<ChannelEntry> cl, String zeroText) {
		super(namesOfEach(cl, zeroText));
		this.nullString=zeroText;
		this.theChannelentries=cl;
		ChannelColorCellRenerer cc = new ChannelColorCellRenerer(this);
		cc.setBox(this);
		cc.setFont(cc.getFont().deriveFont((float)20));
		this.setRenderer(cc);
	}
	
	/**returns the names used for each of the options in the combo box*/
	public static String[] namesOfEach(ArrayList<ChannelEntry> theChannelentries, String zeroText) {
		String[] names = new String[theChannelentries.size()+1];
		names[0]=zeroText;
		for(int i=1;i<=names.length-1&&i-1<theChannelentries.size(); i++) {
			names[i]=getUsedChanName(theChannelentries.get(i-1), zeroText);//.getRealChannelName();
			
		}
		return names;
	}
	
	public static String getUsedChanName(ChannelEntry c1, String zeroText) {
		String st = zeroText; 
		if (c1!=null) {
			st=c1.getLabelForMenuItem();
		}
		return st;
	}
	
	
	
	
	
	public void drawRainbowString(Graphics g, ChannelEntry c1, int x, int y) {
		drawRainbowString(g,c1, x,y, nullString);
		
	}
	
	/**draws the text for the given channel entry in the appropriate color. if not channel is given draws the text in black*/
public static void drawRainbowString(Graphics g, ChannelEntry c1, int x, int y, String nullString) {
		
		String st = nullString; 
		if (c1!=null) {
			st=c1.getRealChannelName();//if possible uses the channel name
			if (st==null) st=c1.getLabel();//if not, then there may be a different label used
			if (st==null) st="Ch "+c1.getOriginalChannelIndex();//if no other label is available, uses the channel number
		}
		
		 {
			
			if (c1!=null) {
				g.setColor(c1.getColor());
				g.drawString(st, x, y);
			} else {
				g.setColor(Color.black);
				g.drawString(nullString, x, y);
			}
			
		}
		
	}
	
	
	

}
