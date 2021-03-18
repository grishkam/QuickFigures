package exportMenus;

import java.io.IOException;
import java.util.ArrayList;

import appContextforIJ1.ImageDisplayTester;
import logging.IssueLog;
import testing.FigureTester;
import testing.TestExample;
import testing.TestProvider;

/**Shows the example figures that are used for the export tests*/
 class ShowExportExamples {
	
	
	
	static TestExample testCase=TestExample.MANY_ANGLE_COMPLEX_TEXT;//which cases to test. set to null if all should be tested




	/**
	 * @throws Exception
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception, IOException {
		ImageDisplayTester.startToolbars(true);
		IssueLog.sytemprint=true;
		ArrayList<TestProvider> testsCases = TestProvider.getStandardExportTestsAndImages();
		for(TestProvider t: FigureTester.getTests()) {
			testsCases.add(t);
		}
		for(TestProvider ex: testsCases) {
			if (testCase==null||testCase==ex.getType())
			ex.createExample();
			
				}
		
		
		
		IssueLog.waitSeconds(215);
		
	}

	

}
