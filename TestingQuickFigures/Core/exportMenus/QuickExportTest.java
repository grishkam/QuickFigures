package exportMenus;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import applicationAdapters.DisplayedImage;
import figureFormat.DirectoryHandler;
import ij.IJ;
import testing.FigureTester;
import testing.TestProvider;
import ultilInputOutput.FileChoiceUtil;

abstract class QuickExportTest {
	
	private static final int ALL_CASES = 0;

	boolean opensFiles=false;
	
	int testCase=ALL_CASES;//which cases to test

	@Test
	void test() throws Exception {
		QuickExport qe=createExporter();
		int count=1;
		ArrayList<String> createsFiles=new ArrayList<String>(); 
		ArrayList<TestProvider> testsCases = TestProvider.getStandardExportTestsImages();
		for(TestProvider t: FigureTester.getTests()) {
			testsCases.add(t);
		}
		for(TestProvider ex: testsCases) {
			if(testCase!=ALL_CASES &&testCase!=count) { count++; continue;}
			long time=System.currentTimeMillis();
			System.out.println("starting test "+count);
			String testOutput = DirectoryHandler.getDefaultHandler().getTempFolderPath(qe.getExtension())+"/"+count+" Export Test "+count+"("+Math.random()+")."+qe.getExtension();
			
			File file = new File(testOutput);
			file.delete();
			file.deleteOnExit();
			DisplayedImage createExample = ex.createExample();
			createExample.getImageAsWrapper().setTitle(testOutput);
			qe.saveInPath(createExample, testOutput);
			
			createsFiles.add(testOutput);
			
			
			System.out.println(System.currentTimeMillis()-time);
			System.out.println("ending test "+count);
			System.out.println("Find saved file in "+file);
			count++;
				}
		Desktop.getDesktop().open(new File(DirectoryHandler.getDefaultHandler().getTempFolderPath(qe.getExtension())+"/"));
		assert(FileChoiceUtil.yesOrNo("Check the newly created files before clicking yes/no. The exported images should look highly similar to the originals. are they?"));
		
		if (opensFiles)openFiles(createsFiles);
		
		IJ.wait(400);
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
