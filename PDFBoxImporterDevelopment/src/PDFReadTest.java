
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import appContextforIJ1.ImageDisplayTester;
import applicationAdapters.DisplayedImage;
import figureFormat.DirectoryHandler;
import graphicalObjects_LayerTypes.ClosedGroup;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasAutoResize;
import logging.IssueLog;
import render.GraphicsRenderQF;
import undo.CombinedEdit;
import undo.Edit;

/**
 * Author: Greg Mazo
 * Date Modified: Nov 26, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */

/**
 quick test of pdf reading with pdf box
 */
public class PDFReadTest {

	public static String path=new DirectoryHandler().getFigureFolderPath()+"/vv2.pdf";
	
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File file = new File(path);
		IssueLog.log(file.exists()+"will check  "+path);
		IssueLog.sytemprint=true;
		showPDFFile(file);
		
		//reader.renderPageToGraphics(0, graphics);
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public static void showPDFFile(File file) throws IOException {
		
		
		
		
		ImageDisplayTester.main(null);
		new PDFImporter_().run("");
		
		
		
		addPDFToFigure(file, null);
	}

	/**
	 * @param file
	 * @param diw
	 * @return 
	 * @throws IOException
	 */
	public static CombinedEdit addPDFToFigure(File file, DisplayedImage diw) throws IOException {
		if(diw==null)
			diw = ImageWindowAndDisplaySet.createAndShowNew("New Image", 400, 300);
		
		if(file==null)
			return null;
		
		ClosedGroup gg = new ClosedGroup();
		
		gg.setName(file.getName());
		
		float scale=1;
		PDDocument pd = Loader.loadPDF(file);
		PDFRenderer reader=new PDFRenderer(pd);
		reader.renderPageToGraphics(0,  GraphicsRenderQF.getGraphics2D(gg.getTheInternalLayer()), scale);
		
	
	
	
		gg.scaleAbout(new Point(0,0), 1/scale);
		
		CombinedEdit ce=new CombinedEdit();
		ce.addEditToList(
		Edit.addItem(diw.getImageAsWorksheet().getTopLevelLayer(), gg));
		diw.getImageAsWorksheet().addItemToImage(gg);
		ce.addEditToList(
		new CanvasAutoResize(true).performUndoableAction(diw));
		
		return ce;
	}

}
