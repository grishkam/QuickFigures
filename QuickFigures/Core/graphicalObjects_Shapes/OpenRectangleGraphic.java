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
 * Date Modified: Dec 6, 2021
 * Version: 2022.1
 */
package graphicalObjects_Shapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import locatedObject.RectangleEdges;

/**a modified rectangle class. might replace with */
public class OpenRectangleGraphic extends RectangularGraphic {
	

	{name="Open Rectangle";}
	/**
	 * 
	 */
	
	
	
	public  OpenRectangleGraphic(Rectangle rectangle) {
		super(rectangle);
		this.setClosedShape(false);
	}
	
	
	public OpenRectangleGraphic(RectangularGraphic r) {
		super(r);
		this.setClosedShape(false);
	}
	
	
	private static final long serialVersionUID = 1L;
	
	
	public RectangularGraphic blankShape(Rectangle r, Color c) {
		OpenRectangleGraphic r1 = new OpenRectangleGraphic(r);
		
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	public String getShapeName() {return "Open Rectangle";}

	public OpenRectangleGraphic copy() {
		OpenRectangleGraphic output = new OpenRectangleGraphic(this);
		return output;
	}
	
	

	/**implements a formula to create a rectangle that is missing one side*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		Rectangle2D r = this.getRectangle();
		

		Point2D startPoint = RectangleEdges.getLocation(LOWER_LEFT, r);
		path.moveTo( startPoint .getX(),startPoint .getY());
		
		Point2D p2 = RectangleEdges.getLocation(UPPER_LEFT, r);
		path.lineTo(p2.getX(), p2.getY());
		
		Point2D p3 = RectangleEdges.getLocation(UPPER_RIGHT, r);
		path.lineTo(p3.getX(), p3.getY());
		
		
		Point2D p4 = RectangleEdges.getLocation(LOWER_RIGHT, r);
		path.lineTo(p4.getX(), p4.getY());
	
		
		this.setClosedShape(false);
		
		return path;
		
	}


	/**overrides the superclass*/
	@Override
	RectangularGraphic shapeUsedForIcon() {
		return  blankShape(new Rectangle(0,0,12,10), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
	}
		
	/**returns a pathGraphic that looks just like this shape
	 * @see PathGraphic*/
	public PathGraphic createPathCopy() { 
		PathGraphic out = super.createPathCopy();
		out.setClosedShape(false);
		return out;
	}
	
	
	/**Creates the shape in adobe illustrator*/
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi, false);
	}
	
	public boolean isDrawClosePoint() {
		return false;
	}
	
	
	}
	
	
	
	
	

