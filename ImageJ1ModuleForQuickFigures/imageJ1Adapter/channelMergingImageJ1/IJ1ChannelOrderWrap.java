package channelMergingImageJ1;

import java.awt.Color;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.ChannelOrderAndColorWrap;
import ij.CompositeImage;
import ij.ImagePlus;
import ij.process.LUT;
import logging.IssueLog;

public class IJ1ChannelOrderWrap implements ChannelOrderAndColorWrap{

	private ImagePlus imp;
	
	
	private IJ1ChannelSwapper swapper=new IJ1ChannelSwapper();


	private ChannelSwapListener listener;
	

	public IJ1ChannelOrderWrap(ImagePlus s) {
		this.imp=s;
		
	}
	
	public IJ1ChannelOrderWrap(ImagePlus imp2, ImagePlusWrapper imagePlusWrapper) {
		imp=imp2;
		
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
	
	private void setLutColor(Color lut, int chan) {
		if (chan<=0) {
			IssueLog.log("Error, Was asked to chang color for channel '0' but channel numbering starts from 1");
			
			return;
			}
		if (imp instanceof CompositeImage)
		setLutColorWithoutDisplayRangeEdit((CompositeImage)imp, LUT.createLutFromColor(lut), chan);
		else {
			imp.getProcessor().setLut(LUT.createLutFromColor(lut));
		}
	}
	
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
