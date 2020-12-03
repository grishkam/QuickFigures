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
package imageMenu;

import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import appContext.ImageDPIHandler;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import basicMenusForApp.MenuItemForObj;
import genericMontageKit.BasicObjectListHandler;
import undo.CanvasResizeUndo;

public class CanvasAutoResize implements MenuItemForObj {

	public static int fitAll=0, page=1, slide=2;
	int mode=fitAll;
	
	
	public CanvasAutoResize() {}
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
		CanvasResizeUndo undo = new CanvasResizeUndo(diw);//creates an undo
		ImageWrapper iw = diw.getImageAsWrapper();
		BasicObjectListHandler boh = new BasicObjectListHandler();
		
		if (mode==fitAll)
			{
			boolean requiresWindowSizeChange = boh.resizeCanvasToFitAllObjects(iw);
			if(!requiresWindowSizeChange) return undo;
			}
		if (mode!=fitAll) {
			Rectangle2D.Double r=new Rectangle2D.Double(0,0, ImageDPIHandler.getStandardDPI()*8.5, 490);
			
			if (mode==slide) {
				r=new Rectangle2D.Double(0,0,ImageDPIHandler.getStandardDPI()*10, ImageDPIHandler.getStandardDPI()*7.5);
				}
			iw.CanvasResize( (int)r.width, (int)r.height, 0,0);
		}
		
		
		diw.updateDisplay();
		diw.updateWindowSize();
		
		undo.establishFinalState();
		return undo;
	}
	

	
	public void makeAllVisible(DisplayedImage diw) {
		performActionDisplayedImageWrapper(diw);
		diw.zoomOutToDisplayEntireCanvas();
	}

	@Override
	public String getCommand() {
		String output= "Canvas Resize";
		if (mode>fitAll) output+=mode;
		return output;
	}

	@Override
	public String getNameText() {
		if (mode==1) return "Make Page Size";
		if (mode==2) return "Make PowerPoint Slide Size";
		return "Expand Canvas To Fit All Objects";
	}

	@Override
	public String getMenuPath() {
		// TODO Auto-generated method stub
		return "Image<Canvas";
	}
	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
	




}
