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
 * Version: 2023.2
 */
package export.svg;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import org.apache.batik.svggen.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import logging.IssueLog;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;

/**An  SVG exporter implementation for text items
 * Text items will appear faithfully when the exported SVG files are opened in 
 * Firefox or Inkscape but will not appear correctly in other softwares*/
public class TextSVGExporter extends SVGExporter {

	private TextGraphic textgra;
	private boolean singleObjectExport=true;
	String rotAttribute=null;

	public TextSVGExporter(TextGraphic t) {
		this.textgra=t;
		 rotAttribute=null;
	}
	
	
	
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
		
		Font font=textgra.getFont();
		String sstringText=textgra.getText();
		if (complex) {
			comp=(ComplexTextGraphic) textgra;
			sstringText=comp.getParagraph().get(0).get(0).getText();
			
		}
		
		
		
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
		
		setUprotation(text);
		
		if (complex) {
			comp=(ComplexTextGraphic) textgra;
			
			TextLine line = comp.getParagraph().get(0);
			Point2D.Double pcenter=new Point2D.Double(x, y);
			for(int i=0; i<comp.getParagraph().size();i++) {
				line = comp.getParagraph().get(i);
				for(int i2=0; i2<line.size();i2++) {
					createTextSpan(text, line.get(i2), dom, i2==0, g2d, i==0, pcenter);
				
					}
				}
		}
	
		return text;
	}


	/**makes the rotation attribute of the text match the text item
	 * @param text
	 */
	public void setUprotation(Element text) {
		
		double angle=(-textgra.getAngleInDegrees());
		if(angle<0) angle+=360;
		if (angle==0)
			return;
		double rotx = textgra.getCenterOfRotation().getX();
		double roty = textgra.getCenterOfRotation().getY();
		setRotationAttribute(angle, rotx, roty, text);
	}


	/**introduces a rotation attribute to the text
	 * 
	 * @param angle
	 * @param rotx
	 * @param roty
	 * @param text
	 */
	public void setRotationAttribute(double angle, double rotx, double roty, Element text) {
		rotAttribute="rotate("+angle+" "+rotx+","+roty+")";
		
		if (rotAttribute!=null)
		text.setAttribute("transform", rotAttribute);
	}
	
	
	/**adds a text span element
	 * @param generatorCtx */
	Element createTextSpan(Element parent, TextLineSegment seg, Document dom, boolean lineStart, SVGGraphics2D g2d, boolean firstLine, Point2D.Double centerOfRotation) {
		g2d.setFont(seg.getFont());
		g2d.setColor(seg.getTextColor());
		SVGGeneratorContext generatorCtx=g2d.getGeneratorContext();
		Element text = dom.createElementNS(SVGSyntax.SVG_NAMESPACE_URI, SVGSyntax.SVG_TSPAN_TAG);
		
		/**for some reason the code for the test cases is not consistent without this step*/
		IssueLog.log("creating text span "+seg.getText()+" "+seg.baseLine);
		if (lineStart ) {	
				text.setAttribute(SVGSyntax. SVG_X_ATTRIBUTE,
		                generatorCtx.doubleString(seg.baseLine.getX()));
				
				text.setAttribute(SVGSyntax. SVG_Y_ATTRIBUTE,
		                generatorCtx.doubleString(seg.baseLine.getY()));
		
				
		
		}
		
		this.setUprotation(text);
		
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
		if(seg.isSuperscript()||seg.isSubscript()) {
			font=font.deriveFont((float) font.getSize()/2);
		}
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
