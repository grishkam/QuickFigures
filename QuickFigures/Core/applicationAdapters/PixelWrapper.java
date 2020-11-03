package applicationAdapters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ColorModel;

/**A raster of pixels might take different forms depending on what library is used.
  Due to plans for using this library for different apps, wrote this  */
public interface PixelWrapper {
	
	/**fills with shite or transparent*/
	public void clear(Shape r) ;
	
	/**Bit depth*/
	public int getBitsPerPixel();
	
	/***/
	public void fill(Shape r, Color c)  ;
	
	/**draws a String */
	public void drawPixelString(String label, Color c,  Font font, int lx, int ly, double angle, boolean antialiasedText );
	
	
	/**returns a copy. If r is null, returns a full copy. otherwise, returns a cropped copy*/
	public PixelWrapper copy(Rectangle r) ;
	
	
	public PixelWrapper cut(Rectangle r);
	public PixelWrapper makenew(Dimension d);
	

	
	public void insertInto(PixelWrapper recipient, int x, int y);
	
	
	public Object getPixels();/**
	public void setPixels(DataType ip);*/
	
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
	
	/***/
	public ColorModel getColorModel();
	public boolean isRGB();
	
	/**returns a histogram or all the pixel values in the image*/
	public int[] getDistribution();

	void scaleBilinear(double scale);

	
}
