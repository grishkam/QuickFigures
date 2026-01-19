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
 * Date Created: Jan 19, 2026
 * Date Modified: Jan 19, 2026
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
import graphicalObjects_Shapes.RoundedRectangleGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;
import standardDialog.graphics.GraphicObjectDisplayBasic;

/**A class for rendering my own design for save icon*/
public class SaveIcon extends GraphicGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	private Color iconColor2=Color.gray.darker();
	private Color iconColor3=Color.lightGray;


	
	ArrayList<ShapeGraphic> iconParts=new ArrayList<ShapeGraphic>();
	
	public  SaveIcon() {
	
		createItems() ;
		
		addItems();
	}
	
	
	
	/**creates the graphical objects that will compose the shapes in the icon */
	public void createItems() {
		NotchedRectangleGraphic spacefilled = new NotchedRectangleGraphic(getDisplayRect(), new boolean [] {false, true, false, false});
		
		spacefilled.setRectangle(getDisplayRect());
		spacefilled.setAntialize(true);
		spacefilled.setLocation(0, 2);
		spacefilled.setAntialize(true);
		spacefilled.setStrokeJoin("round");
		
		NotchedRectangleGraphic r_large = spacefilled.copy();
		
		r_large.setNotchRatios(0.15);
		r_large.setStrokeJoin("round");
		
		RectangularGraphic r_slider = new RectangularGraphic(spacefilled.copy());
		
		r_slider.moveLocation(2, 0);
		r_slider.setWidth(r_large.getObjectWidth()/2);
		r_slider.setHeight(r_large.getObjectHeight()/3);
		
		RoundedRectangleGraphic r_label = new RoundedRectangleGraphic(spacefilled.copy());
		
		r_label.moveLocation(2, r_large.getObjectHeight()/2);
		r_label.setWidth(r_large.getObjectWidth()*0.75);
		r_label.setHeight(r_large.getObjectHeight()/2);
		
		
		r_slider.setStrokeColor(iconColor);
		r_slider.setFillColor(iconColor3);
		
		r_large.setStrokeColor(iconColor);
		r_large.setFillColor(iconColor2);
		r_label.setStrokeColor(iconColor);
		r_label.setArcw(1);
		r_label.setArch(1);
		r_label.setFillColor(iconColor3);
		
		iconParts.add(r_large);
		iconParts.add(r_slider);
		iconParts.add(r_label);
		
		
		
	}



	
	
	
	/**returns the shape of Rectangle 1. this will depend on the circumstances*/
	private Rectangle getDisplayRect() {
			return new Rectangle(0,0,16,16);
		
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
		return new GraphicObjectDisplayBasic<SaveIcon>(new 	SaveIcon());
	}

	
}