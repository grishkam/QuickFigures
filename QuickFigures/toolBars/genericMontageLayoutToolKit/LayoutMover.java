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
 * Date Modified: Jan 5, 2021
 * Version: 2023.1
 */
package genericMontageLayoutToolKit;

import genericTools.Object_Mover;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import icons.GraphicToolIcon;

/**A version of the object mover than only selects the default layouts*/
public class LayoutMover extends Object_Mover {
	{
		super.bringSelectedToFront=true; 
		setSelectOnlyThoseOfClass(PanelLayoutGraphic.class);
		iconSet=GraphicToolIcon.createIconSet(prepareIcon());
	}

	/**
	
	 */
	protected GeneralLayoutToolIcon prepareIcon() {
		return new GeneralLayoutToolIcon(0, true);
	};
	
	@Override
	public String getToolTip() {
			
			return "Select and Manipulate Layouts";
		}
	

	@Override
	public String getToolName() {
			
			return "Select, move and edit layouts";
		}
	
}
