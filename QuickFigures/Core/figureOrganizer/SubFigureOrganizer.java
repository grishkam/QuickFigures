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
 * Version: 2022.2
 */
package figureOrganizer;

import java.util.ArrayList;

import channelLabels.ChannelLabelManager;
import channelMerging.MultiChannelImage;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner.InsetPanelManager;

/**This interface is for all classes that maintain sets of panels that are updated 
  with changes to source images*/
public interface SubFigureOrganizer {
	
	/**Returns all the multidimensional images in the subfigure*/
	public ArrayList<MultiChannelImage> getAllSourceImages();

	/**refreshes the panels and labels based on the source images*/
	public void updatePanelsAndLabelsFromSource();
	
	
	/**This stops the process of automatically updating the figure from 
	 changes to the source stacks. not implemented in all subclasses */
	public void supress();
	
	
	/**This restarts the process of automatically updating the figure from 
	 changes to the source stacks. not implemented in all subclasses */
	public void release();
	
	
	
}
