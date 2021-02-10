/**
 * Author: Greg Mazo
 * Date Modified: Dec 6, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package figureEditDialogs;

import org.junit.Test;

import figureOrganizer.FigureOrganizingLayerPane;
import graphicActionToolbar.QuickFigureMakerTest;

/**
 
 * 
 */
public class SubStackDialogTest {

	@Test
	public void test() {
		FigureOrganizingLayerPane qf = QuickFigureMakerTest.generateQuickFigure(3, 4, 1);
		SubStackDialog ss = new SubStackDialog(qf.getMultiChannelDisplaysInOrder());
		
		ss.setModal(true);
		ss.showDialog();
		
	}

}
