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
 * Version: 2023.2
 */
package textObjectProperties;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**Font metrics objects in java return integers but some classes in QuickFigures
  need something that is close to a double equivalent.
  this class is used as a workaround. Multiplies the font by a factor to create a large size font metrics then returns the measurements
  for the enlarged font what can be divided by a double*/
public class TextPrecision {
		double inflationfactor=500;
	
	public TextPrecision() {}
	
	public TextPrecision(int pres) {
		inflationfactor=pres;
	}
	/**creates a new text precision object*/
	public static TextPrecision createPrecisForFont(Font f) {
		if (f.getSize()<30) return new TextPrecision(500);
		if (f.getSize()<60) return new TextPrecision(250);
		if (f.getSize()<120) return new TextPrecision(125);
		 return new TextPrecision(60);
		
	}
	
	/**returns an enlraged versin of the font metrics*/
	public FontMetrics getInflatedMetrics(Font f, Graphics g) {
		return g.getFontMetrics(f.deriveFont((float)(f.getSize()*inflationfactor)));
	}
	
	/**the scale factor*/
	 public double getInflationFactor() {
		return inflationfactor;
	}
	
	//TODO: check to make sure text appears normal after this
	/**returns the font height and descent given a particular font and graphics*/
	public static double[] getFontHeightAndDescent(Font f, Graphics g) {
		TextPrecision tp= createPrecisForFont(f);
		FontMetrics metricsi0=tp.getInflatedMetrics(f, g);
		double fontHeight = metricsi0.getHeight()/tp.getInflationFactor();
        double descent = metricsi0.getDescent()/tp.getInflationFactor();
        return new double[] {fontHeight, descent};
	}
}
