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
 * Date Modified: Jan 5, 2021
 * Version: 2022.0
 */
package undo;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects_Shapes.RectangularGraphic;
import handles.HasHandles;


/**An undoable edit for a handle drag. this simply drags the handle back to its original location.
  for object that properly implement the handle press and handle move methods
  this should work perfectly. */
public class UndoDragHandle extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Rectangle2D rect;
	private Point2D original;
	private HasHandles item;
	private int handlenum;
	private Point2D finalLoc;
	
	public UndoDragHandle(int handleNum, HasHandles item, Point2D originalLocation) {
		if(item instanceof RectangularGraphic) {
			this.rect=(Rectangle2D) (( RectangularGraphic)item).getShape().getBounds();
		}
		this.original=originalLocation;
		this.item=item;
		this.handlenum=handleNum;
		
	}
	
	public void setFinalLocation(Point2D finalLoc) {
		this.finalLoc=finalLoc;
	}
	
	public void undo() {
		Point p=new Point((int)original.getX(),(int) original.getY());
		item.handlePress(handlenum, p);
		item.handleMove(handlenum, p,p);
		
		if(item instanceof RectangularGraphic) {
			if(item instanceof RectangularGraphic) {
				(( RectangularGraphic)item).setRectangle(rect);;
			}
	}

}
	
	
	public void redo() {
		Point p=new Point((int)finalLoc.getX(),(int) finalLoc.getY());
		item.handlePress(handlenum, p);
		item.handleMove(handlenum, p,p);
		
		if(item instanceof RectangularGraphic) {
			if(item instanceof RectangularGraphic) {
				(( RectangularGraphic)item).setRectangle(rect);;
			}
	}

}
	
}
