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
package layout.plasticPanels;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import utilityClassesForObjects.LocatedObject2D;


public class ObjectListPanelLayout extends BasicSpacedPanelLayout {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private ArrayList<LocatedObject2D> array=new ArrayList<LocatedObject2D>();

private transient HashMap<LocatedObject2D, Rectangle> ObjectToRectMap=new HashMap<LocatedObject2D, Rectangle> ();



public void addObject(LocatedObject2D add) {
	array.add(add);
}

public void removeObject(LocatedObject2D add) {
	array.remove(add);
	this.getMap().remove(add);
}

@Override
public Rectangle getPanel(int index) {
	if (index<1) return null;
	if (index>array.size()) return null;
	LocatedObject2D object = array.get(index-1);
	return getRectForObject(object);
}

/**Recturns the rectangular panel for the object,
   returns the same object of class Rectangle every
   time but with different dimensions depending 
   on the bounds of the located object*/
Rectangle getRectForObject(LocatedObject2D object) {
	Rectangle output = object.getBounds();
	Rectangle oldrect = getMap().get(object);
	if (oldrect!=null) {
		oldrect.setRect(output);
		return oldrect;
	}
	getMap().put(object, output);
	
	return output;
}

@Override
public int getNearestPanelIndex(double d, double e) {
	Rectangle2D ret = this.getNearestPanel(d, e);
	for(LocatedObject2D loc: array) {
		if (loc==null) continue;
		if (loc.getBounds().equals(ret)) return array.indexOf(loc)+1;
	}
	// TODO Auto-generated method stub
	return 1;
}

@Override
public void move(double x, double y) {
	for(LocatedObject2D loc: array) {
		if (loc==null) continue;
		loc.moveLocation((int)x, (int)y);
	}
	
}

@Override
public void setPanelWidth(int panel, double width) {
	// TODO Auto-generated method stub
	
}

@Override
public void setPanelHeight(int panel, double height) {
	// TODO Auto-generated method stub
	
}

@Override
public void resetPtsPanels() {
	// TODO Auto-generated method stub
	
}

@Override
public double getStandardPanelWidth() {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public double getStandardPanelHeight() {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public void setStandardPanelWidth(double width) {
	// TODO Auto-generated method stub
	
}

@Override
public void setStandardPanelHeight(double height) {
	// TODO Auto-generated method stub
	
}

@Override
public boolean doesPanelUseUniqueWidth(int panel) {
	// TODO Auto-generated method stub
	return true;
}

@Override
public boolean doesPanelUseUniqueHeight(int panel) {
	// TODO Auto-generated method stub
	return true;
}

@Override
public void nudgePanel(int panelnum, double dx, double dy) {
	if (panelnum>array.size()) return;
	LocatedObject2D loc = this.getArray().get(panelnum-1);
	if (loc!=null) {loc.moveLocation((int)dx, (int)dy);}
	
}

@Override
public void nudgePanelDimensions(int panelnum, double dx, double dy) {
	
}

@Override
public int nPanels() {
	// TODO Auto-generated method stub
	return getArray().size();
}

public ArrayList<LocatedObject2D> getArray() {
	return array;
}

public void setArray(ArrayList<LocatedObject2D> array) {
	this.array = array;
}

public void autoLocatePanels() {
	Rectangle2D[] pan=getPanels();
	HashMap<Rectangle2D, Rectangle2D> lmap = this.getneighborFinder().getLeftNeighborMap(pan);
	HashMap<Rectangle2D, Rectangle2D> amap = this.getneighborFinder().getUpNeighborMap(pan);
	
	for(LocatedObject2D p:array) {
		if (p==null) continue;
		Rectangle rect = 	getneighborFinder().autoLocatePanel(p.getBounds(), this.getHorizontalBorder(), this.getVerticalBorder(), amap.get(this.getMap().get(p)), lmap.get(this.getMap().get(p)));
		p.setLocationUpperLeft(rect.x, rect.y);
	}
}



protected HashMap<LocatedObject2D, Rectangle> getMap() {
	if (ObjectToRectMap==null) ObjectToRectMap=new HashMap<LocatedObject2D, Rectangle>();
	return ObjectToRectMap;
}



	

}
