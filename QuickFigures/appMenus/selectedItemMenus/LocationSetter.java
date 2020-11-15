package selectedItemMenus;

import objectDialogs.LocationSettingDialog;
import utilityClassesForObjects.LocatedObject2D;

public class LocationSetter extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Set Item Location";
	}

	@Override
	public void run() {
		if (this.array.size()<1) return ;
		if (array.get(0) instanceof LocatedObject2D)
		new LocationSettingDialog((LocatedObject2D) array.get(0)).showDialog();;

	}
	
public String getMenuPath() {
		
		return "Item";
	}

}
