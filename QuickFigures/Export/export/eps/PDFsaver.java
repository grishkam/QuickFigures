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
import export.svg.SVGsaver;
import figureFormat.DirectoryHandler;
import graphicalObjects_BasicShapes.TextGraphic;
import logging.IssueLog;
import undo.CombinedEdit;
import undo.UndoTextEdit;
import utilityClassesForObjects.LocatedObject2D;

public class PDFsaver {

	public void saveWrapper(String newpath, DisplayedImage diw) throws IOException, TransformerException, ParserConfigurationException, TranscoderException {
		
		
		CombinedEdit ce=new CombinedEdit() ;
		boolean notUsedFont=false;
		ArrayList<LocatedObject2D> all = diw.getImageAsWrapper().getLocatedObjects();
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
		
		
		
		String tempPath = DirectoryHandler.getDefaultHandler().getTempFolderPath()+"/temp.svg";
		IssueLog.log(tempPath);
		File tempFile = new File(tempPath);
		if(tempFile.exists()) tempFile.delete();
		new SVGsaver().saveWrapper(tempPath, diw);
		
		
		transcode(newpath, tempFile);
		
		
		if (notUsedFont) {
			ce.undo();
			IssueLog.showMessage("Some fonts will not export exactly. May be replaced by nearly identical font");
		}
		
		tempFile.deleteOnExit();
		File outputFile = new File(newpath);
		outputFile.setReadable(true);
		outputFile.setReadOnly();
	}

	/**
	 
	 */
	public void transcode(String newpath, File tempFile) throws FileNotFoundException, TranscoderException {
		org.apache.fop.svg.PDFTranscoder transcoder = new PDFTranscoder();
	

		TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(tempFile));
		TranscoderOutput transcoderOutput = new TranscoderOutput(new FileOutputStream(newpath));
		transcoder.transcode(transcoderInput, transcoderOutput);
	}
	
	public static void main(String[] args ) {
		
	}
	
	
	

}
