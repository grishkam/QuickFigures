package selectedItemMenus;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.GraphicTreeUI;
import undo.UndoHideUnhide;
import utilityClassesForObjects.Hideable;

public class HideItem extends BasicMultiSelectionOperator {

	@Override
	public String getMenuCommand() {
		return "Hide";
	}
	
	@Override
	public void setSelection(ArrayList<ZoomableGraphic> a) {
		array=new ArrayList<ZoomableGraphic>();
		for(ZoomableGraphic z: a) {
			if (z instanceof GraphicLayer) {
				GraphicLayer l=(GraphicLayer) z;
				array.addAll(l.getAllGraphics());
			} else array.add(z);
		}
	}

	@Override
	public void run() {
		UndoHideUnhide undo = new UndoHideUnhide(this.array, false);
		for(ZoomableGraphic i: this.array) {
			if (i instanceof Hideable) {
				((Hideable) i).setHidden(true);
			}
		}
		this.getSelector().getGraphicDisplayContainer().getUndoManager().addEdit(undo);
	}
	
	public boolean isValidForLayerSelector(LayerSelector graphicTreeUI) {
		if (graphicTreeUI instanceof GraphicTreeUI)
		return true;
		return false;
		}

}
