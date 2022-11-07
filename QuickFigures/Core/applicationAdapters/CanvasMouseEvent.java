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
package applicationAdapters;

import java.awt.Component;
import java.awt.Point;
import java.io.Serializable;

import javax.swing.undo.AbstractUndoableEdit;

import selectedItemMenus.LayerSelectionSystem;
import undo.UndoManagerPlus;

/**An interface with methods that return information about mouse events
 such as thEach figure has its own coordinate. 
 Broadly similar to java's MouseEvent class except with a few methods
 specifically relevant to QuickFigures images */
public interface CanvasMouseEvent extends Serializable {

	/**The point clicked given in my coordinate system. Immediately useful for may tools*/
	Point getCoordinatePoint() ;
	/**The x and y for the point above*/
	int getCoordinateX();
	int getCoordinateY();
	
	/**The raw location of the click point as given by the MouseEvent.getX()*/
	int getClickedXScreen();
	/**The raw location of the click point as given by the MouseEvent.getX()*/
	int getClickedYScreen();
	
	
	/**method for converting the raw points into the coordinate system of my canvas */
	int convertClickedXImage(int x);
	int convertClickedYImage(int y);



	/**The image that is being clicked on */
	DisplayedImage getAsDisplay();
	
	
	/**Adds an undo to the appropriate undo manager*/
	public void addUndo(AbstractUndoableEdit... e);
	/**returns the undo manager*/
	public UndoManagerPlus getUndoManager();
	/**Returns the LayerSelection*/
	LayerSelectionSystem getSelectionSystem();

	

	/**returns the mouse event information*/
	java.awt.event.MouseEvent getAwtEvent();
	public boolean shiftDown();
	public int clickCount();
	public boolean altKeyDown();
	int mouseButton();
	Component getComponent();
	Object getSource();
	boolean isPopupTrigger();
	boolean isMetaDown();
	boolean isControlDown();
	
	

}
