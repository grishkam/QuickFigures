package graphicalObjectHandles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEventWrapper;
import genericMontageKit.BasicObjectListHandler;
import genericMontageKit.OverlayObjectManager;
import genericMontageUIKit.Object_Mover;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import undo.CombinedEdit;
import undo.UndoMoveItems;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ObjectContainer;
import utilityClassesForObjects.RectangleEdges;

/**A class for the  handles on an ImagePanel*/
public class ImagePanelHandle extends SmartHandle {

	int handlecode=0;
	
	/**The panel on which this acts*/
	private ImagePanelGraphic panel;
	
	private Point2D originalL;//the location of the upper left corner of the panel when the mouse press starts
	private CombinedEdit undo;
	
	
	/**another panel within the image which occupies the space that this one is being dragged over*/
	private ImagePanelGraphic rivalPanel;
	private PanelMovementGroupItems passengers;
	private PanelMovementGroupItems rivalFamily;
	
	public ImagePanelHandle(int x, int y) {
		super(x, y);
		super.setEllipseShape(true);
	}
	
	public ImagePanelHandle(ImagePanelGraphic panel, int handlenum) {
		this(0,0);
		handlecode=handlenum;
		this.panel=panel;
		super.setCordinateLocation(RectangleEdges.getLocation(handlecode, getBounds()));
		this.setHandleNumber(handlenum);
		if(RectangleEdges.CENTER==handlenum) {
			super.handlesize*=5;
		} 
		
	}
	
	/**Returns a 4 direction arrow shape that will appear on this handle*/
	protected Area getOverdecorationShape() {
		if (overDecorationShape==null &&getHandleNumber()==ImagePanelGraphic.CENTER) {
			decorationColor=Color.black;
			overDecorationShape=super.getAllDirectionArrows(3, 3, false);
		}
		return overDecorationShape;
	}
	
	public void handlePress(CanvasMouseEventWrapper e) {
		undo=new CombinedEdit(new UndoMoveItems(panel));
		rivalPanel=null;
		originalL=panel.getLocationUpperLeft();
		
		/**Will move all the items above the panel within the same area*/
		passengers=createPassengerList(e, panel, null, e.shfitDown());
		
	}

	/**
	creates a PanelMovementGroup for moving the objects that are above the image panel
	 */
	public 	PanelMovementGroupItems createPassengerList(CanvasMouseEventWrapper e, ImagePanelGraphic panel, PanelMovementGroupItems exclude, boolean shift) {
		ArrayList<LocatedObject2D> objects = getObjetsAbove(panel, e.getAsDisplay().getImageAsWrapper());
		if(exclude!=null) {
			objects.removeAll(exclude.originalLocations.getObjectList());
			objects.remove(exclude.panel);
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
	public void handleDrag(CanvasMouseEventWrapper e) {
		super.handleDrag(e);
		OverlayObjectManager selectionManagger = e.getAsDisplay().getImageAsWrapper().getSelectionManagger();
		
		if(super.getHandleNumber()==ImagePanelGraphic.CENTER)
			{
				dragCenterHandle(e, selectionManagger);
				}
		
			showPanelInformation(selectionManagger);
		
		/**if the user is holding shift while adjusting the frame it adjust all of the image panels*/
		if (this.getHandleNumber()==ImagePanelHandleList.FRAME_HANDLE_ID&&e.shfitDown()) {
			for (ZoomableGraphic item: e.getSelectionSystem().getSelecteditems()) {
				if (item instanceof ImagePanelGraphic) {
					((ImagePanelGraphic) item).setFrameWidthH(panel.getFrameWidthH());
					((ImagePanelGraphic) item).setFrameWidthV(panel.getFrameWidthV());
					}
			}
		}
	}

	/**creates a message below that panel that gives the user information regarding the image panel*/
	protected void showPanelInformation(OverlayObjectManager selectionManagger) {
		TextGraphic mark2 = new TextGraphic(panel.getSummary());
		mark2.hideHandles(true);
		mark2.deselect();
		mark2.setLocationUpperLeft(panel.getBounds().getX(), panel.getBounds().getMaxY()+2);
		mark2.setFontSize(panel.getBounds().height/5);
		mark2.setFontStyle(Font.BOLD);
		mark2.setTextColor(Color.green.darker());
		selectionManagger.setSelection(mark2, 1);
	}

	/**
	 Called when the center handle is dragged
	 */
	public void dragCenterHandle(CanvasMouseEventWrapper e, OverlayObjectManager selectionManagger) {
		Rectangle2D r = Object_Mover.getNearestPanelRect(e.getAsDisplay().getImageAsWrapper(), e.getCoordinatePoint().getX(), e.getCoordinatePoint().getY(), true, null);
		Rectangle b = panel.getBounds();
		RectangularGraphic mark = RectangularGraphic.blankRect(b, Color.green, true, true);
		Object_Mover.snapRoi(mark,r, 2, true);
		mark.setStrokeWidth(1);
		selectionManagger.setSelection(mark, 0);
		
		/**check if another panel is in that destination location*/
		ArrayList<LocatedObject2D> allObjects = e.getAsDisplay().getImageAsWrapper().getLocatedObjects();
		allObjects.remove(panel);
		
		ArraySorter.removeThoseNotOfClass(allObjects, ImagePanelGraphic.class);
		LocatedObject2D p = BasicObjectListHandler.identifyPanel(mark.getBounds(), allObjects);
		if(p!=panel && p instanceof ImagePanelGraphic) {
			rivalPanel=(ImagePanelGraphic) p;//this panel occupies the destination space
			RectangularGraphic mark2 = RectangularGraphic.blankRect(new Rectangle2D.Double(originalL.getX(), originalL.getY(), rivalPanel.getObjectWidth(), rivalPanel.getObjectHeight()), Color.blue, true, true);
			mark2.setStrokeWidth(1);
			selectionManagger.setSelection(new GraphicGroup(true, mark, mark2), 0);
			rivalFamily=this.createPassengerList(e, rivalPanel, passengers, e.shfitDown());
			
			
		} else rivalPanel=null;
		
		
		if (passengers!=null)passengers.movePassengers();
	}

	
	
	public void handleRelease(CanvasMouseEventWrapper e) {
		
		if(super.getHandleNumber()==ImagePanelGraphic.CENTER)
		{
		releaseCenterHandle(e);
		}
		e.getAsDisplay().getImageAsWrapper().getSelectionManagger().setSelectionstoNull();
		
	}

	/**
	 * @param e
	 */
	public void releaseCenterHandle(CanvasMouseEventWrapper e) {
		Rectangle2D r = Object_Mover.getNearestPanelRect(e.getAsDisplay().getImageAsWrapper(), e.getCoordinatePoint().getX(), e.getCoordinatePoint().getY(), true, null);
		Object_Mover.snapRoi(panel,r, 2, true);
		
		/**If a panel was found already occupying the target location, swaps the locations*/
		if(rivalPanel!=null) {
			if (undo!=null) undo.addEditToList(new UndoMoveItems(rivalPanel));
			rivalPanel.setLocationUpperLeft(originalL);
			undo.establishFinalState();
			rivalPanel=null;
			rivalFamily.movePassengers();
			undo.addEditToList(rivalFamily.originalLocations);
		}
		if (passengers!=null) {
				passengers.movePassengers();
				undo.addEditToList(passengers.originalLocations);
						
			}
		
		e.addUndo(undo);
	}
	

	/**What to do when a handle is moved from point p1 to p2. TODO determine if this is redundant to the handleMove method in imagePanel*/
	public void handleMove(Point2D p1, Point2D p2) {
	

	int handlenum=handlecode;
		if (handlenum==ImagePanelHandleList.FRAME_HANDLE_ID) {
			double bottom=y+panel.getObjectHeight();
			double size=p2.getY()-bottom;
			if(size>=0) {
				panel.setFrameWidthV(size);
				panel.notifyListenersNow();
			}
			return;
		}
		
		if (handlenum==8) return;// the handleDrag method does what is needed in this case
	
		panel. setLocationType(RectangleEdges.oppositeSide(handlenum));
		 double dist1=RectangleEdges.distanceOppositeSide(handlenum, panel.getCroppedImageSize());
		double dist2= RectangleEdges.getLocation(panel.getLocationType(), getBounds()).distance(p2);
	
		if (panel.getScale()==dist2/dist1) return;
		panel.setScale( dist2/dist1);		
		panel.notifyListenersNow();
			}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**The items above a panel should stay above that panel when the center handle is used*/
	public class PanelMovementGroupItems implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ImagePanelGraphic panel;
		private UndoMoveItems originalLocations;
		private Point2D oriL;

		public PanelMovementGroupItems(ImagePanelGraphic panel, ArrayList<LocatedObject2D> objects) {
			this.panel=panel;
			originalLocations=new UndoMoveItems(objects);
			oriL=panel.getLocationUpperLeft();
			
		}
		
		/**
		 Moves several other objects from their original locations to that of the panel displacement			 
		 */
		public void movePassengers() {
			if (passengers!=null) {
				double dx = panel.getLocationUpperLeft().getX()-this.oriL.getX();
				double dy = panel.getLocationUpperLeft().getY()-this.oriL.getY();
				originalLocations.undo();
				for(LocatedObject2D o: originalLocations.getObjectList()) {
					o.moveLocation(dx, dy);
				}
				
			}
		}

	}

}