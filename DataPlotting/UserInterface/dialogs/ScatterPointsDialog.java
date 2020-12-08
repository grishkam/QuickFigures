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
package dialogs;

import java.util.ArrayList;

import plotParts.DataShowingParts.PointModel;
import plotParts.DataShowingParts.ScatterPoints;
import standardDialog.choices.ComboBoxPanel;
import standardDialog.colors.ColorComboboxPanel;

public class ScatterPointsDialog  extends PointOptionsDialog/**GraphicItemOptionsDialog*/ {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ScatterPoints rect;
	ArrayList<ScatterPoints> additionalBars=new ArrayList<ScatterPoints>();

	
	
	
	public ScatterPointsDialog(ScatterPoints b, boolean bareBones) {
		this.bareBones=bareBones;
		rect=b;
		addOptionsToDialog();
	}
	public void addAdditionalBars(ArrayList<ScatterPoints> bars) {
		additionalBars=bars;
	}
	
	public  ScatterPointsDialog(ArrayList<?> objects) {
		this.bareBones=true;
		
		for(Object o: objects) {
			if (o instanceof ScatterPoints ) {
				if (rect==null) {
					rect=(ScatterPoints ) o;
					addOptionsToDialog();
				}
				else {
					additionalBars.add((ScatterPoints ) o);
				}
			}
		}
	}


	@Override
	public void addOptionsToDialog() {
		addShapeAttributesToDialog(rect);
		
		
	}
	
	public void addShapeAttributesToDialog(ScatterPoints  rect) {
		if (!bareBones)
		{
			super.addNameField(rect);
			super.addStrokePanelToDialog(rect);
			ColorComboboxPanel filpanel = new ColorComboboxPanel("Fill Color", null, rect.getFillColor());
			this.add("FillColor", filpanel);
		}
		
		addScatterSpecificOptions(rect);
		
	}


	protected void addScatterSpecificOptions(ScatterPoints rect) {
		PointModel p=rect.getPointModel();
		
		addPointModelOptions(p);
		
		this.add("exclusion",
				new ComboBoxPanel("Exclude", new String[] {"None", "Anything withing 1.5x IQR"}, rect.getExclusion()));
	
	
	}
	
	
	@Override
	public void setItemsToDiaog() {
		setItemsToDialog(rect);
		for(ScatterPoints bar: this.additionalBars) {setItemsToDialog(bar);}
		return ;
	}
	
	public void setItemsToDialog(ScatterPoints  rect) {
		if (!bareBones)
		{super.setNameFieldToDialog(rect);
		rect.setFillColor(super.getColor("FillColor"));
		super.setStrokedItemtoPanel(rect);
		}
		
		setMeanBarSpecific(rect);
}


	protected void setMeanBarSpecific(ScatterPoints rect) {
		setPointModelToDialog(rect.getPointModel());
		rect.setExclusion(this.getChoiceIndex("exclusion"));
		rect.requestShapeUpdate();
		rect.demandShapeUpdate();
		rect.needsPlotPointUpdate=true;
		rect.needsJitterUpdate=true;
	}
	
	
	
	
}