/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package export.svg;

import java.awt.Color;

import org.apache.batik.svggen.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;

/**An SVG exporter for shapes*/
public class SVGEXporterForShape extends SVGExporter {

	private ShapeGraphic shape;

	public  SVGEXporterForShape(ShapeGraphic shape) {
		this.shape=shape;
	}
	
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
	
	
	SVGGeneratorContext context = SVGGeneratorContext.createDefault(dom);
	SVGGraphics2D gra2D = new SVGGraphics2D(dom);
	//DOMTreeManager treman = gra2D.getDOMTreeManager();
	
	Color c=shape.getStrokeColor();
	if(scaleBar) shape.setStrokeWidth(0);
	
	gra2D.setStroke(shape.getStroke());
	
	gra2D.setColor(shape.getStrokeColor());
	
	
	
	Element element = new SVGShape(context).toSVG(shape.getRotationTransformShape());
	
	e.appendChild(element);
	
	SVGBasicStroke stroke = new SVGBasicStroke(context);
	
	
	SVGStrokeDescriptor strokesvg = stroke.toSVG(shape.getStroke());
	
	addSVGDescriptor( strokesvg, element);
	
	//gra2D.setColor(this.getStrokeColor());;
	//SVGColor storkeColor = new SVGColor(context);
	//SVGDescriptor to = storkeColor.toSVG(gra2D.getGraphicContext());
	//strokeColor=getStrokeColor();
	element.setAttribute("id", shape.getName());
	SVGPaintDescriptor fillpaint = new SVGPaint(context).toSVG(shape.getFillColor());
	
	
	if (shape.isFilled()) addSVGDescriptor( fillpaint, element);

	setColorString(element, "stroke", "stroke-opacity", c);
	
	
	
	return element;
	
	
	}

	
}
