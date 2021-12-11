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

	/**crops the processor. If the crop area extends beyond the image processor, will creates a processor that is the 
	 * same size as the crop area*/
	@Override
	public void crop(Rectangle r) {
		if (object==null) 
			return;
		boolean add=false;
		int addLeft=0;
		if(r.x<0)
			{addLeft=Math.abs(r.x);add=true;}
		int addTop=0;
		if(r.y<0)
			{addTop=Math.abs(r.y);add=true;}
		if(r.getMaxX()>object.getWidth())
			{add=true;}
		if(r.getMaxY()>object.getHeight())
			{add=true;}
		
		
			object.setRoi(r);
		object=object.crop();
		
		if(add) {
			ImageProcessor newObject = object.createProcessor(r.width,r.height);
			newObject.insert(object, addLeft, addTop);
			object=newObject;
		}
		
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
	if(angle%Math.PI==0) {
		this.crop(r);
		return;//non-rotated crop areas are simple
		}
	if(angle%Math.PI/2==0) {
		IssueLog.log("performing 90 deg crop");
		Rectangle r2 = new Rectangle(r);
		r2.width=r.height; r2.height=r.width; r2.x+=(r.width-r2.width)/2; r2.y+=(r.height-r2.height)/2;
		this.crop(r2);
		return;//right angle crop areas are very simple too.
		}
	
	/**first performs an easy 'pre-crop' crop that just gets rid of extra pixels
	 *  and makes the center of the image match the center of rotation 
	 * later rotation can be time consuming for large images, crops to a smaller image first.
	 * Crop area for first crop has same center location as final crop and contains all the pixels that will be in the final crop. 
	 * after first crop, rotation will be performed. Lastly, the final crop will be done*/
	Rectangle firstCropBounds = createFirstPhaseCropArea(r, angle);
	
	
	
	crop(firstCropBounds);//the first crop bounds has the same center as the final crop area but is larger such that all pixels that might fall within a rotated crop are present
	
	
	
	//at this point, a smaller image that requires less processing to rotate is available
	
	
	/**now rotates the smaller image*/
	this.setInterPolationMethodFor(object);
	angle=angle*180/Math.PI;//changes the units of the angle to degree
	object.rotate(angle);//rotation is about center. 
	
	/**performs the final crop*/
	Rectangle secondPhaseCropArea = new Rectangle(r);
	secondPhaseCropArea.x-=firstCropBounds.getMinX();
	secondPhaseCropArea.y-=firstCropBounds.getMinY();
	this.crop(secondPhaseCropArea);
	
}

/**
 * @param r
 * @param angle
 * @return
 */
protected Rectangle createFirstPhaseCropArea(Rectangle r, double angle) {
	Shape firstLevelCrop = AffineTransform.getRotateInstance(-angle, r.getCenterX(), r.getCenterY()).createTransformedShape(r);
	Rectangle firstCropBounds = firstLevelCrop.getBounds(); //this area should have the same center as the crop area
	if (firstCropBounds.x>r.x||firstCropBounds.y>r.y) {
		/** At certain angles for rectangles with high aspect ratio, the bounds of the rotated rectangle does not include
		  all of the region of interest. Problem might occur under these conditions so the next few lines alter*/
		Point2D center = RectangleEdges.getLocation(RectangleEdges.CENTER, firstCropBounds);
		if(firstCropBounds.height>firstCropBounds.width)firstCropBounds.width=firstCropBounds.height;
		if(firstCropBounds.height<firstCropBounds.width)firstCropBounds.height=firstCropBounds.width;
		RectangleEdges.setLocation(firstCropBounds, RectangleEdges.CENTER, center.getX(), center.getY());
		
	}
	return firstCropBounds;
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





