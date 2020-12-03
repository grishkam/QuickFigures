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
	

	public Object getPixels();
	
	/**resize. Sets the image to a copy with width and height specified*/
	public void resize( double width, double height) ;
	public void resizeBilinear(double width, double height);
	
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
	void scaleBilinear(double scale);

	
}
