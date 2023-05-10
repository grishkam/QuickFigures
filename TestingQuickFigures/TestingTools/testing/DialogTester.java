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
 * Date Modified: Feb 20, 2021
 * Version: 2023.2
 */
package testing;

import java.awt.Color;
import java.util.HashMap;

import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorComboBox;
import standardDialog.colors.ColorInputPanel;
import standardDialog.numbers.NumberInputPanel;

/**
 
 * 
 */
public class DialogTester {

	boolean printUpdates=true;
	boolean testInvalidNums=true;
	boolean testNumbers=true;
	protected boolean testAllcombinations=false;
	protected int testNCombinations=2;
	private boolean testBools=true;
	private boolean testColors;
	
	/**tests different combinations of settings for the chioces in the dialog
	 */
	protected void testCombinations(StandardDialog dialog) {
		HashMap<String, Object> inputs = dialog.getAllInputPanels();
		testCombinations(inputs, testNCombinations);
		dialog.revertAll();
	}

	/**
	 recursively tests different 
	 */
	private void testCombinations(HashMap<String, Object> inputs, int cycle) {
		for(String key1: inputs.keySet()) {
			Object o=inputs.get(key1);
			
				testInputCombinations(key1, o, inputs, cycle);
			
		}
	}
	
	
	
	/**
	 makes each combo box popup menu appear to they can be seen
	 */
	public static void testInputPanelApperance(StandardDialog d, int i) {
		HashMap<String, Object> inputs=d.getAllInputPanels();
		for(String key1: inputs.keySet()) {
			Object o=inputs.get(key1);
			if(o instanceof ChoiceInputPanel) {
				ChoiceInputPanel panel=(ChoiceInputPanel) o;
				if(panel.getBox() !=null) {
					VisualTest.comboBoxVisualTest(d, panel.getBox());
				}
				IssueLog.waitSeconds(i);
				IssueLog.log("popup menu from combo box will be seen "+panel.getName());
			}
			
		}
	}
	
	/**
	Tests all combinations of choices that a single input panel may have. If any combination of setting returns
	in an uncaught exception or an endless loop, this test would reveal it. The hashmap may contain 
	 * @param cycle 
	 */
	private void testInputCombinations(String key, Object inputPanel, HashMap<String, Object> inputs, int cycle) {
		boolean continueRecursion = testAllcombinations||cycle>1;
		if (inputPanel instanceof ChoiceInputPanel) {
			ChoiceInputPanel c=(ChoiceInputPanel) inputPanel;
			/**does not automatically test color combo boxes as that would entail a dialog*/
			if (c.getBox() instanceof ColorComboBox) {
				ColorComboBox c2=(ColorComboBox) c.getBox();
				c2.showsChooserDialog=false;
			}
			for(int i=0; i<c.getNChoices(); i++) {
				log("Combo box "+key + " set to "+i);
				c.setValue(i);
				
				/**tests all the other combinations of all the other panels*/
				if (continueRecursion)testCombinations(getMapWithout(key,inputPanel, inputs), cycle-1);
				
			}
			c.revert();
		}
		
		if (inputPanel instanceof NumberInputPanel &&testNumbers) {
			NumberInputPanel c=(NumberInputPanel) inputPanel;
			double n = c.getNumber();
			
			/**in many circumstances, 0 and negative numbers will be a nonsense value
			 If those nonsense values result in any infinite loops, uncaught exceptions or crashes
			 this would be a good change to learn*/
			double[] testNumber = new double[] {0, n*0.5, n*2, n*4, -2, n};
			if (!testInvalidNums) testNumber = new double[] {n*0.5, n*2, n*4, n};
			
			for(int i=0; i<testNumber.length; i++) {
				double testValue = testNumber[i];
				log("Number "+key + " set to "+testValue);
				c.setNumberAndNotify(testValue);
				
				/**tests all the other combinations of all the other panels*/
				if (continueRecursion)testCombinations(getMapWithout(key,inputPanel, inputs), cycle-1);
				
			}
			c.revert();
		}
		
		
		if (inputPanel instanceof BooleanInputPanel &&testBools) {
			BooleanInputPanel c=(BooleanInputPanel) inputPanel;
			
			c.setChecked(true);
			/**tests all the other combinations of all the other panels*/
			if (continueRecursion)testCombinations(getMapWithout(key,inputPanel, inputs), cycle-1);
			c.setChecked(false);
			/**tests all the other combinations of all the other panels*/
			if (continueRecursion)testCombinations(getMapWithout(key,inputPanel, inputs), cycle-1);
			c.revert();
			
		}
		
		
		if (inputPanel instanceof ColorInputPanel &&testColors) {
			ColorInputPanel c=(ColorInputPanel) inputPanel;
			Color[] testCaseColors =new Color[] { Color.black, Color.red, Color.green};
			for(Color color: testCaseColors ) {
				c.setSimulateSelectColor(color);
				/**tests all the other combinations of all the other panels*/
				if (continueRecursion)testCombinations(getMapWithout(key,inputPanel, inputs), cycle-1);
			}
			
		}
		
	}

	/**returns a version of the given map that lacks the object and key. used to move recursively accross the combinations*/
	private HashMap<String, Object> getMapWithout(String key, Object value, HashMap<String, Object> map ) {
		
		HashMap<String, Object> output=new HashMap<String, Object>();
		output.putAll(map);
		
		boolean containsKey = output.containsKey(key);
		
		
		output.remove(key, value);
		output.remove(key);

		boolean containsKeyEnd = output.containsKey(key);
		if (containsKey&&!containsKeyEnd) {}
			else
			if (containsKey&&containsKeyEnd) 
				IssueLog.showMessage("Key removal failed");
		
		return output;
	}
	
	void log(String st) {
		if (printUpdates) IssueLog.log(st);
	}
	
	void performChecks() {}
}
