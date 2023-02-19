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
 * Date Created: Nov 24, 2021
 * Date Modified: Nov 25, 2021
 * Version: 2023.1
 */
package handles;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import actionToolbarItems.AlignItem;
import applicationAdapters.CanvasMouseEvent;
import genericTools.Object_Mover;
import graphicTools.AttachedItemTool;
import graphicTools.AttachedItemTool2;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.OverlayObjectManager;
import layout.PanelLayout;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.TakesAttachedItems;
import undo.CombinedEdit;
import undo.UndoAddOrRemoveAttachedItem;
import undo.UndoMoveItems;
import undo.UndoReorder;
import undo.UndoTagChange;
import utilityClasses1.TagConstants;

/**
 A handle that can be used to attach text item to other objects 
 */
public class ItemGlueSmartHandle extends SmartHandle {

	/**
	 * 
	 */
	public static final int GLUE_HANDLE_ID = 9803720;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextGraphic target;
	private CombinedEdit currentEdit=null;
	private CanvasMouseEvent pressLocation;
	private boolean attach;
	private LocatedObject2D newAttachmentSite;
	private ArrowGraphic lineMark;

	/**
	 * @param textGraphic
	 */
	public ItemGlueSmartHandle(TextGraphic textGraphic) {
		this.target=textGraphic;
		this.setHandleColor(Color.red);
		this.setHandleNumber(GLUE_HANDLE_ID);
	}
	
	@Override
	public boolean isHidden() {
		if(target.isEditMode())
			return true;
		return super.isHidden();
	}
	
	/**location of the handle. this determines where in the figure the handle will actually appear
	   overwritten in many subclasses*/
	public Point2D getCordinateLocation() {
		double locy = target.getBounds().getMaxY();
		double locx = target.getBounds().getCenterX();
		return new Point2D.Double(locx, locy+target.getFont().getSize()/15);
	}

	
	/**Called when a handle is pressed*/
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		pressLocation=canvasMouseEventWrapper;
	
	}
	
	/**called when a user drags a handle */
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		
		OverlayObjectManager selectionManagger =lastDragOrRelMouseEvent.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger();
		if(pressLocation!=null&&pressLocation.getCoordinatePoint().distance(lastDragOrRelMouseEvent.getCoordinatePoint())>10)
				showMessageForPredictedAction(lastDragOrRelMouseEvent, target.getAttachmentPosition());
		else selectionManagger.setSelectionstoNull();
		
	
	}
	
	
	/**determines what kinds of actions are possible and displays a message*/
	public void showMessageForPredictedAction(CanvasMouseEvent mEvent, AttachmentPosition originalSnap) {

		attach=false;
		LocatedObject2D potentialNewAttachmentSite = AttachedItemTool.getPotentialLockAcceptorAtPoint(mEvent.getCoordinatePoint(), this.getObject(), mEvent.getAsDisplay().getImageAsWorksheet(), false);
		TakesAttachedItems b = Object_Mover.findLockContainer(getObject(), mEvent.getAsDisplay().getImageAsWorksheet());
		
		
		
		lineMark = new ArrowGraphic(pressLocation.getCoordinatePoint(),mEvent.getCoordinatePoint());
		lineMark.setStrokeColor(Color.red.darker());
		lineMark.setStrokeWidth(2);
		lineMark.hideNormalHandles=true;
		lineMark.setNumerOfHeads(0);
		lineMark.setDashes(new float[] {1,1});
		
		Point2D p2=mEvent.getCoordinatePoint();
		OverlayObjectManager selectionManagger = mEvent.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger();
		
		if(potentialNewAttachmentSite==b&&b!=null)
			selectionManagger.setSelectionstoNull();
		else
			if(potentialNewAttachmentSite==null)
				selectionManagger.setSelection(lineMark, 0);
			else
		if(b==null &&potentialNewAttachmentSite!=null) {
			Rectangle bounds = findBoundsOfAttachmentLocation(potentialNewAttachmentSite, this.getObject().getAttachmentPosition(), mEvent.getCoordinatePoint());
			createAttachMarker(p2, selectionManagger,bounds);
			this.attach=true;
			this. newAttachmentSite= potentialNewAttachmentSite;
			}
		else
		if(b!=null &&potentialNewAttachmentSite==null&& outOfRange(mEvent, (LocatedObject2D) b))
			createReleaseMarker(p2, selectionManagger);
		else
		if(b!=null&&potentialNewAttachmentSite!=null)
			createTransplantMarker(p2, selectionManagger,potentialNewAttachmentSite);
		else 
			selectionManagger.setSelectionstoNull();
		
		
		/**if(out &&willTransplant) {
			releaseIt=true;
			
			
			
			if (potentialNewAttachmentSite!=null)
				{
				
				potentialTransplantTarget=potentialNewAttachmentSite;
				transplantIt=true;
				} else transplantIt=false;
			if(originalSnap!=null)
				getObject().getAttachmentPosition().copyPositionFrom(originalSnap);
			
		} else {
			releaseIt=false;
			transplantIt=false;
			
		}*/
	}

	/**Returns a rectangle that best depicts where the label be be attached
	 * @param potentialNewAttachmentSite
	 * @param point 
	 * @param attachmentPosition 
	 * @return
	 */
	protected Rectangle findBoundsOfAttachmentLocation(LocatedObject2D potentialNewAttachmentSite, AttachmentPosition attachmentPosition, Point2D point) {
		if( potentialNewAttachmentSite==null)
			return null;
		
		if(attachmentPosition==null)
			attachmentPosition=AttachmentPosition.defaultInternalPanel();
		
		Rectangle bounds = potentialNewAttachmentSite.getBounds();
		
		if(potentialNewAttachmentSite instanceof PanelLayoutGraphic) {
			 PanelLayoutGraphic layout=(PanelLayoutGraphic) potentialNewAttachmentSite ;
			
				PanelLayout panelLayout = layout.getPanelLayout();
				
				
				
				bounds= panelLayout.getNearestPanel(point.getX(), point.getY()).getBounds();
			
		}
		
		
		if(potentialNewAttachmentSite instanceof DefaultLayoutGraphic) {
			DefaultLayoutGraphic layout=(DefaultLayoutGraphic) potentialNewAttachmentSite ;
			
			 BasicLayout panelLayout = layout.getPanelLayout();
				
				
				if(attachmentPosition.isColumnAttachment())
					panelLayout =panelLayout.makeAltered(LayoutSpaces.COLS);
				if(attachmentPosition.isRowAttachment())
					panelLayout =panelLayout.makeAltered(LayoutSpaces.ROWS);
				
				bounds= panelLayout.getNearestPanel(point.getX(), point.getY()).getBounds();
			
		}
		
		return bounds;
	}
	
	/**Returns a rectangle that best depicts where the label be be attached
	 * @param potentialNewAttachmentSite
	 * @param point 
	 * @param attachmentPosition 
	 * @return
	 */
	protected int findIndexOfAttachmentLocation(LocatedObject2D potentialNewAttachmentSite, AttachmentPosition attachmentPosition, Point2D point) {
		if( potentialNewAttachmentSite==null)
			return 1;
		
		if(attachmentPosition==null)
			attachmentPosition=AttachmentPosition.defaultInternalPanel();
		
		int index=1;
		
		if(potentialNewAttachmentSite instanceof PanelLayoutGraphic) {
			 PanelLayoutGraphic layout=(PanelLayoutGraphic) potentialNewAttachmentSite ;
			
				PanelLayout panelLayout = layout.getPanelLayout();
				
				
				
				index= panelLayout.getNearestPanelIndex(point.getX(), point.getY());
			
		}
		
		
		if(potentialNewAttachmentSite instanceof DefaultLayoutGraphic) {
			DefaultLayoutGraphic layout=(DefaultLayoutGraphic) potentialNewAttachmentSite ;
			
			 BasicLayout panelLayout = layout.getPanelLayout();
				
				
				if(attachmentPosition.isColumnAttachment())
					panelLayout =panelLayout.makeAltered(LayoutSpaces.COLS);
				if(attachmentPosition.isRowAttachment())
					panelLayout =panelLayout.makeAltered(LayoutSpaces.ROWS);
				
				index= panelLayout.getNearestPanelIndex(point.getX(), point.getY());
			
		}
		
		return index;
	}

	/**
	 * @param p2
	 * @param selectionManagger
	 * @return
	 */
	protected TextGraphic createReleaseMarker(Point2D p2, OverlayObjectManager selectionManagger) {
		TextGraphic marker = new TextGraphic("Release Item?");
		marker.setLocation(p2);
		LocatedObject2D marker2 = getObject().copy();
		marker2.setLocation(p2.getX(), p2.getY()+10);
		selectionManagger.setSelection(marker, 1);
		selectionManagger.setSelection(marker2, 0);
		return marker;
	}
	
	/**
	 * @param p2
	 * @param selectionManagger
	 * @return
	 */
	protected TextGraphic createTransplantMarker(Point2D p2, OverlayObjectManager selectionManagger, LocatedObject2D a) {
		TextGraphic marker = new TextGraphic("Transplant Item?");
		RectangularGraphic marker3 = RectangularGraphic.blankRect(findBoundsOfAttachmentLocation(a, a.getAttachmentPosition(), p2), Color.green);
		selectionManagger.setSelection(marker3, 0);
		
		marker.setTextColor(Color.red);
		marker.setLocation(p2);
		LocatedObject2D marker2 = getObject().copy();
		marker2.setLocation(p2.getX(), p2.getY()+10);
		selectionManagger.setSelection(marker, 1);
		selectionManagger.setSelection(marker2, 0);
		selectionManagger.setSelection(marker3, 0);
		return marker;
	}
	
	
	
	
	/**
	 * @param p2
	 * @param selectionManagger
	 * @return
	 */
	protected TextGraphic createAttachMarker(Point2D p2, OverlayObjectManager selectionManagger,Rectangle rect) {
		TextGraphic messageMarker = new TextGraphic("Attach Item?");
		RectangularGraphic marker3 = RectangularGraphic.blankRect(rect, Color.green);
		selectionManagger.setSelection(marker3, 0);
		
		messageMarker.setTextColor(Color.red);
		messageMarker.setLocation(p2);
		LocatedObject2D marker2 = getObject().copy();
		
		marker2.setLocation(p2.getX(), p2.getY()+10);
		marker2=this.lineMark;
		selectionManagger.setSelection(marker3, 1);
		selectionManagger.setSelection(marker2, 0);
		selectionManagger.setSelection(messageMarker, 2);
		return messageMarker;
	}
	
	

	/**
	 * @return
	 */
	private LocatedObject2D getObject() {
		return target;
	}
	
	private void performTransplant(CombinedEdit currentEdit, Object potentialTransplantTarget) {
		
		/**These lines of code move the locked item beween lock takers*/
		TakesAttachedItems potentialTransplant2 = (TakesAttachedItems) potentialTransplantTarget;
		UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(potentialTransplant2, getObject() , false);
		potentialTransplant2.addLockedItem(getObject());
		if(currentEdit!=null) currentEdit.addEditToList(undo);
		
		boolean needsLayerTransplant = false;
		boolean needsLayerReorder = false;
		
		try {
		ZoomableGraphic z= (ZoomableGraphic) potentialTransplantTarget;
		ZoomableGraphic z2= (ZoomableGraphic) getObject();
		
			if(!z.getParentLayer().getParentLayer().hasItem(z))
				needsLayerTransplant = true;
			
			
			
			ArrayList<ZoomableGraphic> all = z.getParentLayer().getTopLevelParentLayer().getAllGraphics();
			if(all.indexOf(z)>all.indexOf(z2))
				needsLayerReorder = true;
			
			if(needsLayerReorder) {
				UndoReorder undoRe = AlignItem.moveItemInLayer(z2, AlignItem.MOVE_TO_FRONT, z2.getParentLayer());			
				currentEdit.addEditToList(undoRe);
			}
			
			if (needsLayerTransplant) {
				//not yet implemented
			}
			
			} catch (Throwable t) {}

		
	}
	
	/**returns true if the user mouse event has dragged the objects beyond the range of its attachment point*/
	protected boolean outOfRange(CanvasMouseEvent mEvent, LocatedObject2D attachmentSite) {
		return AttachedItemTool2.outofRange(this.getObject().getBounds(), attachmentSite.getBounds(), mEvent.getCoordinatePoint());
	}
	
	
	public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
		
		if(attach) {
			this.getObject().setLocation(canvasMouseEventWrapper.getCoordinatePoint());
			currentEdit=new CombinedEdit();
			currentEdit.addEditToList(new UndoMoveItems( getObject()));
			TakesAttachedItems newAttachmentSite2 = (TakesAttachedItems) newAttachmentSite;
			
			if(target.getTag(TagConstants.INDEX)!=null) {//if there is a layout address on the item
				currentEdit.addEditToList(new  UndoTagChange(target.getTagHashMap()));
				 int i=findIndexOfAttachmentLocation(newAttachmentSite, target.getAttachmentPosition(), canvasMouseEventWrapper.getCoordinatePoint());
				 target.getTagHashMap().put(TagConstants.INDEX, i);
				
			}
			UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(newAttachmentSite2, getObject() , false);
			currentEdit.addEditToList(undo);
			newAttachmentSite2 .addLockedItem(getObject());
			
			
			canvasMouseEventWrapper.getAsDisplay().getUndoManager().addEdit(currentEdit);
		}
		canvasMouseEventWrapper.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionstoNull();
		
	}
}
