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
 * Version: 2023.1
 */
package standardDialog.choices;

import java.awt.Component;

import standardDialog.ComponentInputEvent;
import standardDialog.InputPanel;

/**A component input event for choices*/
public class ChoiceInputEvent extends ComponentInputEvent {

	private int number;
	private Object chosen;
	
	
	public ChoiceInputEvent(InputPanel panel, Component component, int number, Object chosenObject) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		this.setIndex(number);
		setChosenObject(chosenObject);
	}



	public double getChoiceIndex() {
		return number;
	}

	public void setIndex(int number) {
		this.number = number;
	}



	public Object getChosenObject() {
		return chosen;
	}



	public void setChosenObject(Object chosen) {
		this.chosen = chosen;
	}



	
	


}
