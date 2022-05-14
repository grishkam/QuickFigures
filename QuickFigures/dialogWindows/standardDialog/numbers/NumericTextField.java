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
package standardDialog.numbers;

import javax.swing.JTextField;


/**Just a text field that keeps track of a number input*/
public class NumericTextField extends JTextField  {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double originalnum=0;
	private int decimalplaces=3;
	

	public NumericTextField(double number, int deci) {
		super(number+"");
		setDecimalPlaces(deci);
	
		setNumber(number);
	
	}
	
	public NumericTextField(double number) {
		this(number, 0);
	}
	
	public void setNumber(double d) {
		originalnum=d;
		this.setText(numberToString(d));
		
	}
	
	
	public String numberToString(double d) {
		double factor = Math.pow(10, getDecimalPlaces());
		int d2=(int)(d*factor);
		return ""+ (d2/factor);
		
	}
	
	public double getNumberFromField() {
		String st=this.getText();
		try {
			
			double out = Double.parseDouble(st);
			if (Double.isNaN(out)) return originalnum;
			originalnum=out;
			return  originalnum;
		} catch (Throwable t) {
			return originalnum;
		}
	}
	
	public boolean isBlank() {
		return this.getText().trim().equals("");
	}

	public int getDecimalPlaces() {
		return decimalplaces;
	}

	public void setDecimalPlaces(int decimalplaces) {
		this.decimalplaces = decimalplaces;
		this.setColumns(getDecimalPlaces()+4);
	}
	
	
	
}
