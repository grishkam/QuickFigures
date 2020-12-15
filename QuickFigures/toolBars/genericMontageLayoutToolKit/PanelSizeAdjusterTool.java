/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package genericMontageLayoutToolKit;
import genericMontageLayoutToolKit.GeneralLayoutEditorTool;
import gridLayout.LayoutSpaces;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;


public class PanelSizeAdjusterTool extends GeneralLayoutEditorTool implements ActionListener, LayoutSpaces {
	int mode=1;
	

	@Override
	public
	void performDragEdit(boolean b) {
		if (mode==1) {
			 getLayoutEditor().augmentPanelHeight(getCurrentLayout(), getYDisplaceMent() , getRowIndexClick());
			 getLayoutEditor().augmentPanelWidth(getCurrentLayout(), getXDisplaceMent() , getColIndexClick());
		}
		
	}
	
	{createIconSet("icons/PanelSizeAdjusterToolIcon.jpg","icons/PanelSizeAdjusterToolIconPressed.jpg","icons/PanelSizeAdjusterToolIconRollover.jpg");}
	


	public void onActionPerformed(Object sour, String st) {
		
	}
	
	public ArrayList<JMenuItem> getPopupMenuItems() {
		//return 	new MontageEditCommandMenu( currentlyInFocusWindowImage().createLayout()).getPanelSizeList();
		return null;
	}
	
	@Override
	public String getToolTip() {
		
			return "Adjust Montage Layout Panel Size";
		}
	
	@Override
	public String getToolName() {
		
			return "Panel Size AdjustMentTool";
		}

}
