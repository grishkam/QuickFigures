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
 * Date Modified: Nov 29, 2021
 * Version: 2021.2
 */
package export.svg;

import java.awt.Color;

import org.apache.batik.svggen.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphicalObjects_Shapes.FrameGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import logging.IssueLog;
import messages.ShowMessage;

/**An SVG exporter for shapes. works somewhat differently in the special case
 * of scale bars
 * TODO: fix issue with EPS export in that shapes without a fill are filled in black. this does not occur with SVG or PDF
  so it might be a problem with the EPS transcoder. if I am not mistaken, this problem appeared to be fixed in some earlier versions */
public class SVGEXporterForShape extends SVGExporter {

	private ShapeGraphic shape;
	

	public  SVGEXporterForShape(ShapeGraphic shape) {
		this.shape=shape;
	}
	
	/***/
public Element toSVG(Document dom, Element e) {
	
	boolean scaleBar=false;
	
	if (shape instanceof BarGraphic){
		scaleBar=true;
		BarGraphic b=(BarGraphic) shape;
		if (b.isShowText()) {
			b.getBarText().getSVGEXporter().toSVG(dom, e);
		}
		//return b.getBreakdownGroup().getSVGEXporter().toSVG(dom, e);
	}
	
	if(!shape.isFilled()||shape.getFillColor().getAlpha()==0) {
		if(BatiKExportContext.currentContext==BatiKExportContext.EPS)
			{
			ShowMessage.showOptionalMessage("EPS file format does not support semi-transparent colors", true, "EPS file format not compatible with some colors", "Will convert completely transparent shapes to empty forms");
			shape=shape.createFilledStrokeCopy();
		}
	}
	
	if (shape instanceof FrameGraphic) {
		IssueLog.log("frames notperfect");
		
	}
	
	SVGGeneratorContext context = SVGGeneratorContext.createDefault(dom);
	SVGGraphics2D gra2D = new SVGGraphics2D(dom);
	
	Color c=shape.getStrokeColor();
	if(scaleBar) shape.setStrokeWidth(0);
	
	gra2D.setStroke(shape.getStroke());
	
	gra2D.setColor(shape.getStrokeColor());
	
	
	
	Element element = new SVGShape(context).toSVG(shape.getRotationTransformShape());
	
	e.appendChild(element);
	
	SVGBasicStroke stroke = new SVGBasicStroke(context);
	
	
	SVGStrokeDescriptor strokesvg = stroke.toSVG(shape.getStroke());
	
	addSVGDescriptor( strokesvg, element);
	
	
	element.setAttribute("id", shape.getName());
	
	
	
	Color fillColor = shape.getFillColor();
	if(!shape.isFilled())
		{fillColor=new Color(0,0,0,0);}//transparent fill color. some formats 
	
	SVGPaintDescriptor fillpaint = new SVGPaint(context).toSVG(fillColor);
	
	
	if ((shape.isFilled())) {//for some reason EPS files after transcoding have a black fill instead of nothing but making the fill transparent also does not work well
		//EPS transcoder does not use transparent fill colors nor does it appear to allow unfilled shapes
		addSVGDescriptor( fillpaint, element);
	} else {
		
		element.setAttribute("fill", "none");//fill should be none if no attribute is given. This should not be necesary but does no harm. problem might be with EPS transcoder
		//element.setAttribute("style", "fill: #0000ff; fill-opacity: 0.0;  ");//does not fix issue with eps but does no harm. problem might be with EPS transcoder
	}
	

	setColorString(element, "stroke", "stroke-opacity", c);
	
	
	
	return element;
	
	
	}



	
}
