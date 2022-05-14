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
 * Version: 2022.1
 */
package standardDialog.booleans;

import java.awt.Component;

import standardDialog.ComponentInputEvent;
import standardDialog.InputPanel;

/**A component input event for boolean values*/
public class BooleanInputEvent extends ComponentInputEvent {
	private boolean bool=false;
	
	
	public BooleanInputEvent(InputPanel panel, Component component, boolean number) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		setBool(number);
		
	}

	public boolean getBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	
}
