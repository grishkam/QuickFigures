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
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;

public class RainbowPaintProvider extends DefaultPaintProvider {

	public RainbowPaintProvider() {
		super(Color.BLACK);
		this.setFe1(RectangleEdges.LEFT);
		this.setFe2(RectangleEdges.RIGHT);
	}
	


	
	@Override
	public Paint getPaint() {
		
			return getRaindowGradient(point1,point2);
		
	}
	
public static Color[] standardRBColors=new Color[] {Color.red, Color.GREEN, Color.blue, Color.cyan, Color.magenta, Color.yellow};
	
	
	public static Paint getRaindowGradient(Point2D rr, Point2D r2) {
		float[] fracs=new float[standardRBColors.length]; fracs[0]=(float) (1.0/fracs.length); for(int i=1; i<fracs.length; i++) {fracs[i]=fracs[i-1]+(float) (1.0/fracs.length);}
		return new LinearGradientPaint((int)rr.getX(), (int)rr.getY(), (float) r2.getX(), (float) r2.getY(),  fracs, standardRBColors);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
