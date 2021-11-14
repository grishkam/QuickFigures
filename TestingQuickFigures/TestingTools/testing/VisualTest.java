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
 
 * 
 */
package testing;

import java.awt.Window;

import javax.swing.JComboBox;
import logging.IssueLog;
import messages.ShowMessage;

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
	public static void comboBoxVisualTest(Window jf, JComboBox<?> sb) {
		jf.setVisible(true);
		sb.showPopup();
         sb.setPopupVisible(true);
		
		IssueLog.waitSeconds(2);
	}
}
