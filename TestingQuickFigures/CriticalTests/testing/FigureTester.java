/**
 * Author: Greg Mazo
 * Date Modified: Mar 7, 2021
 * Version: 2021.1
 */
package testing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import actionToolbarItems.AlignItem;
import actionToolbarItems.EditScaleBars;
import actionToolbarItems.SetAngle;
import actionToolbarItems.SuperTextButton;
import addObjectMenus.FigureAdder;
import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import appContextforIJ1.ImageDisplayTester;
import applicationAdapters.DisplayedImage;
import channelLabels.ChannelLabelProperties;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.ChannelEntry;
import channelMerging.PreProcessInformation;
import figureFormat.DirectoryHandler;
import figureOrganizer.FigureLabelOrganizer.RowLabelTextGraphic;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelListElement;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import genericMontageLayoutToolKit.FitLayout;
import graphicActionToolbar.CurrentFigureSet;
import graphicActionToolbar.QuickFigureMaker;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import ij.IJ;
import ij.ImagePlus;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasAutoResize;
import imageMenu.ZoomFit;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import messages.ShowMessage;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import multiChannelFigureUI.InsetTool;
import textObjectProperties.TextLine;
import utilityClasses1.ArraySorter;

/**main method from this class creates a figure from a set of saved images
 * figures appear immediately and use can visually confirm 
 * that the figures are created.
 * User can manually test any of the features
 * 
 *  
 * */
public class FigureTester {
	
	
	
	
	public static final String testFolderPath = new DirectoryHandler().getFigureFolderPath()+"/Testing/Test ";

	Rectangle[] cropRectsForExample1=new Rectangle[] {new Rectangle(280, 200, 300,250),
								new Rectangle(630, 600, 300,250),
								new Rectangle(300, 200, 300,250), 
								new Rectangle(300, 200, 300,250),
								new Rectangle(380, 390, 300,250)};
	public boolean ignoreTemplate;
	
	/**
	 * @return
	 */
	public static String getTest1ImagePath(int group, int image) {
		
		String string = testFolderPath+group+"/"+image+".tif";
		if (!(new File(string)).exists()) IssueLog.showMessage("One must place the testing files in the QuickFigures folder to perform this test");;
		return string;
	}
	
	/**
	checks the subfolders for Test image group3 which consists of many small images
	 */
	public static File[] getTest3ImagePaths( int subfoldIndex) {
		
		String string = testFolderPath+3+"/"+subfoldIndex+"/";
		File dir=new File(string);
		
		
		File[] matches = dir.listFiles();
		if (!(new File(string)).exists()) IssueLog.showMessage("One must place the testing files in the QuickFigures folder to perform this test");;
		
		for(File f: matches) {IssueLog.log("file used will be "+f.getAbsolutePath());}
		if (matches.length==0)IssueLog.log("was unable to obtain images");
		
		
		return matches;
	}
	
	/**
	 * @return
	 */
	public static String[] getScaleTestPaths() {
		String string = getFolderForScaleBarTests();
		
		if (!(new File(string)).exists()) IssueLog.showMessage("One must place the testing files in the QuickFigures folder to perform this test "+string);;		
		File f = new File(string);
		return f.list();
		
	}

	/**
	 * @return
	 */
	static String getFolderForScaleBarTests() {
		return testFolderPath+"Scale Bars";
	}
	
	
	
	
	/**
	creates a figure for testing. figure 1 is a simple split channel figure with 4 channels plus merge
	It includes two source images so has two rows and 5 columns for a total of 10 images.
	 */
	public FigureOrganizingLayerPane createFigureFromExample1Images(QuickFigureMaker qm, int nImages) {
		PreProcessInformation p1 = new PreProcessInformation(cropRectsForExample1[0]);
		FigureOrganizingLayerPane figure = qm.createFigure(FigureTester.getTest1ImagePath(1,1), p1);
		for(int imageIndex=2; imageIndex<= nImages; imageIndex++) {
			PreProcessInformation p2 = new PreProcessInformation(cropRectsForExample1[imageIndex-1]);
			figure.nextMultiChannel(FigureTester.getTest1ImagePath(1,imageIndex), p2);
		}
		figure.updateDisplay();
		
		return figure;
		
	}
	
	
	
	/**
	creates a figure for testing. Test 3 are groups of tiny split channel images
	with control and RNAi 
	 */
	public FigureOrganizingLayerPane createFigureFromExample3Images(ImageWindowAndDisplaySet diw, QuickFigureMaker qm, int nIndex, Point2D displace) {
		this.ignoreTemplate=true;
		qm.figureCreationOptions.ignoreSavedTemplate=true;
		File[] files = FigureTester.getTest3ImagePaths(nIndex);
		File control=null;
		File siRNA=null;
		for(File f: files) {
			if(f.getName().toLowerCase().contains("control"))
				control=f;
			else siRNA=f;
		}
		
		PreProcessInformation p1 = new PreProcessInformation(null, 0, 10);
		FigureOrganizingLayerPane figure = qm.createFigure(diw, control.getAbsolutePath(), p1);
		
		
		
		figure.getPrincipalMultiChannel().getSlot().applyCropAndScale(p1);
		figure.getMontageLayoutGraphic().resizeLayoutToFitContents();
		
		changeChannelLabe(figure);
			
		figure.nextMultiChannel(siRNA.getAbsolutePath(), p1);
		figure.getMontageLayoutGraphic().resizeLayoutToFitContents();
		
		/**removes the 4th channel*/
	
		new ChannelPanelEditingMenu(figure, 4).setChannelExcludedFromFigure(4, true, true, false);
		
		figure.updateDisplay();
		figure.addLabelsBasedOnImageNames(BasicLayout.ROWS);
		figure.fixLabelSpaces();
		if(displace!=null) {
			figure.getMontageLayoutGraphic().generateCurrentImageWrapper();
			figure.getMontageLayoutGraphic().changeLayoutLocation(new Point((int)displace.getX(), (int)displace.getY()));
		
		}
		
		
		return figure;
		
	}

	/**
	 * @param figure
	 */
	public void changeChannelLabe(FigureOrganizingLayerPane figure) {
		/**changes the channel labels*/
		
		ArrayList<ChannelLabelTextGraphic> allLabels = figure.getPrincipalMultiChannel().getChannelLabelManager().getAllLabels();
		for(int i=0; i<allLabels.size(); i++)
				{
			ChannelLabelTextGraphic l= allLabels.get(i);
			if(l.isThisMergeLabel())
				continue;
			l.changeText("Gene "+(i+1));
			}
	}
	
	/**returns the standard version of example 1*/
	public FigureOrganizingLayerPane createFigureFromExample1AImages() {return createFigureFromExample1Images(example1FigureMaker(), 2);}

	/**returns a version of example 1 with merge panels only*/
	public FigureOrganizingLayerPane createFigureFromExample1BImages() {return createFigureFromExample1Images(example1BFigureMaker(), 4);}

	/**returns the versionof example 1 with inset panels*/
	public FigureOrganizingLayerPane createFigureFromExample1CImages() {
		FigureOrganizingLayerPane createFigureFromExample1Images = createFigureFromExample1Images(example1BFigureMaker(), 1);
		 DisplayedImage image1 = CurrentFigureSet.getCurrentActiveDisplayGroup();
		PanelListElement panel = createFigureFromExample1Images.getAllPanelLists().getMergePanel();
		InsetTool tool = new InsetTool();
		
		PanelGraphicInsetDefiner inset1 = tool.createInsetOnImagePanel( image1.getImageAsWrapper(), panel.getImageDisplayObject(), new Rectangle(20,25, 12,10));
	
		PanelGraphicInsetDefiner inset2 = tool.createInsetOnImagePanel(image1.getImageAsWrapper(),panel.getImageDisplayObject(), new Rectangle(42,47, 15,10));
		inset2.setAngle(-Math.PI/12);inset2.updateImagePanels();
		
		new CanvasAutoResize(true).performUndoableAction(image1);
		panel.getImageDisplayObject().getScaleBar().getAttachmentPosition().setLocationTypeInternal(RectangleEdges.LOWER_LEFT);
		
		AttachmentPosition a=null;
		for(ChannelLabelTextGraphic c: inset1.getPanelManager().getPanelList().getChannelLabels()) {
			if (a!=null) c.setAttachmentPosition(a); else a=c.getAttachmentPosition();
			c.getAttachmentPosition().setLocationCategory(AttachmentPosition.EXTERNAL);
			c.getAttachmentPosition().setLocationTypeExternal(RectangleEdges.ABOVE_AT_MIDDLE);
			c.getAttachmentPosition().setHorizontalOffset(0);
		}
		return createFigureFromExample1Images;
		}

	
	/**
	creates a figure maker that generated split channel figures
	 */
	private QuickFigureMaker example1FigureMaker() {
		QuickFigureMaker out = figureMaker();
		out.setMergeOrSplit(FigureAdder.SPLIT_CHANNELS_ONLY);
		return out;
	}

	/**
	 returns the quickfigure maker that will be used to consturct a figure
	 for consistency, it has been set to ignore the saved template.
	 During testing that variable 
	 */
	public QuickFigureMaker figureMaker() {
		QuickFigureMaker quickFigureMaker = new QuickFigureMaker();
		quickFigureMaker.figureCreationOptions.ignoreSavedTemplate=ignoreTemplate;
		return quickFigureMaker;
	}
	
	/**
	creates a figure maker that generated split channel figures
	 */
	private QuickFigureMaker example1BFigureMaker() {
		QuickFigureMaker out = figureMaker();
		out.setMergeOrSplit(FigureAdder.MERGE_PANELS_ONLY);
		return out;
	}
	
	/**
	creates a figure for testing. figure 1 is a simple split channel figure with 4 channels plus merge
	It includes two source images so has two rows and 5 columns for a total of 10 images.
	 */
	public FigureOrganizingLayerPane createFigureFromScaleExample(QuickFigureMaker qm, int whichImage) {
		File path = getScaleExample(whichImage);
		if (path.exists()) {
			return qm.createFigure(path.getAbsolutePath(), null);
		}
		ShowMessage.showOptionalMessage("file not found", true, path.toString());
		return null;
		
	}

	/**
	 * @param whichImage
	 * @return
	 */
	public File getScaleExample(int whichImage) {
		String[] paths = getScaleTestPaths();
		
		String path =getFolderForScaleBarTests()+"/"+ paths[whichImage];
		return new File( path);
	}
	
	public static ImagePlus openExample1(int i) {
		return IJ.openImage(FigureTester.getTest1ImagePath(1,i));
	}
	
	
	public static void main(String[] args) {
		
		FigureOrganizingLayerPane.suppressCropDialog=true;
		setup();
		FigureTester figureTester = new FigureTester();
		
		 figureTester.ignoreTemplate=true;
		showExamples(figureTester);
		 
		 /**figureTester.ignoreTemplate=false;
		 showExamples(figureTester);
		 */
	}

	/**Shows several split channel 
	 * @param figureTester
	 */
	public static void showExamples(FigureTester figureTester) {
		figureTester. createFigureFromExample1AImages();
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
		 figureTester. createFigureFromExample1BImages();
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
		 figureTester. createFigureFromExample1CImages();
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
		 figureTester. createFromExample3Images(false);
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
		 figureTester. createFromExample3Images(true);
		 CurrentFigureSet .updateActiveDisplayGroup();
	}

	/**
	 * @param figureTester
	 */
	public void createFromExample3Images(boolean diversidy) {
		ImageWindowAndDisplaySet diw = ImageWindowAndDisplaySet.createAndShowNew("New Image", 40, 30);
			
		int space=200;
		
		 Point2D[] points=new Point2D[] {new Point(50,0),  new Point(500,0), 
				 						new Point(50,space), new Point(500, space), 
				 						new Point(50,2*space), new Point(500, 2*space)};
		ArrayList<FigureOrganizingLayerPane> list=new ArrayList<FigureOrganizingLayerPane>();
		 
		 for(int i=0; i<6; i++) {
			 list.add(
			 createFigureFromExample3Images(diw, new FigureTester().example1FigureMaker(), i+1, points[i])
		 		);
		 }
		 
		 new ZoomFit(ZoomFit.SCREEN_FIT).performActionDisplayedImageWrapper(diw);;
		
		ArrayList<LocatedObject2D> listObuects = diw.getTheSet().getLocatedObjects();
		ArraySorter.removeThoseNotOfClass(listObuects,DefaultLayoutGraphic.class);
		 new FitLayout(FitLayout.ALIGN_GRID).alignObjects(listObuects);;
		 
		 if(diversidy)
			 diversifyFigures(diw, listObuects, list);
			
		 
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
	}

	/**Makes edits to the objects within the figure such that no 
	 * two figures within the group will be the same
	 * @param diw
	 * @param listObuects
	 * @param list 
	 */
	public void diversifyFigures(ImageWindowAndDisplaySet diw, ArrayList<LocatedObject2D> listObuects, ArrayList<FigureOrganizingLayerPane> list) {
		int i=0;
		 
		 for(LocatedObject2D layout: listObuects) {
			 if(layout instanceof DefaultLayoutGraphic) {
				 DefaultLayoutGraphic dl=(DefaultLayoutGraphic) layout;
				 dl.generateCurrentImageWrapper();
				 dl.getEditor().setHorizontalBorder(dl.getPanelLayout(), 12+2*i);
				 dl.getEditor().setVerticalBorder(dl.getPanelLayout(), 12-2*i);
			
			 }
		 }
		 
		 /**changes the color modes*/
		 for (int j=0; j<list.size(); j++) {
			 if(j%2==0)
			 new ChannelPanelEditingMenu(list.get(j), 1).changeColorModes();
		 }
		 
		 ArrayList<LocatedObject2D> listObuects2 = diw.getTheSet().getLocatedObjects();
			ArraySorter.removeThoseNotOfClass(listObuects2,BarGraphic.class);
		 TestShapes.diversify(listObuects2, EditScaleBars.getProjectionList());
		 TestShapes.diversify(listObuects2,EditScaleBars.getUnitLengthList("um", EditScaleBars.shortBarLengths));
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
		 
		 listObuects2 = diw.getTheSet().getLocatedObjects();
			ArraySorter.removeThoseNotOfClass(listObuects2,ChannelLabelTextGraphic.class);
		 diversifyText(listObuects2);
		 
		 listObuects2 = diw.getTheSet().getLocatedObjects();
			ArraySorter.removeThoseNotOfClass(listObuects2,RowLabelTextGraphic.class);
		 diversifyText(listObuects2);
	}

	/**
	 * @param listObuects2
	 */
	public void diversifyText(ArrayList<LocatedObject2D> listObuects2) {
		TestShapes.diversify(listObuects2, SetAngle.createManyAnglesVeryLimited());
		 TestShapes.diversify(listObuects2, SuperTextButton.getForDims(null));
		 TestShapes.diversify(listObuects2, SuperTextButton.getForFonts(new TextGraphic()));
		 TestShapes.diversify(listObuects2, SuperTextButton.getForFontSizes());
	}

	/**
	 * 
	 */
	public static void setup() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		ImageDisplayTester.startToolbars(true);
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		IssueLog.startChecking=true;
	}
	
	public static TestProvider[] getTests() {
		return new TestProvider[] {new FigureProvider(TestExample.SPLIT_CHANNEL_FIGURE), new FigureProvider(TestExample.MERGE_PANEL_FIGURE),
				new FigureProvider(TestExample.FIGURE_WITH_INSETS), new FigureProvider(TestExample.MANY_SPLIT_CHANNEL)};
	}
	
	/**A test provider to return figures. used by other classes*/
	public static class FigureProvider extends TestProvider {
		
		
		
		TestExample form=TestExample.SPLIT_CHANNEL_FIGURE;
		
		public FigureProvider() {}
		public FigureProvider(TestExample type) {
			this.form=type;
			super.parameter1=type;
		}
	
		
		public DisplayedImage createExample() {
			CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
			if (form==TestExample.SPLIT_CHANNEL_FIGURE)
			new FigureTester(). createFigureFromExample1AImages();
			if (form==TestExample.MERGE_PANEL_FIGURE)
				new FigureTester(). createFigureFromExample1BImages();
			if (form==TestExample.FIGURE_WITH_INSETS)
				new FigureTester(). createFigureFromExample1CImages();
			if (form==TestExample.MANY_SPLIT_CHANNEL)
				new FigureTester().createFromExample3Images(false);
			if (form==TestExample.MANY_SPLIT_CHANNEL_SCRAMBLE)
				new FigureTester().createFromExample3Images(true);
			
			return  CurrentFigureSet.getCurrentActiveDisplayGroup();
		}
	}
	
	
	public static void closeAllWindows() {
		Window[] windows = Window.getWindows();
		for(Window w: windows) {
			w.setVisible(false);
		}
	}
}
