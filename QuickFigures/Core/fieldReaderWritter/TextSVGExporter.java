package fieldReaderWritter;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.batik.svggen.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import applicationAdapters.ToolbarTester;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import imageDisplayApp.ImageWindowAndDisplaySet;
import utilityClassesForObjects.TextLine;
import utilityClassesForObjects.TextLineSegment;

/**An  SVG exporter implementation for text items*/
public class TextSVGExporter extends SVGExporter {

	private TextGraphic textgra;
	boolean testSave=true;

	public TextSVGExporter(TextGraphic t) {
		this.textgra=t;
	}
	
	String rotAttribute=null;
	
	@Override
	public Element toSVG(Document dom, Element e) {
		// TODO Auto-generated method stub
		boolean complex=textgra instanceof ComplexTextGraphic;
		ComplexTextGraphic comp=null;
		
		if (complex &&!testSave) {//exports each line as a distinct object
			GraphicLayerPane units = ((ComplexTextGraphic) textgra).getBreakdownGroup();
			return units.getSVGEXporter().toSVG(dom, e);
		}
		
		SVGGraphics2D g2d = new SVGGraphics2D(dom);
		SVGGeneratorContext generatorCtx = g2d.getGeneratorContext();
		Color c=textgra.getDimmedColor();
		SVGFont font2 = new SVGFont(generatorCtx);
		double x=textgra.getX();
		double y=textgra.getY();
		double angle=(-textgra.getAngleInDegrees());
		Font font=textgra.getFont();
		String sstringText=textgra.getText();
		if (complex) {
			comp=(ComplexTextGraphic) textgra;
			sstringText=comp.getParagraph().get(0).get(0).getText();
			
		}
		double rotx = textgra.getCenterOfRotation().getX();
		double roty = textgra.getCenterOfRotation().getY();
		
		
		g2d.setFont(font);
		g2d.setColor(c);
		
		Element text = dom.createElementNS(SVGSyntax.SVG_NAMESPACE_URI, SVGSyntax.SVG_TEXT_TAG);
		
		
		
		text.setAttributeNS(null,SVGSyntax. SVG_X_ATTRIBUTE,
				                            generatorCtx.doubleString(x));
				          text.setAttributeNS(null, SVGSyntax.SVG_Y_ATTRIBUTE,
				                         generatorCtx.doubleString(y));
			       text.setAttributeNS(SVGSyntax.XML_NAMESPACE_URI,
			    		   SVGSyntax. XML_SPACE_ATTRIBUTE,
			    		   SVGSyntax.   XML_PRESERVE_VALUE);
		
		
			       e.appendChild(text);
			      if (!complex) text.appendChild(dom.createTextNode(sstringText));  
		SVGFontDescriptor fd = font2.toSVG(font, g2d.getFontRenderContext());
		addSVGDescriptor(fd,text);	  
		setColorString(text, "fill", "opacity",c);
		rotAttribute="rotate("+angle+" "+rotx+","+roty+")";
		
		if (rotAttribute!=null)
		text.setAttribute("transform", rotAttribute);
		
		if (complex) {
			comp=(ComplexTextGraphic) textgra;
			//comp.getParagraph().get(0).get(0);
			TextLine line = comp.getParagraph().get(0);
			//for(int i=1; i<line.size();i++) {createTextSpan(text, line.get(i), dom, false, g2d);}
			for(int i=0; i<comp.getParagraph().size();i++) {
				line = comp.getParagraph().get(i);
				for(int i2=0; i2<line.size();i2++) {
					
					Element espan = createTextSpan(text, line.get(i2), dom, i2==0, g2d);
				
					}
				}
		}
	
		return text;
	}
	
	
	/**adds a text span element
	 * @param generatorCtx */
	Element createTextSpan(Element parent, TextLineSegment seg, Document dom, boolean line, SVGGraphics2D g2d) {
		g2d.setFont(seg.getFont());
		g2d.setColor(seg.getTextColor());
		SVGGeneratorContext generatorCtx=g2d.getGeneratorContext();
		Element text = dom.createElementNS(SVGSyntax.SVG_NAMESPACE_URI, SVGSyntax.SVG_TSPAN_TAG);
		if (line) {	
		text.setAttribute(SVGSyntax. SVG_X_ATTRIBUTE,
                generatorCtx.doubleString(seg.baseLine.getX()));
		
		text.setAttribute(SVGSyntax. SVG_Y_ATTRIBUTE,
                generatorCtx.doubleString(seg.baseLine.getY()));
		
		
		
		
		}
		
		/**takes care of the super and subscripts*/
		if(seg.isSuperscript()) {text.setAttribute("baseline-shift", "super");}
		if(seg.isSubscript()) {text.setAttribute("baseline-shift", "sub");}
		
		if (seg.isUnderlined()) {
			text.setAttribute("text-decoration", "underline");
			
		}
		
		if (seg.isStrikeThrough()) {
			text.setAttribute("text-decoration", "line-through");
			
		}
		
		Font font = seg.getFont();
		SVGFont font2 = new SVGFont(generatorCtx);
		SVGFontDescriptor fd = font2.toSVG(font, g2d.getFontRenderContext());
		addSVGDescriptor(fd,text);
		Color segColor=seg.getTextColor();
		setColorString(text, "fill", "opacity", 
				textgra.getDimmedColor(segColor)
				);
		
		if (rotAttribute!=null)
			text.setAttribute("transform", rotAttribute);
		
		parent.appendChild(text);
		text.appendChild(dom.createTextNode(seg.getText()));  
		return text;
		
	}
	
	
	/**opens a display. exports as svg then asks desktop to open the saved file*/
	public static void main(String[] arts) throws TransformerException, ParserConfigurationException, IOException {
		ImageWindowAndDisplaySet ex = ToolbarTester.showExample(true);
		String path="/Users/mazog/Desktop/test2.svg";
		  new SVGsaver().saveWrapper(path, ex);
		
		
		Desktop.getDesktop().open(new File(path));
		
		
	  
	}

}
