package standardDialog;

import java.awt.ItemSelectable;

public interface SpecialChoicePanel extends OnGridLayout, ItemSelectable {

	public int getSelectedItemIndex() ;
	public Object getSelectedObject() ;
	
}
