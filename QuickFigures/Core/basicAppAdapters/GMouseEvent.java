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
package basicAppAdapters;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.undo.AbstractUndoableEdit;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import basicMenusForApp.SelectedSetLayerSelector;
import graphicalObjects.CordinateConverter;
import imageDisplayApp.KeyDownTracker;
import selectedItemMenus.LayerSelector;

/**Simple class that stores information related to the context
  of a mouse event. this class is passed as an argument to different
  tools*/
public class GMouseEvent implements CanvasMouseEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient DisplayedImage imp;
	transient MouseEvent e;
	
	public GMouseEvent(DisplayedImage imp, MouseEvent e) {
		this.imp=imp;
		this.e=e;
	}
	
	public DisplayedImage getImageDispay() {return imp;}
	public ImageWrapper getImage() {return imp.getImageAsWrapper();}
	
	/**returns the cordinate of the clickpoin on the canvas*/
	@Override
	public int getCoordinateX() {
		//imp.getTheWindow()
		return this.convertClickedXImage(e.getX());
		//return 0;
	}

	@Override
	public int getCoordinateY() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
	
	}

	@Override
	public int convertClickedYImage( int y) {
		// TODO Auto-generated method stub
		return (int) getUsedConverter().unTransformY(y);
	}

	/**
	returns the cordinateconverter that 
	 */
	public CordinateConverter<?> getUsedConverter() {
		return imp.getConverter();
	}
	
	@Override
	public int getClickedXScreen() {
		// TODO Auto-generated method stub
		return e.getX();
	}

	@Override
	public int getClickedYScreen() {
		// TODO Auto-generated method stub
		return e.getY();
	}

	@Override
	public DisplayedImage getAsDisplay() {
		// TODO Auto-generated method stub
		return imp;
	}
	
	@Override
	public int getClickedChannel() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getClickedFrame() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getClickedSlice() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int mouseButton() {
		return e.getButton();
	}
	
	
	public int clickCount() {
		// TODO Auto-generated method stub
		return e.getClickCount();
	}

	@Override
	public boolean altKeyDown() {
		if (e.isAltDown()) return true;
		return KeyDownTracker.isKeyDown(KeyEvent.VK_ALT);
	}
	
	public boolean shfitDown() {
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
		// TODO Auto-generated method stub
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
	public LayerSelector getSelectionSystem() {
		return new SelectedSetLayerSelector(imp.getImageAsWrapper());
		
	}

	@Override
	public void addUndo(AbstractUndoableEdit... e) {
		
		if(e!=null &&imp!=null &&imp.getUndoManager()!=null) {
			imp.getUndoManager().addEdits(e);
		}
	}
}
