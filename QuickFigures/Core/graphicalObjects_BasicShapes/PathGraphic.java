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
import undo.CompoundEdit2;
import undo.PathEditUndo;
import undo.UndoScaling;
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
	private Path2D path=new Path2D.Float();
	
	
	public static final int anchorHandleOnlyMode=0, ThreeHandelMode=2, TwohandleMode=1, linkedHandleMode=3, allSelectedHandleMode=4, symetricHandleMode=5;
	private int handleMode=ThreeHandelMode; 
	
	ArrayList<HandleRect> leftHandles=new ArrayList<HandleRect>();
	ArrayList<HandleRect> rightHandles=new ArrayList<HandleRect>();
	
	ArrowGraphic arrowHead1=null;
	private ArrowGraphic arrowHead2=null;

	private transient SmartHandleList smartHandleBoxes;

	private Shape outline;

	private boolean useArea=false;
	
	ArrayList<Point2D> getAnchorPoints() {
		ArrayList<Point2D> out=new ArrayList<Point2D>();
		for(PathPoint p:getPoints()) {out.add(p.getAnchor());}
		return out;
	}
	
	ArrayList<Point2D> getLeftPoints() {
		ArrayList<Point2D> out=new ArrayList<Point2D>();
		for(PathPoint p:getPoints()) {out.add(p.getCurveControl1());}
		return out;
	}
	
	ArrayList<Point2D> getRightPoints() {
		ArrayList<Point2D> out=new ArrayList<Point2D>();
		for(PathPoint p:getPoints()) {out.add(p.getCurveControl2());}
		return out;
	}
	
	public ArrayList<Point2D> getTranslatedVersion(ArrayList<Point2D> in, double x, double y) {
		ArrayList<Point2D> out=new ArrayList<Point2D>();
		for(Point2D p:in) {out.add(new Point2D.Double(p.getX()+x, p.getY()+y));}
		return out;
	}
	
	
	
	
	void setAnchorPoints(ArrayList<Point2D> in) {
		PathPointList out=new PathPointList();
		for(Point2D p:in) {out.add(new PathPoint(p));}
		setPoints(out);
	}
	
	
	
	void copyAnchorPointsFrom(PathGraphic in) {
		this.setAnchorPoints(in.getAnchorPoints());
		//this.setLeftPoints(in.getLeftPoints());
	}
	
	@Override
	public int handleNumber(int x, int y) {
		if (this.getSmartHandleBoxes()!=null) {
			
			int output=getSmartHandleList().handleNumberForClickPoint(x, y);
			
			return output;
		}
		
		return -1;

	}
	
	
	
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
	
	public void setLocationInnitial(Point2D p) {
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
	
	public PathGraphic createPathCopy() {
		return copy();
	}

	/**Add point takes the overall cordinate not the one within the path
	 * @return */
	public PathPoint addPoint(Point2D p) {
		Point2D p2 = convertPointToInternalCrdinates(p);
		PathPoint output = getPoints().addPoint(p2);
		path.lineTo(p2.getX(), p2.getY());
		return output;
	}
	
	/**Add point takes the overall cordinate not the one within the path
	 * @return */
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
	  intricate series of stroked shapes for line outline necesary*/
	@Override
	public Shape getOutline() {
		if (outline==null) outline=createOutline() ;
		return outline ;
	}
	
	
	private Shape createOutline() {
		// TODO Auto-generated method stub
		if (isUseFilledShapeAsOutline()||this.getPoints().size()>25) return getShape();
		
		float strokeWidth2 = this.getStrokeWidth();
		if(strokeWidth2<0)strokeWidth2=0;
		Shape shape = new BasicStroke(strokeWidth2).createStrokedShape(getShape());
		Area a=new Area(shape);
		a.add(new Area(new BasicStroke(12).createStrokedShape(a)));
	
		return getRotationTransform().createTransformedShape(a);
	}
	
	public static Polygon shapeToPolygon(PathIterator s) {
		PathIterator pi = s;
		double[] d=new double[6];
		Polygon poly = new Polygon();
		
		while (!pi.isDone()) {
			pi.currentSegment(d);
			//if (d[0]==0&& d[1]==0) {} else
			poly.addPoint((int)d[0], (int)d[1]);
			
			pi.next();
		}
		return poly;
	}
	
	public PathIterator getPathIterator() {
		return getShape().getPathIterator(new AffineTransform());
	}
	
	public static ArrayList<Point2D> shapeToArray(PathIterator s) {
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
		// TODO Auto-generated method stub
		return getShape().getBounds();
	}

	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		if (this.getSmartHandleBoxes()==null) return;
		SmartHandle thehandle = this.getSmartHandleBoxes().getHandleNumber(handlenum);
		if (thehandle!=null)thehandle.handleMove(p1, p2);
		
		/**
		if (isCurvemode()) {
		if (handlenum<2000&&handlenum>=1000) {
			moveLeftTo(handlenum-1000, p2);
			
		}
		
		
		if (this.isSupercurvemode()&&handlenum<3000&&handlenum>=2000)
					moveRightTo(handlenum-2000, p2);
		
		
		}
			if (handlenum<1000)movePointTo(handlenum, p2);
		*/
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
			if (getSmartHandleBoxes()==null) setSmartHandleBoxes(SmartHandleForPathGraphic.getPathSmartHandles(this));
			getSmartHandleBoxes().draw(g2d, cords);
		
		   }
		 getGrahpicUtil().setHandleFillColor(Color.GRAY); 
		
	}
	
	

	public Path2D getPath() {
		return path;
	}

	
	public void setPath(Path2D path2d) {
		this.path = path2d;
		this.setAnchorPoints(shapeToArray(path2d.getPathIterator(new AffineTransform())));
	}
	
	public void setPathToShape(Shape path2d) {
		
		this.setAnchorPoints(shapeToArray(path2d.getPathIterator(new AffineTransform())));
		this.updatePathFromPoints();
	}
	
	
	public void updatePathFromPoints(){
		this.path=getPoints().createPath(this.isClosedShape());//updatePathFromPoints(this.getPoints(), isClosedShape());
		outline=null;
		setSmartHandleBoxes(null);
		reshapeList2=null;
	}
	

	
	public boolean isCurvemode() {
		if(getHandleMode()==allSelectedHandleMode) return false;
		if (getHandleMode()>0) return true;
		return false;
		//return curvemode;
	}

	

	public boolean isSupercurvemode() {
		if (getHandleMode()==PathGraphic.ThreeHandelMode) return true;
		if (getHandleMode()==PathGraphic.linkedHandleMode) return true;
		if (getHandleMode()==PathGraphic.symetricHandleMode) return true;
		return false;
	}

	public void setSupercurvemode(boolean supercurvemode) {
		setHandleMode(PathGraphic.ThreeHandelMode);//.TwohandleMode;
	}
	
	public boolean isDrawClosePoint() {
		return false;
	}

	public PathPointList getPoints() {
		return points;
	}

	public void setPoints(PathPointList points) {
		this.points = points;
		this.updatePathFromPoints();
	}
	
	
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
	/**
	public void updateDisplay() {
		IssueLog.log("was asked to update display"+this.setContainer);
		super.updateDisplay();
	}*/
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
		

	}
	
	public void moveLocation(double x, double y) {
		super.moveLocation(x, y);
		outline=null;//so a new outline will be created next time its needed
		reshapeList2=null;
	}
	
	@Override
	public void scaleAbout(Point2D p, double mag) {
		p=this.convertPointToInternalCrdinates(p);
		
		//Point2D p2 = this.getLocation();
		AffineTransform af = new AffineTransform();
		af.translate(p.getX(), p.getY());
		af.scale(mag, mag);
		af.translate(-p.getX(), -p.getY());
		//p2=scaleAbout(p2, p,mag,mag);
		BasicStrokedItem.scaleStrokeProps(this, mag);
		getPoints().applyAffine(af);
		//this.setLocation(p2);
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
	
	
	
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		if (this.isCompleteMoveToIlls()) {
			pi.addPathWithCurves(aref, this.getPoints(), true, isDrawClosePoint());
			 pi.translate(x, y);
			//IssueLog.log("trying experimental illustrator export on "+this);
		} else
			pi.createPathWithoutCurves(aref, getShape());
		
		
		 if (this.isClosedShape()) pi.setClosed(true);
		 pi.setName(this.getName());
		}
	
	boolean compound=true;

	
	
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
	
	
	/**creates the given pathpoint list to illustrator*/
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
	
	/**This the the transform that transforma the pathPoint list points into the path graphic's actual cordiantes that \
	  will be displayed*/
	public AffineTransform getTransformForPathGraphic() {
		
		AffineTransform output =getRotationTransform();
		output.concatenate( AffineTransform.getTranslateInstance(getLocation().getX(), getLocation().getY()));
	//	output.concatenate(getRotationTransform());
		return output;
	}

	@Override
	public SmartHandleList getSmartHandleList() {
		// TODO Auto-generated method stub
		if ( getSmartHandleBoxes()==null) {
			setSmartHandleBoxes(new SmartHandleList());
		}
		if (this.superSelected) return SmartHandleList.combindLists(getSmartHandleBoxes(), getButtonList(), getReshapeList(), getReshapeList2(),getAddPointList());
		return  getSmartHandleBoxes();
	}

	
	private transient SmartHandleList addPointList;
	private SmartHandleList getAddPointList() {
		if (addPointList==null)addPointList=SmartHandleList.createList(new AddPointSmartHandle(this, false),new AddPointSmartHandle(this, true));
		return addPointList;
	}

	private transient ReshapeHandleList reshapeList;
	public transient ReshapeHandleList reshapeList2;
	private ReshapeHandleList getReshapeList() {
		if(reshapeList==null)reshapeList=new ReshapeHandleList(0, this);
		reshapeList.updateRectangle();
		return reshapeList;
	}

	private ReshapeHandleList getReshapeList2() {
		if(this.getPoints().getSelectedPointsOnly().size()<2) return null;
		if(reshapeList2==null)reshapeList2=new PathPointReshapeList( 90000000, this);
		reshapeList2.updateRectangle();
		return reshapeList2;
	}
	
	
	/**creates an uncurved path that resembles this curve*/
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

	public void rotateAbout(Point2D clickedCord, double distanceFromCenterOfRotationtoAngle) {
		Point2D pointCenter=convertPointToInternalCrdinates(clickedCord);
		AffineTransform at = AffineTransform.getRotateInstance(distanceFromCenterOfRotationtoAngle, pointCenter.getX(), pointCenter.getY());
		this.getPoints().applyAffine(at);
		this.updatePathFromPoints();
		
	}

	public synchronized SmartHandleList getSmartHandleBoxes() {
		if (smartHandleBoxes==null) {
			setSmartHandleBoxes(SmartHandleForPathGraphic.getPathSmartHandles(this));
		
		}
		return smartHandleBoxes;
	}

	public void setSmartHandleBoxes(SmartHandleList smartHandleBoxes) {
		if(this.smartHandleBoxes == smartHandleBoxes)return;
		this.smartHandleBoxes = smartHandleBoxes;
		if(this.arrowHead1!=null&&smartHandleBoxes!=null) {
			smartHandleBoxes.add(arrowHead1.createArrowSizeHandle(100));
		}
		if(this.getArrowHead2()!=null&&smartHandleBoxes!=null) {
			smartHandleBoxes.add(getArrowHead2().createArrowSizeHandle(200));
		}
	}

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

	public static PathGraphic blackLine(Point2D[] pts) {
		PathGraphic output = new PathGraphic(pts[0]);
		output.setDashes(new float[] {});output.setStrokeColor(Color.black);
		output.setFillColor(Color.black);
		output.addPoint(pts[1]);
		return output;
	}

	public void selectHandlesInside(Rectangle2D selection) {
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
	
	
	public void deselectHandlesInside(Rectangle2D selection) {
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
	
	
	@Override
	public void deselect() {
		super.deselect();
		//for(PathPoint p:this.getPoints()) {p.deselect();}
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
	public void setAngle(double angle) {	this.angle=0;}

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
			SmartHandleList boxes = path.getSmartHandleBoxes();
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
		return new CompoundEdit2(new UndoStrokeEdit(this), new UndoScaling(this), new ColorEditUndo(this), new PathEditUndo(this));
	}

}
