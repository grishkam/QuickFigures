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
package handles;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import utilityClassesForObjects.LocatedObject2D;

/**A list of smart handles that has a few methods particularly useful for working with handles*/
public class SmartHandleList extends ArrayList<SmartHandle> implements ZoomableGraphic{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int NO_HANDLE = -1;
	
	public static  SmartHandleList createList(SmartHandle... handles) {
		SmartHandleList out = new SmartHandleList();
		for(SmartHandle h: handles) {
			if(h!=null) out.add(h);
		}
		
		return out;
	}
	
	public static  SmartHandleList combindLists(SmartHandleList... handles) {
		SmartHandleList out = new SmartHandleList();
		for(SmartHandleList h: handles) {
			if(h!=null) out.addAll(h);
		}
		
		return out;
	}
	

	public void draw(Graphics2D g, CordinateConverter<?> cords) {
		for(SmartHandle sh:this) {
			if (sh.isHidden()) continue;
			sh.draw(g, cords);
		}
	}
	
	public void addEach(SmartHandle...handles ) {
		for(SmartHandle h: handles) {add(h);}
	}
	
	
	public SmartHandle getHandleForClickPoint(Point2D p) {
		for(SmartHandle sh:this) {
			if (sh==null||sh.lastDrawShape==null||sh.isHidden()) continue;
			if (sh.containsClickPoint(p)) return sh;
			
		}
		return null;
		
	}
	
	/**Returns the handle with the specified ID number*/
	public SmartHandle getHandleNumber(int id) {
		for(SmartHandle sh:this) {
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


	public int containsLockedItemHandle(LocatedObject2D o) {
		for(int i=0; i<this.size();i++) {
			SmartHandle l = this.get(i);
			if (l instanceof LockedItemHandle) {
				LockedItemHandle lih=(LockedItemHandle) l;
				if (lih.getObject()==o) {
					
					return i;
					}
			}
		}
		return -1;
	}
	
	public LockedItemHandle getLockedItemHandle(LocatedObject2D o) {
		for(int i=0; i<this.size();i++) {
			SmartHandle l = this.get(i);
			if (l instanceof LockedItemHandle) {
				LockedItemHandle lih=(LockedItemHandle) l;
				if (lih.getObject()==o) {
					
					return lih;
					}
			}
		}
		return null;
	}


	private transient GraphicLayer layer;
	@Override
	public GraphicLayer getParentLayer() {
		return layer;
	}

	@Override
	public void setParentLayer(GraphicLayer parent) {
		layer=parent;
		
	}
	
	/**Removes a locked item handle that is meant for object l*/
	public void removeLockedItemHandle(LocatedObject2D l) {
		int index=containsLockedItemHandle(l);
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
}
