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
 * Date Created: Jan 10, 2026
 * Date Modified: Jan 10, 2026
 * Version: 2026.1
 */
package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.NotchedRectangleGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;
import standardDialog.graphics.GraphicObjectDisplayBasic;

/**A class for rendering my own design for duplicate icon*/
public class DuplicateIcon extends GraphicGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	private Color iconColor2=Color.gray;
	boolean singleHead=true;
	boolean arrowIncluded=false;
	private RectangularGraphic spacefilled;

	
	ArrayList<ShapeGraphic> iconParts=new ArrayList<ShapeGraphic>();
	
	public  DuplicateIcon() {
	
		createItems() ;
		
		addItems();
	}
	
	
	
	/**creates the graphical objects that will compose the shapes in the icon */
	public void createItems() {
		spacefilled = new NotchedRectangleGraphic(getDisplayRect());
		spacefilled.setRectangle(getDisplayRect());
		spacefilled.setAntialize(true);
		spacefilled.setLocation(0, 5);
		spacefilled.setStrokeWidth((float) 1.5);
		spacefilled.setAntialize(true);
		spacefilled.setStrokeJoin("round");
		RectangularGraphic r_small = spacefilled.copy();
		RectangularGraphic r_large = spacefilled.copy();
		
		
		
		r_small.moveLocation(7, -5);
		
		
		r_small.setStrokeColor(iconColor);
		r_large.setStrokeColor(iconColor2);
		
		
		
		iconParts.add(r_small);
		iconParts.add(r_large);
		
		if(arrowIncluded)
			iconParts.add(createArrow(r_small, r_large));
		
		
	}



	/**
	 * @param r_small
	 * @param r_large
	 * @return
	 */
	private ArrowGraphic createArrow(RectangularGraphic r_small, RectangularGraphic r_large) {
		Point2D locationH2 = r_large.getLocation();
		locationH2.setLocation(locationH2.getX()-2, locationH2.getY()+2);
		
		
		Point2D locationH1 = r_small.getCenterOfRotation();
		locationH1.setLocation(locationH1.getX()-2, locationH1.getY()+2);
		
		if(singleHead) {
			locationH1 = r_large.getCenterOfRotation();
		}
		
		
		
		ArrowGraphic a = new ArrowGraphic(locationH1, locationH2);
		a.setStrokeColor(Color.red.darker());
		a.setNumerOfHeads(2);
		if(singleHead) {
			a.setNumerOfHeads(1);
		}
		a.setStrokeWidth(1);
		
		a.getHead().setArrowHeadSize(5);
		a.setAntialize(true);
		return a;
	}
	
	
	/**returns the shape of Rectangle 1. this will depend on the circumstances*/
	private Rectangle getDisplayRect() {
			return new Rectangle(0,0,10,14);
		
	}

	
	public void addItems() {
		for(ShapeGraphic d:iconParts) {	getTheInternalLayer().add(d);}
	}



	public Color getIconColor() {
		if (iconColor==null) {
			iconColor=Color.black;
		}
		return iconColor;
	}

	/**
	 * @return
	 */
	public static Icon createIcon() {
		return new GraphicObjectDisplayBasic<DuplicateIcon>(new 	DuplicateIcon());
	}

	
}