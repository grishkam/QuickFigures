package graphicalObjects_BasicShapes;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import animations.KeyFrameCompatible;
import applicationAdapters.CanvasMouseEventWrapper;
import animations.Animation;
import animations.KeyFrameAnimation;
import fLexibleUIKit.MenuItemExecuter;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjectHandles.HandleRect;
import graphicalObjects.CordinateConverter;
import graphicalObjects.FigureDisplayContainer;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.KnowsSetContainer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import keyFrameAnimators.BasicGraphicObjectKeyFrameAnimator;
import menuUtil.PopupMenuSupplier;
import undo.AbstractUndoableEdit2;
import undo.UndoManagerPlus;
import undo.UndoScalingAndRotation;
import utilityClassesForObjects.Hideable;
import utilityClassesForObjects.LocationChangeListener;
import utilityClassesForObjects.LocationChangeListenerList;
import utilityClassesForObjects.ObjectContainer;
import utilityClassesForObjects.RectangleEdgePosisions;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.SnappingPosition;
import menuUtil.HasUniquePopupMenu;

/**The abstract superclass for many graphical objects*/
public abstract class BasicGraphicalObject implements GraphicalObject, HasUniquePopupMenu,KnowsSetContainer,KnowsParentLayer,  Hideable, KeyFrameCompatible  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int locationType=RectangleEdgePosisions.UPPER_LEFT;//indicates which location will actually be returned and set by the getlocation and set location methods

	protected double angle=0;
	LocationChangeListenerList listeners=new LocationChangeListenerList();
	protected boolean handlesHidden=false;;
	

	protected GraphicLayer parent;
	protected Animation animation=null;
	
	transient boolean hidden=false;
	
	
	protected String name="";//the name of the object
	
	protected transient boolean selected=false, superSelected=false;// the selection state of the object


	protected double x=0;
	protected double y=0;
	
	Object key=null;
	public HashMap<String, Object> map=new HashMap<String, Object>();
	transient GraphicUtil Gu=new GraphicUtil();
	transient boolean isDead=false;
	protected transient ArrayList<HandleRect> handleBoxes=new ArrayList<HandleRect> ();
	transient FigureDisplayContainer setContainer;
	private SnappingPosition snappingBehaviour=null;

	protected int userLocked=0;
	
	
	protected GraphicUtil getGrahpicUtil() {
		if (Gu==null)
			Gu=new GraphicUtil();
		return Gu;
	}
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String st) {
		name=st;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	public LocationChangeListenerList getListenerList() {
		return listeners;
	}
	
	public void notifyListenersOfMoveMent() {
		
		LocationChangeListenerList list = getListeners();
		list.notifyListenersOfMoveMent(this);
	
	}
	
	public void notifyListenersOfSizeChange() {
		ArrayList<LocationChangeListener> list = getListeners();
		for(LocationChangeListener lis: list) {
			if (lis==null) continue;
			lis.objectSizeChanged(this);
		}
		
	}
	
	public void notifyListenersOfUserSizeChange() {
		getListeners().notifyListenersOfUserSizeChange(this);
		
	}
	
	public void notifyListenersOfDeath() {
		 LocationChangeListenerList list = getListeners();
		list.notifyListenersOfDeath(this);
		//IssueLog.log("will notify listeners of death "+this);
	}
	
	public LocationChangeListenerList getListeners() {
		if (listeners==null) {
			listeners=new LocationChangeListenerList();
			
		}
		return listeners;
	}
	@Override
	public void addLocationChangeListener(LocationChangeListener l) {
		if (getListeners().contains(l)) return;
		
		getListeners().add(l);
		
	}
	@Override
	public void removeLocationChangeListener(LocationChangeListener l) {
		getListeners().remove(l);
	}
	@Override
	public void removeAllLocationChangeListeners() {
		getListeners().clear();
	}
	

	
	@Override
	public void select() {
		selected=true;

	}

	@Override
	public void deselect() {
		selected=false;
		superSelected=false;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}
	

	@Override
	public Object getTag(String Key) {
		return map.get(Key);
	}

	@Override
	public HashMap<String, Object> getHash() {
		return map;
	}
	

	@Override
	public Object getLayerKey() {
		return key;
	}

	@Override
	public void setLayerKey(Object o) {
		key=o;
		
	}
	
	@Override
	public void moveLocation(double xmov, double ymov) {
		x=x+xmov;
		y=y+ymov;
		notifyListenersOfMoveMent();
	}
	

	public Point2D getLocation() {
		return new Point2D.Double(x,y);
	}

	
	public void setLocation(double x,double y) {
		this.x=x;
		this.y=y;
	}
	
	public void setLocation( Point2D p) {
		setLocation(p.getX(), p.getY());
	}
	
	public void kill() {
		notifyListenersOfDeath();
		isDead=true;
	}
	
	@Override
	public void handleMouseEvent(CanvasMouseEventWrapper me, int handlenum, int button, int clickcount, int type,
			int... other) {
		if (clickcount<2||handlenum>-1) return;
		if (clickcount==2) showOptionsDialog();

	}
	
	@Override
	public Object dropObject(Object ob, int x, int y) {
		Point p = new Point((int)getBounds().getCenterX(), (int)getBounds().getCenterY());
		
		if (ob instanceof Color) {
			Color c = (Color) ob;
			dropColor(c,new Point(x,y));
		} /**else 
		if (ob instanceof MouseEvent) {
				 return dropMouseEvent( (MouseEvent) ob, new Point(x,y));
		}*/
		
		return dropOther(ob, p);
	}


	public Object dropOther(Object ob, Point p) {
		// TODO Auto-generated method stub
		return null;
	}

/**
	public Object dropMouseEvent(MouseEvent ob, Point p) {
				if (ob.getClickCount()==2) this.showOptionsDialog();
		return null;
	}
*/

	public void dropColor(Color c, Point p) {
		// TODO Auto-generated method stub
		
	}
	
	public SnappingPosition getSnapPosition() {
		return snappingBehaviour;
	}


	public void setSnapPosition(SnappingPosition snappingBehaviour) {
		this.snappingBehaviour = snappingBehaviour;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	
	public PopupMenuSupplier getMenuSupplier() {
		return new MenuItemExecuter(this);
	}
	
	
	/**given a center of rotation and a point, returns the angle*/
	public static double distanceFromCenterOfRotationtoAngle(Point2D pcent, Point2D p2) {
		//Point2D pcent=getCenterOfRotation();
		double xc2=p2.getX()-pcent.getX();
		double yc2=p2.getY()-pcent.getY();

		double angle=-Math.atan((yc2)/(xc2));
		if (!Double.isNaN(angle)) {
			if (xc2<0) angle+=Math.PI;
			while (angle<0) angle+=Math.PI*2;
			return angle;
			}
		return 0;
	}
	
	
	@Override
	public int handleNumber(int x, int y) {
		{
			if (handleBoxes==null||handleBoxes.size()==1) return -1;
			for(int i=0; i<handleBoxes.size(); i++) {
				
				if (handleBoxes.get(i).contains(x, y))  {
					return i;
				}
			}
			return -1;
		}
	}

	
	public void clearHandleBoxes() {
		handleBoxes=new ArrayList<HandleRect>();
	}
	@Override
	public void setGraphicSetContainer(FigureDisplayContainer gc) {
		this.setContainer=gc;
		
	}
	
	@Override
	public void updateDisplay() {
		if( this.setContainer==null) return;
		setContainer.updateDisplay();
	}
	
	@Override
	public GraphicLayer getParentLayer() {
		return parent;
	}


	@Override
	public void setParentLayer(GraphicLayer parent) {
		this.parent=parent;
		
	}
	@Override
	public boolean isHidden() {
		return hidden;
	}
	@Override
	public void setHidden(boolean b) {
		hidden=b;
	}
	
	public boolean makePrimarySelectedItem(boolean isFirst) {
		this.superSelected=isFirst;
		if(isFirst) this.select();
		return superSelected;
	}
	
	public void setLocationUpperLeft(Point2D p2) {
		setLocationUpperLeft(p2.getX(),p2.getY());
	}
	
	protected static Point2D scaleAbout(Point2D p1, Point2D about, double sx, double sy) {
		double dx=p1.getX()-about.getX();
		double dy=p1.getY()-about.getY();
		dx*=sx;
		dy*=sy;
		p1.setLocation(dx+about.getX(), dy+about.getY());
		
		return p1;
		
	}
	
	@Override
	public boolean isInside(Rectangle2D rect) {
		return rect.contains(getBounds());
	}
	
	@Override
	public boolean doesIntersect(Rectangle2D rect) {
		return getOutline().intersects(rect);
	}
	
	public int getLocationType() {
		return locationType;
	}
	public void setLocationType(int locationType) {
		this.locationType = locationType;
	}
	
	public int isUserLocked() {
		return userLocked;
	}
	
	public void drawLocationAnchorHandle(Graphics2D g2d, CordinateConverter<?> cords) {
		Point2D p = RectangleEdges.getLocation(getLocationType(), this.getBounds());
		 getGrahpicUtil().setHandleFillColor(Color.red);
	//	 IssueLog.log("will try to draw red handle");
		getGrahpicUtil().drawHandlesAtPoint(g2d, cords, p);
		getGrahpicUtil().setHandleFillColor(Color.white);
	}
	
	public void handleRelease(int handlenum, Point p1, Point p2) {}
	public void handlePress(int handlenum,  Point p2) {}

	public Animation getAnimation() {return animation;}


	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
	
	
	public  KeyFrameAnimation getOrCreateAnimation() {
		if (animation instanceof KeyFrameAnimation) return (KeyFrameAnimation) animation;
		animation=new BasicGraphicObjectKeyFrameAnimator(this);
		return (KeyFrameAnimation) animation;
	}
	
	public AffineTransform getRotationTransform() {
		double xr = getCenterOfRotation().getX();
		double yr = getCenterOfRotation().getY();
		return AffineTransform.getRotateInstance(-angle, xr, yr);
	}
	
	public Point2D getCenterOfRotation() {
		Rectangle b = this.getBounds();
		return new Point2D.Double(b.getCenterX(), b.getCenterY());
	}
	
	
	public double getAngle() {
		
		return angle;
	}

	
	public void setAngle(double angle) {	
		if (angle==this.getAngle()) return;
		Point2D p = this.getLocation();
		this.angle=angle;
		this.setLocation(p.getX(), p.getY());
	}

	/**adds the argument to the current angle*/
	public void rotate(double angle) {
		Point2D p = this.getLocation();
		this.angle+=angle;
		this.setLocation(p.getX(), p.getY());
	}
	
	
	
	/**And edit is requested */
	public AbstractUndoableEdit2 provideDragEdit() {
		return new UndoScalingAndRotation(this);
		
	}
	
	public UndoManagerPlus getUndoManager() {
		return new CurrentFigureSet().getCurrentlyActiveDisplay().getUndoManager();
	}
	
	/**finds the layer pane at the top of the tree*/
	public ObjectContainer getTopLevelContainer() {
		if(this.getParentLayer()==null) return null;
		GraphicLayer layer = getParentLayer().getTopLevelParentLayer();
		if(layer instanceof GraphicLayerPane) 
			return (ObjectContainer) layer;
		
		return null;
	}
	public void hideHandles(boolean b) {
		handlesHidden=b;
	}
	
	
	public Object getScaleWarning() {
		return null;
	}
	
}
