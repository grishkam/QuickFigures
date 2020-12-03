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
package exportMenus;


import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.batik.transcoder.TranscoderException;

import applicationAdapters.DisplayedImage;
import export.eps.PDFsaver;
import logging.IssueLog;

public class PDFQuickExport extends QuickExport {
	
	protected String getExtension() {
		
		return "pdf";
	}
	
	protected String getExtensionName() {
		return "PDF";
	}
	
	

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {

		try{
			System.setProperty("javax.xml.transform.TransformerFactory",
	                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
			File file = getFileAndaddExtension();
			if (file==null) return;
		String newpath=file.getAbsolutePath();
		
		 
		   
		  saveInPath(diw, newpath);
		  //  Desktop.getDesktop().open(new File(newpath));
		
		} catch (Throwable t) {
			if (t instanceof NoClassDefFoundError) {
				IssueLog.showMessage("Opps"+ "It seems imageJ cant find "+t.getMessage());
			//this.getClass().getClassLoader().loadClass(arg0)
			}
			IssueLog.logT(t);
		}
	        
	}

	/**
	 saves the image in the given path
	 */
	public void saveInPath(DisplayedImage diw, String newpath)
			throws IOException, TransformerException, ParserConfigurationException, TranscoderException {
		PDFsaver saver = new PDFsaver();
		   
		  saver.saveWrapper(newpath, diw);
	}

	@Override
	public String getCommand() {
		return "Export as PDF";
	}

	@Override
	public String getNameText() {
		return "Export as PDF";
	}
	
}