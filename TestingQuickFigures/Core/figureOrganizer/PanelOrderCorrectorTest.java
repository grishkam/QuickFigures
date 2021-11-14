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
 * Date Modified: Feb 20, 2021
 * Version: 2021.2
 */
package figureOrganizer;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.junit.Test;

import layout.basicFigure.BasicLayoutEditor;
import layout.basicFigure.LayoutSpaces;
import testing.FigureTest;
import testing.FigureTester;
import undo.UndoLayoutEdit;

/**
 
 * 
 */
public class PanelOrderCorrectorTest  extends FigureTest {

	@Test
	public void test() {
		performOrderTest();
		performChannelEachTest();
		FigureTester.closeAllWindows();
	}
	
	/**
	test the methods that are used to determine if each col, row or panel is a distinct column
	 */
	public void performChannelEachTest() {
		FigureOrganizingLayerPane f = createFirstExample();
		super.chooseCurrentWindow();
		UndoLayoutEdit undo = new UndoLayoutEdit(f.getMontageLayoutGraphic());
		
		/**The first example is one in row major order with each column containing a different channel*/
		PanelOrderCorrector panelOrderCorrector = new  PanelOrderCorrector(f);
		assert(panelOrderCorrector.singleChannelPer(LayoutSpaces.COLS));
		assert(!panelOrderCorrector.singleChannelPer(LayoutSpaces.ROWS));
		assert(panelOrderCorrector.determineChannelLayout()!=LayoutSpaces.PANELS);
		
		new BasicLayoutEditor().invertPanels(f.getLayout());//inverts the panels so that each row now contains a single channel 
		assert(!panelOrderCorrector.singleChannelPer(LayoutSpaces.COLS));
		assert(panelOrderCorrector.singleChannelPer(LayoutSpaces.ROWS));
		assert(panelOrderCorrector.determineChannelLayout()!=LayoutSpaces.PANELS);
		new BasicLayoutEditor().invertPanels(f.getLayout());
		
		
		
		/**ruins the channel order by switching the physical locations of two panels in the top row but not the bottom row*/
		PanelList allPanelLists = f.getAllPanelLists();
		this.switchPhysicalLocations(allPanelLists.getPanels().get(0), allPanelLists.getPanels().get(1));
		assert(!panelOrderCorrector.singleChannelPer(LayoutSpaces.COLS));//confirms that figure no longer fits a logical channel order
		assert(!panelOrderCorrector.singleChannelPer(LayoutSpaces.ROWS));
		this.switchPhysicalLocations(allPanelLists.getPanels().get(0), allPanelLists.getPanels().get(1));
		
		undo.undo();
		
		/**swaps two columns and makes sure that the corrector finds that a new channel order is set*/
		f.getMontageLayoutGraphic().generateCurrentImageWrapper();
		ArrayList<Integer> oldOrder = panelOrderCorrector.determineChannelOrder().getChanPanelReorder().copyOfOrder();;
		ArrayList<Integer> newOrder = panelOrderCorrector.determineChannelOrder().getChanPanelReorder().copyOfOrder();;
		assert(oldOrder.equals(newOrder));
		int swap1=1;
		int swap2=3;
		new BasicLayoutEditor().swapColumn(f.getLayout(), swap1, swap2);
		assert(panelOrderCorrector.singleChannelPer(LayoutSpaces.COLS));
		newOrder = panelOrderCorrector.determineChannelOrder().getChanPanelReorder().copyOfOrder();;
		assert(!oldOrder.equals(newOrder));
		assert(newOrder.indexOf(swap1)==oldOrder.indexOf(swap2));
		assert(newOrder.indexOf(swap2)==oldOrder.indexOf(swap1));
		
		
		undo.undo();
		
		/**check to see if the channel order becomes panel based if only one channel panel exists for each channel*/
		f.remove(f.getMultiChannelDisplays().get(1));//removes the second image
		System.out.println("  "+panelOrderCorrector.determineChannelLayout());
		gg.updateDisplay();
		new BasicLayoutEditor().repackagePanels(f.getLayout(), 2, 3);//packages the panels into a 2*3 form. in these circumstances, each row and each column has more than one channel
		showUser();
		assert(panelOrderCorrector.isEachPanelADifferentChannel());
		assert(panelOrderCorrector.determineChannelLayout()==LayoutSpaces.PANELS);
		
	super.closeCurrentWindow();
		
	}

	

	/**
	 tests the methods that determine panel order
	 */
	public void performOrderTest() {
		FigureOrganizingLayerPane f = createFirstExample();
		super.chooseCurrentWindow();
		
		PanelOrderCorrector panelOrderCorrector = new  PanelOrderCorrector(f);
		int n = panelOrderCorrector.getOrderedPanelList().size();
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
		testOrderfinding(panelOrderCorrector, i, j);
		super.closeCurrentWindow();
	}

	/**a test to make sure that changes in the physical location of panels
	 * causes the panel order corrector to detect a change of panel order 
	 * @param panelOrderCorrector
	 * @param j 
	 * @param i 
	 */
	private void testOrderfinding(PanelOrderCorrector panelOrderCorrector, int i, int j) {
		ArrayList<PanelListElement> original = panelOrderCorrector.getOrderedPanelList();
		if(i==j||i>= original .size()||j>=original.size())
			return;
		PanelListElement panelI = original.get(i);
		PanelListElement panelJ = original.get(j);
		
	
		/**switches the locations and has the order corrector determine the new order*/
		switchPhysicalLocations(panelI, panelJ);
		ArrayList<PanelListElement> neworder = panelOrderCorrector.getOrderedPanelList();
		
		assert(neworder.indexOf(panelI)==original.indexOf(panelJ));
		assert(neworder.indexOf(panelJ)==original.indexOf(panelI));
	
	}

	/**
	 * @param panelI
	 * @param panelJ
	 */
	public void switchPhysicalLocations(PanelListElement panelI, PanelListElement panelJ) {
		Point2D li = panelI.getImageDisplayObject().getLocationUpperLeft();
		Point2D lj = panelJ.getImageDisplayObject().getLocationUpperLeft();
		panelI.getImageDisplayObject().setLocationUpperLeft(lj);
		panelJ.getImageDisplayObject().setLocationUpperLeft(li);
	}

}
