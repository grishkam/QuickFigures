package fileread;

import applicationAdapters.DisplayedImageWrapper;
import basicMenusForApp.BasicMenuItemForObj;
import dataTableDialogs.SmartDataInputDialog;

public class ShowTable extends BasicMenuItemForObj {

	int type=0;
	
	public ShowTable(int t) {
		type=t;
	}
	
	
	
	@Override
	public String getNameText() {
		if (type==1) return "Open text file as Data Table";
		return "New Data Table";
	}


	
	public static void main(String[] args) {
		new ShowTable(1).performActionDisplayedImageWrapper(null);;
	}



	@Override
	public String getMenuPath() {
		return "Plots";
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
		if (type==1) {SmartDataInputDialog.showTableFromUserFile(false);}
		else
		SmartDataInputDialog.createDialog(0).showDialog();;
	}
}
