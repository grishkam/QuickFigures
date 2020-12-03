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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**Font metrics objects in java return integers but some classes in quickfigures
  need something that is close to a double equivalent.
  this class is used as a workaround.*/
public class TextPrecision {
		int inflationfactor=500;
	
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
	
	public FontMetrics getInflatedMetrics(Font f, Graphics g) {
		return g.getFontMetrics(f.deriveFont((float)f.getSize()*inflationfactor));
	}
	


	
	public double getInflationFactor() {
		return (double)inflationfactor;
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
