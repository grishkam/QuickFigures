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
 * Date Modified: Mar 28, 2021
 * Version: 2023.1
 */
package imageMenu;

import java.awt.geom.Rectangle2D;

import appContext.ImageDPIHandler;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWorkSheet;
import basicMenusForApp.BasicMenuItemForObj;
import layout.BasicObjectListHandler;
import undo.CanvasResizeUndo;
import utilityClasses1.SizeConstants;

/**Performs an automated resize of a worksheet*/
public class CanvasAutoResize extends BasicMenuItemForObj {

	public static final int RESIZE_TO_FIT_ALL_OBJECTS=0, PAGE_SIZE=1, SLIDE_SIZE=2;
	int mode=RESIZE_TO_FIT_ALL_OBJECTS;
	
	/**set to true if this autoresize can not be blocked*/
	boolean mandatory=true;
	
	/**Creates a new canvas auto resizer. 
	 * @param mandatory set to true if the canvas object is not allowed to block this resize*/
	public CanvasAutoResize(boolean mandatory) {this.mandatory=mandatory;}
	public CanvasAutoResize(int mode) {
		this.mode=mode;
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if(diw==null) return;
		CanvasResizeUndo undo = performUndoableAction(diw);
		if(undo==null||undo.sizeSame()) return;//if no changes were made
		diw.getUndoManager().addEdit(undo);//adds the undo
	}
	public CanvasResizeUndo performUndoableAction(DisplayedImage diw) {
		if (!mandatory&&!diw.getImageAsWorksheet().allowAutoResize()) {return null;}
		CanvasResizeUndo undo = new CanvasResizeUndo(diw);//creates an undo
		ImageWorkSheet iw = diw.getImageAsWorksheet();
		BasicObjectListHandler boh = new BasicObjectListHandler();
		
		if (mode==RESIZE_TO_FIT_ALL_OBJECTS)
			{
			boolean requiresWindowSizeChange = boh.resizeCanvasToFitAllObjects(iw);
			if(!requiresWindowSizeChange) return undo;
			}
		if (mode!=RESIZE_TO_FIT_ALL_OBJECTS) {
			Rectangle2D.Double r=new Rectangle2D.Double(0,0, ImageDPIHandler.getInchDefinition()*8.5, 490);
			
			if (mode==SLIDE_SIZE) {
				r=new Rectangle2D.Double(0,0, SizeConstants.SLIDE_SIZE.width, SizeConstants.SLIDE_SIZE.height);
				}
			iw.worksheetResize( (int)r.width, (int)r.height, 0,0);
		}
		
		
		diw.updateDisplay();
		diw.updateWindowSize();
		
		undo.establishFinalState();
		return undo;
	}
	

	
	public CanvasResizeUndo makeAllVisible(DisplayedImage diw) {
		CanvasResizeUndo undo = performUndoableAction(diw);
		diw.zoomOutToDisplayEntireCanvas();
		return undo;
	}

	@Override
	public String getCommand() {
		String output= "Canvas Resize";
		if (mode>RESIZE_TO_FIT_ALL_OBJECTS) output+=mode;
		return output;
	}

	@Override
	public String getNameText() {
		if (mode==PAGE_SIZE) return "Make Page Size";
		if (mode==SLIDE_SIZE) return "Make PowerPoint Slide Size";
		return "Expand Canvas To Fit All Objects";
	}

	@Override
	public String getMenuPath() {
		return "Edit<Canvas";
	}
	
	




}
