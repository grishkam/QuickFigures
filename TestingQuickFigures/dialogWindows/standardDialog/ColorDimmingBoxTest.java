package standardDialog;


import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import standardDialog.colors.ColorDimmingBox;
import testing.VisualTest;

class ColorDimmingBoxTest  extends VisualTest{

	@Test
	void test() {
		
		JFrame jf = new JFrame();
		jf.setLocation(400, 400);
		ColorDimmingBox box = new ColorDimmingBox();
		jf.add(box);
		jf.pack();jf.setVisible(true);

		comboBoxVisualTest(jf, box);
		jf.setVisible(false);
		
	}

}
