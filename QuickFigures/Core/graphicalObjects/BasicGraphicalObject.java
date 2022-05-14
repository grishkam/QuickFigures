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
 * Date Modified: May 1, 2021
 * Version: 2022.1
 */
package graphicalObjects;


import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import animations.KeyFrameCompatible;
import applicationAdapters.CanvasMouseEvent;
import animations.Animation;
import animations.KeyFrameAnimation;
import fLexibleUIKit.MenuItemExecuter;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.GraphicUtil;
import handles.DecorativeSmartHandleList;
import handles.HandleListFilter;
import handles.HasHandles;
import keyFrameAnimators.BasicGraphicObjectKeyFrameAnimator;
import locatedObject.AttachmentPosition;
import locatedObject.Hideable;
import locatedObject.LocationChangeListener;
import locatedObject.LocationChangeListenerList;
import locatedObject.ObjectContainer;
import locatedObject.RectangleEdgePositions;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import undo.AbstractUndoableEdit2;
import undo.UndoManagerPlus;
import undo.UndoScalingAndRotation;
import menuUtil.HasUniquePopupMenu;

/**The abstract superclass for many graphical objects*/
public abstract class BasicGraphicalObject implements GraphicalObject, HasHandles, HasUniquePopupMenu,KnowsSetContainer,KnowsParentLayer,  Hideable, KeyFrameCompatible  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected int locationType=RectangleEdgePositions.UPPER_LEFT;//indicates which location will actually be returned and set by the getlocation and set location methods

	/**the angle of the object*/
	protected double angle=0;
	
	/**used by certain subclasses*/
	public HandleListFilter handleModifier;
	
	LocationChangeListenerList listeners=new LocationChangeListenerList();
	protected boolean handlesHidden=false;
	

	protected GraphicLayer parent;// the parent layer
	protected Animation animation=null;//if an animation has been set up
	
	transient boolean hidden=false;//set to true if hidden
	
	
	protected String name="";//the name of the object
	
	protected transient boolean selected=false, superSelected=false;// the selection state of the object

	
	protected double x=0;//location of an object
	protected double y=0;//location of an object
	
	Object key=null;
	public final HashMap<String, Object> map=new HashMap<String, Object>();
	
	transient GraphicUtil Gu=new GraphicUtil();
	transient boolean isDead=false;
	
	/**A smart handle list that is just for drawing. is not used by most subclasses*/
	protected transient DecorativeSmartHandleList handleBoxes=new DecorativeSmartHandleList();
	
	/**The worksheet that contains this object is stored here. TODO: determine if this is set properly*/
	private transient FigureDisplayWorksheet parentWorksheet;
	
	/***/
	private AttachmentPosition attachementPosition=null;

	protected int userLocked=NOT_LOCKED;
	
	
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
	
	/**The list of items that are informed when the object size changes*/
	public LocationChangeListenerList getListenerList() {
		return listeners;
	}
	/**The list of items that are informed when the object size changes
	 * never returns null*/
	public LocationChangeListenerList getListeners() {
		if (listeners==null) {
			listeners=new LocationChangeListenerList();
			
		}
		return listeners;
	}
	
	/**Calls the objectMoved method for each listener */
	public void notifyListenersOfMoveMent() {
		
		try {
			LocationChangeListenerList list = getListeners();
			list.notifyListenersOfMoveMent(this);
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	
	}
	
	/**Calls the object size changed method for each listener */
	protected void notifyListenersOfSizeChange() {
		ArrayList<LocationChangeListener> list = getListeners();
		for(LocationChangeListener lis: list) try {
			if (lis==null) continue;
			lis.objectSizeChanged(this);
		} catch(Throwable t) {}
		
	}
	
	/**Calls the user size changed method for each listener */
	public void notifyListenersOfUserSizeChange() {
		getListeners().notifyListenersOfUserSizeChange(this);
		
	}
	
	/**Calls the object eliminated method for each listener */
	public void notifyListenersOfDeath() {
		 LocationChangeListenerList list = getListeners();
		list.notifyListenersOfDeath(this);
	}
	
	/**Adds a location change listener*/
	@Override
	public void addLocationChangeListener(LocationChangeListener l) {
		if (getListeners().contains(l)) return;
		
		getListeners().add(l);
		
	}
	/**removes a location change listener*/
	@Override
	public void removeLocationChangeListener(LocationChangeListener l) {
		getListeners().remove(l);
	}
	/**removes all location change listener*/
	@Override
	public void removeAllLocationChangeListeners() {
		getListeners().clear();
	}
	

	/**sets object as selected*/
	@Override
	public void select() {
		selected=true;

	}

	/**sets object as not selected*/
	@Override
	public void deselect() {
		selected=false;
		superSelected=false;
	}

	/**returns true if the object is selected*/
	@Override
	public boolean isSelected() {
		return selected;
	}
	
	/**returns a tag*/
	@Override
	public Object getTag(String Key) {
		return map.get(Key);
	}

	/**returns all the tags*/
	@Override
	public HashMap<String, Object> getTagHashMap() {
		return map;
	}
	
	/**moves the object*/
	@Override
	public void moveLocation(double xmov, double ymov) {
		x=x+xmov;
		y=y+ymov;
		notifyListenersOfMoveMent();
	}
	
	/**returns the location*/
	public Point2D getLocation() {
		return new Point2D.Double(x,y);
	}

	/**sets the location. does not trigger the listeners*/
	public void setLocation(double x,double y) {
		this.x=x;
		this.y=y;
	}
	
	/**sets the location. does not trigger the listeners*/
	public void setLocation( Point2D p) {
		setLocation(p.getX(), p.getY());
	}
	
	/**calls the object eliminated method*/
	public void kill() {
		notifyListenersOfDeath();
		isDead=true;
	}
	
	/**this method is called after a mouse event
	 * shows and options dialog if the item is double clicked.
	 * will be deprecated in the future*/
	@Override
	public void handleMouseEvent(CanvasMouseEvent me, int handlenum, int button, int clickcount, int type,
			int... other) {
		if (clickcount<2||handlenum>NO_HANDLE_) return;
		if (clickcount==2) showOptionsDialog();

	}
	
	/**Implementation depends on the subclass*/
	@Override
	public Object dropObject(Object ob, int x, int y) {
		Point p = new Point((int)getBounds().getCenterX(), (int)getBounds().getCenterY());
		
		if (ob instanceof Color) {
			Color c = (Color) ob;
			dropColor(c,new Point(x,y));
		} 
		
		return dropOther(ob, p);
	}

	/**Implementation depends on the subclass*/
	public Object dropOther(Object ob, Point p) {
		// TODO Auto-generated method stub
		return null;
	}



	public void dropColor(Color c, Point p) {
		
	}
	
	/**returns the attachment position*/
	public AttachmentPosition getAttachmentPosition() {
		return attachementPosition;
	}

	/**sets the attachment position*/
	public void setAttachmentPosition(AttachmentPosition snappingBehaviour) {
		this.attachementPosition = snappingBehaviour;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	/**returns the menu supplier for this object*/
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
	
	
	/**returns the handle number at the raw location of the lickpoint*/
	@Override
	public int handleNumber(double x, double y) {
		{
			if (handleBoxes==null||handleBoxes.size()==1) return NO_HANDLE_;
				return handleBoxes.handleNumberForClickPoint(x, y);
			
		}
	}

	/**eliminates the */
	public void clearHandleBoxes() {
		handleBoxes=new DecorativeSmartHandleList();
	}
	
	/**sets the worksheet that contains this object*/
	@Override
	public void setGraphicSetContainer(FigureDisplayWorksheet gc) {
		this.parentWorksheet=gc;
		
	}
	
	/**if the worksheet is known, this updates the display*/
	@Override
	public void updateDisplay() {
		if( this.parentWorksheet==null) return;
		parentWorksheet.updateDisplay();
	}
	
	/**returns the parent layer*/
	@Override
	public GraphicLayer getParentLayer() {
		return parent;
	}

	/**set the parent layer*/
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
	
	/**makes this item a primary selected item*/
	public boolean makePrimarySelectedItem(boolean isFirst) {
		this.superSelected=isFirst;
		if(isFirst) this.select();
		return superSelected;
	}
	
	/**Sets the location*/
	public void setLocationUpperLeft(Point2D p2) {
		setLocationUpperLeft(p2.getX(),p2.getY());
	}
	
	/**scales the point location
	 * @param p1 the point to be scaled and returned
	 * @param the center with respect to scaling
	 * @param sx the x axis scale factor
	 * @param sy the y axis scale factor
	 * */
	protected static Point2D scalePointAbout(Point2D p1, Point2D about, double sx, double sy) {
		double dx=p1.getX()-about.getX();
		double dy=p1.getY()-about.getY();
		dx*=sx;
		dy*=sy;
		p1.setLocation(dx+about.getX(), dy+about.getY());
		
		return p1;
		
	}
	
	/**returns true if this item is inside of the rectangle*/
	@Override
	public boolean isInside(Rectangle2D rect) {
		return rect.contains(getBounds());
	}
	
	/**returns true if this item intersects the rectangle*/
	@Override
	public boolean doesIntersect(Rectangle2D rect) {
		return getOutline().intersects(rect);
	}
	
	/**returns the location type. What point on the bounding box is the location (@see RectangleEdgePosisions)*/
	public int getLocationType() {
		return locationType;
	}
	
	/**sets the location type. What point on the bounding box is the location (@see RectangleEdgePosisions)*/
	public void setLocationType(int locationType) {
		this.locationType = locationType;
	}
	
	/**Returns the user locked property
	 * @see LocatedObject2D  
	 * 
	 * */
	public int isUserLocked() {
		return userLocked;
	}
	
	/**Called whenever a handle is released. In this case, does nothing */
	public void handleRelease(int handlenum, Point p1, Point p2) {}
	/**Called whenever a handle is pressed. In this case, does nothing */
	public void handlePress(int handlenum,  Point p2) {}
	/**Called whenever a handle is moved. In this case, does nothing*/
	public void handleMove(int handlenum, Point p1, Point p2) {}

	/**returns the animation for the object*/
	public Animation getAnimation() {return animation;}

	/**sets the animation for the object*/
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
	
	/**if there is not animation, creates one and returns it*/
	public  KeyFrameAnimation getOrCreateAnimation() {
		if (animation instanceof KeyFrameAnimation) return (KeyFrameAnimation) animation;
		animation=new BasicGraphicObjectKeyFrameAnimator(this);
		return (KeyFrameAnimation) animation;
	}
	
	/**returns the rotation transform for the shape*/
	public AffineTransform getRotationTransform() {
		double xr = getCenterOfRotation().getX();
		double yr = getCenterOfRotation().getY();
		return AffineTransform.getRotateInstance(-angle, xr, yr);
	}
	
	/**returns the pivot point about which this object rotates */
	public Point2D getCenterOfRotation() {
		Rectangle b = this.getBounds();
		return new Point2D.Double(b.getCenterX(), b.getCenterY());
	}
	
	/**returns the current angle*/
	public double getAngle() {
		return angle;
	}

	/**set the current angle*/
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
	
	
	
	/**provides an edit that can be used to undo a mouse drag */
	public AbstractUndoableEdit2 provideDragEdit() {
		return new UndoScalingAndRotation(this);
		
	}
	
	/**returns an undo manager that is available
	 * TODO: find a way to replace this*/
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
	
	/**set to true to hide the handles*/
	public void hideHandles(boolean b) {
		handlesHidden=b;
	}
	
	/**returns nothing but subclass may return  */
	public Object getScaleWarning() {
		return null;
	}
	
	
	
	
	
	/**
	 * @returns the handle list filter is there is one
	 */
	public  HandleListFilter getHandleFilter() {
		return handleModifier;
	}
	
	public void setHandleListFilter(HandleListFilter handleSpecialization) {handleModifier=handleSpecialization;}
	
	
	
}
