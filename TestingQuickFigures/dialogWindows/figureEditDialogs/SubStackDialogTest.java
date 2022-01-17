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
 * Date Modified: Dec 6, 2020
 * Version: 2022.0
 */
package figureEditDialogs;

import figureOrganizer.FigureOrganizingLayerPane;
import graphicActionToolbar.QuickFigureMakerTest;

/**
 tests the appearance of the substack selection dialog
 */
public class SubStackDialogTest {

	//@Test//test does not need to be regularly performed
	public void test() {
		FigureOrganizingLayerPane qf = QuickFigureMakerTest.generateQuickFigure(3, 4, 1);
		SubStackDialog ss = new SubStackDialog(qf.getMultiChannelDisplaysInOrder());
		
		ss.setModal(true);
		ss.showDialog();
		
	}

}
