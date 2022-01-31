package lineprofile;
import java.awt.Color;
import java.util.ArrayList;

import applicationAdapters.PixelWrapper;
import channelMerging.ChannelEntry;
import channelMerging.MultiChannelImage;
import dataSeries.XYDataSeries;

/**
 * Author: Greg Mazo
 * Date Modified: Jan 29, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
public class LineProfileBuilder {

	/**
	 * @param image
	 * @param channelChoices
	 */
	public LineProfileBuilder(MultiChannelImage image, ArrayList<Integer> channelChoices) {
		
		createProfiles(image, channelChoices);
	}

	/**
	 * @param image
	 * @param channelChoices
	 * @return 
	 */
	public static  ArrayList<XYDataSeries> createProfiles(MultiChannelImage image, ArrayList<Integer> channelChoices) {
		ArrayList<XYDataSeries> profiles=new ArrayList<XYDataSeries>();
		
		for(Integer c: channelChoices) {
			
			PixelWrapper pix = image.getPixelWrapperForSlice(c, 1,1);
			
			float[][] data=(float[][]) pix.getRawData();
			float[] averages=new float[data.length];
			for(int i=0; i<averages.length; i++) {
				averages[i]=getMean(data[i]);
			
			}
			
			
			XYDataSeries profile1 = new XYDataSeries(getChannelName(image, c), averages);
			profile1.setTag("Color", getChannelColor(image, c));
			profiles.add(profile1);
		}
		return profiles;
	}

	/**
	 * @param image
	 * @param c
	 * @return
	 */
	private static Color getChannelColor(MultiChannelImage image, Integer c) {
		ArrayList<ChannelEntry> entry = image.getChannelEntriesInOrder();
		if(entry.size()>=c)
			return image.getChannelEntriesInOrder().get(c-1).getColor();
		return null;
	}

	/**
	 * @param image
	 * @param c
	 * @return
	 */
	public static String getChannelName(MultiChannelImage image, Integer c) {
		ArrayList<ChannelEntry> entry = image.getChannelEntriesInOrder();
		if(entry.size()>=c)
			return image.getChannelEntriesInOrder().get(c-1).getShortLabel();
		
		return ""+c;
	}
	
	/**returns the mean*/
	public static float getMean(float[] rawValues) {
		float total=0;
		for(float d: rawValues) {
			total+=d;
		}
		return total/rawValues.length;
	}

}
