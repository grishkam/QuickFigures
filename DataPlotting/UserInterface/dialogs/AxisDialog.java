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

import javax.swing.JTabbedPane;

import graphicalObjects_SpecialObjects.TextGraphic;
import objectDialogs.GraphicItemOptionsDialog;
import objectDialogs.TextGraphicSwingDialog;
import plotParts.Core.AxesGraphic;
import standardDialog.GriddedPanel;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;

/**A dialog for editing plot axes*/
public class AxisDialog  extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	AxesGraphic axis;
	
	
	public AxisDialog(AxesGraphic targetAxis) {
		
		axis=targetAxis;
		addOptionsToDialog();
	}


	@Override
	public void addOptionsToDialog() {
		addBarAttributesToDialog(axis);
		
		
	}
	
	public void addBarAttributesToDialog(AxesGraphic  rect) {
		
		super.addNameField(rect);
		
		NumberInputPanel nip = new NumberInputPanel("Minimun ", rect.getAxisData().getMinValue(), 0, 100);
		NumberInputPanel nip2 = new NumberInputPanel("Maximum ", rect.getAxisData().getMaxValue(), 50, 1000);
		nip.setDecimalPlaces(2);nip2.setDecimalPlaces(2);
		this.add("min", nip);this.add("max", nip2);
		
		NumberInputPanel nip3 = new NumberInputPanel("Major marks ", rect.getAxisData().getMajorTic(), 1, 500);
		NumberInputPanel nip4 = new NumberInputPanel("Minor marks ", rect.getAxisData().getMinorTic(), 1, 100);
		nip3.setDecimalPlaces(2);nip4.setDecimalPlaces(2);
		this.add("major", nip3);this.add("minor", nip4);
		ChoiceInputPanel panel = new ChoiceInputPanel("Marks ", new String[] {"Show all", "Dont Show", "Show Major Only"}, rect.getHideTicmarks());
		this.add("hidTic", panel);
	
	
			NumberInputPanel nip5 = new NumberInputPanel("Tic length", rect.getTicLength(), 5, 50);
			nip5.setDecimalPlaces(2);
			this.add("tic length" , nip5);
			this.addScaleInfoToDialog(rect.getScaleInfo());
			this.add("showT" , new BooleanInputPanel("Show text ", rect.isShowText()));
			this.add("otherSide" , new BooleanInputPanel("Alternte Side", rect.isOnAlternateSide()));
			
			
			
			

		super.addStrokePanelToDialog(rect);
	
		this.getCurrentUsePanel().moveGrid(2, -1);
		this.getCurrentUsePanel().moveGrid(-2, 0);
		//super.addSnappingBehviourToDialog(rect);
		
		/**text tab*/
		TextGraphic t = rect.getLabelText();
		TextGraphicSwingDialog tgsd = new TextGraphicSwingDialog(t);
		JTabbedPane mp = tgsd.removeOptionsTab();
		this.getOptionDisplayTabs().addTab("Text Labels", mp);
		
		
		GriddedPanel gp2 = new GriddedPanel();
		this.getOptionDisplayTabs().addTab("Scale Level", gp2);
		this.setMainPanel(gp2);
		ChoiceInputPanel panel2 = new ChoiceInputPanel("Scale Type ", new String[] {"Normal", "Log", "other"}, rect.getScaleType());
		this.add("ScaleType", panel2);
		ChoiceInputPanel panel3 = new ChoiceInputPanel("Label Scale as ", new String[] {"Normal", "a*10^b", "other"}, rect.getScaleLabelType());
		this.add("ScaleLabel", panel3);
		
		
		GriddedPanel gp = new GriddedPanel();
		this.getOptionDisplayTabs().addTab("Gaps", gp);
		this.setMainPanel(gp);
		NumberInputPanel nip8 = new NumberInputPanel("Gap location", rect.getGap().location(), 1, 500);
		NumberInputPanel nip9 = new NumberInputPanel("Gap size", rect.getGap().getSize(), 1, 100);
		nip8.setDecimalPlaces(2);nip9.setDecimalPlaces(2);
		this.add("gapLocation", nip8);
		this.add("gap size", nip9);
	}
	
	@Override
	public void setItemsToDiaog() {
		setItemsToDialog(axis);
		return ;
	}
	
	public void setItemsToDialog(AxesGraphic  rect) {
		//super.setNameFieldToDialog(rect);

		rect.setOnAlternateSide(getBoolean("otherSide"));
		rect.getAxisData().setMinValue(this.getNumber("min"));
		rect.getAxisData().setMaxValue(this.getNumber("max"));
		rect.getAxisData().setMajorTic((int) this.getNumber("major"));
		rect.getAxisData().setMinorTic((int) this.getNumber("minor"));
		rect.setShowText(this.getBoolean("showT"));
		rect.setTicLength(this.getNumber("tic length"));
		rect.setHideTicmarks(this.getChoiceIndex("hidTic"));
		
		rect.getGap().setLocation(getNumber("gapLocation"));
		rect.getGap().setSize(getNumber("gap size"));
		rect.setSetScaleType(this.getChoiceIndex("ScaleType"));
		rect.setScaleLabelType(this.getChoiceIndex("ScaleLabel"));
		this.setScaleInfoToDialog(rect.getScaleInfo());
		super.setStrokedItemtoPanel(rect);
		rect.updatePlotArea();;
}
	
	
}