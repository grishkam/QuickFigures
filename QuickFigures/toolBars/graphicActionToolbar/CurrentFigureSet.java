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
package graphicActionToolbar;

import java.util.ArrayList;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import applicationAdapters.DisplayedImage;
import graphicalObjects.FigureDisplayContainer;
import imageMenu.CanvasAutoResize;
import undo.CanvasResizeUndo;

/**An implementation of the current set informer interface.
  this class stores which figure window is the currently active one
  the active one will change depending on which figure the user is working with
  */
public class CurrentFigureSet implements CurrentSetInformer {
	public static String workingDirectory="";
	private static DisplayedImage  currentActiveDisplayGroup=null;
	private static ArrayList<DisplayedImage  > allVisible=new ArrayList<DisplayedImage  >();
	
	private static FigureDisplayContainer activeGraphicDisplay;
	
	public CurrentFigureSet() {}
	
	@Override
	public FigureDisplayContainer getCurrentlyActiveOne() {
		if (currentActiveDisplayGroup!=null) return currentActiveDisplayGroup.getImageAsWrapper();
		
		return activeGraphicDisplay;
	}
	
	public void updateDisplayCurrent() {
		if (getCurrentlyActiveOne() !=null) {
			
			getCurrentlyActiveOne().updateDisplay();
			
		}
	}
	public static void updateActiveDisplayGroup() {
		if (currentActiveDisplayGroup!=null)
			currentActiveDisplayGroup.updateDisplay();
	}

	public static DisplayedImage  getCurrentActiveDisplayGroup() {
		return currentActiveDisplayGroup;
	}
	
	

	/**Sets the stored figure display. takes an instance of DisplayedImage as  */
	public static void setCurrentActiveDisplayGroup(
			DisplayedImage  currentActiveDisplayGroup) {
		CurrentFigureSet.currentActiveDisplayGroup = currentActiveDisplayGroup;
		activeGraphicDisplay=currentActiveDisplayGroup.getImageAsWrapper();
		
	}

	/**Sets the stored figure display. takes an instance of GraphicSetDisplayContainer as an argumant*/
	public void setActiveGraphicDisplay(FigureDisplayContainer activeGraphicDisplay) {
		CurrentFigureSet.activeGraphicDisplay = activeGraphicDisplay;
	}

	/**returns the visible image displays*/
	@Override
	public DisplayedImage getCurrentlyActiveDisplay() {
		return currentActiveDisplayGroup;
	}

	@Override
	public ArrayList<DisplayedImage> getVisibleDisplays() {
		return allVisible;
	}
	
	/**called with a new figure window appears*/
	public static void onApperance(DisplayedImage diw) {
		allVisible.add(diw);
		
	}
	/**called when a figure is closed*/
	public static void onDisapperance(DisplayedImage diw) {
		allVisible.remove(diw);
		
	}
	
	/**performs an automated resize of the current canvas*/
	public static void canvasResize() {
		new CanvasAutoResize().performActionDisplayedImageWrapper(CurrentFigureSet.getCurrentActiveDisplayGroup());
	}
	/**performs an automated resize of the current canvas and returns an undoable action*/
	public static CanvasResizeUndo canvasResizeUndoable() {
		return new CanvasAutoResize().performUndoableAction(CurrentFigureSet.getCurrentActiveDisplayGroup());
	}

	/**Adds an undo the undo manager if an undo manager is present*/
	@Override
	public void addUndo(UndoableEdit e) {
		try {
			getCurrentActiveDisplayGroup().getUndoManager().addEdit(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
	
	public UndoManager getUndoManager() {
		if (getCurrentActiveDisplayGroup()==null) return null;
		return getCurrentActiveDisplayGroup().getUndoManager();
	}

}