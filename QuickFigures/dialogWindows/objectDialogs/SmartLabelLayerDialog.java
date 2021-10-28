/**
 * Author: Greg Mazo
 * Date Created: Oct 24, 2021
 * Date Modified: Oct 25, 2021
 * Version: 2021.2
 * 
 */

package objectDialogs;

import graphicalObjects_LayerTypes.SmartLabelLayer;
import standardDialog.booleans.BooleanInputPanel;
import textObjectProperties.TextPattern;

/**An options dialog that allows the user to change the pattern for the labels*/
public class SmartLabelLayerDialog extends TextPatternDialog {

	
	public static final String constantUpdateKey="update";;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private SmartLabelLayer labelLayer;
	
	public SmartLabelLayerDialog(SmartLabelLayer l) {
		super(l.getTextPattern());
		labelLayer=l;
		this.setTitle("Smart Label Options");
		addLayerOptionsToDialog();
		this.setWindowCentered(true);
		this.setModal(true);
	}
	
	public void addLayerOptionsToDialog() {
		this.add(constantUpdateKey, new BooleanInputPanel("Update Labels Constantly", labelLayer.isContinuouseUpdate())) ;
		
	}


	/**sets the pattern for this series of smart labels to the dialog*/
	public TextPattern setOptionsToDialog() {
		TextPattern theTextPattern = super.setOptionsToDialog();
		
		labelLayer.setTextPattern(theTextPattern);
		
		labelLayer.setContinuouseUpdate(this.getBoolean(constantUpdateKey));
		
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