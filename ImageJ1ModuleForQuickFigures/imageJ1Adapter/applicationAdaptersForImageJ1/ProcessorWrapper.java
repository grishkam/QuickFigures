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
 * Date Modified: April 18, 2021
 * Version: 2021.2
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
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imageScaling.Interpolation;
import locatedObject.RectangleEdges;
import logging.IssueLog;

/**an implementation of pixel wrapper interface for ImageJ images
 *   Of the methods below, only a few are crucial for the 
 *  function of QuickFigures in its current form
 *  */
public class ProcessorWrapper implements PixelWrapper {
	public ImageProcessor object;
	private Interpolation interpolate ;
	
	public ProcessorWrapper(ImageProcessor ip) {
		this.object=ip;
	}
	
	public ProcessorWrapper createnew(
			ImageProcessor object) {
		ProcessorWrapper processorWrapper = new ProcessorWrapper(object);
		processorWrapper.setInterpolationType(interpolate);
		return processorWrapper;
	}



	public ImageProcessor getPixels() {
		return object;
	}
	
	public float[][] getRawData() {
		float[][] floatArray = getPixels().getFloatArray();
		return floatArray;
	}


	public void setPixels(ImageProcessor ip) {
		object=ip;;
		
	}

	
	

	@Override
	public void resize(double x, double y) {
		setInterPolationMethodFor(object);
		object=object.resize((int)x, (int)y);
		
	}
	
	/**sets theinterpolation method for the image processor
	 * @param object2
	 */
	private void setInterPolationMethodFor(ImageProcessor object2) {
		if(object2==null)
			return;
		if (interpolate==Interpolation.BILINEAR) 
			object2.setInterpolationMethod(ImageProcessor.BILINEAR);
		if (interpolate==Interpolation.BICUBIC) 
			object2.setInterpolationMethod(ImageProcessor.BICUBIC);
		if (interpolate==Interpolation.NONE)
			object2.setInterpolationMethod(ImageProcessor.NONE);
		
		
	}

	@Override
	public void resizeWithInterpolationMethod(double width, double height) {
		this.setInterPolationMethodFor(object);
		object=object.resize((int)width, (int)height);
		
	}
	
	@Override
	public void scaleWithCurrentInterpolationMethod(double scale) {
		this.setInterPolationMethodFor(object);
		resizeWithInterpolationMethod(object.getWidth()*scale, object.getHeight()*scale);
		
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

	/**crops the processor*/
	@Override
	public void crop(Rectangle r) {
		if (object==null) return;
			object.setRoi(r);
		object=object.crop();
		
	}
	
	/**Done prior to crops in which the crop area is below zero (done prior to rotation)
	 * @param xShift
	 * @param yShift
	 */
	private void createExpandedTopAndLeft(int xShift, int yShift) {
		if(xShift==0&&yShift==0)
			return;
		ImageProcessor newObject = object.createProcessor(2*xShift+object.getWidth(), 2*yShift+object.getHeight());
		newObject.insert(object, xShift, yShift);
		object=newObject;
		
	}
	
	/**Done prior to crops in which the crop area is beyond the max of the image (done prior to rotation)
	 * @param xShift
	 * @param yShift
	 */
	private void createExpandedBottomAndRight(int xShift, int yShift) {
		if(xShift==0&&yShift==0)
			return;
		ImageProcessor newObject = object.createProcessor(xShift+object.getWidth(), yShift+object.getHeight());
		newObject.insert(object, 0, 0);
		object=newObject;
		
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

/**implements the angle crop*/
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
	
	/**first performs an easy 'pre-crop' crop that just gets rid of extra pixels. 
	 * later rotation can be time consuming for large images, crops to a smaller image first*/
	Shape firstLevelCrop = AffineTransform.getRotateInstance(-angle, r.getCenterX(), r.getCenterY()).createTransformedShape(r);
	Rectangle bounds = firstLevelCrop.getBounds(); //this area should have the same center as the crop area
	
	
	
	if (bounds.x>r.x||bounds.y>r.y) {
		/** At certain angles for rectangles with high aspect ratio, the bounds of the rotated rectangle does not include
		  all of the region of interest. Problem might occur under these conditions so the next few lines alter*/
		Point2D center = RectangleEdges.getLocation(RectangleEdges.CENTER, bounds);
		if(bounds.height>bounds.width)bounds.width=bounds.height;
		if(bounds.height<bounds.width)bounds.height=bounds.width;
		RectangleEdges.setLocation(bounds, RectangleEdges.CENTER, center.getX(), center.getY());
		
	}
	
	r=performZeroCorrection(bounds, r);
	
	angle=angle*180/Math.PI;//changes the units of the angle to degrees
	this.crop(bounds);
	
	
	
	//at this point, a smaller image that requires less processing to rotate is available
	
	
	/**now rotates the smaller image*/
	this.setInterPolationMethodFor(object);
	object.rotate(angle);//rotation is about center
	
	Rectangle r2 = new Rectangle(r);
	r2.x-=bounds.getMinX();
	r2.y-=bounds.getMinY();
	
	
	this.crop(r2);
	
}



/**If the innitial bounds for a pre-crop is below zero this will attempt to correct it
 * @param bounds
 * @param r 
 * @return 
 */
private Rectangle performZeroCorrection(Rectangle bounds, Rectangle r) {
	int xShift = 0;
	int yShift =0;
	if (bounds.x<0) {
		/** Found to have difficulty with x or y below zero. fix was created on Dec 10 2021*/
		xShift=bounds.x;
		if(bounds.x<0)
			{bounds.x=0;}
			createExpandedTopAndLeft(Math.abs(xShift), 0);//creates a version that does not need to have a crop rectangle below zero
			Rectangle output = new Rectangle(r.x-xShift, r.y, r.width, r.height);
			
		return output;
		
	}
	
	if (bounds.y<0) {
		/** Found to have difficulty with x or y below zero. fix was created on Dec 10 2021*/
		yShift=bounds.y;
		if(bounds.y<0)
			{bounds.y=0;}
			createExpandedTopAndLeft( 0, Math.abs(yShift));//creates a version that does not need to have a crop rectangle below zero
			Rectangle output = new Rectangle(r.x, r.y-yShift, r.width, r.height);
			
		return output;
		
	}
	
	if(bounds.getMaxX()>object.getWidth()) {
		createExpandedBottomAndRight((int) (bounds.getMaxX()-object.getWidth()), 0);
	}
	if(bounds.getMaxY()>object.getHeight()) {
		createExpandedBottomAndRight(0, (int) (bounds.getMaxY()-object.getHeight()));
	}
	
	return r;
}

@Override
public int getBitsPerPixel() {
	if (object instanceof ByteProcessor) return 8;
	if (object instanceof ShortProcessor) return 16;
	if (object instanceof FloatProcessor) return 32;
	if (object instanceof ColorProcessor) return 32;
	return 8;
}

/**
 * @param interpolateMe
 */
public void setInterpolationType(Interpolation interpolateMe) {
	this.interpolate=interpolateMe;
	
}


public boolean equals(Object o) {
	if(o instanceof ProcessorWrapper)
		{
		ProcessorWrapper o2=(ProcessorWrapper) o;
		if(o2.getProcessor().equals(getProcessor()))
			return true;
		for(int i=0; i<this.getProcessor().getWidth()*this.getProcessor().getHeight(); i++)
			if(this.getProcessor().get(i)!=o2.getProcessor().get(i))
				return false;
		
		
		
		return true;
		}
	else
	 return false;
}





}





