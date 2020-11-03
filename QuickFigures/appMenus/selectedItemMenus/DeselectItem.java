package selectedItemMenus;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.Selectable;

public class DeselectItem extends HideItem implements MultiSelectionOperator {

	boolean deselect=true;
	
	public DeselectItem(boolean b) {
		deselect=b;
	}



	@Override
	public String getMenuCommand() {
		if (!deselect) return "Items of Same Type (press a)";
		return "De-Select All";
	}
	


	@Override
	public void run() {
		if (deselect) for(ZoomableGraphic i: this.array) {
			if (i==null) continue;
			if (i instanceof Selectable) {
				Selectable h=(Selectable) i;
				h.deselect();
			}
		}
		
		if (!deselect) for(ZoomableGraphic i: this.array) {
			selectThoseOfSameClass(i);
		}

	}



	private void selectThoseOfSameClass(ZoomableGraphic sel) {
		ArrayList<LocatedObject2D> all = selector.getImageWrapper().getLocatedObjects();
		for(LocatedObject2D item: all) {
			if(sel==null||item==null) continue;
			if(item.getClass()==sel.getClass()) item.select();
		}
	}
	
	public String getMenuPath() {return "Select";}
}
