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

import javax.swing.JTabbedPane;

import kaplanMeierPlots.KaplanMeierCensorShower;
import objectDialogs.GraphicItemOptionsDialog;
import plotParts.DataShowingParts.PointModel;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorComboboxPanel;
import standardDialog.numbers.NumberInputPanel;

/**A dialog for editing the appearance of centor marks*/
public class CensorMarkDialog  extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	KaplanMeierCensorShower censorMark;
	ArrayList<KaplanMeierCensorShower> additionalMarks=new ArrayList<KaplanMeierCensorShower>();

	private boolean bareBones;

	private PointOptionsDialog dia2;

	private boolean points;
	
	
	public CensorMarkDialog(KaplanMeierCensorShower b, boolean bareBones) {
		this.bareBones=bareBones;
		censorMark=b;
		addOptionsToDialog();
	}
	
	public  CensorMarkDialog(ArrayList<?> objects) {
		this.bareBones=true;
		
		for(Object o: objects) {
			if (o instanceof KaplanMeierCensorShower ) {
				if (censorMark==null) {
					censorMark=(KaplanMeierCensorShower ) o;
					addOptionsToDialog();
				}
				else {
					additionalMarks.add((KaplanMeierCensorShower ) o);
					addToPointList((KaplanMeierCensorShower) o);
				}
			}
		}
	}
	
	public void addAdditionalBars(ArrayList<KaplanMeierCensorShower> bars) {
		additionalMarks=bars;
		for(KaplanMeierCensorShower b: bars) {
			addToPointList(b);
		}
	}

	public void addToPointList(KaplanMeierCensorShower b) {
		if (dia2!=null&&points) dia2.addAdditionalPoint(b.getPointModel());
	}


	@Override
	public void addOptionsToDialog() {
		addBarAttributesToDialog(censorMark);
		
		
	}
	
	public void addBarAttributesToDialog(KaplanMeierCensorShower  rect) {
		if (!bareBones)
		{
			super.addNameField(rect);
			super.addStrokePanelToDialog(rect);
			ColorComboboxPanel filpanel = new ColorComboboxPanel("Fill Color", null, rect.getFillColor());
			this.add("FillColor", filpanel);
		}
		
		if (rect.showsAsCustomMarkPoint()) {
			points=true;
			PointModel m = rect.getPointModel();
			dia2 = new PointOptionsDialog(m, bareBones, new StandardDialogListener() {

				@Override
				public void itemChange(DialogItemChangeEvent event) {
					afterEachItemChange();
					
				}});
			JTabbedPane tab2 = dia2.removeOptionsTab();
			tab2.setName("Point Options");
			this.getOptionDisplayTabs().addTab("Edit Points", tab2);
		}
		
		addMeanBarSpecificOptions(rect);
		
	}


	protected void addMeanBarSpecificOptions(KaplanMeierCensorShower rect) {
		NumberInputPanel nip = new NumberInputPanel("Mark Width", rect.getBarWidth(), 0, 50);
		nip.setDecimalPlaces(2);
		this.add("width", nip);
		
		this.add("typ",
				new ChoiceInputPanel("Show as", new String[] {"Line only", "Crossing Line", "Plus", "Circle", "Shape"}, rect.getMarkType()));
	}
	
	@Override
	public void setItemsToDiaog() {
		setItemsToDialog(censorMark);
		for(KaplanMeierCensorShower bar: this.additionalMarks) {setItemsToDialog(bar);}
		return ;
	}
	
	public void setItemsToDialog(KaplanMeierCensorShower  rect) {
		if (!bareBones)
		{super.setNameFieldToDialog(rect);
		rect.setFillColor(super.getColor("FillColor"));
		super.setStrokedItemtoPanel(rect);
		}
		
		setMeanBarSpecific(rect);
}


	protected void setMeanBarSpecific(KaplanMeierCensorShower rect) {
		if (rect==null) return;
		rect.setBarWidth((int)getNumber("width"));
		rect.setMarkType(this.getChoiceIndex("typ"));
		
		rect.requestShapeUpdate();
		rect.updatePlotArea();
	}
	
	
	
	
}