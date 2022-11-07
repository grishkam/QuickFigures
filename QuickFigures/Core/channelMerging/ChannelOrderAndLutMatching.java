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
 * Date Modified: Jan 4, 2021
 * Version: 2022.2
 */
package channelMerging;

import java.util.ArrayList;

import logging.IssueLog;

/**Sometimes, the channel order of different images
  in the same figure will be different. 
  This class contains code to reorder the channels of several images to match one another.
  Also possible to match the colors and the display ranges of the channel.
  */
public class ChannelOrderAndLutMatching {

	/**Contants for the diffent type of matching that can be done*/
	public static final int ORDER_ONLY=0, lUT_ONLY=1, ORDER_AND_COLOR=2, DISPLAY_RANGE=3;
	
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
		boolean sameOrder = isOrderSame(ChannelNamesA, ChannelNamesB);
		
		for (int i=0; i<ChannelNamesA.size()&& i<ChannelNamesB.size();i++) {
			
			
			String channelI=ChannelNamesA.get(i);
			int channelOfRef=reference.getIndexOfChannel(channelI);
			int channelToEdit=edited.getIndexOfChannel(channelI);
			
			
			
			if (command!=ORDER_ONLY) edited.getChannelColors().setChannelColor(reference.getChannelColor(channelOfRef),channelToEdit);
			if (command==ORDER_ONLY||command==ORDER_AND_COLOR) {
				if(!sameOrder)
					edited.getChannelSwapper().swapChannelsOfImage( channelOfRef, channelToEdit);
				
			}
			
			
		}
		
	}
	
	
	/**
	 * returns true if the channel orders do not need to be matched
	 * @param channelNamesA
	 * @param channelNamesB
	 * @return
	 */
	private boolean isOrderSame(ArrayList<String> channelNamesA, ArrayList<String> channelNamesB) {
		if(channelNamesA.size()!=channelNamesB.size())
			return false;
		if(channelNamesA.size()==1||1==channelNamesB.size())
			return true;
		for(int i=0; i<channelNamesA.size()&& i<channelNamesB.size(); i++) {
			String name0 = channelNamesA.get(i);
				if(name0==null) {
					return true;//if one of the names is null, cannot match channel order anyway
				}
			boolean match1 = name0.equals(channelNamesB.get(i));
			if(!(match1))
					return false;
		}
		
		return true;
	}


	//TODO this is slow for czi files
	/** will alter the display range and channel order of the edited multichannel image to that of the reference. */
	public void matchDisplayRangeLUTandOrder(MultiChannelImage reference, MultiChannelImage edited) {
		
		matchDisplayRange(reference, edited);
	
		matchChannels(reference,edited , ChannelOrderAndLutMatching.ORDER_AND_COLOR);
		
		
		
		
	}
	
	/** will alter the display range of the edited multichannel image to that of the reference. 
	 * of every channel. Uses the channel names to determine which channel is which*/
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
		if(m1==null) {
			IssueLog.log("no image detected");
		}
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
