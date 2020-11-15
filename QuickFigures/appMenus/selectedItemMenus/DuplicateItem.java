package selectedItemMenus;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import undo.CombinedEdit;
import undo.UndoAddItem;
import utilityClassesForObjects.LocatedObject2D;

public class DuplicateItem extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Duplicate";
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
		CombinedEdit undo = new CombinedEdit();
		//new BasicOverlayHandler().copyRois(input)
	
		for(ZoomableGraphic i: this.array) {
			
			if (i==null) continue;
			if (i instanceof LocatedObject2D) {
				LocatedObject2D h=(LocatedObject2D) i;
				GraphicLayer layerforadd = i.getParentLayer();
				
				ZoomableGraphic copy1 = (ZoomableGraphic) h.copy();
				undo.addEditToList(new UndoAddItem(layerforadd, copy1));
				layerforadd.add(copy1);
			}
		}
		
		this.getSelector().getGraphicDisplayContainer().getUndoManager().addEdit(undo);

	}

}
