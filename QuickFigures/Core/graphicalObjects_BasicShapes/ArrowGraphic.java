package graphicalObjects_BasicShapes;


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
import java.util.ArrayList;

import javax.swing.Icon;
import officeConverter.OfficeObjectConvertable;
import officeConverter.OfficeObjectMaker;
import popupMenusForComplexObjects.ArrowGraphicMenu;
import standardDialog.GraphicDisplayComponent;
import standardDialog.StandardDialog;
import undo.UndoScalingAndRotation;
import utilityClassesForObjects.BasicStrokedItem;
import utilityClassesForObjects.PathPointList;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.RotatesFully;
import utilityClassesForObjects.Scales;
import animations.KeyFrameAnimation;
import applicationAdapters.CanvasMouseEventWrapper;
import externalToolBar.IconSet;
import fieldReaderWritter.SVGExporter;
import graphicalObjectHandles.CountHandle;
import graphicalObjectHandles.HasSmartHandles;
import graphicalObjectHandles.ReshapeHandleList;
import graphicalObjectHandles.ShapeActionButtonHandleList2;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects.CordinateConverter;
import graphicalObjects.HasBackGroundShapeGraphic;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import keyFrameAnimators.ArrowGraphicKeyFrameAnimator;
import layersGUI.HasTreeLeafIcon;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import menuUtil.HasUniquePopupMenu;
import objectDialogs.ArrowSwingDialog;
import objectDialogs.StrokeOnlySwingDialog;

/**Draws an arrow*/
public class ArrowGraphic extends ShapeGraphic implements Scales,RotatesFully, HasTreeLeafIcon,HasBackGroundShapeGraphic, HasUniquePopupMenu, OfficeObjectConvertable, HasSmartHandles {

	private static final int STANDARD_HEAD_SIZE = 16;
	private static final double STANDARD_TIP_ANGLE = Math.PI/4, STANDARD_NOTCH_ANGLE = Math.PI*3/4;
	public static final int normalHead=0, openHead=1,outline=2, outlineHeads=3, openOutlineHeads=4, openOutline=5, barHead = 6, squareHead=7, ballhead=8, lineHead=9, triangleHead=10, polygonHead=11;;
	public static final String[] arrowStyleChoices=new String[] {"Normal", "Open Head", "Outline", "Outline Heads", "Open Outline Heads", "Outline of Open Head", "Bar Head", "Square Cap", "Circle Cap", "Line Cap", "Arrow Cap", "Triangle Cap", "Diamond Cap", "Pentagon Cap", "Hexagon Cap"};
	public static final int HANDLE_1=0, HANDLE_2=1, ARROW_SIZE_HANDLE=2, ARROW_STROKE_HANDLE=3, HEAD_NUN_HANDLE=4;;
	
	
	{name="Arrow"; strokeWidth=4; dash=new float[] {700000000}; angle=0;}
	/**
	 * 
	 */
	
	private CountParameter headnumber=new CountParameter(this, 1, 0,2);
	
	/**set to true if only the arrow head should be drawn*/
	boolean headOnly=false;
	
	private static final long serialVersionUID = 1L;
	
	/**The variables that determine the appearance of the arrow*/
	private int style=normalHead;
	private double arrowTipAngle=STANDARD_TIP_ANGLE;
	private double notchAngle=STANDARD_NOTCH_ANGLE;
	private double arrowHeadSize=STANDARD_HEAD_SIZE;
	
	/**The second point within the arrow. see superclass for 1st point*/
	protected double x2=60, y2=60;//
	
	Point2D.Double upperend=null;
	Point2D.Double lowerend=null;
	
	
	
	/**As the methods calculate where different parts of the arrow are. values are stored here*/
	Point2D.Double notchpoint=null;
	private Point2D.Double[] processesPoints;
	Point2D.Double notchpoint2=null;
	private Point2D.Double[] processesPoints2;
	private Point2D.Double upperend2;
	private Point2D.Double lowerend2;
	private Double[] processesPoints3;
	

	/**The handles*/
	private transient SmartHandleList smartList;
	public boolean hideNormalHandles;//set to true if only the specialized arrow head handle will be shown
	private transient ReshapeHandleList rotList;//a list containing a rotation handle
	private BasicShapeGraphic backGroundShape=null;
	
	
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
	void setPoint2(Point2D p2) {
		x2=p2.getX();
		y2=p2.getY();
	}
	/**Creates an arrow outline that is filled with one color and stroked with another.
	  useful for creating icons*/
	public static ArrowGraphic createDefaltOutlineArrow(Color fill, Color stroke) {
		ArrowGraphic ag1=new ArrowGraphic();
		ag1.setArrowHeadSize(20);
		ag1.setStrokeWidth(8);
		ag1.setArrowStyle(ArrowGraphic.outline);
		ag1.getBackGroundShape().setFilled(true);
		ag1.getBackGroundShape().setFillColor(fill);
		ag1.getBackGroundShape().setStrokeColor(stroke);
		ag1.getBackGroundShape().setStrokeJoin(BasicStroke.JOIN_MITER);

		ag1.getBackGroundShape().setDashes(new float[] {10000000});
		
		return ag1;
	}
	
	/**Creates an arrow outline without heads stretching from one point to the other
	 * that is filled with one color and stroked with another.
	  useful for creating icons*/
	public static ArrowGraphic createLine(Color fill, Color stroke, Point2D p1, Point2D p2) {
		ArrowGraphic output = ArrowGraphic.createDefaltOutlineArrow(fill, stroke);
		output.setHeadnumber(0);
		output.setPoints(p1, p2);
		return output;
	}
	
	/**switches the location of the two points. if the head is at one point, this changed the direction*/
	public void swapDirections() {
		double xo = x;
		double yo = y;
		x=x2;
		y=y2;
		x2=xo;
		y2=yo;
	}
	
	public Point2D getNotchLocation() {
		if (processesPoints==null)  computePoints();
		return getHeadPoints()[1];
	}
	public Point2D getNotchLocation2() {
		if (processesPoints==null)  computePoints();
		return getHeadPoints2()[1];
	}
	
	public Point2D getNotchLocation( int i) {
		if(i==2) return getNotchLocation2();
		
		return  getNotchLocation();
	}
	
	double getArrowLength() {
		return getLocation().distance(getTipLocation());
	}
	

	
	/**returns the location of the stroke handle*/
	public Point2D[] getStrokeHandlePoints() {
		/**this math places the handle at the edge of the stroke near the middle of the line*/
		
		Point2D location1 = getLocation();
		Point2D location2 = getTipLocation();
		return calculatePointsOnStrokeBetween(location1, location2);
	}
	
	/**performs calculations to determine the points along the arrow*/
	private void computePoints() {
	
		double px = getArrowLength()-getArrowHeadSize();
		double py = Math.tan( getArrowTipAngle()/2)*getArrowHeadSize();
		if (this.getArrowTipAngle()==Math.PI||isBarHead()) {
			py=this.getArrowHeadSize();
			px=getArrowLength();
		}
		upperend=new Point2D.Double(px, py);
		lowerend=new Point2D.Double(px, -py);
		double pxn = px+py/Math.tan( getNotchAngle()/2);
		if (this.isBarHead()) {pxn=px+this.getStrokeWidth();}
		notchpoint=new Point2D.Double(pxn, 0);
		
		if(overTipShape()) {
			notchpoint=new Point2D.Double(getArrowLength()-getArrowHeadSize()/2, 0);
		}
		if (isOpenHeadType()) {
			notchpoint=new Point2D.Double(getArrowLength(), 0);
		}
		
		px=getArrowHeadSize();
		if (this.getArrowTipAngle()==Math.PI||isBarHead()) {
			px=0;
		}
		upperend2=new Point2D.Double(px, py);
		lowerend2=new Point2D.Double(px, -py);
		pxn =px-py/Math.tan( getNotchAngle()/2);
		if (this.isBarHead()) {pxn=px-getStrokeWidth();}
		notchpoint2=new Point2D.Double(pxn, 0);
		
		if(overTipShape()) {
			
			notchpoint2=new Point2D.Double(getArrowHeadSize()/2, 0);
		}
		if (isOpenHeadType()) {
			notchpoint2=new Point2D.Double(0, 0);
		}
	}
	
	/**returns true for certain arrow head shapes that will be placed above the tip of the line*/
	boolean overTipShape() {
		if(this.isSquareHead()) return true;
		if(this.isCircleHead()) return true;
		if(this.isPolygonHead()) return true;
		if(this.isTriangleHead()) return true;
		return false;
	}
	
	private boolean isCircleHead() {
		return this.getArrowStyle()==ballhead;
	
	}
	private boolean isSquareHead() {
		return this.getArrowStyle()==squareHead;
		}
	
	private boolean isPolygonHead() {
		return this.getArrowStyle()>=polygonHead;
		}
	
	/**returns the points of the first head*/
	public Point2D.Double[] getHeadPoints() {
		computePoints();
		Point2D.Double[]  out=new Point2D.Double[4];
		out[0]=lowerend;
		out[1]=notchpoint;
		out[2]=upperend;
		out[3]=new Point2D.Double(getArrowLength(),0);
		processesPoints=transform(out);
		return processesPoints;
	}
	
	/**returns the points of the second head*/
	public Point2D.Double[] getHeadPoints2() {
		computePoints();
		Point2D.Double[]  out=new Point2D.Double[4];
		out[0]=lowerend2;
		out[1]=notchpoint2;
		out[2]=upperend2;
		out[3]=new Point2D.Double(0,0);
		processesPoints2=transform(out);
		return processesPoints2;
	}
	
	
	
	public Point2D.Double[] getOutLinePoints() {
		computePoints();
		Point2D.Double[]  out=new Point2D.Double[4];
		out[0]=new Point2D.Double(0,this.getStrokeWidth()/2);
		out[1]=new Point2D.Double(getArrowLength(),this.getStrokeWidth()/2);
		out[2]=new Point2D.Double(getArrowLength(),-this.getStrokeWidth()/2);
		out[3]=new Point2D.Double(0,-this.getStrokeWidth()/2);
		processesPoints3=transform(out);
		return processesPoints3;
	}
	
	public Path2D.Double  getOutLinePointsPath() {
		Double[] points = getOutLinePoints();
		Path2D.Double output=new Path2D.Double();
		output.moveTo(points[0].x, points[0].y);
		output.lineTo(points[3].x, points[3].y);
		output.lineTo(points[2].x, points[2].y);
		output.lineTo(points[1].x, points[1].y);
		return output;
	}
	
	public Point2D.Double[] transform(Point2D.Double[] out) {
		AffineTransform a=AffineTransform.getTranslateInstance(x, y);
		
		Point2D.Double[]  out2=new Point2D.Double[4];
		a.transform(out, 0, out2, 0, 4);
		
		a=AffineTransform.getRotateInstance(getAngleBetweenPoints(), x, y);
		Point2D.Double[]  out3=new Point2D.Double[4];
		a.transform(out2, 0, out3, 0, 4);
		return out3;
	}
	

	
	public Path2D.Double getArrowHeadPath1(boolean includeNotch, boolean loopstart) {
		Double[] points = getHeadPoints();
		Path2D.Double output=new Path2D.Double();
		output.moveTo(points[0].x, points[0].y);
		output.lineTo(points[3].x, points[3].y);
		output.lineTo(points[2].x, points[2].y);
		if (includeNotch)output.lineTo(points[1].x, points[1].y);
		if (loopstart)  output.closePath();//{output.lineTo(points[0].x, points[0].y);}
		
		return output;
	}

	public Path2D.Double getArrowHeadPath2(boolean includeNotch, boolean loopstart) {
		Double[] points = getHeadPoints2();
		Path2D.Double output=new Path2D.Double();
		output.moveTo(points[0].x, points[0].y);
		output.lineTo(points[3].x, points[3].y);
		output.lineTo(points[2].x, points[2].y);
		if (includeNotch)output.lineTo(points[1].x, points[1].y);
		if (loopstart) output.closePath();//{output.lineTo(points[0].x, points[0].y);}
		return output;
	}
	
	

	@Override
	public ArrowGraphic copy() {
		ArrowGraphic arrow = new ArrowGraphic();
		arrow.copyAttributesFrom(this);
		arrow.copyColorsFrom(this);
		
		arrow.copyArrowAtributesFrom(this);
		arrow.headOnly=headOnly;
		arrow.copyPositionFrom(this);
		arrow.hideNormalHandles=this.hideNormalHandles;
		arrow.backGroundShape=this.getBackGroundShape().copy();

		return arrow;
	}
	
	public void copyPositionFrom(ArrowGraphic arr) {
		x2=arr.x2;
		x=arr.x;
		y=arr.y;
		y2=arr.y2;
	}
	
	public void copyArrowAtributesFrom(ArrowGraphic arr) {
		this.setArrowTipAngle(arr.getArrowTipAngle());
		this.setNotchAngle(arr.getNotchAngle());
		this.setHeadnumber(arr.getHeadnumber());
		this.setArrowHeadSize(arr.getArrowHeadSize()); 
		this.setArrowStyle(arr.getArrowStyle());
		this.setStrokeWidth(arr.getStrokeWidth());
		this.headOnly=arr.headOnly;
	}

	@Override
	public Point2D getLocation() {
		return new Point2D.Double(x,y);
	}
	
	public Point2D.Double getTipLocation() {
		return new Point2D.Double(x2,y2);
	}
	
	public Point2D.Double getOppositeTipEndLocation() {
		return new Point2D.Double(x,y);
	}

	public Point2D.Double getTipLocation( int i) {
		if(i==2) return this.getOppositeTipEndLocation();
		
		return this.getTipLocation();
	}
	

	@Override
	public void setLocation(double x,double y) {
		this.x2+=x-this.x;
		this.y2+=y-this.y;
		this.x=x;
		this.y=y;
		
	}
	
	@Override
	public void moveLocation(double x, double y) {
		moveStartLocation(x, y);
		moveTipLocation(x, y);
	}
	public void moveStartLocation(double x, double y) {
		this.x+=x;
		this.y+=y;
	}
	public void moveTipLocation(double x, double y) {
		this.x2+=x;
		this.y2+=y;
	}

	
	/**returns the shape of the arrow*/
	@Override
	public Area getOutline() {
		Area ar = new Area(getStroke().createStrokedShape(getDrawnLineBetweenHeads()));
		if (this.getHeadnumber()==0) return ar;
		if(headOnly) ar=new Area();
		Shape a1 = this.getShapeToDrawHead(1);
		if (this.isOpenHeadType())
			a1=this.getStroke().createStrokedShape(a1);
		
		ar.add(new Area(a1));
		if (this.getHeadnumber()==1) 
			return ar;
		
		a1=getShapeToDrawHead(2);
		if (this.isOpenHeadType()) 
			a1=this.getStroke().createStrokedShape(a1);
		ar.add(new Area(a1));
		return ar;
	}

	@Override
	public Rectangle getBounds() {
		return getOutline().getBounds();
	}
	
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

	@Override
	public void showOptionsDialog() {
		ArrowSwingDialog ad = new ArrowSwingDialog(this, hideNormalHandles?1:0);
		ad.showDialog();
	}

	@Override
	public Shape getShape() {
		return this.getOutline();
	
		
	}
	

private Line2D getDrawnLineBetweenHeads() {
	Line2D.Double line = new Line2D.Double();
	line.setLine(getDrawnLineEnd1(), getDrawnLineEnd2());
	return line;
}
	/**
	private Line2D getLineBetweenHeads() {
		Line2D.Double line = new Line2D.Double();
		line.setLine(this.getLineEnd1(), this.getLineEnd2());
		return line;
	}*/
	
	private Point2D getLineEnd1() {
		 Point2D l1=this.getOppositeTipEndLocation();
		 if (this.getNotchAngle()>this.getArrowTipAngle()) {
		 		
		 		if (getHeadnumber()>1) l1=this.getNotchLocation2();
	 									}
		 return l1;
		 
	}
	
	private Point2D getLineEnd2() {
		 Point2D l2=this.getTipLocation();
		 if (this.getNotchAngle()>this.getArrowTipAngle()) {
			 		if (getHeadnumber()>0) l2=this.getNotchLocation();
			 		
		 									}
		 return l2;
		 
	}

	public double getArrowHeadSize() {
		return arrowHeadSize;
	}

	public void setArrowHeadSize(double d) {
		this.arrowHeadSize = d;
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
	}
	
	boolean outlineDraw() {
		if (getArrowStyle()==outline) return true;
		if (getArrowStyle()==openOutline) return true;
		return false;
	}
	
	@Override
	public void draw(Graphics2D g, CordinateConverter<?> cords) {
		
		if (java.lang.Double.isNaN(y)||java.lang.Double.isNaN(x)) return;
		if (java.lang.Double.isInfinite(y)||java.lang.Double.isInfinite(x)) return;
		
		if (x==x2&&y==y2) {
			x-=this.getArrowHeadSize();//in the event the points are set to a nonsensical locations
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
			 		if (!headOnly)getGrahpicUtil().drawLine(g, cords, this.getDrawnLineEnd1(), getDrawnLineEnd2() , false);			
			 		
			 		//draws the heads
			 		g.setStroke(new BasicStroke());
			 		
			 		
			 		
			 		if (getHeadnumber()>0) this.getHead1DrawShape(1).draw(g, cords);//getGrahpicUtil().fillPolygon(g, cords, getHeadPoints(), false);
			 		if (getHeadnumber()>1) this.getHead1DrawShape(2).draw(g, cords);//getGrahpicUtil().fillPolygon(g, cords, getHeadPoints2(), false);
		 			
		 			}
	
		
		
		 if (selected) {
		// getGrahpicUtil().drawHandlesAtPoints(g, cords, getEndPoints());
		 //this.handleBoxes= getGrahpicUtil().lastHandles;
			 this.getSmartHandleList().draw(g, cords);
		 }
		 //super.drawHandesSelection(g, cords);
		 
		 g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			
		   }
	
	protected Point2D getDrawnLineEnd1() {
		 if ( !isOpenHeadType()) return this.getLineEnd1();
		 else return this.getTipLocation();
	}
	
protected Point2D getDrawnLineEnd2() {
	if ( !isOpenHeadType()) return this.getLineEnd2();
	 else return this.getOppositeTipEndLocation();
	}
	




	private BasicShapeGraphic getHead1DrawShape(int headnumber) {
		BasicShapeGraphic out = getBackGroundShape();
		
		boolean notopen=!isOpenHeadType();
		Shape s = getShapeToDrawHead(headnumber);
		if (isBarHead()) {
			
		}
		if (this.getArrowStyle()==openHead||isLineHead()) {
			out=BasicShapeGraphic.createStroked(this, s);
		}else 
		if (this.getArrowStyle()==normalHead||this.isSquareHead()||this.isCircleHead()||this.isTriangleHead()||this.isPolygonHead()) {
			out=BasicShapeGraphic.createFilled(getFillColor(), s);
		}
		
		
		
		out.setShape(s);
		out.setAntialize(true);
		out.setClosedShape(notopen);
		out.setDashes(new float[] {});
		return out;
	}
	public boolean isLineHead() {
		return this.getArrowStyle()==lineHead;
	}
	
	private  Shape getShapeToDrawHead(int headnumber) {
		boolean notopen=!isOpenHeadType();
		
		Shape shapeOfHead = this.getArrowHeadPath1(notopen,notopen);
		if (headnumber==2) shapeOfHead=this.getArrowHeadPath2(notopen,notopen);
		
		if(this.isTriangleHead()) shapeOfHead = this.getArrowHeadPath1(false, notopen);
		if(this.isTriangleHead()&&headnumber==2) shapeOfHead = this.getArrowHeadPath2(false, notopen);
		
		
		Point2D pt = this.getNotchLocation(headnumber);//.getTipLocation(headnumber);
		//Point2D pt2=this.getTipLocation(headnumber);
		//Point2D pt=super.midPoint(pt1, pt2);
		if(this.isSquareHead()||this.isPolygonHead()) {
			java.awt.geom.Rectangle2D.Double r2d = new Rectangle2D.Double(0,0, this.getArrowHeadSize(), this.getArrowHeadSize());	
			RectangleEdges.setLocation(r2d, RectangleEdges.CENTER, pt.getX(), pt.getY());
			
			AffineTransform at = AffineTransform.getRotateInstance(this.getAngleBetweenPoints(),  pt.getX(), pt.getY());
			if(headnumber==2) at = AffineTransform.getRotateInstance(this.getAngleBetweenPoints()+Math.PI,  pt.getX(), pt.getY());
			
			if(this.isPolygonHead()) {
				RegularPolygonGraphic rp = new RegularPolygonGraphic(r2d);
				rp.setNvertex(this.getArrowStyle()-polygonHead+3);
				shapeOfHead=at.createTransformedShape(rp.getShape());
			} else 
			shapeOfHead=at.createTransformedShape(r2d);
			
			
		}
		if(this.isCircleHead()) {
			java.awt.geom.Ellipse2D.Double r2d = new Ellipse2D.Double(0,0, this.getArrowHeadSize(), this.getArrowHeadSize());
			RectangleEdges.setLocation(r2d, RectangleEdges.CENTER, pt.getX(), pt.getY());
			shapeOfHead=r2d;
		}
		

		return shapeOfHead;
	}
	
	boolean isBarHead() {
		if (this.getArrowTipAngle()==Math.PI&&this.getNotchAngle()==Math.PI) return true;
		if (this.getArrowStyle()==barHead) return true;
		
		return false;
	}
	

	
	boolean isOpenHeadType() {
		if (this.getArrowStyle()==openHead) return true;
		if (this.getArrowStyle()==openOutlineHeads) return true;
		if (this.getArrowStyle()==openOutline) return true;
		if(isLineHead()) return true;
		if (this.isBarHead()) return true;
		return false;
	}
	boolean isOutlineType() {
		return this.getArrowStyle()==outlineHeads||this.getArrowStyle()==openOutlineHeads||getArrowStyle()==openOutline||this.getArrowStyle()==outline;
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

	private boolean isTriangleHead() {
		return this.getArrowStyle()==triangleHead;
	}
	public void setNotchAngle(double notchAngle) {
		this.notchAngle = notchAngle;
	}
	public int getHeadnumber() {
		return headnumber.getValue();
	}
	public void setHeadnumber(int headnumber) {
		this.headnumber.setValue(headnumber);
	}

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
			 if (getHeadnumber()>0) this.getHead1DrawShape(1).toIllustrator(aref2);
		 		if (getHeadnumber()>1) this.getHead1DrawShape(2).toIllustrator(aref2);
			return output; 
		 }
	}
	
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {

		
	pi.createItem(aref);
	Point2D[] pt=new Point2D[2];
	pt[1]=this.getDrawnLineEnd1();
	pt[0]=this.getDrawnLineEnd2();
	pi.setPointsOnPath(pt, false);
	}
	
	public int getArrowStyle() {
		return style;
	}
	public void setArrowStyle(int style) {
		this.style = style;
	}
	
	transient static IconSet i;//=new IconSet("icons2/TextIcon.jpg");

	@Override
	public Icon getTreeIcon() {
		return new GraphicDisplayComponent(createIconArrow() );
		//return createImageIcon();
	}
	
	ArrowGraphic createIconArrow() {
		ArrowGraphic out = ArrowGraphic.createDefaltOutlineArrow(this.getFillColor(), this.getStrokeColor());
		out.setPoints(new Point(0,0), new Point(14,12));
		out.copyColorsFrom(this);
		if (this.isOutlineType()) {
			out.getBackGroundShape().setStrokeColor(this.getBackGroundShape().getStrokeColor());
			out.getBackGroundShape().setFillColor(this.getBackGroundShape().getFillColor());
			out.getBackGroundShape().setStrokeWidth(1);
			
		}
		//out.setArrowStyle(getArrowStyle());
		
		out.setArrowTipAngle(this.getArrowTipAngle());
		out.setNotchAngle(this.getNotchAngle());
		out.setHeadnumber(getHeadnumber());
		if (this.isOpenHeadType()||this.isOutlineType())out.setArrowStyle(this.getArrowStyle());
		out.setStrokeWidth(2);
		out.setArrowHeadSize(6);
		return out;
	}

	public static Icon createImageIcon() {
		if (i==null) i=new IconSet("iconsTree/ArrowGraphicTreeIcon.png");
		return i.getIcon(0);//new ImageIcon(i.getIcon(0));
	}
	public BasicShapeGraphic getBackGroundShape() {
		if (backGroundShape==null) {
			backGroundShape=new  BasicShapeGraphic(this.getOutline());
			backGroundShape.setStrokeWidth(2);
			backGroundShape.setStrokeColor(this.getStrokeColor());
			backGroundShape.setAntialize(true);
		}
		return backGroundShape;
	}
	
	public PopupMenuSupplier getMenuSupplier() {
		return new ArrowGraphicMenu(this);
	}
	
	
	@Override
	public void scaleAbout(Point2D p, double mag) {
		try {
		UndoScalingAndRotation output = new UndoScalingAndRotation(this);
			Point2D p1 = new Point2D.Double(x,y);
			Point2D p2 = new Point2D.Double(x2,y2);
			p2=scaleAbout(p2, p,mag,mag);
			p1=scaleAbout(p1, p,mag,mag);
			this.setPoints(p1, p2);
			BasicStrokedItem.scaleStrokeProps(this, mag);
			//this.setStrokeWidth((float) (this.getStrokeWidth()*mag));
			this.setArrowHeadSize(this.getArrowHeadSize()*mag);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	/**Sets the color*/
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
		if (!this.isOutlineType()) {
			oo.setStrokeWidth(-1);
			oo.setFillColor(getStrokeColor());
			
		}
		return oo;
	}
	
	@Override
	public OfficeObjectMaker getObjectMaker() {
		// TODO Auto-generated method stub
		return this.createPathCopy().getObjectMaker();
	}
  
	@Override
	public SVGExporter getSVGEXporter() {
		// TODO Auto-generated method stub
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
			return SmartHandleList.combindLists(smartList, getButtonList());
		return SmartHandleList.combindLists(smartList, getButtonList(), getRotateList());
		//return smartList;
	}
	
	private SmartHandleList getRotateList() {
		if (rotList==null)rotList=new ReshapeHandleList(1,400, this);
		rotList.updateRectangle();
		return rotList;
	}
	
	public ShapeActionButtonHandleList2 getButtonList() {
		if(buttonList==null) {
			buttonList=new ShapeActionButtonHandleList2(this);
			if(this.hideNormalHandles) 	buttonList=new ShapeActionButtonHandleList2(this, 4);
		}
		buttonList.updateLocation();
		return buttonList;
	}
	protected SmartHandleList createSmartHandleList() {
		 SmartHandleList smList = new SmartHandleList();
		 if (!hideNormalHandles) {
		smList.add(new ArrowSmartHandle(HANDLE_1, this));
		smList.add(new ArrowSmartHandle(HANDLE_2, this));
		smList.add(new ArrowSmartHandle(ARROW_STROKE_HANDLE, this));
		//smList.add(createHeadNumberHandle());
		}
		smList.add(createArrowSizeHandle(2));
		
		
		return smList;
	}
	public CountHandle createHeadNumberHandle() {
		return new CountHandle(this, headnumber, HEAD_NUN_HANDLE, 25,2, true, 1);
	}
	public ArrowSmartHandle createArrowSizeHandle(int i) {
		return new ArrowSmartHandle(1000*i+ARROW_SIZE_HANDLE, this);
	}
	
	@Override
	public int handleNumber(int x, int y) {
		return getSmartHandleList().handleNumberForClickPoint(x, y);
	}
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		SmartHandle h = this.getSmartHandleList().getHandleNumber(handlenum);
		if (h!=null) h.handleMove(p1, p2);
	}
	
	/**A handle for the user to modify the arrow*/
class ArrowSmartHandle extends SmartHandle {
		
		public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
			
			super.draw(graphics, cords);
		}

		private ArrowGraphic rect;
		private transient UndoScalingAndRotation undo;
		private transient boolean undoAdded=false;

		public ArrowSmartHandle(int type, ArrowGraphic r) {
			super(0, 0);
			this.setHandleNumber(type);
			this.rect=r;
			if(isArrowSizeHandle()) {
				this.setHandleColor(Color.blue);
			}
			if(type==ARROW_STROKE_HANDLE) {
				this.setHandleColor(Color.magenta);
			}
		}

		public boolean isArrowSizeHandle(int type) {
			return type==ARROW_SIZE_HANDLE;
		}
		
		public Point2D getCordinateLocation() {
			ArrayList<Point2D> ends = getEndPoints();
			if (this.getHandleNumber()==HANDLE_1) return ends.get(0);
			if (this.getHandleNumber()==HANDLE_2) return ends.get(1);
			if (this.isArrowStrokeHandle()) {
				return getStrokeHandlePoints()[0];
			}
			return getHeadPoints()[2];
		
		}
		
		
		public void handlePress(CanvasMouseEventWrapper lastDragOrRelMouseEvent) {
			undoAdded=false;
			undo=new UndoScalingAndRotation(rect);
			if (lastDragOrRelMouseEvent.clickCount()==2&&isArrowSizeHandle())
				{
				java.lang.Double p = StandardDialog.getNumberFromUser("Arrow Size", getArrowHeadSize());
				setArrowHeadSize(p);
				setupUndo(lastDragOrRelMouseEvent);
				}
			
			if (lastDragOrRelMouseEvent.clickCount()==2&&isArrowStrokeHandle()) {
				new StrokeOnlySwingDialog(rect).showDialog();
				setupUndo(lastDragOrRelMouseEvent);
			}
			
			if (lastDragOrRelMouseEvent.clickCount()==2&&this.getHandleNumber()==HANDLE_1) {
				Point2D p = StandardDialog.getPointFromUser("Set Location ",getOppositeTipEndLocation());
				setPoint1(p);
				setupUndo(lastDragOrRelMouseEvent);
			}
			
			if (lastDragOrRelMouseEvent.clickCount()==2&&this.getHandleNumber()==HANDLE_2) {
				Point2D p = StandardDialog.getPointFromUser("Set Location ",getTipLocation());
				setPoint2(p);
				setupUndo(lastDragOrRelMouseEvent);
			}
			
			}

		protected void setupUndo(CanvasMouseEventWrapper lastDragOrRelMouseEvent) {
			if(undo==null) return;
			undo.establishFinalState();
			if(!undoAdded) {lastDragOrRelMouseEvent.addUndo(undo);
			undoAdded=true;}
		}

		public void handleDrag(CanvasMouseEventWrapper lastDragOrRelMouseEvent) {
			Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
			if (this.getHandleNumber()==HANDLE_1) {
				setPoint1(p2);
			}
			if (this.getHandleNumber()==HANDLE_2) {
				setPoint2(p2);
			}
			
			if (isArrowSizeHandle()) {
				java.awt.geom.Point2D.Double metric = getHeadPoints()[3];
				double d = metric.distance(p2);
				setArrowHeadSize(d);
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
			return this.getHandleNumber()%1000==ARROW_SIZE_HANDLE;
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
			if (getHeadnumber()==0 &&isArrowSizeHandle() )
				return true;
			return hidden;
		}
		
	}

@Override
public boolean isFillable() {return false;}

/**returns true if the stroke join is relevant to the appearance of the arrow*/
public boolean doesJoins() {
	if(this.style==openHead) return true;
	return false;
}

/**extends the arrow's length such that the notch will be moved to the previous location of the head */
public void moveNotchToHead1() {
	Point2D p1 = this.getTipLocation();
	Point2D n1 = this.getNotchLocation();
	moveTipLocation(p1.getX()-n1.getX(), p1.getY()-n1.getY());
}
@Override
public void rotateAbout(Point2D c, double distanceFromCenterOfRotationtoAngle) {
	try {
		if(distanceFromCenterOfRotationtoAngle==0) {
			IssueLog.log("returned after arrow rotation attempt due to 0 angle");
			return;
			}
		
		AffineTransform a = AffineTransform.getRotateInstance(distanceFromCenterOfRotationtoAngle, c.getX(), c.getY());
		
		Point2D p2 = this.getTipLocation();
		Point2D p1 = this.getOppositeTipEndLocation();
		a.transform(p2, p2);
		a.transform(p1, p1);
		this.setPoint1(p1);
		this.setPoint2(p2);
		
	} catch (Exception e) {
		
		e.printStackTrace();
	}
}

/**overrides the methos in the interface but implementation is not crucial for arrows*/
@Override
public void setFillBackGround(boolean fillBackGround) {
	
}


}
