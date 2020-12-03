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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

/**An implementation of the SVG exporter for layers*/
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
