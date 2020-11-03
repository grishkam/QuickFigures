package utilityClassesForObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.io.Serializable;

public interface PaintProvider extends Serializable{

	public Paint getPaint();
	public void showOptionsDialog();
	public void setPaintedShape(Shape s);
	public void fillShape(Graphics2D graphics, Shape s);
	public Color getColor();
	public void setColor(Color c);
	public Color getColor(int i);
	public void setColor(int i, Color c);
	void strokeShape(Graphics2D g, Shape s);
	void setStrokeShape(Shape s);
	
}
