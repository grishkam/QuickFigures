package exportMenus;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import applicationAdapters.DisplayedImage;
import logging.IssueLog;
import photoshopScripts.AdobeScriptGenerator;
import testing.FigureTester;
import testing.TestExample;
import testing.TestProvider;
import ultilInputOutput.FileChoiceUtil;

/**a test to determine if code to generate adobe illustrator scripts works*/
 class JSXExportTest {
	
	
	
	TestExample testCase=TestExample.MANY_ANGLE_COMPLEX_TEXT;//which cases to test. set to null if all should be tested


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
		ArrayList<TestProvider> testsCases = TestProvider.getStandardExportTestsAndImages();
		for(TestProvider t: FigureTester.getTests()) {
			testsCases.add(t);
		}
		for(TestProvider ex: testsCases) {
			if(testCase!=null &&testCase!=ex.getType()) { count++; continue;}
			long time=System.currentTimeMillis();
			IssueLog.log("starting test "+count);
			AdobeScriptGenerator.outputFile=count+"output.jsx";
			AdobeScriptGenerator.outputFile2=ex.getType().name()+count+"output.ai";
		
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
