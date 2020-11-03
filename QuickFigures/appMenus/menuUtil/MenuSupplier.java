package menuUtil;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public interface MenuSupplier extends PopupMenuSupplier{
	JPopupMenu getJPopup();
	public ArrayList<JMenuItem> findJItems();
	JMenu getJMenu();
}
