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
import java.awt.Window;
import java.io.File;

import applicationAdapters.DisplayedImage;
import export.svg.SVGsaver;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

public class SVGQuickExport extends QuickExport {
	
	protected String getExtension() {
		
		return "svg";
	}
	
	protected String getExtensionName() {
		return "SVG Images";
	}
	
	public boolean isBatikInstalled() {
		try {
			getClass().getClassLoader().loadClass("org.apache.batik.svggen.SVGGraphics2DRuntimeException");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {

		try{
			System.setProperty("javax.xml.transform.TransformerFactory",
	                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
			File file = getFileAndaddExtension();
			if (file==null) return;
		String newpath=file.getAbsolutePath();
		FileChoiceUtil.overrideQuestion(new File(newpath));
		saveInPath(diw,newpath);
		   
		  if (openImmediately)
		    Desktop.getDesktop().open(new File(newpath));
		
		} catch (Throwable t) {
			if (t instanceof NoClassDefFoundError) {
				IssueLog.showMessage("Opps"+ "It seems imageJ cant find "+t.getMessage());
			//this.getClass().getClassLoader().loadClass(arg0)
			}
			IssueLog.logT(t);
		}
	        
	}
	
	
	/**
	saves the item in the given path
	 */
	public  void saveInPath(DisplayedImage diw, String newpath) {
		 try {
			 diw.getWindow().setVisible(true);
			 diw.updateDisplay();
			 SVGsaver saver = new SVGsaver();
			   
			  saver.saveFigure(newpath, diw);
		} catch (Exception e) {
			IssueLog.log(e);
		} 
	}

	@Override
	public String getCommand() {
		return "Export as SVG";
	}

	@Override
	public String getNameText() {
		return "SVG";
	}
	
	/**shows the saved file*/
	@Override
	public Window viewSavedFile(File f) {
		SVGsaver saver = new SVGsaver();;
		return saver.viewSavedSVG(f);
	}
	
}
