/**
 * Author: Greg Mazo
 * Date Modified: Mar 28, 2021
 * Version: 2021.1
 */
package exportMenus;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import basicMenusForApp.MenuItemForObj;
import logging.IssueLog;
import photoshopScripts.AdobeScriptGenerator;
import testing.FigureTester;
import testing.TestExample;
import testing.TestProvider;
import ultilInputOutput.FileChoiceUtil;

/**a test to determine if code to generate adobe illustrator scripts works*/
 class JSXExportTest  {
	
	
	
	TestExample testCase=TestExample.COLUMN_PLOTS;//which cases to test. set to null if all should be tested


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
		ArrayList<TestProvider> testsCases =TestProvider.getTestProviderListWithfigures();
		
		ArrayList<DisplayedImage> examples=new ArrayList<DisplayedImage>();
		
		for(TestProvider ex: testsCases) {
			if(testCase!=null &&testCase!=ex.getType()) { count++; continue;}
			long time=System.currentTimeMillis();
			IssueLog.log("starting test "+count);
			AdobeScriptGenerator.outputFile=count+"output.jsx";
			AdobeScriptGenerator.outputFile2="Export Test "+count+" "+ex.getType().name()+count+".ai";
			String st = "Export Test "+count+" "+ex.getType().name();
			
			
			DisplayedImage createExample = ex.createExample();
			createExample.getImageAsWorksheet().setTitle(st);
			examples.add(createExample);
			
			
			IssueLog.log(System.currentTimeMillis()-time);
			IssueLog.log("ending test "+count);
			count++;
			
			
				}
		
		new ExportIllustrator().createInIllustrator(AdobeScriptGenerator.destinationFolder(""), examples);
		
		assert(FileChoiceUtil.yesOrNo("run the script in adobe illustrator. determine if it worked before clicking yes"));
		
		IssueLog.waitSeconds(15);
		
	}

	
	

	

}
