package utilityClassesForObjects;

import java.awt.FontMetrics;

/***/
public interface TextItem extends  HasText, Rotatable{

	public int getX();
	public int getY();
	public void setLocation(double x, double y);
	
	
	public void storeFontMetrics(FontMetrics fontMetrics);
	public FontMetrics getStoredFontMetrics();
	
	public void cleanUpText();
	
	public int getTextWidth();
	
	
}
