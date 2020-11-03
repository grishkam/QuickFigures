package fieldReaderWritter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

public class SVGExporter_GraphicLayer extends SVGExporter {
	
	private GraphicLayer layer;

	public SVGExporter_GraphicLayer(GraphicLayer g) {
		this.layer=g;
	}
	
	
public Element toSVG(Document dom, Element e) {
	Element element = dom.createElement("g");
	e.appendChild(element);
	element.setAttribute("id",layer.getName());
	
	
	
	for(ZoomableGraphic z: layer.getItemArray()) try {
    	if (z instanceof SVGExportable) {
    		SVGExportable exs=(SVGExportable) z;
    		exs.getSVGEXporter().toSVG(dom, element);
    	}
	
	
	
	} catch (Throwable t) {t.printStackTrace();}
	
	return element;
	}

public static void main(String[] args) {
	//.getWriterFor();
	
	//IssueLog.log(ImageWriterRegistry.getInstance().getWriterFor("image/png"));
}

}
