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
package objectDialogs;

import graphicalObjects_BasicShapes.RectangularGraphic;
import standardDialog.NumberInputPanel;

public class WidthAndHeightDialog extends ShapeGraphicOptionsSwingDialog {

	RectangularGraphic rect=null;
	{this.setWindowCentered(true);}
	
	public WidthAndHeightDialog(RectangularGraphic s) {
		super(s, true);
		rect=s;
		// TODO Auto-generated constructor stub
	}
	
	protected void addOptionsToDialog() {
		 addOptionsToDialogPart1();
	}
	
	protected void addOptionsToDialogPart1() {
		addWidthAndHeightToDialog();
	}

	private void addWidthAndHeightToDialog() {
		if (s instanceof RectangularGraphic) rect=(RectangularGraphic) s;
		NumberInputPanel win = new NumberInputPanel("Width", rect.getRectangle().width);
		NumberInputPanel hin = new NumberInputPanel("Height", rect.getRectangle().height);
		this.add("width", win);
		this.add("height", hin);
	}
	
	protected void setItemsToDiaog() {
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
