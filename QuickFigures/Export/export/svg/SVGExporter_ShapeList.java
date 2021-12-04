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
 * Date Created: Dec 4, 2021
 * Date Modified: Dec 4, 2021
 * Version: 2021.2
 */
package export.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphicalObjects.ZoomableGraphic;
import logging.IssueLog;
import messages.ShowMessage;

/**An implementation of the SVG exporter for compound shapes that have no perfect equivalent in svg but can be broken down 
 * into separate objects that are each exportable*/
public class SVGExporter_ShapeList extends SVGExporter {
	
	private ZoomableGraphic[] layer;
	private String name="Multiple objects";

	public SVGExporter_ShapeList(String name, ZoomableGraphic... g) {
		this.name=name;
		this.layer=g;
	}
	
	/**Adds elements for the layer and its content to the dom tree*/
public Element toSVG(Document dom, Element e) {
			Element element = dom.createElement("g");
			e.appendChild(element);
			element.setAttribute("id",name);
			
			
			
			for(ZoomableGraphic z: layer) try {
				
		    	if (z instanceof SVGExportable) {
		    		SVGExportable exs=(SVGExportable) z;
		    		exs.getSVGEXporter().toSVG(dom, element);
		    	}
			
			
			
			} catch (Throwable t) {
				IssueLog.log("Problem occured when try to export "+z);
				if (t instanceof ClassNotFoundException) {
					ShowMessage.showOptionalMessage("Missing class", false, "looks like part of batik is missing. Make sure you have Batik installed correctly");
				}
				IssueLog.logT(t);
			}
			
			return element;
	}



}
