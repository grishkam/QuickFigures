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
 * Version: 2021.2
 */
package channelMergingImageJ1;

import java.awt.Color;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.ChannelOrderAndColorWrap;
import ij.CompositeImage;
import ij.ImagePlus;
import ij.process.LUT;
import logging.IssueLog;

/**ImageJ implementation of the channel order interface 
 * @see ChannelOrderAndColorWrap*/
public class IJ1ChannelOrderWrap implements ChannelOrderAndColorWrap{

	private ImagePlus imp;
	private ImagePlusWrapper container;
	
	private IJ1ChannelSwapper swapper=new IJ1ChannelSwapper();


	private ChannelSwapListener listener;
	

	public IJ1ChannelOrderWrap(ImagePlus s) {
		this.imp=s;
		
	}
	
	public IJ1ChannelOrderWrap(ImagePlus imp2, ImagePlusWrapper imagePlusWrapper) {
		imp=imp2;
		container= imagePlusWrapper;
	}

	@Override
	public void swapChannelsOfImage(int a, int b) {
		
		swapper.swapChannelsOfImage(imp, a, b);
		
		listener.afterChanSwap();
	}

	@Override
	public void swapChannelLuts(int a, int b) {
		swapper.swapChannelLuts(imp, a, b);
		listener.afterChanSwap();
	}

	@Override
	public void setChannelColor(Color c, int chan) {
		setLutColor(c,chan);
	}
	
	@Override
	public void setChannelColor(byte[][] lut, int chan) {
		LUT l = new LUT(lut[0], lut[1], lut[2]);
		setLut(chan, l);
	}
	
	private void setLutColor(Color lut, int chan) {
		if (chan<=0) {
			IssueLog.log(" Was asked to change color for channel '0' but channel numbering starts from 1");
			
			return;
			}
		LUT createLutFromColor = LUT.createLutFromColor(lut);
		
		/**inverted lut created for some colors*/
		if (Color.black.equals(lut)) {
			createLutFromColor = LUT.createLutFromColor(Color.white);
			createLutFromColor=createLutFromColor.createInvertedLut();
		}
		setLut(chan, createLutFromColor);
	}

	private void setLut(int chan, LUT createLutFromColor) {
		if (chan<=0) {
			IssueLog.log(" Was asked to change color for channel '0' but channel numbering starts from 1");
			
			return;
			}
		
		if (imp instanceof CompositeImage)
			setLutColorWithoutDisplayRangeEdit((CompositeImage)imp, createLutFromColor, chan);
		else {
			try {
				imp.getProcessor().setLut(createLutFromColor);
			} catch (Exception e) {
				IssueLog.log("Problem, failed to set channel color");
			}
		}
	}
	
	/**Sets the channel LUT of the channel*/
	private static void setLutColorWithoutDisplayRangeEdit(CompositeImage ci4, LUT lut, int a) {
		if (a==0) {
			IssueLog.log("Error, Was asked to chang color for channel '0' but channel numbering starts from 1");
			return;
		}
		LUT[] oldluts=ci4.getLuts().clone();
		lut.min=oldluts[a-1].min;lut.max=oldluts[a-1].max;
		ci4.setChannelLut(lut, a);
	}
	
	

	@Override
	public void moveChannelOfImage(int choice1, int choice2) {
		 swapper.moveChannels(imp, choice1, choice2);
		
	}

	@Override
	public void moveChannelLutsOfImage(int choice1, int choice2) {
		swapper.moveChannelsLuts(imp, choice1, choice2);
		
	}

	public void addChannelSwapListener(ChannelSwapListener imagePlusWrapper) {
		this.listener=imagePlusWrapper;
		
	}
	
}
