/**
 * Author: Greg Mazo
 * Date Modified: Nov 14, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package figureOrganizer;

import java.awt.Color;

/**
 
 * 
 */
public enum FigureType {
	FLUORESCENT_CELLS, WESTERN_BLOT("Western Blot "), ELECTRON_MICROSCOPY, H_AND_E;
	
	/**if crop area is below this value, asks user to re-draw the crop area */
	 static int MIN_WIDTH_FOR_CROP_AREA = 25;
	/**if crop area is below this value, asks user to re-draw the crop area */
	static int MIN_HIEGHT_FOR_CROP_AREA = 25;
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

	public  int getMIN_WIDTH_FOR_CROP_AREA() {
		return MIN_WIDTH_FOR_CROP_AREA;
	}

	public  int getMIN_HIEGHT_FOR_CROP_AREA() {
		if(this==WESTERN_BLOT)
			return 10;
		return MIN_HIEGHT_FOR_CROP_AREA;
	}

	public double getMaxAspectRatioForCropArea() {
		if(this==WESTERN_BLOT)
			return 100;
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
