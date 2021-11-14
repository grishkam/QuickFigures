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
package standardDialog.numbers;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

import testing.TestingOptions;
import testing.VisualTest;

/**
 test to determine if angle box appearance is normal.
 Use may also drag to test if angle box responds to mouse drags with changes
 to the angle
 */
public class AngleBoxTest extends VisualTest {

	//@Test//does not need to be regularly tested
	public void test() {
		JFrame ff = new JFrame("frame");
		ff.setLayout(new FlowLayout());
		ff.add(new JButton("button"));
		
		ff.add(new AngleBox2(45));
		ff.pack();
		
		ff.setVisible(true);
		
		/**enough time for the human tester to see how it looks*/
		super.visualTestMessage("look at the window with the angle box");
		TestingOptions.waitTimeAfterVisualTests();
		ff.setVisible(false);
	}
	
	

}
