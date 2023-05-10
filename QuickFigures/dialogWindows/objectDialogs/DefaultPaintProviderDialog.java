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

import locatedObject.DefaultPaintProvider;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.choices.ItemSelectblePanel;
import standardDialog.colors.ColorInputPanel;
import standardDialog.graphics.FixedEdgeSelectable;

/**A dialog for editing paint provider objects 
 * @see DefaultPaintProvider*/
public class DefaultPaintProviderDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultPaintProvider paintprovider;
	
	public DefaultPaintProviderDialog(DefaultPaintProvider d) {
		this.paintprovider=d;
		this.add("Color1", new ColorInputPanel("Color", paintprovider.getColor() ));
		this.add("Color2", new ColorInputPanel("Color", paintprovider.getFillColor2() ));
		FixedEdgeSelectable f = new FixedEdgeSelectable(paintprovider.getFe1());
		ItemSelectblePanel is = new ItemSelectblePanel("Select Fixed Edge 1", f);
		add("edge fix1", is);
		
		FixedEdgeSelectable f2 = new FixedEdgeSelectable(paintprovider.getFe2());
		ItemSelectblePanel is2 = new ItemSelectblePanel("Select Fixed Edge 2", f2);
		add("edge fix2", is2);
		
		this.add("type", new ChoiceInputPanel("Paint Type",DefaultPaintProvider.types, paintprovider.getType()));
	}
	
	protected void setItemsToDiaog() {
		paintprovider.setColor(this.getColor("Color1"));
		paintprovider.setFillColor2(this.getColor("Color2"));
		paintprovider.setType(this.getChoiceIndex("type"));
		paintprovider.setFe1(this.getChoiceIndex("edge fix1"));
		paintprovider.setFe2(this.getChoiceIndex("edge fix2"));
	}
	

}
