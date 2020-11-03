package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import animations.HasAnimation;
import animations.Animation;
import animations.Animator;
import applicationAdapters.DisplayedImageWrapper;
import includedToolbars.StatusPanel;
import logging.IssueLog;

public class UndoManagerPlus extends UndoManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void undo() {
		StatusPanel.updateStatus("Undoing "+ editToBeUndone().getClass().getName());
		
		super.undo();
	}
	
	 public synchronized boolean addEdit(UndoableEdit anEdit) {
		 if(anEdit==null) return false;
		 return super.addEdit(anEdit);
	 }
	
	public boolean hasUndo(Object o) {
		return edits.contains(o);
	}
	
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
		
		CompoundEdit2 c = new CompoundEdit2();
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
		
		CompoundEdit2 c = new CompoundEdit2();
		c.addEditToList(newedit);
		c.addEditToList(oldedit);
		edits.set(edits.indexOf(oldedit), c);
		
		return c;
	}
	
	/**Combines the last few edits*/
	public void mergeLastNEdits(int i) {
		if (edits.size()<i || i<2) return;
		
		ArrayList<UndoableEdit> editsToBeFused=new ArrayList<UndoableEdit>();
		
		for(int j=edits.size()-i; j<edits.size(); j++ ) {
			editsToBeFused.add(edits.get(j));
		}
		
		this.trimEdits(edits.size()-i, edits.size()-1);
		
		CompoundEdit2 c = new CompoundEdit2();
		for(UndoableEdit edit: editsToBeFused) {
			c.addEditToList(edit);
			
		}
		
		this.addEdit(c);
		
	}
	
	public void addEdits(AbstractUndoableEdit... edits) {
		if(edits.length==1)this.addEdit(edits[0]);
		else addEdit(new CompoundEdit2(edits));
		
	}
	
	/**undoes the action and returns an animation for the redo*/
	public Animation doAnimatedUndo(DisplayedImageWrapper diw) {
		UndoableEdit todo = this.editToBeUndone();
		if(todo instanceof HasAnimation) {
			IssueLog.log("This undo can be animated");
			
			HasAnimation ani=(HasAnimation) todo;
			this.undo();
			Animator animator = new Animator(diw);
			animator.addAnimation(ani.getAnimation());
			try {
		animator.animate();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
			
		} else this.undo();
		return null;
	}



	
	

}
