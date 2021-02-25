package standardDialog;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import genericTools.ToolTester;
import ij.IJ;
import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.channels.ChannelEntryBox;
import testing.FigureTester;
import testing.TestingOptions;
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
	
			
			super.comboBoxVisualTest(box);
			
			
			
			//String st="you should see a windos with a channel entry box.try clicking on it. a colorfull menu should come up";
			//visualTestMessage(st);
		
			
	}

	

}
