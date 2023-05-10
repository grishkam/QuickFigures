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
 * Version: 2023.2
 */
package objectDialogs;

import graphicalObjects_Shapes.RegularPolygonGraphic;
import standardDialog.numbers.NumberInputPanel;

/**An options dialog for regular polygons, includes options for the n-vertices in addition to all other options*/
public class PolygonGraphicOptionsDialog extends ShapeGraphicOptionsSwingDialog {

	RegularPolygonGraphic currentPolygon=null;
	
	/***/
	public PolygonGraphicOptionsDialog(RegularPolygonGraphic s, boolean simple) {
		super(s, true);
		currentPolygon=s;
	}
	
	/**Adds a version of the options from the */
	@Override
	protected void addOptionsToDialogPart1() {
		super.addOptionsToDialogPart1();
		if (s instanceof RegularPolygonGraphic) currentPolygon=(RegularPolygonGraphic) s;
		NumberInputPanel win = new NumberInputPanel("Width", currentPolygon.getObjectWidth());
		NumberInputPanel hin = new NumberInputPanel("Height", currentPolygon.getObjectHeight());
		NumberInputPanel vin = new NumberInputPanel("N-Vertex", currentPolygon.getNvertex());
		this.add("width", win);
		this.add("height", hin);
		this.add("Vertices", vin);
	}
	
	protected void setItemsToDiaog() {
		super.setItemsToDiaog();
		currentPolygon.setWidth(this.getNumber("width"));
		currentPolygon.setHeight(this.getNumber("height"));
		currentPolygon.setNvertex((int)this.getNumber("Vertices"));
	}
	

	private static final long serialVersionUID = 1L;
	
	

}
