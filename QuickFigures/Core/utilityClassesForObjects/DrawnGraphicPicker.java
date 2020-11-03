package utilityClassesForObjects;

import utilityClasses1.ItemPicker;

public class DrawnGraphicPicker implements ItemPicker {

	private int type;

	public DrawnGraphicPicker(int type) {
		this.type=type;
	}
	
	@Override
	public boolean isDesirableItem(Object o) {
		if (o instanceof DrawnGraphic) {
			DrawnGraphic d=(DrawnGraphic) o;
			if (d.getTypeOfGraphic()==type) return true;
			else return false;
			
		}
		
		if (type==DrawnGraphic.Text_Item && o instanceof TextItem) return true;
		
		
		// TODO Auto-generated method stub
		return false;
	}

}
