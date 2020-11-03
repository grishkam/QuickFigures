package selectedItemMenus;

import javax.swing.Icon;

import graphicalObjects_BasicShapes.BarGraphic;
import objectDialogs.DialogIcon;
import objectDialogs.MultiBarDialog;

public class BarOptionsSyncer extends BasicMultiSelectionOperator {
	
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

