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
 * Version: 2022.2
 */
package export.svg;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.svggen.SVGDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import logging.IssueLog;

/**A class that transforms a QuickFigures objects into svg items for export*/
public abstract class SVGExporter {
	boolean logattributes=false;
	
	public abstract Element toSVG(Document dom, Element e) ;
	
	/**Adds the SVG descriptor to the element*/
	public void addSVGDescriptor(SVGDescriptor d, Element element) {
		Map<?,?> map2 = d.getAttributeMap(new HashMap<String,String> ());
		addAttributeMap(map2, element);
	}
	
	/**Adds an attribute map to the elment*/
	public void addAttributeMap(Map<?,?> map2 , Element element) {
		for(Object  key:map2.keySet() ) {
			element.setAttribute(key+"", ""+map2.get(key));
			if (this.logattributes) {
				IssueLog.log("Key "+key);
				IssueLog.log("value "+map2.get(key));
			}
		}
		
	}
	
	/**Adds an attribute describing a color to the element*/
	public void  setColorString(Element element, String st, String opacity, Color c) {
		element.setAttribute(st, "rgb("+c.getRed()+","+c.getGreen()+","+c.getBlue()+")");
		element.setAttribute(opacity, ""+c.getAlpha()/(255.00));
	}
}
