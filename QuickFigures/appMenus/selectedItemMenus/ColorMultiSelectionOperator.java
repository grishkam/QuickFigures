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
 
 * 
 */
public interface ColorMultiSelectionOperator extends MultiSelectionOperator {

	/**
	 * @param fie
	 */
	void onColorInput(ColorInputEvent fie);

	/**
	 * @return
	 */
	boolean doesStroke();

}
