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
 * Version: 2021.1
 */
package objectDialogs;

import java.awt.GridBagConstraints;

import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import standardDialog.ObjectEditEvent;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorDimmingBox;

/**A dialog for complex text items, includes text alignment options
 * and a series of tabs for every single individual segment of text*/
public class ComplexTextGraphicSwingDialog extends TextGraphicSwingDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ComplexTextGraphic ct=null;
	
	public ComplexTextGraphicSwingDialog(ComplexTextGraphic t) {
		textItem=t;
		ct=t;
		addOptionsToDialog();
	}

	
	protected void addOptionsToDialog() {
		this.addNameField(textItem);
		
		addJustificationToDialog(textItem);
		
		//this.add("Text", new StringInputPanel("Text", textItem.getText()));
		
		this.addFixedEdgeToDialog(textItem);
		super.addFontAngleToDialog();
		addBackgroundOptionsToDialog();
		ChoiceInputPanel cp=new ChoiceInputPanel("Color Dim Type ",  new ColorDimmingBox(textItem.getDimming().ordinal()));
		this.add(DIM_KEY, cp);
		this.getMainPanel().moveGrid(2, -1);
		this.add(DOES_DIM_KEY, new BooleanInputPanel("Dim Color?", textItem.isDimColor()));
		this.getMainPanel().moveGrid(-2, 0);
		
		addLineTabs();
		
	
		addSnappingBehviourToDialog(textItem);
		
		
	}


	protected void addLineTabs() {
		ParaGraphPane tabsfull = new ParaGraphPane(ct.getParagraph());
		
		tabsfull.addObjectEditListener(this);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=gx;
		c.gridy=gridPositionY;
		c.gridheight=4;
		c.gridwidth=6;
		//this.add(tabsfull, c);
		gridPositionY+=4;
		getOptionDisplayTabs().addTab("Edit Text Lines",tabsfull);
		
		
	}
	
	protected void setItemsToDiaog(TextGraphic textItem) {
		super.setNameFieldToDialog(textItem);
		this.setBackgroundOptionsToDialog(textItem);
		//textItem.setText(this.getString("Text"));
		setAtrributesToDialog(textItem);
	}
	
	protected void setItemsToDiaog() {
		setItemsToDiaog(textItem);
		textItem.setDimColor(this.getBoolean("dim?"));
		setComplexProperteisToDialog(ct);
}
	
	public static void main(String[] args) {
		ComplexTextGraphic g = new ComplexTextGraphic();
		new ComplexTextGraphicSwingDialog(g).showDialog();;
	}
	
	@Override
	public void objectEdited(ObjectEditEvent oee) {
		
		notifyAllListeners(null, oee.getKey());
		pack();
		
	}
	
	

}
