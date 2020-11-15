package uiForAnimations;

import applicationAdapters.DisplayedImage;
import selectedItemMenus.MultiSelectionOperator;

public interface TimeLineOperator extends MultiSelectionOperator{

	void setDisplay(DisplayedImage diw);

	void setUI(TimeLineDialog dialog);

}
