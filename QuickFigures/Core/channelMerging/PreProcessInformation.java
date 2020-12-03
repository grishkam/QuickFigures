package channelMerging;

import java.awt.Rectangle;
import java.io.Serializable;

/**this class contains information for the rotation, cropping and scaling
  performed on a multidimensional image. This is the 'preprocess'*/
public class PreProcessInformation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double angle;//the angle of the cropping region
	private Rectangle rectangle;//the dimensions of the crop region
	private double scale=1;//the scale factor applied
	
	public String toString() {
		return "[Rectangle="+rectangle+" angle="+angle+" scale="+scale+"]";
	}
	
	/**creates an instance cropped a the given rectangle but with no rotation nor scale*/
	public PreProcessInformation(Rectangle r2) {
		this(r2, 0, 1);
	}
	
	/**creates an instance cropped at an angle with the given rectangle and then scaled*/
	public PreProcessInformation(Rectangle rectangle2, double angle2, double scale2) {
		this.rectangle=rectangle2;
		this.angle=angle2;
		this.scale=scale2;
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
		return new PreProcessInformation(getRectangle(), getAngle(), getScale());
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

	/**returns true if the argument's information is identical to this object*/
	public boolean isSame(PreProcessInformation original) {
		if(original==null) return false;
		if(scale!= original.getScale()) return false;
		if(angle!= original.getAngle()) return false;
		
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
	

	

}
