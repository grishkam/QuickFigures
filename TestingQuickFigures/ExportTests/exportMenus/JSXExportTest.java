/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Mar 28, 2021
 * Version: 2022.0
 */
package exportMenus;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import applicationAdapters.DisplayedImage;
import logging.IssueLog;
import photoshopScripts.AdobeScriptGenerator;
import testing.TestExample;
import testing.TestProvider;
import ultilInputOutput.FileChoiceUtil;

/**a test to determine if code to generate adobe illustrator scripts works*/
 class JSXExportTest  {
	
	
	
	TestExample testCase=TestExample.DIVERSE_SHAPES;//which cases to test. set to null if all should be tested


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
