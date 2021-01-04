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
import java.io.File;

import applicationAdapters.DisplayedImage;
import export.eps.EPSsaver;
import logging.IssueLog;

public class EPSQuickExport extends QuickExport {
	
	/**
	 * @param openNow
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {

		try{
			File file = getFileAndaddExtension();
			if (file==null) return;
		String newpath=file.getAbsolutePath();
		
		 
		   
		  EPSsaver saver = new EPSsaver();
		   
		  saver.saveWrapper(newpath, diw);
		  
		  
		    Desktop.getDesktop().open(new File(newpath));
		
		} catch (Throwable t) {
			if (t instanceof NoClassDefFoundError) {
				IssueLog.showMessage("Opps. "+ "It seems imageJ cant find "+t.getMessage());
			//this.getClass().getClassLoader().loadClass(arg0)
			}
			IssueLog.logT(t);
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
