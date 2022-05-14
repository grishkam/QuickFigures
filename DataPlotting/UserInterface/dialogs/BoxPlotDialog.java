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
 * Date Modified: Jan 7, 2021
 * Version: 2022.1
 */
package dialogs;

import java.util.ArrayList;

import objectDialogs.GraphicItemOptionsDialog;
import plotParts.DataShowingParts.Boxplot;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;

/**A dialog for editing the appearance of a boxplot*/
public class BoxPlotDialog  extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Boxplot mainBox;
	ArrayList<Boxplot> additionalBoxes=new ArrayList<Boxplot>();

	private boolean bareBones;
	
	
	public BoxPlotDialog(ArrayList<?> objects) {
		this.bareBones=true;
		
		for(Object o: objects) {
			if (o instanceof Boxplot) {
				if (mainBox==null) {
					mainBox=(Boxplot) o;
					addOptionsToDialog();
				}
				else additionalBoxes.add((Boxplot) o);
			}
		}
	}
	
	public BoxPlotDialog(Boxplot b, boolean bareBones) {
		this.bareBones=bareBones;
		mainBox=b;
		addOptionsToDialog();
	}
	public void addAdditionalBars(ArrayList<Boxplot> bars) {
		additionalBoxes=bars;
	}


	@Override
	public void addOptionsToDialog() {
		addBarAttributesToDialog(mainBox);
		
		
	}
	
	public void addBarAttributesToDialog(Boxplot  rect) {
		if (!bareBones)
		{
			super.addNameField(rect);
			super.addStrokePanelToDialog(rect);

		}
		
		addMeanBarSpecificOptions(rect);
		
	}


	protected void addMeanBarSpecificOptions(Boxplot rect) {
		NumberInputPanel nip = new NumberInputPanel("Box Width", rect.getBarWidth(), 4, 40);
		nip.setDecimalPlaces(2);
		this.add("width", nip);
		
		NumberInputPanel nip2 = new NumberInputPanel("Cap Width", rect.getCapSize(), 4);
	
		this.add("width2", nip2);
		
		this.add("typ",
				new ChoiceInputPanel("Show ends as", new String[] {"Min/Max", "Min/Max excluding outliers"}, rect.getWhiskerType()));
	}
	
	@Override
	public void setItemsToDiaog() {
		setItemsToDialog(mainBox);
		for(Boxplot bar: this.additionalBoxes) {setItemsToDialog(bar);}
		return ;
	}
	
	public void setItemsToDialog(Boxplot  rect) {
		if (!bareBones)
		{super.setNameFieldToDialog(rect);
		super.setStrokedItemtoPanel(rect);
		}
		
		setBoxBarSpecific(rect);
}


	protected void setBoxBarSpecific(Boxplot rect) {
		rect.setBarWidth((int)getNumber("width"));
		rect.setWhiskerType(this.getChoiceIndex("typ"));
		rect.setCapSize(getNumber("width2"));
		rect.requestShapeUpdate();
		rect.updatePlotArea();
	}
	
	
}