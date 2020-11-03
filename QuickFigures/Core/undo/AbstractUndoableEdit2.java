package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects.ZoomableGraphic;
import layersGUI.GraphicSetDisplayTree;

public class AbstractUndoableEdit2 extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//private int index;
	protected GraphicSetDisplayTree tree;
	
	ArrayList<ZoomableGraphic> actedOnObjects=new ArrayList<ZoomableGraphic> ();
	
  public boolean isMyObject(Object o) {
	  return actedOnObjects.contains(o);
  }
	
	public void establishFinalState() {}
	public void redo() {
	
	}
	
	public void undo() {
		
	}
	
	public boolean canUndo() {
		return true;
	}
	
	public boolean canRedo() {
		return true;
	}
	
	public void selectTree() {
		if (tree!=null)tree.addUserObjectsToSelection(actedOnObjects);
	}
	
	public void setTree(GraphicSetDisplayTree tree) {
		this.tree=tree;
		
	}
	

	


}
