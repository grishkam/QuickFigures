/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package graphicTools;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.ImageWrapper;
import genericMontageKit.OverlayObjectManager;
import graphicalObjects_BasicShapes.RectangularGraphic;
import undo.CombinedEdit;
import undo.UndoMoveItems;
import undo.UndoSnappingChange;
import undo.UndoTakeLockedItem;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.AttachmentPosition;
import utilityClassesForObjects.TakesLockedItems;

public class LockedObjectSwapper extends LockGraphicTool2 {

	
	public LockedObjectSwapper() {
		super(false);
		// TODO Auto-generated constructor stub
	}

	{createIconSet("icons2/lockGraphic3.jpg","icons2/RectangleIconPress.jpg","icons2/lockGraphic3Rollover.jpg");
	}
	
	LocatedObject2D object2=null;;
	
	private TakesLockedItems lockTaker2=null;
	
	public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
		super.onPress(gmp, roi2);
		setMarkerRoi();
	}
	
	
	public void setMarkerRoi() {
		
		
				OverlayObjectManager select = this.getImageWrapperClick().getOverlaySelectionManagger();
				
				select.setSelection(MarkerRoi(), 0);
				
			
		
	} 
	
	
	
	public LocatedObject2D MarkerRoi() {
		if (getSelectedObject()==null) return null;
		return RectangularGraphic.blankRect(this.getSelectedObject().getBounds(), Color.blue, true, true);
	}
	
	
public void mouseDragged() {
		
	object2 = getObject(getImageWrapperClick(), getDragCordinateX(), getDragCordinateY());
	if (object2==null) return;
	lockTaker2=findLockContainer(object2);
	OverlayObjectManager select = this.getImageWrapperClick().getOverlaySelectionManagger();
	
	select.setSelection(RectangularGraphic.blankRect(object2.getBounds(), Color.green, true, true),1);
	
		
	}




public void onRelease(ImageWrapper gmp, LocatedObject2D roi2) {
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
	ArrayList<LocatedObject2D> allRoi = getPotentialLockAcceptors(getImageWrapperClick());
	TakesLockedItems t=(TakesLockedItems) lockTaker;
	TakesLockedItems t2=(TakesLockedItems) lockTaker2;
	
			removeFromAlltakers(object2, allRoi, undoer);
			removeFromAlltakers(inside, allRoi, undoer);
			removeFromAlltakers(inside, allRoi, undoer);
			
			UndoSnappingChange undoS1 = new  UndoSnappingChange(inside);
			UndoSnappingChange undoS2 = new  UndoSnappingChange(object2);
			
			AttachmentPosition snap1 = inside.getAttachmentPosition();
			AttachmentPosition snap2 = object2.getAttachmentPosition();
			inside.setAttachmentPosition(snap2);
			object2.setAttachmentPosition(snap1);
			
			undoS1.establishFinalState();
			undoS2.establishFinalState();
			
		
			
			
			 t.removeLockedItem(inside);
			 t2.removeLockedItem(object2);
			 UndoTakeLockedItem undo1 = new UndoTakeLockedItem(t, inside, true);
			 UndoTakeLockedItem undo2 = new UndoTakeLockedItem(t2, object2, true);
			 
			 
			t.addLockedItem(object2);
			t2.addLockedItem(inside);
			 UndoTakeLockedItem undo3 = new UndoTakeLockedItem(t2, inside, false);
			 UndoTakeLockedItem undo4 = new UndoTakeLockedItem(t, object2, false);
			 
			CombinedEdit undoFinal = new CombinedEdit(undoS1,undoS2, undo1, undo2, undo3, undo4);
			this.getImageDisplayWrapperClick().getUndoManager().addEdit(undoFinal);
}

@Override
public String getToolTip() {
		
		return "Switch Locations of Objects";
	}

public void removeMarkerRoi()  {
	
	getImageWrapperClick().getOverlaySelectionManagger().removeSelections();
	
}

@Override
public String getToolName() {
		return "Swaps Locations of 2 Attached Items";
	}
}
