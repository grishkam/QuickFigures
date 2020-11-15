package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

import logging.IssueLog;

/**Since edit that contains a sequence of undoable edits*/
public class CombinedEdit extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<UndoableEdit> editlist=new 	ArrayList<UndoableEdit> ();
	ArrayList<EditListener> afterEdits=new 	ArrayList<EditListener> ();
	
	public CombinedEdit(AbstractUndoableEdit... edits) {
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
		afterEditDone();
		super.selectTree();
		
	}
	public void redo() {
		for(int i=0; i<nEdits();i++) {
			editlist.get(i).redo();
		}
		afterEditDone();
		super.selectTree();
		
	}
	
	public void afterEditDone() {
		for(int i=afterEdits.size()-1; i>-1;i--) try {
			afterEdits.get(i).afterEdit();
			} catch (Throwable t) {IssueLog.log(t);}
	}
	
	public void addEditListener(EditListener el) {
		afterEdits.add(el);
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
