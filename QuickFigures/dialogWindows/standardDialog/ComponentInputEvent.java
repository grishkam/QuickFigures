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
 * Version: 2021.2
 */
package standardDialog;

import java.awt.Component;

/**A superclass for different types of standard dialog parts*/
public class ComponentInputEvent {

	private InputPanel sourcePanel;
	private Component component;
	protected String key;
	
	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}
	
	public InputPanel getSourcePanel() {
		return sourcePanel;
	}

	public void setSourcePanel(InputPanel sourcePanel) {
		this.sourcePanel = sourcePanel;
	}
	
	public void setKey(String key) {
		this.key=key;
		
	}
	
	public String getKey() {
		return key;
	}
}
