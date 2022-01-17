/**
 * Author: Greg Mazo
 * Date Created Nov 27, 2021
 * Date Modified: Jan 3, 2022
 * Version: 2022.0
 */

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

	
	
	

