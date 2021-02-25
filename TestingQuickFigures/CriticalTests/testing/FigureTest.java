/**
 * Author: Greg Mazo
 * Date Modified: Feb 20, 2021
 * Version: 2021.1
 */
package testing;

import applicationAdapters.DisplayedImage;
import figureOrganizer.FigureOrganizingLayerPane;
import graphicActionToolbar.CurrentFigureSet;
import logging.IssueLog;

/**
 
 * 
 */
public class FigureTest {
	public DisplayedImage gg;
	
	/**
	 * @return
	 */
	public FigureOrganizingLayerPane createFirstExample() {
		FigureTester.setup();
		FigureTester figureTester = new FigureTester();
		figureTester.ignoreTemplate=true;
		FigureOrganizingLayerPane f = figureTester. createFigureFromExample1AImages();
		chooseCurrentWindow() ;
		return f;
	}
	
	/**
	 stores the current window
	 */
	public void chooseCurrentWindow() {
		gg=CurrentFigureSet.getCurrentActiveDisplayGroup();
		gg.getWindow().setLocation(10, 400);
	}
	
	/**
	 stores the current window
	 */
	public void closeCurrentWindow() {
		gg.closeWindowButKeepObjects();
	}
	
	/**
	 * 
	 */
	public void showUser() {
		gg.updateDisplay();
		IssueLog.waitSeconds(4);
	}
}
