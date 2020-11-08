package graphicalObjects_BasicShapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import graphicalObjectHandles.AngleHandle;
import graphicalObjectHandles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;

public class CircularGraphic extends RectangularGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int arc=0;
	boolean isArc() {return arc>0;}
	
	AngleParameter parameterArcStart=new AngleParameter(this);
	AngleParameter parameterArcEnd=new AngleParameter(this); {parameterArcEnd.setAngle(Math.PI/2);}
	
	
	public static RectangularGraphic blankOval(Rectangle r, Color c, int arc) {
		CircularGraphic r1 = new CircularGraphic(r);
		r1.arc=arc;
		r1.setDashes(new float[]{100000,1});
		r1.setStrokeWidth(4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	

	public CircularGraphic copy() {
		
		CircularGraphic ovalGraphic = new CircularGraphic(this, arc);
		giveParametersTo(ovalGraphic);
	
		return ovalGraphic;
	}



	protected void giveParametersTo(CircularGraphic ovalGraphic) {
		ovalGraphic.parameterArcStart.setAngle(parameterArcStart.getAngle());
		ovalGraphic.parameterArcEnd.setAngle(parameterArcEnd.getAngle());
	}
	
	public CircularGraphic(Rectangle rectangle) {
		this(rectangle, 0);
	}
	public CircularGraphic(Rectangle rectangle, int arc) {
		super(rectangle);
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
		
		return new Arc2D.Double(rect1, start, extent, arc==1?Arc2D.PIE: Arc2D.CHORD);
	}
	@Override
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		if (!this.isArc())
			pi.createElipse(aref, getBounds());
		else basicCreateShapeOnPathItem(aref, pi);
		pi.setName(getName());
	}
	
	RectangularGraphic rectForIcon() {
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
		
		
		protected SmartHandleList createSmartHandleList() {
			SmartHandleList list = super.createSmartHandleList();
			if (isArc()) {
				list.add(new AngleHandle(this, parameterArcStart, Color.cyan, 0, 62102));
				list.add(new AngleHandle(this, parameterArcEnd, Color.orange.darker(), 0, 231042));
			}
			return list;
		}

		
		public String getShapeName() {
			if (this.isArc()) return "Arc";
			return "Oval";
		}
		
		public static CircularGraphic filledCircle(Rectangle r) {
			CircularGraphic output = new CircularGraphic(r);
			output.setFilled(true);
			output.setStrokeWidth(-1);
			return output;
			}
		
		public static CircularGraphic halfCircle(Rectangle r) {
			CircularGraphic output = new CircularGraphic(r);
			output.setFilled(true);
			output.setStrokeWidth(-1);
			output.arc=1;
			output.parameterArcStart.setAngle(-Math.PI/2);
			output.parameterArcEnd.setAngle(Math.PI/2);
			return output;
			}
		
}