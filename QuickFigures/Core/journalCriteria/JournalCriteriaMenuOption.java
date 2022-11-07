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
 * Date Modified: Feb 22, 2021
 * Version: 2022.2
 */
package journalCriteria;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import messages.ShowMessage;
import selectedItemMenus.BasicMultiSelectionOperator;

/**A menu option that allows the user to input speficic criteria
 * and apply them to all objects in a figure */
public class JournalCriteriaMenuOption extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public JournalCriteriaMenuOption() {
	}

	@Override
	public String getMenuCommand() {
		return "Impose Journal Limits";
	}
	

	@Override
	public void run() {
		ArrayList<ZoomableGraphic> arrayTaret = getAllArray();
		if (arrayTaret.size()==0) {
			boolean result = ShowMessage.showOptionalMessage("none selected", true, "no objects are selected, do you want to target all objects?");
			if(result) {
				arrayTaret=super.getSelector().getImageWrapper().getTopLevelLayer().getAllGraphics();
			}
			else return;
		}
		new FormatOptionsDialog(arrayTaret, new JournalCriteria()).showDialog();

	}

}
