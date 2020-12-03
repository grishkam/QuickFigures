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

import graphicalObjects.ImagePanelGraphic;

/**An SVG exporter implementation for image panels*/
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
