package channelMerging;

import java.awt.Rectangle;
import java.io.Serializable;

public class PreProcessInformation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double angle;
	private Rectangle rectangle;
	private double scale;
	
	public String toString() {
		return "[Rectangle="+rectangle+" angle="+angle+" scale="+scale+"]";
	}
	
	public PreProcessInformation(Rectangle rectangle2, double angle2, double scale2) {
		this.rectangle=rectangle2;
		this.angle=angle2;
		this.scale=scale2;
	}

	/**returns true if nothing done to the image*/
	boolean nothingDone() {
		if (getAngle()!=0) return false;
		if (getRectangle()!=null) return false;
		if(getScale()!=1) return false;
		
		return true;
	}
	
	double cropAngle() {
		return this.getAngle();
	}
	Rectangle cropRect() {
		return this.getRectangle();
	}
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

	/**returns true if the preprocess information is identical*/
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
