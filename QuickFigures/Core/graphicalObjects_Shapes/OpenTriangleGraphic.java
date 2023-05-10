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
 * Date Modified: Jan 5, 2021
 * Version: 2023.2
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

public class OpenTriangleGraphic extends TriangleGraphic {
	

	{name="Open Triangle";}
	/**
	 * 
	 */
	
	
	
	public  OpenTriangleGraphic(Rectangle rectangle) {
		super(rectangle);
		this.setClosedShape(false);
	}
	public OpenTriangleGraphic(Rectangle rectangle, double trapezoidTopSize) {
		super(rectangle);
		this.parameter.setRatioToMaxLength(trapezoidTopSize);
		this.setClosedShape(false);
	}
	
	public OpenTriangleGraphic(RectangularGraphic r) {
		super(r);
		this.setClosedShape(false);
	}
	
	
	private static final long serialVersionUID = 1L;
	
	
	public RectangularGraphic blankShape(Rectangle r, Color c) {
		OpenTriangleGraphic r1 = new OpenTriangleGraphic(r, parameter.getRatioToMaxLength());
		r1.parameter.setRatioToMaxLength(this.parameter.getRatioToMaxLength());
		
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	public String getShapeName() {return "Open Triangle";}

	public OpenTriangleGraphic copy() {
		OpenTriangleGraphic output = new OpenTriangleGraphic(this);
		output.parameter.setRatioToMaxLength(parameter.getRatioToMaxLength());
		return output;
	}
	
	

	/**implements a formula to produce a wedge shape*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		Rectangle2D r = this.getRectangle();
		double rx=getObjectWidth()/2;

		Point2D startPoint = RectangleEdges.getLocation(LOWER_LEFT, r);
		path.moveTo( startPoint .getX(),startPoint .getY());
		
		Point2D p2 = RectangleEdges.getLocation(UPPER_LEFT, r);
		path.lineTo(p2.getX()+parameter.getRatioToMaxLength()*rx*2, p2.getY());
		
		
		Point2D p4 = RectangleEdges.getLocation(LOWER_RIGHT, r);
		path.lineTo(p4.getX(), p4.getY());
	
		
		this.setClosedShape(false);
		
		return path;
		
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
	
	
	
	
	

