package genericMontageUIKit;

import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import undo.UndoAbleEditForRemoveItem;
import utilityClassesForObjects.LocatedObject2D;


public class Roi_Eraser extends Object_Mover {
	{createIconSet("icons2/Eraser.jpg","icons2/EraserPressed.jpg","icons2/Eraser.jpg");
	 realtimeshow=true;
	}
	
	public String getToolName() {
		return "Eraser Tool";
	}

	
	void eraseSlectedObject() {
		
		LocatedObject2D o = getSelectedObject();
		
		if (o instanceof ZoomableGraphic && o instanceof KnowsParentLayer) {
			
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(null, (ZoomableGraphic)o);
			getImageWrapperClick().getImageDisplay().getUndoManager().addEdit(undo);
			
		}
		
		
		
		
		
		getImageWrapperClick().takeFromImage(o);
		
		
		if (getSelectedObject()!=null)	getSelectedObject().kill();
		
	}
	
	public void mousePressed() {
		
		eraseSlectedObject();
		
		getImageWrapperClick().updateDisplay();
	}
	
	@Override
	public void mouseDragged() {
		mousePressed();

	}
	
	public String getToolTip() {
		
		return "Delete Objects";
	}

	

	
}
