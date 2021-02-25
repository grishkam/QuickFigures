package standardDialog;


import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import ij.IJ;
import logging.IssueLog;
import standardDialog.colors.ColorDimmingBox;
import testing.TestingOptions;
import testing.VisualTest;

class ColorDimmingBoxTest  extends VisualTest{

	@Test
	void test() {
		
		JFrame jf = new JFrame();
		jf.setLocation(400, 400);
		ColorDimmingBox box = new ColorDimmingBox();
		jf.add(box);
		jf.pack();jf.setVisible(true);

		this.comboBoxVisualTest(box);
		jf.setVisible(false);
		
	}

}
