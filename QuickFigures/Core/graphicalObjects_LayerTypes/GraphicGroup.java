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
 * Date Modified: Jan 4, 2021
 * Version: 2021.2
 */
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
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import export.pptx.GroupToOffice;
import export.pptx.OfficeObjectConvertable;
import export.pptx.OfficeObjectMaker;
import export.svg.SVGExportable;
import export.svg.SVGExporter;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.CordinateConverter;
import graphicalObjects.FigureDisplayWorksheet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects.KnowsTree;
import handles.HasHandles;
import handles.HasSmartHandles;
import handles.ReshapeHandleList;
import handles.SmartHandleList;
import iconGraphicalObjects.IconUtil;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorObjectConvertable;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import utilityClasses1.ArraySorter;
import layersGUI.HasTreeBranchIcon;
import locatedObject.ArrayObjectContainer;
import locatedObject.LocatedObject2D;
import locatedObject.LocatedObjectGroup;
import locatedObject.ObjectContainer;
import locatedObject.RectangleEdges;
import locatedObject.Scales;
import locatedObject.Selectable;

/**The most straightforward implementation of grouping that I could manage*/
public class GraphicGroup extends BasicGraphicalObject implements ZoomableGraphicGroup, HasSmartHandles, Selectable, HasUniquePopupMenu,  LocatedObjectGroup,HasTreeBranchIcon, IllustratorObjectConvertable,KnowsTree, LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>,SVGExportable, OfficeObjectConvertable, Scales{

	

	public Color outlineColor = new Color(100,100,100,100);

	public static boolean treatGroupsLikeLayers=true;
	/**
	 * 
	 */
	private GraphicLayerPane theLayerPane=new GroupedLayerPane("Group", this);
				{theLayerPane.addLayerStructureChangeListener(this);}
	private boolean drawGhost;
	

	private transient ReshapeHandleList reshapeList; 
	
	
	
	public GraphicGroup() {}
	
	
	
	/**creates a group containing all the objects for layer l*/
	public GraphicGroup(GraphicLayer donor) {
		for(ZoomableGraphic i: donor.getItemArray()) this.getTheInternalLayer().add(i);
		this.setName(donor.getName());
	}
	
	/**creates group with many objects*/
	public GraphicGroup( boolean b, ZoomableGraphic... tzs) {
		for(ZoomableGraphic tz: tzs) getTheInternalLayer().add(tz);
	}

	/**creates a group*/
	public GraphicGroup(ArrayList<? extends LocatedObject2D> o2) {
		for(LocatedObject2D tz: o2) 
			{if(tz instanceof ZoomableGraphic)
				getTheInternalLayer().add((ZoomableGraphic) tz);}
	}

	public static Color defaultFolderColor=new Color(200,180, 140);
	
	Color folderColor=  defaultFolderColor;
	
	double tx=0;
	double ty=0;
	Rectangle bounds=null;
	
	private static final long serialVersionUID = 1L;
	
	

	/**Select the group*/
	@Override
	public void select() {
		super.select();
		setupReshapeList();
		selectContent();
	}
	
	/**deselect the group*/
	@Override
	public void deselect() {
		super.deselect();
		setupReshapeList();
		deselectContent();
	}


	/**sets all the objects inside the group to not selected*/
	private void deselectContent() {
		ArrayList<ZoomableGraphic> allGraphics = this.getTheInternalLayer().getAllGraphics();
		ArraySorter.removeNonSelectionItems(allGraphics);
		for(Object z:allGraphics) {
			((Selectable) z).deselect();
		}
	}
	
	/**sets all the objects inside the group to not selected*/
	private void selectContent() {
		ArrayList<ZoomableGraphic> allGraphics = this.getTheInternalLayer().getAllGraphics();
		ArraySorter.removeThoseOfClass(allGraphics, GraphicGroup.GroupHook.class);
		for(Object z:allGraphics) {
			if (z instanceof Selectable )
				((Selectable) z).select();
		}
	}

	/**Setup a handle list that can be used to resize the group*/
	protected void setupReshapeList() {
		if (reshapeList==null)
		reshapeList = new ReshapeHandleList(getTheInternalLayer().getLocatedObjects(), 2, 8000000, true, 0, false);
		else {
			reshapeList.refreshList(getTheInternalLayer().getLocatedObjects());
		}
	}
	
	
	/**returns the parent layer of the graphic group*/
	private GraphicLayer theParent() {
		return this.getParentLayer();
	}
	
	/**returns the location*/
	@Override
	public Point2D getLocationUpperLeft() {
		Rectangle b = getBounds();
		return new Point(b.x, b.y);
		
	}

	/**sets the location*/
	@Override
	public void setLocationUpperLeft(double x, double y) {
		Point2D p = getLocationUpperLeft() ;
		moveLocation(x-p.getX(), y-p.getY());
		
	}

	@Override
	public int isUserLocked() {
		return NOT_LOCKED;
	}

	@Override
	public GraphicGroup copy() {
		return new GraphicGroup();
	}

	@Override
	public boolean doesIntersect(Rectangle2D rect) {
		for(LocatedObject2D log: this.getTheInternalLayer().getLocatedObjects()) {
			if (log.doesIntersect(rect))return true;
		}
		return false;
	}


	@Override
	public Rectangle getExtendedBounds() {
		return getBounds();
	}

	/**returns the outline of all the objects in the group*/
	@Override
	public Shape getOutline() {
		ArrayObjectContainer.ignoredClass=GraphicGroup.GroupHook.class;
		Shape a = ArrayObjectContainer.combineOutLines(getTheInternalLayer().getLocatedObjects());
		ArrayObjectContainer.ignoredClass=null;
		return transform().createTransformedShape(a);
	}
	

	
	
	@Override
	public void moveLocation(double xmov, double ymov) {
		for(LocatedObject2D l:getTheInternalLayer().getLocatedObjects())  {
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
		if (bounds==null) {
			this.updateBoundsFromContents();
		}
		return bounds;
	}
	
	public void updateBoundsFromContents() {
		bounds=getOutline().getBounds();
	}


	/***/
	@Override
	public int getLocationType() {
		return RectangleEdges.UPPER_LEFT;
	}

	/**draws the group*/
	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		//CordinateConverter c = cords.getCopyTranslated(-x, -y);

		this.drawLayer(graphics, cords);
		
		if (this.isSelected()&& drawGhost) {
			graphics.setStroke(new BasicStroke(5));
			graphics.setColor(getOutlineColor());

		}
		else if (this.isSelected()&&this.reshapeList!=null&&!this.handlesHidden) {
			
			getReshapeList().draw(graphics, cords);
			
			
			
		}
	}
	
	
	/**draws the objects in the group layer*/
	public void drawLayer(Graphics2D graphics, CordinateConverter cords) {
	
		for(ZoomableGraphic z: getTheInternalLayer().getGraphicsSync()) try {
			if (z==null) continue;
			
			z.draw(graphics, cords);
		}
		catch (Throwable t) {IssueLog.logT(t);}
		
		
	}

	private Color getOutlineColor() {
		return outlineColor;
	}
	
	

	/**only informs listeners for large movements*/
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		if (handlenum<0) return;

		double dx = p2.getX()-p1.getX();
		double width=this.getBounds().getWidth();
		
		if (dx*2>width) return;
		if (dx>width*2) return;

		
		getListenerList().notifyListenersOfUserSizeChange(this);
	}

	@Override
	public void showOptionsDialog() {
		getTheInternalLayer().showOptionsDialog();
	}

	public GraphicLayerPane getTheInternalLayer() {
		if (theLayerPane==null) {
			theLayerPane=new GraphicLayerPane("");
			{theLayerPane.addLayerStructureChangeListener(this);}
		}
		return theLayerPane;
	}



	
	@Override
	public ObjectContainer getObjectContainer() {
		return getTheInternalLayer();
	}
	
	@Override
	public Object toIllustrator(ArtLayerRef aref) {
		ArtLayerRef sub = aref.createSubRefG();
		sub.setName(getName());
		for(ZoomableGraphic layer: this.getTheInternalLayer() .getItemArray()) try{
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
	
	}
	
	@Override
	public void setGraphicSetContainer(FigureDisplayWorksheet gc) {
		super.setGraphicSetContainer(gc);
		this.getTheInternalLayer().setGraphicSetContainer(gc);
		
	}

	@Override
	public void setTree(
			LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> t) {
		this.getTheInternalLayer().setTree(t);
		
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
		return this.getTheInternalLayer();
	}
	
	public String getName() {
		return getTheInternalLayer().getName();
	}
	public void setName(String name) {
		getTheInternalLayer().setName(name);
	}

	@Override
	public void scaleAbout(Point2D p, double mag) {
		for(ZoomableGraphic l: this.getTheInternalLayer().getAllGraphics()) {
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

		
		@Override
		public ArrayList<ZoomableGraphic> getAllGraphics() {
			ArrayList<ZoomableGraphic> out = super.getAllGraphics();
			out.add(0, new GroupHook(theGroup));
			return out;
		}
		
		
	
	}
	
	@Override
	public SVGExporter getSVGEXporter() {
		return getTheInternalLayer().getSVGEXporter();
	}

	@Override
	public OfficeObjectMaker getObjectMaker() {
		return new GroupToOffice(this);
	}
	
	/**ungroups the group, replacing it with the objects*/
	public void ungroup() {
		ArrayList<ZoomableGraphic> l = getTheInternalLayer().getGraphicsSync();
		ArrayList<ZoomableGraphic> l2 = new ArrayList<ZoomableGraphic>();l2.addAll(l);
		int i = getParentLayer().getItemArray().indexOf(this);
		for(ZoomableGraphic item:l2 ) {
			getTheInternalLayer().remove(item);
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
	
	

	@Override
	public SmartHandleList getSmartHandleList() {
		
		return getReshapeList();
	}
	
	/**A list of handles for resizing the objects in the group*/
	public ReshapeHandleList getReshapeList() {
		if(reshapeList==null)this.setupReshapeList();
		reshapeList.updateRectangle();
		
		reshapeList.thePopup=new GroupGraphicMenu(this);
		
		return reshapeList;
	}
	
	@Override
	public int handleNumber(double x, double y) {
		return this.getSmartHandleList().handleNumberForClickPoint(x, y);
	}
	
	/**A phantom object. Is not drawn but this can be clicked on by the user
	 while the group is being treated as a layer*/
	public class GroupHook extends BasicGraphicalObject implements HasSmartHandles, HasHandles {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private GraphicGroup theGroup;

		public GroupHook(GraphicGroup tg) {
			theGroup=tg;
		}

		@Override
		public Point2D getLocationUpperLeft() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setLocationUpperLeft(double x, double y) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public GroupHook copy() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Rectangle getExtendedBounds() {
			return getBounds() ;
		}

		/**includes and extra rectangle in the outline so the user has a place to click the group*/
		@Override
		public Shape getOutline() {
			Shape outline = theGroup.getOutline();
			
			Area o = new Area(new BasicStroke(7).createStrokedShape(outline.getBounds()));
			o.add(new Area(new BasicStroke(21).createStrokedShape(outline)));
			
			return o;
		}

		@Override
		public Rectangle getBounds() {
			return theGroup.getBounds();
		}

		@Override
		public void draw(Graphics2D graphics, CordinateConverter cords) {
			//group hooks are not drawn but they can be clicked on 
			
		}

	

		@Override
		public void showOptionsDialog() {
			theGroup.showOptionsDialog();
			
		}
		public SmartHandleList getSmartHandleList() {
			
			return theGroup.getSmartHandleList();
		}
		
		/**always returns a positive handle number*/
		public int handleNumber(double x, double y) {
			int num = this.getSmartHandleList().handleNumberForClickPoint(x, y);
			if (num<0 &&this.getOutline().contains(x, y))
				return ReshapeHandleList.defaultHandleNumber+RectangleEdges.CENTER;
			
			return num;
		}

		@Override
		public void select() {
			super.select();
			theGroup.select();

		}

		@Override
		public void deselect() {
			super.deselect();
			theGroup.deselect();
		}
		
		
	

	}
	
	/**The menu that appears when a user right clicks on a group*/
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
	
	
	

}
