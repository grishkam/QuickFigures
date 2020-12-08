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

import externalToolBar.GraphicToolIcon;
import genericTools.Object_Mover;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;

public class LayoutMover extends Object_Mover {
	{super.bringSelectedToFront=true; 
	super.onlySelectThoseOfClass=PanelLayoutGraphic.class;
	}
	{//createIconSet("icons2/LayoutMoverIcon.jpg","icons2/LayoutMoverIconPress.jpg","icons2/LayoutMoverIcon.jpg");
	set=GraphicToolIcon.createIconSet(prepareIcon());
	}

	/**
	 * @return
	 */
	protected LayoutShowingToolIcon prepareIcon() {
		return new LayoutShowingToolIcon(0, true);
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
