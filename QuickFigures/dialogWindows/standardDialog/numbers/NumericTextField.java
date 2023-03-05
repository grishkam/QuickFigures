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
package standardDialog.numbers;

import java.util.HashMap;

import javax.swing.JTextField;

import logging.IssueLog;


/**Just a text field that keeps track of a number input*/
public class NumericTextField extends JTextField  {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double originalnum=0;
	private int decimalplaces=3;
	private HashMap<String, String> contantMap;
	

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
	
	/**Sets the text of the field*/
	public String numberToString(double d) {
		double factor = Math.pow(10, getDecimalPlaces());
		int d2=(int)(d*factor);
		return ""+ (d2/factor);
		
	}
	
	/**Gets the number currently set by the field*/
	public double getNumberFromField() {
		String st=this.getText();
		try {
			
			double out = translateStringValueToNumber(st);
			if (Double.isNaN(out)) return originalnum;
			originalnum=out;
			return  originalnum;
		} catch (Throwable t) {
			return originalnum;
		}
	}

	/**converts the text to the number it represents 
	 * @param st
	 * @return
	 */
	public double translateStringValueToNumber(String st) {
		st=interpretConstant(st);
		return Double.parseDouble(st);
	}
	
	/**Translates the constant
	 * @return
	 */
	private String interpretConstant(String st) {
		if(getConstantMap()==null)
			return st;
		String m=getConstantMap().get(st);
		if(m!=null) {
			
			return m;
			}
		return st;
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

	/**returns the map of constants*/
	public HashMap<String, String> getConstantMap() {
		return contantMap;
	}

	public void setContantMap(HashMap<String, String> contantMap) {
		this.contantMap = contantMap;
	}
	
	/**Sets a constant map for this field*/
	public void setContantMap(String[] contantMapValues) {
		this.contantMap = new HashMap<String, String>();
		for(int i=1; i<contantMapValues.length; i+=2) {
			String key = contantMapValues[i-1];
			String value = contantMapValues[i];
			contantMap.put(key, value);
			if(			translateStringValueToNumber(value)==this.getNumberFromField()) {
				this.setText(key);
			}
		}
		
	}
	
}
