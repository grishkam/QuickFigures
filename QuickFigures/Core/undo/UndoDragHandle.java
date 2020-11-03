package undo;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjectHandles.HasHandles;
import graphicalObjects_BasicShapes.RectangularGraphic;


/**An undoable edit for a handle drag. this simply drags the handle back to its original location.
   may not faithfully represent original item*/
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
