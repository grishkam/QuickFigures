/**
 * Author: Greg Mazo
 * Date Modified: Apr 17, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package graphicalObjects_BandMarkers;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import handles.AttachmentPositionHandle;
import locatedObject.ObjectContainer;
import locatedObject.RectangleEdges;

/**
 
 * 
 */
public class BandDragOverride extends AttachmentPositionHandle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrowGraphic arrow;
	
	
	/**
	 * @param theArrow
	 */
	public BandDragOverride(ArrowGraphic theArrow) {
		this.arrow=theArrow;
	}


	/**What to do when a handle is moved from point p1 to p2*/
	@Override
	public void handleDrag(CanvasMouseEvent mEvent) {
		
			arrow.setYLocation(mEvent.getCoordinateY());
			
			arrow.notifyListenersOfMoveMent();
		
	}
	
	/**returns true if the attached item has been removed*/
	public boolean absent() {
		
		return false;
	}
	
	/**If the locked item is either hidden or not in the image anymore, will hide the handle*/
	@Override
	public boolean isHidden() {
		return false;
	}

	

	public void updateLocation() {
		
	}
}
