/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Nov 19, 2022
 * Version: 2023.1
 */
package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

import graphicalObjects.ZoomableGraphic;
import logging.IssueLog;
import messages.ShowMessage;

/**Since edit that contains a sequence of undoable edits*/
public class CombinedEdit extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<UndoableEdit> editlist=new 	ArrayList<UndoableEdit> ();
	
	/**Called before and after each undo*/
	ArrayList<EditListener> afterEdits=new 	ArrayList<EditListener> ();
	
	String name="combined edit";
	
	public CombinedEdit(AbstractUndoableEdit... edits) {
		for(AbstractUndoableEdit undo: edits)
			if (undo!=null)this.addEditToList(undo);
	}
	
	public CombinedEdit(String name,ZoomableGraphic z, AbstractUndoableEdit... edits) {
		this(edits);
		this.name=name;
		this.actedOnObjects.add(z);
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
			} catch (Throwable t) {IssueLog.logT(t);}
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

	/**the number of edits inside of this*/
	public int nEdits() {
		return editlist.size();
	}

	public void addEditListenerMessage(String st) {
		addEditListener(new EditListener() {

			@Override
			public void afterEdit() {
				IssueLog.log(st);
				
			}});
	}

	
	public String toString() {
		return this.name+": "+super.toString();
	}
}
