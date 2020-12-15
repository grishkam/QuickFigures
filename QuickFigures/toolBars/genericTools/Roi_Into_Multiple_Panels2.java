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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;



public class Roi_Into_Multiple_Panels2 extends Object_Mover {
	
	{createIconSet("icons/RoiShifterIcon.jpg","icons/RoiShifterIconPressed.jpg","icons/RoiShifterRolloverIcon.jpg");

	}
	@Override
	public void mouseDragged() {
		if (super.altKeyDown()) {
			super.mouseDragged(); return;
		}
		
		if (getPrimarySelectedObject()!=null) {
		PanelLayoutGraphic layout = getClickContainingLayout();
		if (layout==null) return;
		if (layout==this.getPrimarySelectedObject()) return;
		Rectangle2D l1 = layout.getPanelLayout().getNearestPanel(this.getPrimarySelectedObject().getBounds().getCenterX(), getPrimarySelectedObject().getBounds().getCenterY());
		Rectangle2D l2 = layout.getPanelLayout().getNearestPanel(getDragCordinateX(), getDragCordinateY());
			if (l1.equals(l2)) return;
			this.getPrimarySelectedObject().moveLocation(l2.getX()-l1.getX(), l2.getY()-l1.getY());
		}
		
		if (currentUndo!=null) {
			 currentUndo.establishFinalLocations();
			 if (!this.addedToManager)
				 {this.getImageDisplayWrapperClick().getUndoManager().addEdit(currentUndo);
				 this.addedToManager=true;
				 }
	}
		
	}
	
	
	public PanelLayoutGraphic getClickContainingLayout() {
		ArrayList<LocatedObject2D> layouts = this.getObjecthandler().getAllClickedRoi(getImageClicked(), getClickedCordinateX(), getClickedCordinateY(), PanelLayoutGraphic.class);
		if (this.ignorehidden) ArraySorter.removehideableItems(layouts);
		if (layouts.size()>0) return (PanelLayoutGraphic) new ArraySorter<LocatedObject2D>().getFirstNonNull(layouts);
		return null;
	}
	
	@Override
	public String getToolTip() {
			
			return "Move Objects Between Panels";
		}
	
	
	@Override
	public String getToolName() {
			
			return "Shift Object Between Panels";
		}

	
	
	
}
