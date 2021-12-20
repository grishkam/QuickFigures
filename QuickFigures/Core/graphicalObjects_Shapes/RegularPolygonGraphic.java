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
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import handles.CountHandle;
import handles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import objectDialogs.PolygonGraphicOptionsDialog;
import undo.AbstractUndoableEdit2;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.SimpleItemUndo;
import undo.UndoScalingAndRotation;
import undo.UndoStrokeEdit;

/**A graphic that depicts a polygon with n sides. subclasses include many shapes
  with a specific number of sides*/
public class RegularPolygonGraphic extends RectangularGraphic {
	

	{name="Polygon";}
	
	/**Parameter that defines the number of vertices*/
	private final CountParameter nvertex=new CountParameter(this, 5); {nvertex.parameterName="n sides";}
	
	private static final long serialVersionUID = 1L;
	
	
	public RegularPolygonGraphic(Rectangle2D rectangle) {
		super(rectangle);
	}
	public RegularPolygonGraphic(Rectangle rectangle, int nV) {
		super(rectangle);
		this.setNvertex(nV);
	}
	
	public RegularPolygonGraphic(RectangularGraphic r) {
		super(r);
	}
	
	
	public RectangularGraphic blankShape(Rectangle r, Color c) {
		RegularPolygonGraphic r1 = new RegularPolygonGraphic(r, getNvertex());
		
		
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	/**returns the name of the polygon*/
	public String getPolygonType() {
		switch(getNvertex()) {
			case 3:
				return "Triangle";
			case 4:
				return "Parallelogram";
			case 5:
				return "Pentagon";
			case 6:
				return "Hexagon";
			case 7:
				return "Septagon";
			case 8:
				return "Octogon";
			case 9:
				return "Nonogon";
			case 10:
				return "Decagon";
			
		}
		
		
		return "Regular Polygon";
	}

	public RegularPolygonGraphic copy() {
		RegularPolygonGraphic output = new RegularPolygonGraphic(this);
		output.setNvertex(getNvertex());
		return output;
	}
	


	/**implements a formular to produce a regular polygon with a certain number of vertices*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		double centx = x+rx;
		double centy = y+ry;
		double angle=getIntervalAngle();
		path.moveTo(centx+rx,centy);
		for(int i=1; i<getNvertex();i++) {
				double curx=centx+Math.cos(angle*i)*rx;
				double cury=centy+Math.sin(angle*i)*ry;
				path.lineTo(curx, cury);
		}
		path.closePath();
		this.setClosedShape(true);
		
		return path;
		
	}

	/**returns the angle between points on this polygon*/
	public double getIntervalAngle() {
		return 2*Math.PI/getNvertex();
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
		
	
	/**does nothing in this class but subclasses override this*/
	protected void selectSegmentForStrokeHandle(PathIterator pi) {
		
	}

	RectangularGraphic shapeUsedForIcon() {
		return  blankShape(new Rectangle(0,0,12,10), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
	}


	/**returns the number of vertices*/
	public int getNvertex() {
		return nvertex.getValue();
	}
	
	/**Sets the number of vertices*/
	public void setNvertex(int n) {
		if(n>=minimumNVertex())
			nvertex.setValue(n);
	}

	/**creates the shape for an illustrator script*/
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}

	
	/**returns the minimum number of vertices*/
	public int minimumNVertex() {
		return 3;
	}
	
	@Override
	public void showOptionsDialog() {
		new PolygonGraphicOptionsDialog(this, false).showDialog();
	}
	
	/**generates a handle list with handle for the number of vertices*/
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		nvertex.setMinValue(minimumNVertex());
		
		list.add(new CountHandle(this, nvertex, 900215));
		return list;
	}
	
	
	
	@Override
	public AbstractUndoableEdit2 provideUndoForDialog() {
		return new CombinedEdit(new UndoStrokeEdit(this), new UndoScalingAndRotation(this), new ColorEditUndo(this),new SimpleItemUndo<CountParameter> (nvertex));
	}
	
	
	
}
