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
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import standardDialog.graphics.GraphicObjectDisplayBasic;

/**A class for rendering my own design for an indo/redo icon*/
public class ArrowArcIcon extends GraphicGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	boolean singleHead=true;
	boolean arrowIncluded=true;
	boolean reverse=false;
	private RectangularGraphic spacefilled;

	
	ArrayList<ShapeGraphic> iconParts=new ArrayList<ShapeGraphic>();
	
	public  ArrowArcIcon(boolean direction) {
		reverse=direction;
		createItems() ;
		
		addItems();
	}
	
	
	
	/**creates the graphical objects that will compose the shapes in the icon */
	public void createItems() {
		PathGraphic r_large = createUsedPath();
		
		
		iconParts.add(r_large);
		
		
		
	}



	/**
	 * @return
	 */
	public PathGraphic createUsedPath() {
		spacefilled = new CircularGraphic(getDisplayRect(), CircularGraphic.PI_ARC);
		spacefilled.setRectangle(getDisplayRect());
		spacefilled.setAntialize(true);
		spacefilled.setLocation(0, 0);
		spacefilled.setStrokeWidth((float) 1.5);
		spacefilled.setAntialize(true);
		spacefilled.setStrokeJoin("round");
		
		
		PathGraphic r_large = createPathFromArc(spacefilled.copy());
		
		
		
		r_large.setStrokeColor(iconColor);
		return r_large;
	}



	/**
	 * @param copy
	 * @return
	 */
	private PathGraphic createPathFromArc(RectangularGraphic copy) {
		if(reverse)
			copy.setAngle(-Math.PI/2*0.75);
		
		PathGraphic output = copy.createPathCopy();
		output.setClosedShape(false);
		int npoint=output.getPoints().size()-1;
		output.getPoints().remove(npoint);
		
		output.addArrowHeads(1);
		if(reverse)
			output.flipArrowHeads();
		output.updatePathFromPoints();
		output.setAntialize(true);
		
		return output;
	}



	
	/**returns the shape of Rectangle that is the basis for the shape*/
	private Rectangle getDisplayRect() {
			return new Rectangle(0,0,15,15);
		
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

	/**creates and icon for the arrow. Intended this to become a group of items but that for whatever reason created a group of 0 bounds and strange icon size
	 * @return
	 */
	public static Icon createIcon(boolean direction) {
		return new GraphicObjectDisplayBasic<PathGraphic>(new 	ArrowArcIcon(direction).createUsedPath());
	}

	
}