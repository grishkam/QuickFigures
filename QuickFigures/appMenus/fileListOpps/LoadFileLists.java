/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package fileListOpps;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import graphicalObjects.FileStandIn;
import graphicalObjects.FigureDisplayContainer;
import layersGUI.GraphicTreeUI;
import logging.IssueLog;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelector;
import ultilInputOutput.FileChoiceUtil;

public class LoadFileLists extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean save=true;
	String menuPath="File Lists";
	private boolean multiFileChooser;
	
	public  LoadFileLists(int save) {
		this.save=save==1;
		this.multiFileChooser=save==2;
	}
	
	@Override
	public String getMenuCommand() {
		if (multiFileChooser) return  "Select Files in File Chooser";
		if (save) return "Save List As Text";
		 return "Load File List From Text";
	}
	
	public String getMenuPath() {
			return menuPath;
		}
	
	
	@Override
	public void run() {
		if (multiFileChooser) {
			File[] files = FileChoiceUtil.getFiles();
			FigureDisplayContainer container = super.getSelector().getGraphicDisplayContainer();
			 for(File f: files) {
				 FileStandIn fileSI = new FileStandIn(f);
				 container.getGraphicLayerSet().add(fileSI);
			 }
			return;
		}
		
		if (save) saveList();
			else loadList();
	}
	
	void loadList() {
		File input=FileChoiceUtil.getOpenFile();
		FigureDisplayContainer container = super.getSelector().getGraphicDisplayContainer();
		try{
			if (input==null ||!input.exists()) return;
		Scanner scan2 =  new Scanner(input);
		while(scan2.hasNextLine()) {
    		String line = scan2.nextLine();
    		if (line==null||line.trim().equals("")) continue;
    		FileStandIn fileSI = new FileStandIn(new File(line));
    		container.getGraphicLayerSet().add(fileSI);
    		
		}
		scan2.close();
		
		
		}catch (Throwable t) {IssueLog.logT(t);}
	}
	
	void saveList() {
		
		
		ArrayList<File> files = super.getPointedFiles();
		File output=FileChoiceUtil.getSaveFile();
		
		try{
		  FileWriter fw = new FileWriter(output,false);
	    for(File f: files) try {
	    		
	    		fw.write('\n'+f.getAbsolutePath()+'\n');
	    		
		} catch (Throwable t) {IssueLog.logT(t);}
	    	
	    fw.close();
		} catch (Exception e) {IssueLog.logT(e);}
		
	}
	
	public boolean isValidForLayerSelector(LayerSelector graphicTreeUI) {
		if (graphicTreeUI instanceof GraphicTreeUI)
		return true;
		return false;
		}

}
