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
 * Date Modified: Feb 24, 2021
 * Version: 2022.0
 */
package layout.basicFigure;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Test;

import applicationAdapters.DisplayedImage;
import figureOrganizer.FigureLabelOrganizer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import testing.FigureTester;
import testing.TestingOptions;
import undo.UndoLayoutEdit;

/**
 Performs a series of automated tests on the layout editor class that both alters a layout and moves
 the objects within that layout accordingly.
 During these tests, the figure being edited remains visible to the user
 who may observe in order to spot any irregularities that were not anticipated by the manual tests
 Manual tests for the the layout tools and handles were also performed
 
 */
public class BasicLayoutEditorTest implements LayoutSpaces {

	private BasicLayoutEditor le;
	private DisplayedImage example;
	int waitTime = 20;//how long to pause for the user to visually see what edit has been tested
	private GraphicLayer targetLayer;
	DefaultLayoutGraphic layout;
	private BasicLayout layout2;

	@Test
	public void test() {
		IssueLog.sytemprint=true;
		
		example = new FigureTester.FigureProvider().createExample();
		LocatedObject2D objects = example.getImageAsWorksheet().getLocatedObjects().get(0);
		if (objects instanceof DefaultLayoutGraphic) {
			DefaultLayoutGraphic inputLayout = (DefaultLayoutGraphic) objects;
			performLayoutEditingTests(inputLayout);
			
		}
		else {
			
			fail("test assumes that automaticaly generated figure will have layout in the array");
		}
		
		/**pauses to allow user to observe */
		IssueLog.waitSeconds(TestingOptions.waitTimeAfterTests);
	
	}

	/**performs a series of automated tests for various layout edits
	 * to ensure that the contents of layouts are moved correctly.
	 * User can also see the changes to check for any issue
	 * @param inputLayout
	 */
	public void performLayoutEditingTests(DefaultLayoutGraphic inputLayout) {
		layout=inputLayout;
		layout.setAlwaysShow(true);
		layout2 = layout.getPanelLayout();
		layout.generateCurrentImageWrapper();
		le = new BasicLayoutEditor();
		targetLayer=layout.getParentLayer();
		layout.select();example.updateDisplay();
		
		
		moveMentTests();
		
		testRowColAdder(layout2, 1);
		testRowColAdder(layout2, 2);
		

		
		testBorderAdjuster(layout2, 2);
		testBorderAdjuster(layout2, 5);
		
		
		testRowSwaps();
		testColSwaps();
		
		
		testLabelSpaceSetter(20);
		testLabelSpaceSetter(40);

		performPanelSizeChangeTests();
		
		testRowColSizeChangesForEachRowCol();
		
		
		
		
		performTestsForLayoutInversion();
	}

	/**
	checks to make sure that objects that should move when layouts are inverted indeed move
	 */
	public void performTestsForLayoutInversion() {
		int r = layout2.nRows();
		int c = layout2.nColumns();
		
		if(c*r>1) {
			Rectangle2D locationWillChangeC = layout2.getPanelAtPosition(1, c);
			RectangularGraphic willChangeC = this.prepateTestObject(locationWillChangeC);
			
			Rectangle2D locationWillChangeR = layout2.getPanelAtPosition(r, 1);
			RectangularGraphic willChangeR = this.prepateTestObject(locationWillChangeR);
			
			/**objects in the first panel (row 1, col 1 are not moved when a layout is inverted)*/
			Rectangle2D locationWillNotChange = layout2.getPanelAtPosition(1, 1);
			RectangularGraphic willNotChange = this.prepateTestObject(locationWillNotChange);
			
			le.invertPanels(layout2);
			/**Assertion to make sure that objects within the panels move*/
			assert(!willChangeC.getRectangle().equals(locationWillChangeC));
			assert(!willChangeR.getRectangle().equals(locationWillChangeR));
			assert(willNotChange.getRectangle().equals(locationWillNotChange));
			this.showUser();
			
			
			le.invertPanels(layout2);
			/**makes sure the original locations are restored*/
			assert(willChangeC.getRectangle().equals(locationWillChangeC));
			assert(willChangeR.getRectangle().equals(locationWillChangeR));
			assert(willNotChange.getRectangle().equals(locationWillNotChange));
			this.showUser();
		}
	}

	/**
	performs a series of automated tests 
	to check if alterations in the sizes of rows
	and columns occur correctly
	 */
	public void testRowColSizeChangesForEachRowCol() {
		for(int c=1; c<=layout2.nColumns(); c++)
			peformIndividualColWidthChanges(c);
		for(int r=1; r<=layout2.nRows(); r++)
			this.peformIndividualRowHeightChanges(r);
	}

	/**changes a columns width and checks if objects are being moved properly
	 * @param the col index whose width will be changed
	 */
	public void peformIndividualColWidthChanges(int colIndex) {
		Rectangle2D locationWillNotChange = layout2.getPanelAtPosition(1, colIndex);
		RectangularGraphic willNotChange = this.prepateTestObject(locationWillNotChange);
		
		Rectangle2D locationWillChange=null;
		RectangularGraphic willChange=null;
		if(colIndex<layout2.nColumns()) {
			locationWillChange = layout2.getPanelAtPosition(1, colIndex+1);
			willChange = this.prepateTestObject(locationWillChange);
			
		}
		
		
		double original = layout2.getPanelWidthOfColumn(colIndex);
		int increase = 15;
		le.setPanelWidthOfColumn(layout2, original+increase,  colIndex);
		
		/**asserts to confirm that the object which should not move, did not move*/
		assert(locationWillNotChange.equals(willNotChange.getRectangle()));
		
		/**objects in subsequent columns should be moved. if there are subsequent columns*/
		if(locationWillChange!=null) {
			assert(locationWillChange.getX()!=willChange.getLocationUpperLeft().getX());
			assert(increase==willChange.getLocationUpperLeft().getX()-locationWillChange.getX());
			assert(locationWillChange.getY()==willChange.getLocationUpperLeft().getY());
		}
		this.showUser();
		
		
		le.setPanelWidthOfColumn(layout2, original,  colIndex);
		this.showUser();
	}
	
	/**changes a rows width and checks if objects are being moved
	 * @param the row index whose width will be changed
	 */
	public void peformIndividualRowHeightChanges(int rowIndex) {
		Rectangle2D locationWillNotChange = layout2.getPanelAtPosition( rowIndex, 1);
		RectangularGraphic willNotChange = this.prepateTestObject(locationWillNotChange);
		
		Rectangle2D locationWillChange=null;
		RectangularGraphic willChange=null;
		if(rowIndex<layout2.nRows()) {
			locationWillChange = layout2.getPanelAtPosition( rowIndex+1, 1);
			willChange = this.prepateTestObject(locationWillChange);
			
		}
		
		
		double original = layout2.getPanelHeightOfRow(rowIndex);
		int increase = 15;
		le.setPanelHeightOfRow(layout2, original+increase,  rowIndex);
		
		/**asserts to confirm that the object which should not move, did not move*/
		assert(locationWillNotChange.equals(willNotChange.getRectangle()));
		
		/**objects in subsequent rows should be moved*/
		if(locationWillChange!=null) {
			assert(locationWillChange.getX()==willChange.getLocationUpperLeft().getX());
			assert(locationWillChange.getY()!=willChange.getLocationUpperLeft().getY());
			assert(increase==willChange.getLocationUpperLeft().getY()-locationWillChange.getY());
		}
		this.showUser();
		
		
		le.setPanelHeightOfRow(layout2, original,  rowIndex);
		this.showUser();
	}

	/**
	performs a series of tests to make sure that objects within the layout are moved correctly in response to 
	changes in the standard panel size.
	Assumes that all panels in the layout use the standard panel size
	 */
	public void performPanelSizeChangeTests() {
		
		for(int col=1; col<=layout2.nColumns(); col++)
			for(int row=1; row<=layout2.nRows(); row++)
		{
			testStandardPanelSizeChanges(20,30, row, col);
			
		}
	}

	/**
	tests the methods for editing the standard panel heights
	@param row 
	@param col the location for a sample object to be placed. will test if the sample object is moved based on the layout change
	 */
	public void testStandardPanelSizeChanges(double widthincrease, double heightincrease, int row , int col) {
		//the row that the example rectangle is inside
		//the column that the example rectangle is inside
		Rectangle2D startLocationForSampleObject = layout2.getPanelAtPosition(row, col);
		RectangularGraphic testObject = this.prepateTestObject(startLocationForSampleObject);
		testObject.setStrokeColor(Color.blue);
	
		le.augmentStandardPanelWidth(layout2, widthincrease);
		/**makes sure object has moved if it needed to be*/
		if(col>1)
			 assert(testObject.getLocationUpperLeft().getX()!=startLocationForSampleObject.getX());
		else assert(testObject.getLocationUpperLeft().getX()==startLocationForSampleObject.getX());
		assert(widthincrease*(col-1)==testObject.getLocationUpperLeft().getX()-startLocationForSampleObject.getX());
		showUser();
		
		le.augmentStandardPanelWidth(layout2, -widthincrease);
		/**makes sure object has been moved back*/
		assert(testObject.getLocationUpperLeft().getX()==startLocationForSampleObject.getX());
		
		showUser();
		
		
		le.augmentStandardPanelHeight(layout2, heightincrease);
		showUser();
		/**makes sure object has moved if it needed to be*/
		if (row>1)
			  assert(testObject.getLocationUpperLeft().getY()!=startLocationForSampleObject.getY());
		else  assert(testObject.getLocationUpperLeft().getY()==startLocationForSampleObject.getY());
		assert(heightincrease*(row-1)==testObject.getLocationUpperLeft().getY()-startLocationForSampleObject.getY());
		
		
		le.augmentStandardPanelHeight(layout2, -heightincrease);
		/**makes sure object has moved back*/
		assert(testObject.getLocationUpperLeft().getY()==startLocationForSampleObject.getY());
		
		showUser();
		
		targetLayer.removeItemFromLayer(testObject);
	}

	
	/**Test methods for changing how much space at either sides of images are allocated for labels
	Manual testing done, this automated test method is a work in progress
	 * @param space
	 */
	public void testLabelSpaceSetter(int space) {
		UndoLayoutEdit undo = new UndoLayoutEdit(layout);
		
		/**if the top and left space setters for correctly, the location will be changed from 0,0 to a visible location*/
		le.setBaseLocation(layout2, 0, 0);
		this.showUser();
		le.setBottomLabelSpace(layout2, space);
		this.showUser();
		assert(layout2.labelSpaceWidthBottom==space);
		
		le.setTopLabelSpace(layout2, space);
		this.showUser();
		assert(layout2.labelSpaceWidthTop==space);
		le.setLeftLabelSpace(layout2, space);
		this.showUser();
		assert(layout2.labelSpaceWidthLeft==space);
		le.setRightLabelSpace(layout2, space);
		this.showUser();
		assert(layout2.labelSpaceWidthRight==space);
		undo.undo();
		this.showUser();
	}

	/**
	 * 
	 */
	public void moveMentTests() {
		
		testLayoutMovement(10);
		testLayoutMovement(20);
	}

	/**moves the entire layout and all of its content
	 * confirms that an object in the layout will also move
	 * @param moveAmount
	 */
	public void testLayoutMovement(int moveAmount) {
		testLayoutMovement(moveAmount,moveAmount);
		testLayoutMovement(-moveAmount,-moveAmount);
		testLayoutMovement(0,moveAmount);
		testLayoutMovement(0,-moveAmount);
		testLayoutMovement(moveAmount,0);
		testLayoutMovement(-moveAmount,-0);
	}

	/**
	moves the layout and its content, checks to make sure all objects move
	 */
	public void testLayoutMovement(int moveAmountX, int moveAmountY) {
		
		Rectangle2D[] panels = layout2.getPanels();//the innitial panel locations
		
		
		Rectangle2D startLocationForSampleObject = panels[0];
		RectangularGraphic sampleObject = prepateTestObject(startLocationForSampleObject);
		
		le.moveLayout(layout2,  moveAmountX, 0);
		this.showUser();
		le.moveLayout(layout2, 0,  moveAmountY);
		this.showUser();
		
		Rectangle2D[] panels2 = layout2.getPanels();//the final panel locations
		/**assert to make sure the panel locations have changed*/
		for(int i=0; i<panels.length; i++) 
			{
			assert(panels2[i].getCenterX()-panels[i].getCenterX()==moveAmountX);
			assert(panels2[i].getCenterY()-panels[i].getCenterY()==moveAmountY);
			
			}
		assert(sampleObject.getBounds().getX()-startLocationForSampleObject.getX()==moveAmountX);
		assert(sampleObject.getBounds().getY()-startLocationForSampleObject.getY()==moveAmountY);
		targetLayer.removeItemFromLayer(sampleObject);
	}

	/**adds a rectangular to the layout.
	 * Testimg methods will check if the object is moved in response to layout changes
	 * @param startLocationForSampleObject
	 * @return
	 */
	public RectangularGraphic prepateTestObject(Rectangle2D startLocationForSampleObject) {
		RectangularGraphic sampleObject = new RectangularGraphic(startLocationForSampleObject);
		targetLayer.addItemToLayer(sampleObject);//adds an object to test if object are also moved
		layout.generateCurrentImageWrapper();
		sampleObject.setStrokeColor(Color.red);
		return sampleObject;
	}

	/**tests border adjuster, user will visually see the border change
	 * @param layout2
	 * @param expand
	 */
	public void testBorderAdjuster(BasicLayout layout2, int expand) {
		double iBorder = layout2.theBorderWidthLeftRight;
		
		le.expandBorderX2(layout2, expand);
	
		assert(layout2.theBorderWidthLeftRight==expand+iBorder);
		showUser();
		
		
		le.expandBorderX2(layout2, -expand);
		assert(layout2.theBorderWidthLeftRight==iBorder);
		showUser();
		
		iBorder=layout2.theBorderWidthBottomTop;
		le.expandBorderY2(layout2, expand);
		assert(layout2.theBorderWidthBottomTop==expand+iBorder);
		showUser();
		
		le.expandBorderY2(layout2, -expand);
		assert(layout2.theBorderWidthBottomTop==iBorder);
		showUser();
		
		
		
		
		/**a more detailed set of tests for edits to the horizontal border between columns*/
		le.setHorizontalBorder(layout2, 0);
		
		Rectangle2D col2Starts = layout2.makeAltered(COLS).getPanel(2);
		RectangularGraphic sampleRect = new RectangularGraphic(col2Starts);
		targetLayer.addItemToLayer(sampleRect);
		layout.generateCurrentImageWrapper();
		int border2 = expand*2;
		le.setHorizontalBorder(layout2, border2);
		
		assert(layout2.theBorderWidthLeftRight==border2);//assertion to check to see if the border is set to the new value
		Rectangle2D col2Ends = layout2.makeAltered(COLS).getPanel(2);
		assert(col2Ends.getX()-col2Starts.getX()==border2);//assertion to see if the panel positions are changed
		assert(sampleRect.getBounds().getX()-col2Starts.getX()==border2);//assertion to ensure that objects move	
		showUser();
		targetLayer.removeItemFromLayer(sampleRect);
		
		
		
		
		/**a more detailed examination of vertical borders between rows*/
		
		le.setVerticalBorder(layout2, 0);
		Rectangle2D row2Starts = layout2.makeAltered(ROWS).getPanel(2);
		sampleRect = new RectangularGraphic(row2Starts);
		targetLayer.addItemToLayer(sampleRect);
		layout.generateCurrentImageWrapper();
		
		
		le.setVerticalBorder(layout2, border2);
		assert(layout2.theBorderWidthBottomTop==border2);
		
		Rectangle2D row2Ends = layout2.makeAltered(ROWS).getPanel(2);
		assert(row2Ends.getY()-row2Starts.getY()==border2);//Since the border increased from 0, object in the second col are expected to move a distance equal to the border 
		assert(sampleRect.getBounds().getY()-row2Starts.getY()==border2);//Since the border increased from 0, object in the second col are expected to move a distance equal to the border 
		
		showUser();
		targetLayer.removeItemFromLayer(sampleRect);
		
		
	}

	/**
	 * @param layout2
	 */
	public void testRowColAdder(BasicLayout layout2, int add) {
		testColumnAddition(layout2, add);
		testRowAddition(layout2, add);
	}

	/**
	 * @param layout2
	 * @param addedCols
	 */
	public void testColumnAddition(BasicLayout layout2, int addedCols) {
		int iCol=layout2.nColumns();
		le.addCols(layout2, addedCols);
		assert(iCol+addedCols==layout2.nColumns());
		
		showUser();
		
		
		le.addCols(layout2, -addedCols);
		assert(iCol==layout2.nColumns());
		
		showUser();
		
	}
	
	/**
	 * @param layout2
	 * @param addedCols
	 */
	public void testRowAddition(BasicLayout layout2, int addedRows) {
		int iCol=layout2.nRows();
		le.addRows(layout2, addedRows);
		assert(iCol+addedRows==layout2.nRows());
		
		showUser();
		
		le.addRows(layout2, -addedRows);
		assert(iCol==layout2.nRows());
		showUser();
		
	}
	
	public void testRowSwaps() {
		int n = layout2.nRows();
		for(int i=1; i<=n; i++)
			for(int j=1; j<=n; j++) {
				test1rowSwap( i, j);
			}
		
	}

	/**
	 * @param layout2
	 * @param i
	 * @param j
	 */
	public void test1rowSwap( int i, int j) {
		
		if(i==j)
			return;
		
		TextGraphic t1 = FigureLabelOrganizer.addRowLabel("starts in row "+i, i, targetLayer, layout, false);
		TextGraphic t2 = FigureLabelOrganizer.addRowLabel("starts in row "+j, j, targetLayer, layout, false);
		
		setTextMarkR(t1);
		setTextMarkR(t2);
		
		showUser();
		Point2D t1i = t1.getBaseLocation();
		Point2D t2i = t2.getBaseLocation();
		
		
		layout.generateCurrentImageWrapper();//
		IssueLog.waitSeconds(1);
		
		le.swapRow(layout2, i,j);
		
		Point2D t1f = t1.getBaseLocation();
		Point2D t2f = t2.getBaseLocation();
		
		

		showUser();
		showUser();
		/**asserts that the locations have changed*/	
		assert(!t1i.equals(t1f));
		assert(!t2i.equals(t2f));
		assert(t2i.getY()==t1f.getY());
		assert(t1i.getY()==t2f.getY());
		targetLayer.remove(t1);
		targetLayer.remove(t2);
		
	}

	/**
	a few setup steps so that the marker text items for each row during swaps appears
	 */
	public void setTextMarkR(TextGraphic t1) {
		layout.removeLockedItem(t1);
		t1.moveLocation(50, 0);
		t1.setTextColor(Color.red);
		if(t1 instanceof ComplexTextGraphic) {
			((ComplexTextGraphic) t1).getParagraph().getLastLine().getLastSegment().setTextColor(Color.red);
		}
	}
	/**
	a few setup steps so that the marker text items for each column during swaps appears
	 */
	public void setTextMarkC(TextGraphic t1) {
		layout.removeLockedItem(t1);
		t1.moveLocation(0, 40);
		t1.setTextColor(Color.red);
		if(t1 instanceof ComplexTextGraphic) {
			((ComplexTextGraphic) t1).getParagraph().getLastLine().getLastSegment().setTextColor(Color.red);
		}
	}
	
	public void testColSwaps() {
		int n = layout2.nColumns();
		for(int i=1; i<=n; i++)
			for(int j=1; j<=n; j++) {
				test1ColumnSwap(layout2, i, j);	
			}
		
	}

	/**tests to see if columns are swapped correctly
	 * @param layout2
	 * @param i
	 * @param j
	 */
	public void test1ColumnSwap(BasicLayout layout2, int i, int j) {
		if(i==j)
			return;
		
		TextGraphic t1 = FigureLabelOrganizer.addColLabel("Col "+i, i, targetLayer, layout);
		TextGraphic t2 = FigureLabelOrganizer.addColLabel("Col "+j, j, targetLayer, layout);
		
		setTextMarkC(t1);
		setTextMarkC(t2);
		
		showUser();
		Point2D t1i = t1.getBaseLocation();
		Point2D t2i = t2.getBaseLocation();
		
		layout.generateCurrentImageWrapper();//
		
		
		le.swapColumn(layout2, i,j);
		
		showUser();
		
		Point2D t1f = t1.getBaseLocation();
		Point2D t2f = t2.getBaseLocation();
		
		

		showUser();
		/**asserts that the locations have changed*/	
		assert(!t1i.equals(t1f));
		assert(!t2i.equals(t2f));
		assert(t2i.getX()==t1f.getX());
		assert(t1i.getX()==t2f.getX());
		targetLayer.remove(t1);
		targetLayer.remove(t2);
	}

	/**
	 updates the display and pauses a moment so that the human doing the testing
	 can visually see what is occuring
	 */
	public void showUser() {
		example.updateDisplay();
		IssueLog.waitMiliseconds(waitTime);//allows the user to see the test
	}

}
