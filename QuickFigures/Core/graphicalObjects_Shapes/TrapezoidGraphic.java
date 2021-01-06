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
 * Version: 2021.1
 */
package graphicalObjects_Shapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import handles.RectangleEdgeHandle;
import handles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import locatedObject.RectangleEdges;

/**A trapezoid shaped graphic.user can adjust the exaxt appearance of the trapezoid
  */
public class TrapezoidGraphic extends RectangularGraphic {
	

	

	{name="Trapezoid";}
	/**
	 * 
	 */
	
	/**Determines the size of the top of the trapezoid*/
	protected RectangleEdgeParameter parameter=new RectangleEdgeParameter(this, 0.5,  UPPER_LEFT, TOP);
	
	
	public  TrapezoidGraphic(Rectangle rectangle) {
		super(rectangle);
	}
	public TrapezoidGraphic(Rectangle rectangle, double nV) {
		super(rectangle);
		this.getParameter().setRatioToMaxLength(nV);
	}
	
	public TrapezoidGraphic(RectangularGraphic r) {
		super(r);
	}
	
	private static final long serialVersionUID = 1L;
	
	
	public RectangularGraphic blankShape(Rectangle r, Color c) {
		TrapezoidGraphic r1 = new TrapezoidGraphic(r, getParameter().getRatioToMaxLength());
		r1.getParameter().setRatioToMaxLength(this.getParameter().getRatioToMaxLength());
		
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	


	public TrapezoidGraphic copy() {
		TrapezoidGraphic output = new TrapezoidGraphic(this);
		output.getParameter().setRatioToMaxLength(getParameter().getRatioToMaxLength());
		return output;
	}
	
	

	/**implements a formula to produce a trapezoid*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		Rectangle2D r = this.getRectangle();
		double rx=getObjectWidth()/2;
		
		Point2D startPoint = RectangleEdges.getLocation(LOWER_LEFT, r);
		path.moveTo( startPoint .getX(),startPoint .getY());
		
		Point2D p2 = RectangleEdges.getLocation(UPPER_LEFT, r);
		path.lineTo(p2.getX()+getParameter().getRatioToMaxLength()*rx, p2.getY());
		
		Point2D p3 = RectangleEdges.getLocation(UPPER_RIGHT, r);
		if (getParameter().getRatioToMaxLength()<1)path.lineTo(p3.getX()-getParameter().getRatioToMaxLength()*rx, p3.getY());
		
		Point2D p4 = RectangleEdges.getLocation(LOWER_RIGHT, r);
		path.lineTo(p4.getX(), p4.getY());
	
		path.closePath();
		this.setClosedShape(true);
		
		return path;
		
	}


	
	/**returns the points that define the stroke' handles location and reference location.
	   Precondition: the distance between the two points should be about half the stroke*/
		public Point2D[] getStrokeHandlePoints() {
			PathIterator pi = getShape().getPathIterator(null);
			pi.next();
			if (this.getParameter().getRatioToMaxLength()!=1)pi.next();
			double[] d=new double[6];pi.currentSegment(d);
			Point2D location2 =new Point2D.Double(d[0],d[1]);
			pi.next();d=new double[6];pi.currentSegment(d);
			Point2D location1 =new Point2D.Double(d[0],d[1]);
			this.getRotationTransform().transform(location2, location2);
			this.getRotationTransform().transform(location1, location1);
			return calculatePointsOnStrokeBetween(location1, location2);
		}
		
	
	RectangularGraphic shapeUsedForIcon() {
		return  blankShape(new Rectangle(0,0,12,10), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
	}

	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}

	
	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		RectangleEdgeHandle handle = new RectangleEdgeHandle(this, getParameter(), Color.green, 20,1, 0.05);
		list.add(0,handle);
		return list;
	}
	
	
	public String getShapeName() {
		return "Trapezoid";
	}



	public RectangleEdgeParameter getParameter() {
		return parameter;
	}



	public void setParameter(RectangleEdgeParameter parameter) {
		this.parameter = parameter;
	}
	
	
	}
	
	
	
	
	

