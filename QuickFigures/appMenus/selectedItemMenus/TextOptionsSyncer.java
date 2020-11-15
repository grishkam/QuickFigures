package selectedItemMenus;

import javax.swing.Icon;

import objectDialogs.DialogIcon;
import objectDialogs.MultiTextGraphicSwingDialog;

public class TextOptionsSyncer extends BasicMultiSelectionOperator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Show Text Options";
	}

	@Override
	public void run() {
		MultiTextGraphicSwingDialog mt = new MultiTextGraphicSwingDialog(array, false);
		mt.showDialog();
	}
	
	public Icon getIcon() {
		return DialogIcon.getIcon();
	}
	
	public String getMenuPath() {
		
		return "Text";
	}

}
