package basicMenusForApp;

import applicationAdapters.DisplayedImage;
import includedToolbars.ActionToolset1;
import includedToolbars.ActionToolset2;
import includedToolbars.LayoutToolSet;
import includedToolbars.ObjectToolset1;

public class ShowToolBar  extends BasicMenuItemForObj {

	static String obToolBar="Object Toolbar", layoutToolbar="Layout Tools", actionToolbar="Action Tools", actionToolbar2="Shape And Text Edit Actions", mintools="Minimum Toolset";
	
	static String[] names=new String[] {obToolBar, layoutToolbar, actionToolbar,actionToolbar2, mintools};
	
	
	int bar=0;
	
	public ShowToolBar(int type) {
		bar=type;
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (getToolBarName().equals(obToolBar))new ObjectToolset1().run("");
		if (getToolBarName().equals(layoutToolbar))new LayoutToolSet().run("");	
		if (getToolBarName().equals(actionToolbar))new ActionToolset1().run("");
		if (getToolBarName().equals(actionToolbar2))new ActionToolset2().run("");
		if (getToolBarName().equals(mintools))new ObjectToolset1().run("");
	}
	
	String getToolBarName() {
		return names[bar];
	}
	

	@Override
	public String getCommand() {
		return "Show Toolbar "+getToolBarName() ;
	}

	@Override
	public String getNameText() {
		return "Show "+ getToolBarName() ;
	}

	@Override
	public String getMenuPath() {
		return "Toolbars<";
	}

}
