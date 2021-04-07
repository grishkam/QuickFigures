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
package imageMenu;

import java.awt.Rectangle;
import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWorkSheet;
import basicMenusForApp.BasicMenuItemForObj;
import layout.BasicObjectListHandler;
import locatedObject.LocatedObject2D;
import undo.CanvasResizeUndo;

/**A menu bar item that resizes the canvas to precisely the size at which the objects fit.*/
public class CanvasAutoTrim extends BasicMenuItemForObj{

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if(diw==null) return;
		CanvasResizeUndo undo = new CanvasResizeUndo(diw);//creates an undo
		
		trimCanvas(diw);
		undo.establishFinalState();
		if(!undo.sizeSame())
			diw.getUndoManager().addEdit(undo);
	}

	public void trimCanvas(DisplayedImage diw) {
		ImageWorkSheet iw = diw.getImageAsWorksheet();
		BasicObjectListHandler boh = new BasicObjectListHandler();
		
		ArrayList<LocatedObject2D> list = iw.getLocatedObjects();
		Rectangle bound=null;
		for(LocatedObject2D l:list) {
			if (l==null) continue;
			if (bound==null) bound=l.getBounds(); 
				else  {
					Rectangle b2 = l.getBounds();
					bound=bound.createUnion(b2).getBounds();
				}
		}
		
		boh.CanvasResizeObjectsIncluded(iw, bound.width, bound.height, -bound.x, -bound.y);
		
		
		diw.updateDisplay();
		diw.updateWindowSize();
	}

	@Override
	public String getCommand() {
		return "Canvas Trim";
	}

	@Override
	public String getNameText() {
		return "Trim Canvas To Fit Obects";
	}

	@Override
	public String getMenuPath() {
		return "Edit<Canvas";
	}
	




}
