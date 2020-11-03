package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.GraphicSetDisplayTree;

/**Implements undo for an operation that reorders items in a layer*/
public class UndoReorder extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicSetDisplayTree tree;
	ArrayList<ZoomableGraphic> oldOrder ;
	GraphicLayer layer;
	private ArrayList<ZoomableGraphic> newOrder;
	
	
	public UndoReorder(GraphicLayer gl) {
		layer = gl;
		 oldOrder = new ArrayList<ZoomableGraphic>();
		 oldOrder.addAll(gl.getItemArray());
	}
	
	public void saveNewOrder() {
		 newOrder = new ArrayList<ZoomableGraphic>();
		 newOrder.addAll(layer.getItemArray());
	}
	
	@Override
	public void undo() {
		layer.setOrder(oldOrder);
		if (tree!=null) tree.repaint();
	}
	
	@Override
	public void redo() {
		if (newOrder!=null) {
			layer.setOrder(newOrder);
		}
		if (tree!=null) tree.repaint();
	}

	public void setTree(GraphicSetDisplayTree tree) {
		this.tree=tree;
		
	}
	
	public boolean canRedo() {
		return true;
	}

}
