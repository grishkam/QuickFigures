package appContextforIJ1;

import org.junit.jupiter.api.Test;

import ij.IJ;
import logging.IssueLog;

class ImageDisplayTesterTest {

	@Test
	void test() {
		ImageDisplayTester.main(null);
		IssueLog.showMessage("If this test works you should see ImageJ and the quickfigures toolbars appear. "
				+'\n'+ "They will close in 10 seconds");
		IJ.wait(10000);
	}

}
