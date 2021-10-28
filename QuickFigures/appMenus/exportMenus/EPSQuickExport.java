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
 * Version: 2021.2
 */
package exportMenus;


import java.awt.Desktop;
import java.io.File;
import applicationAdapters.DisplayedImage;
import export.eps.EPSsaver;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

/**A menu item for exporting as an EPS*/
public class EPSQuickExport extends QuickExport {
	
	/**
	 * @param openNow determines if the file will be opened strait away
	 */
	public EPSQuickExport(boolean openNow) {
		super(openNow);
	}

	protected String getExtension() {
		
		return "eps";
	}
	
	protected String getExtensionName() {
		return "Encapsulated PostScript";
	}
	
	public boolean isFOPInstalled() {
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
			File file = getFileAndaddExtension();
			if (file==null) return;
		String newpath=file.getAbsolutePath();
		
		FileChoiceUtil.overrideQuestion(new File(newpath));
		
		 this.saveInPath(diw, newpath);
		   
		 
		  
		  
		    Desktop.getDesktop().open(new File(newpath));
		
		} catch (Throwable t) {
			if (t instanceof NoClassDefFoundError) {
				IssueLog.showMessage("Opps. "+ "It seems imageJ cant find "+t.getMessage());
			
			}
			IssueLog.logT(t);
		}
	        
	}
	
	/**
	saves the item in the given path
	 */
	public  void saveInPath(DisplayedImage diw, String newpath) {
		 try {
			EPSsaver saver = new EPSsaver();
			   
			  saver.saveWrapper(newpath, diw);
		} catch (Exception e) {
			IssueLog.log(e);
		} 
	}

	@Override
	public String getCommand() {
		return "Export as EPS";
	}

	@Override
	public String getNameText() {
		return "EPS";
	}
	
	
	
}
