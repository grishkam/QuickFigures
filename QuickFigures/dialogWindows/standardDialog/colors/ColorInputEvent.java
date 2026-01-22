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
 * Version: 2023.2
 */
package standardDialog.colors;

import java.awt.Color;
import java.awt.Component;

import applicationAdapters.CanvasMouseEvent;
import standardDialog.ComponentInputEvent;
import standardDialog.InputPanel;

/**A component input event for Colors*/
public class ColorInputEvent extends ComponentInputEvent {
	
	private Color color;
	public CanvasMouseEvent event;
	String colorString=null;

	
	public ColorInputEvent(InputPanel sourcePanel, Component component, Color number) {
		this.setSourcePanel(sourcePanel);
		this.setComponent(component);
		this.setColor(number);
	}
	
	public ColorInputEvent(InputPanel sourcePanel, Component component, String value) {
		this.setSourcePanel(sourcePanel);
		this.setComponent(component);
		colorString=value;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color number) {
		this.color = number;
	}
	
	public String getColorAsText() {
		return colorString;
	}

	public Object getColorObject() {
		if (colorString!=null)
			return colorString;
		
		return color;
	}


	
}
