package basicMenusForApp;

import applicationAdapters.DisplayedImage;
import imageDisplayApp.GraphicSetDisplayWindow;
import includedToolbars.ActionToolset1;
import includedToolbars.ActionToolset2;
import includedToolbars.LayoutToolSet;
import includedToolbars.ObjectToolset1;

public class ShowToolBar  extends BasicMenuItemForObj {

	public static final String OBJECT_TOOLS="Object Toolbar", lAYOUT_TOOLS="Layout Tools", ACTION_BAR="Action Tools", SHAPE_AND_TEXT_EDITING_BAR="Shape And Text Edit Actions", MINIMUM_TOOLBAR="Minimum Toolset";

	private static final String SIDE_PANEL="Side Panels";
	
	public static String[] names=new String[] {OBJECT_TOOLS, lAYOUT_TOOLS, ACTION_BAR,SHAPE_AND_TEXT_EDITING_BAR, MINIMUM_TOOLBAR, SIDE_PANEL};
	
	
	int bar=0;
	
	public ShowToolBar(int type) {
		bar=type;
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (getToolBarName().equals(OBJECT_TOOLS))new ObjectToolset1().run("");
		if (getToolBarName().equals(lAYOUT_TOOLS))new LayoutToolSet().run("");	
		if (getToolBarName().equals(ACTION_BAR))new ActionToolset1().run("");
		if (getToolBarName().equals(SHAPE_AND_TEXT_EDITING_BAR))new ActionToolset2().run("");
		if (getToolBarName().equals(MINIMUM_TOOLBAR))new ObjectToolset1().run("");
		
		if (this.getToolBarName().equals(SIDE_PANEL)) {
			if (diw.getWindow() instanceof GraphicSetDisplayWindow) {
				GraphicSetDisplayWindow g=(GraphicSetDisplayWindow) diw.getWindow();
				g.setUsesBuiltInSidePanel(!g.usesBuiltInSidePanel());
			}
		};
		
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
