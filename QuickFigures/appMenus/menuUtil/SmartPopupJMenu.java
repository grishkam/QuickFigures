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
 * Date Modified: Jan 6, 2021
 * Version: 2022.1
 */
package menuUtil;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.undo.AbstractUndoableEdit;

import applicationAdapters.CanvasMouseEvent;
import logging.IssueLog;
import undo.UndoManagerPlus;

/**A popup menu that storesinformation regarding an undo manager and a mouse event
 * @see SmartJMenu
 * @see SmartMenuItem for the items that will be part of this menu*/
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
		setUndoManager(e.getUndoManager());
		MenuElement[] subElements = this.getSubElements();
		setMouseEventForAll(e, subElements);
		
	}
	
	
	/**
	Sets the mouse event for each of the SmartMenuItems
	 */
		public static void setMouseEventForAll(CanvasMouseEvent e, MenuElement[] subElements) {
			for(Object e2 : subElements) {
				if (e2 instanceof SmartMenuItem) {
					SmartMenuItem smartMenuItem = (SmartMenuItem) e2;
					smartMenuItem.setLastMouseEvent(e);
					smartMenuItem.setUndoManager(e.getUndoManager());
				}
				
			}
		}

	/**
	Sets the mouse event for each of the SmartMenuItems
	 */
	public void setMouseEventForAll(CanvasMouseEvent e, Iterable<?> subElements) {
		for(Object e2 : subElements) {
			if (e2 instanceof SmartMenuItem) {
				SmartMenuItem smartMenuItem = (SmartMenuItem) e2;
				smartMenuItem.setLastMouseEvent(e);
				smartMenuItem.setUndoManager(e.getUndoManager());
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
	
	/**shows the popup menu for the given mouse event*/
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
	
	public CanvasMouseEvent getMemoryOfMouseEvent() {return mEvent;};

	
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

/**returns a j menu with all the same menu items*/
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
	for(JMenuItem j:i) {
		if(j!=null)
			add(j);
		}
}

public void addUndo(AbstractUndoableEdit e) {
	if (undoManager!=null) {
		undoManager.addEdits(e);
	} else {
		IssueLog.log("failed to add undo "+e);
	}
}

}
