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

import basicMenusForApp.MenuBarForApp;
import genericTools.NormalToolDragHandler;
import ij.plugin.PlugIn;
import logging.IssueLog;
import messages.ShowMessage;

/**This class is used by imageJ to add the ploth package items to QuickFigures*/
public class PDFImporter_ implements PlugIn {
	static boolean firstRun=true;

	@Override
	public void run(String arg0) {
		IssueLog.log("QuickFigures: PDF importer installing");
		if (firstRun) {
			onFirstRun();
			firstRun=false;
			new Toolset_Runner().run("Object Tools");
		}
		

	}
	
	
	/**Called the frist time a plot tool installer is run*/
	private void onFirstRun() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
	
		try {
			installPlotPackagePDFImporterOntoQuickFigures();
			ShowMessage.showOptionalMessage("You should be able to import PDF files into QuickFigures", true, "You can now import some objects from PDF files as vector graphics", "The objects will be grouped together", "This is a work in progress","Move the layout to move the object");
					
			} catch (Throwable t) {
			IssueLog.logT(t);
		}
		
	}


	/**
	 * 
	 */
	private void installPlotPackagePDFImporterOntoQuickFigures() {
		NormalToolDragHandler.fileDropExtras.add(new PDFDrop());
		MenuBarForApp.addMenuBarItemInstaller(new PDFImportMenuItem());
		
	}


	
	
	
	
	
	
	
	
	
	
		
	
}

	
	
	

