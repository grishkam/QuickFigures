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
 * Version: 2021.1
 */
package standardDialog.numbers;

import java.awt.Component;

import javax.swing.JPanel;

import standardDialog.ComponentInputEvent;

/**A component input event for one or more number values*/
public class NumberInputEvent extends ComponentInputEvent {

	private double number;
	private float[] numbers;

	public NumberInputEvent(JPanel panel, Component component, double number) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		this.setNumber(number);
	}

	public NumberInputEvent(JPanel panel, Component component, float[] numbers) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		this.setNumber(numbers[0]);
		this.numbers=numbers;
	}
	
	public NumberInputEvent(JPanel panel, Component component, double num, float[] numbers) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		this.setNumber(num);
		this.numbers=numbers;
	}

	public double getNumber() {
		return number;
	}
	public float[] getNumbers() {
		if(numbers==null)
			return new float[] {(float) number};
		return numbers;
	}
	

	public void setNumber(double number) {
		this.number = number;
	}




}
