/**
 * Author: Greg Mazo
 * Date Modified: Jan 8, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
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
