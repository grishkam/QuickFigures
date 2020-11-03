package utilityClassesForObjects;

import java.awt.Color;
import java.awt.Font;

public interface HasText {
	public String getText();
	public void setText(String st);
	public Font getFont();
	public void setFont(Font font);
	public Color getTextColor();
	public void setTextColor(Color c);
}
