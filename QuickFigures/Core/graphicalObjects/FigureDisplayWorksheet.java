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
 * Version: 2023.1
 */
package graphicalObjects;

import java.awt.Dimension;

import applicationAdapters.ImageWorkSheet;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.OverlayObjectManager;
import undo.UndoManagerPlus;

/**A super interface for classes that display the figures on a canvas.*/
public interface FigureDisplayWorksheet {

	/**updates the display. usually by repainting the component*/
	public void updateDisplay();
	
	/**Returns the primary layer*/
	public GraphicLayer getTopLevelLayer();
	
	/**Called when item is added to the worksheet*/
	public void onItemLoad(ZoomableGraphic z);
	
	/**returns An overlay this is drawn above the worksheet. These are not permanent parts of the worksheet*/
	public OverlayObjectManager getOverlaySelectionManagger();
	
	/**Returns the dimensions width/height of the worksheet*/
	public Dimension getCanvasDims();
	
	/**returns the undo manager for the worksheet*/
	public UndoManagerPlus getUndoManager();
	
	/**returns this as an instance of class 
	 * @see ImageWorkSheet*/
	public ImageWorkSheet getAsWrapper();
	
	/**returns the title*/
	public String getTitle();

	
}
