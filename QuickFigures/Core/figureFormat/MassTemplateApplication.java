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
 * Date Modified: Jan 4, 2021
 * Version: 2021.1
 */
package figureFormat;

import java.io.File;
import java.util.ArrayList;

import figureOrganizer.FigureOrganizingLayerPane;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageDisplayApp.ImageDisplayIO;
import layersGUI.GraphicTreeUI;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelectionSystem;
import ultilInputOutput.FileChoiceUtil;


/**A multi selection operator that applies a figure template to many files*/
public class MassTemplateApplication extends BasicMultiSelectionOperator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TemplateUserMenuAction templateSaver=new TemplateUserMenuAction(TemplateUserMenuAction.APPLY_TEMPLATE, true);
	
	/**Will apply a single default template to a series of files that each contain serialized
  	figures*/
	public void perform(ArrayList<File> files) {
		 FigureTemplate template = templateSaver.loadDefaultTemplate();
		for(File f: files) {
			ImageWindowAndDisplaySet figure = ImageDisplayIO.showFile(f);
			GraphicLayer layer = figure.getImageAsWorksheet().getTopLevelLayer();
			ArrayList<GraphicLayer> allsublayers = layer.getSubLayers();
			
			for(GraphicLayer figLayer: allsublayers) {
				if (figLayer instanceof FigureOrganizingLayerPane) {
					template.applyTemplateToLayer(figLayer);
					
				}
			}
			
			
		}
		
		
	}
	

	@Override
	public String getMenuCommand() {
		return "Apply Default Template to File List";
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
		perform(files);
		
	}
	
	/**this option is only available from the layers window (that window contains a means of storing a file list)*/
	public boolean isValidForLayerSelector(LayerSelectionSystem graphicTreeUI) {
		if (graphicTreeUI instanceof GraphicTreeUI)
			return true;
		return false;
		}


}
