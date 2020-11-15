package undo;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

public class UndoRemoveManyItem extends  CombinedEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	public UndoAddManyItem(GraphicLayer gl, ArrayList<ZoomableGraphic> xxs) {
		for(ZoomableGraphic z: xxs) {
			this.addEditToList(new UndoAddItem(gl, z));
		}
	}*/
	
	public UndoRemoveManyItem(GraphicLayer gl, ArrayList<?> xxs) {
		for(Object z: xxs) {
			if (z instanceof ZoomableGraphic) this.addEditToList(new UndoAbleEditForRemoveItem(gl, (ZoomableGraphic) z));
		}
	}

}
