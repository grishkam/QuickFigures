package menuUtil;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.undo.AbstractUndoableEdit;
import applicationAdapters.CanvasMouseEvent;
import selectedItemMenus.MenuItemInstall;
import undo.UndoManagerPlus;


/**normal JMenus often would not vanish when i clicked outside of them. this 
 * class is a fix for that. it also has other useful methods*/
public class SmartJMenu extends JMenu implements SmartMenuItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SmartJMenu(String string) {
		super(string);
	}
	PopupCloser closer;
	private transient UndoManagerPlus undoManager;
	protected transient CanvasMouseEvent mouseE;
	
	
	public void setPopupMenuVisible(boolean b) {
		
		
		super.setPopupMenuVisible(b);
		if (b==true) {new PopupCloser(this);new PopupCloser(this.getPopupMenu());
		}
	}
	
	
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
		String[] path = o.getMenuPath().split("<");
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


	public void setLastMouseEvent(CanvasMouseEvent e) {
		this.mouseE=e;
		
		for(Component e2 : this.getMenuComponents()) {
			if (e2 instanceof SmartMenuItem) {
				((SmartMenuItem) e2).setLastMouseEvent(e);
			}
		}
	}

}
