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
import logging.IssueLog;

/**Tool that moves the layout*/
public class MontageMoverTool  extends GeneralLayoutEditorTool implements LayoutSpaces{



	{createIconSet( "icons/MontageMoverIcon.jpg", "icons/MontageMoverPressedIcon.jpg", "icons/MontageMoverRolloverIcon.jpg");}

	
	public void performDragEdit(boolean shift) {
	try {
			 getEditor().moveMontageLayout(getCurrentLayout(), getMouseDisplacementX(), getMouseDisplacementY());

		} catch (Throwable t) {IssueLog.logT(t);}
	}
	

	@Override
	public String getToolTip() {
		
			return "Adjust Layout Position";
		}
	@Override
	public String getToolName() {
			
			return "Move Figure Layout";
		}


	
}