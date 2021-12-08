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
 * Date Created: Nov 14, 2021
 * Date Modified: Nov 14, 2021
 * Version: 2021.2
 */
package figureOrganizer;

import java.awt.Color;

/**
 An enum that lists different types of figures. Methods determine how each type of figure will be treated
 */
public enum FigureType {
	FLUORESCENT_CELLS, WESTERN_BLOT("Western Blot "), ELECTRON_MICROSCOPY, H_AND_E;
	
	/**if crop area is below this value, asks user to re-draw the crop area */
	 static int MIN_WIDTH_FOR_CROP_AREA = 25;
	/**if crop area is below this value, asks user to re-draw the crop area */
	static int MIN_HEIGHT_FOR_CROP_AREA = 25;
	/**if crop area width/hieght or height/width is above this ratio, will ask user to re-draw*/
	static double MAX_ASPECT_RATIO_FOR_CROP_AREA = 3.5;
	
	String menuItemName="";
	
	FigureType() {}
	FigureType(String menuName) {
		this.menuItemName=menuName;
	}
	
	public Color getForeGroundDrawColor() {
		switch(this) {
			case WESTERN_BLOT: 
				return Color.black;
			case ELECTRON_MICROSCOPY: 
				return Color.black;
			case H_AND_E: 
				return Color.black;
		default: 
			return Color.white;
			
		}
			
		
	}
	
	/**returns the frame thickness that image panels start with in this type of figure*/
	public double getFrameWidth() {
		switch(this) {
		case WESTERN_BLOT: 
			return 2;
			default:
				return 0;
		}
		
	}

	/**a minimum width to determine when crop areas are valid and when to ask the user to adjust it*/
	public  int getMinWidthForCropArea() {
		return MIN_WIDTH_FOR_CROP_AREA;
	}

	/**a minimum height to determine when crop areas are valid and when to ask the user to adjust it*/
	public  int getMinHeightForCropArea() {
		if(this==WESTERN_BLOT)
			return 10;// western blots can have very long narrow crop areas. 
		return MIN_HEIGHT_FOR_CROP_AREA;
	}

	/***/
	public double getMaxAspectRatioForCropArea() {
		if(this==WESTERN_BLOT)
			return 100;// western blots can have very long narrow crop areas. 
		return MAX_ASPECT_RATIO_FOR_CROP_AREA;
	}
	/**
	 * @return
	 */
	public boolean needsScaleBar() {
		if(this==WESTERN_BLOT)
			return false;
		return true;
	}
	/**
	 * @return
	 */
	public boolean needsLabels() {
		if(this==WESTERN_BLOT)
			return false;
		return true;
	}
	
	/**returns whether or not a figure containing multiple single images is expected to contain more than one row or column
	 * @return
	 */
	public boolean doesExpandLayoutToBox() {
		if(this==WESTERN_BLOT)
			return false;
		return true;
	}
}
