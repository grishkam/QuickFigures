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
package appContext;

/**A certain number of points on the canvas correspond to 1 inch distance.
 * That number of points is 72 in QuickFigures. 
 * This ensures that the rulers end up being consistent with the rulers in Abobe Illustrator 
 * An image panel with a pixel density of 300 per inch in QuickFigures will have the same pixel density in illustrator when scaled to the same size
 * Confusingly, Inkscape has each inch correspond to 96 pixels (the CSS/SVG defined value) but has that distance still equal to 72 points in Inkscape. 
 * Draw (an application that is part of LibreOffice) seems to have rulers similar to Inkscape 
 * Exported Images still look identical in Inkscape but rulers in Inkscape are not consistent with QuickFigures.
 * 
 * For example an image panel 
 */
public class ImageDPIHandler {

	public static final int DEFAULT_POINTS_PER_INCH = 72,
							 	SVG_PIXELS_PER_INCH = 96;
	
	
	
	public static int inchDefinition= DEFAULT_POINTS_PER_INCH;
	public static RulerUnit rulerUnit=RulerUnit.DEFAULT_INCH;

	/**Returns the number of points on the canvas that corresponds to 1 inch distance*/
	public static int getInchDefinition() {
		return inchDefinition;
		
	}
	
	
	public static void setRulerUnit(RulerUnit rl) {
		
		rulerUnit=rl;
	}
	public static RulerUnit getRulerUnit() {
		return rulerUnit;
	}
	
	/**returns the pixel density ratio needed for panels with 300 pixels per inch*/
	public static double ratioForIdealDPI() {
		double i=getInchDefinition();
		return i/idealPanelPixelDesity();
	}
	
	
	public static double idealPanelPixelDesity() {return 300;}


	/**
	 */
	public static double getCMDefinition() {
		return ImageDPIHandler.getInchDefinition()/2.54;
	}
	
	
	


}
