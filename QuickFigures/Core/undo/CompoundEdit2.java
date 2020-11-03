package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

import logging.IssueLog;


public class CompoundEdit2 extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<UndoableEdit> editlist=new 	ArrayList<UndoableEdit> ();
	
	
	public CompoundEdit2(AbstractUndoableEdit... edits) {
		for(AbstractUndoableEdit undo: edits)
			if (undo!=null)this.addEditToList(undo);
	}
	
	public void establishFinalState() {
		for(UndoableEdit undo1: editlist) {
			if (undo1 instanceof AbstractUndoableEdit2) {
				((AbstractUndoableEdit2) undo1).establishFinalState();
			}
		}
	}
	
	public void undo() {
		/**goes in reverse order to undo the items*/
		for(int i=nEdits()-1; i>-1;i--) {
			editlist.get(i).undo();
			}
		super.selectTree();
		
	}
	public void redo() {
		for(int i=0; i<nEdits();i++) {
			editlist.get(i).redo();
		}
		super.selectTree();
		
	}
	
	
	
	
	/**adds an edit. They will be undone, starting from the last one added*/
	public boolean addEditToList(UndoableEdit edit) {
		if (edit==null) return false;
		editlist.add(edit);
		if (edit instanceof AbstractUndoableEdit2)this. actedOnObjects.addAll(((AbstractUndoableEdit2)edit).actedOnObjects);
		
		return true;
	}
	
	public boolean empty() {return nEdits()==0;}

	public int nEdits() {
		return editlist.size();
	}


}
