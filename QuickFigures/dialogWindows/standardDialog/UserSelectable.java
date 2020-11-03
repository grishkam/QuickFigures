package standardDialog;

import java.awt.ItemSelectable;

public interface UserSelectable extends ItemSelectable{

	public int getSelectionNumber();
	public Object getSelectedItem();
	
	public void setSelectionNumber(int index);
	//Dimension getPreferedSize();
}
