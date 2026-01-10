/**
 * Author: Greg Mazo
 * Date Modified: Jan 10, 2026
 * Copyright (C) 2026 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

/**
 
 * 
 */
public class PixelDensityIcon implements Icon {

	
	public static final int VERSION_IMAGE_SCALE=1, VERSION_PIXEL_RESULUTION = 2;
	int the_version=VERSION_IMAGE_SCALE;
	int rw=4;
	int rh=3;
	private BufferedImage startImage;
	boolean include_small=true;
	private Image big_image;

	public PixelDensityIcon(int version) {
		the_version=version;
		this.startImage=createStartingImage();
		
	}
	
	public BufferedImage createStartingImage() {
		Dimension dim = new Dimension(rw,rh);
		 BufferedImage img=new BufferedImage((int)(dim.width), (int)(dim.height),  BufferedImage.TYPE_INT_ARGB);
			Graphics g = img.getGraphics();
			
			g.setColor(Color.white);			
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			for(int i=0; i<dim.height*dim.width; i++) {
				int grey_val = (int) (255*Math.random());
				Color c = new Color(grey_val,grey_val,grey_val);
				g.setColor(c);
				g.fillRect( i%dim.width,i/dim.width, 2, 1);
			}
			return img;
	}

	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int big_inflate_level=5;
		
		this.big_image=startImage.getScaledInstance(rw*big_inflate_level, rh*big_inflate_level, BufferedImage.SCALE_SMOOTH);
		
		g.drawImage(big_image, x+2, y, rw*big_inflate_level, rh*big_inflate_level, Color.black, null);
		if(include_small) {
			
			g.setColor(Color.blue);
			g.drawRect(x+1, y, rw*big_inflate_level, rh*big_inflate_level);
			
			
			int ylevel_smal = y+rh*5*3/5;
			int inflate_levels=3;
			g.drawImage(startImage, x, ylevel_smal, rw*inflate_levels, rh*inflate_levels, Color.black, null);
			g.setColor(Color.red);
			g.drawRect(x, ylevel_smal, rw*inflate_levels, rh*inflate_levels);
		}

	}

	@Override
	public int getIconWidth() {
		// TODO Auto-generated method stub
		return 22;
	}

	@Override
	public int getIconHeight() {
		// TODO Auto-generated method stub
		return 18;
	}

}
