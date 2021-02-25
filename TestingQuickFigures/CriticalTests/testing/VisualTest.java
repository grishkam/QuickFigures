/**
 * Author: Greg Mazo
 * Date Modified: Feb 24, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package testing;

import javax.swing.JComboBox;

import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.choices.GraphicComboBox;

/**
 superclass for several tests that rely on the human to visually confirm 
 that the appearance of an item is correct
 */
public class VisualTest {
	/**
	 * @param st
	 */
	public static void visualTestMessage(String st) {
		ShowMessage.showOptionalMessage("Visual Test", false, st);
	}
	
	/**
	 * @param sb
	 */
	public void comboBoxVisualTest(JComboBox<?> sb) {
		sb.showPopup();
         sb.setPopupVisible(true);
		
		IssueLog.waitSeconds(10);
	}
}
