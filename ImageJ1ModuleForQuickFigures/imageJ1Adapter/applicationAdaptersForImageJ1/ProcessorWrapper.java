package applicationAdaptersForImageJ1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;

import applicationAdapters.PixelWrapper;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.process.Blitter;
import ij.process.ByteBlitter;
import ij.process.ByteProcessor;
import ij.process.ColorBlitter;
import ij.process.ColorProcessor;
import ij.process.FloatBlitter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortBlitter;
import ij.process.ShortProcessor;
import logging.IssueLog;
import utilityClassesForObjects.RectangleEdges;

public class ProcessorWrapper implements PixelWrapper {
	public ImageProcessor object;
	
	public ProcessorWrapper(ImageProcessor ip) {
		this.object=ip;
	}
	
	public ProcessorWrapper createnew(
			ImageProcessor object) {
		return new ProcessorWrapper(object);
	}


	@Override
	public void insertInto(PixelWrapper recipient, int x, int y) {
		Object ob = recipient.getPixels();
		if (ob instanceof ImageProcessor) {
		((ImageProcessor)ob ).insert(getPixels(), x, y);
		}
		
	}


	public ImageProcessor getPixels() {
		return object;
	}


	public void setPixels(ImageProcessor ip) {
		object=ip;
		
	}

	
	

	@Override
	public void resize(double x, double y) {
		object=object.resize((int)x, (int)y);
		
	}
	
	@Override
	public void resizeBilinear(double width, double height) {
		object.setInterpolationMethod(ImageProcessor.BILINEAR);
		object=object.resize((int)width, (int)height);
		
	}
	
	@Override
	public void scaleBilinear(double scale) {
		resizeBilinear(object.getWidth()*scale, object.getHeight()*scale);
		
	}


	@Override
	public Image image() {
		return object.createImage();
	}
	
	public ProcessorWrapper copy(Rectangle r) {
		   object.setRoi(r);
		   return createnew(object.crop());
		
		//return createnew(dataAdapter().cropped(object, r));
		}
	
	public ProcessorWrapper cut(Rectangle r) {
		ProcessorWrapper output = copy(r);
		clear(r);
		return output;
		}
	
	public void clear(Shape r) {fill(r, Color.WHITE);}
	public void fill(Shape r, Color c)  {
		if (object==null|| r==null||c==null) return;
		   Roi clear=  new ShapeRoi(r);
		   object.setColor(c);
		   object.fill(clear);
	}


	@Override
	public int width() {
		// TODO Auto-generated method stub
		return object.getWidth();
	}


	@Override
	public int height() {
		// TODO Auto-generated method stub
		return object.getHeight();
	}

	

	@Override
	public Dimension dim() {
		return new Dimension(width() , height());
	}

	@Override
	public PixelWrapper makenew(Dimension d) {
		ImageProcessor newone;
		if (object==null)  newone=new ColorProcessor(d.width, d.height);
		else newone=object.createProcessor(d.width, d.height);
			newone.setColor(Color.white);
			newone.fill();
			return 	new ProcessorWrapper(newone);
				
	}

	@Override
	public void crop(Rectangle r) {
		if (object==null) return;
			object.setRoi(r);
		object=object.crop();
		
	}
	
	private ImageProcessor getProcessor() {
		return getPixels();
	}
	
	/**draws the object. implementation may differ based on what object.
	  Written to take imageJ rois*/
	public void drawPixelObject(Object lc) {
		if (lc instanceof RoiWrapper) {
			RoiWrapper roiw =(RoiWrapper) lc;
			Roi roi =roiw.getObject();
			drawRoi(roi, roiw.isFilled());
		}
		if (lc instanceof Roi) {
			Roi roi=(Roi) lc;
			drawRoi(roi, roi.getFillColor()!=null);
		}
		
	}
	
	private void drawRoi(Roi roi, boolean fill) {
		getProcessor().setColor(roi.getStrokeColor());
		
		if (roi.getFillColor()!=null) getProcessor().setColor(roi.getFillColor());
		if (fill) getProcessor().fill(roi); else roi.drawPixels(getProcessor()) ;
	}
	
public void drawPixelString(String label, Color c,  Font font, int lx, int ly, double angle, boolean antialiasedText ) {
	 ImageProcessor ip=getPixels();
	getProcessor().setAntialiasedText(antialiasedText);
	 getProcessor().setFont(font);
	 getProcessor().setColor(c);
if (angle==0||angle==360) {
	
	ip.drawString(label, lx, ly+ip.getFont().getSize()); 
//if (debugmode)	ip.drawRect(lx, ly, sWidth(label), ip.getFont().getSize());
}else
	drawRotatedString(label, getProcessor(), c, lx, ly, angle);
	}


public static void drawRotatedString(String st, ImageProcessor ip, Color c, int x, int y, double angle){
	boolean aboutcenter=false;
	int swidth=ip.getStringWidth(st);
	int sheight=ip.getFont().getSize();
	int ssquare=swidth;
	if (ssquare<sheight) {ssquare=ip.getFont().getSize();}
	ssquare+=ip.getFont().getSize();
	ImageProcessor rotatable=ip.createProcessor(ssquare*2, ssquare*2) ;
	Color tempBG=Color.white;
	if (c.equals(Color.white)) {tempBG=Color.black;rotatable.setBackgroundValue(0) ;}
	rotatable.setColor(tempBG); rotatable.fill(); rotatable.setColor(c);
	rotatable.setFont(ip.getFont());
	if (!aboutcenter) rotatable.drawString(st, ssquare, ssquare);
	if (aboutcenter) rotatable.drawString(st, ssquare-swidth/2, ssquare-sheight/2);
	//rotatable.setInterpolationMethod(ImageProcessor.BILINEAR);
	rotatable.rotate(angle);
	Blitter b1 = null;
	if (ip instanceof ColorProcessor) b1=new ColorBlitter((ColorProcessor) ip);
	if (ip instanceof ByteProcessor) b1=new ByteBlitter((ByteProcessor) ip);
	if (ip instanceof FloatProcessor) b1=new FloatBlitter((FloatProcessor) ip);
	if (ip instanceof ShortProcessor) b1=new ShortBlitter((ShortProcessor) ip);
	b1.setTransparentColor(tempBG);
	if (!aboutcenter) b1.copyBits(rotatable, x-ssquare+sheight, y-ssquare+swidth, Blitter.COPY_TRANSPARENT) ;
	if (aboutcenter) b1.copyBits(rotatable, x-ssquare+swidth/2+sheight, y-ssquare+sheight/2+swidth, Blitter.COPY_TRANSPARENT) ;
}

@Override
public ColorModel getColorModel() {
	// TODO Auto-generated method stub
	return object.getColorModel();
}

@Override
public boolean isRGB() {
	// TODO Auto-generated method stub
	return object instanceof ColorProcessor;
}

@Override
public int[] getDistribution() {
	 
		
			return object.getHistogram();
		
}

@Override
public void cropAtAngle(Rectangle r, double angle) {
	if(angle%Math.PI==0) {this.crop(r);return;}
	if(angle%Math.PI/2==0) {
		IssueLog.log("performing 90 deg crop");
		Rectangle r2 = new Rectangle(r);
		r2.width=r.height; r2.height=r.width; r2.x+=(r.width-r2.width)/2; r2.y+=(r.height-r2.height)/2;
		this.crop(r2);
		return;
		}
	
	/**first performs an easy crop that just gets rid of extra pixels*/
	Shape firstLevelCrop = AffineTransform.getRotateInstance(-angle, r.getCenterX(), r.getCenterY()).createTransformedShape(r);
	Rectangle bounds = firstLevelCrop.getBounds(); if (bounds.x>r.x||bounds.y>r.y) {
		/** At certain angles for rectangles with high aspect ratio, the bounds of the rotated rectangle does not include
		  all of the region of interest. Problem might occur under these conditions so the next few lines alter*/
		Point2D center = RectangleEdges.getLocation(RectangleEdges.CENTER, bounds);
		if(bounds.height>bounds.width)bounds.width=bounds.height;
		if(bounds.height<bounds.width)bounds.height=bounds.width;
		RectangleEdges.setLocation(bounds, RectangleEdges.CENTER, center.getX(), center.getY());
	}
	angle=angle*180/Math.PI;
	this.crop(bounds);
	
	/**now rotates the smaller image*/
	object.setInterpolationMethod(ImageProcessor.BILINEAR);
	object.rotate(angle);
	
	Rectangle r2 = new Rectangle(r);
	r2.x-=bounds.getMinX();
	r2.y-=bounds.getMinY();
	this.crop(r2);
	
}

@Override
public int getBitsPerPixel() {
	if (object instanceof ByteProcessor) return 8;
	if (object instanceof ShortProcessor) return 16;
	if (object instanceof FloatProcessor) return 32;
	if (object instanceof ColorProcessor) return 32;
	return 8;
}





}





