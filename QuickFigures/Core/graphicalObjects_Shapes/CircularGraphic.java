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
 * Version: 2021.2
 */
package graphicalObjects_Shapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import handles.AngleHandle;
import handles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;

/**A graphical object that draws either an Ellipse or an Arc
  within the bounds of a rectangle*/
public class CircularGraphic extends RectangularGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int NO_ARC=0, PI_ARC=1, CHORD_ARC=2;
	
	public int arc=NO_ARC;
	boolean isArc() {return arc>NO_ARC;}
	
	/**An angle parameter that determines the start of the arc2d*/
	AngleParameter parameterArcStart=new AngleParameter(this);
	/**An angle parameter that determines the end of the arc2d*/
	AngleParameter parameterArcEnd=new AngleParameter(this); {parameterArcEnd.setAngle(Math.PI/2);}
	
	
	public CircularGraphic(Rectangle2D rectangle) {
		this(rectangle, 0);
	}
	public CircularGraphic(Rectangle2D r2d, int arc) {
		super(r2d);
		this.arc=arc;
		{name="Oval";
		if(this.isArc())name="Arc";
		}
	}
	
	public CircularGraphic(RectangularGraphic r, int arc) {
		super(r);
		this.arc=arc;
		{name="Oval";}
		if(this.isArc())name="Arc";
	}

	
	
	
	
	/**Creates a copy*/
	public CircularGraphic copy() {
		
		CircularGraphic ovalGraphic = new CircularGraphic(this, arc);
		giveParametersTo(ovalGraphic);
	
		return ovalGraphic;
	}



	protected void giveParametersTo(CircularGraphic ovalGraphic) {
		ovalGraphic.parameterArcStart.setAngle(parameterArcStart.getAngle());
		ovalGraphic.parameterArcEnd.setAngle(parameterArcEnd.getAngle());
	}
	

	@Override
	public Shape getShape() {
		if (isArc()) return getArcShape();
		return new Ellipse2D.Double(x, y, getObjectWidth(), getObjectHeight());
		
	}



	public java.awt.geom.Arc2D.Double getArcShape() {
		Rectangle2D.Double rect1 = new Rectangle2D.Double(x, y, getObjectWidth(), getObjectHeight());
				
		double start = 360-parameterArcStart.inDegrees();
		double extent =-parameterArcEnd.inDegrees()+ parameterArcStart.inDegrees();
		if(extent<0) extent+=360;
		
		return new Arc2D.Double(rect1, start, extent, arc==PI_ARC?Arc2D.PIE: Arc2D.CHORD);
	}
	@Override
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		if (!this.isArc())
			pi.createElipse(aref, getBounds());
		else basicCreateShapeOnPathItem(aref, pi);
		pi.setName(getName());
	}
	
	RectangularGraphic shapeUsedForIcon() {
		return  blankOval(new Rectangle(0,0,12,10), Color.BLACK, arc);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
	}
	
	public boolean doesJoins() {
		return isArc();
	}
	
	/**determines two points along a line tangent to this circle
	  the shape should be a circle*/
	public Point2D[] getTangentForStrokeHandle(double angle) {
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		double centx = x+rx;
		double centy = y+ry;
		double curx=centx+Math.cos(angle)*rx;
		double cury=centy+Math.sin(angle)*ry;
		Point2D p0 = new Point2D.Double(curx, cury);//finds a point on the circle
		double dist=10;
		double angle2=angle+Math.PI/2;
		
		double x1 = p0.getX()+Math.cos(angle2)*dist;
		double y1 = p0.getY()+Math.sin(angle2)*dist;
		double x2 = p0.getX()-Math.cos(angle2)*dist;
		double y2 = p0.getY()-Math.sin(angle2)*dist;
		Point2D.Double location1 = new Point2D.Double(x1, y1);
		Point2D.Double location2 = new Point2D.Double(x2, y2);
		this.getRotationTransform().transform(location2, location2);
		this.getRotationTransform().transform(location1, location1);
		
		return new Point2D[] {location1,location2};
	}
	
	/**returns the points that define the stroke' handles location and reference location.
	   Precondition: the distance between the two points should be about half the stroke*/
		public Point2D[] getStrokeHandlePoints() {
			Point2D[] pt = getTangentForStrokeHandle(Math.PI/4);
			return calculatePointsOnStrokeBetween(pt[0], pt[1]);
		}
		
		/**creates a handle list that contains both the handles for a rectangular shape 
		 * and the handles that determine the angles of an arc*/
		protected SmartHandleList createSmartHandleList() {
			SmartHandleList list = super.createSmartHandleList();
			if (isArc()) {
				list.add(new AngleHandle(this, parameterArcStart, Color.cyan, 0, 62102));
				list.add(new AngleHandle(this, parameterArcEnd, Color.orange.darker(), 0, 231042));
			}
			return list;
		}

		
		public String getShapeName() {
			if(this.isArc()&&arc==PI_ARC) return "Pie Arc";
			if(this.isArc()&&arc==CHORD_ARC) return "Chord Arc";
			if (this.isArc()) return "Arc";
			return "Oval";
		}
		
		public static CircularGraphic filledCircle(Rectangle r) {
			CircularGraphic output = new CircularGraphic(r);
			output.setFilled(true);
			output.setStrokeWidth(-1);
			return output;
			}
		
		public static CircularGraphic halfCircle(Rectangle2D r2d) {
			CircularGraphic output = new CircularGraphic(r2d);
			output.setFilled(true);
			output.setStrokeWidth(-1);
			output.arc=1;
			output.parameterArcStart.setAngle(-Math.PI/2);
			output.parameterArcEnd.setAngle(Math.PI/2);
			return output;
			}
		public static RectangularGraphic blankOval(Rectangle r, Color c, int arc) {
			CircularGraphic r1 = new CircularGraphic(r);
			r1.arc=arc;
			
			r1.setStrokeWidth(THICK_STROKE_4);
			r1.setStrokeColor(c);
			return r1;
		}
		
}
