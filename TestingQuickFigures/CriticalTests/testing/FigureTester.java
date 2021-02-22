package testing;

import java.awt.Rectangle;
import java.io.File;

import addObjectMenus.FigureAdder;
import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import appContextforIJ1.ImageDisplayTester;
import applicationAdapters.DisplayedImage;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.PreProcessInformation;
import figureFormat.DirectoryHandler;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelListElement;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicActionToolbar.CurrentFigureSet;
import graphicActionToolbar.QuickFigureMaker;
import ij.IJ;
import ij.ImagePlus;
import imageMenu.CanvasAutoResize;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import messages.ShowMessage;
import multiChannelFigureUI.InsetTool;

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
	public static String getTestImagePath(int group, int image) {
		
		String string = testFolderPath+group+"/"+image+".tif";
		if (!(new File(string)).exists()) IssueLog.showMessage("One must place the testing files in the QuickFigures folder to perform this test");;
		return string;
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
		FigureOrganizingLayerPane figure = qm.createFigure(FigureTester.getTestImagePath(1,1), p1);
		for(int imageIndex=2; imageIndex<= nImages; imageIndex++) {
			PreProcessInformation p2 = new PreProcessInformation(cropRectsForExample1[imageIndex-1]);
			figure.nextMultiChannel(FigureTester.getTestImagePath(1,imageIndex), p2);
		}
		figure.updateDisplay();
		
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
		return IJ.openImage(FigureTester.getTestImagePath(1,i));
	}
	
	
	public static void main(String[] args) {
		
		
		setup();
		FigureTester figureTester = new FigureTester();
		
		 figureTester.ignoreTemplate=true;
		showExamples(figureTester);
		 
		 figureTester.ignoreTemplate=false;
		 showExamples(figureTester);
		 
	}

	/**
	 * @param figureTester
	 */
	public static void showExamples(FigureTester figureTester) {
		figureTester. createFigureFromExample1AImages();
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
		 figureTester. createFigureFromExample1BImages();
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
		 figureTester. createFigureFromExample1CImages();
		 CurrentFigureSet .updateActiveDisplayGroup();
		 
		
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
		return new TestProvider[] {new FigureProvider(FigureProvider.form1), new FigureProvider(FigureProvider.form1b),new FigureProvider(FigureProvider.form1c)};
	}
	
	/**A test provider to return figures. used by other classes*/
	public static class FigureProvider extends TestProvider {
		
		
		
		static final int form1=0, form1b=1, form1c=2;
		int form=form1;
		
		public FigureProvider() {}
		public FigureProvider(int type) {
			this.form=type;
		}
	
		
		public DisplayedImage createExample() {
			CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
			if (form==form1)
			new FigureTester(). createFigureFromExample1AImages();
			if (form==form1b)
				new FigureTester(). createFigureFromExample1BImages();
			if (form==form1c)
				new FigureTester(). createFigureFromExample1CImages();
			return  CurrentFigureSet.getCurrentActiveDisplayGroup();
		}
	}
}
