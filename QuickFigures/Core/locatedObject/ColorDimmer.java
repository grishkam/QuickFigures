/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 4, 2021
 * Version: 2021.1
 */
package locatedObject;

import java.awt.Color;

/**enum for a set of color modifying effects 
 * the methods in this class applies a color modifying effect to an input color.
 * used to implement the color dimming for text item colors*/
public enum ColorDimmer {
	FULL_BRIGTHNESS, NORMAL_DIM, HIGH_DIM, DARK, 
			OUTSIDE_BLACK_INSITE_WHITE, OUTSIDE_WHITE_INSITE_BLACK, DESATURATED, DESATURATED_2;
	
	/**names of the effects*/
	public static String[] colorModChoices2 = new String[] { "Bright", "Normal Color",
		"Dim Color", "Dark Color", "Black",
		"White", "Pale Color" , "Paler Color"};
	
	/**returns the modified version of the color
	 * @param baseColor the base color
	 * @param type how to alter the color
	 * @param labelOutside indicates whether the item is in front of a dark panel*/
	public static Color modifyColor(Color baseColor,  ColorDimmer type, boolean labelOutside) {
		if (type ==  NORMAL_DIM)
			return baseColor.darker();
		if (type == HIGH_DIM)
			return baseColor.darker().darker();
		if (type == DARK)
			return baseColor.darker().darker().darker();
		if (type == OUTSIDE_BLACK_INSITE_WHITE && labelOutside)
			return Color.black;
		if (type == OUTSIDE_BLACK_INSITE_WHITE && !labelOutside)
			return Color.white;
		if (type == OUTSIDE_WHITE_INSITE_BLACK && !labelOutside)
			return Color.black;
		if (type == OUTSIDE_WHITE_INSITE_BLACK && labelOutside)
			return Color.white;
		if (isDesaturated(type) && baseColor.equals(Color.black)&&!labelOutside)
			return Color.white;
		if (isDesaturated(type)) {
			int desaturationLevel=1;
			if(type==DESATURATED) desaturationLevel=1;
			if(type==DESATURATED_2) desaturationLevel=2;
			
			return desaturateColor(baseColor, (float) ((desaturationLevel)*0.3));
			}
		return baseColor;
	}
	
	/**returns true if the dimmer is a desaturation type*/
	static boolean isDesaturated(ColorDimmer type) {
		if(type==DESATURATED)
			return true;
		if(type==DESATURATED_2)
			return true;
		
		
		return false;
	}
	
	/**returns the modified version of the color array*/
	public static Color[] modifyArray(Color[] c,  ColorDimmer type, boolean labelOutside) {
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
