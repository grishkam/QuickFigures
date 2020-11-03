package utilityClassesForObjects;

/**An interface for anyting with an angle and rotation*/
public interface Rotatable {
	public double getAngle();
	public void setAngle(double angle);
	public void rotate(double angle);
	boolean isRandians();
	boolean isDegrees();
	
}
