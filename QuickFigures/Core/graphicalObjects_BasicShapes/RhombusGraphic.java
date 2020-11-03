package graphicalObjects_BasicShapes;

import java.awt.Shape;
import java.awt.geom.Point2D;

import utilityClassesForObjects.PathPointList;

/**A Rhomboid shape */
public class RhombusGraphic extends RectangularGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double angleBend=0;
	
	@Override
	public Shape getShape() {
		PathPointList p = new PathPointList();
		for(int i=0; i<4; i++) p.addPoint(getVertexPoint(i));
		
		return p.createPath(true);
		
	}
	
	
	/**returns a point along the rhombus. */
	public Point2D getVertexPoint(int i) {
		if (i==1) return  new Point2D.Double(x+getObjectWidth(),y);
		if (i==2) return  new Point2D.Double(x+getObjectWidth()+Math.tan(getAngleBend())*getObjectHeight(),y+getObjectHeight());
		if (i==3) return  new Point2D.Double(x+Math.tan(getAngleBend())*getObjectHeight(),y+getObjectHeight());
		return new Point2D.Double(x,y);
		
		
		
		
	}


	public double getAngleBend() {
		return angleBend;
	}


	public void setAngleBend(double angleBend) {
		this.angleBend = angleBend;
	}
	

}
