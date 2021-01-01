/**
 * Author: Greg Mazo
 * Date Modified: Dec 19, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package selectedItemMenus;


import standardDialog.colors.ColorInputEvent;

/**
 classes that respond to the color popup menu from the minitoolbars
 implement this interface
 */
public interface ColorMultiSelectionOperator extends MultiSelectionOperator {

	/**responds to a color input
	 * @param fie
	 */
	void onColorInput(ColorInputEvent fie);

	/**returns true if the color menu should be labeled as a stroke color
	 * this will also affect the icon
	 * @return
	 */
	boolean doesStroke();
	
	

}
