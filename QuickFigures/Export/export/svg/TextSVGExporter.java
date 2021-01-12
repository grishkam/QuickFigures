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

import java.awt.Color;
import java.awt.Font;
import org.apache.batik.svggen.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;

/**An  SVG exporter implementation for text items*/
public class TextSVGExporter extends SVGExporter {

	private TextGraphic textgra;
	boolean singleObjectExport=true;

	public TextSVGExporter(TextGraphic t) {
		this.textgra=t;
	}
	
	String rotAttribute=null;
	
	@Override
	public Element toSVG(Document dom, Element e) {
		
		boolean complex=textgra instanceof ComplexTextGraphic;
		ComplexTextGraphic comp=null;
		
		if (complex &&!singleObjectExport) {//exports each line as a distinct object
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
			
			TextLine line = comp.getParagraph().get(0);
			
			for(int i=0; i<comp.getParagraph().size();i++) {
				line = comp.getParagraph().get(i);
				for(int i2=0; i2<line.size();i2++) {
					createTextSpan(text, line.get(i2), dom, i2==0, g2d);
				
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
	
	


}
