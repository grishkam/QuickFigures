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
 
 * 
 */
package standardDialog.choices;

import java.awt.Window;

import javax.swing.JFrame;

import org.junit.Test;

import standardDialog.GriddedPanel;
import standardDialog.StandardDialog;
import testing.VisualTest;

/**
 Tests to determine if a combo box based on the names of an enum appears
 */
public class ChoiceInputPanelTest  extends VisualTest{

	public static enum MOCK {
		TEST_1, TEST_2_, TEST_FOUR;
		}
	
	@Test
	public void test() {
		
		String[] names = ChoiceInputPanel.enumNames(MOCK.values());
		
		Window w=new JFrame("Input panel");
		w.setLocation(1000, 1000);
		GriddedPanel panel = new GriddedPanel();
		w.add(panel);
		StandardDialog.center(w);
		
		ChoiceInputPanel c = new ChoiceInputPanel("Input Test", names, 0);
		c.placeItems(panel, 0, 0);
		w.pack();
		w.setVisible(true);
		
		super.comboBoxVisualTest(w, c.box);
		
		w.setVisible(false);
	
	}

}
