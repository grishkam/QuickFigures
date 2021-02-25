/**
 * Author: Greg Mazo
 * Date Modified: Jan 11, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package standardDialog.numbers;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.junit.Test;

import logging.IssueLog;
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
