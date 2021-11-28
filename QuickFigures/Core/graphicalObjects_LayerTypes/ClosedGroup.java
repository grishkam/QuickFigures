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
 * Date Created: Nov 27, 2021
 * Date Modified: Nov 27, 2021
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
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import export.pptx.GroupToOffice;
import export.pptx.OfficeObjectConvertable;
import export.pptx.OfficeObjectMaker;
import export.svg.SVGExportable;
import export.svg.SVGExporter;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.CordinateConverter;
import graphicalObjects.FigureDisplayWorksheet;
import graphicalObjects.KnowsTree;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.ClosedGroup.ClosedSmartHandle;
import graphicalObjects_Shapes.RectangularGraphic;
import handles.HasSmartHandles;
import handles.ReshapeHandleList;
import handles.SmartHandle;
import handles.SmartHandleList;
import handles.ReshapeHandleList.ReshapeSmartHandle;
import iconGraphicalObjects.IconUtil;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorObjectConvertable;
import layersGUI.HasTreeBranchIcon;
import locatedObject.ArrayObjectContainer;
import locatedObject.LocatedObject2D;
import locatedObject.LocatedObjectGroup;
import locatedObject.ObjectContainer;
import locatedObject.RectangleEdgePositions;
import locatedObject.RectangleEdges;
import locatedObject.Scales;
import locatedObject.Selectable;
import logging.IssueLog;
import menuUtil.BasicSmartMenuItem;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartMenuItem;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;

/**An object that consists of many internal objects. Each is a vector graphic but none are directly editable
   This object can be scaled and moved by the user but objects inside are not accessible
   Meant to simplify figures with complex graphics that can appear when using @class QFGraphics2D
  Expect that if I later write code to import into QuickFigures, this will be needed to keep the figures simple
   */
public class ClosedGroup extends BasicGraphicalObject implements HasSmartHandles, Selectable, HasUniquePopupMenu,  LocatedObjectGroup,HasTreeBranchIcon, IllustratorObjectConvertable,KnowsTree, LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>,SVGExportable, OfficeObjectConvertable, Scales, GraphicHolder{

	

	
	
	public class ClosedSmartHandle extends SmartHandle {

		private int type;
		private double scale;
		private Point2D centerOfScaling;
		private Point2D leftCorner;
		/**
		 * @param reshapeHandleList
		 * @param type
		 * @param r
		 */
		public ClosedSmartHandle(int type) {
			this.type=type;
		}

		/**location of the handle. this determines where in the figure the handle will actually appear
		   overwritten in many subclasses*/
		public Point2D getCordinateLocation() {
			return RectangleEdges.getLocation(type, bounds);
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public 
		void handleDrag(CanvasMouseEvent wrap) {
			int op=RectangleEdges.oppositeSide(type);//the center of scaling
			setLocationType(op);
			Point destination = wrap.getCoordinatePoint();
			centerOfScaling = RectangleEdges.getLocation(op, bounds);
			double newwidth = Math.abs(destination.getX()-centerOfScaling.getX());
			double newheight = Math.abs(destination.getY()-centerOfScaling.getY());
			
			/**Determines the scale factors for the rectangle drag*/
			double xScale = newwidth/bounds.getWidth();
			double yScale = newheight/bounds.getHeight();
			scale=xScale;
			
			if(type==TOP||type==BOTTOM) {
				scale=yScale;
			}
			
			 RectangularGraphic mark = RectangularGraphic.blankRect(bounds, Color.green, true, true);
			 mark.scaleAbout(centerOfScaling, scale);
			 leftCorner=mark.getLocationUpperLeft();
			 wrap.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionGraphic(mark);
			
			
		}
		
		@Override
		public 
		void handleRelease(CanvasMouseEvent wrap) {
			double scaleAboutX = centerOfScaling.getX()-tx;
			double scaleAboutY = centerOfScaling.getY()-ty;
			Double p = new Point2D.Double(scaleAboutX, scaleAboutY);
			p = new Point2D.Double(0, 0);
			
			wrap.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionstoNull();
			double theScale = scale;
			
			for( ZoomableGraphic current: getTheInternalLayer().getAllGraphics()) {
				if(current instanceof Scales) {
					Scales s1=(Scales) current;
					s1.scaleAbout(p, scale);
				}
			}
			setLocationUpperLeft(leftCorner);
			updateBounds();
		}
			
	}

	/**
		 
		 * 
		 */
	public class ClosedGroupSmartHandleList extends SmartHandleList  implements RectangleEdgePositions{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ClosedGroupSmartHandleList() {
			for(int i:RectangleEdges.internalLocations) {
				crateHandleFor(i);
			}
		}
		
		/**
		Creates a handle for the given rectangle position i.
		 */
		public void crateHandleFor(int i) {
			SmartHandle createSmartHandle = createSmartHandle(i);
			if (i==CENTER)
				return;//no center handle yet
			
			add(createSmartHandle);
		}
		
		/**creates a handle for the given location on the bounding box. */
		public SmartHandle createSmartHandle(int location) {
			ClosedSmartHandle out = new ClosedSmartHandle(location);
			
			out.setHandleNumber(location);
			
					return out;
		}
		
		
	}



	public Color outlineColor = new Color(100,100,100,100);

	public static boolean treatGroupsLikeLayers=true;
	/**
	 * 
	 */
	private GraphicLayerPane theLayerPane=new GraphicLayerPane("Group", this);
				{theLayerPane.addLayerStructureChangeListener(this);}
	
	
	
	
	public ClosedGroup() {}
	
	
	
	/**creates a group containing all the objects for layer l*/
	public ClosedGroup(GraphicLayer donor) {
		for(ZoomableGraphic i: donor.getItemArray()) this.getTheInternalLayer().add(i);
		this.setName(donor.getName());
		this.updateBounds();
	}
	
	/**creates group with many objects*/
	public ClosedGroup( boolean b, ZoomableGraphic... tzs) {
		for(ZoomableGraphic tz: tzs) getTheInternalLayer().add(tz);
	}

	/**creates a group*/
	public ClosedGroup(ArrayList<? extends LocatedObject2D> o2) {
		for(LocatedObject2D tz: o2) 
			{if(tz instanceof ZoomableGraphic)
				getTheInternalLayer().add((ZoomableGraphic) tz);}
	}

	public static Color defaultFolderColor=new Color(200,180, 140);
	
	Color folderColor=  defaultFolderColor;
	
	double tx=0;
	double ty=0;
	Rectangle2D.Double bounds=null;

	
	private static final long serialVersionUID = 1L;
	
	

	/**Select the group*/
	@Override
	public void select() {
		super.select();
		
		
	}
	
	/**deselect the group*/
	@Override
	public void deselect() {
		super.deselect();
		
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
		
		this.tx=x;
		this.ty=y;
		
	}

	@Override
	public int isUserLocked() {
		return NOT_LOCKED;
	}

	@Override
	public ClosedGroup copy() {
		return new ClosedGroup();
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
		return bounds;
		}




	

	
	
	@Override
	public void moveLocation(double xmov, double ymov) {
		
		
		bounds.x+=xmov;
		bounds.y+=ymov;
		tx+=xmov;
		ty+=ymov;
		notifyListenersOfMoveMent();
	}
	
	public AffineTransform transform() {
		return AffineTransform.getTranslateInstance(tx, ty);
	}
	
	@Override
	public Rectangle getBounds() {
		if (bounds==null) {
			this.updateBounds();
		}
		return bounds.getBounds();
	}
	
	
	
	/**
	 * 
	 */
	public void updateBounds() {
		ArrayObjectContainer.ignoredClass=null;
		Shape a = ArrayObjectContainer.combineOutLines(getTheInternalLayer().getLocatedObjects());
		
		bounds=(Rectangle2D.Double)transform().createTransformedShape(a).getBounds2D();
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

		graphics.translate(tx*cords.getMagnification(), ty*cords.getMagnification());
		
		this.drawLayer(graphics, cords);
		
		graphics.translate(-tx*cords.getMagnification(), -ty*cords.getMagnification());
		
		if (this.isSelected()) {
			graphics.setStroke(new BasicStroke(5));
			graphics.setColor(getOutlineColor());

		}
		else if (this.isSelected()&&!this.handlesHidden) {
			
			
			
			
			
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
		 updateBounds();
	}

	@Override
	public void itemAddedToContainer(GraphicLayer gc, ZoomableGraphic z) {
		// TODO Auto-generated method stub
		 updateBounds();
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
		this.updateBounds();
	}
	

	
	@Override
	public SVGExporter getSVGEXporter() {
		ShowMessage.showOptionalMessage("This figure contains objects that might not be exportable");
		return getTheInternalLayer().getSVGEXporter();
	}

	@Override
	public OfficeObjectMaker getObjectMaker() {
		ShowMessage.showOptionalMessage("This figure contains objects that might not be exportable");
		return new GroupToOffice(this);
	}
	
	/**ungroups the group, replacing it with the objects*/
	public void ungroup() {
	
		int i = getParentLayer().getItemArray().indexOf(this);
		getParentLayer().add(getTheInternalLayer());
		getParentLayer().moveItemToIndex(getTheInternalLayer(), i);//does not move the item correctly
		
		getParentLayer().remove(this);
	}
	
	@Override
	public PopupMenuSupplier getMenuSupplier() {
		return  getMenu();
	}
	
	public PopupMenuSupplier getMenu() {
		return new GroupGraphicMenu(this);
	}
	
	
	transient ClosedGroupSmartHandleList closedGroupSmartHandleList=null;
	@Override
	public SmartHandleList getSmartHandleList() {
		if (closedGroupSmartHandleList==null)
			closedGroupSmartHandleList = new ClosedGroupSmartHandleList();
		return closedGroupSmartHandleList;
	}
	
	
	
	@Override
	public int handleNumber(double x, double y) {
		return this.getSmartHandleList().handleNumberForClickPoint(x, y);
	}
	
	
	
	/**The menu that appears when a user right clicks on a group*/
	class GroupGraphicMenu extends SmartPopupJMenu implements 
	PopupMenuSupplier  {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public GroupGraphicMenu(ClosedGroup graphicGroup) {
			
			BasicSmartMenuItem j = new BasicSmartMenuItem("Break Group") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					ungroup();
					
					
				}
			};
			
			add(j);
		}

		@Override
		public JPopupMenu getJPopup() {
			return this;
		}

		

		
		
	}

	@Override
	public ArrayList<ZoomableGraphic> getAllHeldGraphics() {
		// TODO Auto-generated method stub
		return new ArrayList<ZoomableGraphic>();
	}
	
	
	

}
