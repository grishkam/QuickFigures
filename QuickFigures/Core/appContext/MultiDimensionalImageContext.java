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
 * Version: 2022.0
 */
package appContext;

import java.util.ArrayList;

import channelMerging.MultiChannelImage;
import multiChannelFigureUI.MultiChannelDisplayCreator;

/**Interface for whatever package is used to open and display multidimensional images
   A programmer may implements this interface and set the CurrentAppContext
   to an instance of his implementation. As of composing this, there is an ImageJ implementation*/
public interface MultiDimensionalImageContext {
	
	/**returns the system for creating multi dimensional images*/
  public MultiChannelDisplayCreator getMultichannelOpener();
  public MultiChannelDisplayCreator createMultichannelDisplay();
 
  /**returns a list of all multidimensional images that are open in their own windows*/
  public ArrayList< MultiChannelImage> getallVisibleMultichanal();
  
  /**Returns the active multi-dimensional image if one is present*/
  public MultiChannelImage getCurrentMultichanal();

  /**returns the default save directory for multidimensional images*/
  public String getDefaultDirectory();

  
  /**returns an aritififial image */
  MultiChannelImage getDemoExample(boolean show,String path, int nChan, int rowIndex, int scale);
  
  



  
}
