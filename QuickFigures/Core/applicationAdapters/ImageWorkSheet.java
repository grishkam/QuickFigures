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
 * Version: 2021.1
 */
package applicationAdapters;
import infoStorage.MetaInfoWrapper;
import locatedObject.ObjectContainer;

import java.awt.Window;

import graphicalObjects.FigureDisplayWorksheet;
import imageDisplayApp.OverlayObjectManager;

/**a general interface for worksheets.
 The methods in this interface and superinterfaces must work in order for the basics of the layouts
 and layout editing to work*/
public interface ImageWorkSheet extends ObjectContainer, FigureDisplayWorksheet, OpenFileReference{
	
	public void updateDisplay();
	public DisplayedImage getImageDisplay();
	
	/**returns and shows the window (if there is one)*/
	public Window window();
	public void show();
	
	/**A way to refer to the medadata of the image*/
	public MetaInfoWrapper getMetadataWrapper();
	
	/**This object manages a list of items that are drawn above the figure. never returns null*/
	public OverlayObjectManager getOverlaySelectionManagger();
	
	/**sets the primary selected object for the image*/
	public boolean setPrimarySelectionObject(Object d);
	
	/**returns the dimensions*/
	public int width();
	public int height();
	
	/**resizes the Canvas filling all the newly added space with white*/
	public void CanvasResize(int width, int height, int xOff, int yOff);
	
	/**
	 */
	boolean allowAutoResize();
	void setAllowAutoResize(boolean allow);

}
