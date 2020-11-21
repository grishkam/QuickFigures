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

import applicationAdapters.CanvasMouseEventWrapper;
import applicationAdapters.GenericImage;
import applicationAdapters.ImageWrapper;
import externalToolBar.IconSet;
import genericMontageKit.BasicObjectListHandler;
import genericMontageKit.PanelLayout;
import genericMontageKit.PanelLayoutContainer;
import genericMontageKit.PanelContentExtract;
import graphicalObjectHandles.HasSmartHandles;
import graphicalObjectHandles.LockedItemHandle;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import imageMenu.CanvasAutoResize;
import layersGUI.HasTreeLeafIcon;
import menuUtil.PopupMenuSupplier;
import menuUtil.HasUniquePopupMenu;
import popupMenusForComplexObjects.LockedItemMenu;
import popupMenusForComplexObjects.MontageLayoutDisplayOptions;
import popupMenusForComplexObjects.MontageLayoutPanelModDialog;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.LocationChangeListener;
import utilityClassesForObjects.LockedItemList;
import utilityClassesForObjects.ObjectContainer;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.SnappingPosition;
import utilityClassesForObjects.TakesLockedItems;

public class PanelLayoutGraphic extends BasicGraphicalObject implements PanelLayoutContainer, TakesLockedItems,KnowsParentLayer, HasUniquePopupMenu, LocationChangeListener, HasTreeLeafIcon, HasSmartHandles {
	
	public Color panelColor=Color.red;
	public Color boundryColor=Color.blue;
	private int handleArmDistance=12;
	private int handleArmsize=3;
	public static final int handleIDFactor=10000;//defines the difference in handle ids between motion handles and resize handles
	protected static final int ROW_HEIGTH_HANDLE=0, PANEL_LOCATION_HANDLE=1, COLUMN_WIDTH_HANDLE=2, ROW_HEIGHT_HANDLE_UNIFORM=3,COLUMN_WIDTH_HANDLE_UNIFORM=4;
	protected static final int LocationHandleID=5*handleIDFactor, RightHandleID = LocationHandleID+1,
					BottomHandleID=LocationHandleID+2, LeftHandleID=LocationHandleID+3, TopHandleID=LocationHandleID+4;
	public static final int AddRowHandle=LocationHandleID+5,
			AddColHandle=LocationHandleID+6, 
			RepackPanelsHandle=LocationHandleID+7;
	
	/**
	 * 
	 * 
	 */

	
	public PanelLayoutGraphic() {}
	
	public PanelLayoutGraphic(PanelLayout p) {
		this.layout=p;
	}
	
	
	transient ArrayList<PanelContentExtract> contentstack;
	private int editMode=0;
	private transient boolean alwaysShow=false;
	private boolean filledPanels=false;

	public int getEditMode() {
		return editMode;
	}

	public void setEditMode(int editMode) {
		this.editMode = editMode;
	}
	
	protected PanelLayout layout=new BasicMontageLayout();
	private  HashMap<LocatedObject2D, Integer> panelLocations=new HashMap<LocatedObject2D, Integer>();
	
	private int strokeWidth=2;
	
	ArrayList<PanelLayoutHandle> panelMotionHandles=new ArrayList<PanelLayoutHandle>();
	//
	protected transient GenericMontageEditor editor;
	
	LockedItemList lockedItems=new LockedItemList(this);
	  private LockedItemList panelSizeDefiningItems=new LockedItemList(this);
	  
	public LockedItemList getLockedItems() {
		if (lockedItems==null) lockedItems=new LockedItemList(this);
		return lockedItems;
	}
	
	public void addLockedItem(LocatedObject2D l) {
	
		getLockedItems().add(l);
	
		/**
		if (l instanceof TakesLockedItems) {
			TakesLockedItems t=(TakesLockedItems) l;
			t.removeLockedItem(this);
		}*/
		this.mapPanelLocation(l);
	
		this.snapLockedItems();
		
		if (l instanceof TextGraphic) {
			generateHandleForText(l);	
		
		}
		
		if (l instanceof ImagePanelGraphic) {
			generateHandleForImage(l);	
		
		}
		
		}

	protected void generateHandleForImage(LocatedObject2D l) {
		// TODO Auto-generated method stub
		
	}

	protected void generateHandleForText(LocatedObject2D l) {
		SmartHandleList list = this.getLocedItemHandleList();
		list.add(new LockedItemHandle(this, l, 1000000000+list.size()));
	}
		
	
	public void removeLockedItem(LocatedObject2D l) {
	
		getLockedItems().remove(l);
		
		this.getPanelLocations().remove(l);
		if (this.getPanelSizeDefiningItems().contains(l)) {
			this.getPanelSizeDefiningItems().remove(l);
			l.removeLocationChangeListener(this);
		}
		getLocedItemHandleList().removeLockedItemHandle(l);
		
	}
	
	@Override
	public boolean hasLockedItem(LocatedObject2D l) {
		return getLockedItems().contains(l);
	}
	
	{this.setName("Layout");}
	private static final long serialVersionUID = 1L;
	
	

	private GraphicLayer parent;

	private int userLocked=1;
	private SmartHandleList panelHandleList=new SmartHandleList();;
	private SmartHandleList allrefPointHandles=new SmartHandleList();;
	private SmartHandleList handleBoxes2=new SmartHandleList();
	
	
	

	@Override
	public Point2D getLocationUpperLeft() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return userLocked;
	}

	@Override
	public LocatedObject2D copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean doesIntersect(Rectangle2D rect) {
		// TODO Auto-generated method stub
		if (this.getPanelLayout().getPanel(1).intersects(rect)) return true;
		return false;
	}

	@Override
	public boolean isInside(Rectangle2D rect) {
		// TODO Auto-generated method stub
		return rect.contains(getPanelLayout().getBoundry().getBounds());
	}

	@Override
	public Rectangle getExtendedBounds() {
		// TODO Auto-generated method stub
		return this.getPanelLayout().getBoundry().getBounds();
	}

	@Override
	public Shape getOutline() {
		// TODO Auto-generated method stub
		return getPanelLayout().allPanelArea();
	}

	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		//return getPanelLayout().allPanelArea().getBounds();
		return getPanelLayout().getBoundry().getBounds();
	}

	@Override
	public void setLocationType(int n) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLocationType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
		//mapPanelLocations();
		 graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		snapLockedItems();
		getPanelLayout().getPanels();
			Rectangle2D[] ps = this.getPanelLayout().getPanels();
			
			 graphics.setStroke(new BasicStroke((float) this.getStrokeWidth()));
			 graphics.setColor(getBoundryColor());
			 
			 if (this.isSelected()||isAlwaysShow()) 
			 		{
				
				 
				 getGrahpicUtil().drawRectangle(graphics, cords,getPanelLayout().getBoundry().getBounds(), false);
				 this.drawLayoutTypeSpecific(graphics, cords);
				 int number=1;
				 for(Rectangle2D r:ps) {
					 drawPanel(graphics, cords, number, r);
					number++;
				 	}
				 if (this.isSelected())drawHandles(graphics,cords);
			
			 
			 		}

	}

	public Color getBoundryColor() {
		return boundryColor;
	}
	
	protected void drawLayoutTypeSpecific(Graphics2D graphics,
			CordinateConverter<?> cords) {
		// TODO Auto-generated method stub
		
	}

	protected void drawPanel(Graphics2D graphics, CordinateConverter<?> cords, int number, Rectangle2D r) {
		if (r==null) return;
		 graphics.setColor(getPanelColor());
		this.getGrahpicUtil().drawRectangle(graphics, cords, r, false);
		if (isFilledPanels()) getGrahpicUtil().fillRectangle(graphics, cords,r);
					 this.getGrahpicUtil().drawString(graphics, cords, ""+number, new Point2D.Double(r.getX(), r.getY()), new Font("Arial", Font.BOLD, 12+this.getStrokeWidth()/2), Color.green.darker(), 0);
	}

	public Color getPanelColor() {
		return panelColor;
	}
	
	
	
	
	
	public void clearHandleBoxes() {
		super.clearHandleBoxes();
		panelMotionHandles.clear();
		handleBoxes2.clear();
		
	}
	
	
	
	public void drawHandles(Graphics2D graphics, CordinateConverter<?> cords) {
		Rectangle2D[] ps = this.getPanelLayout().getPanels();
		clearHandleBoxes();
		
		int hdisplace=(int) (15/cords.getMagnification());
		double handleArmDistance2 = handleArmDistance;//*cords.getMagnification();
		double handleArmsize2 = handleArmsize;//*cords.getMagnification();
		if (handleArmDistance2<15)handleArmDistance2=15;
		if (handleArmsize2<3)handleArmsize2=3;
		if (hdisplace<20)hdisplace=20;
		
		addFirstLayerHandles(handleBoxes2);
		
		int w=0;//panel numer from 
		for(Rectangle2D r:ps) {
			createHandlesForPanel(w, r, handleBoxes2);
			
			w++;
		}
		
		this.allrefPointHandles=new SmartHandleList();
		PanelLayoutHandle rightRefPointHandle;
		PanelLayoutHandle bottomRefPointHandle;
		PanelLayoutHandle leftRefPointHandle;
		PanelLayoutHandle topRefPointHandle;
		PanelLayoutHandle  refPointHandle;
		Point p = new Point(this.getBounds().x, this.getBounds().y);
		
		p = new Point((int)getBounds().getMaxX(), this.getBounds().y-hdisplace);
		
		Point p2 = (Point) p.clone();
		p2.y+=handleArmDistance2+5;
		rightRefPointHandle =createHandle(  p, RightHandleID, handleArmsize2);
		rightRefPointHandle.hasLine(p2);
				//getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p, p2);
	
		allrefPointHandles.add(rightRefPointHandle);
		
		p = new Point((int)getBounds().getMaxX(), (int)(this.getBounds().getMaxY()+hdisplace));

		p2 = (Point) p.clone();
		p2.y-=handleArmDistance2+5;
		rightRefPointHandle =createHandle(  p, RightHandleID, handleArmsize2);
		rightRefPointHandle.hasLine(p2);
		allrefPointHandles.add(rightRefPointHandle);
		
		p = new Point(getBounds().x-hdisplace, (int)getBounds().getMaxY());
	
		p2 = (Point) p.clone();
		p2.x+=handleArmDistance+5;
		bottomRefPointHandle =createHandle(  p, BottomHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p,p2);
		bottomRefPointHandle.hasLine(p2);
	
		allrefPointHandles.add(bottomRefPointHandle);
		
		p = new Point((int)getBounds().getMaxX()+hdisplace, (int)getBounds().getMaxY());

		p2 = (Point) p.clone();
		p2.x-=handleArmDistance+5;
		bottomRefPointHandle =createHandle(  p, BottomHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p,p2);
		bottomRefPointHandle.hasLine(p2);
	
		allrefPointHandles.add(bottomRefPointHandle);
		
		p = new Point((int)getBounds().getX(), (int)getBounds().getMaxY()+hdisplace);

		p2 = (Point) p.clone();
		p2.y-=handleArmDistance2;
		leftRefPointHandle =createHandle(  p, LeftHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p, p2);
		leftRefPointHandle.hasLine(p2);
	
		allrefPointHandles.add(leftRefPointHandle);
		
		p = new Point((int)getBounds().getX(), this.getBounds().y-hdisplace);

		p2 = (Point) p.clone();
		p2.y+=handleArmDistance2;
		leftRefPointHandle =createHandle(  p, LeftHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p, p2);
		leftRefPointHandle.hasLine(p2);
	
		allrefPointHandles.add(leftRefPointHandle);
		
		p = new Point(getBounds().x-hdisplace, (int)getBounds().getY());
	
		p2 = (Point) p.clone();
		p2.x+=handleArmDistance2;
		topRefPointHandle =createHandle(  p, TopHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p,p2);
		topRefPointHandle.hasLine(p2);//this.getGrahpicUtil().setHandleSize(3);
		allrefPointHandles.add(topRefPointHandle);
		
		p = new Point((int)getBounds().getMaxX()+hdisplace, (int)getBounds().getY());

		p2 = (Point) p.clone();
		p2.x-=handleArmDistance2;
		topRefPointHandle =createHandle(  p,TopHandleID, handleArmsize2);// getGrahpicUtil().drawSizeHandlesAtPoint(graphics, cords, p,p2);
		topRefPointHandle.hasLine(p2);//this.getGrahpicUtil().setHandleSize(3);
		allrefPointHandles.add(topRefPointHandle);
		
		p = new Point(this.getBounds().x, this.getBounds().y);
	
		refPointHandle =createHandle(  p, LocationHandleID, 14);
	
		allrefPointHandles.add(refPointHandle);
		
		addAdditionalHandles(handleBoxes2);
		
		this.getAllSmartHandles().draw(graphics, cords);
		this.getLocedItemHandleList().draw(graphics, cords);
	}

	public void createHandlesForPanel(int w, Rectangle2D r, SmartHandleList handleBoxes2) {
		/**Creates the panel resizing handles*/
		 Point2D location = RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, r.getBounds());
		
		 PanelLayoutHandle r2 = createHandle( new Point2D.Double(location.getX(), location.getY()-5), w+ROW_HEIGTH_HANDLE*handleIDFactor, 2);
		this.handleBoxes2.add(r2);
		if (getPanelLayout().doesPanelUseUniqueWidth(w+1)) {
			r2.handlesize=3;
			r2.setHandleColor(Color.red);
			}
		 
		  r2 = createHandle( new Point2D.Double(location.getX()-5, location.getY()), w+COLUMN_WIDTH_HANDLE*handleIDFactor, 2);
		  this.handleBoxes2.add(r2);
		  if (getPanelLayout().doesPanelUseUniqueHeight(w+1)) {
			  r2.handlesize=3;
			  r2.setHandleColor(Color.red);
			  }
			
		
		/**creates the panel motion handles*/
		 PanelLayoutHandle r3=createHandle( RectangleEdges.getLocation(RectangleEdges.CENTER, r.getBounds()), w+PANEL_LOCATION_HANDLE*handleIDFactor, 6);
		panelMotionHandles.add(r3);
		

	}
	
	
	protected void addFirstLayerHandles(SmartHandleList handleBoxes22) {
		// TODO Auto-generated method stub
		
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
	
	public void onhandlePress() {
		this.generateCurrentImageWrapper();
		 contentstack = this.getEditor().cutStack(getPanelLayout());
		 this.getEditor().pasteStack(getPanelLayout(),  contentstack);
	}

	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		onSmartHandleMove(handlenum, p1, p2);
	
		if (handlenum==LocationHandleID) {
			moveLayoutAndContents(p2.x-this.getBounds().x, p2.y-this.getBounds().y);
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

	protected void onSmartHandleMove(int handlenum, Point p1, Point p2) {
		
		SmartHandle handle = this.getAllSmartHandles().getHandleNumber(handlenum);
		
		if(handle instanceof LockedItemHandle) {
		//	((LockedItemHandle) handle).setInfineControl((p2.distance(handle.getCordinateLocation())<2.5));
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
		SnappingPosition sb = o.getSnapPosition();
			if (sb==null) {
				o.setSnapPosition(SnappingPosition.defaultInternal());
				sb=o.getSnapPosition();
				}
			Integer rw = getPanelLocations().get(o);
			
			if(rw<0) return;//some items may have a -1 for no location
			if (rw==null||rw==-1) mapPanelLocation(o);
			Rectangle2D rectForSnap = getRectForSnap(getPanelLocations().get(o), o);
			
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
	
	public void mapPanelLocationIfValid(LocatedObject2D o) {
		if (o==null) return;
		int r = getPanelForObject(o);
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

	public int getPanelForObject(LocatedObject2D o) {
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
	public void handleMouseEvent(CanvasMouseEventWrapper me, int handlenum, int button, int clickcount, int type,
			int... other) {
		if (clickcount==2 && type==MouseEvent.MOUSE_CLICKED)
					{
						
						if (handlenum<this.getPanelLayout().getPanels().length&&handlenum>=0) {
							new MontageLayoutPanelModDialog(this, this.getPanelLayout()).showPaneldimDialog(handlenum+1);
							return;
						} else {this.showOptionsDialog();}
					
						
					}
	
		
		//if (type==MouseEvent.MOUSE_DRAGGED) {IssueLog.log("handle "+handlenum+" has been "+"Dragged");}
		if (type==MouseEvent.MOUSE_PRESSED) {onhandlePress();}
		if (type==MouseEvent.MOUSE_RELEASED) {onhandleRelease();}
	
	}
	
	
	
	public void onhandleRelease() {}
	
	
	
	@Override
	public int handleNumber(int x, int y) {
		
			SmartHandle handle = getAllSmartHandles().getHandleForClickPoint(new Point(x,y));
			if (handle!=null) {
				
				return handle.getHandleNumber();
			}
			
	return -1;
	
	}

	@Override
	public GraphicLayer getParentLayer() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public void setParentLayer(GraphicLayer parent) {
		this.parent=parent;
		
	}
	
	
	
	public PopupMenuSupplier getMenuSupplier(){
		return new  LockedItemMenu(this, lockedItems);
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

	public LockedItemList getPanelSizeDefiningItems() {
		if (panelSizeDefiningItems==null)panelSizeDefiningItems=new LockedItemList(this);
		return panelSizeDefiningItems;
	}

	public void setPanelSizeDefiningItems(LockedItemList panelSizeDefiningItems) {
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
	

	public ImageWrapper generateCurrentImageWrapper() {
		if (getEditMode()==0) return  generateStandardImageWrapper() ;
		if (getEditMode()==1) return generateEditNonpermissiveWrapper() ;
		return null;
	}
	

	/**generates an image Wrapper for the layout graphic
	  This is used in the implementation of montage editor tools
	 * @return */
	public ImageWrapper generateStandardImageWrapper() {
	ArrayList<ZoomableGraphic> parent2 = new ArrayList<ZoomableGraphic>();
	if (getParentLayer()!=null) parent2=this.getParentLayer().getAllGraphics();
		GenericImage wrap1 = new GenericImage(new ArrayObjectContainer(parent2));
		this.getPanelLayout().setWrapper(wrap1);
		this.getPanelLayout().getWrapper().takeFromImage(this);
		return wrap1;
		
	}
	
	public ImageWrapper generateEditNonpermissiveWrapper() {
			GenericImage genericImage = new GenericImage(new ArrayObjectContainer(new ArrayList<ZoomableGraphic>()));
			this.getPanelLayout().setWrapper(genericImage);
			this.getPanelLayout().getWrapper().takeFromImage(this);
			return genericImage;
		}
	
	public ImageWrapper generateRemovalPermissiveImageWrapper() {
			if (this.getParentLayer() instanceof ObjectContainer)
			{
				GenericImage genericImage = new GenericImage(getParentLayerAsContainer());
				this.getPanelLayout().setWrapper(genericImage);
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
	
	
	public GenericMontageEditor getEditor() {
		if (editor==null) editor= new GenericMontageEditor ();
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
		if (i==null) i=new IconSet("iconsTree/LayoutTreeIcon.png");
		return i.getIcon(0);
	}

	@Override
	public void userMoved(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void showOptionsDialog() {
		//	BasicMontageLayout b= getPanelLayout();
		MontageLayoutDisplayOptions dia = new MontageLayoutDisplayOptions(this);
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
	
	
	protected SmartHandleList getLocedItemHandleList() {
		if (panelHandleList==null) panelHandleList=new SmartHandleList();
			return panelHandleList;
	}
	
	public SmartHandleList getAllSmartHandles() {
		SmartHandleList output = new SmartHandleList();
		output.addAll(getLocedItemHandleList());
		output.addAll(panelMotionHandles);
		output.addAll(allrefPointHandles);
		output.addAll(handleBoxes2);
		
		
		return output;
	}
	

	 
	 PanelLayoutHandle createHandle(Point2D pt, int handleNum) {
		 PanelLayoutHandle output = new PanelLayoutHandle(0,0);
		 
		 output.setCordinateLocation(new Point((int)pt.getX(), (int)pt.getY()));
		 output.setHandleNumber(handleNum);
		 return output;
	 }
	 
	 PanelLayoutHandle createHandle(Point2D pt, int handleNum, double size) { 
		 PanelLayoutHandle o = createHandle(pt, handleNum);
		 o.handlesize=(int) size;
		 return o;
	 }
	
	protected class PanelLayoutHandle extends SmartHandle {
		
		boolean hasLinkLine=false;
		Point2D startForLine=null;//cordinate locatin for start of line
		//private IconSet iconSet=new IconSet("iconsHandles/Move cursor 4.png","iconsHandles/Move cursor 4.png","iconsHandles/Move cursor 4.png");
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
			if (this.getHandleNumber()==LocationHandleID&&overDecorationShape==null) {
				
				this.decorationColor=Color.black;
				overDecorationShape=getAllDirectionArrows(3, 3, false);
			}
			return overDecorationShape;
		}

		public PanelLayoutHandle(int x, int y) {
			super(x, y);
			super.handlesize=4;
		}
		
		@Override
		public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
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
		
		public void handleRelease(CanvasMouseEventWrapper canvasMouseEventWrapper) {
			new CanvasAutoResize().performActionDisplayedImageWrapper(canvasMouseEventWrapper.getAsDisplay());
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
		TakesLockedItems taker = this;
		return getLockedItems().getEligibleNONLockedItems(taker, getBounds());
	}
}
