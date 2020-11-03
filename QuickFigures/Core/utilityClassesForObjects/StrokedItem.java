package utilityClassesForObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public interface StrokedItem {
	/**returns the stroke width */
	public float getStrokeWidth();
	public Stroke getStroke();
	public void setStrokeWidth(float width);
	
	/**getter and setter methods for the stroke color*/
	public void setStrokeColor(Color c);
	public Color getStrokeColor();
	public float[] getDashes();
	public void setDashes(float[] fl);
	void setStroke(BasicStroke stroke);
	public int getStrokeJoin();
	public int getStrokeCap();
	public void setStrokeJoin(int selectedIndex);
	public void setStrokeCap(int size);
	public void setMiterLimit(double miter);
	public double getMiterLimit();
}
