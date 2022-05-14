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
 * Version: 2022.1
 */
package objectDialogs;

import graphicalObjects_Shapes.ShapeGraphic;

/**A shape options dialog that only contains the options for the stroke colors and
 * not the fill colors*/
public class StrokeOnlySwingDialog extends ShapeGraphicOptionsSwingDialog{

	public StrokeOnlySwingDialog(ShapeGraphic s) {
		super(s, true);
		addOptionsToDialog2();
		
	}

	
	protected void addOptionsToDialog2() {
		
		addOptionsToDialogPart2();
	}
	
	/**Overrides the method from the superclass*/
	@Override
	protected void addOptionsToDialog() {
		
		
	}
	
	/**Overrides the method from the superclass*/
	@Override
	protected void addOptionsToDialogPart2() {
		
		addStrokePanelToDialog(s);
		
		
	}
	

	/**Overrides the method from the superclass*/
	@Override
	protected void setItemsToDiaog() {
		this.setStrokedItemtoPanel(s);
		
		
		
}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
