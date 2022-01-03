/**
 * Author: Greg Mazo
 * Date Modified: Jan 2, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package pdfImporter;

import java.awt.Graphics2D;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

import graphicalObjects_LayerTypes.GraphicLayer;
import render.QFGraphics2D;

/**
 
 * 
 */
public class QFPDFRenderer extends PDFRenderer {

	private GraphicLayer layer;

	/**
	 * @param document
	 * @param g2d 
	 */
	public QFPDFRenderer(PDDocument document, GraphicLayer layer, java.awt.Graphics2D g2d) {
		super(document);
		this.layer=layer;
	}

	protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException{
		return  new PageDrawer2(parameters, layer);
	}

	/**
	 * @param graphics2d
	 */
	public void setRecipient(QFGraphics2D graphics2d) {
		// TODO Auto-generated method stub
		
	}
	
}
