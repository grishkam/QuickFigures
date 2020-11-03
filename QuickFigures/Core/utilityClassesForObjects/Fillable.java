package utilityClassesForObjects;

import java.awt.Color;

/**an interface for object that can be filled*/
public interface Fillable {
	
	/**getter and setter methods for the fill color*/
	public void setFillColor(Color c);
	public Color getFillColor();
	boolean isFilled();
	void setFilled(boolean fill);
	public PaintProvider getFillPaintProvider();
	public void setFillPaintProvider(PaintProvider p);
	
	/**returns true if the item can currently be filled*/
	public boolean isFillable();
}
