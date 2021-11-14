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
 * Version: 2021.2
 */
package exportMenus;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import applicationAdapters.DisplayedImage;
import figureFormat.DirectoryHandler;
import imageDisplayApp.ImageDisplayIOTest;
import logging.IssueLog;
import messages.ShowMessage;
import testing.FigureTester;
import testing.TestExample;
import testing.TestProvider;
import ultilInputOutput.FileChoiceUtil;

/**superclass for export tests*/
abstract class QuickExportTest {
	

	/**set to true if one want each file to be opened automatically.
	  Dye to dialogs comming up in powerpoint and other softwares, decided it best not to do this*/
	boolean opensFiles=false;
	boolean prompt=false;
	
	TestExample testExample=null;//which cases to test. set to null if all should be tested

	/**set to true if user will view files one by one as they are being created*/
	private boolean viewOnebyOne=false;

	@Test
	void test() throws Exception {
		IssueLog.sytemprint=true;
		PNGQuickExport.showDialogEverytime=false;
		exportTest();
		
		
		
		boolean yesOrNo = FileChoiceUtil.yesOrNo("Check the newly created files before clicking yes/no (they will be deleted). The exported images should look highly similar to the originals. are they?");
		assert yesOrNo;
		
		
	}

	/**creates the example and exports it (file format determined by the subclass)
	 * if there is not test example, iterates through a list of them)
	 * @throws Exception
	 * @throws IOException
	 */
	public void exportTest() throws Exception, IOException {
		QuickExport qe=createExporter();
		int count=1;
		ArrayList<String> createsFiles=new ArrayList<String>(); 
		ArrayList<TestProvider> testsCases = TestProvider.getTestProviderListWithfigures();
	
		for(TestProvider ex: testsCases) {
			if(testExample!=null &&testExample!=ex.getType()) { count++; continue;}
			long time=System.currentTimeMillis();
			IssueLog.log("starting test "+count);
			
			String variation ="";// " ("+Math.random()+")";//
			String testOutput = DirectoryHandler.getDefaultHandler().getTempFolderPath(qe.getExtension())+"/"+"Export Test "+count+" "+ex.getType().name()+variation+"."+qe.getExtension();
			
			File file = new File(testOutput);
			file.delete();
			file.deleteOnExit();
			DisplayedImage createExample = ex.createExample();
			
			createExample.getImageAsWorksheet().setTitle("Example "+ex.getType().ordinal()+" "+ex.getType().name());
			createExample.getImageAsWorksheet().setTitle(testOutput);
			if (prompt) {
				createExample .updateDisplay();
				createExample.getWindow().repaint();
				IssueLog.waitSeconds(5);
				ShowMessage.showMessages("Take a look at the new example. Will test export of this");
			}
			qe.saveInPath(createExample, testOutput);
			
			createsFiles.add(testOutput);
			
			
			IssueLog.log(System.currentTimeMillis()-time);
			IssueLog.log("ending test "+count+" "+ex.getType().name());
			IssueLog.log("Find saved file in "+file);
			count++;
			
			if (viewOnebyOne) {
			createExample.setZoomLevel(1);
			ImageDisplayIOTest.assertCompareWindows(createExample.getWindow(), this.createExporter().viewSavedFile(file));
			}
			
			FigureTester.closeAllWindows();
				}
		Desktop.getDesktop().open(new File(DirectoryHandler.getDefaultHandler().getTempFolderPath(qe.getExtension())+"/"));
		
		if (opensFiles)openFiles(createsFiles);
	}

	/**
	 * @return
	 */
	abstract QuickExport createExporter() ;

	/**
	 * @param createsFiles
	 * @throws IOException
	 */
	void openFiles(ArrayList<String> createsFiles) throws IOException {
		for(String testOutput:createsFiles) {
			
				Desktop.getDesktop().open(new File(testOutput));
			
		}
	}
	

	

}
