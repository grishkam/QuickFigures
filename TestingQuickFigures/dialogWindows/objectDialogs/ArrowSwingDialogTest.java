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
package objectDialogs;

import java.awt.Color;
import java.awt.Point;

import org.junit.Test;

import graphicalObjects_Shapes.ArrowGraphic;
import imageDisplayApp.ImageWindowAndDisplaySet;
import testing.DialogTester;
import testing.FigureTester;

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
		

		FigureTester.closeAllWindows(true);
		d.setVisible(false);
	}

}
