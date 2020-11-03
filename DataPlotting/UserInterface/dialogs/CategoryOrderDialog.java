package dialogs;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.HashMap;

import panelGUI.OrderSelectionJList;
import standardDialog.StandardDialog;

public class CategoryOrderDialog extends StandardDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	OrderSelectionJList<String> usedList;
	
	public CategoryOrderDialog(HashMap<Double, String> locationMap, Iterable<String> otherItems) {
		super("Change order", true);
		ArrayList<String> items=new ArrayList<String>();
		
		for(Double d: locationMap.keySet()) {
			items.add(locationMap.get(d));
		}
		
		
		usedList=new OrderSelectionJList<String>(items, null, otherItems);
		this.getMainPanel().add(usedList, this.getCurrentConstraints());
		GridBagConstraints cons2 = getCurrentConstraints();
		cons2.gridy=8;
		getMainPanel().add(usedList.createButtonPanel(), cons2);
	}
	
	public ArrayList<String> getNewOrder() {
		return usedList.getNewOrder();
	}
	
	public ArrayList<String> getRemovedItems() {
		return usedList.getRemovedItems();
	}
	
	public ArrayList<String> getNewlyAddedItems() {
		return usedList.getNewlyAddedItems();
	}
	
}
