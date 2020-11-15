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
import figureFormat.AutoFigureGenerationOptions;
import figureFormat.FigureTemplate;
import graphicalObjects.GraphicSetDisplayContainer;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.ImageAndDisplaySet;
import imageMenu.CanvasAutoResize;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import objectDialogs.PanelStackDisplayOptions;
import ultilInputOutput.FileChoiceUtil;

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
	
	
	 localAdder  la=new localAdder();
	 
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
		// TODO Auto-generated constructor stub
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


	protected void perform(GraphicSetDisplayContainer graphic) {
		createFigureFromOpenImage();
	}

	/**creates a figure from the multi channel image that the user currently has open
	 * @return */
	public FigureOrganizingLayerPane createFigureFromOpenImage() {
		FigureOrganizingLayerPane f = createFigure();
		if (f!=null && this.hidesImage)f.hideImages();
		return f;
	}

	private FigureOrganizingLayerPane createFigure() {
		return createFigure(null);
	}
	
	/**creates a figure. If path is set to null, uses the image that the user has open
	  otherwise, opens the file in the path*/
	public FigureOrganizingLayerPane createFigure(String path) {
		ImageAndDisplaySet diw = ImageAndDisplaySet.createAndShowNew("New Image", 40, 30);
		if (path==null) la.openFile=false; else la.openFile=true;
	
		FigureOrganizingLayerPane added = la.add(diw.getImageAsWrapper().getGraphicLayerSet(), path);
		
		if(added==null) {
			//if something goes wrong, closes the newly created window
			
			diw.getWindow().setVisible(false);
			IssueLog.showMessage("You need to have an image open to create a figure");
			return null;
			}
		diw.getTheSet().setTitle(added.getName());
		new CanvasAutoResize().performUndoableAction(diw);//resizes the canvas to fit the figure
		diw.autoZoom();
		ImageAndDisplaySet.centreWindow(diw.getWindow());
		
			
		return added;
	}
	
	private class localAdder extends ImageAndlayerAdder   {
		
		public localAdder() {
			super(false);
			// TODO Auto-generated constructor stub
		}

		
		public MultichannelDisplayLayer createMultiChannel(String path) {
			if (path==null) return getMultiChannelCreator().creatMultiChannelDisplayFromUserSelectedImage(false, MultiChannelDisplayCreator.useActiveImage);
			//if(this.openFile) getMultiChannelCreator().creatMultiChannelDisplayFromUserSelectedImage(true, path);
			return super.createMultiChannel(path);
		
		}
		
		protected FigureTemplate getUsedTemplate(MultichannelDisplayLayer display) {
			if (!mergeOnly) return super.getUsedTemplate(display);
			FigureTemplate tp = super.getUsedTemplate(display);
			tp.makeMergeOnly();
			return tp; 
		}
		
	}
	
	
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
		
	
		JMenuItem mi;
		mi = new JMenuItem("Add Image To Current");
		mi.setActionCommand("Add");
		mi.addActionListener(new actionLis());
		output.add(mi);
		
		mi = new JMenuItem("Add Multiple Images To Current");
		mi.setActionCommand("Add2");
		mi.addActionListener(new actionLis());
		output.add(mi);
		
		
		return output;
	}


	public JMenuItem addToMenu(String text, String atext) {
		JMenuItem mi = new JMenuItem(text);
		
		mi.setActionCommand(atext);
		mi.addActionListener(new actionLis());
		return mi;
	}
	
	
	class actionLis implements ActionListener {

		

		


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

	
	
	public void setOptionsBasedOnCodeString(String aC) {
		if (aC.contains("Merge")) 
			mergeOnly=true;
		la.useSingleSlice=aC.contains("+Z");
		la.useSingleFrame=aC.contains("+T");
	}
	
	public void setOptionsBackToDefault() {
		la.useSingleFrame=false;
		la.useSingleSlice=false;
		mergeOnly=false;
	}
	
	/**Attempts to find the organizing layer for the current image*/
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
	
	class QuickFigureFileMenuItem extends BasicMenuItemForObj {

		@Override
		public String getNameText() {
			if (codeString!=null) {
				return getMenuTextForCode(codeString);
			}
			if (mergeOnly) return "Figure from open image (with Merge only)";
			return "Figure from open image with Split Channels";
		}

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
