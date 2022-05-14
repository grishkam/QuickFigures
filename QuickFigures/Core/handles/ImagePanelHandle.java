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
 * Date Modified: Jan 25, 2022
 * Version: 2022.1
 */
package handles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import genericTools.Object_Mover;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.GraphicList;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.OverlayObjectManager;
import layout.BasicObjectListHandler;
import locatedObject.LocatedObject2D;
import locatedObject.ObjectContainer;
import locatedObject.RectangleEdges;
import undo.CombinedEdit;
import undo.UndoMoveItems;
import utilityClasses1.ArraySorter;

/**A handle for editing ImagePanels
 * @see ImagePanelGraphic
 * @see SmartHandle
 * */
public class ImagePanelHandle extends SmartHandle {

	private int handlecode=50;
	
	/**The panel on which this acts*/
	private ImagePanelGraphic thePanel;
	
	private CombinedEdit undo;
	
	
	/**another panel within the image which occupies the space that this one is being dragged over*/
	
	private PanelMovementGroupItems passengers;

	ArrayList<PanelMovementGroupItems> allMovementGroups=new ArrayList<PanelMovementGroupItems>();
	ArrayList<PanelMovementGroupItems> draggedGroups=new ArrayList<PanelMovementGroupItems>();

	private ArrayList<RectangularGraphic> markers;

	
	public ImagePanelHandle(ImagePanelGraphic panel, int handlenum) {
		super.setEllipseShape(true);
		handlecode=handlenum;
		this.thePanel=panel;
		super.setCordinateLocation(RectangleEdges.getLocation(handlecode, panel.getBounds()));
		this.setHandleNumber(handlenum);
		if(RectangleEdges.CENTER==handlenum) {
			super.handlesize*=5;
		} 
		setupHandleColor();
	}

	/**
	sets the handle color such that the fixed edge handle will be red
	 */
	protected void setupHandleColor() {
		if(handlecode==thePanel.getLocationType())
			this.setHandleColor(Color.red);
		else this.setHandleColor(Color.white);
	}
	
	/**Returns a 4 direction arrow shape that will appear on this handle*/
	protected Area getOverdecorationShape() {
		if (overDecorationShape==null &&isCenterHandle()) {
			decorationColor=Color.black;
			overDecorationShape=super.getAllDirectionArrows(3, 3, false);
		}
		return overDecorationShape;
	}

	boolean isCenterHandle() {
		return getHandleNumber()==ImagePanelGraphic.CENTER;
	}
	
	public void handlePress(CanvasMouseEvent e) {
		
		if (isCenterHandle())
			setupForCenterHandleDrag(e);
		
	}

	/**
	 when the center handle is pressed, this method will store the original locations of every panel being 
	 moved and every item above the panel in a list of objects
	 */
	public void setupForCenterHandleDrag(CanvasMouseEvent e) {
		undo=new CombinedEdit(new UndoMoveItems(thePanel));
		
		allMovementGroups.clear();
		draggedGroups.clear();
		
		ArrayList<ZoomableGraphic> items = e.getSelectionSystem().getSelecteditems();
		for(ZoomableGraphic item: items) {
			if (item instanceof ImagePanelGraphic) {
				
				PanelMovementGroupItems p2 = createPassengerList(e, (ImagePanelGraphic) item, allMovementGroups, e.shiftDown());
				if (item==thePanel)passengers=p2;
				allMovementGroups.add(p2);
				draggedGroups.add(p2);
			}
		}
	}

	/**
	creates a PanelMovementGroup for moving the objects that are above the image panel
	 */
	public 	PanelMovementGroupItems createPassengerList(CanvasMouseEvent e, ImagePanelGraphic panel, Iterable<PanelMovementGroupItems> exclude, boolean shift) {
		ArrayList<LocatedObject2D> objects = getObjetsAbove(panel, e.getAsDisplay().getImageAsWorksheet());
		if(exclude!=null) {
			for(PanelMovementGroupItems exc:exclude) {
				if (exc==null) continue;
				objects.removeAll(exc.originalLocations.getObjectList());
				objects.remove(exc.panel);
			}
		}
		if (shift)ArraySorter.selectItems(objects);
		
		PanelMovementGroupItems pa=null;
		if (objects!=null) {
			pa=new PanelMovementGroupItems(panel, objects);
			
		} else pa=null;
		return pa;
	}
	
	/**returns all objects in a container that are located directly above the panel*/
	ArrayList<LocatedObject2D> getObjetsAbove(ImagePanelGraphic panel, ObjectContainer im) {
		ArrayList<LocatedObject2D> objects = new BasicObjectListHandler().getContainedObjects(panel.getBounds(),im);
		if (objects.contains(panel)) {
			ArraySorter.removeItemsBefore(objects, panel);
		}
		return objects;
	}

	/**performed to drag the handles*/
	public void handleDrag(CanvasMouseEvent e) {
		super.handleDrag(e);
		thePanel.dragOngoing=true;
		OverlayObjectManager selectionManagger = e.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger();
		Point p2 = e.getCoordinatePoint();
		int handlenum = this.getHandleNumber();
		if (this.getHandleNumber()<RectangleEdges.CENTER){
						moveResizeHandle(p2, handlenum);
						
		}
		
		if(super.getHandleNumber()==ImagePanelGraphic.CENTER)
			{
				thePanel.setLocationType(RectangleEdges.CENTER);
				thePanel.setLocation(p2);
				dragCenterHandle(e, selectionManagger);
				}
		
			showPanelInformation(selectionManagger);
			
		if (isFrameHandle()) {
			
			double bottom=thePanel.getLocationUpperLeft().getY()+thePanel.getObjectHeight();
			double size=e.getCoordinateY()-bottom;
			thePanel.setFrameWidthV(size);
			thePanel.notifyListenersNow();
		}	
		
		/**if the user is holding shift while adjusting the frame it adjust all of the image panels*/
		if (isFrameHandle()&&e.shiftDown()) {
			for (ZoomableGraphic item: e.getSelectionSystem().getSelecteditems()) {
				if (item instanceof ImagePanelGraphic) {
					((ImagePanelGraphic) item).setFrameWidthH(thePanel.getFrameWidthH());
					((ImagePanelGraphic) item).setFrameWidthV(thePanel.getFrameWidthV());
					}
			}
		}
	}

	/**
	 method is called when a handle is moved to resize the image panel
	 */
	public void moveResizeHandle(Point p2, int handlenum) {
		thePanel.setLocationType(RectangleEdges.oppositeSide(handlenum));
		 double dist1=RectangleEdges.distanceOppositeSide(handlenum, thePanel.getCroppedImageSize());
		 double dist2= RectangleEdges.getLocation(thePanel.getLocationType(), thePanel.getBounds()).distance(p2);
		
		if (thePanel.getRelativeScale()==dist2/dist1) {} else {
			thePanel.setRelativeScale( dist2/dist1);		
			thePanel.notifyListenersNow();
		}
	}

	/** returns true if this is the frame handle
	 * @return
	 */
	public boolean isFrameHandle() {
		return this.getHandleNumber()==ImagePanelHandleList.FRAME_HANDLE_ID;
	}

	/**creates a message below that panel that gives the user information regarding the image panel*/
	protected void showPanelInformation(OverlayObjectManager selectionManagger) {
		TextGraphic mark2 = new TextGraphic(thePanel.getSummary());
		mark2.dontScaleText=true;
		mark2.hideHandles(true);
		mark2.deselect();
		mark2.setLocationUpperLeft(thePanel.getBounds().getX(), thePanel.getBounds().getMaxY()+2);
		int fontSize = thePanel.getBounds().height/5;
		if(fontSize<16)
			fontSize=16;
		mark2.setFontSize(fontSize);
		mark2.setFontStyle(Font.BOLD);
		mark2.setTextColor(Color.green.darker());
		selectionManagger.setSelection(mark2, 1);
	}

	/**
	 Called when the center handle is dragged
	 */
	public void dragCenterHandle(CanvasMouseEvent e, OverlayObjectManager selectionManagger) {
		
		markers=new ArrayList<RectangularGraphic>();
		
		/**marks the destination panel*/
		passengers.markDestinationAndRival(e);
		if (passengers!=null)
			passengers.movePassengers();
		
		for(PanelMovementGroupItems item: draggedGroups) {
			if (item.panel==thePanel ) continue;
			item.revertOriginalLocation();
			item.panel.moveLocation(passengers.getShiftX(), passengers.getShitY());
			item.movePassengers();
			item.markDestinationAndRival(e);
		}
		
		
		selectionManagger.setSelectionGraphic(new GraphicList(markers));
	}

	
	
	
	public void handleRelease(CanvasMouseEvent e) {
		thePanel.dragOngoing=false;
		if(super.getHandleNumber()==ImagePanelGraphic.CENTER)
			{
			releaseCenterHandle(e);
			}
		e.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionstoNull();
		
	}

	/**
		After the center handle is released, image panels will snap into place
	 */
	public void releaseCenterHandle(CanvasMouseEvent e) {
		
		for(PanelMovementGroupItems p: draggedGroups) {
			p.snapPanel(e);
			
			p.snapRivalPanel();
			if (p!=null) {
				undo.addEditToList(p.originalLocations);
			}
		}
		
		
		
		e.addUndo(undo);
	}

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**This class keeps track of the items above a panel 
	  which will move with the panel when the center handle is used.
	  It also check for the destination at the drop location*/
	public class PanelMovementGroupItems implements Serializable {

		/**
		 * 
		 */
		private static final int MARKER_STROKE_WIDTH = 2;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ImagePanelGraphic panel;
		private UndoMoveItems originalLocations;
		private Point2D originalLocationOfPanel;
		
		private ImagePanelGraphic rivalPanel;
		private PanelMovementGroupItems rivalFamily;

		public PanelMovementGroupItems(ImagePanelGraphic panel, ArrayList<LocatedObject2D> objects) {
			this.panel=panel;
			originalLocations=new UndoMoveItems(objects);
			originalLocationOfPanel=panel.getLocationUpperLeft();
			
		}
		
		void revertOriginalLocation() {
			panel.setLocationUpperLeft(originalLocationOfPanel);
			originalLocations.undo();
		}
		
		/**
		aligns the ImagePanel with the layout at its destination location
		 */
		void snapPanel(CanvasMouseEvent e) {
			Point2D center = RectangleEdges.getLocation(RectangleEdges.CENTER, panel.getBounds());
			
			Rectangle2D r = Object_Mover.getNearestPanelRect(e.getAsDisplay().getImageAsWorksheet(), center, true, null);
			
			Object_Mover.snapRoi(panel,r, 2, true);
			movePassengers();
		}
		
		/**
		 Moves several other objects (the ones above the panel) from their original locations to that of the panel displacement			 
		 */
		 void movePassengers() {
			
				double dx = getShiftX();
				double dy = getShitY();
				originalLocations.undo();
				for(LocatedObject2D o: originalLocations.getObjectList()) {
					o.moveLocation(dx, dy);
				}
				
			
		}

		/**
		 returns how much the panel has moved from its original y location since the mouse drag began
		 */
		double getShitY() {
			return panel.getLocationUpperLeft().getY()-this.originalLocationOfPanel.getY();
		}

		/**
		 returns how much the panel has moved from its original x location since the mouse drag began
		 */
		 double getShiftX() {
			return panel.getLocationUpperLeft().getX()-this.originalLocationOfPanel.getX();
		}
		
		/**
		this method check the location that the panel has been dragged to 
		 */
		RectangularGraphic[] markDestinationAndRival(CanvasMouseEvent e) {
			Point2D center = RectangleEdges.getLocation(RectangleEdges.CENTER, panel.getBounds());
			
			Rectangle2D r = Object_Mover.getNearestPanelRect(e.getAsDisplay().getImageAsWorksheet(), center, true, null);
			
			Rectangle b = panel.getBounds();
			RectangularGraphic mark = RectangularGraphic.blankRect(b, Color.green, true, true);
			Object_Mover.snapRoi(mark,r, 2, true);
			mark.setStrokeWidth(MARKER_STROKE_WIDTH);
			markers.add(mark);
			
			
			/**check if another panel is in that destination location*/
			ArrayList<LocatedObject2D> allObjects = e.getAsDisplay().getImageAsWorksheet().getLocatedObjects();
			allObjects.remove(panel);
			
			ArraySorter.removeThoseNotOfClass(allObjects, ImagePanelGraphic.class);
			LocatedObject2D p = BasicObjectListHandler.identifyPanel(mark.getBounds(), allObjects);
			
			if(p!=panel && p instanceof ImagePanelGraphic) {
				rivalPanel=(ImagePanelGraphic) p;//this panel occupies the destination space
				RectangularGraphic mark2 = RectangularGraphic.blankRect(new Rectangle2D.Double(originalLocationOfPanel.getX(), originalLocationOfPanel.getY(), rivalPanel.getObjectWidth(), rivalPanel.getObjectHeight()), Color.blue, true, true);
				mark2.setStrokeWidth(MARKER_STROKE_WIDTH);
				
				rivalFamily=createPassengerList(e, rivalPanel,allMovementGroups, e.clickCount()>1);
				markers.add(mark2);
				return new RectangularGraphic[] {mark, mark2};
			} else rivalPanel=null;
			return new RectangularGraphic[] {mark, null};
			
		}
		
		/**
		 * 
		 */
		void snapRivalPanel() {
			/**If a panel was found already occupying the target location, swaps the locations*/
			if(rivalPanel!=null) {
				if (undo!=null) undo.addEditToList(new UndoMoveItems(rivalPanel));
				rivalPanel.setLocationUpperLeft(originalLocationOfPanel);
				undo.establishFinalState();
				rivalPanel=null;
				rivalFamily.movePassengers();
				undo.addEditToList(rivalFamily.originalLocations);
			}
		}



	}

}