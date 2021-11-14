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
package standardDialog.graphics;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.junit.Test;

import graphicalObjects_Shapes.RectangularGraphic;
import logging.IssueLog;
import testing.VisualTest;

/**
 this test verifies the appearance of the icons for the combo box
 */
public class GraphicComboBoxIconTest extends VisualTest {

	@Test
	public void test() {
		
		JFrame frame=new JFrame();
		
		addItemToFrame(frame, Color.red);
		addItemToFrame(frame, Color.blue);
		
		
		frame.pack();
		frame.setVisible(true);
		
		IssueLog.waitSeconds(5);
		frame.setVisible(false);
	}

	/**
	 * @param frame
	 */
	private void addItemToFrame(JFrame frame, Color c) {
		RectangularGraphic r1 = RectangularGraphic.blankRect(new Rectangle(20,0, 300,200),c);
		
		
		
		JLabel l = new JLabel("has rect icon");
		l.setIcon(new GraphicComboBoxIcon(r1));
		frame.add(l);
	}

}
