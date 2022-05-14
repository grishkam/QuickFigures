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
 * Version: 2022.1
 */
package export.svg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.parser.Parser;
import org.apache.batik.dom.GenericText;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.StrokeShapePainter;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import figureFormat.DirectoryHandler;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.BufferedImageGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.BasicImageInfo;
import imageDisplayApp.StandardWorksheet;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasAutoResize;
import infoStorage.BasicMetaDataHandler;
import infoStorage.DomMetaInfoWrapper;
import locatedObject.BasicStrokedItem;
import locatedObject.PathPointList;
import logging.IssueLog;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;
import textObjectProperties.TextParagraph;

//WORK IN PROGRESS nees lot of work
//currently does not function in an acceptable form
/**A quick and dirty class to load svg graphics back into quickfigures
   since this is a super sloppy attempt for parsing SVG it only really works with
   files that have been exported from Quickfigures. Does not take into account the full complexity
   of .svg files nor the full complexity of batik. might re-write after understanding batik bridge
   and GVT trees better
   Re-imported items are not imported with the quickfigures specific properties */
public class GraphicSVGParser {
	
	static BridgeContext startingContext=null;
	
	
	public  StandardWorksheet openSVG(String path) throws IOException {
		 String parser = XMLResourceDescriptor.getXMLParserClassName();
		    SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		    String uri = path;
		    
		    if (uri.startsWith("C:") )
	    		uri=uri.replace("C:", "");//it will throw an exception if the path is given 
		    SVGOMDocument doc = (SVGOMDocument) f.createDocument(uri);
		   
 
		    BridgeContext mw =  showPreview(doc, false);
		    if (startingContext==null) {
		    	startingContext=mw;
		    }
		    
		   doc.getCSSEngine();
		    IssueLog.log("is the engine missing"+doc.getCSSEngine());
		  doc.setCSSEngine(new SVGCSSEngine(doc, new ParsedURL(path),new Parser(), mw));
		    
		    Element element1 = doc.getDocumentElement();
			  
			   
			 ZoomableGraphic graphic =  parseLayer(element1, mw);
			 StandardWorksheet set = new StandardWorksheet((GraphicLayerPane) graphic, new BasicImageInfo());
			 
			// IssueLog.log("from line "+graphic+" "+set);
			 
		    return set;
	}
	
	public  ZoomableGraphic getGraphic(org.w3c.dom.Node node, BridgeContext bc) {
		String nodename = node.getNodeName();
		String trimed = nodename.trim();
		
		IssueLog.log("Trying to parse graphic out of node anmed "+nodename);
		
		if (trimed.equals("g")||trimed.equals("svg")) {
			return  parseLayer(node, bc);
		} else if (trimed.equals("image")) {
			
			return parseImage(node);
		} if (trimed.equals("text")) {
			
			//if (node instanceof SVGOMTextElement)	return parseText((SVGOMTextElement) node);	else 
					return parseTextOld(node);
		} else if (trimed.equals("metadata")) {
			return  parseSpecial(node, bc);
			
		} else
		{
		
			return  parseShape(node, bc);
		}
		/**
		if (trimed.equals("path")) {
			return  parsePath(node);
		}
		
		if (trimed.equals("rect")) {
			return  parseRect(node);
		}
		
		if (trimed.equals("ellipse")) {
			return parseCircle(node);
		}*/
		
		
		
		
		
		
		
	}
	
	private ZoomableGraphic parseSpecial(Node node, BridgeContext bc) {
		// TODO Auto-generated method stub
		return null;
	}

	public  GraphicLayerPane parseLayer(org.w3c.dom.Node node, BridgeContext mw) {
		
		NamedNodeMap att = node.getAttributes();
		HashMap<String, String> attributemap = printAttributes(att);
		
		GraphicLayerPane output = new GraphicLayerPane(attributemap.get("id"));
		
		int length = node.getChildNodes().getLength();
		
		for(int i=0; i<length; i++) try {
			Node n = node.getChildNodes().item(i);
			
			try {
				IssueLog.log("Parsing "+n.getClass());
				ZoomableGraphic graphic = getGraphic(n, mw);
				
				output.add(graphic);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (Throwable r) {
			r.printStackTrace();
			IssueLog.log("problem reading object "+node);
		}
		
		return output;
	
}
	

	
	/**Tries to read the text from a dom node*/
	private  ZoomableGraphic parseTextOld(Node node) {
		if (node instanceof GenericText) {
			GenericText text=(GenericText) node;;
			IssueLog.log("Working on "+text.getTextContent());
		}
		
		TextGraphic tg = new TextGraphic();
		DomMetaInfoWrapper meta = new DomMetaInfoWrapper(node);
		ArrayList<DomMetaInfoWrapper> spans = meta.getSubNodesWithTag("tspan", null);
		if (spans.size()>1)
			tg = new ComplexTextGraphic();
		
		Double x = meta.getEntryAsDouble("x");
		Double y = meta.getEntryAsDouble("y");
		
		
		Color c=getColorFromNode(node, "fill");
		if (spans.size()==1) {//for single span text, the properties are taken from the span
			DomMetaInfoWrapper span = spans.get(0);
			c=getColorFromNode(span.getNode(), "fill");
			tg.setFont(getFontFromElement(span.getNode()));
			
			if(x==null||y==null) {
				String textPosition = meta.getEntryAsString("class");
				BasicMetaDataHandler.getPoint(textPosition);
			}
		}
		
		double angle=0;
		
		String transform=meta.getEntryAsString("transform");
		if (transform!=null && transform.startsWith("rotate(")) {
				String tf = transform.substring("rotate(".length());
				 angle = Double.parseDouble(tf.split(" ")[0]);
				
			}
		else if (transform!=null && transform.startsWith("matrix(")) {
			String tf = transform.substring("matrix(".length(),transform.length()-1 );
			 x = Double.parseDouble(tf.split(" ")[4]);
			 y = Double.parseDouble(tf.split(" ")[5]);
			
		}
		

		
		Font fontFromElement = getFontFromElement(node);
		if (fontFromElement==null) {
			spans = meta.getSubNodesWithTag("tspan", "");
		}else 
		tg.setFont(fontFromElement);
		
		tg.setText(node.getTextContent());
		tg.setAngleInDegrees(-angle);
		
		if (x==null) {
			x=0.0;
			IssueLog.log("Erroe: null x location");
		}
		if (y==null) {
			y=0.0;
			IssueLog.log("Erroe: null y location");
		}
		
		if (x!=null&&y!=null) tg.setLocation(x, y);
		tg.setTextColor(c);
		
		if(tg instanceof ComplexTextGraphic &&spans.size()>1) {
			ComplexTextGraphic comp=(ComplexTextGraphic) tg;
			TextParagraph para = comp.getParagraph();
			TextLine currentline = para.get(0);
			TextLineSegment currentSegment = currentline.get(0);
			TextLineSegment firstSegment = currentSegment;
			String st1=node.getTextContent();
			currentSegment.setText(st1);
			IssueLog.log("Set text content to "+node.getTextContent());
			DomMetaInfoWrapper last=meta;
			
			Double lastLiney=null;
			
			HashMap<TextLineSegment, DomMetaInfoWrapper> map=new HashMap<TextLineSegment, DomMetaInfoWrapper>();
			
			for(DomMetaInfoWrapper span: spans) {
				String string = span.getTextContent();
				Color color=getColorFromNode(span.getNode(), "fill");
				st1=st1.substring(0, st1.length()-string.length());
				firstSegment.setText(st1);
				IssueLog.log("y for span is "+span.getEntryAsString("y"));
				
				if(last!=null) {
				Double y2 = span.getEntryAsDouble("y");
				 lastLiney = last.getEntryAsDouble("y");
				IssueLog.log("ys are "+y2+  "   "+lastLiney);
				
				if ((y2!=null&& lastLiney==null)||(y2!=null&&y2>lastLiney)) {
					currentline=para.addLine();
					currentSegment=currentline.get(0);
					currentline.get(0).setText(string);
					currentline.get(0).setTextColor(color);
					
					Double x2 = span.getEntryAsDouble("x");
					if(x2!=null &&x2>tg.getX()) comp.getParagraph().setJustification(TextParagraph.JUSTIFY_CENTER);
				
				}  else currentSegment= currentline.addSegment(string,color);
				
				} else 
				currentSegment= currentline.addSegment(string,color);
				map.put(currentSegment, span);
				currentSegment.setFont(getFontFromElement(span.getNode()));
				IssueLog.log("Trying to set font to "+getFontFromElement(span.getNode()));
				
				
				if (span.getEntryAsString("baseline-shift").equals("super")) {
					currentSegment.makeSuperScript() ;
				}
				if (span.getEntryAsString("baseline-shift").equals("sub")) {
					currentSegment.makeSubScript() ;
				}
				
				
				
				last=span;
			}
			
			para.get(0).removeAllEmptySegments();
			
			
		}
		
		return tg;
	}
	
	/**Work in progress. Tries to read the text from a dom node*/
	ZoomableGraphic parseText(SVGOMTextElement node) {
		
		
		TextGraphic tg = new TextGraphic();
		DomMetaInfoWrapper meta = new DomMetaInfoWrapper(node);
		ArrayList<DomMetaInfoWrapper> spans = meta.getSubNodesWithTag("tspan", null);
		if (spans.size()>1)
			tg = new ComplexTextGraphic();
		
		
		
		
		Color c=getColorFromNode(node, "fill");
		if (spans.size()==1) {//for single span text, the properties are taken from the span
			DomMetaInfoWrapper span = spans.get(0);
			c=getColorFromNode(span.getNode(), "fill");
			tg.setFont(getFontFromElement(span.getNode()));
			
			
		}
		double angle=0;
	
		try {
			angle = node.getRotate().getBaseVal().getItem(0).getValue();
		} catch (DOMException e) {
		}
		
		
		 float x=0;
		float y=0;
		try {
			x = node.getX().getBaseVal().getItem(0).getValue();
			 y = node.getY().getBaseVal().getItem(0).getValue();
		} catch (DOMException e) {
		
		}
		
		
		
	//	IssueLog.log("value  is "+node.getNodeValue());
		//IssueLog.log(node.getTextContent());
		
		
		
		Font fontFromElement = getFontFromElement(node);
		if (fontFromElement==null) {
			spans = meta.getSubNodesWithTag("tspan", "");
		}else 
		tg.setFont(fontFromElement);
		
		tg.setText(node.getTextContent());
		tg.setAngleInDegrees(-angle);
		
		tg.setLocation(x, y);
		
		tg.setTextColor(c);
		
		if(tg instanceof ComplexTextGraphic &&spans.size()>1) {
			ComplexTextGraphic comp=(ComplexTextGraphic) tg;
			TextParagraph para = comp.getParagraph();
			TextLine currentline = para.get(0);
			TextLineSegment currentSegment = currentline.get(0);
			TextLineSegment firstSegment = currentSegment;
			String st1=node.getTextContent();
			currentSegment.setText(st1);
			IssueLog.log("Set text content to "+node.getTextContent());
			DomMetaInfoWrapper last=meta;
			
			Double lastLiney=null;
			
			HashMap<TextLineSegment, DomMetaInfoWrapper> map=new HashMap<TextLineSegment, DomMetaInfoWrapper>();
			
			for(DomMetaInfoWrapper span: spans) {
				String string = span.getTextContent();
				Color color=getColorFromNode(span.getNode(), "fill");
				st1=st1.substring(0, st1.length()-string.length());
				firstSegment.setText(st1);
				IssueLog.log("y for span is "+span.getEntryAsString("y"));
				
				if(last!=null) {
				Double y2 = span.getEntryAsDouble("y");
				 lastLiney = last.getEntryAsDouble("y");
				IssueLog.log("ys are "+y2+  "   "+lastLiney);
				
				if ((y2!=null&& lastLiney==null)||(y2!=null&&y2>lastLiney)) {
					currentline=para.addLine();
					currentSegment=currentline.get(0);
					currentline.get(0).setText(string);
					currentline.get(0).setTextColor(color);
					
					Double x2 = span.getEntryAsDouble("x");
					if(x2!=null &&x2>tg.getX()) comp.getParagraph().setJustification(TextParagraph.JUSTIFY_CENTER);
				
				}  else currentSegment= currentline.addSegment(string,color);
				
				} else 
				currentSegment= currentline.addSegment(string,color);
				map.put(currentSegment, span);
				currentSegment.setFont(getFontFromElement(span.getNode()));
				IssueLog.log("Trying to set font to "+getFontFromElement(span.getNode()));
				
				
				if (span.getEntryAsString("baseline-shift").equals("super")) {
					currentSegment.makeSuperScript() ;
				}
				if (span.getEntryAsString("baseline-shift").equals("sub")) {
					currentSegment.makeSubScript() ;
				}
				
				
				
				last=span;
			}
			
			para.get(0).removeAllEmptySegments();
			
			
		}
		
		return tg;
	}
	
	Font getFontFromElement(Node node) {
		DomMetaInfoWrapper meta = new DomMetaInfoWrapper(node);
		String fam = meta.getEntryAsString("font-family");
		 Double size = meta.getEntryAsDouble("font-size");
		
		if (fam.startsWith("'")) fam=fam.substring(1, fam.length()-1);
		
		IssueLog.log(fam);
		
		boolean bold =false;
		boolean ital=false;
		 
		try{
			bold ="bold".equals(meta.getEntryAsString("font-weight"));
			ital="italic".equals(meta.getEntryAsString("font-style"));
			}
					catch (Throwable r) {}
		
		int i = 0;
		if (bold) i+=Font.BOLD;
		if (ital) i+=Font.ITALIC;
		
		
		if(size==null||fam==null) return null;
		Font font = new  	Font(fam, i, size.intValue());
		return font;
	}
	
	
	
	private  ZoomableGraphic parseShape(Node node, BridgeContext bc) {
		if ( node instanceof Element) try {
			   GVTBuilder builder2 = new GVTBuilder();
			GraphicsNode root = (GraphicsNode) builder2 .build(bc, (Element) node);
		//	IssueLog.log("Bridge is"+bc.getBridge((Element) node));//sometimes returns a null bridge. from source, its because it lack a 
		 	if (root==null) {
				SVGOMDocument n=(SVGOMDocument) node.getOwnerDocument();
				bc=startingContext.createBridgeContext(n);
				 builder2.build(bc, n);
				root = (GraphicsNode) builder2 .build(bc, (Element) node);
			
			}
			/*
			if (root==null) {
				testWorkAround tw = new testWorkAround();
				tw.setDocument(node.getOwnerDocument());
				bc=tw.letBrindgeContextBePublic();
				root=(GraphicsNode) builder2 .build(bc, (Element) node);
				if (root!=null) IssueLog.log("Got shape with atttemot 1 with bridge "+bc.getBridge((Element) node));
			}//tries with a new bridge context if the attempt failed
			
			
			if (root==null) {
				UserAgentAdapter uaad = new UserAgentAdapter() {};
				DocumentLoader loader = new DocumentLoader(uaad);
				SVGOMDocument n=(SVGOMDocument) node.getOwnerDocument();
				if (n.isSVG12()) {
					bc= new SVG12BridgeContext(uaad, loader);
				} else new BridgeContext(uaad, loader);
				
				root=(GraphicsNode) builder2 .build(bc, (Element) node);
				if (root!=null) IssueLog.log("Got shape with atttemot 2 with bridge "+bc.getBridge((Element) node));
			}//tries again if still fails
			/***/
			
			if (root instanceof ShapeNode) {
				ShapeGraphic shape = printShapeNode(null, (ShapeNode) root, "");
				if(shape.getStrokeJoin()==BasicStroke.JOIN_ROUND &&shape.isClosedShape()) shape.setStrokeCap(BasicStroke.CAP_ROUND);
				if(shape.getStrokeJoin()==BasicStroke.JOIN_MITER &&shape.isClosedShape()) shape.setStrokeCap(BasicStroke.CAP_SQUARE);
				
				shape.setAntialize(true);
				parseID(node, shape);
				return shape;
			} else { 
				IssueLog.log("Error: node named "+node.getNodeName()+" is could not be used to build a shape node "+root);
				String nodename = node.getNodeName();
				String trimed = nodename.trim();
				
				if (trimed.equals("rect")) {
					IssueLog.log("Error reading rectangle ");
					return  parseRect(node);
				}
			}
		
			
		} catch (Throwable t) {
			t.printStackTrace();
		} else {
			IssueLog.log("Error: node named "+node.getNodeName()+" is not an element node for a shape");
		}
		
		return null;
	}
/**
	private  ZoomableGraphic parsePath(Node node) {
		AWTPathProducer producer = new AWTPathProducer();
		PathParser pp = new PathParser();
		pp.setPathHandler(producer);
		// TODO Auto-generated method stub
		pp.parse(node.getAttributes().getNamedItem("d").getNodeValue());
		PathGraphic outputgraphic = new PathGraphic();
		outputgraphic.setPathToShape(producer.getShape());
		
		shapeAttsToDom(node, outputgraphic);
		
		
		return outputgraphic;
	}
	shap
	
	
	private  ZoomableGraphic parseCircle(Node node) {
		
		DomMetaInfoWrapper meta = new DomMetaInfoWrapper(node);
		
		Double x = meta.getEntryAsDouble("rx");
		Double y = meta.getEntryAsDouble("ry");
		Double width = meta.getEntryAsDouble("cx");
		Double height = meta.getEntryAsDouble("cy");
		
		OvalGraphic outputgraphic = new OvalGraphic(new Rectangle2D.Double(x,y,width,height).getBounds());
		
		
		shapeAttsToDom(node, outputgraphic);
		
		
		return outputgraphic;
	}*/
	
	
private  ZoomableGraphic parseRect(Node node) {
		
		DomMetaInfoWrapper meta = new DomMetaInfoWrapper(node);
		RectangularGraphic outputgraphic = new RectangularGraphic();
		Double x = meta.getEntryAsDouble("x");
		Double y = meta.getEntryAsDouble("y");
		Double width = meta.getEntryAsDouble("width");
		Double height = meta.getEntryAsDouble("height");
		outputgraphic.setRectangle(new Rectangle2D.Double(x,y,width,height));
		
		shapeAttsToDom(node, outputgraphic);
		
		
		return outputgraphic;
	}
	
	private  ZoomableGraphic parseImage(Node node) {
		
		DomMetaInfoWrapper meta = new DomMetaInfoWrapper(node);
		String st=meta.getEntryAsString("xlink:href");
		Double x=meta.getEntryAsDouble("x");
		Double y=meta.getEntryAsDouble("y");
		Double w=meta.getEntryAsDouble("width");
		Double h=meta.getEntryAsDouble("height");
		Double scale=null;
		
		String transform=meta.getEntryAsString("transform");
		if(transform.startsWith("matrix")) {
			String tf = transform.substring("matrix(".length(),transform.length()-1 );
			 scale= Double.parseDouble(tf.split(" ")[0]);
			 x = Double.parseDouble(tf.split(" ")[4]);
			 y = Double.parseDouble(tf.split(" ")[5]);
			
			
		}
		
		if(x==null) x=(double) 0;
		if(y==null) y=(double) 0;
		
		//PNGDecodeParam param = new PNGDecodeParam();
		
		//String dc0 = "data:image/png;base64,";
		byte[] imageData=decodeEmbedSVG(st);
		
		BufferedImage img =null;
		
		if (imageData!=null) try {
			
			 img = ImageIO.read(new ByteArrayInputStream(imageData));
			
		} catch (Throwable t) {
			t.printStackTrace();
		} else {
			 try {
				 String path=node.getOwnerDocument().getDocumentURI();
				 if(path.startsWith("file:")) path=path.substring("file:".length());
				File sourceFile = new File(path);
				
				IssueLog.log("Path for image file is "+'\n'+sourceFile.getParentFile().getAbsolutePath() +"/"+st);
				
				
				img = ImageIO.read(new FileInputStream(sourceFile.getParentFile().getAbsolutePath() +"/"+st));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		
		if (img!=null) try {
			
				//BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
				
				BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
				//BufferedImage image=ImageIO.read(stream);
			    // Draw the image on to the buffered image
				
				
				
			   Graphics2D bGr = image.createGraphics();
			    bGr.drawImage((Image)img, 0, 0, null);
			    bGr.dispose();
				
				//byte[] imageData = dataUrl.getBytes();
				
				
			    BufferedImageGraphic big = new BufferedImageGraphic(image,x,y);
			   if(scale!=null) big.setRelativeScale(scale);
			    double ratio = w/img.getWidth();
			     double ratio2 = h/img.getHeight();
			     if(Math.abs(ratio-ratio2)>0.01) {IssueLog.log("pan aspect ratio is uneven");}
			   if (scale==null) big.setRelativeScale(ratio);
			    
			    big.setFrameWidthH(0);
			    big.setFrameWidthV(0);
			    
				
			    
			    
			    
			    
			    return big;
			    // Return the buffered image
			   
			} catch (Exception e) {
				// TODO Auto-generated catch block
				IssueLog.logT(e);
				e.printStackTrace();
				
				return null;
			}
			IssueLog.log("did not attempt parse image embeded");
			return null;
	}
	
	byte[] decodeEmbedSVG(String st) {
		String dc0 = "data:image/png;base64,";
		if (st.startsWith(dc0)) try {
		
			String dataUrl = st.substring(dc0.length());
			byte[] imageData = Base64.decodeBase64(dataUrl);
			
			return imageData;
		
		} catch (Throwable t) {
				t.printStackTrace();
				
			}
		
		return null;
	}
	
	
	public  void parseID(Node node, BasicGraphicalObject s) {
		DomMetaInfoWrapper meta = new DomMetaInfoWrapper(node);
		s.setName(meta.getEntryAsString("id"));
	}
	
	 void shapeAttsToDom(Node node, ShapeGraphic s) {
		
		parseID(node, s);
		
		s.setFillColor(getColorFromNode(node, "fill"));
		
		Color stroke_color = getColorFromNode(node, "stroke");
		IssueLog.log("Stroke was parsed as "+stroke_color);
		
		s.setStrokeColor(stroke_color);
		setShapeStrokeToElement(node,s);
		
		
	}
	 
	 
	 void setShapeStrokeToElement(Node node, ShapeGraphic s) {
		 DomMetaInfoWrapper meta = new DomMetaInfoWrapper(node);
			
			Double stroke_width = meta.getEntryAsDouble("stroke-width");
			Double stroke_miter = meta.getEntryAsDouble("stroke-miterlimit");
			
			String stroke_join = meta.getEntryAsString("stroke-linejoin");
			String stroke_linecap = meta.getEntryAsString("stroke-linecap");
			float[] dashes=(float[]) meta.getEntryAsDestringedClass("stroke-dasharray", float[].class);
			
			s.setStrokeJoin(stroke_join);
			s.setStrokeCap(stroke_linecap);
			
			if (stroke_miter!=null)
			s.setMiterLimit(stroke_miter);
			if (stroke_width!=null)
			s.setStrokeWidth(stroke_width.floatValue());
			if (dashes!=null)
			s.setDashes(dashes);
	 }
	
	/**
	public  void getFromMeta(ShapeGraphic s, MetaInfoWrapper meta ) {
		BasicMetaDataHandler han = new BasicMetaDataHandler();
		s.setName(han.getEntryFromInfoAsString(meta, "id"));
		
		
	}*/
	
	 Color getColorFromNode(Node node, String name) {
		DomMetaInfoWrapper meta = new DomMetaInfoWrapper(node);
		
		Color c=(Color) meta.getEntryAsDestringedClass( name, Color.class);
		IssueLog.log("Got color "+name+" from dom node as "+c);
		
		double opacity=1;
		try{
		  opacity=meta.getEntryAsDouble( name+"-opacity");} catch (Throwable r) {}
		
		
		if (c==null) return Color.black;
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(255*opacity));
	}
	
	public  StandardWorksheet openDisplaySVG(String path) throws IOException {
		StandardWorksheet set = openSVG(path);
		return set;
	}
	



	
	public  HashMap<String, String> printAttributes(NamedNodeMap att) {
		HashMap<String, String> o=new HashMap<String, String>();
		int l = att.getLength();
		
		for(int i=0; i<l; i++) {
			Node item = att.item(i);
			String att_name=item.getNodeName();
			String att_value=item.getNodeValue();
			//IssueLog.log(att_name+" = "+att_value);
			o.put(att_name, att_value);
		}
		
		
		return o;
	}
	
	/**
	public  void pasteSVG(String path, GraphicLayer ex, boolean showPrev) throws IOException {
		

		String parser = XMLResourceDescriptor.getXMLParserClassName();
	    SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
	    String uri = path;
	    SVGOMDocument doc = (SVGOMDocument) f.createDocument(uri);
	   
	   
	   BridgeContext bc = showPreview(doc, showPrev);
	    GVTBuilder builder2 = new GVTBuilder();
		RootGraphicsNode root = (RootGraphicsNode) builder2 .build(bc, doc);
		
		
		printRootnode(ex, root, "");
		
	}*/
	
	
	
	public  BridgeContext showPreview(SVGOMDocument doc, boolean showPrev) {
		 testWorkAround tw = new testWorkAround();
		    JSVGCanvas canvas = tw;
		    
		    
		   
		    
		    canvas.setDocument(doc);
		 
		  
		   JFrame fram = new JFrame();
		   fram.add(canvas);
		   
		   fram.pack();
		   
		   fram.setVisible(true);;
		   canvas.repaint();
		   
		   if (!showPrev) fram.setVisible(false);;
		 
		   BridgeContext bc = tw.letBrindgeContextBePublic();
			  
			
			
			return bc;
	}
	
	public static void main(String [] args) throws Exception {
		
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		//String path="/Users/mazog/Desktop/test.svg";
		String path=new DirectoryHandler().getFigureFolderPath()+"/export 5.svg";
		IssueLog.log(new File(path).exists());
		//loadClass("org/w3c/dom/Window");
		
		
		StandardWorksheet set = new GraphicSVGParser().openSVG(path);
		if (set==null) return ;
		ImageWindowAndDisplaySet output = new ImageWindowAndDisplaySet(set);
		
		new CanvasAutoResize(true).performActionDisplayedImageWrapper(output);
		
   
	}
	
	/**
	 void printRootnode(GraphicLayer layer, CompositeGraphicsNode root, String prefix) {
		for(Object node: root) {
			IssueLog.log("read "+node);
			if (node instanceof CompositeGraphicsNode ) {
				CompositeGraphicsNode n=(CompositeGraphicsNode) node;
				GraphicLayerPane layer2 = new GraphicLayerPane("");
				if (layer!=null)layer.add(layer2);
				printRootnode(layer2, n, "Child of "+ prefix);
			}
			
			if (node instanceof org.apache.batik.gvt.ShapeNode) {
				ShapeNode nshape=(ShapeNode) node;
				printShapeNode(layer, nshape, prefix);
			}
			
			if (node instanceof TextNode) {
				TextNode nshape=(TextNode) node;
				printTextNode(layer, nshape, prefix);
			}
		}
	}
	
	
	private  void printTextNode(GraphicLayer layer, TextNode nshape, String prefix) {
		TextGraphic t=new TextGraphic(nshape.getText());
		t.setLocation(nshape.getLocation());
		layer.add(t);
		
		if (nshape.getTextPainter() instanceof StrokingTextPainter) {
			StrokingTextPainter stp=(StrokingTextPainter) nshape.getTextPainter();
			StrokingTextPainter.TextRun runs = (TextRun) stp.getTextRuns(nshape, nshape.getAttributedCharacterIterator()).get(0);
			TextPaintInfo pinfo= (TextPaintInfo) nshape.getAttributedCharacterIterator().getAttribute(GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO);
			t.setTextColor((Color)pinfo.strokePaint);
			
			IssueLog.log("fill color is "+pinfo.fillPaint);
			IssueLog.log("stroke color is "+pinfo.strokePaint);
		}
		IssueLog.log("text painer is "+nshape.getTextPainter());
		//IssueLog.log("text run is "+nshape.getTextRuns().get(0));
		
		
	}*/

	private  ShapeGraphic createShapeGrapicForItem(Shape shape) {
			if (shape instanceof Rectangle2D) {
				return new RectangularGraphic(shape.getBounds());
			}
			if (shape instanceof Ellipse2D) {
				return new CircularGraphic(shape.getBounds());
			}
			/**
			if (shape instanceof Path2D) {
				return new PathGraphic((Path2D) shape);
			}*/
		
			PathIterator pi = shape.getPathIterator(null);
			PathPointList list = PathPointList.createFromIterator(pi);
			PathGraphic out = new PathGraphic(list);
			
			
			//designed to set the pathGraphic to closed if it is closed shape
						double[] d=new double[6];
						pi = shape.getPathIterator(null);
						 int type=pi.currentSegment(d);
						while (!pi.isDone()) 	{
							type=pi.currentSegment(d);
						if(type==PathIterator.SEG_CLOSE)
							{out.setClosedShape(true); ;}else {out.setClosedShape(false);
							}
						  
						pi.next();
						}
		  
		 
		   
		   
		   
			return  out;
			
	}
	
	private  ShapeGraphic printShapeNode(GraphicLayer layer, ShapeNode nshape, String prefix) {
		ShapeGraphic s=createShapeGrapicForItem(nshape.getShape());
		s.setDashes(null);
		s.setFilled(true);
		if (layer!=null)layer.add(s);
		
		
		setToShapePainter(s, nshape.getShapePainter());
		
		if(nshape.getShapePainter() instanceof FillShapePainter) {
			s.setStrokeWidth(0);
		}
		if(nshape.getShapePainter() instanceof StrokeShapePainter) {
			s.setFilled(false);
		} else if (s instanceof PathGraphic){
			PathGraphic p=(PathGraphic) s;
			p.setUseFilledShapeAsOutline(true);
		}
		
		 if (s instanceof PathGraphic){
				PathGraphic p=(PathGraphic) s;
				if(p.getPoints().size()<3) 	p.setUseFilledShapeAsOutline(false);
			}
		
		 return s;
	}
	
	
	boolean setToShapePainter(ShapeGraphic s, ShapePainter paint2) {
		
		if (paint2 instanceof CompositeShapePainter) {
			 CompositeShapePainter csp=(CompositeShapePainter) paint2;
			// IssueLog.log("Shape painter is composite of "+csp.getShapePainterCount());
			 for(int i=0; i<csp.getShapePainterCount(); i++) {
				 		ShapePainter paint3 = csp.getShapePainter(i);
				 		setToShapePainter(s, paint3);
						
			 }
		 }
		
		 if (paint2 instanceof FillShapePainter) {
			 FillShapePainter fsp=(FillShapePainter) paint2;
			// IssueLog.log("fill paint is " +fsp.getPaint());
			 s.setFillColor((Color)fsp.getPaint());
		 }
		 if (paint2 instanceof StrokeShapePainter) {
				StrokeShapePainter fsp=(StrokeShapePainter) paint2;
				// s.setStrokeColor((Color)fsp.getPaint());
				 Stroke stroke = fsp.getStroke();
				if (stroke instanceof BasicStroke) {
					BasicStrokedItem bsi = new BasicStrokedItem();
					bsi.setStroke((BasicStroke) stroke);
					BasicStrokedItem.copyStrokeProps(s, bsi);
				}
				 s.setStrokeColor((Color)fsp.getPaint());
			 }
		
		return true;
	}

	 class testWorkAround extends JSVGCanvas {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * 
		 */
		
		
	public BridgeContext letBrindgeContextBePublic( ) {
		return super.bridgeContext;
	}
	
	}
	/**
	 class my_workaround extends JSVGCanvas {

		
		private  final long serialVersionUID = 1L;
		
		public my_workaround() { 
		}
		
		public UserAgent borrowAgent() {
			return super.createUserAgent();
			
		}
		
		public BridgeContext borrowContext() {
			
			
			
			return super.bridgeContext;
			
		}
		
		
	}*/

}
