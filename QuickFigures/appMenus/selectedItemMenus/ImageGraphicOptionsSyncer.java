package selectedItemMenus;

import javax.swing.Icon;

import objectDialogs.DialogIcon;
import objectDialogs.MultiImageGraphicDialog;

public class ImageGraphicOptionsSyncer extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Set Image Options";
	}


	@Override
	public void run() {
		MultiImageGraphicDialog mt = new MultiImageGraphicDialog(array);
		if (mt.getArray().size()==0) return;
		if (mt.getArray().size()==1) {
			mt.getArray().get(0).showOptionsDialog();
			//dialog.showDialog();
			return;
		}
		mt.showDialog();

	}

	public Icon getIcon() {
		return DialogIcon.getIcon();
	}
}