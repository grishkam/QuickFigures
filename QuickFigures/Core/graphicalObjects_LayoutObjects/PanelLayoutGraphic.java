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
 * Date Modified: Nov 23, 2021
 * Version: 2022.0
 */
package graphicalObjects_LayoutObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Icon;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.GenericImage;
import applicationAdapters.ImageWorkSheet;
import figureOrganizer.FigureType;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.CordinateConverter;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import handles.HasSmartHandles;
import handles.AttachmentPositionHandle;
import handles.SmartHandle;
import handles.SmartHandleList;
import icons.IconSet;
import icons.LayoutTreeIcon;
import imageDisplayApp.CanvasOptions;
import imageMenu.CanvasAutoResize;
import layersGUI.HasTreeLeafIcon;
import layout.BasicObjectListHandler;
import layout.PanelContentExtract;
import layout.PanelLayout;
import layout.PanelLayoutContainer;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.BasicLayoutEditor;
import layout.basicFigure.LayoutSpaces;
import locatedObject.ArrayObjectContainer;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.LocationChangeListener;
import locatedObject.AttachedItemList;
import locatedObject.ObjectContainer;
import locatedObject.RectangleEdges;
import locatedObject.TakesAttachedItems;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import objectDialogs.PanelLayoutDisplayOptions;
import menuUtil.HasUniquePopupMenu;
import popupMenusForComplexObjects.AttachedItemMenu;
import sUnsortedDialogs.LayoutPanelSizeModifyDialog;
import utilityClasses1.ArraySorter;
import utilityClasses1.TagConstants;

/**A graphical object that stores a layout, displays the layout, includes handles for editing the layout
 * this abstract superclass is somewhat complex*/
public abstract class PanelLayoutGraphic extends BasicGraphicalObject implements PanelLayoutContainer, TakesAttachedItems,KnowsParentLayer, HasUniquePopupMenu, LocationChangeListener, HasTreeLeafIcon, HasSmartHandles {
	
	{this.setName("Layout");}
	private static final long serialVersionUID = 1L;
	private int userLocked=LOCKED;//determines whether user handle drags can mobe this item
	
	private transient SmartHandleList panelHandleList=new SmartHandleList();;
	private transient SmartHandleList allrefPointHandles=new SmartHandleList();;
	private transient SmartHandleList handleBoxes2=new SmartHandleList();
	ArrayList<PanelLayoutHandle> panelMotionHandles=new ArrayList<PanelLayoutHandle>();
	
	
	public Color panelColor=Color.red;
	public Color boundryColor=Color.blue;
	private int handleArmDistance=12;
	private int handleArmsize=3;
	public static final int handleIDFactor=10000;//defines the difference in handle ids between motion handles and resize handles
	protected static final int ROW_HEIGTH_HANDLE=0, PANEL_LOCATION_HANDLE=1, COLUMN_WIDTH_HANDLE=2, ROW_HEIGHT_HANDLE_UNIFORM=3,COLUMN_WIDTH_HANDLE_UNIFORM=4;
	protected static final int LAYOUT_LOCATION_HANDLE=5*handleIDFactor, RightHandleID = LAYOUT_LOCATION_HANDLE+1,
					BottomHandleID=LAYOUT_LOCATION_HANDLE+2, LeftHandleID=LAYOUT_LOCATION_HANDLE+3, TopHandleID=LAYOUT_LOCATION_HANDLE+4;
	public static final int ADD_ROW_HANDLE_ID=LAYOUT_LOCATION_HANDLE+5,
			ADD_COL_HANDLE_ID=LAYOUT_LOCATION_HANDLE+6, 
			RepackPanelsHandle=LAYOUT_LOCATION_HANDLE+7, SELECT_ALL_HANDLE=LAYOUT_LOCATION_HANDLE+8, SCALE_HANDLE=LAYOUT_LOCATION_HANDLE+9;
	
	transient ArrayList<PanelContentExtract> contentstack;
	private int editMode=0;
	private transient boolean alwaysShow=false;//whether to draw the layout even when not selected
	private boolean filledPanels=false;
	
	
	public boolean hideAttachedItemHandles=false;
	
	protected PanelLayout layout=new BasicLayout();
	private  HashMap<LocatedObject2D, Integer> panelLocations=new HashMap<LocatedObject2D, Integer>();
	private FigureType figureType;
	private int strokeWidth=2;//how thick the rectangles for the panels are drawn
	
	
	protected transient BasicLayoutEditor editor;

	
	public PanelLayoutGraphic() {}
	
	public PanelLayoutGraphic(PanelLayout p) {
		this.layout=p;
	}
	
	
	public int getEditMode() {
		return editMode;
	}

	public void setEditMode(int editMode) {
		this.editMode = editMode;
	}

	
	
	AttachedItemList lockedItems=new AttachedItemList(this);
	  private AttachedItemList panelSizeDefiningItems=new AttachedItemList(this);
	 
	  /***/
	
	  
	public AttachedItemList getLockedItems() {
		if (lockedItems==null) lockedItems=new AttachedItemList(this);
		return lockedItems;
	}
	
	/**Adds an attached item to the layout*/
	public void addLockedItem(LocatedObject2D l) {
	
		getLockedItems().add(l);
	
		/**
		if (l instanceof TakesLockedItems) {
			TakesLockedItems t=(TakesLockedItems) l;
			t.removeLockedItem(this);
		}*/
		this.mapPanelLocation(l);
	
		this.snapLockedItems();
		
		generateHandlesForAttachedItems(l);
		
		}

	/**creates handles for the attached item
	 * @param l
	 */
	public void generateHandlesForAttachedItems(LocatedObject2D l) {
		if (l instanceof TextGraphic) {
			generateHandleForText(l);	
		
		}
		
		if (l instanceof ImagePanelGraphic) {
			generateHandleForImage(l);	
		
		}
	}

	/**generates a handle for an attached image panel
	 * not implemented here. see subclass*/
	protected void generateHandleForImage(LocatedObject2D l) {
		
		
	}

	/**generates a handle for an attached text item*/
	protected void generateHandleForText(LocatedObject2D l) {
		SmartHandleList list = this.getAttachedItemHandleList();
		list.add(new AttachmentPositionHandle(this, l, 1000000000+list.size()));
	}
		
	/**If the given item is attached to this layout
	 * detaches the item from the layout*/
	@Override
	public void removeLockedItem(LocatedObject2D l) {
	
		getLockedItems().remove(l);
		
		this.getPanelLocations().remove(l);
		if (this.getPanelSizeDefiningItems().contains(l)) {
			this.getPanelSizeDefiningItems().remove(l);
			l.removeLocationChangeListener(this);
		}
		getAttachedItemHandleList().removeLockedItemHandle(l);
		
	}
	
	@Override
	public boolean hasLockedItem(LocatedObject2D l) {
		return getLockedItems().contains(l);
	}
	
	

	
	

	@Override
	public Point2D getLocationUpperLeft() {
		Point2D p = this.getPanelLayout().getReferenceLocation();
		return p;
	}

	@Override
	public void setLocationUpperLeft(double x, double y) {
		Point2D p = this.getBounds().getLocation();
		double x2=x-p.getX();
		double y2=y-p.getY();
		this.moveLocation(x2, y2);

	}

	@Override
	public int isUserLocked() {
		return userLocked;
	}

	/**Not implemented here, subclasses may implement this*/
	@Override
	public PanelLayoutGraphic copy() {
		return null;
	}

	@Override
	public boolean doesIntersect(Rectangle2D rect) {
		if (this.getPanelLayout().getPanel(1).intersects(rect)) return true;
		return false;
	}

	@Override
	public boolean isInside(Rectangle2D rect) {
		return rect.contains(getPanelLayout().getBoundry().getBounds());
	}

	@Override
	public Rectangle getExtendedBounds() {
		return this.getPanelLayout().getBoundry().getBounds();
	}

	@Override
	public Shape getOutline() {
		Shape allPanelArea = getPanelLayout().allPanelArea();
		return allPanelArea;
	}

	@Override
	public Rectangle getBounds() {
		return getPanelLayout().getBoundry().getBounds();
	}

	/**not implemented */
	@Override
	public void setLocationType(int n) {

	}

	/**not implemented */
	@Override
	public int getLocationType() {
		return 0;
	}

	/**Draws the layout*/
	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		
		 graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		snapLockedItems();//ensures all attached items are placed in their proper locations
		getPanelLayout().getPanels();
			
			 
			 if (this.isSelected()||isAlwaysShow()) 
			 		{
				
					Rectangle2D[] ps = this.getPanelLayout().getPanels();
					 graphics.setStroke(new BasicStroke((float) this.getStrokeWidth()));
					 graphics.setColor(getBoundryColor());
					 getGrahpicUtil().drawRectangle(graphics, cords,getPanelLayout().getBoundry().getBounds(), false);
					 this.drawLayoutTypeSpecific(graphics, cords);
					 int number=1;
					 for(Rectangle2D r:ps) {
						 drawPanel(graphics, cords, number, r);
						number++;
					 	}
					 if (this.isSelected())
						 drawHandles(graphics,cords);
			
			 
			 		}

	}

	/**color used to draw the bounds of the layout*/
	public Color getBoundryColor() {
		return boundryColor;
	}
	
	/**Draws certain aspects of the layout that vary depending on the subclass
	 * not implemented here*/
	protected void drawLayoutTypeSpecific(Graphics2D graphics,
			CordinateConverter cords) {
		
		
	}

	/**draws a rectangle indicating the location of a layout panel*/
	protected void drawPanel(Graphics2D graphics, CordinateConverter cords, int number, Rectangle2D r) {
		if (r==null) return;
		 graphics.setColor(getPanelColor());
		this.getGrahpicUtil().drawRectangle(graphics, cords, r, false);
		if (isFilledPanels()) getGrahpicUtil().fillRectangle(graphics, cords,r);
					 this.getGrahpicUtil().drawString(graphics, cords, ""+number, new Point2D.Double(r.getX(), r.getY()), new Font("Arial", Font.BOLD, 12+this.getStrokeWidth()/2), Color.green.darker(), 0, false);
	}

	public Color getPanelColor() {
		return panelColor;
	}
	
	
	
	
	
	public void clearHandleBoxes() {
		if (panelMotionHandles!=null)
			panelMotionHandles.clear();
		if (getHandleBoxes2()!=null)
			getHandleBoxes2().clear();
		
	}
	
	
	
	public void drawHandles(Graphics2D graphics, CordinateConverter cords) {
		Rectangle2D[] ps = this.getPanelLayout().getPanels();
		clearHandleBoxes();
		
		
		
		addFirstLayerHandles(getHandleBoxes2());
		
		int w=0;//panel numer from 
		for(Rectangle2D r:ps) {
			createHandlesForPanel(w, r, getHandleBoxes2());
			
			w++;
		}
		
		
		createReferencePointHandles(cords);
		
		addAdditionalHandles(getHandleBoxes2());
		
		this.getAllSmartHandles().draw(graphics, cords);
		
	}

	/**creates the handles that are used to adjust the label spaces on
	 * the sids of some layouts. in other layout types
	 * @param cords
	 */
	void createReferencePointHandles(CordinateConverter cords) {
		int hdisplace=(int) (15/cords.getMagnification());
		double handleArmDistance2 = handleArmDistance;
		double handleArmsize2 = handleArmsize;
		if (handleArmDistance2<15)handleArmDistance2=15;
		if (handleArmsize2<3)handleArmsize2=3;
		if (hdisplace<20)hdisplace=20;
		this.allrefPointHandles=new SmartHandleList();
		PanelLayoutHandle rightRefPointHandle;
		PanelLayoutHandle bottomRefPointHandle;
		PanelLayoutHandle leftRefPointHandle;
		PanelLayoutHandle topRefPointHandle;
		PanelLayoutHandle  refPointHandle;
		
		Rectangle bounds = this.getBounds();
		
		Point p = new Point(bounds.x, bounds.y);
		
		p = new Point((int)getBounds().getMaxX(), bounds.y-hdisplace);
		
		Point p2 = (Point) p.clone();
		p2.y+=handleArmDistance2+5;
		rightRefPointHandle =createHandle(  p, RightHandleID, handleArmsize2);
		rightRefPointHandle.hasLine(p2);
				
		allrefPointHandles.add(rightRefPointHandle);
		
		p = new Point((int)bounds.getMaxX(), (int)(bounds.getMaxY()+hdisplace));

		p2 = (Point) p.clone();
		p2.y-=handleArmDistance2+5;
		rightRefPointHandle =createHandle(  p, RightHandleID, handleArmsize2);
		rightRefPointHandle.hasLine(p2);
		allrefPointHandles.add(rightRefPointHandle);
		
		p = new Point(bounds.x-hdisplace, (int)bounds.getMaxY());
	
		p2 = (Point) p.clone();
		p2.x+=handleArmDistance+5;
		bottomRefPointHandle =createHandle(  p, BottomHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p,p2);
		bottomRefPointHandle.hasLine(p2);
	
		allrefPointHandles.add(bottomRefPointHandle);
		
		p = new Point((int)bounds.getMaxX()+hdisplace, (int)bounds.getMaxY());

		p2 = (Point) p.clone();
		p2.x-=handleArmDistance+5;
		bottomRefPointHandle =createHandle(  p, BottomHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p,p2);
		bottomRefPointHandle.hasLine(p2);
	
		allrefPointHandles.add(bottomRefPointHandle);
		
		p = new Point((int)bounds.getX(), (int)bounds.getMaxY()+hdisplace);

		p2 = (Point) p.clone();
		p2.y-=handleArmDistance2;
		leftRefPointHandle =createHandle(  p, LeftHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p, p2);
		leftRefPointHandle.hasLine(p2);
	
		allrefPointHandles.add(leftRefPointHandle);
		
		p = new Point((int)bounds.getX(), bounds.y-hdisplace);

		p2 = (Point) p.clone();
		p2.y+=handleArmDistance2;
		leftRefPointHandle =createHandle(  p, LeftHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p, p2);
		leftRefPointHandle.hasLine(p2);
	
		allrefPointHandles.add(leftRefPointHandle);
		
		p = new Point(bounds.x-hdisplace, (int)bounds.getY());
	
		p2 = (Point) p.clone();
		p2.x+=handleArmDistance2;
		topRefPointHandle =createHandle(  p, TopHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p,p2);
		topRefPointHandle.hasLine(p2);//this.getGrahpicUtil().setHandleSize(3);
		allrefPointHandles.add(topRefPointHandle);
		
		p = new Point((int)bounds.getMaxX()+hdisplace, (int)bounds.getY());

		p2 = (Point) p.clone();
		p2.x-=handleArmDistance2;
		topRefPointHandle =createHandle(  p,TopHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p,p2);
		topRefPointHandle.hasLine(p2);//this.getGrahpicUtil().setHandleSize(3);
		allrefPointHandles.add(topRefPointHandle);
		
		p = new Point(bounds.x, bounds.y);
	
		refPointHandle =createHandle(  p, LAYOUT_LOCATION_HANDLE, 14);
	
		allrefPointHandles.add(refPointHandle);
		
	
		
	}

	public void createHandlesForPanel(int w, Rectangle2D r, SmartHandleList handleBoxes2) {
		/**Creates the panel resizing handles*/
		 Point2D location = RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, r.getBounds2D());
		
		 PanelLayoutHandle r2 = createHandle( new Point2D.Double(location.getX(), location.getY()-5), w+ROW_HEIGTH_HANDLE*handleIDFactor, 2);
		this.getHandleBoxes2().add(r2);
		if (getPanelLayout().doesPanelUseUniqueWidth(w+1)) {
			r2.handlesize=3;
			r2.setHandleColor(Color.red);
			}
		 
		  r2 = createHandle( new Point2D.Double(location.getX()-5, location.getY()), w+COLUMN_WIDTH_HANDLE*handleIDFactor, 2);
		  this.getHandleBoxes2().add(r2);
		  if (getPanelLayout().doesPanelUseUniqueHeight(w+1)) {
			  r2.handlesize=3;
			  r2.setHandleColor(Color.red);
			  }
			
		
		/**creates the panel motion handles*/
		 PanelLayoutHandle r3=createHandle( RectangleEdges.getLocation(RectangleEdges.CENTER, r.getBounds2D()), w+PANEL_LOCATION_HANDLE*handleIDFactor, 6);
		panelMotionHandles.add(r3);
		

	}
	
	/**subclasses add their own handles*/
	protected void addFirstLayerHandles(SmartHandleList handleBoxes22) {
		
		
	}

	protected void addAdditionalHandles(SmartHandleList box) {
		
	}

	public void moveLayoutAndContents(double dx, double dy) {
		
		this.generateCurrentImageWrapper();
		ArrayList<PanelContentExtract> stack = getEditor().cutStack(getPanelLayout());
		getPanelLayout().move(dx,dy);
		
		this.getEditor().pasteStack(getPanelLayout(), stack);
		getPanelLayout().resetPtsPanels();
		this.mapPanelLocationsOfLockedItems();
	}
	
	/**called when the handle is pressed but before edits are done*/
	public void onhandlePress() {
		/**not entirely sure why the layout edits dont work without this step*/
		this.generateCurrentImageWrapper();
		 contentstack = this.getEditor().cutStack(getPanelLayout());
		 this.getEditor().pasteStack(getPanelLayout(),  contentstack);
	}

	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		onSmartHandleMove(handlenum, p1, p2);
	
		if (handlenum==LAYOUT_LOCATION_HANDLE) {
			changeLayoutLocation(p2);
			return;
			
		} 
		
		
		int type = handlenum/handleIDFactor;
		
		
		if (type==PANEL_LOCATION_HANDLE&&this.contentstack!=null/**handlenum>=handleIDFactor&&handlenum-handleIDFactor<this.getPanelLayout().getPanels().length*/) {
			handlenum-=handleIDFactor;
			int panelnum=handlenum+1;
			nudgePanel(panelnum, p2, true);
			return;
			}
		
		this.generateCurrentImageWrapper();
		ArrayList<PanelContentExtract> stack = this.getEditor().cutStack(getPanelLayout());
		
		boolean nudgeWidth=type==COLUMN_WIDTH_HANDLE;//handlenum<this.getPanelLayout().getPanels().length;
		boolean nudgeHeigth=type==ROW_HEIGTH_HANDLE;//handlenum<this.getPanelLayout().getPanels().length+3*handleIDFactor &&handlenum>this.getPanelLayout().getPanels().length+2*handleIDFactor ;;
		
		if (nudgeWidth||nudgeHeigth) {
			//IssueLog.log("dragged mouse in handle "+handlenum);
			int panelnum=handlenum%handleIDFactor+1;
			Rectangle2D r = getPanelLayout().getPanel(panelnum);
			double w = p2.x-r.getX()-r.getWidth();
			double h = p2.y-r.getY()-r.getHeight();
			//IssueLog.log("panel dims to be reset "+r);
			if (nudgeWidth)getPanelLayout().nudgePanelDimensions(panelnum,w, 0);
			if (nudgeHeigth)getPanelLayout().nudgePanelDimensions(panelnum,0, h);
			//IssueLog.log("panel dims reset "+getPanelLayout().getPanel(panelnum));
			this.getPanelLayout().resetPtsPanels();
			
			
		} else 
		
		
		if (handlenum-handleIDFactor<this.getPanelLayout().getPanels().length) {
			handlenum-=handleIDFactor;
			int panelnum=handlenum+1;
			nudgePanel(panelnum, p2, false);
			
			}
		getEditor().pasteStack(getPanelLayout(), stack);
		
		
		
		
		this.mapPanelLocationsOfLockedItems();
	}

	/**
	 moves the layout such that its upper left corner ends up at the given location
	 */
	public void changeLayoutLocation(Point p2) {
		moveLayoutAndContents(p2.x-this.getBounds().x, p2.y-this.getBounds().y);
	}

	protected void onSmartHandleMove(int handlenum, Point p1, Point p2) {
		
		SmartHandle handle = this.getAllSmartHandles().getHandleNumber(handlenum);
		
		if(handle instanceof AttachmentPositionHandle) {
		
			handle.handleMove(p1, p2);
			
		}
		else {
			if(handle!=null)handle.handleMove(p1, p2);
			
		}
		
		
	}
	
	
	/**what to do when a user nudges a panel with its mousehandle*/
	void nudgePanel(int panelnum, Point p2, boolean conenttoo) {
		Rectangle2D r = getPanelLayout().getPanel(panelnum);
		double dx = p2.x-r.getCenterX();
		double dy = p2.y-r.getCenterY();
		
		getPanelLayout().nudgePanel(panelnum, dx, dy);
		this.getPanelLayout().resetPtsPanels();
		
		if (conenttoo) {
			contentstack.get(panelnum-1).nudgeObjects(dx, dy);;
		}
	}



	@Override
	public PanelLayout getPanelLayout() {
		// TODO Auto-generated method stub
		return layout;
	}
	
	public String toString() {
		return this.getName();
	}
	
	@Override
	public void moveLocation(double x, double y) {
		//mapPanelLocations();
		getPanelLayout().move(x, y);
		snapLockedItems();
	}
	
	
	
	public void snapLockedItems() {
		for(LocatedObject2D o: getLockedItems()) {
			snapLockedItem(o);
		}
	}
	
	
	@Override
	public void snapLockedItem(LocatedObject2D o) {
	
		if (o==null) return;
		AttachmentPosition sb = o.getAttachmentPosition();
			if (sb==null) {
				o.setAttachmentPosition(AttachmentPosition.defaultInternal());
				sb=o.getAttachmentPosition();
				}
			Integer rw = getPanelLocations().get(o);
			
			
			boolean isSnapLocationDependentOnSpecificPanels = sb.getGridSpaceCode()!=LayoutSpaces.ALL_MONTAGE_SPACE&&sb.getGridSpaceCode()!=LayoutSpaces.BLOCK_OF_PANELS;
			boolean locationDependsOnPanel=isSnapLocationDependentOnSpecificPanels;
			if(!locationDependsOnPanel){
				rw=0;
				
			}
			
			if(rw!=null&&rw<0) 
				return;//some items may have a -1 for no location
			if (rw==null||rw==-1) 
				mapPanelLocation(o);
			
			
			Rectangle2D rectForSnap = getRectForSnap(getPanelLocations().get(o), o);
			
			/**for the labels attached to the entire layout, only need the bound*/
			if(sb.getGridSpaceCode()==LayoutSpaces.ALL_MONTAGE_SPACE) {
				rectForSnap=this.getBounds();
				
			}
			
			/**for the labels attached to all the panels, the bounds of a specific panel will not be used*/
			if(sb.getGridSpaceCode()==LayoutSpaces.BLOCK_OF_PANELS) {
				layout=this.getPanelLayout();
				rectForSnap=layout.allPanelArea().getBounds();
				
			}
			
		
			if(rectForSnap==null) return;
			
			
			
			sb.snapObjectToRectangle(o, rectForSnap);
			
			
	}
	
	public Rectangle2D getRectForSnap(int i, LocatedObject2D o) {
		return getPanelLayout().getPanel(i);
	}
	
	public void mapPanelLocationsOfLockedItems() {
		for(LocatedObject2D o: getLockedItems()) {
			 mapPanelLocationIfValid(o);
		}
		
	}
	
	public void mapPanelLocation(LocatedObject2D o) {
		if (o==null) return;
		int r = getPanelForObject(o);
		getPanelLocations().put(o, r);
	}
	
	/**Checks if the object can be assigned to a panel,
	 * puts the panel index in a hashmap if possible*/
	public void mapPanelLocationIfValid(LocatedObject2D o) {
		if (o==null) return;
		int r = getPanelForObject(o);
		
		/**If a permanent index has been assigned, sets that one*/
		if (o.getTagHashMap().containsKey(TagConstants.INDEX)) {
			Object index = o.getTagHashMap().get(TagConstants.INDEX);
			if(index instanceof Integer)
			{
				getPanelLocations().put(o, ((Integer) index).intValue());
				return;
			}
		}
		
		if(isPanelValid(r, o)) 
			getPanelLocations().put(o, r);
		
		else getPanelLocations().put(o, -1);
	}

	protected boolean isPanelValid(int r, LocatedObject2D o) {
		
		Rectangle2D panel = getPanelLayout().getPanel(r);
		
		if(panel==null) return false;
		if(!panel.intersects(o.getBounds()))
			return false;
	return true;
	}

	/**returns the panel index of the object. this is the index used to determine what layout panel an attached item should belong to*/
	public int getPanelForObject(LocatedObject2D o) {
		
		Object tag = o.getTagHashMap().get(TagConstants.INDEX);
		if(tag!=null && tag instanceof Integer) {
			//if a permanent index is assigned;
			return ((Integer)tag);
			}
		
		/**if not index is permanently set, returns the nearest panel*/
		return findNearestPanelIndex(o);
	}

	/**finds the layout panel that is nearest the object
	 * @param o
	 * @return
	 */
	public int findNearestPanelIndex(LocatedObject2D o) {
		return this.getPanelLayout().getNearestPanelIndex(o.getBounds().getCenterX(),o.getBounds().getCenterY());
	}
	
	/**returns true is at least one locked item is in that panel*/
	public boolean doesPanelHaveLockedItem(int panelnum, Class<?> type) {
		Set<LocatedObject2D> leys = getPanelLocations().keySet();
		for(LocatedObject2D og: leys) {
			int currentcheck= getPanelLocations().get(og);
			if (currentcheck==panelnum && type.isInstance(og)) return true;
		}
		return false;
	}
	
	
	@Override
	public void handleMouseEvent(CanvasMouseEvent me, int handlenum, int button, int clickcount, int type,
			int... other) {
		if (clickcount==2 && type==MouseEvent.MOUSE_CLICKED)
					{
						
						if (handlenum<this.getPanelLayout().getPanels().length&&handlenum>=0) {
							new LayoutPanelSizeModifyDialog(this, this.getPanelLayout()).showPaneldimDialog(handlenum+1);
							return;
						} else {this.showOptionsDialog();}
					
						
					}
	
		
		//if (type==MouseEvent.MOUSE_DRAGGED) {IssueLog.log("handle "+handlenum+" has been "+"Dragged");}
		if (type==MouseEvent.MOUSE_PRESSED) {onhandlePress();}
		if (type==MouseEvent.MOUSE_RELEASED) {onhandleRelease();}
	
	}
	
	
	
	public void onhandleRelease() {}
	
	
	
	@Override
	public int handleNumber(double x, double y) {
		
			SmartHandle handle = getAllSmartHandles().getHandleForClickPoint(new Point2D.Double(x,y));
			if (handle!=null) {
				
				return handle.getHandleNumber();
			}
			
	return -1;
	
	}

	
	
	
	
	public PopupMenuSupplier getMenuSupplier(){
		return new  AttachedItemMenu(this, lockedItems);
	}

	public HashMap<LocatedObject2D, Integer> getPanelLocations() {
		if (this.panelLocations==null) {this.panelLocations=new HashMap<LocatedObject2D, Integer>();
		this.mapPanelLocationsOfLockedItems();}
		return panelLocations;
	}

	public void setPanelLocations(HashMap<LocatedObject2D, Integer> panelLocations) {
		
		this.panelLocations = panelLocations;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public AttachedItemList getPanelSizeDefiningItems() {
		if (panelSizeDefiningItems==null)panelSizeDefiningItems=new AttachedItemList(this);
		return panelSizeDefiningItems;
	}

	public void setPanelSizeDefiningItems(AttachedItemList panelSizeDefiningItems) {
		this.panelSizeDefiningItems = panelSizeDefiningItems;
	}

	@Override
	public void objectMoved(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void objectEliminated(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}
	

	public ImageWorkSheet generateCurrentImageWrapper() {
		if (getEditMode()==0) return  generateStandardImageWrapper() ;
		if (getEditMode()==1) return generateEditNonpermissiveWrapper() ;
		return null;
	}
	

	/**generates a virtual image for the layout graphic
	  This is used in the implementation of montage editor tools
	 * @return */
	public ImageWorkSheet generateStandardImageWrapper() {
		ArrayList<ZoomableGraphic> parent2 = new ArrayList<ZoomableGraphic>();
		if (getParentLayer()!=null) 
				{parent2=this.getParentLayer().getAllGraphics();
				}
			parent2.remove(this);
			ArrayObjectContainer contains = new ArrayObjectContainer(parent2);
			
			GenericImage wrap1 = new GenericImage(contains);
			this.getPanelLayout().setVirtualWorkSheet(wrap1);
			this.getPanelLayout().getVirtualWorksheet().takeFromImage(this);
			if (this.getPanelLayout().getVirtualWorksheet().getLocatedObjects().contains(this)) {
				IssueLog.log("something went wrong  ");
			}
			
			return wrap1;
		
	}
	
	/**generates a virtual worksheet with no objects*/
	private ImageWorkSheet generateEditNonpermissiveWrapper() {
			ArrayObjectContainer cotnainer = new ArrayObjectContainer(new ArrayList<ZoomableGraphic>());
			GenericImage genericImage = new GenericImage(cotnainer);
			this.getPanelLayout().setVirtualWorkSheet(genericImage);
			this.getPanelLayout().getVirtualWorksheet().takeFromImage(this);
			return genericImage;
		}
	
	/**generates a virtual worksheet for the row/col grabber tools*/
	public ImageWorkSheet generateRemovalPermissiveImageWrapper() {
		IssueLog.log("may remove objects");
			if (this.getParentLayer() instanceof ObjectContainer)
			{
				GenericImage genericImage = new GenericImage(getParentLayerAsContainer());
				this.getPanelLayout().setVirtualWorkSheet(genericImage);
				if (!this.getEditor().getObjectHandler().getNeverRemove().contains(this))
				this.getEditor().getObjectHandler().getNeverRemove().add(this);
				return genericImage;
			}
			return null;
	}
	
	private ObjectContainer getParentLayerAsContainer() {
		if (this.getParentLayer() instanceof ObjectContainer)
		{
			return (ObjectContainer) this.getParentLayer();
		}
		return null;
		
	}
	
	/**tests to see if any objects in the parent layer are in panel number panelNum
	  at time of writtin planned to use it as a way to find an empty panel to place
	  a new sequence of panel images*/
	public boolean isPanelEmptyInparentLayer(int panelNum) {
		if (this.getParentLayer() instanceof ObjectContainer)
		{
			
			Rectangle2D r = getPanelLayout().getPanel(panelNum); 
			ArrayList<LocatedObject2D> rois = new BasicObjectListHandler().getOverlapOverlaypingItems(r,  getParentLayerAsContainer());
			ArraySorter.removeThoseOfClass(rois, PanelLayoutGraphic.class);
			if (rois.size()>0) return true;
		}
		
		
		return false;
	}
	
	
	public BasicLayoutEditor getEditor() {
		if (editor==null) editor= new BasicLayoutEditor ();
		return editor;
	}
	
	
	public void addSizeDefiner(LocatedObject2D l) {
		this.getPanelSizeDefiningItems().add(l);
		l.addLocationChangeListener(this);
		resizePanelsToFit(l);
	}
	
	@Override
	public void objectSizeChanged(LocatedObject2D object) {
		if (this.getPanelSizeDefiningItems().contains(object)) {
			resizePanelsToFit(object);
		//	IssueLog.log("Resizing panels");
		}
		
	}
	
	 void resizePanelsToFit(LocatedObject2D l) {
		 	Integer loc = this.getPanelLocations().get(l);
		this.getPanelLayout().setPanelWidth(loc, l.getBounds().width);
		this.getPanelLayout().setPanelHeight(loc, l.getBounds().height);
	}

	public void removeSizeDefiner(LocatedObject2D object) {
		if (this.getPanelSizeDefiningItems().contains(object)) {
			getPanelSizeDefiningItems().remove( object);
			object.removeLocationChangeListener(this);
		}
	}


	
	
	transient static IconSet i;//=new IconSet("icons2/TextIcon.jpg");

	@Override
	public Icon getTreeIcon() {
		//output=
		//return null;
		return createImageIcon();
	}

	public static Icon createImageIcon() {
	//	if (i==null) i=new IconSet("iconsTree/LayoutTreeIcon.png");
		//return i.getIcon(0);
		return new LayoutTreeIcon();
	}

	/**
	 * @param canvasMouseEventWrapper
	 */
	public static void performCanvasResize(CanvasMouseEvent canvasMouseEventWrapper) {
		if (CanvasOptions.current.resizeCanvasAfterEdit)
			new CanvasAutoResize(false).performActionDisplayedImageWrapper(canvasMouseEventWrapper.getAsDisplay());
	}

	@Override
	public void userMoved(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void showOptionsDialog() {
		//	BasicMontageLayout b= getPanelLayout();
		PanelLayoutDisplayOptions dia = new PanelLayoutDisplayOptions(this);
		dia.showDialog();
	}

	@Override
	public void userSizeChanged(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	public boolean isAlwaysShow() {
		return alwaysShow;
	}

	public void setAlwaysShow(boolean alwaysShow) {
		this.alwaysShow = alwaysShow;
	}

	public void setUserLocked(int userLocked) {
		this.userLocked = userLocked;
	}

	public boolean isFilledPanels() {
		return filledPanels;
	}

	public void setFilledPanels(boolean filledPanels) {
		this.filledPanels = filledPanels;
	}
	
	
	/**returns the list of handles for attached items*/
	protected SmartHandleList getAttachedItemHandleList() {
		if (panelHandleList==null) {
			panelHandleList=new SmartHandleList();
			
			for(LocatedObject2D l: this.getLockedItems())  {
				this.generateHandlesForAttachedItems(l);
			}
		}
		
			return panelHandleList;
	}
	
	public SmartHandleList getAllSmartHandles() {
		SmartHandleList output = new SmartHandleList();
		
			output.addAll(getAttachedItemHandleList());
			if (hideAttachedItemHandles) 
				for(SmartHandle s: output) {s.setHidden(true);}
			
		if(panelMotionHandles!=null)
			output.addAll(panelMotionHandles);
		if(allrefPointHandles!=null)
			output.addAll(allrefPointHandles);
		if(getHandleBoxes2()!=null)
			output.addAll(getHandleBoxes2());
		
		
		return output;
	}
	

	 
	 PanelLayoutHandle createHandle(Point2D pt, int handleNum) {
		 PanelLayoutHandle output = new PanelLayoutHandle();
		 
		 output.setCordinateLocation(new Point2D.Double(pt.getX(), pt.getY()));
		 output.setHandleNumber(handleNum);
		 return output;
	 }
	 
	 PanelLayoutHandle createHandle(Point2D pt, int handleNum, double size) { 
		 PanelLayoutHandle o = createHandle(pt, handleNum);
		 o.handlesize=(int) size;
		 return o;
	 }
	
	protected static class PanelLayoutHandle extends SmartHandle {
		
		boolean hasLinkLine=false;
		Point2D startForLine=null;//cordinate location for start of line
		Icon imageIcon=null;
		
		void hasLine(Point2D start) {
			if (start!=null) {
				hasLinkLine=true;
				startForLine=start;
			}
		}
		
		public Icon getIcon() {
			return imageIcon;
		}
		
		@Override
		protected Area getOverdecorationShape() {
			if (this.getHandleNumber()==LAYOUT_LOCATION_HANDLE&&overDecorationShape==null) {
				
				this.decorationColor=Color.black;
				overDecorationShape=getAllDirectionArrows(3, 3, false);
			}
			return overDecorationShape;
		}

		public PanelLayoutHandle() {
			
			super.handlesize=4;
		}
		
		@Override
		public void draw(Graphics2D graphics, CordinateConverter cords) {
			if (hasLinkLine) {
				Point2D p2 =  cords.transformP(startForLine);
				Point2D p2finish = cords.transformP(getCordinateLocation());
				graphics.setColor(Color.black);
				graphics.drawLine((int)p2.getX(), (int)p2.getY(), (int)p2finish.getX(), (int)p2finish.getY());
			}
			super.draw(graphics, cords);
		}
		
		

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
			performCanvasResize(canvasMouseEventWrapper);
		}
	
	
	}

	@Override
	public SmartHandleList getSmartHandleList() {
		return getAllSmartHandles();
	}
	
	
	/**When placing object o into the layout, returns the panel that o should be placed into*/
	@Override
	public Rectangle2D getContainerForBounds(LocatedObject2D o) {
		
		Integer rw = getPanelLocations().get(o);
		if (rw==null) rw=getPanelForObject( o);
		return getRectForSnap(rw, o);
	}
	
	/**Looks for items in the parent layer that may potentially be accepted as locked items but are not currently attached*/
	@Override
	public ArrayList<LocatedObject2D> getNonLockedItems() {
		TakesAttachedItems taker = this;
		return getLockedItems().getEligibleNONLockedItems(taker, getBounds());
	}

	private SmartHandleList getHandleBoxes2() {
		if(handleBoxes2==null)
			handleBoxes2=new SmartHandleList();
		return handleBoxes2;
	}
	
	/** sets the figure type
	 * @param figureType
	 */
	public void setFigureType(FigureType figureType) {
		this.figureType=figureType;
		
	}
	/**
	 returns the figure type 
	 */
	public FigureType getFigureType() {
		if (this.figureType!=null)
			return figureType;
		return FigureType.FLUORESCENT_CELLS;
	}
}
