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
 * Date Created Nov 27, 2021
 * Date Modified: Jan 3, 2022
 * Version: 2022.2
 */
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
