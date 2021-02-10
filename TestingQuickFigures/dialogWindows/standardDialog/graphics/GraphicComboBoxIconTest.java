/**
 * Author: Greg Mazo
 * Date Modified: Jan 16, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
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

/**
 
 * 
 */
public class GraphicComboBoxIconTest {

	@Test
	public void test() {
		
		JFrame frame=new JFrame();
		
		addItemToFrame(frame, Color.red);
		addItemToFrame(frame, Color.blue);
		
		
		frame.pack();
		frame.setVisible(true);
		
		IssueLog.waitSeconds(30);
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
