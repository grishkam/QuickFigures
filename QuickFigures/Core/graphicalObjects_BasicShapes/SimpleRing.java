package graphicalObjects_BasicShapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Point2D;

import graphicalObjectHandles.AngleHandle;
import graphicalObjectHandles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;

/**A star object*/
public class SimpleRing extends CircularGraphic {

	private static final int STAR_RATIO_HANDLE = 80;
	
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
		if(this.isArc()) {
			name="Part Ring";
		}
		else name="Ring";
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
		r1.setDashes(new float[]{100000,1});
		r1.setStrokeWidth(4);
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
