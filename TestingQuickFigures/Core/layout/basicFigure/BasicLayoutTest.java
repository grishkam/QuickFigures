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
 * Date Modified: Dec 20, 2020
 * Version: 2023.1
 */
package layout.basicFigure;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.junit.Test;

import logging.IssueLog;

/**
 tests certain parts of the layout class. 
 A lot of manual testing was also done, since any deviation from expected
 function of the BasicLayout class will cause visible issues with the layouts
 and layout tools, manual testing was more effective at detecting problems than automated tests
 */
public class BasicLayoutTest implements LayoutSpaces{

	@Test
	public void test() {
		IssueLog.sytemprint=true;

		/**tests the calculations for panel position*/
		testBasicTraits(true);
		testBasicTraits(false);
		
		testPanelSizeAndLocationChanges();
		
		
		
	}
	
	/**
	 test to determine if changes to the panel width and panel height 
	 are correctly calculated
	 */
	public void testUniqueSizesChanges() {
		int oldWidth = 100;
		int oldHeight = 50;
		BasicLayout layout = new BasicLayout(12, 8, oldWidth, oldHeight, 3, 2, true);
		
		int[] newWidths = new int[] {2, 150, 500};
		
		for(int c=1; c<=layout.nColumns(); c++) {
			int targetColumn=c;
		for(int newWidth: newWidths)
			testUniqueWidthSet(layout, newWidth, targetColumn);
		
		}
		
		for(int c=1; c<=layout.nRows(); c++) {
			int targetRow=c;
		for(int newWidth: newWidths)
			testUniqueHeightSet(layout, newWidth, targetRow);
		
		}
	}

	/**tests to make sure unique widths and heights are set and recovered correctly
	 * @param layout
	 * @param newWidth
	 * @param targetColumn
	 */
	public void testUniqueWidthSet(BasicLayout layout, int newWidth, int targetColumn) {
		
		layout.setColumnWidth(targetColumn, newWidth);
		layout.resetPtsPanels();
		assert(	layout.getPanelWidthOfColumn(targetColumn)==newWidth);
		int l = layout.getIndexAtPosition(1, targetColumn);
		assert(layout.getPanel(l).getWidth()==newWidth);
	}
	
	/**tests to make sure unique widths and heights are set and recovered correctly
	 * @param layout
	 * @param newH
	 * @param targetRow
	 */
	public void testUniqueHeightSet(BasicLayout layout, int newH, int targetRow) {
		
		layout.setRowHeight(targetRow, newH);
		layout.resetPtsPanels();
		assert(	layout.getPanelHeightOfRow(targetRow)==newH);
		int l = layout.getIndexAtPosition( targetRow, 1);
		assert(layout.getPanel(l).getHeight()==newH);
	}

	/**
	 test to determine if changes to the panel width and panel height 
	 are correctly calculated
	 */
	public void testPanelSizeAndLocationChanges() {
		BasicLayout layout = new BasicLayout(3, 2, 100, 50, 3, 2, true);
		
		/**tests movement of layout*/
		assert(layout.getPanel(2).getX()==103);//expected location for second panel
		assert(layout.getPanel(2).getY()==0);//expected location for second panel
		assert(layout.getPanel(5).getX()==103);//expected location for 5th panel
		assert(layout.getPanel(5).getY()==52);//expected location for 5th panel
		layout.move(40, 100);
		assert(layout.getPanel(2).getX()==143);//expected location for second panel after movement
		assert(layout.getPanel(2).getY()==100);//expected location for second panel after movement
		assert(layout.getPanel(5).getX()==143);//expected location for 5th panel after movement
		assert(layout.getPanel(5).getY()==152);//expected location for 5th panel after movement
		layout.move(-40, -100);
		
		layout.setStandardPanelWidth(110);
		assert(layout.getPanel(2).getX()==113);//expected location for second panel after width change
		assert(layout.getPanel(2).getY()==0);//expected location for second panel after width change
		assert(layout.getPanel(5).getX()==113);//expected location for 5th panel  after width change
		assert(layout.getPanel(5).getY()==52);//expected location for 5th panel  after width change
		layout.setStandardPanelWidth(100);
		
		layout.setStandardPanelHeight(100);
		assert(layout.getPanel(5).getY()==102);//expected location for 5th panel  after width change
		layout.setStandardPanelHeight(50);
		
		layout.rowmajor=false;
		layout.resetPtsPanels();//updates the points and panels to the new layout
		assert(layout.getPanel(2).getX()==0);//expected location for second panel after change
		assert(layout.getPanel(2).getY()==52);//expected location for second panel after change
		assert(layout.getPanel(5).getX()==206);//expected location for 5th panel  after change
		assert(layout.getPanel(5).getY()==0);//expected location for 5th panel  after change
		
		layout = new BasicLayout(3, 2, 100, 50, 3, 2, true);
		layout.setColumnWidth(1, 210);
		layout.setColumnWidth(2, 175);
		layout.resetPtsPanels();
		assert(layout.getPanel(2).getX()==213);//expected location for second panel after width change
		assert(layout.getPanelWidth(1)==210);//the first panel is in the 1st col so should have that width
		assert(layout.getPanelWidth(2)==175);
		assert(layout.getPanel(3).getX()==213+175+3);//if the 1st and 2nd columns have a new width, panels in the 3rd column will be shifted to that location
		
		layout.setRowHeight(2, 20);
		layout.resetPtsPanels();
		assert(layout.getPanelHeight(4)==20);//the 4th panel is in the 2nd row so should have this heigth
		
	}

	/**
	Some layout transformations are critical for certain features. tests to make sure that the most important ones return 
	layouts with dimensions that are matches to the originals
	 */
	public void testTransforms(BasicLayout layout) {
		Rectangle bounds = layout.getSelectedSpace(ALL_OF_THE+ PANELS).getBounds();
		Rectangle boundsRows = layout.makeAltered(ROW_OF_PANELS).getSelectedSpace(ALL_OF_THE+ PANELS).getBounds();
		Rectangle boundsCols = layout.makeAltered(COLUMN_OF_PANELS).getSelectedSpace(ALL_OF_THE+ PANELS).getBounds();
	
		assert(bounds.equals(boundsRows));
		assert(bounds.equals(boundsCols));
		
		bounds = layout.getSelectedSpace(ALL_OF_THE+ ROWS).getBounds();
		boundsRows = layout.makeAltered(ROWS).getSelectedSpace(ALL_OF_THE+ ROWS).getBounds();
		assert(bounds.equals(boundsRows));
		
		bounds = layout.getSelectedSpace(ALL_OF_THE+ COLS).getBounds();
		boundsRows = layout.makeAltered(COLS).getSelectedSpace(ALL_OF_THE+ COLS).getBounds();
		assert(bounds.equals(boundsRows));
	}

	/**
	 * @param rowMajor
	 */
	public void testBasicTraits(boolean rowMajor) {
		int[] manyNumbers=new int[] {2,3,5,200, 300, 250};
		for(int width: manyNumbers)
		for(int height: manyNumbers)
		for(int xborder: manyNumbers)
		for(int yborder: manyNumbers)
			testPanelLocations(rowMajor, xborder, yborder, width, height);
	}

	/**checks to determine that panel positions are calculated correctly
	 * @param rowMajor
	 * @param xborder
	 * @param yborder
	 * @param width
	 * @param height
	 */
	public void testPanelLocations(boolean rowMajor, int xborder, int yborder, int width, int height) {
		int nCol =3;
		int nRow = 4;
		/**creates a new layout starting at location (0,0)*/
		BasicLayout layout = new BasicLayout(nCol, nRow, width, height, xborder, yborder, rowMajor);
		
		/**the most basic check*/
		Rectangle2D p = layout.getPanel(1);
		assert(p.getWidth()== width);
		assert(p.getHeight()==height);
		assert(layout.nPanels()==nRow*nCol);
		
		/**the second panel is located to the right of the first. checks to make sure it is in the correct location */
		Rectangle2D p2 = layout.getPanel(2);
		if (rowMajor&&nCol>1)
			assert(p2.getX()==(width+xborder));
		else 
			assert(p2.getX()==0);
		if (rowMajor&&nCol>1)
			assert(p2.getY()==0);
		else 
			assert(p2.getY()==(height+yborder));
		
		/**checks the 3rd panel*/
		p2 = layout.getPanel(3);
		if (rowMajor&&nCol>2)
			assert(p2.getX()==(width+xborder)*2);
		else 
			assert(p2.getX()==0);
		if (rowMajor&&nCol>2)
			assert(p2.getY()==0);
		else 
			assert(p2.getY()==(height+yborder)*2);
		
		/**the 4th panel should be in the second row*/
		p2 = layout.getPanel(4);
		if (rowMajor&&nCol<=3)
			assert(p2.getX()==0);
		if (rowMajor&&nCol<=3)
			assert(p2.getY()==(height+yborder));
		
		if (!rowMajor) {
			/**in this case the 5th panel should be in the first row*/
			p2 = layout.getPanel(5);
			assert(p2.getY()==0);
			assert(p2.getX()==(width+xborder));
		}
		
		testTransforms(layout);
		testUniqueSizesChanges();
	}

}
