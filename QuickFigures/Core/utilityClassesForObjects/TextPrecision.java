package utilityClassesForObjects;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class TextPrecision {
		int inflationfactor=500;
	
	public TextPrecision() {}
	
	public TextPrecision(int pres) {
		inflationfactor=pres;
	}
	
	public static TextPrecision createPrecisForFont(Font f) {
		if (f.getSize()<30) return new TextPrecision(500);
		if (f.getSize()<60) return new TextPrecision(250);
		if (f.getSize()<120) return new TextPrecision(125);
		 return new TextPrecision(60);
		
	}
	
	public FontMetrics getInflatedMetrics(Font f, Graphics g) {
		return g.getFontMetrics(f.deriveFont((float)f.getSize()*inflationfactor));
	}
	


	
	public double getInflationFactor() {
		return (double)inflationfactor;
	}
}
