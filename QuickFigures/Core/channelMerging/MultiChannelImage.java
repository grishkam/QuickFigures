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
 * Version: 2023.2
 */

package channelMerging;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;

import applicationAdapters.HasScaleInfo;
import applicationAdapters.OpenFileReference;
import applicationAdapters.PixelWrapper;
import graphicalObjects_SpecialObjects.OverlayObjectList;
import infoStorage.MetaInfoWrapper;

/**An interface representing a multidimensional image.
 */
public interface MultiChannelImage extends OpenFileReference, HasScaleInfo {


	/**returns the number of channels*/
	public int nChannels();
	/**The number of time frames*/
	public int nFrames();
	/**The number of z slices*/
	public int nSlices();
	/**returns the overall size including frames, slices, channels*/
	public int getStackSize();
	
	
	/**wrapper class for getting a single section and doing operations on it*/
	public PixelWrapper getPixelWrapperForSlice(int channel, int slice, int frame, int... dim) ;
	
	/**returns the stack index that contains the given channel, slice and frame*/
	public int getStackIndex(int channel, int slice, int frame, int... dim);
	
	
	/**returns the dimensions of the image*/
	public Dimension getDimensions();
	
	/**returns an array containing the channel slice and frame of the given index*/
	public int[] convertIndexToPosition(int i);
	
	
	
	
	/**names as specified by the microscope channel name and not the user. this can be edited by the channel swapper tool.*/
	public String getRealChannelName(int i);
	public String getRealChannelExposure(int i);
	public String getGenericChannelName(int i);
	public boolean hasChannelNameList();
	public ArrayList<String> getRealChannelNamesInOrder();
	
	/**recolors the channels based on the channel names */
	public void colorBasedOnRealChannelName();
	/**starting from one. gets the index of the channel with name 'realname'. if not found, returns 0.*/
	public int getIndexOfChannel(String realname);
	
	public String getSliceName(int channel, int slice, int frame, int...dim);
	public ChannelEntry getSliceChannelEntry(int channel, int slice, int frame, int...dim);
	
	/**returns informatino about each channel*/
	public ArrayList<ChannelEntry> getChannelEntriesInOrder();
	
	/**Set the name of a stack slice*/
	public void setSliceName( String name, int i );
	
	/**returns the stack slice name. null returns if an invalid index*/
	public String getSliceName(int stackIndex);

	

	
	/**sets the display range of the channel*/
	public double getChannelMax(int chan);
	public double getChannelMin(int chan);
	public void setChannelMax(int chan, double max);
	public void setChannelMin(int chan, double max);
	
	/**returns true if the image has split channels*/
	public boolean containsSplitedChannels();
	
	/**Sets the selected area*/
	public Rectangle getSelectionRectangle(int i);
	public void eliminateSelection(int i);
	
	
	/**for handling channel colors*/
	public Color getChannelColor(int i);
	public Object getChannelColorObject(int i);
	
	
	/**returns the channel swapper*/
	public ChannelOrderAndColorWrap getChannelSwapper();
	public ChannelColorWrap getChannelColors();
	
	public void updateDisplay();
	
	/**gets the class with methods to generate a merged RGB*/
	public ChannelMerger getChannelMerger();
	void renameBasedOnRealChannelName();
	
	
	/**Returns a scaled version of this image*/
	public MultiChannelImage scaleBilinear(double d);
	
	/** returns a cropped and scaled version of this*/
//	public MultiChannelImage  cropAtAngle(Rectangle r, double angle, double scale, Interpolation interpolateMethod);
	public MultiChannelImage  cropAtAngle(PreProcessInformation p);
	
	public static final int FRAME_DIMENSION=2, SLICE_DIMENSION=1;
	public Integer getSelectedFromDimension(int i);
	
	/**returns the bit depth*/
	public double bitDepth();
	
	
	/**
	work in progress, returns a list of objects that mimic the overlay
	 */
	public OverlayObjectList getOverlayObjects(String context);
	public void setOverlayObjects(OverlayObjectList reversed);
	
	
	
	/**A way to refer to the medadata of the image*/
	public MetaInfoWrapper getMetadataWrapper();
	
}
