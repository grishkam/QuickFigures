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
 * Version: 2022.0
 */
package applicationAdapters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;

/**An interface for drawing onto an image. 
 * The most critical methods are the crop at angle and scale bilinear
 * A raster of pixels might take different forms depending on what implementation is used.
  There is currently an imageJ implementation
  */
public interface PixelWrapper {
	
	
	/**Bit depth*/
	public int getBitsPerPixel();
	
	/**Fills shape with folor c*/
	public void fill(Shape r, Color c)  ;
	
	
	/**returns a copy. If r is null, returns a full copy. otherwise, returns a cropped copy*/
	public PixelWrapper copy(Rectangle r) ;
	
	/**creates a new instance of this class with dimensions given*/
	public PixelWrapper makenew(Dimension d);
	

	/**returns the forms in which the data for the channel pixels is stored internally*/
	public Object getPixels();
	/**returns an array with the numerical values of the pixel data*/
	public Object getRawData();
	
	/**resize. Sets the image to a copy with width and height specified*/
	public void resize( double width, double height) ;
	public void resizeWithInterpolationMethod(double width, double height);
	
	/**returns the dimensions*/
	public int width();
	public int height();
	public Dimension dim();
	
	/**crops the image*/
	public void crop(Rectangle r);
	/**Crops the image at a specific rotation angle.
	  Rotates the rectangle about its center and then crop*/
	public void cropAtAngle(Rectangle r, double angle);
	
	/**creates an awt image*/
	public Image image();
	
	/**returns true if this represents an RGB image and not another kind*/
	public boolean isRGB();
	
	/**returns a histogram or all the pixel values in the image*/
	public int[] getDistribution();

	/**performs a scaling with bilinear interpolation*/
	void scaleWithCurrentInterpolationMethod(double scale);

	
}
