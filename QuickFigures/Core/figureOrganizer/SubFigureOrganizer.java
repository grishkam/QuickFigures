/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package figureOrganizer;

import java.util.ArrayList;

import channelMerging.MultiChannelImage;

/**This interface is for all classes that maintain sets of panels that are updated 
  with changes to source images*/
public interface SubFigureOrganizer {
	
	/**Returns all the multidimensional images in the subfigure*/
	public ArrayList<MultiChannelImage> getAllSourceImages();

	public void updatePanelsAndLabelsFromSource();
	
	/**These allow any code to stop or set off the process of automatically updating the figure from 
	 * changes to the source stacks. not implemented in all subclasses */
	public void release();
	public void supress();
	
	
	
}