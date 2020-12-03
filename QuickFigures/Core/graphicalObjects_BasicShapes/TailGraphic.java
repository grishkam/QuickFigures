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
package graphicalObjects_BasicShapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.undo.AbstractUndoableEdit;

import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.UndoScalingAndRotation;
import undo.UndoStrokeEdit;
import utilityClassesForObjects.RectangleEdgePosisions;
import utilityClassesForObjects.RectangleEdges;

/**A graphic that was designed to depict the tail of an arrow. 
  TODO: modify it to include options for a variety of interesting shapes*/
public class TailGraphic extends RectangularGraphic implements RectangleEdgePosisions{
	
	final int[] locations = new int[] { RIGHT,  UPPER_RIGHT, UPPER_LEFT, LEFT,  LOWER_LEFT, LOWER_RIGHT};
	int divisions=2;
	double cutAway=0.10;
	{name="tail";}
	/**
	 * 
	 */
	
	private double notchAngle=Math.PI/2; 
	
	private static final long serialVersionUID = 1L;
	
	public TailGraphic(Rectangle2D rectangle, int nDiv) {
		super(rectangle);
		divisions=nDiv;
	}
	
	
	public RectangularGraphic blankShape(Rectangle r, Color c) {
		TailGraphic r1 = new TailGraphic(r, 2);
		
		r1.setDashes(NEARLY_DASHLESS);
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	public String getPolygonType() {
		return "Tail";
	}

	public TailGraphic copy() {
		TailGraphic output = new TailGraphic(this);
		output.setNotchAngle(notchAngle);
		return output;
	}
	
	
	
	
	public TailGraphic(RectangularGraphic r) {
		super(r);
	}

	/**creates the shape*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		
		
		
		double angle=getIntervalAngle();
		
		
		
		
		Rectangle2D rect = this.getRectangle();
		
			
		for(int i=0; i<divisions; i++) {
			double space = rect.getWidth()*cutAway;
			if (divisions==1) space=0;
			double width=rect.getWidth()/divisions-space;
			Rectangle2D r2 = new Rectangle2D.Double(rect.getX()+i*(space+width), rect.getY(), width, rect.getHeight());
			addTailPartToPath(path, angle, r2);
		}
		
		
		
		
		
		
		this.setClosedShape(true);
		
		return path;
		
	}

	/**
	 Adds a fragment to the path that is based on the given rectangle
	 */
	public void addTailPartToPath(Path2D.Double path, double angle, Rectangle2D rect) {
		double shift=rect.getHeight()/Math.tan(angle/2);
		for(int i=0; i<locations.length;i++) {
				Point2D p = RectangleEdges.getLocation(locations[i], rect);
				double cx = p.getX();
				if (locations[i]==LEFT||locations[i]==RIGHT) cx-=shift;
				if (i==0) path.moveTo(cx, p.getY()); else
					path.lineTo(cx, p.getY());
		}
		path.closePath();
	}

	
	
	
	private double getIntervalAngle() {
		return getNothchAngle();
	}

	/**returns the points that define the stroke' handles location and reference location.
	   Precondition: the distance between the two points should be about half the stroke*/
		public Point2D[] getStrokeHandlePoints() {
			PathIterator pi = getShapeForStrokeHandlePoints().getPathIterator(null);
			selectSegmentForStrokeHandle(pi);
			double[] d=new double[6];pi.currentSegment(d);
			Point2D location2 =new Point2D.Double(d[0],d[1]);
			pi.next();d=new double[6];pi.currentSegment(d);
			Point2D location1 =new Point2D.Double(d[0],d[1]);
			this.getRotationTransform().transform(location2, location2);
			this.getRotationTransform().transform(location1, location1);
			return calculatePointsOnStrokeBetween(location1, location2);
		}

	protected Shape getShapeForStrokeHandlePoints() {
		return getShape();
	}
		
	
	protected void selectSegmentForStrokeHandle(PathIterator pi) {
		
	}

	RectangularGraphic rectForIcon() {
		return  blankShape(new Rectangle(0,0,12,10), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
	}



	


	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}


	
	
	
	@Override
	public AbstractUndoableEdit provideUndoForDialog() {
		return new CombinedEdit(new UndoStrokeEdit(this), new UndoScalingAndRotation(this), new ColorEditUndo(this));
	}

	public double getNothchAngle() {
		return notchAngle;
	}

	public void setNotchAngle(double tipAngle) {
		this.notchAngle = tipAngle;
	}
	
	
	
}
