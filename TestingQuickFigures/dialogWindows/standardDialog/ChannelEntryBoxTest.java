package standardDialog;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import standardDialog.channels.ChannelEntryBox;
import testing.FigureTester;
import testing.VisualTest;

/**this contains a manual test to confirm the appearance of the channel entry box */
class ChannelEntryBoxTest extends VisualTest {

	@Test
	void test() {
		
			JFrame jf = new JFrame();
			;
			ImagePlusWrapper wrap = new ImagePlusWrapper(FigureTester.openExample1(1));
			
			JComboBox<?> box = new ChannelEntryBox(2,wrap.getChannelEntriesInOrder());
			jf.add(box);
			jf.pack();
			jf.setVisible(true);
			jf.setLocation(350, 350);
	
			
			super.comboBoxVisualTest(jf, box);
			
			jf.setVisible(false);
			
			
		
			
	}

	

}
