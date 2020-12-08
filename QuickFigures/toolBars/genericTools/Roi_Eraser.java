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
package genericTools;

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
