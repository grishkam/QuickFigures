/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package menuUtil;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.undo.AbstractUndoableEdit;

import applicationAdapters.CanvasMouseEvent;
import logging.IssueLog;
import undo.UndoManagerPlus;

public class SmartPopupJMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SmartPopupJMenu() {
		super();
	}
	PopupCloser closer;
	
	private UndoManagerPlus undoManager;

	private transient CanvasMouseEvent mEvent;
	
	public void setLastMouseEvent(CanvasMouseEvent e) {
		this.mEvent=e;
		for(MenuElement e2 : this.getSubElements()) {
			if (e2 instanceof SmartMenuItem) {
				((SmartMenuItem) e2).setLastMouseEvent(e);
			}
		}
		
	}
	
	public void performUndoable(AbstractUndoableEdit... edits) {
		if(undoManager!=null) undoManager.addEdits(edits);
		else if(mEvent!=null) mEvent.addUndo(edits);
		else {
			IssueLog.log("failed to add undo");
		}
	}
	
	public void showForMouseEvent(CanvasMouseEvent w) {
		this.setLastMouseEvent(w);
		this.show(w.getComponent(), w.getClickedXScreen(), w.getClickedYScreen());
	}
	
	public void showForMouseEvent(CanvasMouseEvent w, int dx, int dy) {
		this.setLastMouseEvent(w);
		this.show(w.getComponent(), w.getClickedXScreen()+dx, w.getClickedYScreen()+dy);
	}
	
	public JMenu getSubmenuOfName(String st) {
		for(MenuElement e2 : this.getSubElements()) {
			if (e2 instanceof JMenu)
				{
				JMenu jMenu = (JMenu) e2;
				
				if (jMenu.getText().equals(st)) return jMenu;
				}
			
		}
		
		
		
		SmartJMenu menuItem = new SmartJMenu(st);
		this.add(menuItem);
		return menuItem;
	}
	
	protected CanvasMouseEvent getMemoryOfMouseEvent() {return mEvent;};

	
	public void setVisible(boolean b) {
		
		
		super.setVisible(b);
		if (b==true) {new PopupCloser(this);new PopupCloser(this);
		}
	}

public UndoManagerPlus getUndoManager() {
	return undoManager;
}

public void setUndoManager(UndoManagerPlus undoManager) {
	this.undoManager = undoManager;
	for(MenuElement e : this.getSubElements()) {
		if (e instanceof SmartMenuItem) {
			((SmartMenuItem) e).setUndoManager(getUndoManager());
		}
	}
}

public JMenu extractToMenu(String label) {
	JMenu output=new SmartJMenu(label);
	MenuElement[] elements = this.getSubElements();
	this.removeAll();
	for(MenuElement e : elements) {
		if (e instanceof JMenuItem) output.add((JMenuItem) e);
	}
	return output;
}

protected void addAllMenuItems(Iterable<? extends JMenuItem> i) {
	for(JMenuItem j:i) {add(j);}
}

}
