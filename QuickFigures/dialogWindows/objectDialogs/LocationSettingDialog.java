package objectDialogs;

import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;
import utilityClassesForObjects.LocatedObject2D;

public class LocationSettingDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocatedObject2D ob;
	
	public  LocationSettingDialog(LocatedObject2D ob) {
		this.ob=ob;
		this.setModal(true);
		addOptionsToDialog();
	}
	
	void addOptionsToDialog() {
		this.add("x", new NumberInputPanel("x", ob.getLocation().getX()));
		this.add("y", new NumberInputPanel("y", ob.getLocation().getY()));
		
	}
	
	@Override
	public void onOK() {
		setItemToDialog();
	}
	
	void setItemToDialog() {
		ob.setLocation(this.getNumberInt("x"), getNumberInt("y"));
		 GraphicItemOptionsDialog.updateCurrentDisplay();
	}

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
