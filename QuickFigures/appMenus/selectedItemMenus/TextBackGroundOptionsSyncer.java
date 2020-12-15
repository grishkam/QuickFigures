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
package selectedItemMenus;

import javax.swing.Icon;

import graphicalObjects_BasicShapes.TextGraphic;
import objectDialogs.ShapeGraphicOptionsSwingDialog;

public class TextBackGroundOptionsSyncer extends BasicMultiSelectionOperator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Set Text Background Options";
	}
	

	@Override
	public void run() {
		ShapeGraphicOptionsSwingDialog mt = new ShapeGraphicOptionsSwingDialog(getAllArray(), true);
		mt.showDialog();

	}
	
	
	public Icon getIcon() {
		return TextGraphic.createImageIcon();
	}
	
	public String getMenuPath() {
		
		return "Text";
	}

}
