import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import appContextforIJ1.ImageDisplayTester;
import applicationAdapters.DisplayedImage;
import graphicalObjects_LayerTypes.ClosedGroup;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasAutoResize;
import pdfImporter.QFPDFRenderer;
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
		java.awt.Graphics2D g2d= GraphicsRenderQF.getGraphics2D(gg.getTheInternalLayer());
		PDFRenderer reader=new QFPDFRenderer(pd, gg.getTheInternalLayer(), g2d);
		
		reader.renderPageToGraphics(0, g2d, scale);
		
	
	
	
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
