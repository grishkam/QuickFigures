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
 * Date Modified: Jan 4, 2021
 * Version: 2021.1
 */
package applicationAdaptersForImageJ1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import applicationAdapters.PixelWrapper;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import locatedObject.RectangleEdges;
import logging.IssueLog;

/**an implementation of pixel wrapper interface for ImageJ images
 *  QuickFigures.  Of the methods below, only a few are crucial for the 
 *  function of QuickFigures in its current form
 *  */
public class ProcessorWrapper implements PixelWrapper {
	public ImageProcessor object;
	
	public ProcessorWrapper(ImageProcessor ip) {
		this.object=ip;
	}
	
	public ProcessorWrapper createnew(
			ImageProcessor object) {
		return new ProcessorWrapper(object);
	}


	/**Inserts this items pixels into the target
	public void insertInto(PixelWrapper recipient, int x, int y) {
		Object ob = recipient.getPixels();
		if (ob instanceof ImageProcessor) {
		((ImageProcessor)ob ).insert(getPixels(), x, y);
		}
		
	}
*/

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
	



@Override
public boolean isRGB() {
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





