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
import java.util.ArrayList;

import actionToolbarItems.ChannelLabelButton;
import actionToolbarItems.EditScaleBars;
import actionToolbarItems.SetAngle;
import actionToolbarItems.SetLayoutProperty;
import actionToolbarItems.SuperTextButton;
import addObjectMenus.FigureAdder;
import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import appContextforIJ1.ImageDisplayTester;
import applicationAdapters.DisplayedImage;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.MultiChannelImage;
import channelMerging.PreProcessInformation;
import figureFormat.DirectoryHandler;
import figureFormat.TemplateChoice;
import figureOrganizer.FigureLabelOrganizer.RowLabelTextGraphic;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelListElement;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import genericMontageLayoutToolKit.FitLayout;
import graphicActionToolbar.CurrentFigureSet;
import graphicActionToolbar.QuickFigureMaker;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import ij.IJ;
import ij.ImagePlus;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasAutoResize;
import imageMenu.ZoomFit;
import layout.basicFigure.BasicLayout;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import messages.ShowMessage;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import multiChannelFigureUI.InsetTool;
import popupMenusForComplexObjects.ImagePanelMenu;
import undo.CombinedEdit;
import utilityClasses1.ArraySorter;

/**main method from this class creates a figure from a set of saved images
 * figures appear immediately and use can visually confirm 
 * that the figures are created.
 * User can manually test any of the features
 * 
 *  
 * */
public class FigureTester {
	
	
	
	
	/**
	 * 
	 */
	private static final String  FILE_NOT_FOUND = "One must place the testing files in the QuickFigures folder to perform this test. they are on github";

	public static final String testFolderPath = new DirectoryHandler().getFigureFolderPath()+"/Testing/Test ";
	public static final String mockFilePath=testFolderPath+"Mock/";
	
	
	/**lists the rectangles that will be used for a sequence of example images*/
	Rectangle[] cropRectsForExample1=new Rectangle[] {new Rectangle(280, 200, 300,250),
								new Rectangle(630, 600, 300,250),
								new Rectangle(300, 200, 300,250), 
								new Rectangle(300, 200, 300,250),
								new Rectangle(380, 390, 300,250)};
	public static boolean ignoreTemplate;
	
	/**
	 * @return
	 */
	public static String getTest1ImagePath(int group, int image) {
		
		String string = testFolderPath+group+"/"+image+".tif";
		checkForFile(string);
		
		
		return string;
	}

	/**
	 * @param string
	 */
	protected static void checkForFile(String string) {
		if (!(new File(string)).exists()) 
		{
			IssueLog.showMessage(FILE_NOT_FOUND);;
			
		}
	}
	
	/**
	checks the subfolders for Test image group 3 which consists of many small images
	 */
	public static File[] getTest3ImagePaths( int subfolderIndex) {
		
		String string = testFolderPath+3+"/"+subfolderIndex+"/";
		File dir=new File(string);
		
		
		File[] matches = dir.listFiles();
		if (!(new File(string)).exists()) IssueLog.showMessage(FILE_NOT_FOUND);;
		
		
		if (matches.length==0)IssueLog.log("was unable to obtain images");
		
		
		return matches;
	}
	
	/**
	 * @return
	 */
	public static String[] getScaleTestPaths() {
		String string = getFolderForScaleBarTests();
		
		if (!(new File(string)).exists())
			IssueLog.showMessage(FILE_NOT_FOUND+string);;		
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
	creates a figure for testing. figure 1 is a simple split channel figure with 3 channels plus merge
	It includes two source images so has two rows and 4 columns for a total of 8 images.
	 */
	public FigureOrganizingLayerPane createFigureFromMockImages() {
		QuickFigureMaker example1FigureMaker = example1FigureMaker();
		createMock(1, true);
		PreProcessInformation p=null;
		
		return createMock(example1FigureMaker, p);
		
	}

	/**creates a mock figure 
	 * @param figureMaker
	 * @param process
	 * @return
	 */
	protected FigureOrganizingLayerPane createMock(QuickFigureMaker figureMaker, PreProcessInformation process) {
		FigureOrganizingLayerPane figure = figureMaker.createFigure(getMockFilePath(1), process);
		createMock(2, true);
		figure.nextMultiChannel(this.getMockFilePath(2), process);
		figure.addLabelsBasedOnImageNames(BasicLayout.ROWS);
		TemplateChoice.changeChannelLabels(figure, "Gene ");
		
		figure.updateDisplay();
		
		return figure;
	}
	
	

	/**
	 * @param mockIndex
	 */
	protected void createMock(int mockIndex, boolean close) {
		MultiChannelImage ex = CurrentAppContext.getMultichannelContext().getDemoExample(!close, getMockFilePath(mockIndex), 3, mockIndex, 1);
	
	}

	/**returns the path for saving the mock images as files
	 * @param mockIndex
	 * @return
	 */
	protected String getMockFilePath(int mockIndex) {
		return mockFilePath+"Row "+mockIndex+".tiff";
	}
	
	
	
	
	/**
	creates a figure for testing. Test 3 are groups of tiny split channel images
	with control and RNAi 
	 */
	public FigureOrganizingLayerPane createFigureFromExample3Images(ImageWindowAndDisplaySet diw, QuickFigureMaker qm, int nIndex, Point2D displace) {
		FigureTester.ignoreTemplate=true;
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
		
		TemplateChoice.changeChannelLabels(figure, "Gene ");
			
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
		
		PanelGraphicInsetDefiner inset1 = tool.createInsetOnImagePanel( image1.getImageAsWorksheet(), panel.getImageDisplayObject(), new Rectangle(20,25, 12,10));
	
		PanelGraphicInsetDefiner inset2 = tool.createInsetOnImagePanel(image1.getImageAsWorksheet(),panel.getImageDisplayObject(), new Rectangle(42,47, 15,10));
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
	protected QuickFigureMaker example1BFigureMaker() {
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
		
		 FigureTester.ignoreTemplate=false;
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
		 
		 figureTester. createFromExample3Images(TestExample.MANY_SPLIT_CHANNEL);
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
		 figureTester. createFromExample3Images(TestExample.MANY_SPLIT_CHANNEL_SCRAMBLE);
		 CurrentFigureSet .updateActiveDisplayGroup();
	}

	/**
	 * @param figureTester
	 */
	public void createFromExample3Images(TestExample manySplitChannelScramble) {
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
		 
		 
		 if(manySplitChannelScramble==TestExample.MANY_SPLIT_CHANNEL_SCRAMBLE)
			 diversifyFigures(diw, listObuects, list, true);
		 
		 if(manySplitChannelScramble==TestExample.MANY_SPLIT_CHANNEL_SCRAMBLE_LIGHT)
			 diversifyFigures(diw, listObuects, list, false);
		 
		 new ZoomFit(ZoomFit.SCREEN_FIT).performActionDisplayedImageWrapper(diw);;
		 
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
	}

	/**Makes edits to the objects within the figure such that no 
	 * two figures within the group will be the same
	 * @param diw
	 * @param listObuects
	 * @param list 
	 */
	public void diversifyFigures(ImageWindowAndDisplaySet diw, ArrayList<LocatedObject2D> listObuects, ArrayList<FigureOrganizingLayerPane> list, boolean angles) {
		
		ArrayList<LocatedObject2D> listObuects0 = diw.getTheSet().getLocatedObjects();
		ArraySorter.removeThoseNotOfClass(listObuects0,DefaultLayoutGraphic.class);
		 TestShapes.diversify(listObuects0, SetLayoutProperty.createManyBorders());
		
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
		 diversifyText(listObuects2, angles);
		 TestShapes.diversify(listObuects2, ChannelLabelButton.getAllMergeLabelFroms());
		 
		 listObuects2 = diw.getTheSet().getLocatedObjects();
			ArraySorter.removeThoseNotOfClass(listObuects2,RowLabelTextGraphic.class);
		 diversifyText(listObuects2, angles);
	}

	/**
	 * @param listObuects2
	 */
	public void diversifyText(ArrayList<LocatedObject2D> listObuects2, boolean angles) {
		if (angles)TestShapes.diversify(listObuects2, SetAngle.createManyAnglesVeryLimited());
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
		ignoreTemplate=true;
		return new TestProvider[] {new FigureProvider(TestExample._FIGURE), new FigureProvider(TestExample.SPLIT_CHANNEL_FIGURE), new FigureProvider(TestExample.MERGE_PANEL_FIGURE),
				new FigureProvider(TestExample.FIGURE_WITH_INSETS), new FigureProvider(TestExample.MANY_SPLIT_CHANNEL), new FigureProvider(TestExample.MANY_SPLIT_CHANNEL_SCRAMBLE), new FigureProvider(TestExample.MANY_SIZE_IMAGEPANEL),  new FigureProvider(TestExample.SCALE_BAR_STYLES_)};
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
			
			
			if (form==TestExample.MANY_SIZE_IMAGEPANEL)
				new FigureTester().createManySizeExample(1.5, false);
			if (form==TestExample.SCALE_BAR_STYLES_)
				new FigureTester().createManySizeExample(1, true);
			if (form==TestExample.SPLIT_CHANNEL_FIGURE)
				new FigureTester(). createFigureFromExample1AImages();
			if (form==TestExample.MERGE_PANEL_FIGURE)
				new FigureTester(). createFigureFromExample1BImages();
			if (form==TestExample.FIGURE_WITH_INSETS)
				new FigureTester(). createFigureFromExample1CImages();
			if (form==TestExample.MANY_SPLIT_CHANNEL)
				new FigureTester().createFromExample3Images(TestExample.MANY_SPLIT_CHANNEL);
			if (form==TestExample.MANY_SPLIT_CHANNEL_SCRAMBLE)
				new FigureTester().createFromExample3Images(TestExample.MANY_SPLIT_CHANNEL_SCRAMBLE);
			if (form==TestExample._FIGURE)
				new FigureTester().createFigureFromMockImages();
			
			DisplayedImage currentActiveDisplayGroup = CurrentFigureSet.getCurrentActiveDisplayGroup();
			currentActiveDisplayGroup.getImageAsWorksheet().setTitle(form.name());
		
			new CanvasAutoResize(true).performUndoableAction(currentActiveDisplayGroup);
			return  currentActiveDisplayGroup;
		}
	}
	
	
	/**creates an example with a series of image panels showing the same image
	 * @param factor
	 * @return */
	public FigureOrganizingLayerPane createManySizeExample(double factor, boolean barProjectionVaries) {
		FigureOrganizingLayerPane out = createFigureFromExample1Images(example1BFigureMaker(), 1);
		
		ImagePanelGraphic image = out.getAllPanelLists().getMergePanel().getImageDisplayObject();
		
		BarGraphic scaleBar = image.getScaleBar();
		
		if(scaleBar==null) {
			scaleBar=ImagePanelMenu.createScaleBar(new CombinedEdit(), image);
		}
		
		for(int i=1;i<=3; i++)	{
			ImagePanelGraphic image2 = image.copy();
			
			BarGraphic bar2 = scaleBar.copy();
			
			out.add(image2);
			out.add(bar2);
			image2.moveLocation(i*(10+image.getObjectWidth()*Math.pow(factor, i-1)), 0);
			image2.addLockedItem(bar2);
			image2.setRelativeScale(image2.getRelativeScale()*Math.pow(factor, i));
			if(barProjectionVaries) {
				bar2.setProjectionType(i-1);
			}
			
			//image=image2;
			}
		
		return out;

	}
	
	
	public static void closeAllWindows() {
		Window[] windows = Window.getWindows();
		for(Window w: windows) {
			w.setVisible(false);
		}
	}
}
