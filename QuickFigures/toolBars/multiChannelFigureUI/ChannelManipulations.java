package multiChannelFigureUI;

import java.awt.Color;

import channelMerging.ChannelOrderAndColorWrap;
import channelMerging.MultiChannelWrapper;
import infoStorage.BasicMetaDataHandler;
import logging.IssueLog;
import standardDialog.ShowDisplayRange;

public class ChannelManipulations {
	/**useful method that sets the display range of channel chan without referencing*/
	public static void setDisplayRangetoMinMax(MultiChannelWrapper mrp, int chan) {
		try {
			int[] basis=mrp.getPixelWrapperForSlice(chan, 1, 1).getDistribution();
			double themin=ShowDisplayRange.findMinOfDistributionHistogram(basis);
			mrp.setChannelMin(chan, themin);
			double themax=ShowDisplayRange.findMaxOfDistributionHistogram(basis);
			mrp.setChannelMax(chan, themax);
		} catch (Exception e) {
			IssueLog.log(e);//sometimes the method  has an argument out of range and an exception
		}
	}
	
	/**Check to see is any of the channels have an invalid display range and fixes the issue*/
	public static void innitializeDisplayRangetoMinMax(MultiChannelWrapper mrp)  {
		if (mrp==null) return;
		for(int chan=1; chan<=mrp.nChannels(); chan++)  try {
		if(mrp.getChannelMin(chan)==0&&mrp.getChannelMax(chan)==0) {
			setDisplayRangetoMinMax(mrp, chan);
		}
		} catch (Throwable t) {
			IssueLog.log(t);
		}
	}
	
	
	/**sets the color of channel i based on String name*/
	public static Color setChannelColor(int i, String name, ChannelOrderAndColorWrap channelSwapper) {
		name=name.trim().toLowerCase();
		if (name.equals("texasred"))
				channelSwapper.setChannelColor(Color.red, i);
		if (name.contains("egfp")||name.contains("488"))
			channelSwapper.setChannelColor(Color.green, i);
		if (name.contains("yfp"))
			channelSwapper.setChannelColor(Color.yellow, i);
		if (name.contains("rfp")||name.contains("568")||name.contains("mplum"))
			channelSwapper.setChannelColor(Color.red, i);
		if (name.equals("dapi")||name.contains("ebfp"))
			channelSwapper.setChannelColor(Color.blue, i);
		if (name.equals("brightfield"))
			channelSwapper.setChannelColor(Color.white, i);
		if (name.equals("dic") ||name.equals("tl dic"))
			channelSwapper.setChannelColor(Color.white, i);
		if (name.equals("cy5"))
			channelSwapper.setChannelColor(Color.magenta, i);
		
		Color color1 = BasicMetaDataHandler.getColor(name);//finds a color based on standard names "Red, Green, blue..."
		//IssueLog.log("Found color "+color1);
		if(color1!=null)
			channelSwapper.setChannelColor(color1, i);
		return color1;
	}
	
	
}
