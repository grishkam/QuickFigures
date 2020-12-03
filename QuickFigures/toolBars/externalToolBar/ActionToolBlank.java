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
package externalToolBar;

import javax.swing.Icon;

/**Subclasses of this tool perform an action rather than being used to draw or edit
  */
public class ActionToolBlank<ImageType> extends BlankTool<ImageType>{

	protected String ActionCommand="";
	
	/**Lets the toolbar know that this is just an action tool*/
	public boolean isActionTool() {
		return true;
	}
	
	@Override
	public Icon getToolPressedImageIcon() {
		return getToolImageIcon();
	}
	@Override
	public Icon getToolRollOverImageIcon()  {
		return getToolImageIcon();
	}
	
	
}
