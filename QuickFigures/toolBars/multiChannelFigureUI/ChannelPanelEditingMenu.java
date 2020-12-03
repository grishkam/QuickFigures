/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package multiChannelFigureUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import channelMerging.ChannelEntry;
import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelImage;
import channelMerging.ImageDisplayLayer;
import genericMontageKit.PanelListElement;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner;
import graphicalObjects_FigureSpecific.PanelManager;
import graphicalObjects_LayerTypes.GraphicLayer;
import iconGraphicalObjects.ColorModeIcon;
import iconGraphicalObjects.IconUtil;
import includedToolbars.StatusPanel;
import logging.IssueLog;
import objectDialogs.PanelStackDisplayOptions;
import panelGUI.PanelListDisplayGUI;
import sUnsortedDialogs.ScaleSettingDialog;
import specialMenus.ColorJMenu;
import standardDialog.ColorInputEvent;
import standardDialog.ColorInputListener;
import standardDialog.DialogItemChangeEvent;
import standardDialog.SwingDialogListener;
import undo.AbstractUndoableEdit2;
import undo.ChannelDisplayUndo;
import undo.ChannelUseChangeUndo;
import undo.CombinedEdit;
import undo.EditListener;
import applicationAdapters.HasScaleInfo;
import channelLabels.ChannelLabelManager;


/**Generates menu items and executes operations related to channel color, channel display range and other properties
  same set of options will appear in different contexts, as submenus withing different menus or outside of menus*/
public class ChannelPanelEditingMenu implements ActionListener, DisplayRangeChangeListener, SwingDialogListener {
	
	
	public static int ALL_IMAGES_IN_FIGURE=1, CLICKED_IMAGES_ONLY=0;
	/***/
	FigureOrganizingLayerPane givenOrganizer=null;//the targetted figure organizing layer
	protected MultichannelDisplayLayer presseddisplay;//the primary target of the actions and options
	public ArrayList<MultiChannelImage> extraWrappers=null;//in some contexts, additional items that are not directly clicked on are included
	public ArrayList<MultichannelDisplayLayer> extraDisplays=null;
	
	boolean updateInsets=true;
	protected PanelGraphicInsetDefiner pressedInset;
	
	protected PanelListElement stackSlicePressed;//the panel that is being targetted 
	protected int chanNum=0;//selected channel number. in some contexts there will not be a selected channel
	private ChannelEntry entryPress;
	Color colorForColorModeIcon=Color.red;
	
	public int workOn=ALL_IMAGES_IN_FIGURE;
	int swapMode=0;
	
	

	static final String scalingCommand="Scale", colorModeCommand="ColorMode",chanUseCommand="Channel Use";
	static final String minMaxCommand="MinMax",WLCommand="WinLev";
	private static final String orderCommand="order and luts", orderCommand2="Min, Max, order and luts";
	private static final String panContentCommand="Panel Content Gui",colorRecolorCommand="Fix Colors";
	private static final String renameChanCommand="Add Channel exposures to summary", channelNameCommand="rename channels";
	
	/**constructor called within figure organizing menu*/
	public ChannelPanelEditingMenu(FigureOrganizingLayerPane given, int chanN) {
		this.givenOrganizer=given;
		chanNum=chanN;
		presseddisplay=(MultichannelDisplayLayer) given.getPrincipalMultiChannel();
		if (presseddisplay!=null) try {
			colorForColorModeIcon=presseddisplay.getMultiChannelImage().getChannelColor(1);
		} catch (Throwable t) {}
	}

	/**constructor called in context of an image panel*/
	public ChannelPanelEditingMenu(ImagePanelGraphic ipg, ChannelEntry e) {
		this(ipg);
		this.entryPress=e;
		colorForColorModeIcon=e.getColor();
	}
	
	/**constructor called for adding this menu to a multichannel display layer's popup*/
	public ChannelPanelEditingMenu(MultichannelDisplayLayer pd, ImagePanelGraphic ipg) {
		
		presseddisplay=pd;
		if(ipg==null) return;
		stackSlicePressed= ipg.getSourcePanel();
		chanNum=stackSlicePressed.targetChannelNumber;
		setColorToStackSlice();
	}

	/**Sets up the color that will be used for certain icons in the menu*/
	public void setColorToStackSlice() {
		try {
			colorForColorModeIcon=stackSlicePressed.getChannelEntries().get(0).getColor();
		} catch (Exception e) {
		}
	}
	
	
	/**Constructor that is called for the image panel's popup menu*/
	public ChannelPanelEditingMenu(ImagePanelGraphic ipg) {
		if(ipg==null) return;// this is sometimes called when there is no image
		MultichannelDisplayLayer pd = MultichannelDisplayLayer.findMultiChannelForGraphic(ipg.getParentLayer(), ipg );
		
		
		ImagePanelGraphic imagepanel = ipg;
		
		pressedInset = PanelGraphicInsetDefiner.findInsetWith(ipg);
		if (pd==null &&pressedInset!=null) {
			pd=pressedInset.getSourceDisplay();
		}
		
		
		if (pd!=null) {
	
			presseddisplay=pd;
			
			stackSlicePressed= pd.getPanelWithDisplay(imagepanel);
			chanNum=stackSlicePressed.targetChannelNumber;
		}
		setColorToStackSlice();
	}
	
	



	public void addBasicWL2(Container output) {
		addButtonToMenu(output, "Window/Level", WLCommand);
		 addButtonToMenu(output, "Min/Max", minMaxCommand);
		
	}
	
	/**Adds the menu items to the given container*/
	public void addChannelRelevantMenuItems(Container output) {
		addChannelRelevantMenuItems(output, false);
	}
	public void addChannelRelevantMenuItems(Container output, boolean limitVersionOfMenu) {
		addButtonToMenu(output, "Window/Level", WLCommand, IconUtil.createBrightnessIcon(0));
		 addButtonToMenu(output, "Min/Max", minMaxCommand, IconUtil.createBrightnessIcon(0));
		 addButtonToMenu(output, "Change Color Modes", colorModeCommand, new ColorModeIcon(colorForColorModeIcon));
		 addButtonToMenu(output, "Channel Use Options", chanUseCommand);
		
			 try {
				addColorMenus("Recolor", output);
			} catch (Throwable e) {
				IssueLog.logT(e);
			}
			
		 
		 
		 addButtonToMenu(output, "Recolor Channels Automatically", colorRecolorCommand);
	
		 
		 if(!limitVersionOfMenu) {
		 JMenu chanLabelMenu=new JMenu("Channel Label");
		 addButtonToMenu(chanLabelMenu, "Edit Channel Label", channelNameCommand);
		 addButtonToMenu(chanLabelMenu, "Reset Channel Label Names ", renameChanCommand);
		// output.add(chanLabelMenu);
		 }
		 if(!limitVersionOfMenu) 
			 addButtonToMenu(output, "Match Min, Max, Channel Order and LUT Colors", orderCommand2);
			
	}



	
	/**Adds menus that are specific to each channel entry*/
	private void addColorMenus(String string, Container j) {
		ArrayList<ChannelEntry> list=getChannelEntryList();
		
		if(list!=null)
			addChenEntryColorMenus(j, list);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		CombinedEdit undo = null ;
		
		if (arg0.getActionCommand().equals(minMaxCommand)) {
			undo=showDisplayRangeDialog(WindowLevelDialog.MIN_MAX);
			
		}
		if (arg0.getActionCommand().equals(WLCommand)) {
			
			undo=showDisplayRangeDialog(WindowLevelDialog.WINDOW_LEVEL);
			
		}
		if(undo!=null) undo.establishFinalState();
		if (arg0.getActionCommand().equals(scalingCommand)) {
			 showScaleSettingDialog() ;
		}
		

		
		
		
		if (arg0.getActionCommand().equals(colorModeCommand)) {
			undo= changeColorModes();
		}
		
if (	arg0.getActionCommand().equals(colorRecolorCommand)) {
			undo=recolorBasedOnRealChannelNames();
		}

if (arg0.getActionCommand().equals(channelNameCommand)) {
	ChannelLabelManager lm=getPressedChannelLabelManager();
	
	if (stackSlicePressed.isTheMerge()) {
		lm.showChannelLabelPropDialog();
		
	} else
	lm.nameChannels(getChannelEntryList());
	
		 
}


if (	arg0.getActionCommand().equals(renameChanCommand)) {
	presseddisplay.getMultiChannelImage().renameBasedOnRealChannelName();
	this.updateAllDisplays();
}
		
		
		if (arg0.getActionCommand().equals(chanUseCommand)) {
			
			
			
			if (pressedInset!=null) {new PanelStackDisplayOptions(presseddisplay,pressedInset.getPanelManager().getPanelList(),pressedInset.getPanelManager(), false).showDialog();;}
			else
			if (workOn==CLICKED_IMAGES_ONLY) {
				if (pressedInset==null)
				presseddisplay.showStackOptionsDialog();
				
				
			} else {
				
				PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(presseddisplay,presseddisplay.getPanelList(),null, false);
				
				/**adds a list of all the channel displays that are relevant*/
				ArrayList<ImageDisplayLayer> all = getAllDisplays();
				all.remove(presseddisplay);
				dialog.addAditionalDisplays(all);
				
				dialog.showDialog();
				
				
			}
		}
		
		
	
		
		if (arg0.getActionCommand().equals(orderCommand2)) {
			workOn=ALL_IMAGES_IN_FIGURE;
			new ChannelOrderAndLutMatching().matchChannels(this.getPressedMultichannel(), this.getAllWrappers(), 2);
			for(int c=1; c<=this.getPressedMultichannel().nChannels(); c++) {
				minMaxSet(c, getPressedMultichannel().getChannelMin(c),getPressedMultichannel().getChannelMax(c));
			}
		}
		
		if (arg0.getActionCommand().equals(panContentCommand)) {
			PanelListDisplayGUI distpla = new PanelListDisplayGUI(  getPressedPanelManager(), this.getPressedChannelLabelManager());
			//getPressedPanelManager().getStack().setChannelUpdateMode(true);
			distpla.setVisible(true);
		}
	
		
		 updateAllAfterMenuAction();
		if(undo!=null) { new CurrentFigureSet().addUndo(undo);
							}
	}

	protected CombinedEdit recolorBasedOnRealChannelNames() {
		ArrayList<MultiChannelImage> all = this.getAllWrappers();
		CombinedEdit undo = ChannelDisplayUndo.createMany(all, this, ChannelDisplayUndo.COLOR_TYPE);
		
		for(MultiChannelImage p: all)
			p.colorBasedOnRealChannelName();
		this.updateAllDisplays();
		undo.establishFinalState();
		return undo;
	}

	protected CombinedEdit changeColorModes() {
		ChannelUseInstructions ins = presseddisplay.getPanelList().getChannelUseInstructions();
		if (this.pressedInset!=null) {
			 ins =pressedInset.getPanelManager().getPanelList().getChannelUseInstructions();
			
		}
		CombinedEdit undo=new CombinedEdit();
		undo.addEditListener(new AfterUndoChannel());
		
		undo.addEditToList(new ChannelUseChangeUndo(ins));
		
		int value = ins.channelColorMode;
		if (value==1) {value=0;} else {value=1;}
		ins.channelColorMode=value;
		
		
		
		if (workOn==ALL_IMAGES_IN_FIGURE && pressedInset==null) for(ImageDisplayLayer d: getAllDisplays()) {
			undo.addEditToList(new ChannelUseChangeUndo(d));
			d.getPanelList().getChannelUseInstructions().channelColorMode=value;
		} 
		if(extraDisplays!=null&&pressedInset==null)	for(ImageDisplayLayer d:this.extraDisplays) {
			undo.addEditToList(new ChannelUseChangeUndo(d));
			d.getPanelList().getChannelUseInstructions().channelColorMode=value;//this part might be redudent
		}
		undo.addEditToList(new AfterUndoChannel());
		undo.establishFinalState();
		this.updateAllDisplays();
		
		return undo;
				
	}

	protected CombinedEdit showDisplayRangeDialog(int type) {
		WindowLevelDialog.showWLDialogs(getChannelEntryList(),  getPrincipalMultiChannel(), this, type , ChannelDisplayUndo.createMany(getAllWrappers(), this));
		return ChannelDisplayUndo.createMany(getAllWrappers(), this);
	}

	


	public MultiChannelImage getPrincipalMultiChannel() {
		return this.presseddisplay.getMultiChannelImage();
	}
	
	public MultichannelDisplayLayer getPrincipalDisplay() {
		return presseddisplay;
	}


	public ArrayList<ChannelEntry> getChannelEntryList() {
		if(this.entryPress!=null) {
			ArrayList<ChannelEntry> out=new ArrayList<ChannelEntry>();
			out.add(entryPress);
			return out;
		}
		if (stackSlicePressed==null && presseddisplay!=null) return  presseddisplay.getMultiChannelImage().getChannelEntriesInOrder();  
		return this.stackSlicePressed.getChannelEntries();
	}
	
	void updateAllAfterMenuAction() {
		if(presseddisplay!=null)presseddisplay.updatePanels();//.getMultichanalWrapper().updateDisplay();
		if (workOn==ALL_IMAGES_IN_FIGURE) {
			for(ImageDisplayLayer d: getAllDisplays()) {
				d.updatePanels();
			}
		}
		presseddisplay.updateDisplay();
	}

	/***/
	public FigureOrganizingLayerPane getCurrentOrganizer() {
		if (givenOrganizer!=null) return givenOrganizer;
		if (presseddisplay.getParentLayer() instanceof FigureOrganizingLayerPane) {
			return (FigureOrganizingLayerPane) presseddisplay.getParentLayer();
		}
		return null;
	}
	
	

	/**sets the display range for a given channel. To account for
	 * images that share the same channel name despite different orders,
	  this first obtains a channel name for the primary target.
	  will first look to see if channels in the images
	 * can be identified by name. if not, will use the given channel number*/
	@Override
	public void minMaxSet(int channelNumber, double min, double max) {
		
		ArrayList<MultiChannelImage> wraps = getAllWrappers() ;
		
		/**The real channel name will be checked against the channel names in each image
		  in the for loop. display ranges will be changed in either those with a match
		  or (if no match), those with the same number*/
		String realName=getPressedMultichannel().getRealChannelName(channelNumber);
		
		StatusPanel.updateStatus("Setting Display Range "+realName+" in c= "+channelNumber+" "+min+", "+max);
		
		setDisplayRange(wraps, channelNumber, realName, min, max);
		updateAllDisplaysWithRealChannel( realName);
		presseddisplay.updatePanelsAndLabelsFromSource();
		new CurrentFigureSet().getCurrentlyActiveDisplay().updateDisplay();
	}

	/**sets the display range for a given channel. will first look to see if channels in the images
	 * can be identified by name. if not, will use the given channel number*/
	public static void setDisplayRange(ArrayList<MultiChannelImage> wraps, int channelNumber, String realName, double min,
			double max) {
		for(MultiChannelImage w: wraps) try {
			if (realName!=null &&!realName.equals("null")) {
				channelNumber=getBestMatchToChannel(w, realName, channelNumber);
			} 
			
			w.setChannelMin(channelNumber, min);
			w.setChannelMax(channelNumber, max);
		} catch (Throwable t) {IssueLog.logT(t);}
	}
	
	
	/**updates the image panels that contain the given channel */
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
	
	/**Displays the Set Scale Dialog*/
	public void showScaleSettingDialog() {
		LocalScaleSetterDialog lss = new LocalScaleSetterDialog(presseddisplay.getMultiChannelImage(), null);
		lss.showDialog();
	}
	
	/**A scale setting dialog that will update all the image panels and scale bars after an ok press*/
	public class LocalScaleSetterDialog extends ScaleSettingDialog {

		public LocalScaleSetterDialog(HasScaleInfo scaled,
				SwingDialogListener listener) {
			super(scaled, listener);
		}

		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public void onOK() {
			super.onOK();
			 updateAllAfterMenuAction();
		
		}
		
	}

	/**Called when a dialog item has been changed*/
	@Override
	public void itemChange(DialogItemChangeEvent event) {
		this.presseddisplay.updatePanels();
		
		this.updateAllDisplays();
		this.updateAllAfterMenuAction();
	}
	

	/**updates the panels*/
	public void updateAllDisplays() {
		for(ImageDisplayLayer pd: getAllDisplays() ) {
			if(pd==null) continue;
			if (this.updateInsets) updateInsetPanels(pd, null);
			pd.updatePanels();
		}
		if(presseddisplay!=null)
		presseddisplay.updatePanelsAndLabelsFromSource();//.updatePanels();//.updateMontageFromSource();
		
	}
	
	/**returns all the image display layers being targeted*/
	public ArrayList<ImageDisplayLayer> getAllDisplays() {
		ArrayList<ImageDisplayLayer> output = new ArrayList<ImageDisplayLayer>();
		if (presseddisplay==null) return output;
		output.add(presseddisplay);
		if (getCurrentOrganizer()!=null&&this.workOn==ALL_IMAGES_IN_FIGURE) {
			output = new ArrayList<ImageDisplayLayer>();
			output.addAll( getCurrentOrganizer().getMultiChannelDisplays());
		}
		if(this.extraDisplays!=null) output.addAll(extraDisplays);
		
		return output;
		
	}
	
	/**returns the panel manager that is relevant to the clicked image*/
	public PanelManager getPressedPanelManager() {
			
			PanelManager output = presseddisplay.getPanelManager();
			if (this.pressedInset!=null) {
				output =pressedInset.getPanelManager();
			}
			
			return output;
		}

	/**Adds a JMenu item to the container pop with the given meny text and action command*/
public void addButtonToMenu(Container pop, String text, String actionCommand) {
	addButtonToMenu(pop, text, actionCommand, null);
	
}

/**
 Adds a menu item to the given container
 */
public void addButtonToMenu(Container pop, String text, String actionCommand, Icon i) {
	JMenuItem renamer = new JMenuItem(text);
	renamer.setActionCommand(actionCommand);
	renamer.addActionListener(this);
	if (i!=null) renamer.setIcon(i);
	pop.add(renamer);
}


/**returns the multichannel image that is the primary target*/
public MultiChannelImage getPressedMultichannel() {
	if (presseddisplay==null) {IssueLog.log("You are not clicking on a figure panel"); return null;}
	return presseddisplay.getMultiChannelImage();
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
	if(extraWrappers!=null) {
		output.addAll(extraWrappers);
	
	}
	return output;
}

/**returns the channel label manager that is relevant to the clicked image*/
protected ChannelLabelManager getPressedChannelLabelManager() {
	
	ChannelLabelManager lm=presseddisplay.getChannelLabelManager();
	if (this.pressedInset!=null) {
		lm=pressedInset.getChannelLabelManager();
	}
return lm;
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

/**returns all of the inset definers in the given layer  */
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



/**looks for an inset definer in the image display layer that uses the given image panel to display the inset images
 * returns null if none found */
public static PanelGraphicInsetDefiner findInsetWith(ImageDisplayLayer pd, ImagePanelGraphic image) {
	ArrayList<PanelGraphicInsetDefiner> insets = getAllInsets(pd);
	for(PanelGraphicInsetDefiner in: insets) {
		if(in.getPanelManager().getPanelList().getPanelGraphics().contains(image)) return in;
		
	}
	return null;
	
}


/**Tries to find the channel index of the channel names realChanName, if it cant, it just returns the chanNum*/
public static int getBestMatchToChannel(MultiChannelImage mw, String realChanName, int chanNum) {
	int chan1=mw.getIndexOfChannel(realChanName);
	if (chan1<1||chan1>mw.nChannels()) 
		chan1=chanNum;
	return chan1;
}



/**sets teh channel color of the currently selected channel*/
 void setTheColor(Color color) {
	Integer chan1 = getSelectedChanNumber();
	setTheColor(color, chan1);
}

/**sets the color of the channel*/
public void setTheColor(Color color, Integer chan1) {
	String realName=getPressedMultichannel().getRealChannelName(chan1);
	
	
	ArrayList<MultiChannelImage> allWrappers = getAllWrappers();
	CombinedEdit undo = ChannelDisplayUndo.createMany(allWrappers, this, ChannelDisplayUndo.COLOR_TYPE);
	
	for(MultiChannelImage ic: allWrappers) {
		int chan=getBestMatchToChannel(ic, realName, chan1);
		ic.getChannelSwapper().setChannelColor(color, chan);
	}
	updateAllDisplaysWithRealChannel( realName);
	updateAllDisplays();
	try {
		new CurrentFigureSet().getCurrentlyActiveDisplay().updateDisplay();
		new CurrentFigureSet().addUndo(undo);
	} catch (Exception e) {
		IssueLog.logT(e);
	}
}


public Integer getSelectedChanNumber() {
	return chanNum;
};  

/**An implementation of color input listener that actually performs the channel recoloring*/
public class ChanReColorer implements ColorInputListener {

	int myNum=1;
	public ChanReColorer(int num) {
		myNum=num;
	}
	@Override
	public void ColorChanged(ColorInputEvent fie) {
		setTheColor(fie.getColor(), myNum);
		
	}
	
	/**Adds the color menu to one container and puts the container inside of the second*/
	private void addColorMenu(Container b, Container output) {
		 b.add(ColorJMenu.getStandardColorJMenu(this).getComponent(0));
		 output.add(b);
	}
	


}

/**Adds a recolor channel menu to the container. the channels in the channel entry list fill appear in that menu*/
public void addChenEntryColorMenus(Container j, ArrayList<ChannelEntry> iFin) {
	JMenu output = new JMenu("Recolor Channel");
	for(int i=0; i<iFin.size(); i++) {
		String nameRC = iFin.get(i).getRealChannelName();
		if (nameRC==null||nameRC.trim().equals("")) nameRC="Chan "+iFin.get(i).getOriginalChannelIndex();
		ChanReColorer colorer = new ChanReColorer(iFin.get(i).getOriginalChannelIndex());
		if (iFin.size()>1)
		colorer.addColorMenu(new JMenu(nameRC),output);
		else colorer.addColorMenu(output,output);
	}
	
	j.add(output);
}

/**updates the display */
public class AfterUndoChannel extends AbstractUndoableEdit2 implements EditListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void undo() {
		updateAllDisplays();
	}
	public void redo() {
		updateAllDisplays();
	}
	@Override
	public void afterEdit() {
		updateAllDisplays();
		
	}
}

}
