package undo;


import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.GraphicSetDisplayTree;

/**undo for the addition of an item to the layer*/
public class UndoAddItem extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayer layer;
	private ZoomableGraphic added;
	private GraphicSetDisplayTree tree;
	
	public UndoAddItem(GraphicLayer layer, ZoomableGraphic added) {
		this.layer=layer;
		this.added=added;
		this.actedOnObjects.add(added);
	}
	
	public UndoAddItem(GraphicLayer destination, ZoomableGraphic item, GraphicSetDisplayTree tree) {
		this(destination, item);
		this.tree=tree;
	}

	public void undo() {
		if (layer!=null)layer.remove(added);
		
		
	}
	public void redo() {
		if (layer!=null)layer.add(added);
		if (tree!=null) {tree.addUserObjectToSelection(added);}
		if (tree!=null) {tree.expandPathForUserObject(layer);}
	}
	
	

}
