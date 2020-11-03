package genericMontageUIKit;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import menuUtil.SmartPopupJMenu;
import selectedItemMenus.LayerSelector;
import selectedItemMenus.SelectionOperationsMenu;

public class GroupOfobjectPopup extends SmartPopupJMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LayerSelector groupd=null;
	private SelectionOperationsMenu allitem;

	public GroupOfobjectPopup(LayerSelector allSelectedRois) {
		groupd=allSelectedRois;
		allitem= SelectionOperationsMenu.getStandardMenu(groupd);
		allitem.setText("All Selected Items");
		this.add(allitem);
		}
	public  SelectionOperationsMenu getAllItemMenu() {
		allitem.setText("All clicked Items");
		return allitem;
	}
	
	public void addItemsFromJMenu(JPopupMenu j, String name) {
		if (j==null) return;
		MenuElement[] elis = j.getSubElements();
		JMenu j2 = new JMenu(name);
		for(MenuElement item: elis) {
			if (item instanceof JMenuItem) {
				j2.add((JMenuItem) item);
			}
			this.add(j2);
		};
	}


	

}
