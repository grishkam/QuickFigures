package fieldReaderWritter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.batik.ext.awt.image.codec.png.PNGImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.svggen.GenericImageHandler;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphicalObjects.ImagePanelGraphic;

public class ImageSVGExporter extends SVGExporter {

	
	private ImagePanelGraphic graphic;

	public ImageSVGExporter(ImagePanelGraphic g) {
		graphic=g;
	}
	
	@Override
	public Element toSVG(Document dom, Element e) {
		ImageWriterRegistry.getInstance().register((ImageWriter) new PNGImageWriter());
		
		BufferedImage image = graphic.getProcessedImageForDisplay();
		SVGGraphics2D g2d = new SVGGraphics2D(dom);
		
		GenericImageHandler handler = g2d.getGenericImageHandler();
		
		Element element = handler.createElement(g2d.getGeneratorContext());
		
		handler.handleImage((Image)image, element, (int)graphic.getLocation().getX(),(int) graphic.getLocation().getY(), (int)graphic.getObjectWidth(), (int)graphic.getObjectHeight(), g2d.getGeneratorContext());
		
		e.appendChild(element);
		
		return element;
	}
	
	public static void main(String[] args) throws IOException {
		String path="/Users/mazog/Desktop/Untitled testpng.png";
		BufferedImage image = ImageIO.read(new File(path));
		PNGImageWriter pngwrit = new PNGImageWriter();
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		pngwrit.writeImage(image, bao);
		
		
	}

}
