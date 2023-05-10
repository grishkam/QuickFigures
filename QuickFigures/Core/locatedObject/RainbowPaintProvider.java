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
package locatedObject;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;

/**An implementation of paint provider that returns a color gradient with many colors
 * */
public class RainbowPaintProvider extends DefaultPaintProvider {

	/**generates a left-right rainbow*/
	public RainbowPaintProvider() {
		super(Color.BLACK);
		this.setFe1(RectangleEdges.LEFT);
		this.setFe2(RectangleEdges.RIGHT);
	}
	


	
	@Override
	public Paint getPaint() {
		
			return getRaindowGradient(point1,point2);
		
	}
	
	/**the rainbow*/
public static Color[] standardRBColors=new Color[] {Color.red, Color.GREEN, Color.blue, Color.cyan, Color.magenta, Color.yellow};
	
	/**Based on a starting and an ending points, generates a rainbow paint*/
	public static Paint getRaindowGradient(Point2D rr, Point2D r2) {
		float[] fracs=new float[standardRBColors.length]; fracs[0]=(float) (1.0/fracs.length); for(int i=1; i<fracs.length; i++) {fracs[i]=fracs[i-1]+(float) (1.0/fracs.length);}
		return new LinearGradientPaint((int)rr.getX(), (int)rr.getY(), (float) r2.getX(), (float) r2.getY(),  fracs, standardRBColors);
	}
	
	/**Based on a starting and an ending points, generates a rainbow paint*/
	public static Paint getRaindowGradient(Point2D rr, Point2D r2, ColorDimmer c) {
		Color[] standardRBColors2 = standardRBColors;
		standardRBColors2 =ColorDimmer.modifyArray(standardRBColors2, c, true);
		float[] fracs=new float[standardRBColors2.length]; fracs[0]=(float) (1.0/fracs.length); for(int i=1; i<fracs.length; i++) {fracs[i]=fracs[i-1]+(float) (1.0/fracs.length);}
		return new LinearGradientPaint((int)rr.getX(), (int)rr.getY(), (float) r2.getX(), (float) r2.getY(),  fracs, standardRBColors2);
	}
	
	/**Based on a starting and an ending points, generates a rainbow paint*/
	public static Paint getRaindowGradient(int x1, int x2, ColorDimmer c, Color[] standardRBColors2 ) {
		
		standardRBColors2 =ColorDimmer.modifyArray(standardRBColors2, c, true);
		float[] fracs=new float[standardRBColors2.length]; fracs[0]=(float) (1.0/fracs.length); for(int i=1; i<fracs.length; i++) {fracs[i]=fracs[i-1]+(float) (1.0/fracs.length);}
		return new LinearGradientPaint(x1, 0, x2, 0,  fracs, standardRBColors2);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
