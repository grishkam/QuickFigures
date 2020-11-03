package undo;

import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.GraphicSetDisplayTree;

public class UndoAbleEditForRemoveItem extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayer layer;
	private ZoomableGraphic added;
	private int index;

	
	public UndoAbleEditForRemoveItem(GraphicLayer layer, ZoomableGraphic added) {
		
		this.added=added;
		if ((layer==null||!layer.getItemArray().contains(added))&&added!=null) {
			KnowsParentLayer kpl=(KnowsParentLayer) added;
			this.layer=kpl.getParentLayer();
		} else
		this.layer=layer;
		this.actedOnObjects.add(added);
		if(this.layer!=null)
		this.index=this.layer.getItemArray().indexOf(added);//finds the stack index
		
	}
	
	
	
	public UndoAbleEditForRemoveItem(GraphicLayer origin, ZoomableGraphic item, GraphicSetDisplayTree tree) {
		this(origin, item);
		this.tree=tree;
	}

	public void redo() {
		if (layer!=null)layer.remove(added);
	}
	
	public void undo() {
		if (layer!=null) 
		{
		
			layer.add(added);
			if (index<0||index>=layer.getItemArray().size()) return;
			/**moves item to its original stack index*/
			ZoomableGraphic atindex = layer.getItemArray().get(index);
			if (added!=null||atindex!=null)
				layer.swapmoveObjectPositionsInArray(added, atindex);
			
			if (tree!=null) {tree.addUserObjectToSelection(added);}
			if (tree!=null) {tree.expandPathForUserObject(layer);}
		}
	}

	


}
