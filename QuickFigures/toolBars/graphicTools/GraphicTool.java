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
package graphicTools;

import applicationAdapters.ImageWrapper;
import genericTools.Object_Mover;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import undo.UndoAddItem;
import utilityClassesForObjects.LocatedObject2D;

/**A tool for adding and manipulating shapes*/
public class GraphicTool extends Object_Mover {

	{createSelector=false;}
	
	/**If set to true, will switch to the default tool when 
	  the mouse movement is done */
	protected boolean temporaryTool=false;

	
	@Override 
	public void mouseEntered() {
		this.getImageClicked().getTopLevelLayer();
	
	}
	

	
	public void mousePressed() {
		
		super.mousePressed();
	
		try{
		
			{
			
			onPress(getImageClicked(), super.getPrimarySelectedObject()) ;

			
		}
		


		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}

	@Override
	public void mouseReleased() {
		super.mouseReleased();
		onRelease(this.getImageClicked(),getPrimarySelectedObject());
		if (temporaryTool) {
			super.todefaultTool();}
	}
	
	public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
		
	}
	
	public void onRelease(ImageWrapper imageWrapper, LocatedObject2D roi2) {
		
	}
	
	public void addUndoerForAddItem(ImageWrapper gmp, GraphicLayer layer, ZoomableGraphic bg) {
		gmp.getImageDisplay().getUndoManager().addEdit(new UndoAddItem(layer, bg));
	}
	

	public String getToolName() {
		return "Graphic Tool";
	}
	

}
