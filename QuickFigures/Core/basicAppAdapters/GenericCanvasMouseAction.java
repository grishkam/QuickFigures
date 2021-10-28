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
package basicAppAdapters;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.undo.AbstractUndoableEdit;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWorkSheet;
import basicMenusForApp.SelectedSetLayerSelector;
import graphicalObjects.CordinateConverter;
import selectedItemMenus.LayerSelectionSystem;
import undo.UndoManagerPlus;

/**Simple class that stores information related to the context
  of a mouse event. this class is passed as an argument to different
  tools. Objects of this class are passed as arguments to tools
  */
public class GenericCanvasMouseAction implements CanvasMouseEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient DisplayedImage clickedFigureDisplay;
	transient MouseEvent e;
	
	public GenericCanvasMouseAction(DisplayedImage imp, MouseEvent e) {
		this.clickedFigureDisplay=imp;
		this.e=e;
	}
	
	public DisplayedImage getImageDispay() {return clickedFigureDisplay;}
	public ImageWorkSheet getImage() {return clickedFigureDisplay.getImageAsWorksheet();}
	
	/**returns the cordinate of the clickpoint on the canvas*/
	@Override
	public int getCoordinateX() {
		return this.convertClickedXImage(e.getX());
	}

	@Override
	public int getCoordinateY() {
		return this.convertClickedYImage( e.getY());
	}
	
	public Point getCoordinatePoint() {
		return new Point(getCoordinateX(), getCoordinateY());
	}
	
	/**When given the clickpoint relative to a the screen or compoent geometry,
	  this returns the cordinate in the image*/
	@Override
	public int convertClickedXImage( int x) {
		return (int) getUsedConverter().unTransformX(x);
	}

	@Override
	public int convertClickedYImage( int y) {
		return (int) getUsedConverter().unTransformY(y);
	}

	/**
	returns the cordinate converter for the clicked canvas
	 */
	public CordinateConverter getUsedConverter() {
		return clickedFigureDisplay.getConverter();
	}
	
	@Override
	public int getClickedXScreen() {
		return e.getX();
	}

	@Override
	public int getClickedYScreen() {
		return e.getY();
	}

	@Override
	public DisplayedImage getAsDisplay() {
		return clickedFigureDisplay;
	}
	
	


	@Override
	public int mouseButton() {
		return e.getButton();
	}
	
	
	public int clickCount() {
		return e.getClickCount();
	}

	@Override
	public boolean altKeyDown() {
		return e.isAltDown();
	}
	
	public boolean shiftDown() {
		return e.isShiftDown();
	}

	@Override
	public Component getComponent() {
		return e.getComponent();
	}

	@Override
	public Object getSource() {
		return e.getSource();
	}

	@Override
	public boolean isPopupTrigger() {
		if (e.isPopupTrigger()) return true;
		if(e.getButton()==3) return true;
		return false;
	}

	@Override
	public boolean isMetaDown() {
		return e.isMetaDown();
	}

	@Override
	public MouseEvent getAwtEvent() {
		return e;
	}
	
	public boolean isControlDown() {
		return e.isControlDown();
	}

	@Override
	public LayerSelectionSystem getSelectionSystem() {
		return new SelectedSetLayerSelector(clickedFigureDisplay.getImageAsWorksheet());
		
	}

	@Override
	public void addUndo(AbstractUndoableEdit... e) {
		
		if(e!=null &&clickedFigureDisplay!=null &&clickedFigureDisplay.getUndoManager()!=null) {
			clickedFigureDisplay.getUndoManager().addEdits(e);
		}
	}

	@Override
	public UndoManagerPlus getUndoManager() {
		return clickedFigureDisplay.getUndoManager();
	}
}
