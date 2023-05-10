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
 * Date Modified: April 17, 2022
 * Version: 2023.2
 */
package handles;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import locatedObject.LocatedObject2D;

/**A list of smart handles that has a few methods particularly useful for working with handles*/
public class SmartHandleList extends ArrayList<SmartHandle> implements ZoomableGraphic{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int NO_HANDLE = -1;
	
	public static final int OVERRIDE_DRAG_HANDLE=409367199;
	public SmartHandle nullHandle=null;
	
	/**creates a new smart handle list*/
	public static  SmartHandleList createList(SmartHandle... handles) {
		SmartHandleList out = new SmartHandleList();
		for(SmartHandle h: handles) {
			if(h!=null) out.add(h);
		}
		
		return out;
	}
	
	/**creates a new smart handle list*/
	public static  SmartHandleList combindLists(SmartHandleList... handles) {
		SmartHandleList out = new SmartHandleList();
		for(SmartHandleList h: handles) {
			if(h!=null) out.addAll(h);
		}
		
		return out;
	}

	
	
	/**Draws the handles, except for the hidden ones*/
	public void draw(Graphics2D g, CordinateConverter cords) {
		for(SmartHandle sh:this) {
			if(sh==null)
				continue;
			if (sh.isHidden()) continue;
			sh.draw(g, cords);
		}
	}
	
	/**Adds handles to the list*/
	public void addEach(SmartHandle...handles ) {
		for(SmartHandle h: handles) {add(h);}
	}
	
	/**returns the handle that contains the click location
	  The point given must be in raw coordinates
	  and not the coordinate location on the worksheet*/
	public SmartHandle getHandleForClickPoint(Point2D p) {
		for(SmartHandle sh:this) {
			if (sh==null||sh.lastDrawShape==null||sh.isHidden()) continue;
			if (sh.containsClickPoint(p)) return sh;
			
		}
		return nullHandle;
		
	}
	
	/**Returns the handle with the specified ID number*/
	public SmartHandle getHandleNumber(int id) {
		for(SmartHandle sh:this) {
			if(sh==null)
				continue;
			if (sh.getHandleNumber()==id&&!sh.isHidden()) return sh;
		}
		return null;
	}
	
	/**Returns the handle at a given point*/
	public int handleNumberForClickPoint(double x, double y) {
		
		SmartHandle hh = getHandleForClickPoint(new Point2D.Double(x, y));
		if (hh!=null) return hh.getHandleNumber(); else
		return NO_HANDLE;
	}

	/**returns a number if the list contained an attachment position handle*/
	public int containsAttachMentPositionHandle(LocatedObject2D o) {
		for(int i=0; i<this.size();i++) {
			SmartHandle l = this.get(i);
			if (l instanceof AttachmentPositionHandle) {
				AttachmentPositionHandle lih=(AttachmentPositionHandle) l;
				if (lih.getObject()==o) {
					
					return i;
					}
			}
		}
		return NO_HANDLE;
	}
	
	/**returns the attachment position handle that controls the location of the given item
	 * @see AttachmentPositionHandle*/
	public AttachmentPositionHandle getAttachmentPositionHandle(LocatedObject2D o) {
		for(int i=0; i<this.size();i++) {
			SmartHandle l = this.get(i);
			if (l instanceof AttachmentPositionHandle) {
				AttachmentPositionHandle lih=(AttachmentPositionHandle) l;
				if (lih.getObject()==o) {
					
					return lih;
					}
			}
		}
		return null;
	}



	
	/**Removes a locked item handle that is meant for object l*/
	public void removeLockedItemHandle(LocatedObject2D l) {
		int index=containsAttachMentPositionHandle(l);
		if (index>=0)remove(index);
	}
	
	/**returns a list of all the handle id numbers*/
	public ArrayList<Integer> getAllHandleNumbers() {
		ArrayList<Integer> output=new ArrayList<Integer>();
		for(SmartHandle sh:this) {
			if(sh==null) continue;
			output.add(sh.getHandleNumber());
		}
		
		return output;
	}
	
	/**the parent layer is not important for the function of a smart handle list*/
	private transient GraphicLayer layer;
	
	@Override
	public GraphicLayer getParentLayer() {
		return layer;
	}

	@Override
	public void setParentLayer(GraphicLayer parent) {
		layer=parent;
		
	}

	/**
	 * @return 
	 * 
	 */
	public SmartHandle getOverrideHandle() {
		for(SmartHandle sh: this) {
			if(sh==null)
				continue;
			if(sh.getHandleNumber()==OVERRIDE_DRAG_HANDLE)
				return sh;
		}
		return null;
		
	}
}
