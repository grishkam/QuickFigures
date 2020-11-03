package undo;


import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.GraphicSetDisplayTree;

/**undo for any changes to the direct contents of a layer*/
public class UndoLayerContentChange extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayer layer;
	private ArrayList<ZoomableGraphic> iItems;
	private GraphicSetDisplayTree tree;
	private ArrayList<ZoomableGraphic> fItems;
	
	public UndoLayerContentChange(GraphicLayer layer) {
		this.layer=layer;
		iItems=currentItems(layer);
		
	}

	protected ArrayList<ZoomableGraphic> currentItems(GraphicLayer layer) {
		ArrayList<ZoomableGraphic> i = new ArrayList<ZoomableGraphic>(); 
		i.addAll(layer.getItemArray());
		return i;
	}
	
	public void establishFinalState() {
		fItems=currentItems(layer);
	}

	public void undo() {
		for(ZoomableGraphic i:currentItems(layer)) {layer.remove(i);}
		for(ZoomableGraphic i:iItems) {layer.addItemToLayer(i);;}
		
	}
	public void redo() {
		for(ZoomableGraphic i:currentItems(layer)) {layer.remove(i);}
		for(ZoomableGraphic i:fItems) {layer.addItemToLayer(i);;}
	}
	
	

}
