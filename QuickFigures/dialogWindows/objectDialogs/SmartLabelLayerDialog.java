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
 * Date Created: Oct 24, 2021
 * Date Modified: Oct 25, 2021
 * Version: 2022.0
 * 
 */

package objectDialogs;

import graphicalObjects_LayerTypes.SmartLabelLayer;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.strings.InfoDisplayPanel;
import textObjectProperties.TextPattern;

/**An options dialog that allows the user to change the pattern for the labels*/
public class SmartLabelLayerDialog extends TextPatternDialog {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String constantUpdateKey="update", patternTypeKey="Index based on", previewKey="preview";;
	/**
	 * 
	 */

	
	
	private SmartLabelLayer labelLayer;
	InfoDisplayPanel previewPanel;
	
	public SmartLabelLayerDialog(SmartLabelLayer l, boolean includeData) {
		super(l.getTextPattern(), includeData, true);
		labelLayer=l;
		this.setTitle("Smart Label Options");
		addLayerOptionsToDialog();
		this.setWindowCentered(true);
		this.setModal(true);
	}
	
	public void addLayerOptionsToDialog() {
		this.add(constantUpdateKey, new BooleanInputPanel("Update Labels Constantly", labelLayer.isContinuouseUpdate())) ;
		previewPanel = new InfoDisplayPanel("Labels will show " ,theTextPattern.prefixAndSuffix(theTextPattern.getCurrentIndexSystem().getCode()));
		this.add(previewKey, previewPanel);
	
	}
	
	public void updatePreviewPanel() {
		if(previewPanel!=null) {
			TextPattern patternNew = labelLayer.getTextPattern();
			previewPanel.setContentText(patternNew .prefixAndSuffix(theTextPattern.getCurrentIndexSystem().getCode()));
		}
	}


	/**sets the pattern for this series of smart labels to the dialog*/
	public TextPattern setOptionsToDialog() {
		TextPattern theTextPattern = super.setOptionsToDialog();
		
		labelLayer.setTextPattern(theTextPattern);
		
		labelLayer.setContinuouseUpdate(this.getBoolean(constantUpdateKey));
		
		
		updatePreviewPanel();
		return theTextPattern;
	}

	protected void afterEachItemChange() {
		 onOK();
	}	
	
	/**what action to take when the ok button is pressed*/
	protected void onOK() {
		setOptionsToDialog();
		labelLayer.updateLabels();
		labelLayer.updateDisplay();
	}

	
	
}