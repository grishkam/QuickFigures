package fieldReaderWritter;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.File;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import applicationAdapters.DisplayedImageWrapper;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;

import org.apache.batik.svggen.*;

public class SVGsaver {
	
	String version="1.1";
	String xmlns="http://www.w3.org/2000/svg";
	Rectangle rs=new Rectangle(0,0,500,500);
	
	public static void main(String[] args) {
		new SVGsaver().saveObject(pathOfDesktopFolder()+"/test.svg");
	}
	
	
	public Document makeDocument(String newpath) throws ParserConfigurationException {
		File file=new File(newpath);
		int i=TestXMLWRitting.overrideQuestion(file);
		if(i!=0) return null;
		// TODO Auto-generated method stub
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;

		docBuilder = docFactory.newDocumentBuilder();


// root elements
		Document doc = docBuilder.newDocument();
		return doc;
	}
	
	public void makeFile(Document doc, String newpath) throws TransformerException {
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
	
	
	public void saveObject(String newpath) {
		try {
			
			Document doc = makeDocument(newpath);
	
	
	
			createSVGelememnt(doc);
	
	

		
			makeFile(doc, newpath);
		
		
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	
	public Element createSVGelememnt(Document doc) {
		// TODO Auto-generated method stub
		Element ele = doc.createElement("svg");
		
		doc.appendChild(ele);
		ele.setAttribute("version", version);
		ele.setAttribute( "xmlns","http://www.w3.org/2000/svg");
		
		
		return ele;
		
	}
	
	public void setRectAttributes(Element e, Rectangle r) {
		String suffix="px";
		
		e.setAttribute("x", r.x+suffix);
		e.setAttribute("y", r.y+suffix);
		e.setAttribute("width", r.width+suffix);
		e.setAttribute("height", r.height+suffix);
	}
	
	public static Rectangle getRectAttributes(Element e, String st) {
		Rectangle output = new Rectangle();
		output.x=(int)getAttributeAsNumber(e, "x");
		output.y=(int)getAttributeAsNumber(e, "y");
		output.width=(int)getAttributeAsNumber(e, "width");
		output.height=(int)getAttributeAsNumber(e, "height");
		
		return output;
	}
	
	
	public static double getAttributeAsNumber(Element e, String st) {
		String att = e.getAttribute(st);
		if (att.contains("px")) att=att.replace("px", "");
		return Double.parseDouble(att);
	}
	
	
	/**creates an attributre for the given object*/
	public static void setAttributeToObject(Element e, String atNam, Object o) {
		
		
		
		e.setAttribute(atNam, o+"");
	}
	
	public Element elementCreateShapeElement(Document dom, Shape s) {
		SVGGeneratorContext context = SVGGeneratorContext.createDefault(dom);
		return new SVGShape(context).toSVG(s);
	}

	public static String pathOfDesktopFolder() {
		return new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+"/Desktop";
	}
	
	
	public void saveWrapper(String newpath, DisplayedImageWrapper diw) throws TransformerException, ParserConfigurationException {
		
		 GraphicLayer set = diw.getImageAsWrapper().getGraphicLayerSet();
		  Document doc = makeDocument(newpath);
		  Element element = createSVGelememnt(doc);
		  element.setAttribute("id", set.getName());
		  setRectAttributes(element, new Rectangle(diw.getImageAsWrapper().getCanvasDims()));
		  
			SVGGeneratorContext context = SVGGeneratorContext.createDefault(doc);
			
		  
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
}
