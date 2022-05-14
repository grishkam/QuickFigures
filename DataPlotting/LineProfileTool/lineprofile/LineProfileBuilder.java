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
 * Date Created: Jan 29, 2022
 * Date Modified: Feb 1, 2022
 * Version: 2022.1
 */
package lineprofile;
import java.awt.Color;
import java.util.ArrayList;

import applicationAdapters.PixelWrapper;
import channelMerging.ChannelEntry;
import channelMerging.MultiChannelImage;
import dataSeries.XYDataSeries;
import logging.IssueLog;

/**Creates line profiles for an XY plot*/
public class LineProfileBuilder {

	
	/**
	 * @param image
	 * @param channelChoices
	 */
	public LineProfileBuilder(MultiChannelImage image, ArrayList<Integer> channelChoices) {
		
		
	}

	/**
	 * @param profileImage
	 * @param channelChoices
	 * @return 
	 */
	public static  ArrayList<XYDataSeries> createProfiles(MultiChannelImage profileImage, ArrayList<Integer> channelChoices, ProfileValueType usepercent2, ProfileDistanceType distance, MultiChannelImage originalImage) {
		ArrayList<XYDataSeries> profiles=new ArrayList<XYDataSeries>();
		
		for(Integer c: channelChoices) {
			
			PixelWrapper pix = profileImage.getPixelWrapperForSlice(c, 1,1);
			
			float[][] data=(float[][]) pix.getRawData();
			float[] averages=new float[data.length];
			for(int i=0; i<averages.length; i++) {
				averages[i]=getMean(data[i]);
				if(data[i].length==0) {
					IssueLog.log("missing informaiton");
				}
			}
			
			if(usepercent2==ProfileValueType.PERCENT_OF_MAX_IN_PROFILE) {
				float max=0;
				for(int i=0; i<averages.length; i++)
					max=Math.max(averages[i],max);
				if(max>0) {
					for(int i=0; i<averages.length; i++)
						averages[i]=100*(averages[i]/max);//converts to percentage
				}
			}
			
			
			if(usepercent2==ProfileValueType.PERCENT_OF_MAX_IN_IMAGE) {
				float[][] dataOriginal=(float[][]) originalImage.getPixelWrapperForSlice(c, 1,1).getRawData();
				float max=0;
				for(float[] part: dataOriginal)
					for(float current:part)
					max=Math.max( current,max);
				
				if(max>0) {
					for(int i=0; i<averages.length; i++)
						averages[i]=100*(averages[i]/max);//converts to percentage
				}
			}
			
			if(usepercent2==ProfileValueType.PERCENT_OF_DISPLAY_RANGE) {
				
				float max=(float) profileImage.getChannelMax(c);
				float min=(float) profileImage.getChannelMin(c);
				
				if(max>0) {
					for(int i=0; i<averages.length; i++)
						averages[i]=100*(Math.max(0,averages[i]-min)/max);//converts to percentage
				}
			}
			
			
			
			XYDataSeries profile1 = new XYDataSeries(getChannelName(profileImage, c), averages);
			
			if (distance==ProfileDistanceType.PERCENT) {
				double maxPosition= averages.length;
				profile1.scalePoints(100/maxPosition, 1);
			}
			if (distance==ProfileDistanceType.UNITS) {
				
				profile1.scalePoints(profileImage.getScaleInfo().getPixelWidth(), 1);
			}
			
			profile1.setTag("Color", getChannelColor(profileImage, c));
			profiles.add(profile1);
		}
		return profiles;
	}

	/**returns the channel color that will be used for the plot
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

	/**returns the channel name that will be used for the figure legend
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
