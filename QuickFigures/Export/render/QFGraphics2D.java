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
 * Date Created: Nov 27, 2021
 * Date Modified: Nov 20, 2022
 * Version: 2023.2
 */
package render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;

import org.apache.batik.ext.awt.g2d.AbstractGraphics2D;
import org.apache.batik.ext.awt.g2d.GraphicContext;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.FreeformShape;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.PathPointList;
import logging.IssueLog;
import messages.ShowMessage;

/**
 A work in progress. Does not implement every aspect of Graphics2D. implementation of Graphics2D. Good enough to import r plots using Apache PDFBox and
 make them visible as vector graphics. However those objects cannot be re-exported into vector graphic formats correctly.
 */
public class QFGraphics2D extends AbstractGraphics2D {

	private ArrayList<ZoomableGraphic> layer=new ArrayList<ZoomableGraphic>();
	
	ShapeGraphic lastShapeObject=null;
	Shape lastShape=null;
	private Shape lastUntransformed;
	private AffineTransform lastTransform;
	private ShapeGraphic lastAdded;
	int count=1;
	private boolean dontAutoSubgroup=true;
	
	boolean asPath=true;
	private int shapecount;
	private long startShapetime;
	
	public QFGraphics2D() {
		super(false);
		
		if(gc==null)
			gc=new GraphicContext();
	}
	
	public QFGraphics2D(ArrayList<ZoomableGraphic> g) {
		this();
		this.layer=g;
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		IssueLog.logOptional("rendered image draw not yet implemented");
		throw new NullPointerException();
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		IssueLog.logOptional("rendered image draw not yet implemented");
		
	}

	@Override
	public void drawString(String str, float x, float y) {
		addTextGraphic(str, x, y);
		
	}
	
	

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		IssueLog.logOptional("Drawing string method not implemented for "+iterator.toString());
		
	}


	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return new java.awt.Canvas().getGraphicsConfiguration();
		
	}

	

	@Override
	public Graphics create() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void setXORMode(Color c1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return	new java.awt.Canvas().getFontMetrics(f);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean drawImage(Image img,
            AffineTransform xform,
            ImageObserver obs){ 
		
		GraphicsRenderQF.lastImageX=xform.getTranslateX();//stores the location 
		GraphicsRenderQF.lastImageY=xform.getTranslateY();
		return super.drawImage(img, xform,obs);
	}

	@Override
	public boolean drawImage(Image image, int x, int y, ImageObserver io) {
		
		
		BufferedImage bi = createImageWithColorModel(image, io);
		if(image instanceof BufferedImage) {
			bi=(BufferedImage) image;
		}
		ImagePanelGraphic graphicImage = new ImagePanelGraphic(bi);
		 Point2D point1 = new Point2D.Double(x,y);
		 this.getTransform().transform(point1, point1);
		graphicImage .setLocationUpperLeft(point1);
		getLayer().add(graphicImage);
		GraphicsRenderQF.lastImage=graphicImage;
		return true;
	}

	/**
	 * @return
	 */
	public ArrayList<ZoomableGraphic> getLayer() {
		
		return layer;
	}
	
	

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		IssueLog.logOptional("image draw not yet implemented "+2);
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void drawLine(int x1, int y1,int x2, int y2) {
		Double path =new Line2D.Double(x1, y1, x2, y2);
		
		Shape s = this.getTransform().createTransformedShape(path);
		addShape(s,false);
		ShowMessage.showOptionalMessage("line drawing is a work in progress");
		
	}
	
	@Override
	public void drawPolyline(int[] x1, int[] y1,int x2) {
		
		ShowMessage.showOptionalMessage("poly line drawing is a work in progress");
		
	}
	
	@Override
	public void drawPolygon(Polygon p) {
		
		ShowMessage.showOptionalMessage("polygon drawing is a work in progress");
		super.drawPolygon(p);
	
	}
	
	@Override
	public void drawPolygon(int[] x1, int[] y1,int x2) {
		
		ShowMessage.showOptionalMessage("polygon 2 drawing is a work in progress");
	
	
	}
	
	@Override
	public void drawRect(int x, int y, int w, int h) {
		draw(new Rectangle(x,y,w,h));
		ShowMessage.showOptionalMessage("rect drawing is a work in progress");

	}
	
	@Override
	public void fillRect(int x, int y, int w, int h) {
		fill(new Rectangle(x,y,w,h));
		//ShowMessage.showOptionalMessage("fill rect drawing is a work in progress");

	}
	@Override
	public void drawRoundRect(int x, int y, int w, int h, int aw, int ah) {
		
		ShowMessage.showOptionalMessage("round rect drawing is a work in progress");

	}
	
	@Override
	public void fillRoundRect(int x, int y, int w, int h, int aw, int ah) {
		
		ShowMessage.showOptionalMessage("round rect drawing is a work in progress");

	}
	
	@Override
	public void draw3DRect(int x, int y, int w, int h, boolean r) {
		ShowMessage.showOptionalMessage("3d rect drawing is a work in progress");

	}
	
	@Override
	public void fill3DRect(int x, int y, int w, int h, boolean r) {
		ShowMessage.showOptionalMessage("3d rect drawing is a work in progress");

	}
	
	@Override
	public void drawArc(int x, int y, int w, int h, int sa, int r) {
		ShowMessage.showOptionalMessage("arc drawing is a work in progress");
		
	}
	
	@Override
	public void draw(Shape s) {
		boolean putonLastShape = false;
		
		
		if(s==lastUntransformed||  s.equals(lastUntransformed))
			putonLastShape = true;
		/**meant to check if the shapes are nearly the same but not perfect*/
		if(isHighlySimilarToLastShape(s, lastUntransformed)) putonLastShape = true;
		
		if(!getTransform().equals(lastTransform))
			putonLastShape = false;
		
		if(!putonLastShape)s=this.getTransform().createTransformedShape(s);
		addShape(s, putonLastShape);
		
		
		
	}

	boolean hideShapes=false;
	private ArrayList<Object> shapeList=new ArrayList<Object>();
	
	/**
	 * @param s
	 * @param putonLastShape
	 */
	public void addShape(Shape s, boolean putonLastShape) {
		shapecount++;
		if(shapecount==1) {
			startShapetime=System.currentTimeMillis();
		}
		if(shapecount>1000&&shapecount%1000==0) {
			//long ctime = System.currentTimeMillis()-startShapetime;
			//boolean proceed = ShowMessage.showOptionalMessage("will have difficulty drawing so many shapes: ", false, "will have difficulty drawing so many shapes: "+shapecount, "Are you sure you want to continue?", "time since start is "+ctime);
	
		}
		if(hideShapes)
			{
			shapeList.add(s);
			return;
			}
		
		
		
		
		//if(!getClip().getBounds2D().contains(s.getBounds2D())) {return;}
		
		if(putonLastShape&&this.getStroke() instanceof BasicStroke)
			{
			BasicStroke stroke = (BasicStroke) this.getStroke();
			lastShapeObject.setStroke(stroke);
			lastShapeObject.setStrokeColor(getColor());
			
			}
		else if(this.getStroke() instanceof BasicStroke) {
			
			ShapeGraphic bs = createShapeGraphic(s);
			
			//bs.setFilled(false);//not sure why but when some shapes are set to filled here, they are made black when exported as SVG
			bs.setFillColor(new Color(0,0,0,0));
			bs.setStrokeColor(getColor());
			BasicStroke stroke = (BasicStroke) this.getStroke();
			bs.setStroke(stroke);
			if(s.getBounds().width*s.getBounds().height==0)
				return;			
			
			//handleSublayergrouping(lastAdded, bs);
			
			if(s.getBounds().width*s.getBounds().height==0||bs.getStrokeWidth()==0)
						{
				//shapes with no volumne are ignored
						}
			else {
				getLayer().add(bs);
				lastAdded=bs;
			}
		}
		
		lastShape=null;
		lastShapeObject=null;
		lastUntransformed=null;
	}



	/**
	 * @param s
	 * @return
	 */
	public boolean isHighlySimilarToLastShape(Shape s, Shape lastShape) {
		return lastShape!=null&&s.getBounds()!=null&&s.getBounds().equals(lastShape.getBounds());
	}
	@Override
	public void fill(Shape s) {
		
		if(hideShapes)
			return;
		lastUntransformed=s;
		s=this.getTransform().createTransformedShape(s);
		ShapeGraphic newShape = createShapeGraphic(s);
		if(s.getBounds().width*s.getBounds().height==0)
			return;
		//handleSublayergrouping(lastAdded, newShape);
		
		
		if(s.getBounds().width*s.getBounds().height==0)
		{}
		else {
			getLayer().add(newShape);
			lastAdded=newShape;
		}
		
		newShape.setStrokeWidth(0);
		newShape.setFillColor(getColor());
		lastShape=s;
		lastShapeObject=newShape;
		lastTransform=this.getTransform();
		
	}

	/**
	 * @param s
	 * @return
	 */
	public ShapeGraphic createShapeGraphic(Shape s) {
		
		
		if(s instanceof Rectangle2D)
			return new RectangularGraphic( (Rectangle2D)s);
		if(s instanceof Ellipse2D)
		{
			
			return new CircularGraphic( ((Ellipse2D)s).getBounds2D());
			
		}


		if(s instanceof Path2D) {
			
		}
		ShapeGraphic output =null;
	
		
		 output = new FreeformShape(s);
		output.setName("Shape "+count);
		count++;
		
		
		return output;
		
		
	}
	/**
	 * @param str
	 * @param x
	 * @param y
	 */
	protected void addTextGraphic(String str, double x, double y) {
		IssueLog.logOptional("PDF text items not fully implemented");
		TextGraphic t = new TextGraphic(str);
		t.setLocation(x, y);
		
		getLayer().add(t);
	}


	/**Given a buffered image and color model, generates another buffered image with the same raster data but
	 * with the given color model*/
	private  BufferedImage createImageWithColorModel(Image pid, ImageObserver io) {
		
		
		DirectColorModel cm =  new DirectColorModel(32, 0xff0000, 0xff00, 0xff, 0xff000000);
		int width=pid.getWidth(io);
		int height=pid.getHeight(io);
		
		
		BufferedImage bi = new BufferedImage(cm, cm.createCompatibleWritableRaster(pid.getWidth(io), pid.getHeight(io)), false, null);
		bi.getGraphics().drawImage(pid,0, 0, width, height, 0, 0, width, height, null);
       
        return bi;
	}
	
	

	/**
	 * @param z1
	 * @param z2
	 */
	private int simiarity(ZoomableGraphic z1, ZoomableGraphic z2) {
		int output = 0;
		if(z1 instanceof BasicShapeGraphic && z2 instanceof BasicShapeGraphic) {
			Rectangle2D b1 = ((BasicShapeGraphic) z1).getShape().getBounds2D();
			Rectangle2D b2 = ((BasicShapeGraphic) z2).getShape().getBounds2D();
			
			int threshold = 12;
			if(Math.abs(b1.getX()-b2.getX())<threshold)
				output++;
			if(Math.abs(b1.getY()-b2.getY())<threshold)
				output++;
			if(Math.abs(b1.getWidth()-b2.getWidth())<threshold)
				output++;
			if(Math.abs(b1.getHeight()-b2.getHeight())<threshold)
				output++;
			if(Math.abs(b1.getMaxX()-b2.getMaxX())<threshold)
				output++;
			if(Math.abs(b1.getMaxY()-b2.getMaxY())<threshold)
				output++;
			if(b1.contains(b2)||b2.contains(b1))
				output++;
			
			double area1 = b1.getWidth()*b1.getHeight();
			double area2 = b2.getWidth()*b2.getHeight();
			if(area1>20&&area2>20&&area1-area2> threshold)
				output-=2;
			
			
		}
		
		if(z1!=null&&z2!=null&&z1.getClass()!=z2.getClass())
			return 0;
		
		
		return output;
	}
}
