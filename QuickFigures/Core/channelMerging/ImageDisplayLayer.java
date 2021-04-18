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
 * Date Modified: April 18, 2021
 * Version: 2021.1
 */
package channelMerging;

import java.awt.Rectangle;
import java.util.ArrayList;

import channelLabels.ChannelLabelManager;
import figureOrganizer.PanelList;
import figureOrganizer.PanelManager;
import figureOrganizer.PanelSetter;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageScaling.ScaleInformation;

/**A specialized object that contains a source multi-dimensional image
 * and a list of panels that display various parts of that image*/
public interface ImageDisplayLayer extends GraphicLayer {
	
	/**returns the titble of the image being displayed*/
	public String getTitle();
	
	
	
	/**getter method for the panel manager */
	public PanelManager getPanelManager();
	/**getter method for the channel label manager */
	public ChannelLabelManager getChannelLabelManager();
	
	/**returns the list of panels*/
	public PanelList getPanelList();
	/**sets the list of panels*/
	public void setPanelList(PanelList stack);
	
	/**updates the images in each panel based on the source image, the display colors and display ranges*/
	public void updatePanels();
	public void updateOnlyPanelsWithChannel(String realChannelName) ;
	
	/**returns the panel setter object used by this display layer*/
	public PanelSetter getSetter();
	
	/**returns all the insets inside of this display layer*/
	public ArrayList<PanelGraphicInsetDefiner> getInsets();

	/**returns an area that contains every image panel in the figure within its bounds*/
	public Rectangle getBoundOfUsedPanels();
	

	/**gets the scale factor that was applied to the source image*/
	public ScaleInformation getPreprocessScale();
	
	/**sets the scale factor that is applied to the source image*/
	public MultiChannelImage setPreprocessScale(ScaleInformation s);
	
/**Information about the crop, rotation and scaling, that was applied to the original image*/
	PreProcessInformation getPreProcess();
	
	/**returns the slot that stores the source image. the slot should contain both the processed and original forms*/
	MultiChannelSlot getSlot();
	
	/**Returns the source stack. not the original source but the processed one*/
	public MultiChannelImage getMultiChannelImage() ;



	/**
	eliminates the current panels and creates replacements
	 */
	public void eliminateAndRecreate();
	
	

}
