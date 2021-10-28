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
package channelMerging;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import imageScaling.Interpolation;
import imageScaling.ScaleInformation;

/**this class contains information for the rotation, cropping and scaling
  performed on a multidimensional image. This is the 'preprocess'
  Record of this process is kept in the event the user needs to repeat
  with an altered set of parameters*/
public class PreProcessInformation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double angle;//the angle of the cropping region
	private Rectangle rectangle;//the dimensions of the crop region
	private double scale=1;//the scale factor applied
	
	Interpolation interpolationType=Interpolation.BILINEAR;
	
	public String toString() {
		return "[Rectangle="+rectangle+" angle="+angle+" scale="+scale+"]";
	}
	
	/**creates an instance cropped a the given rectangle but with no rotation nor scale*/
	public PreProcessInformation(Rectangle r2) {
		this(r2, 0, 1);
	}
	
	/**creates an instance that is scaled but not cropped*/
	public PreProcessInformation(double scale) {
		this(null, 0, scale);
	}
	
	/**creates an instance cropped at an angle with the given rectangle and then scaled*/
	public PreProcessInformation(Rectangle rectangle2, double angle2, double scale2) {
		this.rectangle=rectangle2;
		this.angle=angle2;
		this.scale=scale2;
	}
	
	/**creates an instance cropped at an angle with the given rectangle and then scaled*/
	public PreProcessInformation(Rectangle rectangle2, double angle2, ScaleInformation scaleInfo) {
		this(rectangle2,angle2,scaleInfo.getScale());
		this.interpolationType=scaleInfo.getInterpolationType();
	}
	
	/**creates an instance that uses the scame crop area as the argument but different scale*/
	public PreProcessInformation(PreProcessInformation p1, ScaleInformation scaleInfo) {
		this(p1.getRectangle(),p1.getAngle(),scaleInfo.getScale());
		this.interpolationType=scaleInfo.getInterpolationType();
	}

	/**returns true if this object instructs nothing to be done to the image*/
	boolean nothingDone() {
		if (getAngle()!=0) return false;
		if (getRectangle()!=null) return false;
		if(getScale()!=1) return false;
		
		return true;
	}
	
	/**returns the angle at which the cropping roi is drawn*/
	double cropAngle() {
		return this.getAngle();
	}
	/**returns the cropping rectangle*/
	Rectangle cropRect() {
		return this.getRectangle();
	}
	/**returns the scale factor*/
	double bilinearScale() {
		return this.getScale();
	}
	
	PreProcessInformation copy() {
		return new PreProcessInformation(getRectangle(), getAngle(), this.getScaleInformation());
	}

	public Rectangle getRectangle() {
		if(rectangle==null)
			return null;
		return rectangle.getBounds();
	}

	public double getAngle() {
		return angle;
	}

	public double getScale() {
		return scale;
	}

	/**returns the expected size of the cropped and scaled image*/
	public Rectangle2D getOutputDimensions(Dimension input) {
		if(rectangle==null)
			{
			double width = input.getWidth()*scale;
			double height = input.getHeight()*scale;
			return new Rectangle2D.Double(0, 0, width, height);
			}
		double width = rectangle.getWidth()*scale;
		double height = rectangle.getHeight()*scale;
		return new Rectangle2D.Double(0, 0, width, height);
	}
	
	
	/**returns true if the argument's information is identical to this object*/
	public boolean isSame(PreProcessInformation original) {
		if(original==null) return false;
		if(scale!= original.getScale()) return false;
		if(angle!= original.getAngle()) return false;
		if(this.getInterpolationType()!=original.getInterpolationType()) return false;
		
		if(rectangle==null &&original.getRectangle()!=null)
			return false;
		if(rectangle!=null &&original.getRectangle()==null)
			return false;
		if(rectangle==original.getRectangle())
			return true;
		
		if(rectangle!=null &&original.getRectangle()!=null) {
			if (!rectangle.equals(original.getRectangle()))
					return false;
		}
		return true;
	}
	
	
	/**returns the interpolation type*/
	public Interpolation getInterpolationType() {
		if(interpolationType==null)
			interpolationType=Interpolation.BILINEAR;
		return interpolationType;
	}

	/**returns the scale information*/
	public ScaleInformation getScaleInformation() {return new ScaleInformation(scale, interpolationType);}


	
	
}
