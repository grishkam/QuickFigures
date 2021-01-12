/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package basicMenusForApp;

import applicationAdapters.DisplayedImage;
import imageDisplayApp.AdaptiveToolbar;
import imageDisplayApp.GraphicSetDisplayWindow;
import includedToolbars.AlignAndArrangeActionTools;
import includedToolbars.ActionToolset2;
import includedToolbars.LayoutToolSet;
import includedToolbars.ObjectToolset1;

/**A Menu item that shows one of several possible toolbars*/
public class ShowToolBar  extends BasicMenuItemForObj {

	public static final String OBJECT_TOOLS="Object Tools", lAYOUT_TOOLS="Layout Tools", ACTION_BAR="Action Tools", SHAPE_AND_TEXT_EDITING_BAR="Shape And Text Edit Actions", MAIN_TOOLBAR="Main Toolbar";

	private static final String SIDE_PANEL="Side Panels", SMART_TOOL_BAR="Smart Toolbar";
	
	public static String[] names=new String[] {OBJECT_TOOLS, lAYOUT_TOOLS, ACTION_BAR,SHAPE_AND_TEXT_EDITING_BAR, MAIN_TOOLBAR, SMART_TOOL_BAR};
	
	
	int toolBarType=0;
	
	public ShowToolBar(int type) {
		toolBarType=type;
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (getToolBarName().equals(OBJECT_TOOLS))new ObjectToolset1().run("");
		if (getToolBarName().equals(lAYOUT_TOOLS))new LayoutToolSet().run("");	
		if (getToolBarName().equals(ACTION_BAR))new AlignAndArrangeActionTools().run("");
		if (getToolBarName().equals(SHAPE_AND_TEXT_EDITING_BAR))new ActionToolset2().run("");
		if (getToolBarName().equals(MAIN_TOOLBAR))new ObjectToolset1().run("");
		if (getToolBarName().equals(SMART_TOOL_BAR)) {
			AdaptiveToolbar a = new AdaptiveToolbar();
			a.setVisible(true);
		}
		if (this.getToolBarName().equals(SIDE_PANEL)) {
			if (diw.getWindow() instanceof GraphicSetDisplayWindow) {
				GraphicSetDisplayWindow g=(GraphicSetDisplayWindow) diw.getWindow();
				g.setUsesBuiltInSidePanel(!g.usesBuiltInSidePanel());
			}
		};
		
	}
	
	String getToolBarName() {
		return names[toolBarType];
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
