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
 * Version: 2021.1
 */
package graphicalObjects_Shapes;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import popupMenusForComplexObjects.ArrowGraphicMenu;
import standardDialog.StandardDialog;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.UndoScalingAndRotation;
import animations.KeyFrameAnimation;
import applicationAdapters.CanvasMouseEvent;
import export.pptx.OfficeObjectConvertable;
import export.pptx.OfficeObjectMaker;
import export.svg.SVGExporter;
import graphicalObjects.CordinateConverter;
import graphicalObjects_SpecialObjects.HasBackGroundShapeGraphic;
import handles.CountHandle;
import handles.HasSmartHandles;
import handles.ReshapeHandleList;
import handles.SmartHandle;
import handles.SmartHandleList;
import handles.miniToolbars.ActionButtonHandleList;
import handles.miniToolbars.ShapeActionButtonHandleList2;
import iconGraphicalObjects.IconTraits;
import icons.IconSet;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import keyFrameAnimators.ArrowGraphicKeyFrameAnimator;
import layersGUI.HasTreeLeafIcon;
import locatedObject.BasicStrokedItem;
import locatedObject.PathPointList;
import locatedObject.RectangleEdges;
import locatedObject.RotatesFully;
import locatedObject.Scales;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import menuUtil.HasUniquePopupMenu;
import objectDialogs.ArrowSwingDialog;
import objectDialogs.StrokeOnlySwingDialog;

/**Defines the shape of a line with one or more arrows heads at its ends
  Many different arrow appearances are possible
 A zero-headed arrow is also possible
 for curved lines with arrow heads @see PathGraphic.
 
 */
public class ArrowGraphic extends ShapeGraphic implements Scales,RotatesFully, HasTreeLeafIcon,HasBackGroundShapeGraphic, HasUniquePopupMenu, OfficeObjectConvertable, HasSmartHandles {

	private static final int STANDARD_HEAD_SIZE = 16;
	private static final double STANDARD_TIP_ANGLE = Math.PI/4, STANDARD_NOTCH_ANGLE = Math.PI*3/4;
	
	/**set to true if an outline of the arrow will be drawn*/
	public static final int NO_OUTLINE=0, OUTLINE_SHAPE=1, OUTLINE_OF_NORMAL_HEAD=20, 
			OUTLINE_OF_OPEN_HEAD=6000;
	private int outline=NO_OUTLINE;
	
	
	/**The choices that appear in the user dialog*/
	
	public static final int NORMAL_HEAD=0, OPEN_HEAD=1, REVERSE_HEAD=2, REVERSE_OPEN_HEAD=3,FEATHER_TAIL=5, FEATHER_TAIL_2=6, SQUARE_HEAD=7, BALL_HEAD=8, LINE_CAP=9,HALF_LINE_HEAD2 = 10, TRIANGLE_HEAD=11, 
			TAIL=12, NARROW_TAIL=13, HALF_CIRCLE_TAIL=14, NO_HEAD=15,
			POLYGON_HEAD=16;
	public static final int DIAMOND_HEAD=POLYGON_HEAD+1, PENTAGON_HEAD=POLYGON_HEAD+2, HEXAGON_HEAD=POLYGON_HEAD+3;	
	
	public static final int[] arrowStyleList=new int[] {
			NORMAL_HEAD, TRIANGLE_HEAD,  POLYGON_HEAD, OPEN_HEAD, SQUARE_HEAD, BALL_HEAD, DIAMOND_HEAD,PENTAGON_HEAD, HEXAGON_HEAD, LINE_CAP, HALF_LINE_HEAD2, REVERSE_HEAD, REVERSE_OPEN_HEAD, TAIL, FEATHER_TAIL, FEATHER_TAIL_2, NARROW_TAIL, HALF_CIRCLE_TAIL, NO_HEAD};
	
	public static final String[] arrowStyleChoices=new String[] {"Normal", "Open Head", "Reverse Head", "Reverse open head", "Outline of head", "Feather Tail", "Fine Feather Tail",  "Square Cap", "Circle Cap", "Line Cap", "Half Line Cap", "Arrow Cap", "Tail", "Narrow Tail","Semi Circle","No Head","Triangle Cap", "Diamond Cap", "Pentagon Cap", "Hexagon Cap"};
	/**some options not available to the user dialog but available to programmer*/
	public static final int 
	 HALF_BAR_HEAD = 400,  HALF_BAR_HEAD2 = 500 ,BAR_HEAD = 444,
	   HALF_LINE_HEAD = 500 ;
	
	
	
	public static final int HANDLE_1=0, HANDLE_2=1, ARROW_SIZE_HANDLE=2, ARROW_STROKE_HANDLE=3, HEAD_NUMBER_HANDLE=4, ARROW_SIZE_HANDLE_2=5;;
	
	public static final int FIRST_HEAD=1, SECOND_HEAD=2;
	
	{name="Arrow"; strokeWidth=4; dash=new float[] {700000000}; angle=0;}
	/**
	 * 
	 */
	
	/**Stores the number of heads that the arrow has.*/
	private CountParameter headnumber=new CountParameter(this, 1, 0,2);
	
	/**set to true if only the arrow head should be drawn and no line*/
	boolean headOnly=false;
	
	private static final long serialVersionUID = 1L;
	
	
	
	
	/**The second point within the arrow. see superclass for 1st point (x, y)*/
	protected double x2=60, y2=60;
	
	/**the first head of the arrow at position x2, y2*/
	private ArrowHead head1=new ArrowHead();
	
	/**the second head of the arrow at position x, y*/
	private ArrowHead head2=new ArrowHead();
	
	/**if true, the properties of the second arrow head will always mimic the first.*/
	private boolean useSameHead=true;
	
	/**The handles*/
	private transient SmartHandleList smartList;
	public boolean hideNormalHandles;//set to true if only the specialized arrow head handle will be shown
	private transient ReshapeHandleList rotList;//a list containing a rotation handle
	private BasicShapeGraphic backGroundShape=null;

	
	/**A simple constructor*/
	public ArrowGraphic() {}

	/**constructor for an arrow stretching from p1 to p2*/
	public ArrowGraphic(Point2D p1, Point2D p2) {
		setPoints(p1,p2);
	}
	
	/**set points p1 and p2*/
	public void setPoints(Point2D p1, Point2D p2) {
		setPoint1(p1);
		setPoint2(p2);
	}
	
	/**the first point. if there is only one head, the head is at the first point*/
	void setPoint1(Point2D p1) {
		x=p1.getX();
		y=p1.getY();
	}
	/**the second point, if there is more than one head, this is the second heads location*/
	void setPoint2(Point2D p2) {
		x2=p2.getX();
		y2=p2.getY();
	}
	/**Creates an arrow outline that is filled with one color and stroked with another.
	  useful for creating icons*/
	public static ArrowGraphic createDefaltOutlineArrow(Color fill, Color stroke) {
		ArrowGraphic ag1=new ArrowGraphic();
		ag1.getHead().setArrowHeadSize(20);
		ag1.setStrokeWidth(8);
		ag1.outline=OUTLINE_SHAPE;
		ag1.getBackGroundShape().setFilled(true);
		ag1.getBackGroundShape().setFillColor(fill);
		ag1.getBackGroundShape().setStrokeColor(stroke);
		ag1.getBackGroundShape().setStrokeJoin(BasicStroke.JOIN_MITER);

		ag1.getBackGroundShape().setDashes(new float[] {10000000});
		
		return ag1;
	}
	
	
	
	/**switches the location of the two points. if the head is at one point, this changed the direction*/
	public void swapDirections() {
		double xo = x;
		double yo = y;
		x=x2;
		y=y2;
		x2=xo;
		y2=yo;
		if (head1!=head2) {
			ArrowHead h = head1; head1=head2; head2=h;
		}
	}
	
	/**returns the location of the notchpoint for the given head*/
	public Point2D getNotchLocation(int h) {
		ArrowHead head = this.getHead(h);
		if (head.processesPoints==null) 
			computePoints();
		
		Point2D[] headPoints = getHeadPoints(h);
		
		return headPoints[1];
	}

	
	/**returns the length of the arrow from one tip to the other*/
	double getArrowLength() {
		return getLocation().distance(getLineEndLocation());
	}
	

	
	/**returns the location of the stroke handle*/
	public Point2D[] getStrokeHandlePoints() {
		/**this math places the handle at the edge of the stroke near the middle of the line*/
		
		Point2D location1 = getLocation();
		Point2D location2 = getLineEndLocation();
		return calculatePointsOnStrokeBetween(location1, location2);
	}
	
	/**performs calculations to determine the points along the arrow heads
	  stores them each*/
	private void computePoints() {
		/**starting with one head's points*/
		if (this.useSameHead) head2.copyAttributesFrom(head1);
	
		computeHead(head1, 1, getArrowLength());
		computeHead(head2, -1, 0);
	
	}

	/**
	 computes the locations of the points on an arrow head.
	 The other two parameters differ depending on the second of first head
	 */
	public void computeHead(ArrowHead head, int headDirection, double locationShift) {
		if (head.rreverseHead()) {
			headDirection*=-1;
			locationShift+=head.getArrowHeadSize()*headDirection;
		}
		
		head.tipPoint=new Point2D.Double(locationShift,0);
		double px = locationShift-head.getArrowHeadSize()*headDirection;
		double py =head.getHalfHeadWidth() ;
		
		if (head.getArrowTipAngle()==Math.PI||head.isBarHead()) {
			py=head.getArrowHeadSize();
			px=locationShift;
		}
		
		
		head.upperend=new Point2D.Double(px, py);
		head.lowerend=new Point2D.Double(px, -py);
		
		if (head.isHalfHead1()) {head.lowerend=new Point2D.Double(px, getStrokeWidth()/2);}
		if (head.isHalfHead2()) {head.upperend=new Point2D.Double(px,-getStrokeWidth()/2);}
		
		
		double pxn = px+py/Math.tan( head.getNotchAngle()/2)*headDirection;
		
		if ( head.isBarHead()) {pxn=px+getStrokeWidth();}
		head.notchpoint=new Point2D.Double(pxn, 0);
		
		
		if(head.overTipShape()) {
			head.notchpoint=new Point2D.Double(locationShift-head.getArrowHeadSize()*headDirection/2, 0);
		}
		if (head.isOpenHeadType()) {
			head.notchpoint=new Point2D.Double(locationShift, 0);
		}
	}



	/**
	 returns the points along the arrow head
	 */
	private Point2D.Double[] getHeadPoints(int headnumber) {
		ArrowHead head=this.getHead(headnumber);
		computePoints();
		Point2D.Double[]  out=new Point2D.Double[4];
		out[0]=head.lowerend;
		out[1]=head.notchpoint;
		out[2]=head.upperend;
		out[3]=head.tipPoint;
		
		head.processesPoints=transform(out);
		return head.processesPoints;
	}

	
	/**when given a set of points, translates and rotates them to the position and angle of this arrow*/
	private Point2D.Double[] transform(Point2D.Double[] out) {
		AffineTransform a=AffineTransform.getTranslateInstance(x, y);
		
		Point2D.Double[]  out2=new Point2D.Double[4];
		a.transform(out, 0, out2, 0, 4);
		
		a=AffineTransform.getRotateInstance(getAngleBetweenPoints(), x, y);
		Point2D.Double[]  out3=new Point2D.Double[4];
		a.transform(out2, 0, out3, 0, 4);
		return out3;
	}
	

	/**creates a path2d that outlines the arrow head
	 * if the arrow style is an open head, the path will
	 * simply be a line*/
	private Path2D.Double getArrowHeadPath(int head,boolean includeNotch, boolean loopstart) {
		
		Path2D.Double output = createArrowHeadPath(head, includeNotch, loopstart);
		
		return output;
	}

	

	/**
	 Creates an arrow head path from the given points
	 */
	public Path2D.Double createArrowHeadPath( int head,boolean includeNotch, boolean loopstart) {
		Double[] points = getHeadPoints(head);
		
		int p1 = 0;
		int p2 = 3;
		int p3 = 2;
		int p4 = 1;
		if(this.getHead(head).getArrowStyle()==REVERSE_OPEN_HEAD) {
			p2=1; p4=3;
		}
		Path2D.Double output=new Path2D.Double();
		
		output.moveTo(points[p1].x, points[p1].y);
	
		output.lineTo(points[p2].x, points[p2].y);
		
		output.lineTo(points[p3].x, points[p3].y);
		
		if (includeNotch) {
			
			Point2D notch = points[p4];
			Point2D n3 = betweenPoint(notch,points[p3], 0.9);
			Point2D n0 = betweenPoint(notch,points[p1], 0.9);
			
			output.lineTo(n3.getX(), n3.getY());	
			output.lineTo(n0.getX(), n0.getY());
			
		}
		if (loopstart) output.closePath();//{output.lineTo(points[0].x, points[0].y);}
		return output;
	}
	


	/**creates a copy of this arrow*/
	@Override
	public ArrowGraphic copy() {
		ArrowGraphic arrow = new ArrowGraphic();
		arrow.copyAttributesFrom(this);
		arrow.copyColorsFrom(this);
		
		arrow.copyArrowAtributesFrom(this);
		arrow.headOnly=headOnly;
		arrow.copyPositionFrom(this);
		arrow.hideNormalHandles=this.hideNormalHandles;
		arrow.outline=outline;
		arrow.backGroundShape=this.getBackGroundShape().copy();
	
		arrow.setHeadsSame(this.headsAreSame());
		arrow.head1.copyAttributesFrom(head1);
		arrow.head2.copyAttributesFrom(head2);
		
		
		return arrow;
	}

	/**
	are the two heads of the arrow identical
	 */
	public boolean headsAreSame() {
		return useSameHead;
	}
	
	/**sets whether the arrow has identical heads*/
	public void setHeadsSame(boolean b) {
		useSameHead=b;
	}
	
	/**Sets the line ends locations to match the arrow given*/
	public void copyPositionFrom(ArrowGraphic arr) {
		x2=arr.x2;
		x=arr.x;
		y=arr.y;
		y2=arr.y2;
	}
	
	/**makes the arrow heads of this arrow identical to the argument*/
	public void copyArrowAtributesFrom(ArrowGraphic arr) {
		
		this.setNumerOfHeads(arr.getNHeads());
		
		
		this.setStrokeWidth(arr.getStrokeWidth());
		this.headOnly=arr.headOnly;
		this.outline=arr.outline;
		this.head1.copyAttributesFrom(arr.head1);
		this.head2.copyAttributesFrom(arr.head2);
		this.setHeadsSame(arr.headsAreSame());
	}

	@Override
	public Point2D getLocation() {
		return new Point2D.Double(x,y);
	}
	
	/**returns the end tip of the arrow*/
	public Point2D.Double getLineEndLocation() {
		return new Point2D.Double(x2,y2);
	}
	
	/**returns the starting tip of the arrow*/
	public Point2D.Double getLineStartLocation() {
		return new Point2D.Double(x,y);
	}
	

	/**sets the location. moves both line ends to maintain the arrows
	  appearance*/
	@Override
	public void setLocation(double x,double y) {
		this.x2+=x-this.x;
		this.y2+=y-this.y;
		this.x=x;
		this.y=y;
		
	}
	
	/**moves the arrow*/
	@Override
	public void moveLocation(double x, double y) {
		moveStartLocation(x, y);
		moveTipLocation(x, y);
	}
	
	/**moves the start of the arrow*/
	public void moveStartLocation(double x, double y) {
		this.x+=x;
		this.y+=y;
	}
	/**moves the end of the arrow*/
	public void moveTipLocation(double x, double y) {
		this.x2+=x;
		this.y2+=y;
	}

	
	/**returns the shape of the arrow*/
	@Override
	public Area getOutline() {
		Area ar = new Area(getStroke().createStrokedShape(getDrawnLineBetweenHeads()));
		if (this.getNHeads()==0) return ar;
		if(headOnly) ar=new Area();
		ArrowHead head = head1;
		Shape a1 = this.getShapeToDrawHead(FIRST_HEAD);
		if (head.isOpenHeadType())
			a1=this.getStroke().createStrokedShape(a1);
		
		ar.add(new Area(a1));
		if (this.getNHeads()==1) 
			return ar;
		
		head = head2;
		a1=getShapeToDrawHead(SECOND_HEAD);
		if (head.isOpenHeadType()) 
			a1=this.getStroke().createStrokedShape(a1);
		ar.add(new Area(a1));
		return ar;
	}

	@Override
	public Rectangle getBounds() {
		return getOutline().getBounds();
	}
	
	/**returns the angle of the arrow*/
	public double getAngleBetweenPoints() {
		double angle=Math.atan(((double)(y2-y))/(x2-x));
		if (!java.lang.Double.isNaN(angle)) {
			if (x2-x<0) angle+=Math.PI;
			this.setAngle(angle);
			}
		return angle;
	}
	
	/**the arrows never have an angle explicitly set, the two points determine the line*/
	public double getAngle() {
		return 0;
	}
	

	/**
	 * @return
	 */
	public ArrowSwingDialog getOptionsDialog() {
		return new ArrowSwingDialog(this, hideNormalHandles?1:0);
	}

	@Override
	public Shape getShape() {
		return this.getOutline();
	
		
	}
	
/**returns a line2D that is drawn from one arrow head to the other*/
private Line2D getDrawnLineBetweenHeads() {
	Line2D.Double line = new Line2D.Double();
	line.setLine(getDrawnLineStart(), getDrawnLineEnd2());
	return line;
}

	/**returns the point where the line part of the arrow will start
	 * this will either be at the notch of the second arrow head or 
	 * at the start of the arrow depending on the arrow head style*/
	private Point2D getLineStart() {
		 Point2D l1=this.getLineStartLocation();
		 if (head2.getNotchAngle()>head2.getArrowTipAngle()) {
		 		if (drawsSecondHead()) l1=this.getNotchLocation(SECOND_HEAD);
	 									}
		 return l1;
		 
	}
	/**returns the point where the line part of arrow will stop.
	  this will be either at the notch of the arrow head, or at the end of the arrow
	  depending on the arrow head style*/
	private Point2D getLineEnd2() {
		 Point2D l2=this.getLineEndLocation();
		 if (head1.getNotchAngle()>head1.getArrowTipAngle()) {
			 		if (drawsFirstHead()) l2=this.getNotchLocation(FIRST_HEAD);
			 		
		 									}
		 return l2;
		 
	}



	public ArrayList<Point2D> getEndPoints() {
		ArrayList<Point2D> out=new ArrayList<Point2D>();
		out.add(new Point2D.Double(x,y));
		out.add(new Point2D.Double(x2,y2));
		return out;
	}
	@Override
	public void setStrokeColor(Color c) {
		super.setStrokeColor(c);
		this.setFillColor(c);
		if(this.outlineDraw()) {
			this.getBackGroundShape().setStrokeColor(c);
			this.getBackGroundShape().setFilled(true);
		}
	}
	
	boolean outlineDraw() {
		if (outline==OUTLINE_SHAPE) return true;
		
		return false;
	}
	
	@Override
	public void draw(Graphics2D g, CordinateConverter cords) {
		
		if (java.lang.Double.isNaN(y)||java.lang.Double.isNaN(x)) return;
		if (java.lang.Double.isInfinite(y)||java.lang.Double.isInfinite(x)) return;
		ArrowHead head = head1;
		if (x==x2&&y==y2) {
			x-=head.getArrowHeadSize();//in the event the points are set to a nonsensical locations
		}
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(this.getFillColor());
		
		//draws the outline shape
		 if (outlineDraw() ) {
			 this.getBackGroundShape().setShape(getOutline());
			 getBackGroundShape().draw(g, cords);
		 }
		 		else
		 			{
		 			//draws the line connecting the heads
			        g.setStroke(cords.getScaledStroke(getStroke()));
			 		if (!headOnly)
			 			getGrahpicUtil().drawLine(g, cords, this.getDrawnLineStart(), getDrawnLineEnd2() , false);			
			 		
			 		//draws the heads
			 		g.setStroke(new BasicStroke());
			 		
			 		
			 		
			 		if (drawsFirstHead()) this.getHead1DrawShape(FIRST_HEAD).draw(g, cords);
			 		if (drawsSecondHead()) this.getHead1DrawShape(SECOND_HEAD).draw(g, cords);
		 			
		 			}
	
		
		
		 if (selected) {
			 this.getSmartHandleList().draw(g, cords);
		 }
		
		 
		 g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			
		   }
	
	
	/**Returns the point that will actually be used to draw a line between arrow heads
	 this takes into account certain arrow head types that are simply lines */
	protected Point2D getDrawnLineStart() {
		 if ( !head2.isOpenHeadType() ) {
			 Point2D p = this.getLineStart();
			 return p;
		 }
		 else return this.getLineStartLocation();
	}
	
	/**Returns the point that will actually be used to draw a line between arrow heads
	 * this takes into account certain arrow head types that are simply lines */
protected Point2D getDrawnLineEnd2() {
	if ( !head1.isOpenHeadType()) {
		 Point2D p = this.getLineEnd2();
		 return p;
	}
	 else return this.getLineEndLocation();
	
	}
	



/**creates a shape graphic that corresponds to an arrow head*/
	private BasicShapeGraphic getHead1DrawShape(int headnumber) {
		BasicShapeGraphic b = getBackGroundShape();
		BasicShapeGraphic out = b;
		
		ArrowHead head = this.getHead(headnumber);
		
		boolean notopen=!head.isOpenHeadType();
		Shape s = getShapeToDrawHead(headnumber);
		if (head.isBarHead()) {
			
		}
		if (head.getArrowStyle()==OPEN_HEAD||head.getArrowStyle()==REVERSE_OPEN_HEAD||head.isLineHead()||outline==OUTLINE_OF_NORMAL_HEAD) {
			out=BasicShapeGraphic.createStroked(this, s);
		}else 
		if (head.getArrowStyle()==NORMAL_HEAD||head.getArrowStyle()==REVERSE_HEAD||head.isTail()||head.isSquareHead()||head.isCircleHead()||head.isTriangleHead()||head.isPolygonHead()||head.halfCircleTail()) {
			out=BasicShapeGraphic.createFilled(getFillColor(), s);
		}
		
		if (outline==OUTLINE_OF_NORMAL_HEAD) {
			out.copyAttributesFrom(b);
		}
		
		
		
		out.setShape(s);
		out.setAntialize(true);
		out.setClosedShape(notopen);
		out.setDashes(new float[] {});
		return out;
	}
	
	
	/**returns the shape of the given arrow head*/
	private  Shape getShapeToDrawHead(int headnumber) {
		ArrowHead head = this.getHead(headnumber);
		
		boolean notopen=!head.isOpenHeadType();
		
		Shape shapeOfHead = this.getArrowHeadPath(headnumber, notopen,notopen);
		
		if(head.isTriangleHead()) shapeOfHead = this.getArrowHeadPath(headnumber, false, notopen);
		
		
		
		
		Point2D pt = this.getNotchLocation(headnumber);
		
		if(head.isSquareHead()||head.isPolygonHead()||head.isTail()||head.halfCircleTail()) {
			java.awt.geom.Rectangle2D.Double r2d = new Rectangle2D.Double(0,0, head.getArrowHeadSize(),head.getArrowHeadSize());	
			
			if(head.isNarrowHead())  
				r2d=new Rectangle2D.Double(0,head.getArrowHeadSize()/4, head.getArrowHeadSize(), head.getArrowHeadSize()/2);	
			
			RectangleEdges.setLocation(r2d, RectangleEdges.CENTER, pt.getX(), pt.getY());
			
			AffineTransform at = AffineTransform.getRotateInstance(this.getAngleBetweenPoints(),  pt.getX(), pt.getY());
			if(headnumber==SECOND_HEAD) at = AffineTransform.getRotateInstance(this.getAngleBetweenPoints()+Math.PI,  pt.getX(), pt.getY());
			
			if(head.isPolygonHead()) {
				RegularPolygonGraphic rp = new RegularPolygonGraphic(r2d);
				rp.setNvertex(head.getArrowStyle()-POLYGON_HEAD+3);//
				shapeOfHead=at.createTransformedShape(rp.getShape());
			} else if (head.halfCircleTail()) {
				
				CircularGraphic a = CircularGraphic.halfCircle(r2d);
				shapeOfHead=at.createTransformedShape(a.getShape());
			} else if (head.isTail()) {
				int nDiv=1;
				if (head.getArrowStyle()==FEATHER_TAIL) nDiv=2;
				if (head.getArrowStyle()==FEATHER_TAIL_2) nDiv=3;
				TailGraphic t = new TailGraphic(r2d,nDiv);
				
				t.setNotchAngle(Math.PI*2/3);
				shapeOfHead=at.createTransformedShape(t.getShape());
			} else
			shapeOfHead=at.createTransformedShape(r2d);
			
			
		}
		if(head.isCircleHead()) {
			java.awt.geom.Ellipse2D.Double r2d = new Ellipse2D.Double(0,0, head.getArrowHeadSize(), head.getArrowHeadSize());
			RectangleEdges.setLocation(r2d, RectangleEdges.CENTER, pt.getX(), pt.getY());
			shapeOfHead=r2d;
		}
		

		return shapeOfHead;
	}


	

	

	public int getNHeads() {
		return headnumber.getValue();
	}
	public void setNumerOfHeads(int headnumber) {
		this.headnumber.setValue(headnumber);
	}

	/**required to generate an illustrator script to create a similar arrow*/
	@Override
	public Object toIllustrator(ArtLayerRef aref) {
		ArtLayerRef aref2 = aref.createSubRefG();
		aref2.setName(getName());
		if ( outlineDraw()) {
			// this.getBackGroundShape().setShape(getOutline());
			// getBackGroundShape().setName(getName());
			 
			return  createPathCopy().toIllustrator(aref2);
			
		 } else {
			 Object output = super.toIllustrator(aref2);
			 if (drawsFirstHead()) this.getHead1DrawShape(FIRST_HEAD).toIllustrator(aref2);
		 		if (drawsSecondHead()) this.getHead1DrawShape(SECOND_HEAD).toIllustrator(aref2);
			return output; 
		 }
	}

	/**
	 * @return
	 */
	public boolean drawsSecondHead() {
		if (head2.isNoHead()) return false;
		return getNHeads()>1;
	}

	/**
	 * @return
	 */
	public boolean drawsFirstHead() {
		if (head1.isNoHead()) return false;
		return getNHeads()>0;
	}
	
	/**required to generate an illustrator script to create a similar arrow*/
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {

		
	pi.createItem(aref);
	Point2D[] pt=new Point2D[2];
	pt[1]=this.getDrawnLineStart();
	pt[0]=this.getDrawnLineEnd2();
	pi.setPointsOnPath(pt, false);
	}
	
	
	
	transient static IconSet arrowIconSet;

	@Override
	public Icon getTreeIcon() {
		return new GraphicDisplayComponent(createIconArrow() );
		//return createImageIcon();
	}
	
	/**creates a small arrow that is used as an icon for the arrow*/
	ArrowGraphic createIconArrow() {
		ArrowGraphic out = ArrowGraphic.createDefaltOutlineArrow(this.getFillColor(), this.getStrokeColor());
		out.setPoints(new Point(0,0), new Point(IconTraits.TREE_ICON_WIDTH-1,IconTraits.TREE_ICON_HEIGHT-1));
		out.copyColorsFrom(this);
		ArrowHead head = this.getHead();
		if (head.isOutlineType()) {
			out.getBackGroundShape().setStrokeColor(this.getBackGroundShape().getStrokeColor());
			out.getBackGroundShape().setFillColor(this.getBackGroundShape().getFillColor());
			out.getBackGroundShape().setStrokeWidth(1);
			
		}
		out.getHead().copyAttributesFrom(getHead());
		out.setHeadsSame(this.headsAreSame());
		ArrowHead secondHead = out.getHead(SECOND_HEAD);
		secondHead.copyAttributesFrom(getHead(SECOND_HEAD));
		
		
		out.setNumerOfHeads(getNHeads());
		
		if (head.isOpenHeadType()||head.isOutlineType())out.getHead().setArrowStyle(head.getArrowStyle());
			out.setStrokeWidth(2);
		out.getHead(FIRST_HEAD).setArrowHeadSize(6);
		secondHead.setArrowHeadSize(6);
		if (secondHead.isTail()) {secondHead.setArrowHeadSize(3);}
		
		return out;
	}

	/**returns the shape that is used for drawing the outline of this arrow if the arrow is in outline mode
	 */
	public BasicShapeGraphic getBackGroundShape() {
		if (backGroundShape==null) {
			backGroundShape=new  BasicShapeGraphic(this.getOutline());
			backGroundShape.setStrokeWidth(2);
			backGroundShape.setStrokeColor(this.getStrokeColor());
			backGroundShape.setAntialize(true);
			backGroundShape.setFillColor(Color.white);
			backGroundShape.setFilled(true);
		}
		return backGroundShape;
	}
	
	public PopupMenuSupplier getMenuSupplier() {
		return new ArrowGraphicMenu(this);
	}
	
	
	@Override
	public void scaleAbout(Point2D p, double mag) {
		try {
		
			Point2D p1 = new Point2D.Double(x,y);
			Point2D p2 = new Point2D.Double(x2,y2);
			p2=scalePointAbout(p2, p,mag,mag);
			p1=scalePointAbout(p1, p,mag,mag);
			this.setPoints(p1, p2);
			BasicStrokedItem.scaleStrokeProps(this, mag);
			
			head1.setArrowHeadSize(head1.getArrowHeadSize()*mag);
			if (head2!=head1)head2.setArrowHeadSize(head2.getArrowHeadSize()*mag);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	/**Overrides the drop color method of the parent. Sets the color*/
	public void dropColor(Color c, Point p) {
			this.setFillColor(c);
			this.setStrokeColor(c);
	}
	
	/**returns a pathGraphic that looks just like this arrow*/
	public PathGraphic createPathCopy() {
		PathPointList list = PathPointList.createFromIterator(this.getOutline().getPathIterator(new AffineTransform()));
		PathGraphic oo = new PathGraphic(list);
		oo.copyColorsFrom(this.getBackGroundShape());
		oo.copyAttributesFrom(this.getBackGroundShape());
		oo.setName(getName());
		oo.setClosedShape(true);
		oo.setUseFilledShapeAsOutline(true);
		//TODO: find out if this works well
		if (!(head1.isOutlineType()||head2.isOutlineType())) {
			oo.setStrokeWidth(-1);
			oo.setFillColor(getStrokeColor());
			
		}
		return oo;
	}
	
	@Override
	public OfficeObjectMaker getObjectMaker() {
		return this.createPathCopy().getObjectMaker();
	}
  
	@Override
	public SVGExporter getSVGEXporter() {
		return  this.createPathCopy().getSVGEXporter();
	}
	
	
	public  KeyFrameAnimation getOrCreateAnimation() {
		if (animation instanceof KeyFrameAnimation) return (KeyFrameAnimation) animation;
		animation=new ArrowGraphicKeyFrameAnimator(this);
		return (KeyFrameAnimation) animation;
	}
	
	@Override
	public SmartHandleList getSmartHandleList() {
		if (smartList==null)smartList=createSmartHandleList(); 
		if (!superSelected||hideNormalHandles) 
			return SmartHandleList.combindLists(smartList);
		return SmartHandleList.combindLists(smartList, getButtonList(), getRotateList());
	}
	
	/**returns a handle list containing a handle for rotation of the arrow*/
	private SmartHandleList getRotateList() {
		if (rotList==null)rotList=new ReshapeHandleList(1,400, this);
		rotList.updateRectangle();
		return rotList;
	}
	
	/**
	Returns an action handle list for the object that acts as a mini toolbar
	 */
	public ActionButtonHandleList getButtonList() {
		if(buttonList==null) {
			buttonList= createActionHandleList();
		}
		buttonList.updateLocation();
		return buttonList;
	}
	
	/**
	Creates an action handle list for the object
	 */
	public ShapeActionButtonHandleList2 createActionHandleList()  {
		ShapeActionButtonHandleList2 buttonList=new ShapeActionButtonHandleList2(this);
		if(this.hideNormalHandles) 	buttonList=new ShapeActionButtonHandleList2(this, ShapeActionButtonHandleList2.ARROW_ONLYFORM);
		return buttonList;
	}
	
	
	/**creates handles specific to the arrows*/
	protected SmartHandleList createSmartHandleList() {
		 SmartHandleList smList = new SmartHandleList();
		 if (!hideNormalHandles) {
				smList.add(new ArrowSmartHandle(HANDLE_1, this));
				smList.add(new ArrowSmartHandle(HANDLE_2, this));
				smList.add(new ArrowSmartHandle(ARROW_STROKE_HANDLE, this));
				//smList.add(createHeadNumberHandle());
		}
		smList.add(createArrowSizeHandle(2));
		smList.add(createArrowSizeHandle2(2));
		
		return smList;
	}
	public CountHandle createHeadNumberHandle() {
		return new CountHandle(this, headnumber, HEAD_NUMBER_HANDLE, 25,2, true, 1);
	}
	public ArrowSmartHandle createArrowSizeHandle(int i) {
		return new ArrowSmartHandle(ArrowSmartHandle.HANDLE_CONTEXT*i+ARROW_SIZE_HANDLE, this);
	}
	public ArrowSmartHandle createArrowSizeHandle2(int i) {
		return new ArrowSmartHandle(ArrowSmartHandle.HANDLE_CONTEXT*i+ARROW_SIZE_HANDLE_2, this);
	}
	
	@Override
	public int handleNumber(double x, double y) {
		return getSmartHandleList().handleNumberForClickPoint(x, y);
	}
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		SmartHandle h = this.getSmartHandleList().getHandleNumber(handlenum);
		if (h!=null) h.handleMove(p1, p2);
	}
	
	/**A handle for the user to modify the arrow*/
class ArrowSmartHandle extends SmartHandle {
		
		/**
		 * 
		 */
		private static final int HANDLE_CONTEXT = 1000;

		public void draw(Graphics2D graphics, CordinateConverter cords) {
			
			super.draw(graphics, cords);
		}

		private ArrowGraphic rect;
		private transient UndoScalingAndRotation undo;
		private transient boolean undoAdded=false;

		public ArrowSmartHandle(int type, ArrowGraphic r) {
		
			this.setHandleNumber(type);
			this.rect=r;
			if(isArrowSizeHandle()) {
				this.setHandleColor(Color.blue);
			}
			if(isArrowSizeHandle2()) {
				this.setHandleColor(Color.blue.darker());
			}
			if(type==ARROW_STROKE_HANDLE) {
				this.setHandleColor(Color.magenta);
			}
		}

		
		
		public Point2D getCordinateLocation() {
			ArrayList<Point2D> ends = getEndPoints();
			if (this.getHandleNumber()==HANDLE_1) return ends.get(0);
			if (this.getHandleNumber()==HANDLE_2) return ends.get(1);
			if (this.isArrowStrokeHandle()) {
				return getStrokeHandlePoints()[0];
			}
			
			
			if (this.isArrowSizeHandle2()) {
				if (head2.isHalfHead2()) return getHeadPoints(SECOND_HEAD)[0];
				return getHeadPoints(SECOND_HEAD)[2];
			}

			if (head1.isHalfHead2()) return getHeadPoints(FIRST_HEAD)[0];
			return getHeadPoints(FIRST_HEAD)[2];
		
		}
		
		
		public void handlePress(CanvasMouseEvent lastDragOrRelMouseEvent) {
			undoAdded=false;
			undo=new UndoScalingAndRotation(rect);
			if (lastDragOrRelMouseEvent.clickCount()==2&&isArrowSizeHandle())
				{
				ArrowHead head = getHead(ArrowGraphic.FIRST_HEAD);
				java.lang.Double p = StandardDialog.getNumberFromUser("Arrow Size", head.getArrowHeadSize());
				head.setArrowHeadSize(p);
				setupUndo(lastDragOrRelMouseEvent);
				}
			
			if (lastDragOrRelMouseEvent.clickCount()==2&&isArrowStrokeHandle()) {
				new StrokeOnlySwingDialog(rect).showDialog();
				setupUndo(lastDragOrRelMouseEvent);
			}
			
			if (lastDragOrRelMouseEvent.clickCount()==2&&this.getHandleNumber()==HANDLE_1) {
				Point2D p = StandardDialog.getPointFromUser("Set Location ",getLineStartLocation());
				setPoint1(p);
				setupUndo(lastDragOrRelMouseEvent);
			}
			
			if (lastDragOrRelMouseEvent.clickCount()==2&&this.getHandleNumber()==HANDLE_2) {
				Point2D p = StandardDialog.getPointFromUser("Set Location ",getLineEndLocation());
				setPoint2(p);
				setupUndo(lastDragOrRelMouseEvent);
			}
			
			}

		protected void setupUndo(CanvasMouseEvent lastDragOrRelMouseEvent) {
			if(undo==null) return;
			undo.establishFinalState();
			if(!undoAdded) {lastDragOrRelMouseEvent.addUndo(undo);
			undoAdded=true;}
		}

		public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
			Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
			if (this.getHandleNumber()==HANDLE_1) {
				setPoint1(p2);
			}
			if (this.getHandleNumber()==HANDLE_2) {
				setPoint2(p2);
			}
			
			if (isArrowSizeHandle()) {
				java.awt.geom.Point2D.Double metric = getHeadPoints(FIRST_HEAD)[3];
				double d = metric.distance(p2);
				getHead(ArrowGraphic.FIRST_HEAD).setArrowHeadSize(d);
			}
			
			if (isArrowSizeHandle2()) {
				java.awt.geom.Point2D.Double metric = getHeadPoints(SECOND_HEAD)[3];
				double d = metric.distance(p2);
				getHead(ArrowGraphic.SECOND_HEAD).setArrowHeadSize(d);
				if(headsAreSame()) getHead(ArrowGraphic.FIRST_HEAD).setArrowHeadSize(d);
			}
			if (isArrowStrokeHandle()) {
				Point2D metric = getStrokeHandlePoints()[1];
				double d = metric.distance(p2);
				rect.setStrokeWidth((float) (d*2));
			}
			setupUndo(lastDragOrRelMouseEvent);
		}
		
		@Override
		public boolean handlesOwnUndo() {return true;}

		public boolean isArrowSizeHandle() {
			return this.getHandleNumber()%HANDLE_CONTEXT==ARROW_SIZE_HANDLE;
		}
		
		public boolean isArrowSizeHandle2() {
			return this.getHandleNumber()%HANDLE_CONTEXT==ARROW_SIZE_HANDLE_2;
		}
		
		public boolean isArrowStrokeHandle() {
			return this.getHandleNumber()==ARROW_STROKE_HANDLE;
		}
	
		public void handleMove(int handlenum, Point p1, Point p2) {
			
			
			
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public boolean isHidden() {
			if (getNHeads()==0 &&isArrowSizeHandle() )
				return true;
			
			if (getNHeads()<2 &&isArrowSizeHandle2() )
				return true;
			return hidden;
		}
		
	}

@Override
public boolean isFillable() {return false;}



/**extends the arrow's length such that the notch will be moved to the previous location of the head */
public void moveNotchToHead1() {
	Point2D p1 = this.getLineEndLocation();
	Point2D n1 = this.getNotchLocation(FIRST_HEAD);
	moveTipLocation(p1.getX()-n1.getX(), p1.getY()-n1.getY());
}

/**rotates the arrow*/
@Override
public void rotateAbout(Point2D c, double distanceFromCenterOfRotationtoAngle) {
	try {
		if(distanceFromCenterOfRotationtoAngle==0) {
			IssueLog.log("returned after arrow rotation attempt failed due to 0 angle");
			return;
			}
		
		AffineTransform a = AffineTransform.getRotateInstance(distanceFromCenterOfRotationtoAngle, c.getX(), c.getY());
		
		Point2D p2 = this.getLineEndLocation();
		Point2D p1 = this.getLineStartLocation();
		a.transform(p2, p2);
		a.transform(p1, p1);
		this.setPoint1(p1);
		this.setPoint2(p2);
		
	} catch (Exception e) {
		
		e.printStackTrace();
	}
}

/**overrides the methods in the interface but implementation is not crucial for arrows*/
@Override
public void setFillBackGround(boolean fillBackGround) {
	
}

public int drawnAsOutline() {
	return outline;
}

public void setDrawAsOutline(int outline) {
	this.outline = outline;
}

/**The class arrow head defines the properties of the arrow head*/
public class ArrowHead implements Serializable {

	public Double tipPoint;
	/**
	 * 
	 */
	/**The variables that determine the appearance of the arrow head*/
	private int style=NORMAL_HEAD;
	private double arrowTipAngle=STANDARD_TIP_ANGLE;
	private double notchAngle=STANDARD_NOTCH_ANGLE;
	private double arrowHeadSize=STANDARD_HEAD_SIZE;
	
	/**As the methods calculate where different parts of the arrow are. values are stored here*/
	/**One heads points*/
	Point2D.Double upperend=null;
	Point2D.Double lowerend=null;
	Point2D.Double notchpoint=null;
	private Point2D.Double[] processesPoints;
	
	private static final long serialVersionUID = 1L;
	
	public int getArrowStyle() {
		return style;
	}
	public void setArrowStyle(int style) {
		this.style = style;
	}

	public ArrowHead copy() {
		ArrowHead output = new ArrowHead();
		output.copyAttributesFrom(this);
		
		return output;
	}

	public void copyAttributesFrom(ArrowHead arr) {
		this.setArrowHeadSize(arr.getArrowHeadSize());
		this.setArrowTipAngle(arr.getArrowTipAngle());
		this.setNotchAngle(arr.getNotchAngle());
		this.setArrowStyle(arr.getArrowStyle());
		
	}
	
	public double getArrowHeadSize() {
		return arrowHeadSize;
	}

	/**sets the size of the arrow head. must be a positive number*/
	public void setArrowHeadSize(double d) {
		this.arrowHeadSize = d;
	}
	
	public double getHalfHeadWidth() {
		return Math.tan( getArrowTipAngle()/2)*getArrowHeadSize();
	}
	
	boolean isOpenHeadType() {
		if (this.getArrowStyle()==OPEN_HEAD) return true;
		if (this.getArrowStyle()==REVERSE_OPEN_HEAD) return true;
		if (this.getArrowStyle()==OUTLINE_OF_OPEN_HEAD) return true;
		
		if(isLineHead()) return true;
		if (this.isBarHead()) return true;
		return false;
	}
	boolean isOutlineType() {
		return outline==OUTLINE_OF_NORMAL_HEAD||outline==OUTLINE_OF_OPEN_HEAD||outline==ArrowGraphic.OUTLINE_SHAPE;
	}

	
	public double getArrowTipAngle() {
		if(isLineHead()) { 
			return Math.PI;
		}
		
		return arrowTipAngle;
	}

	public void setArrowTipAngle(double arrowTipAngle) {
		this.arrowTipAngle = arrowTipAngle;
	}

	public double getNotchAngle() {
		if(this.isCircleHead()||this.isSquareHead()||this.isTriangleHead()) return Math.PI;
		return notchAngle;
	}
	public void setNotchAngle(double notchAngle) {
		this.notchAngle = notchAngle;
	}
	private boolean isTriangleHead() {
		return this.getArrowStyle()==TRIANGLE_HEAD;
	}

	/**returns true if the stroke join is relevant to the appearance of the arrow*/
	public boolean doesJoins() {
		if(this.style==OPEN_HEAD) return true;
		if(this.style==REVERSE_OPEN_HEAD) return true;
		return false;
	}
	
	private boolean isHalfHead1() {
		if (HALF_BAR_HEAD==this.getArrowStyle()) return true;
		if (HALF_LINE_HEAD==this.getArrowStyle()) return true;
		return false;
	}
	
	private boolean isHalfHead2() {
		if (HALF_BAR_HEAD2==this.getArrowStyle()) return true;
		if (HALF_LINE_HEAD2==this.getArrowStyle()) return true;
		return false;
	}

	/**
	 * @return
	 */
	public boolean rreverseHead() {
		if (getArrowStyle()==REVERSE_HEAD) return true;
		return this.getArrowStyle()==REVERSE_OPEN_HEAD;
	}
	
	/**returns true for certain arrow head shapes that will be placed above the tip of the line*/
	boolean overTipShape() {
		if(this.isSquareHead()) return true;
		if(this.isCircleHead()) return true;
		if(this.isPolygonHead()) return true;
		if(this.isTriangleHead()) return true;
		if (halfCircleTail()) return true;
		if(this.isTail()) return true;
		return false;
	}

	/**
	 * @return
	 */
	public boolean halfCircleTail() {
		return this.getArrowStyle()==HALF_CIRCLE_TAIL;
	}
	
	private boolean isTail() {
		if(this.getArrowStyle()==FEATHER_TAIL) return true;
		if(this.getArrowStyle()==FEATHER_TAIL_2) return true;
		if (isNarrowHead()) return true;
		return this.getArrowStyle()==TAIL;
	}

	private boolean isCircleHead() {
		return this.getArrowStyle()==BALL_HEAD;
	
	}
	private boolean isSquareHead() {
		return this.getArrowStyle()==SQUARE_HEAD;
		}
	
	private boolean isPolygonHead() {
		return this.getArrowStyle()>=POLYGON_HEAD;
		}
	
	public boolean isLineHead() {
		if (this.getArrowStyle()==HALF_LINE_HEAD) return true;
		if (this.getArrowStyle()==HALF_LINE_HEAD2) return true;
		return this.getArrowStyle()==LINE_CAP;
	}
	
	/**
	 * @return
	 */
	public boolean isNarrowHead() {
		return this.getArrowStyle()==NARROW_TAIL;
	}
	
	boolean isBarHead() {
		if (this.getArrowTipAngle()==Math.PI&&this.getNotchAngle()==Math.PI) return true;
		if (this.getArrowStyle()==BAR_HEAD) return true;
		if (this.getArrowStyle()==HALF_BAR_HEAD) return true;
		if (this.getArrowStyle()==HALF_BAR_HEAD2) return true;
		return false;
	}
	
	boolean isNoHead() {return getArrowStyle()==NO_HEAD;}

}

public ArrowHead getHead() {
	return head1;
}

/**returns the selected head*/
public ArrowHead getHead(int i) {
	if (i==SECOND_HEAD)
		return head2;
	if (i==FIRST_HEAD)
		return head1;
	
	
	return head1;
}


/**Creates an arrow outline without heads stretching from one point to the other
 * that is filled with one color and stroked with another.
  useful for creating icons*/
public static ArrowGraphic createLine(Color fill, Color stroke, Point2D p1, Point2D p2) {
	ArrowGraphic output = ArrowGraphic.createDefaltOutlineArrow(fill, stroke);
	output.setNumerOfHeads(0);
	output.setPoints(p1, p2);
	return output;
}

@Override
public String getShapeName() {
	return "Arrow";
}





}
