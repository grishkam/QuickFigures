package undo;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.undo.AbstractUndoableEdit;

import layersGUI.GraphicSetDisplayTree;
import utilityClassesForObjects.Hideable;

public class UndoHideUnhide extends AbstractUndoableEdit {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Hideable, Boolean> map=new HashMap<Hideable, Boolean>();//the original condition
	private boolean hide;
	private GraphicSetDisplayTree tree;
	
	public UndoHideUnhide(Hideable h, boolean hide) {
		map.put(h, h.isHidden());
	}
	
	public UndoHideUnhide(ArrayList<?> objects, boolean hide) {
		this.hide=hide;
		for(Object o: objects) {
			if(o instanceof Hideable) {
				Hideable h=(Hideable) o;
				map.put(h, h.isHidden());
			}
		}
	}
	
	public void undo() {
		for(Hideable k:map.keySet()) {
			k.setHidden(map.get(k).booleanValue());
		}
		if (tree!=null) tree.repaint();
	}
	
	public void redo() {
		for(Hideable k:map.keySet()) {
			k.setHidden(this.hide);
		}
		if (tree!=null) tree.repaint();
	}

	public void setTree(GraphicSetDisplayTree tree) {
		this.tree=tree;
		
	}

}
