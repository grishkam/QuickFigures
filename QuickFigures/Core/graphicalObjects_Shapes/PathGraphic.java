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
 * Date Modified: Mar 5, 2021
 * Version: 2023.2
 */
package graphicalObjects_Shapes;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import javax.swing.undo.UndoableEdit;

import animations.KeyFrameAnimation;
import applicationAdapters.CanvasMouseEvent;
import export.svg.SVGEXporterForShape;
import export.svg.SVGExporter;
import export.svg.SVGExporter_ShapeList;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicHolder;
import handles.HasSmartHandles;
import handles.PathPointReshapeList;
import handles.ReshapeHandleList;
import handles.SmartHandle;
import handles.SmartHandleForPathGraphic;
import handles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.CompoundPathItemRef;
import illustratorScripts.IllustratorObjectRef;
import illustratorScripts.PathItemRef;
import keyFrameAnimators.PathGraphicKeyFrameAnimator;
import locatedObject.BasicStrokedItem;
import locatedObject.PathObject;
import locatedObject.PathPoint;
import locatedObject.PathPointList;
import locatedObject.ScalesFully;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import objectCartoon.ShapeMaker;
import popupMenusForComplexObjects.PathGraphicMenu;
import undo.AbstractUndoableEdit2;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.PathEditUndo;
import undo.UndoArrowHeadAttachment;
import undo.UndoScalingAndRotation;
import undo.UndoStrokeEdit;
import utilityClasses1.NumberUse;

/**A shape graphic for an arbitrary shape defined by a Path2D
 * a curved path or shape. can contain any number of points*/
public class PathGraphic extends ShapeGraphic implements PathObject, ScalesFully ,HasSmartHandles, GraphicHolder {
	


	{name="Path"; this.setHasCloseOption(true);}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int NO_SEGMENT_SELECTED=-1;
	/**Which path segment is currently selected*/
	public int selectedsegmentindex=NO_SEGMENT_SELECTED;
	
	private boolean useFilledShapeAsOutline=false;
	
	/**The list of points within the path*/
	private PathPointList points=new PathPointList();
	
	/**a Path2D object for drawing this PathGraphic on a graphics2D. This item is updated when certain methods make changes to the pathpoint list */
	private Path2D path=new Path2D.Float();
	
	/**constants determine how handles are used and which handles are visible and how mouse drags on the handles work*/
	public static final int ANCHOR_HANDLE_ONLY_MODE=0, THREE_HANDLE_MODE=2, TWO_HANDLE_MODE=1, CURVE_CONTROL_HANDLES_LINKED=3, MOVE_ALL_SELECTED_HANDLES=4, CURVE_CONTROL_SYMETRIC_MODE=5;
	protected int handleMode=THREE_HANDLE_MODE; 
	
	
	
	/**some paths will end with an arrow head.*/
	ArrowGraphic arrowHead1=null,
						arrowHead2=null;

	protected transient SmartHandleList smartHandleBoxes;

	/**outline is the area that a user may click on to select the path. */
	private Shape outline;
	private boolean useArea=false;//determines whether the outline is the area enclosed by the path
	
	/**creates a path graphic from the given path2d*/
	public PathGraphic(Path2D path2d) {
		this.setPath(path2d);
	}
	
	/**creates a path graphic from a pathpoint list*/
	public PathGraphic(PathPointList path) {
		this.setPoints(path);
	}
	
	/**creates a path from a list of anchor points*/
	public PathGraphic(Point2D... path) {
		if (path.length==0) return;
		setLocationInnitial(path[0]);
		for(int i=1; i<path.length;i++) {
			addPoint(path[i]);
		}
	}

	/**creates a path graphic with the given point as its location*/
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
	
	/**returns the handle number for the point*/
	@Override
	public int handleNumber(double x, double y) {
		if (this.getPointHandles()!=null) {
			
			int output=getSmartHandleList().handleNumberForClickPoint(x, y);
			
			return output;
		}
		
		return NO_HANDLE;

	}
	
	
	/**used to set the first point when initializing a new path*/
	private void setLocationInnitial(Point2D p) {
		this.setLocation(p.getX(), p.getY());
		getPoints().add(new PathPoint(0,0)); 
		
		path.moveTo(0, 0);
	}

	/**makes a copy*/
	@Override
	public PathGraphic copy() {
		return createIdenticalPath();
	}

	/**returns a path that looks the same as this one
	 * @return
	 */
	public PathGraphic createIdenticalPath() {
		PathGraphic output = createHeadlessCopy();
		if (this.arrowHead1!=null) output.arrowHead1=this.getArrowHead1().copy();
		if (this.arrowHead2!=null) output.arrowHead2=this.getArrowHead2().copy();
		
		return output;
	}

	/**returns a copy that lacks arrow heads
	 * @return
	 */
	public PathGraphic createHeadlessCopy() {
		PathGraphic output = new PathGraphic(getPath());
		output.setName(getName());
		copyColorAttributeTo(output);
		output.setLocation(getLocation());
		output.setPoints(getPoints().copy());
		output.setClosedShape(this.isClosedShape());
		output.updatePathFromPoints();
		return output;
	}
	
	/**returns a group that contains a headless version of this path and two detached heads*/
	public GraphicGroup createCopyWithDetachedHeads() {
		GraphicGroup newpath = new GraphicGroup();
		newpath.setName(getName());
		ArrowGraphic h1 = getArrowHead1();
		ArrowGraphic h2 = getArrowHead2();
		newpath.getTheInternalLayer().add(createHeadlessCopy());
		if(h1!=null)
			{this.prepareArrowHead1();
			newpath.getTheInternalLayer().add(h1.copy());
			}
		if(h2!=null) {
			this.prepareArrowHead2();
			newpath.getTheInternalLayer().add(h2.copy());
			
			}
		return newpath;
	}
	
	/**since this subclass is already a path, its path copy is just a normal copy*/
	public PathGraphic createPathCopy() {
		return createIdenticalPath();
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
	public PathPoint convertPointToInternalCrdinates(PathPoint p2) {
		p2=p2.copy();
		p2.move(-x, -y);
		return p2;
		}
	
	/**when given a point, converts the cordinate to the paths internally used
	 * cordinates system. this involves a translation, and a rotation*/
	public Point2D.Double convertPointToInternalCrdinates(Point2D p2) {
		if (this.getAngle() ==0) return new Point2D.Double(p2.getX()-x, p2.getY()-y);
		/**Point2D ptranslate = new Point2D.Double(p.getX()-x, p.getY()-y);
		try{getRotationTransform().transform(ptranslate,  ptranslate);}  catch (Throwable r) {
			r.printStackTrace();
		}
		return ptranslate;*/
		java.awt.geom.Point2D.Double p = new Point2D.Double();
		try{getTransformForPathGraphic().inverseTransform(p2, p);} catch (Throwable t) {t.printStackTrace();}
		return p;
		}
	
	/**when given a point, converts the cordinate to the paths internally used
	 * cordinates system. this involves a translation, and a rotation*/
	public PathPoint convertPointToExternalCrdinates(PathPoint p2) {
		p2=p2.copy();
		p2.move(x, y);
		return p2;
		}
	
	/**converts the location from one within this path graphics coordinate system
	  to the equivalent in the global coordinate system*/
	public Point2D.Double convertPointToExternalCrdinates(Point2D p2) {
		if (getAngle()==0) return new Point2D.Double(p2.getX()+x, p2.getY()+y);
		return null;
		}
	

	
	
	
	/**converts the location from one within this path graphics coordinate system
	  to the equivalent in the global coordinate system*/
	public Point2D getTransformPointsForPathGraphic(Point2D p) {
		AffineTransform aa = getTransformForPathGraphic();
		Double output = new Point2D.Double();
		aa.transform(p, output);
		return output;
	}
	
	/**Determines whether to close the path at the end or leave it open*/
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
	
	/***/
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		if (this.getPointHandles()==null) return;
		SmartHandle thehandle = this.getPointHandles().getHandleNumber(handlenum);
		if (thehandle!=null)thehandle.handleMove(p1, p2);
	}

	@Override
	public Shape getShape() {
		if (this.isUseArea()) {
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
	
	
	
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter cords) {
		if (selected) {
			if (getPointHandles()==null) setSmartHandleBoxes(SmartHandleForPathGraphic.getPathSmartHandles(this));
			getPointHandles().draw(g2d, cords);
		
		   }
		 getGrahpicUtil().setHandleFillColor(Color.GRAY); 
		
	}
	
	
	/**returns the Path2D that is used to draw this shape*/
	public Path2D getPath() {
		return path;
	}

	/**Sets the current Path2D*/
	private void setPath(Path2D path2d) {
		this.path = path2d;
		this.setPathToAnchorPoints(shapeToArray(path2d.getPathIterator(new AffineTransform())));
	}
	
	/**When given a shape, extracts the anchor points and makes them
	 * this paths anchor points. curve control points are not copied*/
	public void setAnchorPointsTo(Shape path2d) {
		
		this.setPathToAnchorPoints(shapeToArray(path2d.getPathIterator(new AffineTransform())));
		this.updatePathFromPoints();
	}
	
	/**updates the stored path2D that is used to draw this graphic*/
	public void updatePathFromPoints(){
		this.path=getPoints().createPath(this.isClosedShape());//updatePathFromPoints(this.getPoints(), isClosedShape());
		outline=null;
		setSmartHandleBoxes(null);
		reshapeListForSelectedPoints=null;
	}
	
	/**returns true if handles for curve control points are currently usable*/
	public boolean isCurvemode() {
		if(getHandleMode()==MOVE_ALL_SELECTED_HANDLES) return false;
		if (getHandleMode()>0) return true;
		return false;
	}

	
	/**returns true if the second of the two curve control points is to be visible*/
	public boolean isSuperCurveControlMode() {
		if (getHandleMode()==PathGraphic.THREE_HANDLE_MODE) return true;
		if (getHandleMode()==PathGraphic.CURVE_CONTROL_HANDLES_LINKED) return true;
		if (getHandleMode()==PathGraphic.CURVE_CONTROL_SYMETRIC_MODE) return true;
		return false;
	}
	/**sets whether multiple handles for curve control points will be used*/
	public void setSupercurvemode(boolean supercurvemode) {
		setHandleMode(PathGraphic.THREE_HANDLE_MODE);
	}
	
	/**overrides superclass method*/
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
	

	/**returns true if the outline takes the form of a filled shape */
	public boolean isUseFilledShapeAsOutline() {
		return useFilledShapeAsOutline;
	}
	/**set to true for the outline takes the form of a filled shape */
	public void setUseFilledShapeAsOutline(boolean useFilledShapeAsOutline) {
		this.useFilledShapeAsOutline = useFilledShapeAsOutline;
	}

	/**determines what popup menu matches this shape*/
	public PopupMenuSupplier getMenuSupplier() {
		return new PathGraphicMenu(this);
	}
	
	
	@Override
	public Point2D getLocationUpperLeft() {
		Rectangle b = getBounds();
		return new Point(b.x, b.y);
	}

	@Override
	public void setLocationUpperLeft(double x, double y) {
		Point2D p = getLocationUpperLeft() ;
		super.moveLocation(x-p.getX(), y-p.getY());
		outline=null;//so a new outline will be created next time its needed
		reshapeListForSelectedPoints=null;
	}
	
	public void moveLocation(double x, double y) {
		super.moveLocation(x, y);
		outline=null;//so a new outline will be created next time its needed
		reshapeListForSelectedPoints=null;
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
		if(hasArrows()) {
			
			return createCopyWithDetachedHeads().toIllustrator(aref);
		}
		
			if (secs.size()==1) {
					Object illustrator = super.toIllustrator(aref);
					//addArrowsToIllustratorLayers(aref);
					return illustrator;
		
			}
			IllustratorObjectRef wantspathITems;
			
			
	
			CompoundPathItemRef ci=new CompoundPathItemRef();
			
			
			
			ci.createItem(aref);
			wantspathITems=ci;
			
	
		for(PathPointList s: secs) {
			createShapeIllustrator(wantspathITems,s);
		}
		//addArrowsToIllustratorLayers(aref);
		
		return wantspathITems;
		
	}

	/**
	 * @return
	 */
	public boolean hasArrows() {
		return this.getArrowHead1()!=null||this.getArrowHead2()!=null;
	}

	/**
	 * @param aref
	 */
	void addArrowsToIllustratorLayers(ArtLayerRef aref) {
		if(this.getArrowHead1()!=null) {
			IssueLog.log("head 1 in ilustrator");
			this.getArrowHead1().toIllustrator(aref);
			}
		if(this.getArrowHead2()!=null) {
			this.getArrowHead2().toIllustrator(aref);
			}
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
	
	/**This the transform that is used to translate the list of points into the path graphic's coordiantes on the canvas that 
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
		if (this.superSelected) 
			return SmartHandleList.combindLists(getPointHandles(), getButtonList(), getReshapeList(), getReshapeList2(),getAddPointList());
		return  getPointHandles();
	}

	/**The add point list conists of two handles */
	private transient SmartHandleList addPointList;
	
	/**Creates handles to add points to the line*/
	protected SmartHandleList getAddPointList() {
		if (addPointList==null)addPointList=SmartHandleList.createList(new AddPointSmartHandle(this, false),new AddPointSmartHandle(this, true));
		return addPointList;
	}

	/**The reshape handle list contains points for rotation and scaling of the path point list*/
	private transient ReshapeHandleList reshapeList;
	protected ReshapeHandleList getReshapeList() {
		if(reshapeList==null)reshapeList=new ReshapeHandleList(0, this);
		reshapeList.updateRectangle();
		return reshapeList;
	}

	/**the second reshape handle list contains handles for rotating, moving and scaling only a subset of points*/
	public transient ReshapeHandleList reshapeListForSelectedPoints;

	/**the center of rotation that will be used for some operations*/
	public transient PathPoint center_rotation_on;
	
	private ReshapeHandleList getReshapeList2() {
		if(this.getPoints().getSelectedPointsOnly().size()<2) return null;
		if(reshapeListForSelectedPoints==null)reshapeListForSelectedPoints=new PathPointReshapeList( 90000000, this);
		reshapeListForSelectedPoints.updateRectangle();
		return reshapeListForSelectedPoints;
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

	/**returns the handles for the points in the path */
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
		reshapeListForSelectedPoints=null;
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
		reshapeListForSelectedPoints=null;
	}
	
	@Override
	public void draw(Graphics2D g, CordinateConverter cords) {
		super.draw(g, cords);
		this.drawArrowHeads( g,cords);
	}
	
	/**draws the arrow heads*/
	private void drawArrowHeads(Graphics2D g, CordinateConverter cords) {
		drawArrow(g, cords, 1);
		drawArrow(g, cords, 2);
	}

	/**draws arrow heads*/
	private void drawArrow(Graphics2D g, CordinateConverter cords, int i) {
		;
		if (i==2&&getArrowHead2()!=null)
			{
					prepareArrowHead2();
					
					
					getArrowHead2().draw(g, cords);
			}
		
		if (i==1&&getArrowHead1()!=null)
		{
				prepareArrowHead1();
				
				
				getArrowHead1().draw(g, cords);
		}
	}

	/**
	 Formats the internal arrow for display at one end of this path
	 */
	public void prepareArrowHead1() {
		if(this.getArrowHead1()==null)
			return;
		this.getArrowHead1().setStrokeWidth(this.getStrokeWidth());
		getArrowHead1().copyColorsFrom(this);
		PathPoint firstPoint = getPoints().get(0);
		getArrowHead1().setPoint2(getTransformPointsForPathGraphic(firstPoint.getAnchor()));
		boolean useCC = firstPoint.getCurveControl2LocationsRelativeToAnchor()[0]>1;
		if (useCC) getArrowHead1().setPoint1(getTransformPointsForPathGraphic(firstPoint.getCurveControl2()));
		else  getArrowHead1().setPoint1(getTransformPointsForPathGraphic(getPoints().getNextPoint(firstPoint).getCurveControl1()));

		getArrowHead1().moveNotchToHead1();
	}

	/**
	 Formats the internal arrow for display at one end of this path
	 */
	public void prepareArrowHead2() {
		this.getArrowHead2().setStrokeWidth(this.getStrokeWidth());
		getArrowHead2().copyColorsFrom(this);
		PathPoint lastPoint = getPoints().getLastPoint();
		getArrowHead2().setPoint2(getTransformPointsForPathGraphic(lastPoint.getAnchor()));
		boolean useCC = lastPoint.getCurveControl2LocationsRelativeToAnchor()[0]>1;
		if (useCC) 
			getArrowHead2().setPoint1(getTransformPointsForPathGraphic(lastPoint.getCurveControl1()));
		else {
			PathPoint previousPoint = getPoints().getPreviousPoint(lastPoint);
			Double newPP = previousPoint.getCurveControl2();
			getArrowHead2().setPoint1(getTransformPointsForPathGraphic(newPP));
		}
		
		getArrowHead2().moveNotchToHead1();
	}

	public ArrowGraphic getArrowHead1() {
		return arrowHead1;
	}

	/**Adds a new arrow heads. creates the first or the second arrow head depending on the number given*/
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
		arrowHead.getHead().setArrowHeadSize(this.getStrokeWidth()*8);
	}
	/**returns true if this path has an arrow head at position 1*/
	public boolean hasArrowHead1() {
		if (getArrowHead1()==null) return false;
		return true;
	}
	/**returns true if this path has an arrow head at position 2*/
	public boolean hasArrowHead2() {
		if (getArrowHead2()==null) return false;
		return true;
	}

	public ArrowGraphic getArrowHead2() {
		return arrowHead2;
	}

	public void setArrowHead2(ArrowGraphic arrowHead) {
		this.arrowHead2 = arrowHead;
	}
	public void setArrowHead1(ArrowGraphic arrowHead) {
		this.arrowHead1 = arrowHead;
	}
	
	/**returns all items that are part of this object but 
	 * may be clicked on separately*/
	@Override
	public ArrayList<ZoomableGraphic> getAllHeldGraphics() {
		ArrayList<ZoomableGraphic> output = new ArrayList<ZoomableGraphic>();
		if (arrowHead1!=null && arrowHead1.getNHeads()>0)output.add(arrowHead1);
		if (arrowHead2!=null && arrowHead2.getNHeads()>0)output.add(arrowHead2);
		return output;
	}

	/**sets up the number of arrow heads*/
	public void setNArrows(int nArrow) {
		if(nArrow==1) this.addArrowHeads(2);
		if(nArrow==2) {this.addArrowHeads(2);this.addArrowHeads(1);}
	}
	
	/**returns the shape that will be used as an icon for this*/
	ShapeGraphic shapeUsedForIcon() {
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
	/**this class does not use the angle*/
	public boolean doesHaveRotationAngle() {
		return false;
	}

	/**class defines the handle for adding point to the start or end of the path*/
	public class AddPointSmartHandle extends SmartHandle {

		private PathGraphic path;
		private PathPoint addedPoint;
		private boolean toStart;

		
		/**Creates a handle for adding points
		 * @param toStart determines whether the handle is at the start or end of a path*/
		public AddPointSmartHandle(PathGraphic pathGraphic, boolean toStart) {
			
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

		/**returns the location of the point addition handle based on the 
		 * location of the last handle (or first handle)
		  in the path*/
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
		
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			if(toStart) {
				addedPoint = path.addPointToStart(this.getCordinateLocation());
			} else
			addedPoint = path.addPoint(this.getCordinateLocation());
			addedPoint.deselect();
			path.updatePathFromPoints();
		}
		
		public void handleDrag(CanvasMouseEvent w) {
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
	
	/**generates the undoable edit that can be used to undo actions of the dialog*/
	@Override
	public AbstractUndoableEdit2 provideUndoForDialog() {
		return new CombinedEdit(new UndoStrokeEdit(this), new UndoScalingAndRotation(this), new ColorEditUndo(this), new PathEditUndo(this));
	}

	@Override
	public String getShapeName() {
		return "Path";
	}
	
	/**Called when the user exports to SVG*/
	@Override
	public SVGExporter getSVGEXporter() {
		if(hasArrows()) {
			return new SVGExporter_ShapeList(this.getName(), this.createHeadlessCopy(), this.getArrowHead1(), this.getArrowHead2());
		} 
		
		return new SVGEXporterForShape(this);
	}

	/**What to do when a delete of the arrow heads is demanded*/
	@Override
	public UndoableEdit requestDeleteOfHeldItem(Object z) {
		if(z==null)
			return null;
		if(z==this.getArrowHead1()) {
			return UndoArrowHeadAttachment.removeHead(this,getArrowHead1());
		}
		if(z==this.getArrowHead2()) {
			return UndoArrowHeadAttachment.removeHead(this,getArrowHead2());
		}
		return null;
	}
	
	/**switches the two arrow heads*/
	public void flipArrowHeads() {
		ArrowGraphic a1 = getArrowHead1();
		ArrowGraphic a2 = getArrowHead2();
		setArrowHead2(a1);
		setArrowHead1(a2);
	}

}
