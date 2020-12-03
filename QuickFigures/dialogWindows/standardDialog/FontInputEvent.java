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
package standardDialog;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JPanel;

public class FontInputEvent extends ComponentInputEvent {
	
	private Font font;

	
	public FontInputEvent(JPanel sourcePanel, Component component, Font number) {
		this.setSourcePanel(sourcePanel);
		this.setComponent(component);
		this.setNumber(number);
	}

	

	public Font getNumber() {
		return font;
	}

	public void setNumber(Font number) {
		this.font = number;
	}



	
}
