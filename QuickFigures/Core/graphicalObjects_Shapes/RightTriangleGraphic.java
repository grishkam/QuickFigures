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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects.CordinateConverter;
import handles.SmartHandle;
import handles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import locatedObject.RectangleEdgePositions;
import locatedObject.RectangleEdges;

/**A Right triangle shape.
 *  user can change the oritenation of the triangle by dragging a handle*/
public class RightTriangleGraphic extends RectangularGraphic implements RectangleEdgePositions{
	


	{name="Right Triangle";}
	/**
	 * 
	 */
	/**The location of the right angle relative to the bounding box*/
	private int type=UPPER_LEFT;
	
	private static final long serialVersionUID = 1L;
	
	/**creates a right triangle graphic with the given bounds*/
	public RightTriangleGraphic(Rectangle rectangle) {
		super(rectangle);
	}
	
	/**creates a right triangle graphic with the given bounds*/
	public RightTriangleGraphic(Rectangle rectangle, int type) {
		super(rectangle);
		this.type=type;
	}
	
	/**Creates a right triangle with the same size, colors and lines as the input shape*/
	public RightTriangleGraphic(RectangularGraphic r) {
		super(r);
	}

	public RightTriangleGraphic copy() {
		RightTriangleGraphic rightTriangleGraphic = new RightTriangleGraphic(this);
		rightTriangleGraphic.setType(type);
		return rightTriangleGraphic;
	}

	/**Creates the shape of a right triangle*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		
	
		ArrayList<Point2D> loc = getEdgePoints();
		path.moveTo(loc.get(0).getX(),loc.get(0).getY());
		path.lineTo(loc.get(1).getX(),loc.get(1).getY());
		path.lineTo(loc.get(2).getX(),loc.get(2).getY());
		
		path.closePath();
		this.setClosedShape(true);
		
		return path;
		
	}
	
	/**returns the points of the triangle.*/
	ArrayList<Point2D> getEdgePoints() {
		ArrayList<Point2D> output=new ArrayList<Point2D>();
		
		if(getType()==UPPER_LEFT) {
			output.add(RectangleEdges.getLocation(RectangleEdges.UPPER_RIGHT, getRectangle()));
			output.add(RectangleEdges.getLocation(RectangleEdges.LOWER_LEFT, getRectangle()));
			output.add(RectangleEdges.getLocation(RectangleEdges.UPPER_LEFT, getRectangle()));
			
		} else 
			if(getType()==UPPER_RIGHT) {
				output.add(RectangleEdges.getLocation(RectangleEdges.UPPER_RIGHT, getRectangle()));
				output.add(RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, getRectangle()));
				output.add(RectangleEdges.getLocation(RectangleEdges.UPPER_LEFT, getRectangle()));
			
		} else 
			if(getType()==LOWER_RIGHT) {
			output.add(RectangleEdges.getLocation(RectangleEdges.UPPER_RIGHT, getRectangle()));
			output.add(RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, getRectangle()));
			output.add(RectangleEdges.getLocation(RectangleEdges.LOWER_LEFT, getRectangle()));
		} else {
			output.add(RectangleEdges.getLocation(UPPER_LEFT, getRectangle()));
			output.add(RectangleEdges.getLocation(LOWER_RIGHT, getRectangle()));
			output.add(RectangleEdges.getLocation(LOWER_LEFT, getRectangle()));
		}
		
		return output;
	
	}
	
	
	
	
	/**the shape used to draw the icon*/
	RectangularGraphic shapeUsedForIcon() {
		RightTriangleGraphic ss = blankShape(new Rectangle(0,0,12,10), Color.BLACK);
		ss.setType(getType());
		return ss;
		
	}

	
	/**creates the shape for an illustrator script*/
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}



	public int getType() {
		return type;
	}



	public void setType(int type) {
		this.type = type;
	}

	
	RectangularGraphic createIcon() {
		RectangularGraphic out = shapeUsedForIcon() ;
		out.setAntialize(true);
		out.setStrokeWidth(1);
		out.copyColorsFrom(this);
		if(super.isIconTooWhite()) {
			this.setStrokeColor(whiteIcon);
		}
		return out;
	}

	
	
	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		RigthAngleHandle handle = new RigthAngleHandle(this);
		handle.setHandleColor(Color.green);
		list.add(0,handle);
		return list;
	}
	
	/**a handle that lets the user easily change the triangle orientation*/
	public class RigthAngleHandle extends SmartHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private RightTriangleGraphic triangle;

		public RigthAngleHandle(RightTriangleGraphic rightTriangleGraphic) {
			
			triangle=rightTriangleGraphic;
			this.setHandleNumber(900471);
		}
		
		public Point2D getCordinateLocation() {
			int t = triangle.getType();
			Point2D l1 = RectangleEdges.getLocation(t, triangle.getRectangle());
			Point2D l2 = RectangleEdges.getLocation(CENTER, triangle.getRectangle());
			Point2D p = ShapeGraphic.betweenPoint(l1, l2, 0.7);
			triangle.undoRotationCorrection(p);
			return p;
		}
		
		/**changes which point along the bounding box is the location of the right angle.*/
		public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
			Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
			triangle.performRotationCorrection(p2);;
			int e = RectangleEdges.getNearestEdgeFromList(triangle.getRectangle(), new int[] {UPPER_RIGHT, LOWER_LEFT, UPPER_LEFT, LOWER_RIGHT}, p2);
			triangle.setType(e);
		}
		
		/**Draws the handle and the lines to denote the right angle*/
		@Override
		public void draw(Graphics2D graphics, CordinateConverter cords) {
			graphics.setStroke(new BasicStroke(1));
			graphics.setColor(Color.black);
			int t = triangle.getType();
			for (int i:RectangleEdges.adjacentPositions(t)) {
				Point2D l1 = RectangleEdges.getLocation(t, triangle.getRectangle());
				Point2D l2 = RectangleEdges.getLocation(i, triangle.getRectangle());
				Point2D p = ShapeGraphic.betweenPoint(l1, l2, 0.7);
				triangle.undoRotationCorrection(p);
				super.drawLineBetweenPoints(graphics, cords, p, getCordinateLocation());
			}
			super.draw(graphics, cords);
		}

	}
	
	public static RightTriangleGraphic blankShape(Rectangle r, Color c) {
		RightTriangleGraphic r1 = new RightTriangleGraphic(r, UPPER_LEFT);
		
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	public String getShapeName() {
		return "Right Triangle";
	}
	
	

	
}
