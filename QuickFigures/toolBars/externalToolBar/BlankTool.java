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
 * Date Modified: Jan 4, 2021
 * Version: 2023.1
 */
package externalToolBar;
import javax.swing.Icon;

import icons.AcronymIcon;
import icons.IconSet;



/**A default tool that can be used as both space filler in an external toolset or */
public class BlankTool<ImageType> extends  DummyTool<ImageType>{

	/**An initial icon set*/
	private IconSet iconSet=new AcronymIcon("_",0).generateIconSet();//;new IconSet("icons/Blank.jpg", "icons/BlankPressed.jpg", "icons/Blank.jpg","icons/Blank.jpg","icons/Blank.jpg","icons/Blank.jpg", "icons/Blank.jpg");

	@Override
	public Icon getToolNormalIcon() {
		return getIconSet().getIcon(0);
	}

	@Override
	public Icon getToolPressedImageIcon() {
		return getIconSet().getIcon(1);
	}

	@Override
	public Icon getToolRollOverImageIcon() {
		if (isMenuOnlyTool()||isActionTool()) return  getToolNormalIcon();
		return getIconSet().getIcon(2);//.getIcon(2);
	}

	public IconSet getIconSet() {
		return iconSet;
	}

	public void setIconSet(IconSet iconSet) {
		this.iconSet = iconSet;
	}



}
