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
 * Date Modified: May 8, 2021
 * Version: 2022.0
 */
package locatedObject;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

/**An interface for objects within a 2D drawing*/
public interface LocatedObject2D extends Mortal, Hideable, Selectable {
	
	/**The anchor location for most objects is the upper left*/
	public Point2D getLocationUpperLeft();
	public void setLocationUpperLeft(double x, double y);
	public void setLocationUpperLeft(Point2D p);
	
	
	
	/**result determines if the uer is allowed to move the item by direct click and drag*/
	public int isUserLocked();
	public static final int NOT_LOCKED = 0, LOCKED=1;
	
	/**returns a duplicate. the wrapped object is also duplicated*/
	public LocatedObject2D copy();
	
	/**returns true if some part of the object is inside the rectangle*/
	public boolean doesIntersect(Rectangle2D rect);
	/**returns true if the entrie object is inside the rectangle*/
	public boolean isInside(Rectangle2D rect);
	
	/**some objects will have multiple types of bounds, this should return the 
	   the same as get bounds for most objects*/
	public Rectangle getExtendedBounds();
	
	/**some objects will take drag and drop operations*/
	public Object dropObject(Object ob, int x, int y);
	
	/**Getter and setter methods for the location of the object*/
	public Point2D getLocation();
	public void setLocation(double x, double y);
	public void setLocation( Point2D p);
	
	/**move objects 2D locations by amount i in the x axis and j in the y axis*/
	public void moveLocation(double xmov, double ymov) ;
	
	/**returns the object shape as a polygon if possible*/
	public Shape getOutline();
	
	/**the bounding box of the object*/
	public Rectangle getBounds();
	
	/**the listeners*/
	public void addLocationChangeListener(LocationChangeListener l);
	public void removeLocationChangeListener(LocationChangeListener l);
	public void removeAllLocationChangeListeners();
	public LocationChangeListenerList getListenerList();
	
	/**some objects will need to autolocate relative to a
	  rectangular region, the snapping behavior object describes how
	  @see AttachmentPosition
	  */
	public AttachmentPosition getAttachmentPosition();
	public void setAttachmentPosition(AttachmentPosition snap);
	
	/**the Location of the object may refer to one of the corners of its bounding
	 * box or its center, these methods set which one. they are needed when one wants to 
	 * keep an edge of the boudning box fixed suring a resizing operation
	 * @see RectangleEdgePositions*/
	public void setLocationType(int n) ;
	public int getLocationType() ;
	public HashMap<String, Object> getTagHashMap();
	
}
