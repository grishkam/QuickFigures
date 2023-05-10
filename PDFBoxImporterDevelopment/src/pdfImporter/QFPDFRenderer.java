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
 * Date Created Jan 2, 2022
 * Date Modified: Jan 3, 2022
 * Version: 2023.2
 */
package pdfImporter;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import render.QFGraphics2D;

/**
 A class of PDFRenderer that renders PDF files unto a QuickFigures worksheet as objects
 */
public class QFPDFRenderer extends PDFRenderer {

	private ArrayList<ZoomableGraphic> layer;

	/**
	 * @param document
	 * @param g2d 
	 */
	public QFPDFRenderer(PDDocument document, ArrayList<ZoomableGraphic> layer, java.awt.Graphics2D g2d) {
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
