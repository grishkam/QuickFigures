package selectedItemMenus;

import sUnsortedDialogs.ScaleAboutDialog;

public class ScalingSyncer extends BasicMultiSelectionOperator {

	@Override
	public String getMenuCommand() {
		return  "Scale Objects";
	}
	
public String getMenuPath() {
		
		return "Scale";
	}

	@Override
	public void run() {
		ScaleAboutDialog aa = new ScaleAboutDialog(selector.getGraphicDisplayContainer().getUndoManager());
		aa.addItemsScalable(super.getAllArray());
		aa.showDialog();
	
		

	}

}
