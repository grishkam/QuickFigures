/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package journalCriteria;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import selectedItemMenus.BasicMultiSelectionOperator;

public class JournalCriteriaMenuOption extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public JournalCriteriaMenuOption() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Impose Journal Limits";
	}
	

	@Override
	public void run() {
		ArrayList<ZoomableGraphic> arrayTaret = getAllArray();
		new FormatOptionsDialog(arrayTaret, new JournalCriteria()).showDialog();

	}

}
