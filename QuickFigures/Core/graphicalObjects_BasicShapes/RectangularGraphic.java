package graphicalObjects_BasicShapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;
import javax.swing.JPopupMenu;

import actionToolbarItems.EditAndColorizeMultipleItems;
import animations.KeyFrameAnimation;
import applicationAdapters.CanvasMouseEventWrapper;
import fieldReaderWritter.RetrievableOption;
import graphicalObjectHandles.HasSmartHandles;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects.CordinateConverter;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorObjectConvertable;
import illustratorScripts.PathItemRef;
import keyFrameAnimators.RectGraphicKeyFrameAnimator;
import layersGUI.HasTreeLeafIcon;
import objectDialogs.RectangleGraphicOptionsDialog;
import objectDialogs.WidthAndHeightDialog;
import selectedItemMenus.LayerSelector;
import standardDialog.GraphicDisplayComponent;
import standardDialog.StandardDialog;
import undo.UndoStrokeEdit;
import utilityClassesForObjects.BasicStrokedItem;
import utilityClassesForObjects.Fillable;
import utilityClassesForObjects.RectangleEdgePosisions;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.ScalesFully;
import utilityClassesForObjects.ShowsOptionsDialog;
import utilityClassesForObjects.StrokedItem;

public class RectangularGraphic extends ShapeGraphic implements GraphicalObject, StrokedItem, ShowsOptionsDialog ,Fillable, HasTreeLeafIcon,ScalesFully,IllustratorObjectConvertable,  RectangleEdgePosisions, HasSmartHandles {
	

	/**
	 * 
	 */
	
	
	
	private static final long serialVersionUID = 1L;
	
	{name="Rectangle ";}
	@RetrievableOption(key="width",  label="Width ")
	private double width;

	@RetrievableOption(key="height",  label="Height ")
	private double height;
	
	@RetrievableOption(key="stretchX",  label="Stretch X ")
	private double stretchX=1.0;

	@RetrievableOption(key="stretchY",  label="Stretch Y ")
	private double stretchY=1.0;

	private SmartHandleList smartList;
	public boolean flipDuringHandleDrag=true;

	public Integer handleSize=3;
	public boolean hideCenterAndRotationHandle=false;

	public boolean hideStrokeHandle=false;

	public boolean hideRotationHandle=false;

	
	
	public RectangularGraphic(){}
	
	public RectangularGraphic(Point2D p) {
		this(new Rectangle2D.Double(p.getX(), p.getY(), 1, 1));
	}
	
	public RectangularGraphic(Rectangle2D r) {
		setLocationType(0);
		setRectangle(r);
	}
	
	public RectangularGraphic(RectangularGraphic r) {
		this(r.getBounds());
		copyAttributesFrom(r);
		copyColorsFrom(r);
	}
	
	public static RectangularGraphic blackRect() {
		RectangularGraphic r1 = new RectangularGraphic(0,0,300,200);
		r1.setFilled(true);
		r1.setDashes(new float[]{10000});
		r1.setStrokeWidth(4);
		r1.setStrokeColor(Color.black);
		return r1;
	}
	
	/**returns a rectangular graphic*/
	public static RectangularGraphic blankRect(Rectangle r, Color c) {
		return blankRect(r, c, false, false);
	}
	public static RectangularGraphic blankRect(Rectangle2D r, Color c, boolean hideHandle, boolean hideAllHandles) {
		RectangularGraphic r1 = new RectangularGraphic(r);
		r1.hideCenterAndRotationHandle=hideHandle;
		r1.hideStrokeHandle=hideHandle;
		r1.hideHandles(hideAllHandles);
		r1.setDashes(new float[]{10000});
		r1.setStrokeWidth(3);
		r1.setStrokeColor(c);
		return r1;
	}
	
	public RectangularGraphic(double x, double y, double width2, double height2) {
		this.x=x;
		this.y=y;
		this.setObjectWidth(width2);
		this.setObjectHeight(height2);
	}
	public void setRectangle(Rectangle2D r) {
		x=r.getX();
		y=r.getY();
		setObjectWidth(r.getWidth());
		setObjectHeight(r.getHeight());
	}
	
	public static RectangularGraphic filledRect(Rectangle r) {
		RectangularGraphic output = new RectangularGraphic(r);
		output.setFilled(true);
		output.setStrokeWidth(-1);
		return output;
		}

	@Override
	public RectangularGraphic copy() {
		RectangularGraphic copy = new RectangularGraphic(getRectangle().getBounds());
		copy.copyAttributesFrom(this);
		copy.copyColorsFrom(this);
		copy.setLocationType(getLocationType());
		return copy;
	}



	
	

	
	public void handleSmartMove(int handlenum, Point p1, Point p2) {
		if (handlenum!=10) {
		performRotationCorrection(p1);
		performRotationCorrection(p2);
		}
		if (flipDuringHandleDrag)
			handlenum=checkForHandleInvalidity(handlenum,p1,p2);
		
		int op=RectangleEdges.oppositeSide(handlenum);
		setLocationType(op);
		
		//Point2D lo = getLocation(handlenum, getBounds());
		Point2D l2 = RectangleEdges.getLocation(op, getBounds());
		double newwidth = Math.abs(p2.getX()-l2.getX());
		double newheight = Math.abs(p2.getY()-l2.getY());
		
		
		
		if (handlenum<4) {
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
			this.setLocation(p2);
			}
		
		
		if (handlenum==10){

			setAngle(BasicGraphicalObject.distanceFromCenterOfRotationtoAngle(getCenterOfRotation(), p2));
		
			
		}
		
	}
	
	/**Some times the user drags handles past each other, this switches the handle number accordingly*/
	int checkForHandleInvalidity(int handlenum, Point p1, Point p2) {
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


	
	@Override
	public Point2D getLocation() {
		Point2D out = RectangleEdges.getLocation(getLocationType(), getRectangle());
		return new Point.Double(out.getX(), out.getY());
	}

	@Override
	public void setLocation(double x, double y) {
		Rectangle2D.Double r=getRectangle();//.getBounds();
		RectangleEdges.setLocation(r,getLocationType(), x,y);
		setRectangle(r);
		super.notifyListenersOfMoveMent();
	}
	

	@Override
	public Rectangle getBounds() {
		return getRectangle().getBounds();
	}
	
	
public Rectangle2D.Double getRectangle() {
	return new Rectangle2D.Double(x,y,getObjectWidth(),getObjectHeight());
}



	@Override
	public void showOptionsDialog() {
		new RectangleGraphicOptionsDialog(this).showDialog();
	}	


	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		pi.createRectangle(aref, this.getRectangle());
		
	}
	
	@Override
	public Shape getShape() {
		return getRectangle();
	}
	
	
	
	@Override
	public Icon getTreeIcon() {
		return new GraphicDisplayComponent(createIcon() );
		//return createImageIcon();
	}
	
	RectangularGraphic createIcon() {
		RectangularGraphic out = rectForIcon() ;//RectangularGraphic.blankRect(new Rectangle(0,0,14,12), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFillColor(), this.getStrokeColor());
		out.setAntialize(true);
		out.setStrokeWidth(1);
		out.copyColorsFrom(this);
		if (super.isIconTooWhite()) {
			out.setStrokeColor(whiteIcon);
		}
		return out;
	}
	
	RectangularGraphic rectForIcon() {
		return  RectangularGraphic.blankRect(new Rectangle(0,0,12,10), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
	
	
	}
	@Override
	public void scaleAbout(Point2D p, double mag) {
		Point2D p2 = this.getLocationUpperLeft();
		p2=scaleAbout(p2, p,mag,mag);
		this.setWidth(getObjectWidth()*mag);
		this.setHeight(getObjectHeight()*mag);
		BasicStrokedItem.scaleStrokeProps(this, mag);
		this.setLocationUpperLeft(p2);
		
	}
	
	/**faulty implementation*/
	@Override
	public void scaleAbout(Point2D p, double magx, double magy) {
		this.setLocationType(CENTER);
		Point2D p2 = this.getLocation();
		p2=scaleAbout(p2, p,magx,magy);
		double a = this.getAngle();
		
		try {
		Rectangle2D r = this.getRectangle();
			Shape r2 = this.getRotationTransform().createTransformedShape(r);
			r2=AffineTransform.getScaleInstance(magx, magy).createTransformedShape(r2);
			r2=this.getRotationTransform().createInverse().createTransformedShape(r2);
			r=r2.getBounds2D();
			this.setObjectWidth(r.getWidth());
			this.setObjectHeight(r.getHeight());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//double w = getObjectWidth()*magx;//(Math.abs(magx*Math.cos(a))+Math.abs(magy*Math.sin(a)));
		//double h = getObjectHeight()*magy;//*(Math.abs(magx*Math.sin(a))+Math.abs(magy*Math.cos(a)));
		//this.setWidth(w);
		//this.setHeight(h);
		
		
		this.setLocation(p2);
		
	}
	
	@Override
	public void rotateAbout(Point2D p, double distanceFromCenterOfRotationtoAngle) {
		if(distanceFromCenterOfRotationtoAngle==0) return;
		//not yet implemented
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
	
	
	protected void performRotationCorrection(Point2D p) {
		AffineTransform aa = RectangleEdges.getRotationAboutCenter(this.getBounds(), this.getAngle());
		removeStretch(aa);
		aa.transform(p, p);
	}
	
	public void undoRotationCorrection(Point2D p) {
		AffineTransform aa = RectangleEdges.getRotationAboutCenter(this.getBounds(), -getAngle());
		addStretch(aa);
		aa.transform(p, p);
	}
	

	
	
	private void addStretch(AffineTransform rotation) {
		if(stretchX!=1||stretchY!=1) {
			
		}
	}

	/**not implemented properly yet*/
	protected AffineTransform getStretch() {
		double xr = getCenterOfRotation().getX();
		double yr = getCenterOfRotation().getY();
		AffineTransform stretch = AffineTransform.getTranslateInstance(-xr, -yr);
		stretch.concatenate(AffineTransform.getScaleInstance(stretchX, stretchY));
		stretch.concatenate(AffineTransform.getTranslateInstance(xr, yr));
		return stretch;
	}
	private void removeStretch(AffineTransform rotation) {
		if(stretchX!=1||stretchY!=1) {
			try {
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**draws the handles*/
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter<?> cords) {
		if (selected &&!super.handlesHidden) {
		
			getSmartHandleList().draw(g2d, cords);
		}
		
	}
	public Point getRotationHandleLocation() {
		Point p4 = new Point();
		   p4.setLocation(getCenterOfRotation());
		   p4.x+=width*0.6;
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
	
	

	public static final int STROKE_HANDLE_TYPE = 11, rotationType=10;
	
	protected SmartHandleList createSmartHandleList() {
		 SmartHandleList smList = new SmartHandleList();
		for(int i:RectangleEdges.locationsforh) {
			if (hideCenterAndRotationHandle &&i==CENTER) continue;
			smList.add(createSmartHandle(i));
		}
		if (!hideCenterAndRotationHandle &&!hideRotationHandle)
			{
			smList.add(createSmartHandle(rotationType));
			
			}
		if (!hideStrokeHandle) smList.add(createSmartHandle(STROKE_HANDLE_TYPE));
		return smList;
	}
	
	
	
	protected SmartHandle createSmartHandle(int type) {
		RectangleSmartHandle out = new RectangleSmartHandle(type, this);
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
	
	class RectangleSmartHandle extends SmartHandle {
		private RectangularGraphic rect;
		private UndoStrokeEdit strokeUndo;

		public RectangleSmartHandle(int type, RectangularGraphic r) {
			super(0, 0);
			this.setHandleNumber(type);
			this.rect=r;
			
			
		}
		public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
			this.updateLocation(getHandleNumber());
			if (this.getHandleNumber()==rect.getLocationType()) {
				this.setHandleColor(Color.red);
				
			} else this.setHandleColor(Color.white);
			
			if (isRotationHandle()) {
				this.setHandleColor(Color.orange);
				getGrahpicUtil(). drawSizeHandlesAtPoint(graphics, cords,  this.getCordinateLocation(),rect.getCenterOfRotation());
			}
			
			if (getHandleNumber()==STROKE_HANDLE_TYPE) {
				this.setHandleColor(Color.magenta);
			}
			
			if (this.isRotationHandle()&&specialShape==null) {
				int x2 = (int) (-handlesize*1.5);
				int w = (int) (handlesize*3);
				this.specialShape=new Ellipse2D.Double(x2, x2, w, w);
			}
			
			super.draw(graphics, cords);
		}

		public boolean isRotationHandle() {
			return this.getHandleNumber()==rotationType;
		}

		
		
		public void updateLocation(int type) {
			if (type!=rotationType) {
				Point2D p = RectangleEdges.getLocation(type,rect.getBounds());
				rect.undoRotationCorrection(p);
				setCordinateLocation(p);
				
				}  else {
				setCordinateLocation(getRotationHandleLocation());
				}
			
			if (type==STROKE_HANDLE_TYPE) {
				Point2D p =getStrokeHandlePoints()[0];
				//rect.undoRotationCorrection(p);
				setCordinateLocation(p);
			}
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public void handleDrag(CanvasMouseEventWrapper w) {
			if (isStrokeHandle()) {
				
				Point2D p =getStrokeHandlePoints()[1];
				Point c = w.getCordinatePoint();
				double d = 2*p.distance(c);
				if(getStrokeHandlePoints()[0].distance(c)>12) {d=0.5;}
				rect.setStrokeWidth((float)d);
				if(strokeUndo!=null)	
					{
					strokeUndo.establishFinalState();
					addStrokeUndo(w);
				}
			} 
		}
		
		
		public void showJPopup(CanvasMouseEventWrapper w) {
			EditAndColorizeMultipleItems multi = new EditAndColorizeMultipleItems(true, getStrokeWidth());
			multi.setSelector(w.getSelectionSystem());
			multi.getPopup().showForMouseEvent(w);;
		}
		
		private void addStrokeUndo(CanvasMouseEventWrapper w) {
			if (!w.getAsDisplay().getUndoManager().hasUndo(strokeUndo))
				w.getAsDisplay().getUndoManager().addEdit(strokeUndo);
		}
		
		@Override
		public void handlePress(CanvasMouseEventWrapper w) {
			if (this.isStrokeHandle()) strokeUndo= new UndoStrokeEdit(rect);
			if(w.isPopupTrigger()) {
				showJPopup(w);;
				return;
			}
			if (isStrokeHandle()&&w.clickCount()==2) {
				double nSW = StandardDialog.getNumberFromUser("Input Stroke Width", rect.getStrokeWidth());
				if (nSW<200) {
					rect.setStrokeWidth((float) nSW);
					addStrokeUndo(w);
				}
				
			} 
			else 
			if (this.isRotationHandle()&&w.clickCount()==2) {
				double nSW = StandardDialog.getNumberFromUser("Input angle", rect.getAngle(), true);
				rect.setAngle(nSW);
			} else if (w.clickCount()==2) {
				new WidthAndHeightDialog(rect).showDialog();
			}
			
			
		}

		public boolean isStrokeHandle() {
			return this.getHandleNumber()==STROKE_HANDLE_TYPE;
		}
		
		/**What to do when a handle is moved from point p1 to p2*/
		public void handleMove(Point2D p1, Point2D p2) {
			rect.handleSmartMove(getHandleNumber(), (Point) p1,  (Point) p2) ;
			
			
		}
		
	}
	
	
	
	@Override
	public SmartHandleList getSmartHandleList() {
		if (smartList==null)smartList=this.createSmartHandleList(); 
		if (!superSelected) return smartList;
		return SmartHandleList.combindLists(smartList, getButtonList());
		//return smartList;
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

	public String getShapeName() {
		return "Rectangle";
	}



	

	
}
