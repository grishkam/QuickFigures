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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

/**
 
 * 
 */
public class PixelDensityIcon implements Icon {

	
	public static final int VERSION_IMAGE_SCALE=1, VERSION_PIXEL_RESULUTION = 2, VERSION_SCALE_TO_KEEP_PIXEL_SIZE = 3;
	int the_version=VERSION_IMAGE_SCALE;
	int rw=4;
	int rh=3;
	private BufferedImage startImage;
	boolean include_small=true;
	
	int big_inflate_level=5; //inflation level for the second image
	int y_shift_img2=0;
	Object interpolation1=RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
	int x_shift1=-2;
	
	int inflate_levels=3; //inflation level for the first image
	int y_shift_img1=7;
	private int x_shift2=1;
	Object interpolation2=RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

	
	public PixelDensityIcon() {
		this(VERSION_SCALE_TO_KEEP_PIXEL_SIZE);
	}
	
	public PixelDensityIcon(int version) {
		the_version=version;
		this.startImage=createStartingImage(rw, rh);
		if(version==VERSION_PIXEL_RESULUTION) {
			big_inflate_level=4;
			inflate_levels=4;
			x_shift2=7;
		}
		
		if(this.the_version==VERSION_IMAGE_SCALE) {
			interpolation1=RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			interpolation2=RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		}
		
		if(isVersionScaleWithConstantPixDensity()) {
			interpolation1=RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
			interpolation2=RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
			big_inflate_level=6;
		}
		
	}


	/**
	 * @return
	 */
	protected boolean isVersionScaleWithConstantPixDensity() {
		return this.the_version==VERSION_SCALE_TO_KEEP_PIXEL_SIZE;
	}
	
	
	public BufferedImage createStartingImage(int rw, int rh) {
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
	
	/**Creates a scaled version
	 * @return */
	public BufferedImage createScaledImageWithWimilarPixelDensity(double sizeRatio, BufferedImage b) {
		int w2=(int) (b.getWidth()*sizeRatio);
		int h2=(int) (b.getHeight()*sizeRatio);
		 BufferedImage img=new BufferedImage(w2, h2,  BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) img.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(b, 0, 0, w2, h2, null);
			
			return img;
	}
	

	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		
		Graphics2D g2d=null;
		
		Image imageFor2=startImage;
		if(isVersionScaleWithConstantPixDensity()) {
			double ratio = (double) big_inflate_level;
			ratio/=this.inflate_levels;
			imageFor2=createScaledImageWithWimilarPixelDensity(ratio, startImage);
		}
		
		if(g instanceof Graphics2D) {
			g2d=(Graphics2D) g;
		}
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, this.interpolation2);
		
		if(this.the_version==VERSION_PIXEL_RESULUTION) {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
		}
		g.drawImage(imageFor2, x+x_shift2, y, rw*big_inflate_level, rh*big_inflate_level, Color.black, null);
		
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			
			
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(include_small) {
			
			g.setColor(Color.blue);
			
			g.drawRect(x+x_shift2, y+this.y_shift_img2, rw*big_inflate_level, rh*big_inflate_level);
			
			int ylevel_smal = y+y_shift_img1;
			
			
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, this.interpolation2);
			g.drawImage(startImage, x+x_shift1, ylevel_smal, rw*inflate_levels, rh*inflate_levels, Color.black, null);
			g.setColor(Color.red);
			g.drawRect(x+x_shift1, ylevel_smal, rw*inflate_levels, rh*inflate_levels);
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
