package graphicalObjects_BasicShapes;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import utilityClassesForObjects.RectangleEdges;

public class CrossGraphic extends SimpleLineGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CrossGraphic(Rectangle rectangle) {
		super(rectangle);
		// TODO Auto-generated constructor stub
	}
	
	public CrossGraphic(RectangularGraphic crossGraphic) {
		super(crossGraphic);
	}

	public CrossGraphic copy() {
		return new CrossGraphic(this);
	}
	
	/**implements a formula to produce a cross*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		
	
		ArrayList<Point2D> loc = getLocations();
		path.moveTo(loc.get(0).getX(),loc.get(0).getY());
		path.lineTo(loc.get(1).getX(),loc.get(1).getY());
		path.moveTo(loc.get(2).getX(),loc.get(2).getY());
		path.lineTo(loc.get(3).getX(),loc.get(3).getY());
		return new BasicStroke(1).createStrokedShape(path);
		//return path;
		
	}
	
	ArrayList<Point2D> getLocations() {
		ArrayList<Point2D> output=new ArrayList<Point2D>();
		output.add(RectangleEdges.getLocation(RectangleEdges.LEFT, getRectangle()));
		output.add(RectangleEdges.getLocation(RectangleEdges.RIGHT, getRectangle()));
		output.add(RectangleEdges.getLocation(RectangleEdges.TOP, getRectangle()));
		output.add(RectangleEdges.getLocation(RectangleEdges.BOTTOM, getRectangle()));
		
		
		return output;
		
	}
	

}
