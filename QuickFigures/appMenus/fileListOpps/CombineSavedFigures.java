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
 * Date Modified: Jan 6, 2021
 * Version: 2022.1
 */
package fileListOpps;

import java.io.File;
import java.util.ArrayList;

import imageDisplayApp.ImageWindowAndDisplaySet;
import imageDisplayApp.ImageDisplayIO;
import imageMenu.CombineImages;
import layersGUI.GraphicTreeUI;
import layout.BasicObjectListHandler;
import logging.IssueLog;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelectionSystem;
import ultilInputOutput.FileChoiceUtil;

/**work in progress. a meny item that can be used to combine saved worksheets into a 
 * single figure*/
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
	
	
	public boolean isValidForLayerSelector(LayerSelectionSystem graphicTreeUI) {
		if (graphicTreeUI instanceof GraphicTreeUI)
		return true;
		return false;
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
				
				 boh.resizeCanvasToFitAllObjects(figure.getImageAsWorksheet());
				 figure.updateDisplay();
				} catch (Throwable t) {IssueLog.logT(t);}
			figure.autoZoom();
			
			}
	
	
	
		
	}


