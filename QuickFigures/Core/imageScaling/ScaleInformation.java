/**
 * Author: Greg Mazo
 * Date Created: April 18, 2021
 * Date Modified: April 18, 2021
 * Version: 2021.1
 */
package imageScaling;

/**
 Class that carries information about the scale and interpolation
 */
public class ScaleInformation {
	private double scale=1;//the scale factor applied
	Interpolation interpolationType=Interpolation.BILINEAR;
	


	
	/***/
	public ScaleInformation(double scale, Interpolation interpolation) {
		this.scale=scale;
		this.interpolationType=interpolation;
	}

	/**
	
	 */
	public ScaleInformation() {
		
	}
	
	
	/***/
	public ScaleInformation(double scale) {
		this.scale=scale;
		
	}

	public double getScale() {
		return scale;
	}

	public Interpolation getInterpolationType() {
		return interpolationType;
	}
	
	public String toString() {
		return "scale="+scale+"  Interpolation="+interpolationType.name().toLowerCase();
	}

	/**gets a version of this with different scale
	 * @param i
	 * @return
	 */
	public ScaleInformation getAtDifferentScale(double i) {
		return new ScaleInformation(i, interpolationType);
	}
	
	/**returns a version of this that has been multiplied*/
	public ScaleInformation multiplyBy(double factor) {return new ScaleInformation(scale*factor, interpolationType);}
}
