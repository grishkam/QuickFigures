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
 * Version: 2022.2
 */
package graphicalObjects_Shapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;

import handles.AngleHandle;
import handles.SmartHandleList;

import java.awt.geom.Point2D;

import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;

/**Circular graphic with a hole inside*/
public class SimpleRing extends CircularGraphic {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	AngleParameter parameterRing=new AngleParameter(this); {parameterRing.setType(AngleParameter.RADIUS_TYPE); parameterRing.setRatioToMaxRadius(0.5);}
	
	public SimpleRing(Rectangle rectangle) {
		super(rectangle);
		setupName();
	}
	
	public SimpleRing(Rectangle rectangle, int arc) {
		super(rectangle);
		this.arc=arc;
		setupName();
	}

	public SimpleRing(SimpleRing simpleRing, int arc) {
		super(simpleRing, arc);
		setupName();
	}

	/**names the object*/
	private void setupName() {
		name=getShapeName();
	}
	
	/**returns the name for this kind of shape*/
	public String getShapeName() {
		String name1="Ring";
		if(this.isArc()) {
			name1="Part Ring";
		}
		return name1;
	}

	

	
	public SimpleRing copy() {
		SimpleRing output = new SimpleRing(this, arc);
		output.setRingRatio(getRingRatio());
		giveParametersTo(output);
		return output;
	}
	
	public static SimpleRing blankOval(Rectangle r, Color c, int arc) {
		SimpleRing r1 = new SimpleRing(r);
		r1.arc=arc;
		
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	
	/**Creates a certain number of vertices*/
	@Override
	public Shape getShape() {
		Area a=new Area(super.getShape());
		if(this.getRatioInternalToExternal()<1)
			a.subtract(new Area(getInnerRingShape()));;
		
		return a;
		
	}
	
	/**returns the shape of the hole*/
	public Shape getInnerRingShape() {
		
		Double r = new Ellipse2D.Double(x, y, getObjectWidth()*getRingRatio(), getObjectHeight()*getRingRatio());
		Point2D c = this.getCenterOfRotation();
		r.y=(int) (c.getY()-r.height/2);
		r.x=(int) (c.getX()-r.width/2);
		return r;
		
	}

	/**returns a handle list with a handle to control the size of the hole*/
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
	
		list.add(new AngleHandle(this, parameterRing, Color.green, Math.PI/4, 2044290));
		return list;
	}
	
	
	/**returns a point inside of the shape, defined by the ratio to the radius of an'
	 * enclosed oval*/
	public Point2D getInnerPoint(double factor) {
		double currentAngle =-Math.PI/4;
		return super.getPointInside(factor, currentAngle);
	}
	
	RectangularGraphic shapeUsedForIcon() {
		return  blankOval(new Rectangle(0,0,12,10), Color.BLACK, arc);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
	}
	

	/**Creates the shape for an illustrator script*/
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}
	
	
	
	public double getRatioInternalToExternal() {
		return getRingRatio();
	}
	/**returns the size of the hole relative to the size of the object*/
	public double getRingRatio() {
		return parameterRing.getRatioToMaxRadius();
	}
	/**sets the size of the hole relative to the size of the object*/
	public void setRingRatio(double ieRatio) {
		parameterRing.setRatioToMaxRadius(ieRatio);
	}
	
	public Object toIllustrator(ArtLayerRef aref) { 
		return this.createPathCopy().toIllustrator(aref);
	}
}
