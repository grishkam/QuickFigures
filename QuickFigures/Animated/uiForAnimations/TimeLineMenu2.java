package uiForAnimations;

import applicationAdapters.DisplayedImageWrapper;
import selectedItemMenus.LayerSelector;

public class TimeLineMenu2 extends TimeLineMenu {

	public TimeLineMenu2(DisplayedImageWrapper diw, LayerSelector ls, TimeLineDialog dialog) {
		super(diw, ls, dialog);
		// TODO Auto-generated constructor stub
	}
	
	void setup() {
		this.setText("Time Line"); this.setName("Time Line");
		this.addOperation(new TimeLineRange(0));
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
