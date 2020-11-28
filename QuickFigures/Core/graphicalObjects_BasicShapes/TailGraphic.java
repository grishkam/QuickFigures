package graphicalObjects_BasicShapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import javax.swing.undo.AbstractUndoableEdit;

import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.UndoScalingAndRotation;
import undo.UndoStrokeEdit;
import utilityClassesForObjects.RectangleEdgePosisions;
import utilityClassesForObjects.RectangleEdges;

/**A graphic that depicts a polygon with n sides. subclasses include many shapes
  with a specific number of sides*/
public class TailGraphic extends RectangularGraphic implements RectangleEdgePosisions{
	

	{name="tail";}
	/**
	 * 
	 */
	
	private double notchAngle=Math.PI/2; 
	
	private static final long serialVersionUID = 1L;
	
	
	public RectangularGraphic blankShape(Rectangle r, Color c) {
		TailGraphic r1 = new TailGraphic(r);
		
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
	
	public TailGraphic(Rectangle2D rectangle) {
		super(rectangle);
	}
	
	
	public TailGraphic(RectangularGraphic r) {
		super(r);
	}

	/**creates the shape*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		double angle=getIntervalAngle();
		double shift=ry/Math.tan(angle/2);
		
		
		int[] i7 = new int[] { RIGHT,  UPPER_RIGHT, UPPER_LEFT, LEFT,  LOWER_LEFT, LOWER_RIGHT};
		Double rect = this.getRectangle();
		
		
		for(int i=0; i<i7.length;i++) {
				Point2D p = RectangleEdges.getLocation(i7[i], rect);
				double cx = p.getX();
				if (i7[i]==LEFT||i7[i]==RIGHT) cx-=shift;
				if (i==0) path.moveTo(cx, p.getY()); else
					path.lineTo(cx, p.getY());
		}
		path.closePath();
		this.setClosedShape(true);
		
		return path;
		
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
		// TODO Auto-generated method stub
		
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
