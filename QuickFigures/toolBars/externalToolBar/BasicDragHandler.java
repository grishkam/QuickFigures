package externalToolBar;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.geom.Point2D;

import imageDisplayApp.ImageAndDisplaySet;

/**Barebones implementation of drag and drop handler
  does nothing interesting but subclasses perform other actions*/
public class BasicDragHandler implements DragAndDropHandler {

	protected Point2D position;
	
	void setPosition(ImageAndDisplaySet displaySet, Point2D arg0) {
		position=displaySet.getConverter().unTransformP(arg0);
	}
	
	@Override
	public void drop(ImageAndDisplaySet displaySet, DropTargetDropEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
	}

	@Override
	public void dropActChange(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
	}

	@Override
	public void dragOver(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
		
	}

	@Override
	public void dragExit(ImageAndDisplaySet displaySet, DropTargetEvent arg0) {
	
		
	}

	@Override
	public void dragEnter(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
		
	}

}
