package menuUtil;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.undo.AbstractUndoableEdit;
import applicationAdapters.CanvasMouseEventWrapper;
import selectedItemMenus.MultiSelectionOperator;
import undo.UndoManagerPlus;


/**normal JMenus often would not vanish when i clicked outside of them. this 
 * class is a fix for that. it also has other useful methods*/
public class SmartJMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SmartJMenu(String string) {
		super(string);
	}
	PopupCloser closer;
	private transient UndoManagerPlus undoManager;
	protected transient CanvasMouseEventWrapper mouseE;
	
	
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


	public void setUndoManager(UndoManagerPlus undoManager) {
		this.undoManager = undoManager;
		for(Component e : this.getMenuComponents()) {
			if (e instanceof SmartJMenu) {
				((SmartJMenu) e).setUndoManager(getUndoManager());
			}
		}
	}
	
	public void addUndo(AbstractUndoableEdit edit) {
		if (this.getUndoManager()!=null) this.getUndoManager().addEdit(edit);
	}
	
	public static JMenu getOrCreateSubmenuFromPath(MultiSelectionOperator o, JMenu men) {
		String[] path = o.getMenuPath().split("<");
		return getSubmenuFromPath(men, path);
	}


	public static JMenu getSubmenuFromPath(JMenu men, String[] path) {
		return getSubmenuFromPath(men, path, 0);
	}
	
	public static JMenu getSubmenuFromPath(JMenu men, String[] path, int start) {
		for(int i=start; i<path.length; i++)men= getOrCreateSubmenu(men, path[i]);
		return men;
	}


	public void setLastMouseEvent(CanvasMouseEventWrapper e) {
		this.mouseE=e;
		
		for(Component e2 : this.getMenuComponents()) {
			if (e2 instanceof SmartJMenu) {
				((SmartJMenu) e2).setLastMouseEvent(e);
			}
		}
	}

}
