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
 * Version: 2023.2
 */
package export.eps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

import applicationAdapters.DisplayedImage;
import export.svg.BatiKExportContext;
import export.svg.SVGsaver;
import figureFormat.DirectoryHandler;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import messages.ShowMessage;
import undo.CombinedEdit;
import undo.UndoTextEdit;

/**Class used for exporting figures to pdf file*/
public class PDFsaver {

	public void saveWrapper(String newpath, DisplayedImage diw) throws IOException, TransformerException, ParserConfigurationException, TranscoderException {
		BatiKExportContext.currentContext=this.getTheRightContext();
		
		try {
			CombinedEdit ce=new CombinedEdit() ;
			
			boolean notUsedFont = fixfonts(diw, ce);
			
			
			/**creates a temporary file*/
			String tempPath = DirectoryHandler.getDefaultHandler().getTempFolderPath()+"/temp.svg";
			File tempFile = new File(tempPath);
			
			if(tempFile.exists()) 
				{
				
					tempFile.delete();
					IssueLog.log("temp file exists "+tempFile.exists());
					tempFile = new File(tempPath);
					
				}
			
			/**saves the figure in the temp file as an svg*/
			new SVGsaver().saveFigure(tempPath, diw, this.getTheRightContext());
			
			/**transcodes the svg file into a pdf*/
			transcode(newpath, tempFile);
			
			
			if (notUsedFont) {
				ce.undo();
				ShowMessage.showOptionalMessage("Export notes", true, "Some fonts will not export exactly. May be replaced by nearly identical font");
			}
			
			tempFile.deleteOnExit();
			File outputFile = new File(newpath);
			outputFile.setReadable(true);
			outputFile.setReadOnly();
		} catch (Throwable e) {
			IssueLog.logT(e);
			if(e instanceof ClassNotFoundException || e instanceof NoClassDefFoundError) {
				ShowMessage.showOptionalMessage("have you installed batik 1.14?", false, "Seems that you have not installed the latest version of batik", "This feature was written with batik 1.14.");
			}
		}
	}


	/**
	 * @return
	 */
	protected BatiKExportContext getTheRightContext() {
	
		return BatiKExportContext.PDF;
	}


	/**Alters certain fonts into vertains that are suitable for export
	 * @param diw the target image
	 * @param ce
	 * @return whether font changes were made
	 */
	private boolean fixfonts(DisplayedImage diw, CombinedEdit ce) {
		boolean notUsedFont=false;
		
		ArrayList<LocatedObject2D> all = diw.getImageAsWorksheet().getLocatedObjects();
		for(LocatedObject2D a: all) {
			if(a instanceof TextGraphic) {
				TextGraphic t = (TextGraphic) a;
				if(t.getFont().getFamily().equals("Arial")) {
					//Arial font does not export properly to pdf or eps with the library used. Instead of embedding it as a custom font, will be replaced by helvetica. See 'https://xmlgraphics.apache.org/fop/0.95/fonts.html' for some details about the fonts available
					ce.addEditToList(new UndoTextEdit(t));
					t.setFontFamily("Helvetica");
					
					notUsedFont=true;
				}
			}
		}
		return notUsedFont;
	}


	/**
	 transcodes a SVG file into an PDF file
	 */
	public void transcode(String newpath, File tempFile) throws FileNotFoundException, TranscoderException {
		org.apache.fop.svg.PDFTranscoder transcoder = new PDFTranscoder();
	

		TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(tempFile));
		FileOutputStream fileOutputStream = new FileOutputStream(newpath);
		TranscoderOutput transcoderOutput = new TranscoderOutput(fileOutputStream);
		transcoder.transcode(transcoderInput, transcoderOutput);
		try {
			fileOutputStream.close();
		} catch (IOException e) {
			IssueLog.logT(e);
		}
		
	}
	

	
	
	

}
