/**
 * Author: Greg Mazo
 * Date Modified: Feb 20, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package advancedChannelUseGUI;

import applicationAdapters.DisplayedImage;
import figureOrganizer.FigureOrganizingLayerPane;
import graphicActionToolbar.CurrentFigureSet;
import testing.FigureTester;

/**
 
 * 
 */
public class FigureTest {
	protected DisplayedImage gg;
	
	/**
	 * @return
	 */
	public FigureOrganizingLayerPane createFirstExample() {
		FigureTester.setup();
		FigureTester figureTester = new FigureTester();
		figureTester.ignoreTemplate=true;
		FigureOrganizingLayerPane f = figureTester. createFigureFromExample1AImages();
		return f;
	}
	
	/**
	 stores the current window
	 */
	public void chooseCurrentWindow() {
		gg=CurrentFigureSet.getCurrentActiveDisplayGroup();
		gg.getWindow().setLocation(10, 400);
	}
}
