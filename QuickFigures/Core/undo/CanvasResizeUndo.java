package undo;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import utilityClassesForObjects.LocatedObject2D;

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
		oldDims=(Dimension2D) image.getImageAsWrapper().getCanvasDims().clone();
		items=image.getImageAsWrapper().getLocatedObjects();
		itemMovments=new UndoMoveItems(items);//in the event that objects are moved in the canvas resize undo, will undo those movements
	}
	
	public void undo() {
		image.getImageAsWrapper().CanvasResize( (int)oldDims.getWidth(), (int)oldDims.getHeight(), 0,0);
		itemMovments.undo();
		image.updateDisplay();
		image.updateWindowSize();
	}
	
	public void redo() {
		image.getImageAsWrapper().CanvasResize( (int)newDims.getWidth(), (int)newDims.getHeight(), 0,0);
		itemMovments.redo();
		image.updateDisplay();
		image.updateWindowSize();
	}
	
	public void establishFinalState() {
		newDims=(Dimension2D) image.getImageAsWrapper().getCanvasDims().clone();
		itemMovments.establishFinalLocations();
	}
	
	public boolean sizeSame() {return oldDims.equals(newDims);}

}
