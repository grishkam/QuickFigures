package selectedItemMenus;

import graphicalObjects.ZoomableGraphic;
import logging.IssueLog;

public class ListItems extends BasicMultiSelectionOperator {


	@Override
	public void run() {
		for(ZoomableGraphic item: array) {
			if (item==null) continue;
			actioinOnSelected(item);
		}
		
	}
	
	@Override
	public String getMenuCommand() {
		return "List Selected Items";
	}
	
	/**Prints out the item selected. Used for debuging*/
	public void actioinOnSelected(ZoomableGraphic selectedItem) {
		IssueLog.log(selectedItem.toString());
		
	}
	
	@Override
	public String getMenuPath() {
		return "Item";
	}
	


}
