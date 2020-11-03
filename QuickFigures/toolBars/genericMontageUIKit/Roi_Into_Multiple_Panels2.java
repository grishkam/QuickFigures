package genericMontageUIKit;

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
		
		if (getSelectedObject()!=null) {
		PanelLayoutGraphic layout = getClickContainingLayout();
		if (layout==null) return;
		if (layout==this.getSelectedObject()) return;
		Rectangle2D l1 = layout.getPanelLayout().getNearestPanel(this.getSelectedObject().getBounds().getCenterX(), getSelectedObject().getBounds().getCenterY());
		Rectangle2D l2 = layout.getPanelLayout().getNearestPanel(getDragCordinateX(), getDragCordinateY());
			if (l1.equals(l2)) return;
			this.getSelectedObject().moveLocation(l2.getX()-l1.getX(), l2.getY()-l1.getY());
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
		ArrayList<LocatedObject2D> layouts = this.getObjecthandler().getAllClickedRoi(getImageWrapperClick(), getClickedCordinateX(), getClickedCordinateY(), PanelLayoutGraphic.class);
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
