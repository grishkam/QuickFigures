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
 * Version: 2023.1
 */
package objectDialogs;

import graphicalObjects_Shapes.RectangularGraphic;
import standardDialog.numbers.NumberInputPanel;

/**An options dialog for RectangularGraphics*/
public class RectangleGraphicOptionsDialog extends ShapeGraphicOptionsSwingDialog {

	RectangularGraphic rect=null;
	
	public RectangleGraphicOptionsDialog(RectangularGraphic s, boolean simple) {
		super(s, simple);
		rect=s;
	}
	
	protected void addOptionsToDialogPart1() {
		super.addOptionsToDialogPart1();
		addWidthAndHeightToDialog();
	}

	private void addWidthAndHeightToDialog() {
		if (s instanceof RectangularGraphic) rect=(RectangularGraphic) s;
		NumberInputPanel win = new NumberInputPanel("Width", rect.getObjectWidth());
		NumberInputPanel hin = new NumberInputPanel("Height", rect.getObjectHeight());
		this.add("width", win);
		this.add("height", hin);
	}
	
	protected void setItemsToDiaog() {
		super.setItemsToDiaog();
		setWidthAndHeighttoDialog();
	}

	private void setWidthAndHeighttoDialog() {
		rect.setWidth(this.getNumber("width"));
		rect.setHeight(this.getNumber("height"));
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

}
