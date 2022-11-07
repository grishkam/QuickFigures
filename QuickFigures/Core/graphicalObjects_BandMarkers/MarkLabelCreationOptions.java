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
 * Date Created: April 22, 2022
 * Date Created: April 22, 2022
 * Version: 2022.2
 */
package graphicalObjects_BandMarkers;


import addObjectMenus.LaneLabelCreationOptions;
import layout.RetrievableOption;
import logging.IssueLog;
import objectDialogs.TextPatternDialog;
import storedValueDialog.StoredValueDilaog;
import textObjectProperties.TextPattern;

/**A set of properties that determine how figure labels are automatically generated*/
public class MarkLabelCreationOptions {
	
	/**The current label creation options*/
	public static MarkLabelCreationOptions current=new MarkLabelCreationOptions() ;
	
	public static final String numberCode="%number%", letterCode="%letter%";
	public static String defaultLabelText="mark "+numberCode;
	
	@RetrievableOption(key = "nMark", label="How many marks?")
	public double nMarks=5;
	
	@RetrievableOption(key = "label prefix and suffix", label="Label text here", nExpected=12)
	public String[] textOfLabel=new String[] {defaultLabelText};

	/**The pattern for numbers*/
	TextPattern pattern1=new TextPattern(); {pattern1.setSuffix("");pattern1.setPrefix("");}
	
	
	/**
	 * Shos the dialog which allods the user to choose how many lane labels to create
	 * @return true if user pressed ok
	 */
	public static boolean showLaneLabelDialog(MarkLabelCreationOptions markLabelOptions) {
		StoredValueDilaog storedValueDilaog = new StoredValueDilaog(markLabelOptions);
		storedValueDilaog .setModal(true);
		 storedValueDilaog.setTitle("How many lane labels?");
		 
		 /**Adds a text pattern tab*/
		 TextPatternDialog dis = new TextPatternDialog(markLabelOptions.pattern1, false, false);
		storedValueDilaog.addSubordinateDialog("%number%", dis);
		 
		storedValueDilaog.showDialog();
		
		markLabelOptions.pattern1=dis.getTheTextPattern();//sets the pattern based on the text pattern tab
		
		return storedValueDilaog.wasOKed();
	}
	
	/**determines the text for a particular label*/
	public static String determineTextForLabel(int laneIndex, MarkLabelCreationOptions markLabelOptions) {
		/**Sets the text of the label*/
		String[] labelList = markLabelOptions.textOfLabel;
		String text_for_label = LaneLabelCreationOptions.defaultLabelText;
		
	
		
		if(labelList!=null &&labelList.length>0) {
			text_for_label=labelList[(laneIndex-1)%labelList.length];
		} else if (labelList.length==0) 
			text_for_label="";
			
		text_for_label =text_for_label.replace(LaneLabelCreationOptions.numberCode, getTextForLaneNumber(laneIndex, markLabelOptions) );
		
		return text_for_label;
	}
	
	/**
	 * @param laneIndex
	 * @return
	 */
	public static String getTextForLaneNumber(int laneIndex, MarkLabelCreationOptions markLabelOptions) {
		return markLabelOptions.pattern1.getText(laneIndex);
	}

	
}
