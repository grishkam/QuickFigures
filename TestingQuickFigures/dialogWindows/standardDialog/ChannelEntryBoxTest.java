package standardDialog;

import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import ij.IJ;
import logging.IssueLog;
import standardDialog.channels.ChannelEntryBox;
import testing.FigureTester;

/**this contains a manual test to confirm the appearance of the channel entry box */
class ChannelEntryBoxTest {

	@Test
	void test() {
		
			JFrame jf = new JFrame();
			;
			ImagePlusWrapper wrap = new ImagePlusWrapper(FigureTester.openExample1(1));
			
			jf.add(new ChannelEntryBox(2,wrap.getChannelEntriesInOrder()));
			jf.pack();jf.setVisible(true);
			jf.setLocation(350, 350);
		
		IssueLog.showMessage("you should see a windos with a channel entry box.try clicking on it. a colorfull menu should come up");
		IJ.wait(15000);
	}

}
