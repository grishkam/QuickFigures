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
package locatedObject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public interface StrokedItem {
	/**returns the stroke width */
	public float getStrokeWidth();
	public Stroke getStroke();
	public void setStrokeWidth(float width);
	
	/**getter and setter methods for the stroke color*/
	public void setStrokeColor(Color c);
	public Color getStrokeColor();
	public float[] getDashes();
	public void setDashes(float[] fl);
	void setStroke(BasicStroke stroke);
	public int getStrokeJoin();
	public int getStrokeCap();
	public void setStrokeJoin(int selectedIndex);
	public void setStrokeCap(int size);
	public void setMiterLimit(double miter);
	public double getMiterLimit();
}
