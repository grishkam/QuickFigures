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

/**A star object*/
public class SimpleRing extends CircularGraphic {

	
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

	private void setupName() {
		name=getShapeName();
	}
	
	
	public String getShapeName() {
		String name1="Ring";
		if(this.isArc()) {
			name1="Part Ring";
		}
		return name1;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String getPolygonType() {return "Ring";}
	
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
	
	
	public Shape getInnerRingShape() {
		
		Double r = new Ellipse2D.Double(x, y, getObjectWidth()*getRingRatio(), getObjectHeight()*getRingRatio());
		Point2D c = this.getCenterOfRotation();
		r.y=(int) (c.getY()-r.height/2);
		r.x=(int) (c.getX()-r.width/2);
		return r;
		
	}

	public double getRatioInternalToExternal() {
		return getRingRatio();
	}
	
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
	
	RectangularGraphic rectForIcon() {
		return  blankOval(new Rectangle(0,0,12,10), Color.BLACK, arc);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
	}
	


	
	
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}

	public double getRingRatio() {
		return parameterRing.getRatioToMaxRadius();
	}

	public void setRingRatio(double ieRatio) {
		parameterRing.setRatioToMaxRadius(ieRatio);
	}
}
