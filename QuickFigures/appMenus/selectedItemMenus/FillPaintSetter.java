package selectedItemMenus;

import graphicalObjects.ZoomableGraphic;
import utilityClassesForObjects.Fillable;

public class FillPaintSetter extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Set Fill Paint";
	}

	@Override
	public void run() {
		if (array.size()==0) return;
		ZoomableGraphic item = this.array.get(0);
	if (item instanceof Fillable) {
		Fillable f=(Fillable) item;
		f.getFillPaintProvider().showOptionsDialog();
	}

	}
	
public String getMenuPath() {
		
		return "Item";
	}

}
