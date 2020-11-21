package externalToolBar;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.geom.Point2D;

import imageDisplayApp.ImageWindowAndDisplaySet;

/**Barebones implementation of drag and drop handler
  does nothing interesting but subclasses perform other actions*/
public class BasicDragHandler implements DragAndDropHandler {

	protected Point2D position;
	
	void setPosition(ImageWindowAndDisplaySet displaySet, Point2D arg0) {
		position=displaySet.getConverter().unTransformP(arg0);
	}
	
	@Override
	public void drop(ImageWindowAndDisplaySet displaySet, DropTargetDropEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
	}

	@Override
	public void dropActChange(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
	}

	@Override
	public void dragOver(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
		
	}

	@Override
	public void dragExit(ImageWindowAndDisplaySet displaySet, DropTargetEvent arg0) {
	
		
	}

	@Override
	public void dragEnter(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
		
	}

}
