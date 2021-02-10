package graphicActionToolbar;


import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import addObjectMenus.FigureAdder;
import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import appContextforIJ1.ImageDisplayTester;
import applicationAdapters.DisplayedImage;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.CSFLocation;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelListElement;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.HyperStackMaker;
import ij.plugin.Zoom;
import imageMenu.CanvasAutoResize;
import imageMenu.CanvasAutoTrim;
import logging.IssueLog;
import testing.FigureTester;
import ultilInputOutput.FileChoiceUtil;
import utilityClasses1.ArraySorter;

/***general test of the quickfigure maker. since the quickfigure maker applies many parts of quickfigures
   anything wrong will likely generate an exception. Alternatively, the expected figures might not appear*/
public class QuickFigureMakerTest {
	
	
	@Test
	void test() {
	
		prepareForTest();
		
		//testManyCombinations();
		
		
		
		FigureOrganizingLayerPane fig1 =new FigureTester(). createFigureFromExample1Images(new QuickFigureMaker(), 2);
		ArrayList<ZoomableGraphic> allGraphics = fig1.getAllGraphics();
		
		/**makes sure all of the objects were creates*/
		assert(allGraphics.size()==18);//makes sure all the objects were created
		assert(ArraySorter.getNOfClass(allGraphics, ImagePanelGraphic.class)==10);
		assert(ArraySorter.getNOfClass(allGraphics, ChannelLabelTextGraphic.class)==5);
		assert(ArraySorter.getNOfClass(allGraphics, BarGraphic.class)==1);
		ArraySorter.removeThoseNotOfClass(allGraphics, ChannelLabelTextGraphic.class);
		ArrayList<ZoomableGraphic> labels = allGraphics;
		allGraphics = fig1.getAllGraphics();
		
		/**The channel labels from the .zvi files metadata are known from the opened file
		  check their correctness*/
		 String[] expectedLabels = new String[] {"DAPI","eGFP", "Texasred", "Cy5", "Merge"};
		for(int i=0; i<expectedLabels.length; i++)
		{ 
			ChannelLabelTextGraphic c=(ChannelLabelTextGraphic) labels.get(i);
			assert(c.getParagraph().getText().contentEquals( expectedLabels[i]));
		}
		
		
		
		makeVisible() ;
		assert(FileChoiceUtil.yesOrNo("Passed automated testing. portion. A figure should have appeared. Do you see it?"));
		
		ImagePlus j = IJ.openImage(FigureTester.testFolderPath+1+"/Test 1 Split Channels.png");
		j.show();; 
		new Zoom().run("out");;	new Zoom().run("out");;
		IJ.wait(1000);
		assert(FileChoiceUtil.yesOrNo("Compare the figure produced to the expected result 'Test 1 Split Channels' in the 'Test 1' folder. Are they same?"));
	
		
		IJ.wait(1000);
		makeNonVisible() ;
		
		/**now tests the merge only version*/
		FigureOrganizingLayerPane fig2 = new FigureTester().createFigureFromExample1Images(new QuickFigureMaker(FigureAdder.MERGE_PANELS_ONLY, true), 4);
		fig2.getLayout().getEditor().repackagePanels(fig2.getLayout(), 1, 4);
		makeVisible();
		
		allGraphics = fig2.getAllGraphics();
		IJ.wait(1000);
		assert(FileChoiceUtil.yesOrNo("A figure with only merge panels should have appeared. Do you see it?"));
		
		
		IJ.wait(10000);
		
		
		assert(ArraySorter.getNOfClass(allGraphics, ImagePanelGraphic.class)==4);
		assert(ArraySorter.getNOfClass(allGraphics, ChannelLabelTextGraphic.class)==1);
		assert(ArraySorter.getNOfClass(allGraphics, BarGraphic.class)==1);
		assert(ArraySorter.getNOfClass(allGraphics, PanelLayoutGraphic.class)==1);
	}





	/**
	 * 
	 */
	public void testManyCombinations() {
		/**Test to make sure the quick figure maker can work regardless of channel slice or frame.
		  during this test there may be some concurrent modification exceptions. those only occur
		  during high speed automated tests and not during actual use. nested loops ensure that 
		  every possible setting is tried. IMPORTANT, TEST ASSUMES THAT CHANNEL USE OF THEDEFAULT
		  TEMPLATE IS SET TO INCLUDE ALL CHANNELS*/
		for( int c=1; c<3; c++) {
			for( int z=1; z<3; z++) {
				for( int t=1; t<3; t++) {
					for(boolean mergeOnly: new boolean[] {true, false}) {
							for(int selectedT=0; selectedT<=t; selectedT++) {
								for(int selectedZ=0; selectedZ<=z; selectedZ++) {
									testForHyperStack(c, z, t, mergeOnly, selectedT,selectedZ);
								}
						}
					
					}
				}	
			}
		}
	}





	/**
	 * 
	 */
	public static void prepareForTest() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		ImageDisplayTester.showInnitial();
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
	}





	/**
	tests a stack of spefic if dimensions to see if the a figure can be made from it with the correct number of panels.
	
	 */
	protected void testForHyperStack(int c, int z, int t, boolean mergeOnly, int selectedFrame, int selectedSlice ) {
		String string = "Test c="+c+"  z="+z+  "   t="+t+"  "+(mergeOnly?"Merge Only ":"Split Channels"+ " Selects Slice "+selectedSlice +"  Selects Frame "+selectedFrame);
		ImagePlus s = createAndShowExample(c, z, t, string);
		boolean doesSelect1Frame = selectedFrame>0;
		boolean doesSelect1Slice = selectedSlice>0;
		if(doesSelect1Frame) s.setT(selectedFrame);
		if (doesSelect1Slice) s.setZ(selectedSlice);
		QuickFigureMaker quickFigureMaker = new QuickFigureMaker(mergeOnly? FigureAdder.MERGE_PANELS_ONLY: FigureAdder.DEFAULT, true);
		quickFigureMaker.figureCreationOptions.ignoreSavedTemplate=true;
		if(doesSelect1Frame) quickFigureMaker.setSingleFrameMode(true);
		if (doesSelect1Slice)quickFigureMaker.setSingleSliceMode(true);
		FigureOrganizingLayerPane f = quickFigureMaker.createFigureFromOpenImage(null);
		IJ.wait(10);
		s.hide();
		
		/**are the correct number of image panels produced?*/
		//these are the exptected numbers
		int panelsFromChannel = (c>1&&!mergeOnly)?c+1:1;
		int panelsFromZSlices = doesSelect1Slice?1:z;
		int panelsFromTimeFrames = doesSelect1Frame?1:t;
		
		int expectedSize=panelsFromChannel
							*panelsFromZSlices
								*panelsFromTimeFrames;
		
		
		assert(f.getPrincipalMultiChannel().getPanelList().getPanels().size()==expectedSize);
		
		/**checks for the correct number of panels*/
		if (!doesSelect1Frame)for(int frame0=1; frame0<=t; frame0++)
			{
				 int nWithFrame = f.getPrincipalMultiChannel().getPanelList().getPanelsWith(CSFLocation.frameLocation(frame0)).size();
				 assert(nWithFrame==panelsFromZSlices*panelsFromChannel);
			}
		if (!doesSelect1Slice)for(int slice0=1; slice0<=z; slice0++)
		{
			 int nWithFrame = f.getPrincipalMultiChannel().getPanelList().getPanelsWith(CSFLocation.sliceLocation(slice0)).size();
			 assert(nWithFrame==panelsFromTimeFrames*panelsFromChannel);
		}
		
		/**makes sure the correct slice was selected*/
		if (doesSelect1Slice) {
			for(PanelListElement panel: f.getPrincipalMultiChannel().getPanelList().getPanels()) {
				assert(panel.targetSliceNumber==selectedSlice);
			}
		}
		if (doesSelect1Frame) {
			for(PanelListElement panel: f.getPrincipalMultiChannel().getPanelList().getPanels()) {
				assert(panel.targetFrameNumber==selectedFrame);
			}
		}
		
		makeNonVisible() ;
		IJ.wait(100);
	}





	/**
	 creates an example image
	 */
	public static ImagePlus createAndShowExample(int c, int z, int t, String string) {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		ImagePlus s = IJ.createHyperStack(string, 200, 150, c, z, t, 8);
		HyperStackMaker.labelHyperstack(s);
	
		s.show();
		return s;
	}
	
	/**returns a figure that is to be used by many other test methods*/
	public static FigureOrganizingLayerPane generateQuickFigure(int c, int z, int t) {
		createAndShowExample(c, z, t, "C= "+c+"  Z="+z+" T="+t);
		QuickFigureMaker quickFigureMaker = new QuickFigureMaker(FigureAdder.DEFAULT, true);
		
		FigureOrganizingLayerPane f = quickFigureMaker.createFigureFromOpenImage(null);
		return f;
	}





	/**
	 * 
	 */
	protected void makeVisible() {
		DisplayedImage currentlyActiveDisplay = new CurrentFigureSet().getCurrentlyActiveDisplay();
		currentlyActiveDisplay.setZoomLevel(1.5);
		new CanvasAutoResize(true).performActionDisplayedImageWrapper(currentlyActiveDisplay);
		new CanvasAutoTrim().performActionDisplayedImageWrapper(currentlyActiveDisplay);
	}

	
	/**
	 * 
	 */
	protected void makeNonVisible() {
		DisplayedImage currentlyActiveDisplay = new CurrentFigureSet().getCurrentlyActiveDisplay();
		currentlyActiveDisplay.closeWindowButKeepObjects();
		
	}
	

	
	
	

	


}
