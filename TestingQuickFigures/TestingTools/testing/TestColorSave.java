/**
 * Author: Greg Mazo
 * Date Modified: Nov 14, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package testing;

import java.awt.Color;
import java.awt.Window;

import org.junit.Test;

import appContextforIJ1.ImageDisplayTester;
import exportMenus.ShowExportExamples;
import figureOrganizer.FigureOrganizingLayerPane;
import imageDisplayApp.GraphicSetDisplayWindow;
import logging.IssueLog;
import multiChannelFigureUI.ChannelPanelEditingMenu;

/**
 created help debug a channel color saving issue. In this test no problems are seen.
 It appears to be very context specific. however if one changes the luts manually in imageJ prior to crating a figure,
 All subsequent changes are saved. Perhaps some images lack luts or lack them in a saveable format???
 
 */
public class TestColorSave {

	@Test
	public void test() {
		ImageDisplayTester.startToolbars(true);
		IssueLog.sytemprint=true;
		FigureOrganizingLayerPane figure = new FigureTester(). createFigureFromExample1AImages();
		
		/**sets an artifical color*/
		 new ChannelPanelEditingMenu(figure, 1).setTheColor(Color.cyan, 2);
		 new ChannelPanelEditingMenu(figure, 1).setTheColor(Color.yellow, 3);
		 
		Window[] windows = Window.getWindows();
		Window win1 = windows[windows.length-1];
		if(win1 instanceof GraphicSetDisplayWindow) {
			GraphicSetDisplayWindow win2=(GraphicSetDisplayWindow) win1;
			ShowExportExamples.testSaveAndReopen(win2. getDisplaySet());
		}
		
		/** the time needed for a user to check forproblems*/
		IssueLog.waitSeconds(35);
		
	}

}
