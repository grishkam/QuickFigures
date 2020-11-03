package fileListOpps;

import java.io.File;
import java.util.ArrayList;
import genericMontageKit.BasicOverlayHandler;
import imageDisplayApp.ImageAndDisplaySet;
import imageDisplayApp.ImageDisplayIO;
import imageMenu.CombineImages;
import logging.IssueLog;
import selectedItemMenus.BasicMultiSelectionOperator;
import ultilInputOutput.FileChoiceUtil;

public class CombineSavedFigures extends BasicMultiSelectionOperator{
	
	
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
		
		 
		 ImageAndDisplaySet figure =  ImageAndDisplaySet.createAndShowNew("new set", 0,0);;
			
			for(File f: files) try {
			
				 ImageAndDisplaySet figure2 = ImageDisplayIO.showFile(f);
				 CombineImages.combineInto(figure, figure2, false);
					BasicOverlayHandler boh = new BasicOverlayHandler();
				
				 boh.resizeCanvasToFitAllObjects(figure.getImageAsWrapper());
				 figure.updateDisplay();
				} catch (Throwable t) {IssueLog.log(t);}
			figure.autoZoom();
			
			}
	
	
	
		
	}


