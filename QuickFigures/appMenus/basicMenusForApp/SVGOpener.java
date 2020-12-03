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
package basicMenusForApp;

import java.io.File;

import ultilInputOutput.FileChoiceUtil;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ToolbarTester;
import export.svg.GraphicSVGParser;
import figureFormat.DirectoryHandler;
import imageDisplayApp.GraphicContainingImage;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasAutoResize;
import logging.IssueLog;

/**does a crude and dirty import of an SVG file. simply used to open shapes*/
public class SVGOpener   extends BasicMenuItemForObj {


	
	public static void main(String[] arts) {
		ToolbarTester.startToolbars(true);
		String path=new DirectoryHandler().getFigureFolderPath()+"/export 5.svg";
		File f = new File(path);
		SVGOpener.showFile(f);
		
		//svgOpener.showFile(f);
		/**
	   path="/Users/mazog/Desktop/test.svg";
		File f2 = new File(path);
	   svgOpener.showFile(f2);
	   svgOpener.showFile(f);*/
	  
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		File f=FileChoiceUtil.getOpenFile();
	
		showFile(f);
			
		
	}
	
	public static ImageWindowAndDisplaySet showFile(File f) {
		if (f==null) return null;
		//diw.getImageAsWrapper();
		
		
		IssueLog.log("showing file "+'\n'+f.getAbsolutePath());
			
			GraphicContainingImage set = readFromFile(f);
			if (set==null) return null;
			ImageWindowAndDisplaySet output = new ImageWindowAndDisplaySet(set);
			
			new CanvasAutoResize().performActionDisplayedImageWrapper(output);
			return output;
	}
	
	public static GraphicContainingImage readFromFile(File f) {
	
		if (!f.exists()) {
			IssueLog.log("file "+" is non existent"+f);
		}
		
		try {
			
			
			
			
			GraphicContainingImage set = new GraphicSVGParser().openSVG(f.getAbsolutePath());
		return set;
			//return GraphicSVGParser.openSVG(f.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
		
	}
	

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "openSVGDisplaySet";
	}

	@Override
	public String getNameText() {
		
		return "Drawing (experimental)";
	}

	@Override
	public String getMenuPath() {
		return "File<Open";
	}

}
