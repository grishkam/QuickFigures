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
package exportMenus;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;

import appContextforIJ1.ImageDisplayTester;
import applicationAdapters.DisplayedImage;
import applicationAdapters.StartApplication;
import basicMenusForApp.BasicMenuItemForObj;
import basicMenusForApp.MenuBarForApp;
import basicMenusForApp.MenuBarItemInstaller;
import figureFormat.DirectoryHandler;
import imageDisplayApp.ImageWindowAndDisplaySet;
import includedToolbars.ObjectToolset1;
import includedToolbars.QuickFiguresToolBar;
import includedToolbars.ToolInstallers;
import logging.IssueLog;
import testing.TestExample;
import testing.TestProvider;

/**work in progress, gives user a means to test out export examples on their own machine
 * however examples created in this way do not export*/
public class runManyTests  extends StartApplication  implements MenuBarItemInstaller, ToolInstallers{

	
	public static void main(String[] args) {
		IssueLog.sytemprint=false;
		IssueLog.windowPrint=true;
		install();
		startToolbars(true);
		ImageDisplayTester.setupImageJ();
		 ImageWindowAndDisplaySet.createAndShowNew("Figure", 400,300);
		
	}
	
	public static void install() {
		
		runManyTests freeRun = new  runManyTests();
		MenuBarForApp.addMenuBarItemInstaller(freeRun);
		ObjectToolset1.includeBonusTool(new runManyTests());
		
		
		
		
	}
	
	@Override
	public void installTools(QuickFiguresToolBar toolset) {
		
		
	}

	@Override
	public void addToMenuBar(MenuBarForApp installer) {
		
		installer.installItem(new TestEveryExport(null));
		for(TestExample e: TestExample.values())
			installer.installItem(new TestEveryExport(e));
		
	}
	
	 class TestEveryExport extends BasicMenuItemForObj {
	
	private TestExample testCase=TestExample.MANY_ANGLE_COMPLEX_TEXT;


	/**
	 * @param manyAngleComplexText
	 */
	public TestEveryExport(TestExample manyAngleComplexText) {
		 testCase= manyAngleComplexText;
	}


	@Override
	public String getMenuPath() {
		return "File<Test Export";
	}
	
	
	@Override
	public String getNameText() {
		if (testCase==null) return "try several export types";
		return "Show Example: "+testCase.name();
	}
	

	/**Allows the user to select a test example then tests its export
	 * it opens the example but does not properly export*/
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		try {
		/**StandardDialog sd = new StandardDialog("Choose example");
			sd.setModal(true);
			
			ChoiceInputPanel panel = ChoiceInputPanel.buildForEnum("Select Test", TestExample.values(), testCase);
			
			sd.add("Choose ", panel);
			sd.showDialog();
			
			this.testCase=TestExample.values()[sd.getChoiceIndex("Choose ")];
			
			
			/**
			 * 
			 */
			ArrayList<TestProvider> testsCases = TestProvider.getTestProviderListWithfigures();
			
			for(TestProvider ex: testsCases) {
				if (ex.getType()==testCase) {
					diw.getWindow().setVisible(false);
					 ex.createExample();}
					
					
					/**quickExport = new ExportIllustrator();
					quickExport.saveInPath(diw, testOutput+quickExport.getExtension());
					*/
				
			}
			
			if (testCase==null) {
				
				String pathFolder = DirectoryHandler.getDefaultHandler().getTempFolderPath("user export test")+"/";
				String testOutput = pathFolder+diw.getImageAsWorksheet().getTitle()+".";
				QuickExport quickExport = new SVGQuickExport();
				quickExport.saveInPath(diw, testOutput+quickExport.getExtension());
				quickExport = new PDFQuickExport(false);
				quickExport.saveInPath(diw, testOutput+quickExport.getExtension());
				quickExport = new EPSQuickExport(false);
				quickExport.saveInPath(diw, testOutput+quickExport.getExtension());
				quickExport = new PPTQuickExport(false);
				quickExport.saveInPath(diw, testOutput+quickExport.getExtension());
				quickExport = new PNGQuickExport(false);
				quickExport.saveInPath(diw, testOutput+quickExport.getExtension());
				quickExport = new TiffQuickExport(false);
				quickExport.saveInPath(diw, testOutput+quickExport.getExtension());
				
				Desktop.getDesktop().open(new File(pathFolder));
			}
			
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	}
	
	 }

}
