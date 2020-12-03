/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package utilityClassesForObjects;

import java.awt.Color;

/**the methods in this class applies a color modifying effect to an input color.
 * used to implement the color dimming for text item colors*/
public class ColorDimmer {
	public static final int FULL_BRIGTHNESS=0, NORMAL_DIM=1, HIGH_DIM=2, DARK=3, 
			OUTSIDE_BLACK_INSITE_WHITE=4, OUTSIDE_WHITE_INSITE_BLACK=5, DESATURATED=6;
	
	public static String[] colorModChoices = new String[] { "Bright", "Normal Color",
			"Dim Color", "Dark Color", "Outside Black/Inside White",
			"Outside White/Inside Black", "Paler", "More Pale"};
	public static String[] colorModChoices2 = new String[] { "Bright", "Normal Color",
		"Dim Color", "Dark Color", "Black",
		"White", "Pale Color" , "Paler Color"};
	
	/**returns the modified version of the color*/
	public static Color modifyColor(Color c, int type, boolean labelOutside) {
		if (type ==  NORMAL_DIM)
			return c.darker();
		if (type == HIGH_DIM)
			return c.darker().darker();
		if (type == DARK)
			return c.darker().darker().darker();
		if (type == OUTSIDE_BLACK_INSITE_WHITE && labelOutside)
			return Color.black;
		if (type == OUTSIDE_BLACK_INSITE_WHITE && !labelOutside)
			return Color.white;
		if (type == OUTSIDE_WHITE_INSITE_BLACK && !labelOutside)
			return Color.black;
		if (type == OUTSIDE_WHITE_INSITE_BLACK && labelOutside)
			return Color.white;
		if (type >= DESATURATED && c.equals(Color.black)&&!labelOutside)
			return Color.white;
		if (type>= DESATURATED) {return desaturateColor(c, (float) ((1+type-DESATURATED)*0.3));}
		return c;
	}
	
	/**returns the modified version of the color array*/
	public static Color[] modifyArray(Color[] c, int type, boolean labelOutside) {
		Color[] out=new Color[c.length];
		for(int i=0; i<c.length; i++) {
			out[i]=modifyColor(c[i], type, labelOutside);
		}
		return out;
	}
	
	/**returns a less saturated version of the color*/
	public static Color desaturateColor(Color c) {
		return desaturateColor(c, (float)0.18);
	}
	/**returns a less saturated version of the color. unless the color is white, then just returns the color*/
	public static Color desaturateColor(Color c, float a) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		if (c.equals(Color.white)) return c;
		
		int rgb = Color.HSBtoRGB(hsb[0], (float)(hsb[1]-a), hsb[2]);
		return new Color(rgb);
	}
}
