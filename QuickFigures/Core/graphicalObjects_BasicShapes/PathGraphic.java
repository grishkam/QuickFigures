package graphicalObjects_BasicShapes;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import animations.KeyFrameAnimation;
import applicationAdapters.CanvasMouseEventWrapper;
import graphicalObjectHandles.HandleRect;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleForPathGraphic;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjectHandles.PathPointReshapeList;
import graphicalObjectHandles.HasSmartHandles;
import graphicalObjectHandles.ReshapeHandleList;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicHolder;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.CompoundPathItemRef;
import illustratorScripts.IllustratorObjectRef;
import illustratorScripts.PathItemRef;
import keyFrameAnimators.PathGraphicKeyFrameAnimator;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.PathGraphicMenu;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.PathEditUndo;
import undo.UndoScalingAndRotation;
import undo.UndoStrokeEdit;
import utilityClasses1.NumberUse;
import utilityClassesForObjects.BasicStrokedItem;
import utilityClassesForObjects.PathObject;
import utilityClassesForObjects.PathPoint;
import utilityClassesForObjects.PathPointList;
import utilityClassesForObjects.ScalesFully;
import utilityClassesForObjects.ShapeMaker;

/**a curved path or shape. can contain any number of points*/
public class PathGraphic extends ShapeGraphic implements PathObject, ScalesFully ,HasSmartHandles, GraphicHolder {
	


	{name="Path"; this.setHasCloseOption(true);}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int selectedsegmentindex=-1;
	
	private boolean useFilledShapeAsOutline=false;
	private PathPointList points=new PathPointList();
	
	/**a Path2D object for drawing this PathGraphic on a graphics2D. This item is updated when certain methods make changes to the pathpoint list */
	private Path2D path=new Path2D.Float();
	
	/**constants determine how handles are used and which handles are visible*/
	public static final int ANCHOR_HANDLE_ONLY_MODE=0, THREE_HANDLE_MODE=2, TWO_HANDLE_MODE=1, CURVE_CONTROL_HANDLES_LINKED=3, MOVE_ALL_SELECTED_HANDLES=4, CURVE_CONTROL_SYMETRIC_MODE=5;
	private int handleMode=THREE_HANDLE_MODE; 
	
	ArrayList<HandleRect> leftHandles=new ArrayList<HandleRect>();
	ArrayList<HandleRect> rightHandles=new ArrayList<HandleRect>();
	
	/**some paths will end with an arrow head*/
	ArrowGraphic arrowHead1=null,
						arrowHead2=null;

	private transient SmartHandleList smartHandleBoxes;

	/**outline is the area that a user may click on to select the path*/
	private Shape outline;

	private boolean useArea=false;
	
	/**creates a path graphic from the given path2d*/
	public PathGraphic(Path2D path2d) {
		this.setPath(path2d);
	}
	
	public PathGraphic(PathPointList path) {
		this.setPoints(path);
	}
	
	public PathGraphic(Point2D... path) {
		if (path.length==0) return;
		setLocationInnitial(path[0]);
		for(int i=1; i<path.length;i++) {
			addPoint(path[i]);
		}
	}

	public PathGraphic(Point2D p) {
		setLocationInnitial(p);
	}
	
	/**returns each anchor point of the curve*/
	ArrayList<Point2D> getAnchorPoints() {
		ArrayList<Point2D> out=new ArrayList<Point2D>();
		for(PathPoint p:getPoints()) {out.add(p.getAnchor());}
		return out;
	}
	
	/**returns the first curve control point for each point in the curve*/
	ArrayList<Point2D> getLeftPoints() {
		ArrayList<Point2D> out=new ArrayList<Point2D>();
		for(PathPoint p:getPoints()) {out.add(p.getCurveControl1());}
		return out;
	}
	
	/**returns the second curve control point for each point in the curve*/
	ArrayList<Point2D> getRightPoints() {
		ArrayList<Point2D> out=new ArrayList<Point2D>();
		for(PathPoint p:getPoints()) {out.add(p.getCurveControl2());}
		return out;
	}
	

	/**creates new points for the path, */
	void setPathToAnchorPoints(ArrayList<Point2D> in) {
		PathPointList out=new PathPointList();
		for(Point2D p:in) {out.add(new PathPoint(p));}
		setPoints(out);
	}
	
	
	/**Makes this path similar to the path given except that it
	 * copies only the anchor point locations and not the curve control points*/
	void copyAnchorPointsFrom(PathGraphic in) {
		this.setPathToAnchorPoints(in.getAnchorPoints());
		//this.setLeftPoints(in.getLeftPoints());
	}
	
	@Override
	public int handleNumber(int x, int y) {
		if (this.getPointHandles()!=null) {
			
			int output=getSmartHandleList().handleNumberForClickPoint(x, y);
			
			return output;
		}
		
		return -1;

	}
	
	
	/**used to set the first point when initializing a new path*/
	private void setLocationInnitial(Point2D p) {
		this.setLocation(p.getX(), p.getY());
		getPoints().add(new PathPoint(0,0)); 
		
		path.moveTo(0, 0);
	}

	@Override
	public PathGraphic copy() {
		PathGraphic output = new PathGraphic(getPath());
		copyColorAttributeTo(output);
		output.setLocation(getLocation());
		output.setPoints(getPoints().copy());
		output.setClosedShape(this.isClosedShape());
		if (this.arrowHead1!=null) output.arrowHead1=this.getArrowHead1().copy();
		if (this.arrowHead2!=null) output.arrowHead2=this.getArrowHead2().copy();
		
		return output;
	}
	
	/**since this subclass is already a path, its path copy is just a normal copy*/
	public PathGraphic createPathCopy() {
		return copy();
	}

	/**given a location on the canvas, adds a new point to the path (from that location)
	*/
	public PathPoint addPoint(Point2D p) {
		Point2D p2 = convertPointToInternalCrdinates(p);//if the path location is not (0,0) converts
		PathPoint output = getPoints().addPoint(p2);//adds a point to the list
		path.lineTo(p2.getX(), p2.getY());//updates the internal path2d
		return output;
	}
	
	/**given a location on the canvas, adds a new point to the start of the path (from that location)
	  does not update the internal path2D. another method must be called for that
	*/
	public PathPoint addPointToStart(Point2D p) {
		Point2D p2 = convertPointToInternalCrdinates(p);
		PathPoint output = getPoints().addPoint(p2, 0);
		
		return output;
	}
	
	/**when given a point, converts the cordinate to the paths internally used
	 * cordinates system. this involves a translation, and a rotation*/
	public Point2D.Double convertPointToInternalCrdinates(Point2D p2) {
		if (angle ==0) return new Point2D.Double(p2.getX()-x, p2.getY()-y);
		/**Point2D ptranslate = new Point2D.Double(p.getX()-x, p.getY()-y);
		try{getRotationTransform().transform(ptranslate,  ptranslate);}  catch (Throwable r) {
			r.printStackTrace();
		}
		return ptranslate;*/
		java.awt.geom.Point2D.Double p = new Point2D.Double();
		try{getTransformForPathGraphic().inverseTransform(p2, p);} catch (Throwable t) {t.printStackTrace();}
		return p;
		}
	
	public Point2D.Double convertPointToExternalCrdinates(Point2D p2) {
		if (angle ==0) return new Point2D.Double(p2.getX()+x, p2.getY()+y);
		return null;
		}
	
	public Point2D getTransformPointsForPathGraphic(Point2D p) {
		AffineTransform aa = getTransformForPathGraphic();
		Double output = new Point2D.Double();
		aa.transform(p, output);
		return output;
	}
	
	
	public void setClosedShape(boolean closedShape) {
		super.setClosedShape(closedShape);
		updatePathFromPoints();
	}
	
	
	/**The outline needed to determine if the user has clicked inside the shape or not
	  intricate series of stroked shapes for line outline necessary*/
	@Override
	public Shape getOutline() {
		if (outline==null) outline=createOutline() ;
		return outline ;
	}
	
	/**creates the outline of the shape that determines where the user can click to select the shape*/
	private Shape createOutline() {
		/**for filled shapes or relatively complex paths, the shape itself will be used as an outline*/
		if (isUseFilledShapeAsOutline()||this.getPoints().size()>25) return getShape();
		
		/**for simpler paths lines, a thick stroked area around the line is used as an outline*/
		float strokeWidth2 = this.getStrokeWidth();
		if(strokeWidth2<0)strokeWidth2=0;
		Shape shape = new BasicStroke(strokeWidth2).createStrokedShape(getShape());
		Area a=new Area(shape);
		a.add(new Area(new BasicStroke(12).createStrokedShape(a)));
		return getRotationTransform().createTransformedShape(a);
	}
	
	/**returns a path iterator for the shape*/
	public PathIterator getPathIterator() {
		return getShape().getPathIterator(new AffineTransform());
	}
	
	/**returns an array of anchor points */
	private static ArrayList<Point2D> shapeToArray(PathIterator s) {
		PathIterator pi = s;
		double[] d=new double[6];
		ArrayList<Point2D> poly = new ArrayList<Point2D>();
		
		while (!pi.isDone()) {
			pi.currentSegment(d);
			//if (d[0]==0&& d[1]==0) {} else
			poly.add(new Point2D.Float((float)d[0], (float)d[1]));
			
			pi.next();
		}
		return poly;
	}
	

	@Override
	public Rectangle getBounds() {
		return getShape().getBounds();
	}

	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		if (this.getPointHandles()==null) return;
		SmartHandle thehandle = this.getPointHandles().getHandleNumber(handlenum);
		if (thehandle!=null)thehandle.handleMove(p1, p2);
	}

	@Override
	public Shape getShape() {
		if (this.isUseArea()) {
			//IssueLog.log("Will return area");
			Area output = new Area();
			output.add(new Area(getPath()));
			return transform() .createTransformedShape(output);
		}
		;
		return transform() .createTransformedShape(getPath());
	}
	
	public AffineTransform transform() {
		return AffineTransform.getTranslateInstance(x, y);
	}
	
	
	
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter<?> cords) {
		if (selected) {
			if (getPointHandles()==null) setSmartHandleBoxes(SmartHandleForPathGraphic.getPathSmartHandles(this));
			getPointHandles().draw(g2d, cords);
		
		   }
		 getGrahpicUtil().setHandleFillColor(Color.GRAY); 
		
	}
	
	

	public Path2D getPath() {
		return path;
	}

	
	public void setPath(Path2D path2d) {
		this.path = path2d;
		this.setPathToAnchorPoints(shapeToArray(path2d.getPathIterator(new AffineTransform())));
	}
	
	public void setPathToShape(Shape path2d) {
		
		this.setPathToAnchorPoints(shapeToArray(path2d.getPathIterator(new AffineTransform())));
		this.updatePathFromPoints();
	}
	
	
	public void updatePathFromPoints(){
		this.path=getPoints().createPath(this.isClosedShape());//updatePathFromPoints(this.getPoints(), isClosedShape());
		outline=null;
		setSmartHandleBoxes(null);
		reshapeList2=null;
	}
	

	
	public boolean isCurvemode() {
		if(getHandleMode()==MOVE_ALL_SELECTED_HANDLES) return false;
		if (getHandleMode()>0) return true;
		return false;
		//return curvemode;
	}

	
	/**returns true if the second of the two curve control points is to be visible*/
	public boolean isSuperCurveControlMode() {
		if (getHandleMode()==PathGraphic.THREE_HANDLE_MODE) return true;
		if (getHandleMode()==PathGraphic.CURVE_CONTROL_HANDLES_LINKED) return true;
		if (getHandleMode()==PathGraphic.CURVE_CONTROL_SYMETRIC_MODE) return true;
		return false;
	}

	public void setSupercurvemode(boolean supercurvemode) {
		setHandleMode(PathGraphic.THREE_HANDLE_MODE);
	}
	
	
	public boolean isDrawClosePoint() {
		return false;
	}

	/**returns the path point list*/
	public PathPointList getPoints() {
		return points;
	}
	/**sets a new path point list*/
	public void setPoints(PathPointList points) {
		this.points = points;
		this.updatePathFromPoints();
	}
	
	/**creates a simple path that is used for the icon*/
	public static PathGraphic createExample() {
		PathGraphic output = new PathGraphic(new Point(0,0));
		output.addPoint(new Point(1,4));
		output.addPoint(new Point(4,8));
		output.addPoint(new Point(8,6));
		output.addPoint(new Point(12,6));
		Point2D[] pt = output.getPoints().getMidpointsOfAnchors();
		output.getPoints().setCurvePoints(pt);
		return output;
	}
	
	/**when given a shape maker, creates a path*/
	public static PathGraphic createPolygon(ShapeMaker shapeMaker ){//double length, int vertices, boolean in) {
		
		PathGraphic out = new PathGraphic(shapeMaker.getPathPointList());;
		out.setAntialize(true);
		out.setClosedShape(true);
		out.setStrokeJoin(BasicStroke.JOIN_MITER);
		out.setMiterLimit(40);
		return out;
	}
	


	public boolean isUseFilledShapeAsOutline() {
		return useFilledShapeAsOutline;
	}

	public void setUseFilledShapeAsOutline(boolean useFilledShapeAsOutline) {
		this.useFilledShapeAsOutline = useFilledShapeAsOutline;
	}

	public PopupMenuSupplier getMenuSupplier() {
		return new PathGraphicMenu(this);
	}
	
	@Override
	public Point getLocationUpperLeft() {
		Rectangle b = getBounds();
		return new Point(b.x, b.y);
		
	}

	@Override
	public void setLocationUpperLeft(double x, double y) {
		Point p = getLocationUpperLeft() ;
		super.moveLocation(x-p.x, y-p.y);
		outline=null;//so a new outline will be created next time its needed
		reshapeList2=null;
	}
	
	public void moveLocation(double x, double y) {
		super.moveLocation(x, y);
		outline=null;//so a new outline will be created next time its needed
		reshapeList2=null;
	}
	
	/**scales the path about point p, also scale strokes and effects*/
	@Override
	public void scaleAbout(Point2D p, double mag) {
		p=this.convertPointToInternalCrdinates(p);
		
		
		AffineTransform af = new AffineTransform();
		af.translate(p.getX(), p.getY());
		af.scale(mag, mag);
		af.translate(-p.getX(), -p.getY());
		
		BasicStrokedItem.scaleStrokeProps(this, mag);
		getPoints().applyAffine(af);
		
		this.updatePathFromPoints();
		
	}
	
	/**scales the path about point p, does not scale strokes and effects*/
	public void scaleAbout(Point2D p, double magx, double magy) {
		p=this.convertPointToInternalCrdinates(p);

		AffineTransform af = new AffineTransform();
		af.translate(p.getX(), p.getY());
		af.scale(magx, magy);
		af.translate(-p.getX(), -p.getY());
		getPoints().applyAffine(af);
		this.updatePathFromPoints();
		
	}
	
	
	/**Used when generating a script for adobe illustrator*/
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		if (this.isCompleteMoveToIlls()) {
			pi.addPathWithCurves(aref, this.getPoints(), true, isDrawClosePoint());
			 pi.translate(x, y);
			
		} else
			pi.createPathWithoutCurves(aref, getShape());
		
		
		 if (this.isClosedShape()) pi.setClosed(true);
		 pi.setName(this.getName());
		}
	
	boolean compound=true;

	
	/**Used when generating a script for adobe illustrator*/
	@Override
	public Object toIllustrator(ArtLayerRef aref) {
		ArrayList<PathPointList> secs = this.getPoints().createAtCloseSubsections();
	
		
			if (secs.size()==1) {
		return super.toIllustrator(aref);
		
			}
			IllustratorObjectRef wantspathITems;
			
			
	//	if (compound) {
			CompoundPathItemRef ci=new CompoundPathItemRef();
			ci.createItem(aref);
			wantspathITems=ci;
			
		/**} else {
			GroupItemRef gr = new GroupItemRef();
			gr.createNewRef(aref);
			wantspathITems=gr;
		}*/
	
		for(PathPointList s: secs) {
			createShapeIllustrator(wantspathITems,s);
		}
		return wantspathITems;
		
	}
	
	
	/**needed to create script to make the given pathpoint list in illustrator*/
	private Object createShapeIllustrator(IllustratorObjectRef aref, PathPointList p) {
		PathItemRef pi = new PathItemRef();
	
		pi.addPathWithCurves(aref, p, true, isDrawClosePoint());
		 pi.translate(x, y);
		setPathItemColorsToImmitate(pi);
		pi.setClosed(isClosedShape());
		return pi;
	}

	public int getHandleMode() {
		return handleMode;
	}

	public void setHandleMode(int handleMode) {
		this.handleMode = handleMode;
	}
	
	/**This the transform that transform the pathPoint list points into the path graphic's coordiantes on the canvas that 
	  will be displayed*/
	public AffineTransform getTransformForPathGraphic() {
		
		AffineTransform output =getRotationTransform();//the rotation transform will actually have an angle of 0 since. paths are no longer allowed to be.
		output.concatenate( AffineTransform.getTranslateInstance(getLocation().getX(), getLocation().getY()));
		return output;
	}

	/**returns the smart handle list*/
	@Override
	public SmartHandleList getSmartHandleList() {
		if ( getPointHandles()==null) {
			setSmartHandleBoxes(new SmartHandleList());
		}
		if (this.superSelected) return SmartHandleList.combindLists(getPointHandles(), getButtonList(), getReshapeList(), getReshapeList2(),getAddPointList());
		return  getPointHandles();
	}

	/**The add point list conists of two handles */
	private transient SmartHandleList addPointList;
	private SmartHandleList getAddPointList() {
		if (addPointList==null)addPointList=SmartHandleList.createList(new AddPointSmartHandle(this, false),new AddPointSmartHandle(this, true));
		return addPointList;
	}

	/**The reshape handle list contains points for rotation and scaling of the path point list*/
	private transient ReshapeHandleList reshapeList;
	private ReshapeHandleList getReshapeList() {
		if(reshapeList==null)reshapeList=new ReshapeHandleList(0, this);
		reshapeList.updateRectangle();
		return reshapeList;
	}

	/**the second reshape handle list contains handles for rotating, moving and scaling only a subset of points*/
	public transient ReshapeHandleList reshapeList2;
	private ReshapeHandleList getReshapeList2() {
		if(this.getPoints().getSelectedPointsOnly().size()<2) return null;
		if(reshapeList2==null)reshapeList2=new PathPointReshapeList( 90000000, this);
		reshapeList2.updateRectangle();
		return reshapeList2;
	}
	
	
	/**creates an uncurved path with anchor points in position such that it resembles this path*/
	public PathGraphic break10(int parts) {
		double npart=parts;
		PathPointList newlist = this.points.copy();
		PathPointList newlist2 =new PathPointList();
		
		for(int j=0; j<newlist.size(); j++) {
		 PathPoint pp= newlist.get(j);
			PathPoint previois = null;
			if (j>0)previois = newlist.get(j-1);
			if (previois!=null) {
				for(int i=1; i< npart; i++) {
					Point2D curvep =PathPointList.interPolatePlaceOnCurve(previois , pp, i*(1/npart));
					 newlist2.addPoint( curvep);
				 }
			}
			
			newlist2.addPoint(pp.getAnchor());
			
		}
		
		PathGraphic newg = new PathGraphic(newlist2);
		copyColorAttributeTo(newg );
		newg .setLocation(getLocation());
		
		return newg;
		
		
		
	}
	
	/**reflects the path about a line*/
	public void reflectPathAboutLine(Point2D clickedCord,Point2D draggedCord) {
		Point2D lineToProjectOn=convertPointToInternalCrdinates(clickedCord);
		Point2D lineToProjectOn2=convertPointToInternalCrdinates(draggedCord);
			
			for(PathPoint point: this.getPoints()) {
				point.setAnchor(PathPointList.reflectPointAboutLine(point.getAnchor(), lineToProjectOn,  lineToProjectOn2));
				point.setCurveControl1(PathPointList.reflectPointAboutLine(point.getCurveControl1(), lineToProjectOn,  lineToProjectOn2));
				point.setCurveControl2(PathPointList.reflectPointAboutLine(point.getCurveControl2(), lineToProjectOn,  lineToProjectOn2));
			}
			updatePathFromPoints();
			
}

	/**roates the path about a point*/
	public void rotateAbout(Point2D clickedCord, double distanceFromCenterOfRotationtoAngle) {
		Point2D pointCenter=convertPointToInternalCrdinates(clickedCord);
		AffineTransform at = AffineTransform.getRotateInstance(distanceFromCenterOfRotationtoAngle, pointCenter.getX(), pointCenter.getY());
		this.getPoints().applyAffine(at);
		this.updatePathFromPoints();
		
	}

	/**returns the handles for the point in the path */
	public synchronized SmartHandleList getPointHandles() {
		if (smartHandleBoxes==null) {
			setSmartHandleBoxes(SmartHandleForPathGraphic.getPathSmartHandles(this));
		}
		return smartHandleBoxes;
	}

	/**stores the given smart handle list as the main handle boxes. adds arrow size handles if needed*/
	public void setSmartHandleBoxes(SmartHandleList smartHandleBoxes) {
		if(this.smartHandleBoxes == smartHandleBoxes)return;
		this.smartHandleBoxes = smartHandleBoxes;
		if(getArrowHead1()!=null&&smartHandleBoxes!=null) {
			smartHandleBoxes.add(getArrowHead1().createArrowSizeHandle(100));
		}
		if(this.getArrowHead2()!=null&&smartHandleBoxes!=null) {
			smartHandleBoxes.add(getArrowHead2().createArrowSizeHandle(200));
		}
	}

	/**returns the key frame animation for this path graphic*/
	public  KeyFrameAnimation getOrCreateAnimation() {
		if (animation instanceof KeyFrameAnimation) return (KeyFrameAnimation) animation;
		animation=new PathGraphicKeyFrameAnimator(this);
		return (KeyFrameAnimation) animation;
	}

	public boolean isUseArea() {
		return useArea;
	}

	public void setUseArea(boolean useArea) {
		this.useArea = useArea;
	}

	/**creates a simple path with a black line*/
	public static PathGraphic blackLine(Point2D[] pts) {
		PathGraphic output = new PathGraphic(pts[0]);
		output.setDashes(new float[] {});output.setStrokeColor(Color.black);
		output.setFillColor(Color.black);
		output.addPoint(pts[1]);
		return output;
	}

	/**when given an area, selects the points inside the area */
	public void selectHandlesInside(Shape selection) {
		if(selection==null) return;
		for(SmartHandle h:getSmartHandleList()) {
			if(h instanceof SmartHandleForPathGraphic) {
				SmartHandleForPathGraphic hp=(SmartHandleForPathGraphic) h;
				if(hp.isAnchorPointHandle()) {
					if(selection.contains(hp.getCordinateLocation())) hp.select();
					else hp.deselect();
				}
			}
		}
		reshapeList2=null;
	}
	
	/**when given an area, deselects the points inside the area */
	public void deselectHandlesInside(Shape selection) {
		if(selection==null) return;
		for(SmartHandle h:getSmartHandleList()) {
			if(h instanceof SmartHandleForPathGraphic) {
				SmartHandleForPathGraphic hp=(SmartHandleForPathGraphic) h;
				if(hp.isAnchorPointHandle()) {
					if(selection.contains(hp.getCordinateLocation())) hp.deselect();
					
				}
			}
		}
		reshapeList2=null;
	}
	
	@Override
	public void draw(Graphics2D g, CordinateConverter<?> cords) {
		super.draw(g, cords);
		this.drawArrowHeads( g,cords);
	}
	

	private void drawArrowHeads(Graphics2D g, CordinateConverter<?> cords) {
		drawArrow(g, cords, 0);
		drawArrow(g, cords, 1);
	}

	private void drawArrow(Graphics2D g, CordinateConverter<?> cords, int i) {
		;
		if (getArrowHead2()!=null)
			{
			this.getArrowHead2().setStrokeWidth(this.getStrokeWidth());
			getArrowHead2().copyColorsFrom(this);
			PathPoint lastPoint = getPoints().getLastPoint();
			getArrowHead2().setPoint2(getTransformPointsForPathGraphic(lastPoint.getAnchor()));
			boolean useCC = lastPoint.getCurveControl2LocationsRelativeToAnchor()[0]>1;
			if (useCC) getArrowHead2().setPoint1(getTransformPointsForPathGraphic(lastPoint.getCurveControl1()));
			else getArrowHead2().setPoint1(getTransformPointsForPathGraphic(getPoints().getPreviousPoint(lastPoint).getCurveControl2()));
			
			getArrowHead2().moveNotchToHead1();
			
			
			getArrowHead2().draw(g, cords);
			}
		
		if (getArrowHead1()!=null)
		{
			this.getArrowHead1().setStrokeWidth(this.getStrokeWidth());
		getArrowHead1().copyColorsFrom(this);
		PathPoint firstPoint = getPoints().get(0);
		getArrowHead1().setPoint2(getTransformPointsForPathGraphic(firstPoint.getAnchor()));
		boolean useCC = firstPoint.getCurveControl2LocationsRelativeToAnchor()[0]>1;
		if (useCC) getArrowHead1().setPoint1(getTransformPointsForPathGraphic(firstPoint.getCurveControl2()));
		else  getArrowHead1().setPoint1(getTransformPointsForPathGraphic(getPoints().getNextPoint(firstPoint).getCurveControl1()));

		getArrowHead1().moveNotchToHead1();
		
		
		getArrowHead1().draw(g, cords);
		}
	}

	public ArrowGraphic getArrowHead1() {
		return arrowHead1;
	}

	/**Adds new arrow heads. creates 1 or 2 arrow heads depending on the number given*/
	public void addArrowHeads(int i) {
		if (i==1) {
			arrowHead1=new ArrowGraphic();
			setupArrowHead(getArrowHead1());
			smartHandleBoxes=null;
			return;
		}
		setArrowHead2(new ArrowGraphic());
		setupArrowHead(getArrowHead2());
		smartHandleBoxes=null;
	}

	/**alters the arrow graphic so that it has the proper appearance for an arrow head on this path*/
	public void setupArrowHead(ArrowGraphic arrowHead) {
		arrowHead.headOnly=true;
		arrowHead.hideNormalHandles=true;
		arrowHead.setArrowHeadSize(this.getStrokeWidth()*8);
	}

	public boolean hasArrowHead1() {
		if (getArrowHead1()==null) return false;
		return true;
	}
	
	public boolean hasArrowHead2() {
		if (getArrowHead2()==null) return false;
		return true;
	}

	public ArrowGraphic getArrowHead2() {
		return arrowHead2;
	}

	public void setArrowHead2(ArrowGraphic arrowHead2) {
		this.arrowHead2 = arrowHead2;
	}

	@Override
	public ArrayList<ZoomableGraphic> getAllHeldGraphics() {
		ArrayList<ZoomableGraphic> output = new ArrayList<ZoomableGraphic>();
		if (arrowHead1!=null && arrowHead1.getHeadnumber()>0)output.add(arrowHead1);
		if (arrowHead2!=null && arrowHead2.getHeadnumber()>0)output.add(arrowHead2);
		return output;
	}

	public void setNArrows(int nArrow) {
		if(nArrow==1) this.addArrowHeads(2);
		if(nArrow==2) {this.addArrowHeads(2);this.addArrowHeads(1);}
	}
	
	/**returns teh shape that will be used as an icon for thiss*/
	ShapeGraphic rectForIcon() {
		PathGraphic createExample = PathGraphic.createExample();
		if(this.hasArrowHead1()) {
			createExample.addArrowHeads(1);
		}
		if(this.hasArrowHead2()) {
			createExample.addArrowHeads(2);
		}
		return  createExample;
	}
	
	/**the angle field is not used by this class*/
	public double getAngle() {return 0;}
	public void setAngle(double angle) {this.angle=0;}

	/**class defines the handle for adding point to the start or end of the path*/
	public class AddPointSmartHandle extends SmartHandle {

		private PathGraphic path;
		private PathPoint addedPoint;
		private boolean toStart;

		public AddPointSmartHandle(int x, int y) {
			super(x, y);
			// TODO Auto-generated constructor stub
		}

		public AddPointSmartHandle(PathGraphic pathGraphic, boolean toStart) {
			this(0,0);
			this.toStart=toStart;
			this.path=pathGraphic;
			this.setSpecialFill(PLUS_FILL);
			Color handleColor = new Color(0,0,0,0);
			this.setHandleColor(handleColor);
			super.handleStrokeColor= handleColor;
			this.decorationColor=Color.blue;
			if(toStart)decorationColor=Color.cyan.darker();
			this.setHandleNumber(990090+(toStart?500:0));
		}

		public Point2D getCordinateLocation() {
			SmartHandleList boxes = path.getPointHandles();
			SmartHandle lastOne = boxes.get(boxes.size()-1);
			if(toStart) lastOne=boxes.get(0);
			
			/**this part finds the most convenient location for the addpoint handle
			*/
			 PathPoint lastPoint = path.getPoints().getLastPoint();
			 if(toStart) lastPoint = path.getPoints().get(0);
				
			 
			java.awt.geom.Point2D.Double previosAnchor = getPoints().getPreviousPoint(lastPoint).getAnchor();
			if(toStart)previosAnchor = getPoints().getNextPoint(lastPoint).getAnchor();
			double[] raddeg = NumberUse.getPointAsRadianDegree(lastPoint.getAnchor(), previosAnchor);
			
			double angle2=raddeg[1]+(Math.PI);
			double step =12;
			
			return new Point2D.Double(lastOne.getCordinateLocation().getX()+step*Math.cos(angle2), lastOne.getCordinateLocation().getY()-step*Math.sin(angle2));
		}
		
		public void handlePress(CanvasMouseEventWrapper canvasMouseEventWrapper) {
			if(toStart) {
				addedPoint = path.addPointToStart(this.getCordinateLocation());
			} else
			addedPoint = path.addPoint(this.getCordinateLocation());
			addedPoint.deselect();
			path.updatePathFromPoints();
		}
		
		public void handleDrag(CanvasMouseEventWrapper w) {
			SmartHandleForPathGraphic handle = new SmartHandleForPathGraphic(path, addedPoint);
			handle.handleDrag(w);
			addedPoint.deselect();
		}
		
		@Override
		public boolean isHidden() {
			if(path.isClosedShape()) return true;
			return false;
		}
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}
	
	@Override
	public AbstractUndoableEdit provideUndoForDialog() {
		return new CombinedEdit(new UndoStrokeEdit(this), new UndoScalingAndRotation(this), new ColorEditUndo(this), new PathEditUndo(this));
	}

}
