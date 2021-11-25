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
 * Date Modified: Nov 24, 2021
 * Version: 2021.2
 */
package handles;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import actionToolbarItems.AlignItem;
import applicationAdapters.CanvasMouseEvent;
import genericTools.Object_Mover;
import graphicTools.AttachedItemTool;
import graphicTools.AttachedItemTool2;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.OverlayObjectManager;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.TakesAttachedItems;
import undo.CombinedEdit;
import undo.UndoAddOrRemoveAttachedItem;
import undo.UndoMoveItems;
import undo.UndoReorder;

/**
 A handle that can be used to attach text item to other objects 
 */
public class ItemGlueSmartHandle extends SmartHandle {

	/**
	 * 
	 */
	public static final int GLUE_HANDLEid = 9803720;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextGraphic target;
	private CombinedEdit currentEdit=null;
	private CanvasMouseEvent pressLocation;
	private boolean attach;
	private LocatedObject2D newAttachmentSite;

	/**
	 * @param textGraphic
	 */
	public ItemGlueSmartHandle(TextGraphic textGraphic) {
		this.target=textGraphic;
		this.setHandleColor(Color.red);
		this.setHandleNumber(GLUE_HANDLEid);
	}
	
	/**location of the handle. this determines where in the figure the handle will actually appear
	   overwritten in many subclasses*/
	public Point2D getCordinateLocation() {
		double locy = target.getBounds().getMaxY();
		double locx = target.getBounds().getCenterX();
		return new Point2D.Double(locx, locy+target.getFont().getSize()/2);
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
		
		
		Point2D p2=mEvent.getCoordinatePoint();
		OverlayObjectManager selectionManagger = mEvent.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger();
		
		if(potentialNewAttachmentSite==b)
			selectionManagger.setSelectionstoNull();
		else
		if(b==null &&potentialNewAttachmentSite!=null) {
			createAttachMarker(p2, selectionManagger,potentialNewAttachmentSite);
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
		RectangularGraphic marker3 = RectangularGraphic.blankRect(a.getBounds(), Color.green);
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
	protected TextGraphic createAttachMarker(Point2D p2, OverlayObjectManager selectionManagger, LocatedObject2D a) {
		TextGraphic marker = new TextGraphic("Attach Item?");
		RectangularGraphic marker3 = RectangularGraphic.blankRect(a.getBounds(), Color.green);
		selectionManagger.setSelection(marker3, 0);
		
		marker.setTextColor(Color.red);
		marker.setLocation(p2);
		LocatedObject2D marker2 = getObject().copy();
		marker2.setLocation(p2.getX(), p2.getY()+10);
		selectionManagger.setSelection(marker, 1);
		selectionManagger.setSelection(marker2, 0);
		selectionManagger.setSelection(marker3, 2);
		return marker;
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
			UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(newAttachmentSite2, getObject() , false);
			currentEdit.addEditToList(undo);
			newAttachmentSite2 .addLockedItem(getObject());
			
			canvasMouseEventWrapper.getAsDisplay().getUndoManager().addEdit(currentEdit);
		}
		canvasMouseEventWrapper.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionstoNull();
		
	}
}
