package standardDialog;


import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import ij.IJ;
import logging.IssueLog;
import standardDialog.colors.ColorDimmingBox;

class ColorDimmingBoxTest {

	@Test
	void test() {
		
		JFrame jf = new JFrame();
		jf.setLocation(400, 400);
		jf.add(new ColorDimmingBox());
		jf.pack();jf.setVisible(true);

		IssueLog.showMessage("you will see a window with a combo box, try clicking on it to ensure that the text colors appear at a few brightness levels");
		IJ.wait(12000);
		
		
	}

}
