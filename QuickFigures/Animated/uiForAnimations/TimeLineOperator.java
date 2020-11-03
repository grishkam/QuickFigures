package uiForAnimations;

import applicationAdapters.DisplayedImageWrapper;
import selectedItemMenus.MultiSelectionOperator;

public interface TimeLineOperator extends MultiSelectionOperator{

	void setDisplay(DisplayedImageWrapper diw);

	void setUI(TimeLineDialog dialog);

}
