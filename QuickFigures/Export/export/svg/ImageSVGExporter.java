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
 * Version: 2021.1
 */
package export.svg;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.apache.batik.ext.awt.image.codec.png.PNGImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.svggen.GenericImageHandler;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphicalObjects_SpecialObjects.ImagePanelGraphic;

/**An SVG exporter implementation for image panels using Apache Batik*/
public class ImageSVGExporter extends SVGExporter {

	
	private ImagePanelGraphic graphic;

	public ImageSVGExporter(ImagePanelGraphic g) {
		graphic=g;
	}
	
	/**Adds an image panel to the Document*/
	@Override
	public Element toSVG(Document dom, Element e) {
		ImageWriterRegistry.getInstance().register((ImageWriter) new PNGImageWriter());
		
		BufferedImage image = graphic.getProcessedImageForDisplay();
		
		SVGGraphics2D g2d = new SVGGraphics2D(dom);
		
		GenericImageHandler handler = g2d.getGenericImageHandler();
		
		Element element = handler.createElement(g2d.getGeneratorContext());
		
		Point2D p = graphic.getLocationUpperLeft();
		handler.handleImage((Image)image, element, (int)p.getX(),(int) p.getY(), (int)graphic.getObjectWidth(), (int)graphic.getObjectHeight(), g2d.getGeneratorContext());
		
		e.appendChild(element);
		
		return element;
	}
	


}
