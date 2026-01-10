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
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;
import standardDialog.graphics.GraphicObjectDisplayBasic;

/**A class for rendering my own design for scale icon*/
public class ScaleSizeIcon extends GraphicGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	private Color iconColor2=Color.gray;
	boolean singleHead=true;
	boolean arrowIncluded=true;
	private RectangularGraphic spacefilled;

	
	ArrayList<ShapeGraphic> iconParts=new ArrayList<ShapeGraphic>();
	
	public  ScaleSizeIcon() {
	
		createItems() ;
		
		addItems();
	}
	
	
	
	/**creates the graphical objects that will compose the shapes in the icon */
	public void createItems() {
		spacefilled = new RectangularGraphic();
		spacefilled.setRectangle(getR1rect());
		spacefilled.setAntialize(true);
		
		RectangularGraphic r_small = spacefilled.copy();
		RectangularGraphic r_large = spacefilled.copy();
		
		double ratio=0.5;
		
		r_small.moveLocation(0, r_small.getObjectHeight()*(1-ratio));
		
		r_small.setWidth(r_small.getObjectWidth()*ratio);
		r_small.setHeight(r_small.getObjectHeight()*ratio);
		
		r_small.setStrokeColor(iconColor);
		r_large.setStrokeColor(iconColor2);
		r_large.setLocationType(RectangleEdges.UPPER_RIGHT);
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
		
		iconParts.add(r_small);
		iconParts.add(r_large);
		
		if(arrowIncluded)
			iconParts.add(a);
		
		
	}
	
	
	/**returns the shape of Rectangle 1. this will depend on the circumstances*/
	private Rectangle getR1rect() {
			return new Rectangle(0,0,18,14);
		
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
		return new GraphicObjectDisplayBasic<ScaleSizeIcon>(new 	ScaleSizeIcon());
	}

	
}