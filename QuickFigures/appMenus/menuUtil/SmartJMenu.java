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
 * Version: 2021.1
 */
package menuUtil;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.undo.AbstractUndoableEdit;
import applicationAdapters.CanvasMouseEvent;
import selectedItemMenus.MenuItemInstall;
import undo.UndoManagerPlus;


/** A special JMenu with a lot of useful methods for building a menu structure
 * containing @see SmartMenuItem items
 * normal JMenus often would not vanish when i clicked outside of them this 
 * class includes a fix for that.*/
public class SmartJMenu extends JMenu implements SmartMenuItem {

	/**
	 * the string the separates items in a menu path
	 */
	public static final String MENU_PATH_REGEX = "<";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private transient UndoManagerPlus undoManager;
	protected transient CanvasMouseEvent mouseE;
	
	public SmartJMenu(String string) {
		super(string);
	}
	
	public SmartJMenu(String string, Icon icon) {
		super(string);
		this.setIcon(icon);
	}
	
	/**shows the popup menu or hides it*/
	public void setPopupMenuVisible(boolean b) {
		super.setPopupMenuVisible(b);
		if (b==true) {new PopupCloser(this);new PopupCloser(this.getPopupMenu());}
	}
	
	/**finds the submenu with the given name*/
	public static JMenu getSubmenuOfJMenu(JMenu j, String submenu) {
		int i2=j.getItemCount();
		
		for(int i=0; i<i2; i++) {
			JMenuItem arr = j.getItem(i);
		
			if (arr instanceof JMenu){
				JMenu j2=(JMenu) arr;
				if (j2.getText().equals(submenu)) return j2;
			} 
			
		}
		return null;
	}
	
	/**finds the submenu with the given name*/
	public static JMenu getSubmenu(JMenuBar j, String submenu) {
		MenuElement[] array = j.getSubElements();
		for(int i=0; i<array.length; i++) {
			if (array[i] instanceof JMenu){
				
				JMenu j2=(JMenu) array[i];
				
				if (j2.getText().equals(submenu)) return j2;
			} 
			
		}
		return null;
	}
	
	
	/**returns a submenu with the given name*/
	public static JMenu getOrCreateSubmenu(JMenu men3, String command) {
			JMenu men2 = SmartJMenu.getSubmenuOfJMenu(men3, command);
		 if (men2==null) {
					men2=new SmartJMenu(command);
					men3.add(men2);
				};
				
				return men2;
	}


	public UndoManagerPlus getUndoManager() {
		return undoManager;
	}

	/**Sets an undo to the undo manager*/
	public void setUndoManager(UndoManagerPlus undoManager) {
		this.undoManager = undoManager;
		for(Component e : this.getMenuComponents()) {
			if (e instanceof SmartMenuItem) {
				((SmartMenuItem) e).setUndoManager(getUndoManager());
			}
		}
	}
	
	/**Adds an undo to the undo manager*/
	public void addUndo(AbstractUndoableEdit edit) {
		if (this.getUndoManager()!=null) this.getUndoManager().addEdit(edit);
	}
	
	/**When given an instance of a menu items installation instructions and a menu
	  returns the appropriate submenu for the new menu item*/
	public static JMenu getOrCreateSubmenuFromPath(MenuItemInstall o, JMenu men) {
		if(o==null||o.getMenuPath()==null) return men;//if there is no submenu
		String[] path = o.getMenuPath().split(MENU_PATH_REGEX);
		return getSubmenuFromPath(men, path);
	}


	/**When given a JMenu with series of strings describing a menu path, returns the submenu for that path
	  will create a new menu if it does not already exist*/
	public static JMenu getSubmenuFromPath(JMenu men, String[] path) {
		return getOrCreateSubmenuFromPath(men, path, 0);
	}
	
	/**When given a JMenu with series of strings describing a menu path, returns the submenu for that path
	  will create a new menu if it does not already exist. does not consider any strings that are before the start index*/
	public static JMenu getOrCreateSubmenuFromPath(JMenu men, String[] path, int start) {
		for(int i=start; i<path.length; i++)men= getOrCreateSubmenu(men, path[i]);
		return men;
	}

	/**Sets which mouse event triggered the popup menu*/
	public void setLastMouseEvent(CanvasMouseEvent e) {
		this.mouseE=e;
		
		for(Component e2 : this.getMenuComponents()) {
			if (e2 instanceof SmartMenuItem) {
				((SmartMenuItem) e2).setLastMouseEvent(e);
			}
		}
	}

}
