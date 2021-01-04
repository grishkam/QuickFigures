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
 
 * 
 */
package icons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/***/
class ColorBlotchForIcon {
	
	private Rectangle blotchSize=new Rectangle(4,6, 5,5);
	Color blotchColor=Color.white;
	public Float blotchRadius=null;
	
	ColorBlotchForIcon(Rectangle2D r, Color c) {
		blotchSize=r.getBounds();
		blotchColor=c;
	}

/**
 */
public void paintBlotch(Graphics2D g2d, int arg2, int arg3) {
	Rectangle r2=new Rectangle(arg2+blotchSize.x, arg3+blotchSize.y, blotchSize.width, blotchSize.height);
	
	Color gradStart = new Color(blotchColor.getRed(), blotchColor.getGreen(), blotchColor.getBlue(), 0);
	RadialGradientPaint gp = new RadialGradientPaint(new Point2D.Double(r2.getCenterX(), r2.getCenterY()), getBlotchRadiusFor(r2), new float[] {(float) 0,(float) 0.6}, new Color[] {blotchColor, gradStart});
	
	
	g2d.setPaint(gp);
	g2d.fill(r2);
}

/**
 * @param r2
 * @return
 */
Float getBlotchRadiusFor(Rectangle r2) {
	if (blotchRadius!=null) return blotchRadius;
	return (float)r2.height;
}

}