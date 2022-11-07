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
 * Version: 2022.2
 */
package dialogs;

import java.util.ArrayList;

import objectDialogs.GraphicItemOptionsDialog;
import plotParts.DataShowingParts.ErrorBarShowingShape;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;

/**shows a dialog for editing the style of error bars*/
public class ErrorBarDialog  extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final String[] ERROR_BAR_TYPE_CHOICES = new String[] {"Standard Dev", "SEM", "95% Interval (2*SEM)", "99% (3*SEM)"},
									   CHOICES_FOR_LINES = new String[] {"Lines only", "Capped Lines"};

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ErrorBarShowingShape rect;
	ArrayList<ErrorBarShowingShape> additionalBars=new ArrayList<ErrorBarShowingShape>();

	private boolean bareBones;
	
	
	public ErrorBarDialog(ErrorBarShowingShape b, boolean bareBones) {
		this.bareBones=bareBones;
		rect=b;
		addOptionsToDialog();
	}
	
	public  ErrorBarDialog(ArrayList<?> objects) {
		this.bareBones=true;
		
		for(Object o: objects) {
			if (o instanceof ErrorBarShowingShape) {
				if (rect==null) {
					rect=(ErrorBarShowingShape) o;
					addOptionsToDialog();
				}
				else additionalBars.add((ErrorBarShowingShape) o);
			}
		}
	}
	
	public void addAdditionalBars(ArrayList<ErrorBarShowingShape> bars) {
		additionalBars=bars;
	}


	@Override
	public void addOptionsToDialog() {
		addBarAttributesToDialog(rect);
		
		
	}
	
	public void addBarAttributesToDialog(ErrorBarShowingShape  rect) {
		
		if (!bareBones)
			{
			super.addNameField(rect);
			super.addStrokePanelToDialog(rect);
		}
		
		addErrorBarSpecificParts(rect);
		
	}


	protected void addErrorBarSpecificParts(ErrorBarShowingShape rect) {
		NumberInputPanel nip = new NumberInputPanel("Bar Width", rect.getBarWidth(), 0, 80);
		nip.setDecimalPlaces(2);
		
		this.add("upper", new BooleanInputPanel("Shows upper", rect.isUpperBarShown()));
		this.add("lower", new BooleanInputPanel("Shows lower", rect.isLowerBarShown()));
		
		this.add("width", nip);
		
		this.add("typ",
				new ChoiceInputPanel("Show as", CHOICES_FOR_LINES, rect.getBarType()));
		
		this.add("errorT",
				new ChoiceInputPanel("Show Error Bar as", ERROR_BAR_TYPE_CHOICES, rect.getErrorDepiction()));
	}
	
	@Override
	public void setItemsToDiaog() {
		setItemsToDialog(rect);
		for(ErrorBarShowingShape r: additionalBars) {
			setItemsToDialog(r);
		}
		return ;
	}
	
	public void setItemsToDialog(ErrorBarShowingShape  rect) {
		
		if (!bareBones)
		{super.setNameFieldToDialog(rect);

		super.setStrokedItemtoPanel(rect);}
		
		setErrorBarSpecificOptions(rect);
}


	protected void setErrorBarSpecificOptions(ErrorBarShowingShape rect) {
		rect.setBarWidth((int)getNumber("width"));
		rect.setBarType(this.getChoiceIndex("typ"));
		rect.setLowerBarShown(this.getBoolean("lower"));
		rect.setUpperBarShown(this.getBoolean("upper"));
		rect.setErrorDepiction(this.getChoiceIndex("errorT"));
		rect.requestShapeUpdate();
		rect.updatePlotArea();
	}
	
	
}