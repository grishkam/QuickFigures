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
package graphicalObjects_Shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;

import animations.KeyFrameAnimation;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.CordinateConverter;
import graphicalObjects.GraphicalObject;
import handles.HasSmartHandles;
import handles.RectangularShapeSmartHandle;
import handles.SmartHandle;
import handles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorObjectConvertable;
import illustratorScripts.PathItemRef;
import keyFrameAnimators.RectGraphicKeyFrameAnimator;
import layersGUI.HasTreeLeafIcon;
import layout.RetrievableOption;
import locatedObject.BasicStrokedItem;
import locatedObject.Fillable;
import locatedObject.RectangleEdgePositions;
import locatedObject.RectangleEdges;
import locatedObject.ScalesFully;
import locatedObject.ShowsOptionsDialog;
import locatedObject.StrokedItem;
import objectDialogs.RectangleGraphicOptionsDialog;
import standardDialog.graphics.GraphicDisplayComponent;

/**Defines an editable rectangle object. User may edit by dragging handles or using a dedicated dialog*/
public class RectangularGraphic extends ShapeGraphic implements GraphicalObject, StrokedItem, ShowsOptionsDialog ,Fillable, HasTreeLeafIcon,ScalesFully,IllustratorObjectConvertable,  RectangleEdgePositions, HasSmartHandles {
	 
	{name="Rectangle ";}
	
	private static final long serialVersionUID = 1L;
	
	
	@RetrievableOption(key="width",  label="Width ")
	private double width;

	@RetrievableOption(key="height",  label="Height ")
	private double height;
	


	private SmartHandleList smartList;
	public boolean flipDuringHandleDrag=true;

	public Integer handleSize=3;
	
	/**under some circumstances, not all the handles will be visible*/
	public boolean hideCenterAndRotationHandle=false;
	public boolean hideStrokeHandle=false;
	public boolean hideRotationHandle=false;

	
	
	public RectangularGraphic(){}
	
	public RectangularGraphic(double x, double y, double width2, double height2) {
		this.x=x;
		this.y=y;
		this.setObjectWidth(width2);
		this.setObjectHeight(height2);
	}
	
	public RectangularGraphic(Point2D p) {
		this(new Rectangle2D.Double(p.getX(), p.getY(), 1, 1));
	}
	
	public RectangularGraphic(Rectangle2D r) {
		setLocationType(RectangleEdgePositions.UPPER_LEFT);
		if(r!=null)
			setRectangle(r);
	}
	
	public RectangularGraphic(RectangularGraphic r) {
		this(r.getBounds());
		copyAttributesFrom(r);
		copyColorsFrom(r);
	}
	
	public String getShapeName() {
		return "Rectangle";
	}
	

	public void setRectangle(Rectangle2D r) {
		x=r.getX();
		y=r.getY();
		setObjectWidth(r.getWidth());
		setObjectHeight(r.getHeight());
	}
	

	@Override
	public RectangularGraphic copy() {
		RectangularGraphic copy = new RectangularGraphic(getRectangle().getBounds());
		copy.copyAttributesFrom(this);
		copy.copyColorsFrom(this);
		copy.setLocationType(getLocationType());
		return copy;
	}

/**Called when the rectangle's handles are moved*/
	public void handleSmartMove(int handlenum, Point2D destination) {
		
		/**if the rectangle is rotated, transforms the points to the equivalent unrotated points.
		 * this step is not needed for the rotation handle itself*/
		if (handlenum!=RectangularShapeSmartHandle.ROTATION_HANDLE && handlenum!=CENTER) {
			
			performRotationCorrection(destination);
		}
		
		if (flipDuringHandleDrag)
			handlenum=checkForHandleInvalidity(handlenum,destination);
		
		/**When a user drags one corner the other is set as the fixed edge*/
		int op=RectangleEdges.oppositeSide(handlenum);
		setLocationType(op);
		
		Point2D l2 = RectangleEdges.getLocation(op, getBounds());
		double newwidth = Math.abs(destination.getX()-l2.getX());
		double newheight = Math.abs(destination.getY()-l2.getY());
		
		
		
		if (handlenum<LEFT) {
			this.setWidth(newwidth);
			this.setHeight(newheight);
			getListenerList().notifyListenersOfUserSizeChange(this);
		
		}
		if(handlenum==TOP||handlenum==BOTTOM) {
			this.setHeight(newheight);
			getListenerList().notifyListenersOfUserSizeChange(this);
			
		} else 
		if(handlenum==LEFT||handlenum==RIGHT) {
			this.setWidth(newwidth);
			getListenerList().notifyListenersOfUserSizeChange(this);
			
		} else if (handlenum==CENTER) {
			this.setLocationType(CENTER);
			this.setLocation(destination);
			
			}
		
		
		if (handlenum==RectangularShapeSmartHandle.ROTATION_HANDLE){

			setAngle(BasicGraphicalObject.distanceFromCenterOfRotationtoAngle(getCenterOfRotation(), destination));
		
			
		}
		
	}
	
	/**Some times the user drags handles past each other, this switches the handle number accordingly*/
	int checkForHandleInvalidity(int handlenum, Point2D p2) {
		Point2D bot = RectangleEdges.getLocation(BOTTOM, this.getRectangle());
		Point2D top = RectangleEdges.getLocation(TOP, this.getRectangle());
		Point2D left = RectangleEdges.getLocation(LEFT, this.getRectangle());
		Point2D right = RectangleEdges.getLocation(RIGHT, this.getRectangle());
		if(handlenum==TOP &&  bot.distance(p2)<top.distance(p2)) return BOTTOM;
		if(handlenum==BOTTOM &&  bot.distance(p2)>top.distance(p2)) return TOP;
		if(handlenum==LEFT &&  right.distance(p2)<left.distance(p2)) return RIGHT;
		if(handlenum==RIGHT &&  right.distance(p2)>left.distance(p2)) return LEFT;
		
		if (handlenum<4) {
			return RectangleEdges.getNearestEdgeFromList(getRectangle(), new int[] {UPPER_LEFT, UPPER_RIGHT,LOWER_LEFT,LOWER_RIGHT}, p2);
		}
		
		
		return handlenum;
	}
	
	
	/**
	 * @param handleNumber
	 * @param p1
	 * @param p2
	 */
	public void afterHandleMove(int handleNumber, Point p1, Point p2) {
		
		
	}
	
	
	
	/**setter method for this rectangles width. Keeps the location of the
	  rectangle of its location type in the same place.
	  Performs position correction so the final point from getLocation()
	  will be the same as the initial point.*/
	public void setWidth(double w) {
		if (getObjectWidth()==w) return;
		Point2D p = getLocation();
		
		Point2D prot=null;
		if(this.getAngle()!=0) {
			prot = new Point2D.Double(); 
			prot.setLocation(p.getX(), p.getY());
			undoRotationCorrection(prot);
		}
		
		setObjectWidth(w);
		setLocation(p);
		
		/**if the item is rotated, a correcting is needed*/
		if (this.getAngle()!=0) {
		
	
		
		
		p = getLocation();
		Point2D prot2 = new Point2D.Double(); prot2.setLocation(p.getX(), p.getY());
		undoRotationCorrection(prot2);
		double dx = prot.getX()-prot2.getX();
		double dy = prot.getY()-prot2.getY();
		
		this.moveLocation(dx, dy);
		}
	}

	/**setter method for this rectangles size.
	  Performs position correction so the final point from getLocation()
	  will be the same as the initial point*/
	public void setHeight(double h) {
		if (getObjectHeight()==h) return;
		Point2D p = getLocation();
		
		
		Point2D prot=null;
		if(this.getAngle()!=0) {
			prot = new Point2D.Double(); 
			prot.setLocation(p.getX(), p.getY());
			undoRotationCorrection(prot);
		}
		
		setObjectHeight(h);
		
		setLocation(p);
		
		
		
		/**if the item is rotated, a correcting is needed*/
		if (this.getAngle()!=0) {
		
		p = getLocation();
		Point2D prot2 = new Point2D.Double(); prot2.setLocation(p.getX(), p.getY());
		undoRotationCorrection(prot2);
		double dx = prot.getX()-prot2.getX();
		double dy = prot.getY()-prot2.getY();
	
		this.moveLocation(dx, dy);
		}
	}


	/**Returns the location. depending on the location type. 
	 * @see RectangleEdges*/
	@Override
	public Point2D getLocation() {
		Point2D out = RectangleEdges.getLocation(getLocationType(), getRectangle());
		return new Point.Double(out.getX(), out.getY());
	}

	/**Sets the location. what location exactly this refers to depends on the location type
	 @see RectangleEdges */
	@Override
	public void setLocation(double x, double y) {
		Rectangle2D.Double r=getRectangle();//.getBounds();
		RectangleEdges.setLocation(r,getLocationType(), x,y);
		setRectangle(r);
		super.notifyListenersOfMoveMent();
	}
	

	/**returns the bounds of the rectangle in its unroated form*/
	@Override
	public Rectangle getBounds() {
		return getRectangle().getBounds();
	}
	
	/**returns the rectangle in its non-rotated form*/
public Rectangle2D.Double getRectangle() {
	return new Rectangle2D.Double(x,y,getObjectWidth(),getObjectHeight());
}



	/**Displays a dialog*/
	@Override
	public void showOptionsDialog() {
		getOptionsDialog(false).showDialog();
	}
	public RectangleGraphicOptionsDialog getOptionsDialog(boolean simple) {
		return new RectangleGraphicOptionsDialog(this, simple);
	}	


	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		pi.createRectangle(aref, this.getRectangle());
		
	}
	
	/**returns the shape*/
	@Override
	public Shape getShape() {
		return getRectangle();
	}
	
	
	@Override
	public Icon getTreeIcon() {
		return new GraphicDisplayComponent(createIcon() );
	}
	
	RectangularGraphic createIcon() {
		RectangularGraphic out = rectForIcon() ;
		out.setAntialize(true);
		out.setStrokeWidth(1);
		out.copyColorsFrom(this);
		if (super.isIconTooWhite()) {
			out.setStrokeColor(whiteIcon);
		}
		return out;
	}
	
	
	/**returns a small rectangle for use as an icon. subclasses generally override this */
	RectangularGraphic rectForIcon() {
		return  RectangularGraphic.blankRect(new Rectangle(0,0,12,10), Color.BLACK);
	
	
	}
	
	/**Scales the rectangle */
	@Override
	public void scaleAbout(Point2D p, double mag) {
		Point2D p2 = this.getLocationUpperLeft();
		p2=scaleAbout(p2, p,mag,mag);
		this.setWidth(getObjectWidth()*mag);
		this.setHeight(getObjectHeight()*mag);
		BasicStrokedItem.scaleStrokeProps(this, mag);
		this.setLocationUpperLeft(p2);
		
	}
	
	/**Scales the rectangle */
	@Override
	public void scaleAbout(Point2D p, double magx, double magy) {
		this.setLocationType(CENTER);
		Point2D p2 = this.getLocation();
		p2=scaleAbout(p2, p,magx,magy);
		
		try {
		Rectangle2D r = this.getRectangle();
			Shape r2 = this.getRotationTransform().createTransformedShape(r);
			r2=AffineTransform.getScaleInstance(magx, magy).createTransformedShape(r2);
			r2=this.getRotationTransform().createInverse().createTransformedShape(r2);
			r=r2.getBounds2D();
			this.setObjectWidth(r.getWidth());
			this.setObjectHeight(r.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.setLocation(p2);
		
	}
	
	/**Performs a rotation*/
	@Override
	public void rotateAbout(Point2D p, double distanceFromCenterOfRotationtoAngle) {
		if(distanceFromCenterOfRotationtoAngle==0) return;
		this.setLocationType(CENTER);
		Point2D p2 = this.getLocation();
		AffineTransform at = AffineTransform.getRotateInstance(distanceFromCenterOfRotationtoAngle, p.getX(), p.getY());
		at.transform(p2, p2);
		
		this.setLocation(p2);
		this.setAngle(this.getAngle()-distanceFromCenterOfRotationtoAngle);
	}
	
	public double getObjectHeight() {
		return height;
	}
	protected void setObjectHeight(double height) {
		this.height = height;
	}
	public double getObjectWidth() {
		return width;
	}
	protected void setObjectWidth(double width) {
		this.width = width;
	}
	
	/**rotation of the rectangle changes the cordinates of the handles, this method corrects for that*/
	protected void performRotationCorrection(Point2D p) {
		AffineTransform aa = RectangleEdges.getRotationAboutCenter(this.getBounds(), this.getAngle());
		aa.transform(p, p);
	}
	/**rotation of the rectangle changes the cordinates of the handles, this method corrects for that*/
	public void undoRotationCorrection(Point2D p) {
		AffineTransform aa = RectangleEdges.getRotationAboutCenter(this.getBounds(), -getAngle());
		aa.transform(p, p);
	}
	

	/**draws the handles*/
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter cords) {
		if (selected &&!super.handlesHidden) {
		
			getSmartHandleList().draw(g2d, cords);
		}
		
	}
	
	/**returns the location for the rotation handle*/
	public Point getRotationHandleLocation() {
		Point p4 = new Point();
		   p4.setLocation(getCenterOfRotation());
		   double rightShift = width*0.6;
		   if(rightShift<5) rightShift=5;
		   p4.x+=rightShift;
		   undoRotationCorrection(p4);
		return p4;
	}
	
	public  KeyFrameAnimation getOrCreateAnimation() {
		if (animation instanceof KeyFrameAnimation) return (KeyFrameAnimation) animation;
		animation=new RectGraphicKeyFrameAnimator(this);
		return (KeyFrameAnimation) animation;
	}
	
	public Point2D getCenterOfRotation() {
		Rectangle2D.Double b = getRectangle();
		return new Point2D.Double(b.getCenterX(), b.getCenterY());
	}
	
	/**creates the handle list*/
	protected SmartHandleList createSmartHandleList() {
		 SmartHandleList smList = new SmartHandleList();
		for(int i:RectangleEdges.internalLocations) {
			if (hideCenterAndRotationHandle &&i==CENTER) continue;
			smList.add(createSmartHandle(i));
		}
		if (!hideCenterAndRotationHandle &&!hideRotationHandle)
			{
			smList.add(createSmartHandle(RectangularShapeSmartHandle.ROTATION_HANDLE));
			
			}
		if (!hideStrokeHandle) smList.add(createSmartHandle(RectangularShapeSmartHandle.STROKE_HANDLE_TYPE));
		return smList;
	}
	
	/**Creates a handle*/
	protected SmartHandle createSmartHandle(int type) {
		RectangularShapeSmartHandle out = new RectangularShapeSmartHandle(type, this);
		if (handleSize!=null) out.handlesize=handleSize;
		out.setHandleNumber(type);
		out.updateLocation(type);
				return out;
	}
	
/**returns the points that define the stroke' handles location and reference location.
   Precondition: the distance between the two points should be about half the stroke*/
	public Point2D[] getStrokeHandlePoints() {
		/**this math places the handle at the edge of the stroke near the middle of the line*/
	
		Point2D location2 = RectangleEdges.getLocation(RectangleEdges.RIGHT, getRectangle());
		Point2D location1 = RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, getRectangle());
		this.getRotationTransform().transform(location2, location2);
		this.getRotationTransform().transform(location1, location1);
		return calculatePointsOnStrokeBetween(location1, location2);
	}
	
	/**returns the list of handles for the shape*/
	@Override
	public SmartHandleList getSmartHandleList() {
		if (smartList==null)smartList=this.createSmartHandleList(); 
		if (!superSelected) return smartList;
		return SmartHandleList.combindLists(smartList, getButtonList());
	}

	
	/**returns the handle id for the location*/
	@Override
	public int handleNumber(double x, double y) {
		return getSmartHandleList().handleNumberForClickPoint(x, y);
	}
	
	/**Called when a handle is moved*/
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		SmartHandle h = this.getSmartHandleList().getHandleNumber(handlenum);
		if (h!=null) h.handleMove(p1, p2);
	}
	
	/**used by multiple subclasses to find the location of a point inside
	 * the shape defined by an angle and a factor (ratio to the radius of an enclosed oval/circle)*/
	public Point2D getPointInside(double factor, double currentAngle) {
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		
		double centx = x+rx;
		double centy = y+ry;
		double curx=centx+Math.cos(currentAngle)*rx*factor;
		double cury=centy+Math.sin(currentAngle)*ry*factor;
		
		Point2D.Double output = new Point2D.Double(curx, cury);
		
		undoRotationCorrection(output);
		return output;
	}

	


	/**returns a simple rectangle*/
	public static RectangularGraphic blackRect() {
		RectangularGraphic r1 = new RectangularGraphic(0,0,300,200);
		r1.setFilled(true);
		r1.setDashes(new float[]{10000});
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(Color.black);
		return r1;
	}
	
	/**returns a rectangular graphic with the given bounds and color*/
	public static RectangularGraphic blankRect(Rectangle r, Color c) {
		return blankRect(r, c, false, false);
	}
	/**returns a rectangle. If certain arguments are set to true, the handes are not shown */
	public static RectangularGraphic blankRect(Rectangle2D r, Color c, boolean hideHandle, boolean hideAllHandles) {
		RectangularGraphic r1 = new RectangularGraphic(r);
		r1.hideCenterAndRotationHandle=hideHandle;
		r1.hideStrokeHandle=hideHandle;
		r1.hideHandles(hideAllHandles);
	
		r1.setStrokeWidth(3);
		r1.setStrokeColor(c);
		return r1;
	}
	/**returns a filled rectangle with not visible stroke*/
	public static RectangularGraphic filledRect(Rectangle r) {
		RectangularGraphic output = new RectangularGraphic(r);
		output.setFilled(true);
		output.setStrokeWidth(-1);//no stroke
		return output;
		}


	

	
}
