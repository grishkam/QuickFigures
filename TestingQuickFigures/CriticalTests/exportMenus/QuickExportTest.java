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
import testing.FigureTester;
import testing.TestExample;
import testing.TestProvider;
import ultilInputOutput.FileChoiceUtil;

abstract class QuickExportTest {
	

	/**set to true if one want each file to be opened automatically.
	  Dye to dialogs comming up in powerpoint and other softwares, decided it best not to do this*/
	boolean opensFiles=false;
	
	TestExample testCase=TestExample.RECTANGLE_AND_OTHERS;//which cases to test. set to null if all should be tested

	/**set to true if user will test files one by one*/
	private boolean viewOnebyOne=true;

	@Test
	void test() throws Exception {
		IssueLog.sytemprint=true;
		PNGQuickExport.showDialogEverytime=false;
		exportTest();
		
		
		
		assert(FileChoiceUtil.yesOrNo("Check the newly created files before clicking yes/no. The exported images should look highly similar to the originals. are they?"));
		
		
	}

	/**
	 * @throws Exception
	 * @throws IOException
	 */
	public void exportTest() throws Exception, IOException {
		QuickExport qe=createExporter();
		int count=1;
		ArrayList<String> createsFiles=new ArrayList<String>(); 
		ArrayList<TestProvider> testsCases = TestProvider.getStandardExportTestsImages();
		for(TestProvider t: FigureTester.getTests()) {
			testsCases.add(t);
		}
		for(TestProvider ex: testsCases) {
			if(testCase!=null &&testCase!=ex.getType()) { count++; continue;}
			long time=System.currentTimeMillis();
			IssueLog.log("starting test "+count);
			String testOutput = DirectoryHandler.getDefaultHandler().getTempFolderPath(qe.getExtension())+"/"+"Export Test "+count+ex.getType().name()+"("+Math.random()+")."+qe.getExtension();
			
			File file = new File(testOutput);
			file.delete();
			file.deleteOnExit();
			DisplayedImage createExample = ex.createExample();
			createExample.getImageAsWrapper().setTitle("Example "+ex.getType().ordinal()+" "+ex.getType().name());
			createExample.getImageAsWrapper().setTitle(testOutput);
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
