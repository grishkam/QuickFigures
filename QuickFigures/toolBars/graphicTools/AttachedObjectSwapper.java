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
 * Date Modified: Jan 5, 2021
 * Version: 2022.0
 */
package graphicTools;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.ImageWorkSheet;
import graphicalObjects_Shapes.RectangularGraphic;
import imageDisplayApp.OverlayObjectManager;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.TakesAttachedItems;
import undo.CombinedEdit;
import undo.UndoMoveItems;
import undo.UndoAttachmentPositionChange;
import undo.UndoAddOrRemoveAttachedItem;
/**A tool for moving attached items, switching their locations. no longer included in the toolbar
 * but kep in case a need for it appears in later versions
 * */
public class AttachedObjectSwapper extends AttachedItemTool2 {

	
	public AttachedObjectSwapper() {
		super(false);
		// TODO Auto-generated constructor stub
	}

	{createIconSet("icons2/lockGraphic3.jpg","icons2/RectangleIconPress.jpg","icons2/lockGraphic3Rollover.jpg");
	}
	
	LocatedObject2D object2=null;;
	
	private TakesAttachedItems lockTaker2=null;
	
	public void onPress(ImageWorkSheet gmp, LocatedObject2D roi2) {
		super.onPress(gmp, roi2);
		setMarkerRoi();
	}
	
	
	public void setMarkerRoi() {
		
		
				OverlayObjectManager select = this.getImageClicked().getOverlaySelectionManagger();
				
				select.setSelection(MarkerRoi(), 0);
				
			
		
	} 
	
	
	
	public LocatedObject2D MarkerRoi() {
		if (getPrimarySelectedObject()==null) return null;
		return RectangularGraphic.blankRect(this.getPrimarySelectedObject().getBounds(), Color.blue, true, true);
	}
	
	
public void mouseDragged() {
		
	object2 = getObjectAt(getImageClicked(), getDragCordinateX(), getDragCordinateY());
	if (object2==null) return;
	lockTaker2=findLockContainer(object2);
	OverlayObjectManager select = this.getImageClicked().getOverlaySelectionManagger();
	
	select.setSelection(RectangularGraphic.blankRect(object2.getBounds(), Color.green, true, true),1);
	
		
	}




public void onRelease(ImageWorkSheet gmp, LocatedObject2D roi2) {
	removeMarkerRoi();
	
	if (lockTaker==null&&lockTaker2==null&&inside!=null&&object2!=null) {
		UndoMoveItems undo = new UndoMoveItems(inside, object2);
		
		Point2D p1 = inside.getLocationUpperLeft();
		Point2D p2 = object2.getLocationUpperLeft();
		inside.setLocationUpperLeft(p2.getX(), p2.getY());
		object2.setLocationUpperLeft(p1.getX(), p1.getY());
		
		undo.establishFinalLocations();
		gmp.getImageDisplay().getUndoManager().addEdit(undo);
		return;
	}
	
	if (lockTaker==null||inside==null||object2==null||lockTaker2==null) {
		return;
	} else {
switchLockedItem(object2, inside);
	}
	
	
}


private void switchLockedItem(LocatedObject2D object2, LocatedObject2D inside) {
	ArrayList<?> allRoi = getPotentialLockAcceptors(getImageClicked());
	TakesAttachedItems t=(TakesAttachedItems) lockTaker;
	TakesAttachedItems t2=(TakesAttachedItems) lockTaker2;
	
			removeFromAlltakers(object2, allRoi, undoer);
			removeFromAlltakers(inside, allRoi, undoer);
			removeFromAlltakers(inside, allRoi, undoer);
			
			UndoAttachmentPositionChange undoS1 = new  UndoAttachmentPositionChange(inside);
			UndoAttachmentPositionChange undoS2 = new  UndoAttachmentPositionChange(object2);
			
			AttachmentPosition snap1 = inside.getAttachmentPosition();
			AttachmentPosition snap2 = object2.getAttachmentPosition();
			inside.setAttachmentPosition(snap2);
			object2.setAttachmentPosition(snap1);
			
			undoS1.establishFinalState();
			undoS2.establishFinalState();
			
		
			
			
			 t.removeLockedItem(inside);
			 t2.removeLockedItem(object2);
			 UndoAddOrRemoveAttachedItem undo1 = new UndoAddOrRemoveAttachedItem(t, inside, true);
			 UndoAddOrRemoveAttachedItem undo2 = new UndoAddOrRemoveAttachedItem(t2, object2, true);
			 
			 
			t.addLockedItem(object2);
			t2.addLockedItem(inside);
			 UndoAddOrRemoveAttachedItem undo3 = new UndoAddOrRemoveAttachedItem(t2, inside, false);
			 UndoAddOrRemoveAttachedItem undo4 = new UndoAddOrRemoveAttachedItem(t, object2, false);
			 
			CombinedEdit undoFinal = new CombinedEdit(undoS1,undoS2, undo1, undo2, undo3, undo4);
			this.getImageDisplayWrapperClick().getUndoManager().addEdit(undoFinal);
}

@Override
public String getToolTip() {
		
		return "Switch Locations of Objects";
	}

public void removeMarkerRoi()  {
	
	getImageClicked().getOverlaySelectionManagger().removeObjectSelections();
	
}

@Override
public String getToolName() {
		return "Swaps Locations of 2 Attached Items";
	}
}
