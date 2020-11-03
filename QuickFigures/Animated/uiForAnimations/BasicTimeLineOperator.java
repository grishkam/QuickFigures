package uiForAnimations;
import applicationAdapters.DisplayedImageWrapper;
import selectedItemMenus.BasicMultiSelectionOperator;
import uiForAnimations.TimeLineOperator;

public class BasicTimeLineOperator extends BasicMultiSelectionOperator implements TimeLineOperator {

	protected DisplayedImageWrapper display;
	protected TimeLineDialog ui;

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDisplay(DisplayedImageWrapper diw) {
		this.display=diw;
		
	}

	@Override
	public void setUI(TimeLineDialog dialog) {
		this.ui=dialog;
		
	}

}
