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
package applicationAdapters;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;

import graphicalObjects.CordinateConverter;
import handles.SmartHandleList;
import imageDisplayApp.MiniToolBarPanel;
import locatedObject.Selectable;
import undo.UndoManagerPlus;

/**
 Interface with methods to return information related to the
 display window for figures , the layer set inside and what selections are made
 Also used for windows that display multichannel images
  */
public interface DisplayedImage {

	/**repaint the component used to display the item*/
	public void updateDisplay() ;
	
	/**returns the ImageWrapper*/
	public ImageWorkSheet getImageAsWorksheet() ;
	
	/**returns the cordinate converter*/
	public CordinateConverter getConverter();
	/**returns the window used to display the image*/
	public Window getWindow();
	
	
	/**Resizes the window to fit its contents*/
	public void updateWindowSize();
	
	/**returns the undo manager*/
	public UndoManagerPlus getUndoManager();
	
	/**Sets what cursor is drawn over the window*/
	public void setCursor(Cursor c);
	
	/**Methods to control the zoom*/
	public void zoomOutToDisplayEntireCanvas();
	public void zoom(String st);
	public double getZoomLevel();
	public void setZoomLevel(double z);
	
	/**methods to control the scrolling if some method other than a JScrollPane is used*/
	public void scrollPane(double d, double e);
	void setScrollCenter(double dx, double dy);
	
	/**Methods to control which frame of an annimation is shown*/
	public void setEndFrame(int frame);
	public int getEndFrame();
	public int getCurrentFrame() ;
	public void setCurrentFrame(int currentFrame);
	
	/**closes the window*/
	public void closeWindowButKeepObjects();
	
	/**Sets the selected item. the item set here is refered to only by a couple parts of the package.
	 * this may be a different object from the overlay selections and the item the user sets*/
	public Selectable getSelectedItem() ;
	public void setSelectedItem(Selectable s) ;

	/**returns a handle list for the user to resize the canvas*/
	public SmartHandleList getCanvasHandles();

	/**
	
	 */
	public Dimension getPageSize();

	
	/**some incarnations of the displayed image will update the minitoolbar panel*/
	/**
	 * @param miniToolBarPanel
	 */
	public void setSidePanel(MiniToolBarPanel miniToolBarPanel);


	
	
}
