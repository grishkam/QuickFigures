package multiChannelFigureUI;

import channelMerging.MultiChannelWrapper;
import logging.IssueLog;
import standardDialog.ShowDisplayRange;

public class ChannelManipulations {
	/**useful method that sets the display range of channel chan without referencing*/
	public static void setDisplayRangetoMinMax(MultiChannelWrapper mrp, int chan) {
		try {
			int[] basis=mrp.getPixelWrapperForSlice(chan, 1, 1).getDistribution();
			double themin=ShowDisplayRange.findMinOfDistributionHistogram(basis);
			mrp.setChannalMin(chan, themin);
			double themax=ShowDisplayRange.findMaxOfDistributionHistogram(basis);
			mrp.setChannalMax(chan, themax);
		} catch (Exception e) {
			IssueLog.log(e);//sometimes the method  has an argument out of range and an exception
		}
	}
	
	/**Check to see is any of the channels have an invalid display range and fixes the issue*/
	public static void innitializeDisplayRangetoMinMax(MultiChannelWrapper mrp)  {
		if (mrp==null) return;
		for(int chan=1; chan<=mrp.nChannels(); chan++)  try {
		if(mrp.getChannalMin(chan)==0&&mrp.getChannalMax(chan)==0) {
			setDisplayRangetoMinMax(mrp, chan);
		}
		} catch (Throwable t) {
			IssueLog.log(t);
		}
	}
	
	
}
