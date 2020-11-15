package uiForAnimations;
import applicationAdapters.DisplayedImage;
import selectedItemMenus.BasicMultiSelectionOperator;
import uiForAnimations.TimeLineOperator;

public class BasicTimeLineOperator extends BasicMultiSelectionOperator implements TimeLineOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DisplayedImage display;
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
	public void setDisplay(DisplayedImage diw) {
		this.display=diw;
		
	}

	@Override
	public void setUI(TimeLineDialog dialog) {
		this.ui=dialog;
		
	}

}
