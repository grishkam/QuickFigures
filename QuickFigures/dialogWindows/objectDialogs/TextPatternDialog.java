/**
 * Author: Greg Mazo
 * Date Created: Oct 25, 2021
 * Date Modified: Oct 25, 2021
 * Version: 2021.2
 * 
 */

package objectDialogs;

import java.util.ArrayList;

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
	
	public TextPatternDialog(TextPattern l,boolean includeType) {
		this.theTextPattern=l;
		this.includeDataType=includeType;
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
		
		this.add(prefixKey, new StringInputPanel(prefixKey, theTextPattern.getPrefix()));

		this.add(suffixKey, new StringInputPanel(suffixKey, theTextPattern.getSuffix()));
		
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
		 theTextPattern.setPrefix(this.getString(prefixKey));
		 theTextPattern.setSuffix(this.getString(suffixKey));
		 theTextPattern.setCountBy(this.getNumberInt(countByKey));
		 
		 if (includeDataType)
			 theTextPattern.setCurrentIndexSystem(SmartLabelDataType.values()[getChoiceIndex(patternTypeKey)]);
		return theTextPattern;
	}
	
	/**what action to take when the ok button is pressed*/
	protected void onOK() {
		setOptionsToDialog();
		
	}
	
	/**Shows a modal dialog and returns the pattern*/
	public static TextPattern getPatternFromUser(TextPattern input, boolean includeData) {
		TextPatternDialog dialog = new TextPatternDialog(input,includeData);
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