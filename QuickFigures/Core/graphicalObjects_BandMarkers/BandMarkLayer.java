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
 * Date Created: April 17, 2022
 * Date Modified: April 17, 2022
 * Version: 2022.0
 */
package graphicalObjects_BandMarkers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JMenu;

import actionToolbarItems.AlignItem;
import applicationAdapters.CanvasMouseEvent;
import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import graphicTools.AttachedItemTool2;
import graphicalObjects.CordinateConverter;
import graphicalObjects_BandMarkers.BandMarkLayer.BandLockTaker;
import graphicalObjects_BandMarkers.BandMarkLayer.BandRightAlignHandle;
import graphicalObjects_BandMarkers.BandMarkLayer.BandTextHandle;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import handles.HandleListFilter;
import handles.RectangularShapeSmartHandle;
import handles.SmartHandle;
import handles.SmartHandleList;
import locatedObject.ArrayObjectContainer;
import locatedObject.AttachedItemList;
import locatedObject.AttachmentPosition;
import locatedObject.CarriesLockTaker;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import locatedObject.TakesAttachedItems;
import logging.IssueLog;
import popupMenusForComplexObjects.DonatesMenu;
import undo.CombinedEdit;
import undo.UndoAttachmentPositionChange;

/**
 A specialized layer for keeping track of a set of band labels
 */
public class BandMarkLayer extends GraphicLayerPane implements CarriesLockTaker, HandleListFilter, DonatesMenu{

	


	




	/**
		 
		 * 
		 */
	public class BandTextHandle extends SmartHandle {

		private static final int BAND_TEXT_HANDLE_ID = 1008;
		private TextGraphic targetText;
		private CombinedEdit currentEdit;

		/**
		 * @param bandMarkLayer
		 * @param t
		 */
		public BandTextHandle(BandMarkLayer bandMarkLayer, TextGraphic t) {
			targetText=t;
			this.setHandleColor(Color.red);
			this.setEllipseShape(true);
			this.setHandleNumber(SmartHandleList.OVERRIDE_DRAG_HANDLE);
		}
		
		@Override
		public boolean isHidden() {
			return targetText.isEditMode();
		}
		
		public Point2D getCordinateLocation() {
			Rectangle b = targetText.getBounds();
			return new Point2D.Double(b.getCenterX(), b.getCenterY());
			}
		
		/**What to do when a handle is moved from point p1 to p2*/
		@Override
		public void handleDrag(CanvasMouseEvent mEvent) {
			Point2D p2=mEvent.getCoordinatePoint();
			UndoAttachmentPositionChange undo = new UndoAttachmentPositionChange(targetText);
			
				AttachedItemTool2.adjustPosition((int)p2.getX(), (int)p2.getY(), map.get(targetText).getBounds(),targetText, 0.9);
			
				//targetText.getAttachmentPosition().setToNearestSnap(getObject().getBounds().getBounds(), attachmentSite.getContainerForBounds(object), new Point((int)p2.getX(), (int)p2.getY() ));
			
			
			undo.establishFinalState();
			if(!undo.same()) {
				if(currentEdit==null)
				getUndoManager().addEdit(undo);
				else currentEdit.addEditToList(undo);
			}
			snapLockedItems();
		
		
		}
		
		
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			
			currentEdit=new CombinedEdit();
			}
	}



	/**
	 * 
	 */
	private static final int POSITION_HANDLE_id = 15;
	GenericLockTaker lockedItemL=new BandLockTaker(this);
	AttachedItemList list=new AttachedItemList(lockedItemL);
	
	private ImagePanelGraphic parentPanel;
	HashMap<TextGraphic, ArrowGraphic> map;
	private AttachmentPosition at;
	
	
	/**
	 * @param name
	 */
	public BandMarkLayer(ImagePanelGraphic parentPanel) {
		super("Band Marks");
		this.parentPanel=parentPanel;
	}

	
	/**draws the layer*/
	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		
		super.draw(graphics, cords);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public TakesAttachedItems getLockTaker() {
		return lockedItemL;
	}
	
	/**Creates the band marks*/
	public void createBandMarks(Rectangle2D r) {
		int nMarks=4;
		double x=r.getMinX();
		double y=r.getMinY();
		double yStep = r.getWidth()/nMarks;
		map= new HashMap<TextGraphic, ArrowGraphic>();
		at = AttachmentPosition.defaultRowLabel();
		for(int i=0; i<nMarks; i++) {
			double yPosition = y+i*yStep;
			String description2 = "Mark "+i;
			createBandMark(x, yPosition, description2, null, null, 6);
		}
		alignBandLocations() ;
		this.snapLockedItems();
	}


	@MenuItemMethod(menuText = "Add new mark")
	public void createBandMarkFromPoint(CanvasMouseEvent c) {
		Point point = c.getCoordinatePoint();
		ArrowGraphic modelA = null;
		TextGraphic modelT =null;
		if(map.keySet().size()>0) {
			for(TextGraphic key: map.keySet()) {modelT=key;modelA=map.get(key);}
		}
		createBandMark(point.getX(), point.getY()+10, "new mark", modelT, modelA,5);
		this.snapLockedItems();
	}
	
	/**
	 * @param x
	 * @param yPosition
	 * @param description2
	 * @param i 
	 */
	protected void createBandMark(double x, double yPosition, String description2, TextGraphic modelT, ArrowGraphic modelA, int i) {
		TextGraphic tg = new TextGraphic(description2);
		if(modelT!=null)
			tg=modelT.copy();
		tg.usesGlueHandle=false;
		ArrowGraphic ar=new ArrowGraphic();
		if(modelA!=null)
			ar=modelA.copy();
		if(modelA==null) {
			ar.setXLocations(ar.getLineStartLocation().getX(), ar.getLineStartLocation().getX()+i);
			ar.getHead().setArrowHeadSize(5);
			ar.setNumerOfHeads(0);
		}
		ar.setLocation(x, yPosition);
		ar.setYLocation(yPosition);
		if(modelA!=null)
			ar.setXLocations(modelA.getLineStartLocation().getX(), modelA.getLineEndLocation().getX());
		
		tg.setHandleListFilter(this);
		tg.setLocation(ar.getLocation());
		tg.setAttachmentPosition(at);
		map.put(tg, ar);
		ar.setStrokeColor(Color.black);
		ar.setHandleListFilter(this);
		list.add(tg);
		this.add(tg);
		this.add(ar);
		
	}
	
	public void alignBandLocations() {
		Collection<ArrowGraphic> arrows = map.values();
		ArrayList<LocatedObject2D> list2 = new ArrayList<LocatedObject2D>(); list2.addAll(arrows);
		Rectangle r = new Rectangle(parentPanel.getBounds().x, 0, 0,0);
		new AlignItem(RectangleEdges.RIGHT).allignArray(list2, r);
		
		
		for(TextGraphic text: map.keySet()) {
			ArrowGraphic arrow = map.get(text);
			int xConstraint = parentPanel.getBounds().x;
			text.getAttachmentPosition().snapObjectToRectangle(text, arrow.getBounds());
			arrow.getMenuSupplier().makeHorizontal();
			
		}
	}
	
	
	




@Override
public void refineHandleList(Object graphicWithHandles, SmartHandleList smList) {
	if(map.values().contains(graphicWithHandles)) {
		ArrowGraphic theArrow=(ArrowGraphic) graphicWithHandles;
		SmartHandle a1 = smList.getHandleNumber(ArrowGraphic.HANDLE_1) ;
		if(a1==null)
			return;//if the list has already been modified then there is not need to continue 
		SmartHandle a2 = smList.getHandleNumber(ArrowGraphic.HANDLE_2) ;
		SmartHandle rot1 = smList.getHandleNumber(RectangularShapeSmartHandle.ROTATION_HANDLE);
		smList.remove(rot1);
		smList.remove(a1);
		smList.remove(a2);
		
		smList.add(new BandRightAlignHandle(this, theArrow, ArrowGraphic.HANDLE_1));
		smList.add(new BandRightAlignHandle(this, theArrow, ArrowGraphic.HANDLE_2));
		smList.add(new BandRightAlignHandle(this, theArrow, SmartHandleList.OVERRIDE_DRAG_HANDLE));
		
		
		//smList.overrideHandle=new BandDragOverride(theArrow);
		//smList.add(smList.overrideHandle);
		
	}
	
	if(map.keySet().contains(graphicWithHandles)) {
		TextGraphic t=(TextGraphic) graphicWithHandles;
		smList.add(new BandTextHandle(this, t));
	}
	
}



/**
	 
	 * 
	 */
public class BandRightAlignHandle extends SmartHandle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrowGraphic targetArrow;
	private BandMarkLayer theMarkLayer;

	/**
	 * @param bandMarkLayer
	 * @param arrowGraphic
	 * @param handle1 the handle id number
	 */
	public BandRightAlignHandle(BandMarkLayer bandMarkLayer, ArrowGraphic arrowGraphic, int handle1) {
		this.targetArrow=arrowGraphic;
		theMarkLayer=bandMarkLayer;
		this.setHandleNumber(handle1);
		this.setHandleColor(Color.cyan);
	}
	
	public Point2D getCordinateLocation() {
		ArrayList<Point2D> ends = targetArrow.getEndPoints();
		if (this.getHandleNumber()==ArrowGraphic.HANDLE_1) return ends.get(0);
		Point2D point2d = ends.get(1);
		if (this.getHandleNumber()==ArrowGraphic.HANDLE_2) return point2d;
		if (this.getHandleNumber()==POSITION_HANDLE_id) { 
			
			return new Point2D.Double(point2d.getX()-4, point2d.getY());
		}
		return new Point();
		}
	
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
		if (this.getHandleNumber()==ArrowGraphic.HANDLE_1) {
			double lengthChange = p2.getX()-targetArrow.getLineStartLocation().getX();
			double newX = targetArrow.getLineStartLocation().getX()+lengthChange;
			double newWidth = targetArrow.getLineEndLocation().getX()-newX;
			if(newWidth<=0)
				return;
			for(ArrowGraphic a: map.values()) {
				a.setLocation(newX, a.getLocation().getY());
				a.setObjectWidth(newWidth);
			}
		}
		
		if (this.getHandleNumber()==ArrowGraphic.HANDLE_2) {
			targetArrow.setYLocation(p2.getY());
			double dx = p2.getX()-targetArrow.getLineEndLocation().getX();
			targetArrow.moveLocation(dx, 0);
			for(ArrowGraphic a: map.values()) {
				a.setXLocations(targetArrow.getLineStartLocation().getX(), targetArrow.getLineEndLocation().getX());
			}
			
		}
		
		
		if (this.getHandleNumber()==SmartHandleList.OVERRIDE_DRAG_HANDLE) {
			targetArrow.setYLocation(p2.getY());
		}
		
		
		
		snapLockedItems();
	}

	
	
	
	/**returns true if the mouse event location is within the last drawn shape*/
	public boolean containsClickPoint(Point2D p) {
		
		return getClickableArea().contains(p);
	}

}


public class BandLockTaker extends GenericLockTaker {

	/**
	 * @param parentObject
	 */
	public BandLockTaker(Object parentObject) {
		super(parentObject);
		// TODO Auto-generated constructor stub
	}
	
	
	
	

}

/**
 * 
 */
protected void snapLockedItems() {
	for(TextGraphic text: map.keySet()) {
		ArrowGraphic arrow = map.get(text);
		
		text.getAttachmentPosition().snapObjectToRectangle(text, arrow.getBounds());
		
		
	}
}


@Override
public JMenu getDonatedMenuFor(Object requestor) {
	if(map.keySet().contains(requestor)||map.values().contains(requestor)) {
		JMenu jMenu = new MenuItemExecuter(this).getJMenu();
		jMenu.setText("Band marks ");
		return jMenu;
	}
	return null;
}

/**returne the bounds of all the objects*/
public Rectangle fulloutline() {
	 return ArrayObjectContainer.combineBounds(this.getLocatedObjects()).getBounds();
}

}
