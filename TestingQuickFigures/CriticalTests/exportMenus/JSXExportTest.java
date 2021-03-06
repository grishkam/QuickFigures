package exportMenus;

import java.awt.Desktop;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import applicationAdapters.DisplayedImage;
import figureFormat.DirectoryHandler;
import illustratorScripts.ZIllustratorScriptGenerator;
import imageDisplayApp.ImageDisplayIOTest;
import logging.IssueLog;
import photoshopScripts.AdobeScriptGenerator;
import testing.FigureTester;
import testing.TestProvider;
import ultilInputOutput.FileChoiceUtil;

/**a test to determine if code to generate adobe illustrator scripts works*/
 class JSXExportTest {
	
	private static final int ALL_ = 0;

	
	
	int testCase=12;//which cases to test


	@Test
	void test() throws Exception {
		IssueLog.sytemprint=true;
		
		exportTest();
		
		
		
	
	}

	/**
	 * @throws Exception
	 * @throws IOException
	 */
	public void exportTest() throws Exception, IOException {
		
		int count=1;
		ArrayList<TestProvider> testsCases = TestProvider.getStandardExportTestsImages();
		for(TestProvider t: FigureTester.getTests()) {
			testsCases.add(t);
		}
		for(TestProvider ex: testsCases) {
			if(testCase!=ALL_ &&testCase!=count) { count++; continue;}
			long time=System.currentTimeMillis();
			IssueLog.log("starting test "+count);
			AdobeScriptGenerator.outputFile=count+"output.jsx";
		
			DisplayedImage createExample = ex.createExample();
			new ExportIllustrator().performActionDisplayedImageWrapper(createExample);
			
			
			IssueLog.log(System.currentTimeMillis()-time);
			IssueLog.log("ending test "+count);
			count++;
			
			
				}
		
		
		assert(FileChoiceUtil.yesOrNo("run the script in adobe illustrator. determine if it worked before clicking yes"));
		
		IssueLog.waitSeconds(215);
		
	}

	

}
