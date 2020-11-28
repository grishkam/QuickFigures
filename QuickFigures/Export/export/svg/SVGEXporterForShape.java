package export.svg;

import java.awt.Color;

import org.apache.batik.svggen.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;

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
