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
