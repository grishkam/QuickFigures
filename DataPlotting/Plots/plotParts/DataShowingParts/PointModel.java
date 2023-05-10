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
 * Date Modified: Jan 7, 2021
 * Version: 2023.2
 */
package plotParts.DataShowingParts;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Serializable;

import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.CrossGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.RegularPolygonGraphic;
import locatedObject.RectangleEdges;
import plotParts.DataShowingParts.ScatterPoints.PlotPoint;

/**class stores information regarding the shape used to draw points */
public class PointModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  RectangularGraphic modelShape=new  RegularPolygonGraphic(new Rectangle(-2,-2,3,3));
	private int pointType;
	private double nSides;
	
	public int getPointType() {
		return pointType;
	}



	public double getNSides() {
		if (getModelShape() instanceof RegularPolygonGraphic)
		return this.getModelShapeAsPolygon().getNvertex();
		
		return this.nSides;
	}
	
	public void setNVertex(int n) {
		nSides=n;
		if (n>2&&n<20) this.getModelShapeAsPolygon().setNvertex(n);
		if (n==0) setModelShape(new CircularGraphic(getModelShape(), 0));
		if (n==2) {
			setModelShape(new CrossGraphic(getModelShape()));
			
		}
		
	}
	
	/**returns the point size*/
	public double getPointSize() {
		return this.getModelShape().getObjectWidth();
	}

	/**sets the point size*/
	public void setPointSize(double number) {
		this.getModelShape().setHeight(number);
		this.getModelShape().setWidth(number);
	}
	
	

	public void setPointType(int choiceIndex) {
		pointType=choiceIndex;
	}
	

	private RegularPolygonGraphic getModelShapeAsPolygon() {
		
		if (getModelShape()==null||!( getModelShape() instanceof RegularPolygonGraphic )) setModelShape(new  RegularPolygonGraphic(new Rectangle(-2,-2,6,6)));

		return (RegularPolygonGraphic) getModelShape();
	}
	
	/**creates a shape for the data point d. Numbers are 
	 * cordinates to draw, not the values*/
	public RectangularGraphic getShapeGraphicForCordinatePoint(PlotPoint pt) {
		RectangularGraphic baseShape =getModelShape().copy();
		baseShape.setLocationType(RectangleEdges.CENTER);;
		baseShape.setLocation(0, 0);
		baseShape.moveLocation(pt.position.getX(), pt.position.getY());
		return baseShape;
		
		
	}
	
	
	/**creates a shape for the data point d. Numbers are 
	 * cordinates to draw, not the values*/
	public Shape getShapeForCordinatePoint(Point2D d) {
		
		Shape baseShape =getBasShape();
		
			AffineTransform tf = AffineTransform.getTranslateInstance(d.getX(), d.getY());
			return tf.createTransformedShape(baseShape);
		
		
	}
	
	private Shape getBasShape() {
		getModelShape().setLocationType(RectangleEdges.CENTER);;
		getModelShape().setLocation(0, 0);
		return getModelShape().getRotationTransformShape();
	}
	
	public RectangularGraphic createBasShapeCopy() {
		RectangularGraphic c = getModelShape().copy();
		c.setLocationType(RectangleEdges.CENTER);;
		c.setLocation(0, 0);
		return c;
	}




	public RectangularGraphic getModelShape() {
		return modelShape;
	}



	public void setModelShape(RectangularGraphic modelShape) {
		this.modelShape = modelShape;
	}

}
