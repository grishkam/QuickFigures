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
 * Version: 2023.1
 */
package locatedObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.io.Serializable;

/**A class that supplies an object of class paint 
 * that depends on a particular shape*/
public interface PaintProvider extends Serializable{

	
	public Paint getPaint();
	
	public void showOptionsDialog();
	
	public void setPaintedShape(Shape s);
	
	/**returns the first color*/
	public Color getColor();
	/**sets the first color*/
	public void setColor(Color c);
	/**gets the color*/
	public Color getColor(int i);
	/**sets the color*/
	public void setColor(int i, Color c);
	
	/***/
	public void fillShape(Graphics2D graphics, Shape s);
	
	/***/
	void strokeShape(Graphics2D g, Shape s);
	
	/**Sets the shape being used*/
	void setStrokeShape(Shape s);
	
}
