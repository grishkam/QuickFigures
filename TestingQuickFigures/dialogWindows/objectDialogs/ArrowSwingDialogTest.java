/**
 * Author: Greg Mazo
 * Date Modified: Dec 7, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package objectDialogs;

import java.awt.Color;
import java.awt.Point;

import org.junit.Test;

import graphicalObjects_Shapes.ArrowGraphic;
import imageDisplayApp.ImageWindowAndDisplaySet;
import testing.DialogTester;

/**
 
 * 
 */
public class ArrowSwingDialogTest extends  DialogTester  {

	@Test
	public void test() {
		
		ImageWindowAndDisplaySet i = ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,200);
		
		ArrowGraphic arrowGraphic = new ArrowGraphic(new Point(50,30), new Point(150,130));
		arrowGraphic.setHeadsSame(false);
		arrowGraphic.setStrokeColor(Color.black);
		i.getImageAsWorksheet().addItemToImage(arrowGraphic);
		
		ArrowSwingDialog d = arrowGraphic .getOptionsDialog();
		d.showDialog();
		
		/**changes all of the options and field values.
		  should reveal if any option or combination causes an exception
		  takes a long time to run*/
		testCombinations(d);
		

		
		d.setVisible(false);
	}

}
