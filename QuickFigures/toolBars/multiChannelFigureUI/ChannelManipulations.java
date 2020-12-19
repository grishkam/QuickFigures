/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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

import java.awt.Color;

import channelMerging.ChannelOrderAndColorWrap;
import channelMerging.MultiChannelImage;
import infoStorage.BasicMetaDataHandler;
import logging.IssueLog;
import standardDialog.channels.ShowDisplayRange;

public class ChannelManipulations {
	/**useful method that sets the display range of channel chan without referencing*/
	public static void setDisplayRangetoMinMax(MultiChannelImage mrp, int chan) {
		try {
			int[] basis=mrp.getPixelWrapperForSlice(chan, 1, 1).getDistribution();
			double themin=ShowDisplayRange.findMinOfDistributionHistogram(basis);
			mrp.setChannelMin(chan, themin);
			double themax=ShowDisplayRange.findMaxOfDistributionHistogram(basis);
			mrp.setChannelMax(chan, themax);
		} catch (Exception e) {
			IssueLog.logT(e);//sometimes the method  has an argument out of range and an exception
		}
	}
	
	/**Check to see is any of the channels have an invalid display range and fixes the issue*/
	public static void innitializeDisplayRangetoMinMax(MultiChannelImage mrp)  {
		if (mrp==null) return;
		for(int chan=1; chan<=mrp.nChannels(); chan++)  try {
		if(mrp.getChannelMin(chan)==0&&mrp.getChannelMax(chan)==0) {
			setDisplayRangetoMinMax(mrp, chan);
		}
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
	
	/**sets the color of channel i based on String name*/
	public static Color setChannelColor(int i, String name, ChannelOrderAndColorWrap channelSwapper) {
		Color newColor = BasicMetaDataHandler.determineNewChannelColor(name);
		
		if(newColor!=null)
			channelSwapper.setChannelColor(newColor, i);

		
		return newColor;
	}

	
	
	
}
