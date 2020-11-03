package graphicalObjects_LayerTypes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fieldReaderWritter.SVGExportable;
import fieldReaderWritter.SVGExporter;
import graphicalObjectHandles.HasHandles;
import graphicalObjectHandles.HasSmartHandles;
import graphicalObjectHandles.ReshapeHandleList;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects.CordinateConverter;
import graphicalObjects.GraphicSetDisplayContainer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects.KnowsTree;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import iconGraphicalObjects.IconUtil;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorObjectConvertable;
import layersGUI.LayerStructureChangeListener;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import officeConverter.LayerToOffice;
import officeConverter.OfficeObjectConvertable;
import officeConverter.OfficeObjectMaker;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.LocatedObjectGroup;
import utilityClassesForObjects.ObjectContainer;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.Scales;
import utilityClassesForObjects.Selectable;
import layersGUI.HasTreeBranchIcon;

/**The most straitforward implementation of grouping that I could manage*/
public class GraphicGroup extends BasicGraphicalObject implements ZoomableGraphicGroup, HasSmartHandles, HasHandles, Selectable, HasUniquePopupMenu,  LocatedObjectGroup,HasTreeBranchIcon, IllustratorObjectConvertable,KnowsTree, LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>,SVGExportable, OfficeObjectConvertable, Scales{

	public Color outlineColor = new Color(100,100,100,100);

	public static boolean treatGroupsLikeLayers=false;
	/**
	 * 
	 */
	
	
	
	public GraphicGroup() {}
	
	/**creates a group containing all the objects for layer l*/
	public GraphicGroup(GraphicLayer donor) {
		for(ZoomableGraphic i: donor.getItemArray()) this.getTheLayer().add(i);
		this.setName(donor.getName());
	}
	
	public GraphicGroup( boolean b, ZoomableGraphic... tzs) {
		for(ZoomableGraphic tz: tzs) getTheLayer().add(tz);
	}

	public GraphicGroup(ArrayList<LocatedObject2D> o2) {
		for(LocatedObject2D tz: o2) 
			{if(tz instanceof ZoomableGraphic)
				getTheLayer().add((ZoomableGraphic) tz);}
	}

	public static Color defaultFolderColor=new Color(200,180, 140);
	
	Color folderColor=  defaultFolderColor;
	double tx=0;
	double ty=0;
	Rectangle bounds=null;
	
	private static final long serialVersionUID = 1L;
	
	
	private GraphicLayerPane theLayerPane=new GroupedLayerPane("Group", this);
{theLayerPane.addLayerStructureChangeListener(this);}
	private boolean drawGhost;

	private transient ReshapeHandleList reshapeList; 
	
	@Override
	public void select() {
		super.select();
		setupReshapeList();
	}

	protected void setupReshapeList() {
		reshapeList = new ReshapeHandleList(getTheLayer().getLocatedObjects(), 2, 8000000, true, 0, false);
		
	}
	
	
	
	private GraphicLayer theParent() {
		return this.getParentLayer();
	}
	
	@Override
	public Point2D getLocationUpperLeft() {
		Rectangle b = getBounds();
		return new Point(b.x, b.y);
		
	}

	@Override
	public void setLocationUpperLeft(double x, double y) {
		Point2D p = getLocationUpperLeft() ;
		//IssueLog.log("Setting location of group "+this.getBounds(), "will set location to "+new Point2D.Double(x,y));

		moveLocation(x-p.getX(), y-p.getY());
		
	}

	@Override
	public int isUserLocked() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public LocatedObject2D copy() {
		return new GraphicGroup();
	}

	@Override
	public boolean doesIntersect(Rectangle2D rect) {
		// TODO Auto-generated method stub
		for(LocatedObject2D log: this.getTheLayer().getLocatedObjects()) {
			if (log.doesIntersect(rect))return true;
		}
		return false;
	}


	@Override
	public Rectangle getExtendedBounds() {
		return getBounds();
	}

	@Override
	public Shape getOutline() {
		
		Shape a = ArrayObjectContainer.combineOutLines(getTheLayer().getLocatedObjects());
	
		// TODO Auto-generated method stub
		return transform().createTransformedShape(a);
	}
	

	
	
	@Override
	public void moveLocation(double xmov, double ymov) {
		for(LocatedObject2D l:getTheLayer().getLocatedObjects())  {
			l.moveLocation(xmov, ymov);
		}
		
		getBounds().translate((int)xmov,(int) ymov);
		notifyListenersOfMoveMent();
	}
	
	public AffineTransform transform() {
		return AffineTransform.getTranslateInstance(tx, ty);
	}
	
	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		if (bounds==null) {
			this.updateBoundsFromContents();
		}
		return bounds;
	}
	
	public void updateBoundsFromContents() {
		bounds=getOutline().getBounds();
	}


	@Override
	public int getLocationType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
		//CordinateConverter<?> c = cords.getCopyTranslated(-x, -y);

		this.drawLayer(graphics, cords);
		
		if (this.isSelected()&& drawGhost) {
			graphics.setStroke(new BasicStroke(5));
			graphics.setColor(getOutlineColor());
		//	Shape s = this.getOutline();
			
		//	getGrahpicUtil().drawStrokedShape(graphics, cords, s);
		//	graphics.setColor(new Color(0,0,0,0));
			
		//	getGrahpicUtil().drawHandlesAtPoints(graphics, cords, RectangleEdges.getLocationsForHandles(s.getBounds()));//.drawRectangle(graphics, cords, s.getBounds2D(), true);
		//	this.handleBoxes=getGrahpicUtil().lastHandles;
		}
		else if (this.isSelected()&&this.reshapeList!=null) {
			getReshapeList().draw(graphics, cords);
		}
	}
	
	
	public void drawLayer(Graphics2D graphics, CordinateConverter<?> cords) {
	
		for(ZoomableGraphic z: getTheLayer().getGraphicsSync()) try {
			if (z==null) continue;
			
			z.draw(graphics, cords);
		}
		catch (Throwable t) {IssueLog.log(t);}
		
		
	}

	private Color getOutlineColor() {
		return outlineColor;
	}
	
	

	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		if (handlenum<0) return;
	//	IssueLog.log("handle moving");
		double dx = p2.getX()-p1.getX();
		double width=this.getBounds().getWidth();
		//double scale = (width-dx)/width;
		if (dx*2>width) return;
		if (dx>width*2) return;
		//int o = RectangleEdges.oppositeSide(handlenum);
		//this.scaleAbout(RectangleEdges.getLocation(o, getBounds()), scale);
		/**
		
		 setLocationType(RectangleEdges.oppositeSide(handlenum));
		 Rectangle r = this.getBounds();
		 double dist1=r.getWidth();//RectangleEdges.distanceOppositeSide(handlenum, r);
		 double dist2=Math.abs(p2.x-RectangleEdges.getLocation(getLocationType(), getBounds()).getX());// RectangleEdges.getLocation(getLocationType(), getBounds()).distance(p2);
		 //Point2D lo = getLocation(handlenum, getBounds());
	
		//double l1 = getLocation(op, getBounds()).distance( getLocation(handlenum, getBounds()));		
		double scaleme = dist2/dist1;
		Point2D p = RectangleEdges.getLocation(o, r);//this.getLocation();
		
		//setScale( dist2/dist1);		
		 * 
		 */
		
		getListenerList().notifyListenersOfUserSizeChange(this);
	}

	@Override
	public void showOptionsDialog() {
		getTheLayer().showOptionsDialog();
	}

	public GraphicLayerPane getTheLayer() {
		if (theLayerPane==null) {
			theLayerPane=new GraphicLayerPane("");
		}
		return theLayerPane;
	}

	public void setTheLayer(GraphicLayerPane theLayerPane) {
		this.theLayerPane = theLayerPane;
	}

	@Override
	public ObjectContainer getObjectContainer() {
		// TODO Auto-generated method stub
		return getTheLayer();
	}
	
	@Override
	public Object toIllustrator(ArtLayerRef aref) {
		ArtLayerRef sub = aref.createSubRefG();
		sub.setName(getName());
		for(ZoomableGraphic layer: this.getTheLayer() .getItemArray()) try{
			if (layer instanceof  IllustratorObjectConvertable) {
				IllustratorObjectConvertable ills = ( IllustratorObjectConvertable)layer;
				ills.toIllustrator(sub);
			}
		}catch (Throwable t) {
			
		}
		return sub;
	}

	
	
	@Override
	public Icon getTreeIcon(boolean open) {
		
		return IconUtil.createFolderIcon(open, folderColor);
	/**
		if (open) return defaultLeaf;// TODO Auto-generated method stub
		return defaultLeaf2;*/
	}
	
	@Override
	public void setGraphicSetContainer(GraphicSetDisplayContainer gc) {
		super.setGraphicSetContainer(gc);
		this.getTheLayer().setGraphicSetContainer(gc);
		
	}

	@Override
	public void setTree(
			LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> t) {
		this.getTheLayer().setTree(t);
		
	}

	@Override
	public void itemsSwappedInContainer(GraphicLayer gc, ZoomableGraphic z1,
			ZoomableGraphic z2) {

	}

	@Override
	public void itemRemovedFromContainer(GraphicLayer gc, ZoomableGraphic z) {
		 updateBoundsFromContents();
	}

	@Override
	public void itemAddedToContainer(GraphicLayer gc, ZoomableGraphic z) {
		// TODO Auto-generated method stub
		 updateBoundsFromContents();
	}

	@Override
	public GraphicLayer getSelectedLayer() {
		// TODO Auto-generated method stub
		return this.getTheLayer();
	}
	
	public String getName() {
		return getTheLayer().getName();
	}
	public void setName(String name) {
		getTheLayer().setName(name);
	}

	@Override
	public void scaleAbout(Point2D p, double mag) {
		for(ZoomableGraphic l: this.getTheLayer().getAllGraphics()) {
			if (l instanceof Scales) {
				 Scales sl=( Scales) l;
				sl.scaleAbout(p, mag);
			}
		}
	}
	

	public class GroupedLayerPane extends GraphicLayerPane implements HasUniquePopupMenu {

		private GraphicGroup theGroup;
		public GroupedLayerPane(String name, GraphicGroup g) {
			super(name);
			super.setDescription("A grouped layer");
			theGroup=g;
		}
		
		/**Called when the user tries to move objects between layers*/
		public boolean canAccept(ZoomableGraphic z) {
			if(z instanceof GraphicLayer)
				return false;
			if (!theParent().canAccept(z)) {
				return false;//returns false if a parent of this layer rejects the item
			}
			return super.canAccept(z);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public GraphicLayer getParentLayer()  {
			return theParent();
		}

		@Override
		public PopupMenuSupplier getMenuSupplier() {
		
			return getMenu();
		}

		public GraphicGroup getTheGroup() {
			return theGroup;
		}

		
	
	}
	
	@Override
	public SVGExporter getSVGEXporter() {
		// TODO Auto-generated method stub
		return getTheLayer().getSVGEXporter();
	}

	@Override
	public OfficeObjectMaker getObjectMaker() {
		// TODO Auto-generated method stub
		return new LayerToOffice(this);
	}
	
	/**ungroups the group*/
	public void ungroup() {
		ArrayList<ZoomableGraphic> l = getTheLayer().getGraphicsSync();
		ArrayList<ZoomableGraphic> l2 = new ArrayList<ZoomableGraphic>();l2.addAll(l);
		int i = getParentLayer().getItemArray().indexOf(this);
		for(ZoomableGraphic item:l2 ) {
			getTheLayer().remove(item);
			getParentLayer().addItemToLayer(item);
			getParentLayer().moveItemToIndex(item, i);//does not move the item correctly
			
			i++;
			
		}
		getParentLayer().remove(this);
	}
	
	@Override
	public PopupMenuSupplier getMenuSupplier() {
		return  getMenu();
	}
	
	public PopupMenuSupplier getMenu() {
		return new GroupGraphicMenu(this);
	}
	
	class GroupGraphicMenu extends SmartPopupJMenu implements ActionListener,
	PopupMenuSupplier  {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public GroupGraphicMenu(GraphicGroup graphicGroup) {
			
			JMenuItem j = new JMenuItem("Ungoup");
			j.addActionListener(this);
			add(j);
		}

		@Override
		public JPopupMenu getJPopup() {
			return this;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ungroup();
			
			
		}

		
		
	}

	@Override
	public SmartHandleList getSmartHandleList() {
		
		return getReshapeList();
	}
	
	private ReshapeHandleList getReshapeList() {
		if(reshapeList==null)this.setupReshapeList();
		reshapeList.updateRectangle();
		return reshapeList;
	}
	
	public int handleNumber(int x, int y) {
		return this.getSmartHandleList().handleNumberForClickPoint(x, y);
	}

}
