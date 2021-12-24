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
 * Date Created: Oct 25, 2021
 * Date Modified: Oct Dec 23, 2021
 * Version: 2021.2
 * 
 */

package objectDialogs;

import java.util.ArrayList;

import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.StringInputPanel;
import textObjectProperties.SmartLabelDataType;
import textObjectProperties.TextPattern;

/**An options dialog that allows the user to change the pattern for the labels*/
public class TextPatternDialog extends StandardDialog {

	
	public static final String patternKey="pattern",
			constantUpdateKey="update",
			startIndexKey="Start at", countByKey="Count by", patternTypeKey="Index based on",
			prefixKey="prefix", suffixKey="suffix";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	ArrayList<TextPattern> patterns;
	protected TextPattern theTextPattern;
	private boolean includeDataType=true;
	private boolean includePrefixAndSuffix=true;
	
	public TextPatternDialog(TextPattern l,boolean includeType, boolean prefixAndSuffix) {
		this.theTextPattern=l;
		this.includeDataType=includeType;
		this.includePrefixAndSuffix=prefixAndSuffix;
		this.setTitle("Smart Label Options");
		this.addOptionsToDialog();
		this.setWindowCentered(true);
		this.setModal(true);
	}
	
	public void addOptionsToDialog() {
	
		
		addTextPatternToDialog(theTextPattern);
		
		
		
	}

	/**adds fields for the label pattern to be input
	 * @param theTextPattern
	 */
	protected void addTextPatternToDialog(TextPattern theTextPattern) {
		patterns=TextPattern.getList();
		if(theTextPattern!=null)
			patterns.add(0, theTextPattern);
		else theTextPattern=patterns.get(0);
		String[] patternOption=new String[patterns.size()];
		for(int i=0; i<patternOption.length; i++) {
			patternOption[i]=patterns.get(i).getSummary();
		}
		ChoiceInputPanel patternCombo = new ChoiceInputPanel("Select Pattern", patternOption,0);
		
		if (includeDataType)
			this.add(patternTypeKey, ChoiceInputPanel.buildForEnum("Label Index Derived From",SmartLabelDataType.values(),theTextPattern.getCurrentIndexSystem()));
		
		this.add(patternKey, patternCombo);
		
		
	
		
		this.add(startIndexKey, new NumberInputPanel("Start at", theTextPattern.getStartIndex(), 3));
		this.add(countByKey, new NumberInputPanel(countByKey, theTextPattern.getCountBy(), 3));
		
		if(includePrefixAndSuffix) {
			this.add(prefixKey, new StringInputPanel(prefixKey, theTextPattern.getPrefix()));
	
			this.add(suffixKey, new StringInputPanel(suffixKey, theTextPattern.getSuffix()));
		}
		
	}
	
	public TextPattern setOptionsToDialog() {
		theTextPattern=getTextPatternFromDialog();
		return theTextPattern;
		
	}

	/**returns a TextPattern object based on the dialog
	 * @return
	 */
	protected TextPattern getTextPatternFromDialog() {
		TextPattern theTextPattern = patterns.get((int) this.getChoiceIndex(patternKey));
		 theTextPattern.setStartIndex(this.getNumberInt(startIndexKey));
			if(includePrefixAndSuffix) {
				 theTextPattern.setPrefix(this.getString(prefixKey));
				 theTextPattern.setSuffix(this.getString(suffixKey));
			}
		 theTextPattern.setCountBy(this.getNumberInt(countByKey));
		 
		 if (includeDataType)
			 theTextPattern.setCurrentIndexSystem(SmartLabelDataType.values()[getChoiceIndex(patternTypeKey)]);
		return theTextPattern;
	}
	
	/**what action to take when the ok button is pressed*/
	protected void onOK() {
		setOptionsToDialog();
		
	}
	
	/**Each time a user changes a dialog option*/
	protected void afterEachItemChange() {
		
		setOptionsToDialog();
	}	
	
	/**Shows a modal dialog and returns the pattern*/
	public static TextPattern getPatternFromUser(TextPattern input, boolean includeData) {
		TextPatternDialog dialog = new TextPatternDialog(input,includeData, true);
		dialog.showDialog();
		
		return dialog.getTheTextPattern();
	}

	public TextPattern getTheTextPattern() {
		return theTextPattern;
	}

	public void setTheTextPattern(TextPattern theTextPattern) {
		this.theTextPattern = theTextPattern;
	}
	
}