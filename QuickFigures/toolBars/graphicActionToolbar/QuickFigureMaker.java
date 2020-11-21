package graphicActionToolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import addObjectMenus.ImageAndlayerAdder;
import appContext.CurrentAppContext;
import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import channelMerging.PreProcessInformation;
import figureFormat.AutoFigureGenerationOptions;
import figureFormat.FigureTemplate;
import graphicalObjects.FigureDisplayContainer;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasAutoResize;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import objectDialogs.PanelStackDisplayOptions;
import ultilInputOutput.FileChoiceUtil;

/**This class is used to create a new figure from either an open image or a saved on
  The QuickFigure Button on the toolbar*/
public class QuickFigureMaker extends DisplayActionTool {
	private static final String slowFigure = "Slow Figure";
	static String[] possibleCodes=new String[] {"C+",		 "C+T", 				"C+Z", 							"C+Z+T","Merge", 		"Merge+T"				,"Merge+Z"                 ,"Merge+Z+T"
			};
	String[] menuTextForCodes=new String[] {"Default", "Selected T Frame only"  , "Selected Z Slice only", "Single Panel Only"          };

	private String codeString="C+";
	
	public String getMenuTextForCode(String t) {
		if(t==null) return  menuTextForCodes[0];
		if(t.contains("+Z+T")) return  menuTextForCodes[3];
		if(t.contains("+T")) return  menuTextForCodes[1];
		if(t.contains("+Z")) return  menuTextForCodes[2];
		return  menuTextForCodes[0];
	}
	
	
	 LocalImageAdder  la=new LocalImageAdder();
	 
	 boolean mergeOnly=false;
	 public boolean hidesImage=true;
	 
	 AutoFigureGenerationOptions auto=new AutoFigureGenerationOptions();
	
	 void setupAdder() {
		 auto.autoGenerateFromModel=true;
		 auto.showPanelDialog=false;
		 la.autoFigureGenerationOptions=auto;
	 }
	 
	
	public QuickFigureMaker() {
		super("quickFig", "quickFigure.jpg");
		setupAdder() ;
	}
	
	public QuickFigureMaker(boolean mergeOnly) {
		super("quickFig", "quickFigure.jpg");
		this.mergeOnly=mergeOnly;
		codeString="Merge";
		setupAdder() ;
	}
	
	public QuickFigureMaker(String aC) {
		super("quickFig", "quickFigure.jpg");
		codeString=aC;
		setOptionsBasedOnCodeString(aC);
		setupAdder() ;
	}


	protected void perform(FigureDisplayContainer graphic) {
		createFigureFromOpenImage();
	}

	/**creates a figure from the multi channel image that the user currently has open
	 * @return */
	public FigureOrganizingLayerPane createFigureFromOpenImage() {
		FigureOrganizingLayerPane f = createFigure();
		if (f!=null && this.hidesImage)f.hideImages();
		return f;
	}

	/**when no arguments are given, attempt to create a figure for the active image*/
	private FigureOrganizingLayerPane createFigure() {
		return createFigure(null, null);
	}
	
	/**creates a new window with new figure. If path is set to null, uses the image that the user has open
	  otherwise, opens the file in the path*/
	public FigureOrganizingLayerPane createFigure(String path, PreProcessInformation p2) {
		ImageWindowAndDisplaySet diw = ImageWindowAndDisplaySet.createAndShowNew("New Image", 40, 30);
		
		if (path==null) la.openFile=false; else la.openFile=true;
	
		FigureOrganizingLayerPane added = la.add(diw.getImageAsWrapper().getGraphicLayerSet(), path, p2);
		
		if(added==null) {
			//if something goes wrong, closes the newly created window
			
			diw.getWindow().setVisible(false);
			IssueLog.showMessage("You need to have an image open to create a figure");
			return null;
			}
		diw.getTheSet().setTitle(added.getName());
		new CanvasAutoResize().performUndoableAction(diw);//resizes the canvas to fit the figure
		diw.autoZoom();
		ImageWindowAndDisplaySet.centreWindow(diw.getWindow());
		
			
		return added;
	}
	
	private class LocalImageAdder extends ImageAndlayerAdder   {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LocalImageAdder() {
			super(false);
		}

		
		/**Written so that if the path given is null, uses the active image
		  instead of opening a file chooser*/
		public MultichannelDisplayLayer createMultiChannel(String path) {
			if (path==null) return getMultiChannelOpener().creatMultiChannelDisplayFromUserSelectedImage(false, MultiChannelDisplayCreator.useActiveImage);
			//if(this.openFile) getMultiChannelCreator().creatMultiChannelDisplayFromUserSelectedImage(true, path);
			return super.createMultiChannel(path);
		
		}
		
		/**depending on the variable set. might need to transform the template into a merged image only version*/
		protected FigureTemplate getUsedTemplate(MultichannelDisplayLayer display) {
			if (!mergeOnly) return super.getUsedTemplate(display);
			FigureTemplate tp = super.getUsedTemplate(display);
			IssueLog.log("calling get template");
			tp.makeMergeOnly();
			return tp; 
		}
		
	}
	
	
	/**creates a popup menu that displays options*/
	@Override
	public ArrayList<JMenuItem> getPopupMenuItems() {
		ArrayList<JMenuItem> output = new  ArrayList<JMenuItem>();
		
		
		SmartJMenu sm = new SmartJMenu("Create Figure With Merge Only");
		SmartJMenu sm2 = new SmartJMenu("Create Figure With Split Channels");
		
		
		sm.add(addToMenu("Default",  "Merge"));
		sm.add(addToMenu("For Selected T Frame only",  "Merge+T"));
		sm.add(addToMenu("For Selected Z Slice only",  "Merge+Z"));
		sm.add(addToMenu("Single Panel Only",  "Merge+Z+T"));
		
		sm2.add(addToMenu("Default",  "C+"));
		sm2.add(addToMenu(slowFigure, slowFigure));
		sm2.add(addToMenu("Selected T Frame only",  "C+T"));
		sm2.add(addToMenu("Selected Z Slice only",  "C+Z"));
		sm2.add(addToMenu("Selected Slice and Frame only",  "C+Z+T"));
		
		output.add(sm);
		output.add(sm2);
		
	
		includeAddImageMenuItems(output);
		
		
		return output;
	}


	/**
	Menu items to add and image to the current figure. 
	Largely obsolete due to drag and drop feature
	 */
	protected void includeAddImageMenuItems(ArrayList<JMenuItem> output) {
		JMenuItem mi;
		mi = new JMenuItem("Add Image To Current");
		mi.setActionCommand("Add");
		mi.addActionListener(new QuickFigureMenuActionListener());
		output.add(mi);
		
		mi = new JMenuItem("Add Multiple Images To Current");
		mi.setActionCommand("Add2");
		mi.addActionListener(new QuickFigureMenuActionListener());
		output.add(mi);
	}


	public JMenuItem addToMenu(String text, String atext) {
		JMenuItem mi = new JMenuItem(text);
		mi.setActionCommand(atext);
		mi.addActionListener(new QuickFigureMenuActionListener());
		return mi;
	}
	
	/**executes the menu items that are associated with the quickfigure button*/
	class QuickFigureMenuActionListener implements ActionListener {

	
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			String aC = arg0.getActionCommand();
			codeString=aC;
			if (aC.contains("Merge")||aC.contains("C+")||aC.contains(slowFigure)) {
				setOptionsBasedOnCodeString(aC);
				FigureOrganizingLayerPane f = createFigureFromOpenImage();
				
				if (aC.contains(slowFigure)) {
					PanelStackDisplayOptions dialog = f. recreateFigurePanels(false);
					dialog.recropButton().showRecropDialog();
				}
			} 
			
			setOptionsBackToDefault();
			
			FigureOrganizingLayerPane sm = findFigureOrganizingLayer();
			if (aC.equals("Add")) {
				if (sm!=null) {	
					sm.nextMultiChannel(true);
					 setinformer.getCurrentlyActiveOne().getAsWrapper().updateDisplay();
				}
			}
			
			if (aC.equals("Add2")) {
				if (sm!=null) {	
					File[] files = FileChoiceUtil.getFiles();
					for(File f: files) {
						if (f==null||!f.exists()) continue;
						MultichannelDisplayLayer item = CurrentAppContext.getMultichannelContext().createMultichannelDisplay().creatMultiChannelDisplayFromUserSelectedImage(true, f.getAbsolutePath());
						sm.nextMultiChannel(item);
					}
					
					setinformer.getCurrentlyActiveDisplay().zoomOutToFitScreen();;
					setinformer.getCurrentlyActiveOne().getAsWrapper().updateDisplay();
				}
			}
			
		}


		
		}

	
	/**Changes the settings on this object based on a particular string*/
	public void setOptionsBasedOnCodeString(String aC) {
		if (aC.contains("Merge")) 
			mergeOnly=true;
		boolean singleSlice = aC.contains("+Z");
		setSingleSliceMode(singleSlice);
		boolean singleFrame = aC.contains("+T");
		setSingleFrameMode(singleFrame);
	}


	/**
	set to true if the current frame must be used rather than all time frames
	 */
	protected boolean setSingleFrameMode(boolean singleFrame) {
		return la.useSingleFrame=singleFrame;
	}


	/**
	 set to true if the current slice must be used rather than all z slices
	 */
	protected boolean setSingleSliceMode(boolean singleSlice) {
		return la.useSingleSlice=singleSlice;
	}
	
	public void setOptionsBackToDefault() {
		la.useSingleFrame=false;
		la.useSingleSlice=false;
		mergeOnly=false;
	}
	
	/**Attempts to find the figure organizing layer for the currently active figure.*/
	FigureOrganizingLayerPane findFigureOrganizingLayer() {
		GraphicLayer sm = setinformer.getCurrentlyActiveOne().getGraphicLayerSet().getSelectedContainer();
		while(!(sm instanceof FigureOrganizingLayerPane)&&sm!=null) {
			if (sm.getParentLayer()==null) break;
			sm=sm.getParentLayer();
		}
		if (!(sm instanceof FigureOrganizingLayerPane) )
				{	
				ArrayList<GraphicLayer> layers = sm.getSubLayers();
				for(GraphicLayer l: layers) {
					if (l instanceof FigureOrganizingLayerPane) {
						sm=l; break;
					}
				}
				}
		
		
		if (sm instanceof FigureOrganizingLayerPane) {
			return (FigureOrganizingLayerPane) sm;
		} else return null;
	}
	
	
	@Override
	public String getToolTip() {
			return "Quick Multichannel Figure";
		}
	
	/**returns an object that is used to add an item to the file menu
	  that functions the same way as the quickfigure button*/
	public BasicMenuItemForObj getMenuVersion() {
		return new QuickFigureFileMenuItem();
	}
	
	public static BasicMenuItemForObj[] getMenuBarItems() {
		BasicMenuItemForObj[] b=new BasicMenuItemForObj[possibleCodes.length];
		for(int i=0; i<b.length; i++ ) {
			b[i]=new QuickFigureMaker(possibleCodes[i]).getMenuVersion();
		}
		return b;
	}
	
	/**Adds a version of the quickfigure maker to the file menu*/
	class QuickFigureFileMenuItem extends BasicMenuItemForObj {

		@Override
		public String getNameText() {
			if (codeString!=null) {
				return getMenuTextForCode(codeString);
			}
			if (mergeOnly) return "Figure from open image (with Merge only)";
			return "Figure from open image with Split Channels";
		}

		/**determines what submenu contains the quick figure maker*/
		@Override
		public String getMenuPath() {
			String string = "File<New";
			if(mergeOnly) string+="<Figure With Merge Panels Only";
			else string+="<Figure With Split Channels";
			return string;
		}
		
		@Override
		public void performActionDisplayedImageWrapper(DisplayedImage diw) {
			 performLoadAction();

		}
		
	}
	
	

}
