/**
 * Author: Greg Mazo
 * Date Modified: Dec 13, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package graphicalObjects_SpecialShapes;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import appContext.CurrentAppContext;
import appContextforIJ1.ImageDisplayTester;
import channelMerging.PreProcessInformation;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import graphicActionToolbar.QuickFigureMaker;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import ij.IJ;
import logging.IssueLog;
import popupMenusForComplexObjects.ImagePanelMenu;
import testing.FigureTester;

/**
 Testing of the scale bar sizes. 
 Uses a series of saved images of known pixel size 
 */
public class BarGraphicTest {

	/**these are the pixel sizes of the test cases given in microns. 
	 Although software showed the numbers rounded to 0.01, more precise values are within the metadata */
	double[] testImageScales=new double[] {0.1, 0.06, 0.16, 0.26, 0.52, 0.65, 1.04, 2.07};
	
	@Test
	public void test() {
		prepareForTest();
		IJ.wait(100);
		
		/***/
		double pixelSizeFromOriginal=0.1;//pixel size from the 63x .zvi file used for the first test case
		double scaleFactorAppliedWhenProcessingImage=1;//none for this case
		double relativePanelSize=1;// 1:1 ratio for the first case
		double scaleBarLengthInUnits=20;// 20 microns
		double expectedScaleBarWidth=195;//this was the size of the imageJ scale bar under these same circumstances
		
		FigureOrganizingLayerPane figureWithScaleBars = getFigureForTestCaseWithPixelSize(pixelSizeFromOriginal);
		
		/**The first test case. expected size determined using the scale bar tool that is built into imageJ*/
		testThisCase(figureWithScaleBars,  scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize,
				expectedScaleBarWidth);
		
		/**doubling the size of the image (same as ImageJ's Scale... command from the image menu) should also double the scale bar size*/
		testThisCase(figureWithScaleBars,scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage*2, relativePanelSize, 
				expectedScaleBarWidth*2);
		
		/**double the size of the panels should also double the scale bars size*/
		testThisCase(figureWithScaleBars,  scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize*2,
				expectedScaleBarWidth*2);
		
		
		/**second test case uses different inntial parameters on the first image*/
		pixelSizeFromOriginal=0.1;
		scaleBarLengthInUnits=10;
		scaleFactorAppliedWhenProcessingImage=0.5;
		relativePanelSize=0.24;
		expectedScaleBarWidth=12;//manually calculated based on on the other parameters
		/**The second test case. Expected size determined by an equation in excel */
		testVariationsOfThisTestCase(figureWithScaleBars, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize,
				expectedScaleBarWidth);
		
		
		/**3rd test case with a different image. this one taken with 100x objective on same microscope*/
		pixelSizeFromOriginal=0.06;
		figureWithScaleBars=getFigureForTestCaseWithPixelSize(pixelSizeFromOriginal);
		//but similar parameters
		scaleBarLengthInUnits=10;
		scaleFactorAppliedWhenProcessingImage=0.5;
		relativePanelSize=0.24;
		expectedScaleBarWidth=20;// Expected size determined by an equation in excel
		/**The second test case. Expected size determined by an equation in excel */
		testVariationsOfThisTestCase(figureWithScaleBars, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize,
				expectedScaleBarWidth);
		/**another variation of the test case with an enlargement of size. 833.3333333 calculated in excel*/
		testVariationsOfThisTestCase(figureWithScaleBars, 20, 2.5, 1,833.3333333);
		/**another set of variations of the test case, with an enlarged relative scale. also calculated in excel*/
		testVariationsOfThisTestCase(figureWithScaleBars, 10, 1, 4,666.6666667);

		/**another example with expected size based on the scale bar tool in ImageJ*/
		scaleBarLengthInUnits=50;
		scaleFactorAppliedWhenProcessingImage=2;
		relativePanelSize=1;
		expectedScaleBarWidth=1544;//This was the size of an imageJ scale bar under similar ciscumstances
		testVariationsOfThisTestCase(figureWithScaleBars, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize,
				expectedScaleBarWidth);
		
		
		/**4th test case with a different image. this one taken with 10x objective on same microscope*/
		pixelSizeFromOriginal=0.65;
		figureWithScaleBars=getFigureForTestCaseWithPixelSize(pixelSizeFromOriginal);
		//but similar parameters
		scaleBarLengthInUnits=100;
		scaleFactorAppliedWhenProcessingImage=0.1;
		relativePanelSize=1;
		expectedScaleBarWidth=15.38;// Expected size determined by an equation in excel
		/**The second test case. Expected size determined by an equation in excel */
		testVariationsOfThisTestCase(figureWithScaleBars, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize,
				expectedScaleBarWidth);
		
		
		
		/**4th test case with a different image. this one taken with 10x objective on same microscope*/
		pixelSizeFromOriginal=0.16;
		figureWithScaleBars=getFigureForTestCaseWithPixelSize(pixelSizeFromOriginal);
		/**Expected scale bar size determined by an equation in excel */
		testVariationsOfThisTestCase(figureWithScaleBars, 20, 0.5,0.24,15);
		
		pixelSizeFromOriginal=0.26;
		figureWithScaleBars=getFigureForTestCaseWithPixelSize(pixelSizeFromOriginal);
		/**Expected scale bar size determined by an equation in excel (11.53846154) */
		testVariationsOfThisTestCase(figureWithScaleBars, 50, 0.25,0.24,  11.53846154);
		
		pixelSizeFromOriginal=0.52;
		figureWithScaleBars=getFigureForTestCaseWithPixelSize(pixelSizeFromOriginal);
		/**Expected scale bar size determined by an equation in excel (19.23076923) and (2.307692308) */
		testVariationsOfThisTestCase(figureWithScaleBars, 100, 0.1, 1,  19.23076923);
		testVariationsOfThisTestCase(figureWithScaleBars, 50, 0.1, 0.24,  2.307692308);
		
		
		pixelSizeFromOriginal=1.04;
		figureWithScaleBars=getFigureForTestCaseWithPixelSize(pixelSizeFromOriginal);
		/**Expected scale bar size determined by an equation in excel (4.807692308) and (5.769230769) */
		testVariationsOfThisTestCase(figureWithScaleBars, 100, 0.1, 0.5, 4.807692308);
		testVariationsOfThisTestCase(figureWithScaleBars, 250, 0.1, 0.24,  5.769230769);
		
		
		pixelSizeFromOriginal=2.07;
		figureWithScaleBars=getFigureForTestCaseWithPixelSize(pixelSizeFromOriginal);
		/**Expected scale bar size determined by an equation in excel (2.415458937) */
		testVariationsOfThisTestCase(figureWithScaleBars, 500, 0.1, 0.1, 2.415458937);
		
		
		
		
		performScaleInfoConsistencyTests();
		
		FigureTester.closeAllWindows();
	
	}

	/** calls the test case method a few times
	 * multiplying and diving the parameters each time 
	 * @param f
	 * @param scaleBarLengthInUnits
	 * @param scaleFactorAppliedWhenProcessingImage
	 * @param relativePanelSize
	 * @param expectedScaleBarWidth
	 */
	void testVariationsOfThisTestCase(FigureOrganizingLayerPane f, double scaleBarLengthInUnits,
			double scaleFactorAppliedWhenProcessingImage, double relativePanelSize, double expectedScaleBarWidth) {
		
		/**performs the test case with the parameters given*/
		testThisCase(f, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize, 
				expectedScaleBarWidth);
		
		scaleBarLengthInUnits/=2;//half of previous test case. using a scale bar of 5 microns instead of 10 for example
		expectedScaleBarWidth/=2;//half of previous test case
		testThisCase(f, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize, 
				expectedScaleBarWidth);
		
		relativePanelSize/=2;//half of previous test case. panels with same number of pixels are displayed in a smaller area
		expectedScaleBarWidth/=2;//half of previous test case
		testThisCase(f, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize, 
				expectedScaleBarWidth);
		relativePanelSize*=4;//quadruples the previous case. panels with same number of pixels are displayed in a larger area
		expectedScaleBarWidth*=4;//
		testThisCase(f, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize, 
				expectedScaleBarWidth);
		
		
		scaleFactorAppliedWhenProcessingImage*=2;//double the previous test case
		expectedScaleBarWidth*=2;
		testThisCase(f, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize, 
				expectedScaleBarWidth);
		
		scaleFactorAppliedWhenProcessingImage/=4;// quarters the previous cases' size
		expectedScaleBarWidth/=4;
		testThisCase(f, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize, 
				expectedScaleBarWidth);
		
		scaleBarLengthInUnits*=4;//multiplies by 4. For example using a scale bar of 20 microns instead of 5 for example
		expectedScaleBarWidth*=4;//multiplies by 4.
		testThisCase(f, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize, 
				expectedScaleBarWidth);
	}

	/**
	 * @param pixelSizeFromOriginal
	 * @param scaleFactorAppliedWhenProcessingImage
	 * @param relativePanelSize
	 * @param scaleBarLengthInUnits
	 * @param expectedScaleBarWidth
	 */
	void testCase(double pixelSizeFromOriginal, double scaleBarLengthInUnits, double scaleFactorAppliedWhenProcessingImage, double relativePanelSize,
			 double expectedScaleBarWidth) {
		FigureOrganizingLayerPane f = getFigureForTestCaseWithPixelSize(pixelSizeFromOriginal);
		testThisCase(f, scaleBarLengthInUnits, scaleFactorAppliedWhenProcessingImage, relativePanelSize,
				expectedScaleBarWidth);
	}

	/**Performs the test case for the figure given
	 * @param figure
	 * @param scaleBarLengthInUnits
	 * @param scaleFactorAppliedWhenProcessingImage Scale factor applied to source image
	 * @param relativePanelSize The size of the panels 
	 * @param expectedScaleBarWidth  expected value calculated independently 
	 */
	void testThisCase(FigureOrganizingLayerPane figure, double scaleBarLengthInUnits,
			double scaleFactorAppliedWhenProcessingImage, double relativePanelSize, double expectedScaleBarWidth) {
		switchScale(figure, scaleFactorAppliedWhenProcessingImage);
		ArrayList<ImagePanelGraphic> panels = figure.getAllPanelLists().getPanelGraphics();
		for(ImagePanelGraphic panel: panels) {
			panel.setRelativeScale(relativePanelSize);
			setScaleBarSize(panel, scaleBarLengthInUnits);
			double barWidth = panel.getScaleBar().getBarRectangle().getWidth();
			assertSimilarInteger(expectedScaleBarWidth, barWidth);
		}
	}

	/**opens a file and crates a figure with the given pixel size
	 * assuming that the file exists in the appropriate folder
	 * @param pixelSizeFromOriginal
	 * @return
	 */
	FigureOrganizingLayerPane getFigureForTestCaseWithPixelSize(double pixelSizeFromOriginal) {
		IssueLog.log("opening image of pixel size "+pixelSizeFromOriginal);
		return createFigure(getTestExampleOfLength(pixelSizeFromOriginal));
	}
	
	/**when given a scale in units per pixel, returns the example file with that 
	 * measurement*/
	File getTestExampleOfLength(double d) {
		int scaleTests = FigureTester.getScaleTestPaths().length;
		for(int exnumber=0; exnumber<scaleTests; exnumber++)
			{
			File file = new FigureTester().getScaleExample(exnumber);
			double d2 = getKnownDistancePerPixel(file);
			if(d2==d)
				return file;
			}
		IssueLog.showMessage("could not find test example of that size "+d);
		return null;
	}

	/**iterates through all of the test cases saved in the folder
	performs a series of tests on each image with known scale included in the filename
	 */
	void performScaleInfoConsistencyTests() {
		int scaleTests = FigureTester.getScaleTestPaths().length;
		for(int exnumber=0; exnumber<scaleTests; exnumber++)
			performScaleInfoTest(exnumber);
	}

	/**Opens an image with a known scale (present in filename) performs some automated tests
	  to make sure that the scale info being transmitted from the original image, to the processed one
	  all the way down to the scale bars is consistent. 
	  Two values can affect what is transmitted: the scaling applied to the source image and the
	  size of the panel objects
	 * @param exnumber
	 */
	void performScaleInfoTest(int exnumber) {
		File fig1 =new FigureTester(). getScaleExample(exnumber);
		
		/**generates a figure from the example image*/
		FigureOrganizingLayerPane figure = createFigure(fig1);
		
		/**the known distance given by the microscope software was placed in the filename*/
		double knownDistance = getKnownDistancePerPixel(fig1);
		
		IssueLog.log("checking if an image was opened");
		double pixWidth = figure.getPrincipalMultiChannel().getSlot().getScaleInfo().getPixelWidth();
		
		/**check to make sure the pixel size in the filename is similar to the one that is read fromn the metadata*/
		assertSimilarNumber(knownDistance, pixWidth);
		IssueLog.log("Passed known distance test");
		
		
		/**each panel should contain the scale information appropriate to its source image*/
		ArrayList<ImagePanelGraphic> panels = figure.getAllPanelLists().getPanelGraphics();
		for(ImagePanelGraphic p: panels) {
			double units=100;
			setScaleBarSize(p, units);
			assertSimilarNumber(knownDistance, p.getScaleInfo().getPixelWidth());
			assertSimilarNumber(knownDistance,p.getDisplayScaleInfo().getPixelWidth()*p.getRelativeScale());
			assertSimilarNumber(knownDistance,p.getScaleBar().getScaleInfo().getPixelWidth()*p.getRelativeScale());
		}
		
		IssueLog.log("Passed consitency of all panels test. added scale bars to each panel");
		
		figure.updateDisplay();
		for(ImagePanelGraphic imagePanel: panels) {
			BarGraphic bar = imagePanel.getScaleBar();
			IssueLog.log("checking scale bar of length "+bar.unitlengthString()+" which is "+bar.getBounds().width+" points wide "+"  or  " +bar.getBarWidthBasedOnUnits());
			IssueLog.log("Bar has scale info "+bar.getScaleInfo());
			IssueLog.log("Expect bar size to be  "+bar.unitlengthString()+" divided by "+bar.getScaleInfo().getPixelWidth());
			
			IJ.wait(100);//waits a little
			assertSimilarNumber(bar.getScaleInfo().getPixelWidth(), imagePanel.getDisplayScaleInfo().getPixelWidth());
			double scaleBarKnownDistance=bar.getLengthInUnits()/bar.getBarWidthBasedOnUnits();
			
			assertSimilarNumber(knownDistance,scaleBarKnownDistance*imagePanel.getRelativeScale());
			
				}
		
		IssueLog.log("Passed Scale Bar size tests");
		IssueLog.log("proceeding with rescale of original image test");
		
		
		testEachPanelForConsistency(panels, knownDistance, 0.24);
		testEachPanelForConsistency(panels, knownDistance, 1);
		testEachPanelForConsistency(panels, knownDistance, 1.5);
		
		/**a set of tests to determine that the scale changes based on the bilinear scaling applied to the image*/
		
		double[] testRescale=new double[] {2, 0.5, 2.5, .25};
		for(double scaleTest: testRescale) {
			switchScale(figure, scaleTest);
		 
			/**if the orignial images are scaled by this amount, their new known distance will be*/
			double kd = knownDistance/scaleTest;
			
			testEachPanelForConsistency(panels, kd, 0.24);
			testEachPanelForConsistency(panels, kd, 1);
			testEachPanelForConsistency(panels, kd, 1.5);
			testEachPanelForConsistency(panels, kd, 0.24);
			
		}
		
			switchScale(figure, 1);
		
			
		
		
		
	}

	/**
	 * @param p
	 * @param units
	 */
	void setScaleBarSize(ImagePanelGraphic p, double units) {
		if (p.getScaleBar()==null) {
			new ImagePanelMenu(p).addScaleBar();
			assert(p.getScaleBar()!=null);
			
		}
		p.getScaleBar().setLengthInUnits(units);
	}

	/**the known distance given by the microscope software was placed in the filename
	 * Retries that known distance for tests
	 * @param fig1
	 * @return
	 */
	double getKnownDistancePerPixel(File fig1) {
		String[] name = fig1.getName().split(" ");
		double knownDistance = Double.parseDouble(name[0].trim());
		return knownDistance;
	}

	/**creates a figure from the given file
	 * @param fig1
	 * @return
	 */
	FigureOrganizingLayerPane createFigure(File fig1) {
		MultichannelDisplayLayer item = CurrentAppContext.getMultichannelContext().createMultichannelDisplay().creatMultiChannelDisplayFromUserSelectedImage(true, fig1.getAbsolutePath());
		item.getSlot().showImage();
		//opens a copy of that saved image. 
		
		PreProcessInformation pp = new PreProcessInformation(new Rectangle(400,400,500,500));
		
		FigureOrganizingLayerPane figure = new QuickFigureMaker().createFigureFromOpenImage(pp);
		IssueLog.log("Crated figure "+figure+" from image of pixel size "+item.getMultiChannelImage().getScaleInfo().getPixelWidth());
		return figure;
	}

	/**Checks each panel to make srue that the correct scaleinfo has been shared with the panel
	 * @param panels
	 * @param expectedPixelSize
	 * double relativeDisplayScale no changes in this number should have any affect on the panels
	 */
	void testEachPanelForConsistency(ArrayList<ImagePanelGraphic> panels, double expectedPixelSize, double relativeDisplayScale) {
		for(ImagePanelGraphic p: panels) {
			p.setRelativeScale(relativeDisplayScale);
			IssueLog.log("Comparing Scale information to ensure that the expected ratios are held");
			assertSimilarNumber(expectedPixelSize, p.getScaleInfo().getPixelWidth());
			assertSimilarNumber(expectedPixelSize,p.getDisplayScaleInfo().getPixelWidth()*p.getRelativeScale());
			assertSimilarNumber(expectedPixelSize,p.getScaleBar().getScaleInfo().getPixelWidth()*p.getRelativeScale());
		}
	}

	/**
	 * @param figure
	 * @param scaleTest
	 */
	void switchScale(FigureOrganizingLayerPane figure, double scaleTest) {
		figure.getPrincipalMultiChannel().setPreprocessScale( scaleTest);
		 figure.updatePanelsAndLabelsFromSource();
		 figure.updateDisplay();
	}

	/**Checks to determine if the numbers are the same.
	 * Due to multiple multiplication steps, the limited precision of doubles
	 * and rounding to integers
	 * one must consider numbers that are very close to be the same for the purposes of testing
	 * @param knownDistance
	 * @param pixWidth
	 * @return
	 */
	void assertSimilarNumber(double knownDistance, double pixWidth) {
		IssueLog.log("comparing "+knownDistance+"  to "+ pixWidth);
		assert (Math.abs(knownDistance-pixWidth)<=0.04);
	}
	
	/**since the pixel size displayed to user in software is rounded compared with that stored in the file
	 * exact equality is not expected. Manual calculations of expected scale bar size
	 * done were based on the rounded versions
	  to account for the rounding
	   this method simply checks the assertion that they numbers are within 10% of each other
	   rather than exact equality. The rounding step can cause changes to scale bar size of
	   that magnitude. For example, a rounding of  0.06 from 0.0645*/
	void assertSimilarInteger(double d1, double d2) {
		IssueLog.log("comparing rounded scale bar size "+d1+"  to "+ d2);
		assert (Math.abs(d1-d2)<=d1/10);
	}
	
	/**
	 * 
	 */
	public static void prepareForTest() {
		
		ImageDisplayTester.main(new String[] {});
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
	}

}
