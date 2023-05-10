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
 * Version: 2023.2
 */
package dialogs;

import java.util.ArrayList;

import plotParts.DataShowingParts.PointModel;
import plotParts.DataShowingParts.ScatterPoints;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorComboboxPanel;

/**shows a dialog for editing scatter point objects*/
public class ScatterPointsDialog  extends PointOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ScatterPoints mainTargetPoints;
	ArrayList<ScatterPoints> additionalPoints=new ArrayList<ScatterPoints>();

	
	
	
	public ScatterPointsDialog(ScatterPoints b, boolean bareBones) {
		this.bareBones=bareBones;
		mainTargetPoints=b;
		addOptionsToDialog();
	}
	public void addAdditionalBars(ArrayList<ScatterPoints> bars) {
		additionalPoints=bars;
	}
	
	public  ScatterPointsDialog(ArrayList<?> objects) {
		this.bareBones=true;
		
		for(Object o: objects) {
			if (o instanceof ScatterPoints ) {
				if (mainTargetPoints==null) {
					mainTargetPoints=(ScatterPoints ) o;
					addOptionsToDialog();
				}
				else {
					additionalPoints.add((ScatterPoints ) o);
				}
			}
		}
	}


	@Override
	public void addOptionsToDialog() {
		addShapeAttributesToDialog(mainTargetPoints);
		
		
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
				new ChoiceInputPanel("Exclude", new String[] {"None", "Anything within 1.5x IQR"}, rect.getExclusion()));
	
	
	}
	
	
	@Override
	public void setItemsToDiaog() {
		setItemsToDialog(mainTargetPoints);
		for(ScatterPoints bar: this.additionalPoints) {setItemsToDialog(bar);}
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