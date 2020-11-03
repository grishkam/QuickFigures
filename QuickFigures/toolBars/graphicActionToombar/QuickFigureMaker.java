package graphicActionToombar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import addObjectMenus.ImageAndlayerAdder;
import appContext.CurrentAppContext;
import applicationAdapters.DisplayedImageWrapper;
import basicMenusForApp.BasicMenuItemForObj;
import figureTemplates.AutoFigureGenerationOptions;
import figureTemplates.FigureTemplate;
import graphicalObjects.GraphicSetDisplayContainer;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.ImageAndDisplaySet;
import imageMenu.CanvasAutoResize;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import objectDialogs.PanelStackDisplayOptions;
import ultilInputOutput.FileChoiceUtil;

public class QuickFigureMaker extends DisplayActionTool {
	private static final String slowFigure = "Slow Figure";
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
		setupAdder() ;
		// TODO Auto-generated constructor stub
	}
	
	protected void perform(GraphicSetDisplayContainer graphic) {
		
		FigureOrganizingLayerPane f = createFigure();
		if (f!=null && this.hidesImage)f.hideImages();
		
	}

	private FigureOrganizingLayerPane createFigure() {
		return createFigure(null);
	}
	public FigureOrganizingLayerPane createFigure(String path) {
		ImageAndDisplaySet diw = ImageAndDisplaySet.createAndShowNew("New Image", 40, 30);
		if (path==null) la.openFile=false; else la.openFile=true;
	
		FigureOrganizingLayerPane added = la.add(diw.getImageAsWrapper().getGraphicLayerSet(), path);
		if(added==null) {
			//IssueLog.showMessage("No tmage was found, no figure created");
			diw.getWindow().setVisible(false);
			return null;
			}
		diw.getTheSet().setTitle(added.getName());
		new CanvasAutoResize().performUndoableAction(diw);
		diw.autoZoom();
		ImageAndDisplaySet.centreWindow(diw.getWindow());
		
		//Hides the image
			
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
		
		JMenuItem mi7 = new JMenuItem(slowFigure);
		mi7.setActionCommand(slowFigure);
		mi7.addActionListener(new actionLis());
		output.add(mi7);
		
		JMenuItem mi = new JMenuItem("Create Merge only");
		mi.setActionCommand("Merge");
		mi.addActionListener(new actionLis());
		output.add(mi);
		
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
	
	
	class actionLis implements ActionListener {

		

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getActionCommand().equals("Merge")) {
				mergeOnly=true;
				performLoadAction() ;
				mergeOnly=false;
			}
			
			if (arg0.getActionCommand().equals(slowFigure)) {
				MultichannelDisplayLayer m = CurrentAppContext.getMultichannelContext().getMultichannelOpener().creatMultiChannelDisplayFromOpenImage();
				//m.getSlot().showCropDialog(null);
				FigureOrganizingLayerPane f = createFigure();
				PanelStackDisplayOptions dialog = f. recreateFigurePanels(false);
				dialog.recropButton().showRecropDialog();
				
			}
			
			FigureOrganizingLayerPane sm = findFigureOrganizingLayer();
			if (arg0.getActionCommand().equals("Add")) {
				if (sm!=null) {	
					sm.nextMultiChannel(true);
					 setinformer.getCurrentlyActiveOne().getAsWrapper().updateDisplay();
				}
			}
			
			if (arg0.getActionCommand().equals("Add2")) {
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
			
		}}

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
		return new fileMenuVersion();
	}
	
	class fileMenuVersion extends BasicMenuItemForObj {

		@Override
		public String getNameText() {
			if (mergeOnly) return "Figure from open image (with Merge only)";
			return "Figure from open image with split panels";
		}

		@Override
		public String getMenuPath() {
			return "File<New";
		}
		
		@Override
		public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
			 performLoadAction();

		}
		
	}
	
	

}
