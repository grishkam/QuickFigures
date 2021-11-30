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
 * Version: 2021.2
 */
package export.svg;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import applicationAdapters.DisplayedImage;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

/**this class exports figues as .SVG files*/
public class SVGsaver {
	
	String version="1.1";
	String xmlns="http://www.w3.org/2000/svg";
	Rectangle rs=new Rectangle(0,0,500,500);
	
	
	public SVGsaver() {}
	

	
	/**creates a document*/
	private Document makeDocument(String newpath) throws ParserConfigurationException {
		File file=new File(newpath);
		int i=FileChoiceUtil.overrideQuestion(file);
		if(i!=0) return null;
	
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;

		docBuilder = docFactory.newDocumentBuilder();


		Document doc = docBuilder.newDocument();
		return doc;
	}
	
	/**creates a file from the given document*/
	private void makeFile(Document doc, String newpath) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(newpath);
		
		
		try{
		transformer.transform(source, result); 
		} catch (TransformerException  fnf) {
			IssueLog.showMessage("Problem Writting File. Make sure you have permission to write");
		}
	}
	
	/**returns a window to view a specific svg file*/
	public JFrame viewSavedSVG(File f) {
		JSVGCanvas svgCanvas = new JSVGCanvas();
		 try {
             svgCanvas.setURI(f.toURI().toString());
             IssueLog.log("opening "+f);
             svgCanvas.setSize(900, 600);
             svgCanvas.setEnableImageZoomInteractor(true);
             svgCanvas.setPreferredSize(new Dimension(900, 600));
         } catch (Exception ex) {
             ex.printStackTrace();
         }
		
		JFrame window = new JFrame(f.getName());
		window.add(svgCanvas);
		window.pack();
		return window;
	}

	
	private Element createSVGelememnt(Document doc) {
		Element ele = doc.createElement("svg");
		
		doc.appendChild(ele);
		ele.setAttribute("version", version);
		ele.setAttribute( "xmlns","http://www.w3.org/2000/svg");
		
		
		return ele;
		
	}
	
	/**sets the location, width and height of the element to the given rectangle*/
	private static void setRectAttributes(Element e, Rectangle r) {
		String suffix="px";
		
		e.setAttribute("x", r.x+suffix);
		e.setAttribute("y", r.y+suffix);
		e.setAttribute("width", r.width+suffix);
		e.setAttribute("height", r.height+suffix);
	}

	/**Saved the figure to the given path*/
	public void saveFigure(String newpath, DisplayedImage diw, BatiKExportContext e) throws TransformerException, ParserConfigurationException {
		BatiKExportContext.currentContext=e;
		 GraphicLayer set = diw.getImageAsWorksheet().getTopLevelLayer();
		  Document doc = makeDocument(newpath);
		  Element element = createSVGelememnt(doc);
		  element.setAttribute("id", set.getName());
		  setRectAttributes(element, new Rectangle(diw.getImageAsWorksheet().getCanvasDims()));
		  
			createContext(doc);
			
			
		  
		  for(ZoomableGraphic z: set.getItemArray()) try {
	        	if (z instanceof SVGExportable) {
	        		SVGExportable exs=(SVGExportable) z;
	        		exs.getSVGEXporter().toSVG(doc, element);
	        	}
			  
	        }catch (Throwable t) {
	        	t.printStackTrace();
	        }
			
			
	
	

		
			makeFile(doc, newpath);
		
	}


	/**
	creates a context for the document
	 */
	private SVGGeneratorContext createContext(Document doc) {
		return SVGGeneratorContext.createDefault(doc);
	}
	
	
	
}
