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
 * Version: 2023.1
 */
package objectDialogs;

import javax.swing.JTabbedPane;

import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorComboboxPanel;
import standardDialog.numbers.NumberInputPanel;
import undo.Edit;

public class BarSwingGraphicDialog  extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	BarGraphic primaryBar;

	
	
	
	public BarSwingGraphicDialog() {}
	public BarSwingGraphicDialog(BarGraphic b) {
		
		primaryBar=b;
		addOptionsToDialog();
		super.undoableEdit=Edit.createGenericEditForItem(b);
	}


	@Override
	public void addOptionsToDialog() {
		addBarAttributesToDialog(primaryBar);
		
		
	}
	
	public void addBarAttributesToDialog(BarGraphic rect) {
	super.addNameField(rect);
		super.addFixedEdgeToDialog(rect);
		
		NumberInputPanel nip = new NumberInputPanel("Width in "+rect.getScaleInfo().getUnits(), rect.getLengthInUnits());
		nip.setDecimalPlaces(2);
		this.add("uwidth", nip);
		
		
		this.add("barstroke", new NumberInputPanel("Thickness ", rect.getBarStroke(), true, true, 0,25));
		
		this.add("plen", new NumberInputPanel("Projection length", rect.getProjectionLength(), true, true, 0,50));
		this.add("ptype", new ChoiceInputPanel("Projection Type", BarGraphic.projTypes, rect.getProjectionType()));
		this.addScaleInfoToDialog(rect.getScaleInfo());
		
		this.add("Angle",new NumberInputPanel("angle", rect.getAngle()*(180/Math.PI)));
		this.add("fill" ,new ColorComboboxPanel("Fill Color",null, rect.getFillColor()));
		this.add("showT" , new BooleanInputPanel("Show text ", rect.isShowText()));
		this.getCurrentUsePanel().moveGrid(2, -1);
		this.add("autoT" ,new BooleanInputPanel("Autolocate text ", rect.isSnapBarText()));
		this.getCurrentUsePanel().moveGrid(-2, 0);
		super.addAttachmentPositionToDialog(rect);
		
		TextGraphic t = rect.getBarText();
		TextGraphicSwingDialog tgsd = new TextGraphicSwingDialog(t);
		JTabbedPane mp = tgsd.removeOptionsTab();
		this.getOptionDisplayTabs().addTab("Bar Text", mp);
	}
	
	@Override
	public void setItemsToDiaog() {
		setItemsToDialog(primaryBar);
		return ;
	}
	
	public void setItemsToDialog(BarGraphic rect) {
		super.setNameFieldToDialog(rect);
		super.setFixedEdgeToDialog(rect);
		
		rect.setLengthInUnits(this.getNumber("uwidth"));
		rect.setLengthProjection((int)this.getNumber("plen"));
		rect.setBarStroke((this.getNumber("barstroke")));
		this.setScaleInfoToDialog(rect.getScaleInfo());
		rect.setProjectionType(this.getChoiceIndex("ptype"));
		
		double newangle=this.getNumber("Angle")/(180/Math.PI);
		rect.setAngle(newangle);
		rect.setShowText(this.getBoolean("showT"));
		rect.setSnapBarText(this.getBoolean("autoT"));
		rect.setFillColor(this.getColor("fill"));
		super.setObjectSnappingBehaviourToDialog(rect);
	}
	
	

}
