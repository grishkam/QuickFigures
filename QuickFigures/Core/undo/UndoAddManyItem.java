package undo;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

public class UndoAddManyItem extends  CompoundEdit2 {

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
	
	public UndoAddManyItem(GraphicLayer gl, ArrayList<?> xxs) {
		for(Object z: xxs) {
			if (z instanceof ZoomableGraphic) this.addEditToList(new UndoAddItem(gl, (ZoomableGraphic) z));
		}
	}

}