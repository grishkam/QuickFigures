package addObjectMenus;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import selectedItemMenus.CopyItem;
import undo.UndoAddManyItem;
import utilityClassesForObjects.LocatedObject2D;

import java.util.ArrayList;

public class PasteItem extends BasicGraphicAdder{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GraphicLayer layer;
	

	
	
	
	

	@Override
	public ZoomableGraphic add(GraphicLayer layer) {
		if ( CopyItem.thearray==null) return null;
	
		
		ArrayList<ZoomableGraphic> copiedArray = new ArrayList<ZoomableGraphic> ();
		
		for(ZoomableGraphic s: CopyItem.thearray) {
			if (s instanceof LocatedObject2D) {
				LocatedObject2D l=(LocatedObject2D) s;
				l=l.copy();
				copiedArray.add((ZoomableGraphic) l);
				if (layer!=null ) layer.add((ZoomableGraphic) l);
			}
		}
		
		UndoAddManyItem undo = new UndoAddManyItem(layer,copiedArray );
		selector.getGraphicDisplayContainer().getUndoManager().addEdit(undo);
		
		return null;
	}

	






	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "Paste";
	}







	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return getCommand();
	}

}
	//

