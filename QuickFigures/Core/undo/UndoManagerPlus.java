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
 * Date Modified: Jan 5, 2021
 * Version: 2023.2
 */
package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import includedToolbars.StatusPanel;

/**A specialized undo manager for QuickFigures*/
public class UndoManagerPlus extends UndoManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void undo() {
		if ( editToBeUndone()!=null)
		StatusPanel.updateStatus("Undoing "+ editToBeUndone().getClass().getName());
		
		super.undo();
	}
	
	/**does not add null edits*/
	 public synchronized boolean addEdit(UndoableEdit anEdit) {
		 if(anEdit==null) return false;
		 return super.addEdit(anEdit);
	 }
	
	 /**returns true if the list of edits includes the object*/
	public boolean hasUndo(Object o) {
		return edits.contains(o);
	}
	
	 /**returns an edit at index i*/
	public UndoableEdit getEditFromList(int i) {
		return edits.get(i);
	}
	
	public UndoableEdit getLastEditFromList() {
		if (edits.size()==0) return null;
		return edits.get(edits.size()-1);
	}
	
	/**combines this edit with the last one on the list*/
	public void mergeInedit(AbstractUndoableEdit edit) {
		if (edits.size()==0) {
			this.addEdit(edit);
			return;
		}
		
		CombinedEdit c = new CombinedEdit();
		c.addEditToList(edits.get(edits.size()-1));
		c.addEditToList(edit);
		edits.set(edits.size()-1, c);
	}
	
	/**combines this edit with the last one on the list
	 * @return */
	public AbstractUndoableEdit mergeInedit(AbstractUndoableEdit newedit, AbstractUndoableEdit oldedit) {
		if (edits.size()==0 ||edits.indexOf(oldedit)<0) {
			this.addEdit(newedit);
			return newedit;
		}
		
		CombinedEdit c = new CombinedEdit();
		c.addEditToList(newedit);
		c.addEditToList(oldedit);
		edits.set(edits.indexOf(oldedit), c);
		
		return c;
	}
	
	/**Combines the last few edits into one*/
	public void mergeLastNEdits(int i) {
		if (edits.size()<i || i<2) return;
		
		ArrayList<UndoableEdit> editsToBeFused=new ArrayList<UndoableEdit>();
		
		for(int j=edits.size()-i; j<edits.size(); j++ ) {
			editsToBeFused.add(edits.get(j));
		}
		
		this.trimEdits(edits.size()-i, edits.size()-1);
		
		CombinedEdit c = new CombinedEdit();
		for(UndoableEdit edit: editsToBeFused) {
			c.addEditToList(edit);
			
		}
		
		this.addEdit(c);
		
	}
	
	/**Combines many edits into one. Adds many edits at one time*/
	public void addEdits(AbstractUndoableEdit... edits) {
		if(edits.length==1)this.addEdit(edits[0]);
		else addEdit(new CombinedEdit(edits));
		
	}
	




	
	

}
