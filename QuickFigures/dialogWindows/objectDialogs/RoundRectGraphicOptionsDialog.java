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

import graphicalObjects_Shapes.RoundedRectangleGraphic;
import standardDialog.numbers.NumberInputPanel;

public class RoundRectGraphicOptionsDialog extends ShapeGraphicOptionsSwingDialog {

	RoundedRectangleGraphic rect=null;
	
	public RoundRectGraphicOptionsDialog(RoundedRectangleGraphic roundedRectangleGraphic, boolean simple) {
		super(roundedRectangleGraphic, simple);
		rect=roundedRectangleGraphic;
	}
	
	protected void addOptionsToDialogPart1() {
		super.addOptionsToDialogPart1();
		if (s instanceof RoundedRectangleGraphic ) rect=(RoundedRectangleGraphic ) s;
		NumberInputPanel win = new NumberInputPanel("Width", rect.getBounds().width);
		NumberInputPanel hin = new NumberInputPanel("Height", rect.getBounds().height);
		NumberInputPanel arcw = new NumberInputPanel("Arc Width", rect.getArcw());
		NumberInputPanel arch = new NumberInputPanel("Arc Heigth", rect.getArch());
		this.add("width", win);
		this.add("height", hin);
		this.add("arcw", arcw );
		this.add("arch", arch );
	}
	
	protected void setItemsToDiaog() {
		super.setItemsToDiaog();
		rect.setWidth(this.getNumber("width"));
		rect.setHeight(this.getNumber("height"));
		rect.setArcw(this.getNumber("arcw"));
		rect.setArch(this.getNumber("arch"));
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

}
