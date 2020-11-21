package fileListOpps;

import java.io.File;
import java.util.ArrayList;
import genericMontageKit.BasicObjectListHandler;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageDisplayApp.ImageDisplayIO;
import imageMenu.CombineImages;
import logging.IssueLog;
import selectedItemMenus.BasicMultiSelectionOperator;
import ultilInputOutput.FileChoiceUtil;

public class CombineSavedFigures extends BasicMultiSelectionOperator{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getMenuCommand() {
		return "Combine Figures in list";
	}
	
	public String getMenuPath() {
		return "File Lists";
	}

	@Override
	public void run() {
		 ArrayList<File> files = super.getPointedFiles();
		
		 if (files.size()==0) {
			  files = FileChoiceUtil.getFileArray();

		 }
		
		
		 if (files.size()==0) {
			 return;

		 }
		
		 
		 ImageWindowAndDisplaySet figure =  ImageWindowAndDisplaySet.createAndShowNew("new set", 0,0);;
			
			for(File f: files) try {
			
				 ImageWindowAndDisplaySet figure2 = ImageDisplayIO.showFile(f);
				 CombineImages.combineInto(figure, figure2, false);
					BasicObjectListHandler boh = new BasicObjectListHandler();
				
				 boh.resizeCanvasToFitAllObjects(figure.getImageAsWrapper());
				 figure.updateDisplay();
				} catch (Throwable t) {IssueLog.logT(t);}
			figure.autoZoom();
			
			}
	
	
	
		
	}


