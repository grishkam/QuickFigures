package selectedItemMenus;

import javax.swing.Icon;

import objectDialogs.DialogIcon;
import objectDialogs.MultiBarDialog;

public class BarOptionsSyncer extends BasicMultiSelectionOperator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Show Scale Bar Options";
	}

	@Override
	public void run() {
		MultiBarDialog mt = new MultiBarDialog(array);
		mt.showDialog();

	}
	
	public Icon getIcon() {
		return DialogIcon.getIcon();
	}

}

