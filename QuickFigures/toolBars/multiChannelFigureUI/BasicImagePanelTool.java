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
 * Date Modified: Jan 6, 2021
 * Version: 2022.1
 */
package multiChannelFigureUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import channelMerging.MultiChannelDisplayWrapper;
import channelMerging.MultiChannelImage;
import channelMerging.ImageDisplayLayer;
import externalToolBar.DragAndDropHandler;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelListElement;
import figureOrganizer.PanelManager;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import genericTools.BasicToolBit;
import genericTools.NormalToolDragHandler;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.LocatedObject2D;
import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;
import utilityClasses1.ArraySorter;
import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.ImageWorkSheet;
import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelTextGraphic;

/**Tools that can work with channels of clicked image panels extend this class. As the panels are independant objects */
public class BasicImagePanelTool extends BasicToolBit implements ActionListener {

	public static final int ALL_IMAGES_IN_FIGURE=1, SELECTED_IMAGE_ONLY=0;
	int workOn= ALL_IMAGES_IN_FIGURE;

	protected MultichannelDisplayLayer presseddisplay;
	protected PanelListElement stackSlicePressed;
	protected PanelListElement stackSliceReleased;
	boolean clickingOnMultiMode=false;
	private int clickingOnMultiModeChan1;
	private int clickingOnMultiModeChan2;
	private MultiChannelDisplayWrapper m;
	boolean updateInsets=true;
	protected PanelGraphicInsetDefiner pressedInset;
	
	public void mousePressed() {
		ImageWorkSheet impw = getImageClicked();
		
		/**Switches it another mode if the display being clicked is a multichannel itself*/
		if (this.getImageDisplayWrapperClick() instanceof MultiChannelDisplayWrapper) {
			 clickingOnMultiMode=true;
			 m = ( MultiChannelDisplayWrapper) getImageDisplayWrapperClick();
			 clickingOnMultiModeChan1=m.getCurrentChannel();
			return;
		} else clickingOnMultiMode=false;
		
		
		
		stackSlicePressed=getElementAtPoint(impw, this.getClickedCordinateX(), this.getClickedCordinateY());
		
		
		CanvasMouseEvent me = super.getLastMouseEvent();
		Object source = super.getLastMouseEvent().getSource();
		if (me.isPopupTrigger()||me.altKeyDown()) {
			showthePopup((Component)source, me.getClickedXScreen(), me.getClickedYScreen()) ;
		}
		else
			afterMousePress(this.getPressedWrapper(), this.getPressChannelOfMultichannel());
	}
	
	
	/**If called after a mouse press on a multichannel. Does not get called when a popup menu is triggered*/
	protected void afterMousePress(MultiChannelImage mw, int chan1) {
		
	}
	
	
	protected void showthePopup(Component source, int x, int y) {
		// TODO Auto-generated method stub
		
	}



	public void mouseReleased() {
		
		ImageWorkSheet impw = getImageClicked();
		if (this.clickingOnMultiMode&&this.getImageDisplayWrapperClick() instanceof MultiChannelDisplayWrapper) {
			 MultiChannelDisplayWrapper m=( MultiChannelDisplayWrapper) getImageDisplayWrapperClick() ;
			clickingOnMultiModeChan2=m.getCurrentChannel();
			applyReleaseActionToMultiChannel(m.getContainedMultiChannel());//applyReleaseActionToMultiChannel((MultiChannelWrapper) this.getImageWrapperClick());
			impw.updateDisplay();return;
		}
		
		
		stackSliceReleased=getElementAtPoint(impw, this.getReleaseCordinateX(), this.getReleaseCordinateY());

	if (arePressAndreleaseValid() )
		for(MultiChannelImage wr:  getAllWrappers() ) {
			applyReleaseActionToMultiChannel(wr);
		}
	
	updateAllDisplays();
	}
	
	public void updateAllDisplaysWithRealChannel(String realName) {
		if (realName==null||realName.trim().equals("")) this.updateAllDisplays();
		else {
			for(ImageDisplayLayer pd: getAllDisplays() ) {
			pd.updateOnlyPanelsWithChannel(realName);
			if (updateInsets) {
				if (this.updateInsets) updateInsetPanels(pd, realName);
			}
			
			}
			
			
		}	
	
	}
	
	/**updates the panels*/
	void updateAllDisplays() {
		for(ImageDisplayLayer pd: getAllDisplays() ) {
			if(pd==null) continue;
			if (this.updateInsets) updateInsetPanels(pd, null);
			pd.updatePanels();
		}
		if(presseddisplay!=null)
		presseddisplay.updatePanelsAndLabelsFromSource();//.updatePanels();//.updateMontageFromSource();
		
	}
	
	void updateAllAfterMenuAction() {
		if(presseddisplay!=null)presseddisplay.updatePanels();//.getMultichanalWrapper().updateDisplay();
		if (workOn==ALL_IMAGES_IN_FIGURE) {
			for(ImageDisplayLayer d: getAllDisplays()) {
				d.updatePanels();
			}
		}
		this.getImageClicked().updateDisplay();
	}
	
	public ArrayList<ImageDisplayLayer> getAllDisplays() {
		ArrayList<ImageDisplayLayer> output = new ArrayList<ImageDisplayLayer>();
		if (presseddisplay==null) return output;
		output.add(presseddisplay);
		if (getCurrentOrganizer()!=null&&this.workOn==ALL_IMAGES_IN_FIGURE) {
			output = new ArrayList<ImageDisplayLayer>();
			output.addAll( getCurrentOrganizer().getMultiChannelDisplays());
		}
		
		return output;
		
	}
	
	public FigureOrganizingLayerPane getCurrentOrganizer() {
		if (presseddisplay.getParentLayer() instanceof FigureOrganizingLayerPane) {
			return (FigureOrganizingLayerPane) presseddisplay.getParentLayer();
		}
		return null;
	}
	
	/**called to update the inset panels*/
	private void updateInsetPanels(ImageDisplayLayer pd, String name) {
		ArrayList<PanelGraphicInsetDefiner> insets = getAllInsets(pd);
		for(PanelGraphicInsetDefiner ins: insets) {
			if(name==null) {
				ins.getPanelManager().updatePanels();
				
			} else
			ins.getPanelManager().updatePanelsWithChannel(name);
		}
	}
	
	
	static PanelGraphicInsetDefiner findInsetWith(ImageDisplayLayer pd, ImagePanelGraphic image) {
		ArrayList<PanelGraphicInsetDefiner> insets = getAllInsets(pd);
		for(PanelGraphicInsetDefiner in: insets) {
			if(in.getPanelManager().getPanelList().getPanelGraphics().contains(image)) return in;
			
		}
		return null;
		
	}
	
	static ArrayList<PanelGraphicInsetDefiner> getAllInsets(ImageDisplayLayer pd) {
		ArrayList<PanelGraphicInsetDefiner> out =new ArrayList<PanelGraphicInsetDefiner>();
		if (pd instanceof GraphicLayer) {
			 GraphicLayer gl=(GraphicLayer) pd;
			 ArrayList<ZoomableGraphic> items = gl.getAllGraphics();
			 for(ZoomableGraphic i : items) {
				 if (i instanceof PanelGraphicInsetDefiner) {
					 PanelGraphicInsetDefiner i2=(PanelGraphicInsetDefiner) i;
					out.add(i2);
				 } 
			 }
		 }
		return out;
		
	}
	
	
	
	
	
	/**If the multichannel display is not in a figure organized layer pane,
	  this returns an array with just a single multichannel Display.
	  Otherwise returns all the multichannel displays that are directly in the figure organizing
	  pane*/
	public ArrayList<MultiChannelImage> getAllWrappers() {
		ArrayList<MultiChannelImage> output=new ArrayList<MultiChannelImage>();
		output.addAll(presseddisplay.getAllSourceImages());
		if (presseddisplay.getParentLayer() instanceof FigureOrganizingLayerPane &&this.workOn==ALL_IMAGES_IN_FIGURE) {
			FigureOrganizingLayerPane pane=(FigureOrganizingLayerPane) presseddisplay.getParentLayer();
			return pane.getAllSourceImages();
		}
		return output;
	}
	
	/**called after a mouse is released*/
	public void applyReleaseActionToMultiChannel(MultiChannelImage mw) {
		int chan1=getBestMatchToChannel(mw, getRealNameOfPressedChannel(), getPressChannelOfMultichannel());
		int chan2=getBestMatchToChannel(mw, getRealNameOfReleaseChannel(), getReleaseChannelOfMultichannel());	
		applyReleaseActionToMultiChannel(mw, chan1, chan2);
	}
	public void applyReleaseActionToMultiChannel(MultiChannelImage mw, int chan1, int chan2) {
		mw.getChannelSwapper().swapChannelLuts(chan1, chan2);
		
	}
	/**Tries to find the channel index of the channel names realChanName, if it cant, it just returns the chanNum*/
	int getBestMatchToChannel(MultiChannelImage mw, String realChanName, int chanNum) {
		int chan1=mw.getIndexOfChannel(realChanName);
		if (chan1<1||chan1>mw.nChannels()) 
			chan1=chanNum;
		
		return chan1;
	}
	
	
	
	
	protected String getRealNameOfPressedChannel() {
		return getPressedWrapper().getRealChannelName(getPressChannelOfMultichannel());
	}
	
	protected String getRealNameOfReleaseChannel() {
		return getPressedWrapper().getRealChannelName(getReleaseChannelOfMultichannel());
	}
	
	public int getPressChannelOfMultichannel() {
		if(this.clickingOnMultiMode) return this.clickingOnMultiModeChan1;
		if (stackSlicePressed==null) return 0;
		return stackSlicePressed.targetChannelNumber;
	}
	
	public int getReleaseChannelOfMultichannel() {
		if(this.clickingOnMultiMode) return this.clickingOnMultiModeChan2;
		return stackSliceReleased.targetChannelNumber;
	}
	
	boolean arePressAndreleaseValid() {
		if (stackSliceReleased==null) return false;
		if (stackSlicePressed==null) return false;
		return true;
	}
	
	/***/
	public PanelListElement getElementAtPoint(ImageWorkSheet impw, int x, int y) {
		LocatedObject2D pressPointObject = getObjectAt(impw, x, y);
		ImagePanelGraphic imagepanel = (ImagePanelGraphic )pressPointObject;
		PanelListElement output=null;
		
		/**In the even that the user has clicked a channel label*/
		if (imagepanel==null) {
			ChannelLabelTextGraphic label = this.getClickedLabelObject(impw, x, y);
			if(label!=null)
			imagepanel= label.getPanel().getPanelGraphic();
		}
		
		
		MultichannelDisplayLayer pd = MultichannelDisplayLayer.findMultiChannelForGraphic(impw.getTopLevelLayer(),imagepanel );
	
		
		
		if (pd!=null) {
	
			presseddisplay=pd;
			boolean direct=presseddisplay.getPanelList().getPanelGraphics().contains(imagepanel);
			
			if (!direct) {
				pressedInset = findInsetWith(presseddisplay, imagepanel);
			} else pressedInset = null;
			
			output= pd.getPanelWithDisplay(imagepanel);
		}
		
		return output;
		
		
	
	}
	
	
	
	public MultiChannelImage getPressedWrapper() {
		if (clickingOnMultiMode) return m.getContainedMultiChannel();
		if (presseddisplay==null) {return null;}
		return presseddisplay.getMultiChannelImage();
	}
	
	
	/**returns the clicked object*/
	public LocatedObject2D getObjectAt(ImageWorkSheet click, int x, int y) {
		ArrayList<LocatedObject2D> therois = getObjecthandler().getAllClickedRoi(click, x, y,ImagePanelGraphic.class);
		ArraySorter.removeHiddenItemsFrom(therois);
		return new ArraySorter<LocatedObject2D>().getFirstNonNull(therois);
		
	}
	
	/**returns the clicked object*/
	public ChannelLabelTextGraphic getClickedLabelObject(ImageWorkSheet click, int x, int y) {
		ArrayList<LocatedObject2D> therois = getObjecthandler().getAllClickedRoi(click, x, y,ChannelLabelTextGraphic.class);
		ArraySorter.removeHiddenItemsFrom(therois);
		if (therois.size()==0) return null;
		return (ChannelLabelTextGraphic) new ArraySorter<LocatedObject2D>().getFirstNonNull(therois);
		
	}
	
	protected PanelManager getPressedPanelManager() {
		
		PanelManager output = presseddisplay.getPanelManager();
		if (this.pressedInset!=null) {
			output =pressedInset.getPanelManager();
		}
		
		return output;
	}
	
	protected ChannelLabelManager getPressedChannelLabelManager() {
	
		ChannelLabelManager lm=presseddisplay.getChannelLabelManager();
		if (this.pressedInset!=null) {
			lm=pressedInset.getChannelLabelManager();
		}
	return lm;
	}
	
	public void showOptionsDialog() {
		new SwapperOptionDialog(this).showDialog();
	}
	
	public class SwapperOptionDialog extends StandardDialog {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BasicImagePanelTool mover;
		
		public SwapperOptionDialog(BasicImagePanelTool mover) {
			setModal(true);
			this.mover=mover;
			add("eWorkOn", new ChoiceInputPanel("Select what to work on", new String[] {"On Multichannel Image of Clicked Only", "On all in same figure"}, workOn));
			}
		@Override
		public void onOK() {
			mover.workOn=this.getChoiceIndex("eWorkOn");
	
		}
		
		
	}
	
	public void addButtonToMenu(Container pop, String text, String actionCommand) {
		JMenuItem renamer = new JMenuItem(text);
		renamer.setActionCommand(actionCommand);
		renamer.addActionListener(this);
		pop.add(renamer);
		
	}



	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getToolName() {
			
			return this.getToolTip();
		}
	public DragAndDropHandler getDragAndDropHandler() {
		return new NormalToolDragHandler(this);
	}
}
