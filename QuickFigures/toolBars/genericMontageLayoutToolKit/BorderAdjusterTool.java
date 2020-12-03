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
import gridLayout.LayoutSpaces;

import java.util.ArrayList;

import javax.swing.JMenuItem;


public class BorderAdjusterTool extends GeneralLayoutEditorTool implements LayoutSpaces {


	public void performDragEdit(boolean shift) {
		
			
			if (getRowIndexClick() > 1 && getMouseDisplacementY() != 0 && getRowIndexClick() <= getCurrentLayout().nRows()) {
				
				 getEditor().expandBorderY2(getCurrentLayout(), getMouseDisplacementY());
				 
					}
		
			if (getColIndexClick() > 1 && getColIndexClick() <= getCurrentLayout().nColumns() && getMouseDisplacementX() != 0) {
				//IssueLog.log("Attempting to expand border");	
				getEditor().expandBorderX2(getCurrentLayout(), getMouseDisplacementX());
			}

	}
	

		 
	public ArrayList<JMenuItem> getPopupMenuItems() {	
	//IssueLog.log("showing menu for "+currentlyInFocusWindowImage());
		/**return new MontageEditCommandMenu(
				 currentlyInFocusWindowImage().createLayout()
				).getBorderList();*/
		
		return null;
	}


	{createIconSet("icons/Montage_EditorToolIcon.jpg", "icons/Montage_EditorToolIconPressed.jpg", "icons/Montage_EditorToolRollOverIcon.jpg");}
	
	@Override
	public String getToolTip() {
			
			return "Adjust Border Between Panels";
		}
	
	@Override
	public String getToolName() {
			
			return "Adjust Border Between Panels";
		}
	
}
