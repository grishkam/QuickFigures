package graphicalObjectHandles;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEventWrapper;
import graphicalObjects_BasicShapes.RectangleEdgeParameter;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import standardDialog.StandardDialog;
import undo.SimpleItemUndo;
import utilityClasses1.NumberUse;
import utilityClassesForObjects.RectangleEdges;

public class RectangleEdgeHandle extends SmartHandle {

	public static int LENGTH_TYPE=0, RATIO_TYPE=1;
	private RectangularGraphic theShape;
	private RectangleEdgeParameter theParameter;
	double displace=0;
	int type=0;
	private SimpleItemUndo<RectangleEdgeParameter> undo;
	private boolean undoaddedAlready;


	public RectangleEdgeHandle(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	public RectangleEdgeHandle(RectangularGraphic r,  RectangleEdgeParameter a, Color c, int handleNumber, int type, double displace) {
		this(0,0);
		this.theShape=r;
		this.theParameter=a;
		this.setHandleColor(c);
		this.type=type;
		this.displace=displace;
		this.setHandleNumber(handleNumber);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private void displacePoint(Point2D p) {
		Point2D pZero = RectangleEdges.getLocation(theParameter.zeroLocation,theShape.getRectangle());
		Point2D pMax = RectangleEdges.getLocation(theParameter.maxLengthLocation,theShape.getRectangle());
		double angle = ShapeGraphic.getAngleBetweenPoints(pZero, pMax)+Math.PI/2;
		//double d = pZero.distance(pMax);
		double d=displace*150;
		p.setLocation(p.getX()+d*Math.cos(angle), p.getY()+d*Math.sin(angle));
	} 
	
	public Point2D getCordinateLocation() {
		Point2D pZero = RectangleEdges.getLocation(theParameter.zeroLocation,theShape.getRectangle());
		Point2D pMax = RectangleEdges.getLocation(theParameter.maxLengthLocation,theShape.getRectangle());
		double angle = ShapeGraphic.getAngleBetweenPoints(pZero, pMax);
		java.awt.geom.Point2D.Double p = new Point2D.Double(pZero.getX()+theParameter.getLength()*Math.cos(angle), pZero.getY()+theParameter.getLength()*Math.sin(angle));
		if (type==RATIO_TYPE) {
			double r = theParameter.getRatioToMaxLength();
			double x3 = pZero.getX()*(1-r)+pMax.getX()*r;
			double y3 = pZero.getY()*(1-r)+pMax.getY()*r;
			p=new Point2D.Double(x3, y3);
		}
		displacePoint(p);
		theShape.undoRotationCorrection(p);
		return p;
	}
	
	public Point2D getZeroLocation() {
		Point2D p = RectangleEdges.getLocation(theParameter.zeroLocation,theShape.getRectangle());
		theShape.undoRotationCorrection(p);
		return p;
		}
	
	public Point2D getMaxLocation() {
		Point2D p = RectangleEdges.getLocation(theParameter.maxLengthLocation,theShape.getRectangle());
		theShape.undoRotationCorrection(p);
		return p;
		}
	
	
	public void handleDrag(CanvasMouseEventWrapper lastDragOrRelMouseEvent) {
		
		Point2D pz =  getZeroLocation();
		Point2D maxLocation = getMaxLocation();
		Point p2 = lastDragOrRelMouseEvent.getCordinatePoint();
		
		double angle = -NumberUse.getAngleBetweenPoints(pz, maxLocation);
		double maxDistance=distanceOnAxis(pz, maxLocation, angle);
		double distance=distanceOnAxis(pz, p2, angle);
		//IssueLog.log("Max distance "+maxDistance);
		
		/**if the mouse has been dragged to an invalid location*/
		if (maxLocation.distance(p2)>maxLocation.distance(pz)) distance=0;
		if(distance<0) distance=0;
		
		/**if the mouse drag beyond the point of max distance*/
		if(distance>maxDistance) distance=maxDistance;
		

		/**	while (inner>2*Math.PI) inner-=2*Math.PI;
			while (inner<0) 		inner+=2*Math.PI;
			while (inner2>2*Math.PI) inner2-=2*Math.PI;
			while (inner2<0) 		inner2+=2*Math.PI;*/
			theParameter.setLength(distance);
			theParameter.setRatioToMaxLength(distance/maxDistance);
		addUndo(lastDragOrRelMouseEvent);
	}


	
	/**Returns the distance between points along an axis of the given angle.
	  does not actually work properly*/
	private double distanceOnAxis(Point2D pz, Point2D maxLocation, double angle) {
		AffineTransform a = AffineTransform.getRotateInstance(angle, 0, 0);
		
		Point2D p3=new Point2D.Double();
		Point2D p4=new Point2D.Double();
		a.transform(pz, p3);
		a.transform(maxLocation, p4);
		return Math.abs(p4.getX()-p3.getX());
	}

	@Override
	public void handlePress(CanvasMouseEventWrapper w) {
		undo = new SimpleItemUndo<RectangleEdgeParameter>(theParameter);
		undoaddedAlready=false;
		
		if (w.clickCount()==2 && type!=RATIO_TYPE) {
			double nSW = StandardDialog.getNumberFromUser("Input ", theParameter.getLength());
			theParameter.setLength(nSW);
			addUndo(w);
		} 
		if (w.clickCount()==2 && type==RATIO_TYPE) {
			double nSW = StandardDialog.getNumberFromUser("Input ", theParameter.getRatioToMaxLength());
			if(nSW>1) nSW=1;
			theParameter.setRatioToMaxLength(nSW);
			addUndo(w);

		} 
	}

	public void addUndo(CanvasMouseEventWrapper w) {
		undo.establishFinalState();
		if(undoaddedAlready) {return;}
		w.addUndo(undo);
		undoaddedAlready=true;
	}
	
	public boolean handlesOwnUndo() {
		return true;
	}
}
