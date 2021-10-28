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
package channelMerging;

import java.io.Serializable;

import applicationAdapters.HasScaleInfo;

/**classes that implement this store and retrieve a particular 
 multi-dimensional image. */
public interface MultiChannelSlot extends Serializable, HasScaleInfo {

	/**returns the multidimensional image being kept*/
	public MultiChannelImage getMultichannelImage() ;
	
	
	/**options for storing an image*/
	static final int lOAD_FROM_SAVED_FILE=1,  LOAD_EMBEDDED_IMAGE=0;
	static final String[] retrivalOptions=new String[] {"load from embedded (if failed try File)", "load from file (if fail, try embedded)", "Look in working directory"};
	public int getRetrieval() ;
	public void setRetrival(int i);

	
	/**Adds a listener*/		
	public void addMultichannelUpdateListener(MultiChannelUpdateListener lis);
	/**Removes a listener*/	
	public void removeMultichannelUpdateListener(MultiChannelUpdateListener lis);
	

	
	/**stores the serialized image in an internal field*/
	public void saveImageEmbed();
	
	/**shows a dialog that the user can use to switch to a new image. 
	  will be deprecated eventually*/
	public void setImageDialog();
	
	/**makes the image visible in a window*/
	public void showImage();
	/**hides the image. might ask user to save*/
	public void hideImage();
	/**hides the image. does not ask the user to save*/
	public void hideImageWihtoutMessage();
	/**called when the user has chosen to close the image without a prompt to save it*/
	public void kill();
	
	/**returns the cropping and scaling applied*/
	public PreProcessInformation getModifications();
	
	/**returns the original version of the multidimensional image (without crop nor rotation nor scale) */
	public MultiChannelImage getUnprocessedVersion(boolean matchColorsChannelOrder);

	/**applies a new set of processes (roate_crop+scale) to the original version of the multidimensional image
	 * creating a new multidimensional image.
	 the working version is replaced by the new one while the unmodified original remains*/
	public void applyCropAndScale(PreProcessInformation process) ;
	
	/**re-applies the processes (roate_crop+scale) to the original version of the multidimensional image
	 * * creating a new multidimensional image.
	 the working version is replaced by the new one while the unmodified original remains*/
	public void redoCropAndScale();
	
	/**Sets which display layer this slot belong to */
	public void setStackDisplayLayer( ImageDisplayLayer multichannelDisplayLayer);
	/**return the display layer that this slot belong to */
	public ImageDisplayLayer getDisplayLayer();
	
	
	/**creates a copy*/
	public MultiChannelSlot copy();
	
	
	/**returns a slot that uses the same source image as this one. The new slot can maintain a separate crop area*/
	public MultiChannelSlot createPartner();
	
	 
	 /**returns which location in the stack is selected by the user*/
	CSFLocation getDisplaySlice();
	 /**sets which location in the stack is selected by the user*/
	 void setDisplaySlice(CSFLocation display);
	
	 
	 /**
	 change the channel order, display range and colors of the argument to those of this slot
	 */
	public void matchOrderAndLuts(MultiChannelImage cropped);
	
	
	/**
	returns the number of bits needed to save the original version of the file
	 */
	public int getEstimatedSizeOriginal();
	/**
	 * @return
	 */
	
	/**returns the file path for the orginal source image*/
	public String getOriginalPath();
	
	
}
