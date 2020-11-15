package selectedItemMenus;

import javax.swing.Icon;

import objectDialogs.DialogIcon;
import objectDialogs.ShapeGraphicOptionsSwingDialog;

public class ShapeOptionsSyncer extends BasicMultiSelectionOperator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Set Shape Options";
	}
	

	@Override
	public void run() {
		ShapeGraphicOptionsSwingDialog mt = new ShapeGraphicOptionsSwingDialog(getAllArray(), false);
		if (mt.hasItems())
			mt.showDialog();
	}
	
	public Icon getIcon() {
		return DialogIcon.getIcon();
	}

}
