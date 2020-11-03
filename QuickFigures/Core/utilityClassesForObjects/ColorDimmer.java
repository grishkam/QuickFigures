package utilityClassesForObjects;

import java.awt.Color;

public class ColorDimmer {
	public static String[] colorModChoices = new String[] { "Bright", "Normal Color",
			"Dim Color", "Dark Color", "Outside Black/Inside White",
			"Outside White/Inside Black", "Paler", "More Pale"};
	public static String[] colorModChoices2 = new String[] { "Bright", "Normal Color",
		"Dim Color", "Dark Color", "Black",
		"White", "Pale Color" , "Paler Color"};
	
	
	public static Color modifyColor(Color c, int type, boolean labelOutside) {
		if (type == 1)
			return c.darker();
		if (type == 2)
			return c.darker().darker();
		if (type == 3)
			return c.darker().darker().darker();
		if (type == 4 && labelOutside)
			return Color.black;
		if (type == 4 && !labelOutside)
			return Color.white;
		if (type == 5 && !labelOutside)
			return Color.black;
		if (type == 5 && labelOutside)
			return Color.white;
		if (type >5 && c.equals(Color.black)&&!labelOutside)
			return Color.white;
		if (type> 5) {return desaturateColor(c, (float) ((type-5)*0.3));}
		return c;
	}
	
	public static Color[] modifyArray(Color[] c, int type, boolean labelOutside) {
		Color[] out=new Color[c.length];
		for(int i=0; i<c.length; i++) {
			out[i]=modifyColor(c[i], type, labelOutside);
		}
		return out;
	}
	public static Color desaturateColor(Color c) {
		return desaturateColor(c, (float)0.18);
	}
	public static Color desaturateColor(Color c, float a) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		if (c.equals(Color.white)) return c;
		
		int rgb = Color.HSBtoRGB(hsb[0], (float)(hsb[1]-a), hsb[2]);
		return new Color(rgb);
	}
}
