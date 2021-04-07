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
package undo;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import locatedObject.LocatedObject2D;

public class CanvasResizeUndo extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DisplayedImage image;
	private Dimension2D oldDims;
	private Dimension2D newDims;
	private ArrayList<LocatedObject2D> items;
	private UndoMoveItems itemMovments;

	public CanvasResizeUndo(DisplayedImage diw) {
		this.image=diw;
		oldDims=(Dimension2D) image.getImageAsWorksheet().getCanvasDims().clone();
		items=image.getImageAsWorksheet().getLocatedObjects();
		itemMovments=new UndoMoveItems(items);//in the event that objects are moved in the canvas resize undo, will undo those movements
	}
	
	public void undo() {
		image.getImageAsWorksheet().worksheetResize( (int)oldDims.getWidth(), (int)oldDims.getHeight(), 0,0);
		itemMovments.undo();
		image.updateDisplay();
		image.updateWindowSize();
	}
	
	public void redo() {
		image.getImageAsWorksheet().worksheetResize( (int)newDims.getWidth(), (int)newDims.getHeight(), 0,0);
		itemMovments.redo();
		image.updateDisplay();
		image.updateWindowSize();
	}
	
	public void establishFinalState() {
		newDims=(Dimension2D) image.getImageAsWorksheet().getCanvasDims().clone();
		itemMovments.establishFinalLocations();
	}
	
	public boolean sizeSame() {return oldDims.equals(newDims);}

}
