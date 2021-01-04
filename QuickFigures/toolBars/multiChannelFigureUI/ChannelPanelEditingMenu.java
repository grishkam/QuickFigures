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
package multiChannelFigureUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import advancedChannelUseGUI.AdvancedChannelUseGUI;
import applicationAdapters.HasScaleInfo;
import channelLabels.ChannelLabelManager;
import channelMerging.ChannelEntry;
import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import channelMerging.MultiChannelImage;
import figureEditDialogs.PanelStackDisplayOptions;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelListElement;
import figureOrganizer.PanelManager;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import iconGraphicalObjects.ChannelUseIcon;
import iconGraphicalObjects.ColorIcon;
import iconGraphicalObjects.ColorModeIcon;
import iconGraphicalObjects.IconUtil;
import includedToolbars.StatusPanel;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import messages.ShowMessage;
import sUnsortedDialogs.ScaleSettingDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;
import undo.ChannelDisplayUndo;
import undo.ChannelUseChangeUndo;
import undo.CombinedEdit;
import undo.EditListener;
import undo.PanelManagerUndo;


/**Generates menu items and executes operations related to channel color, channel display range and other properties
  same set of options will appear in different contexts, as submenus within different menus or outside of menus*/
public class ChannelPanelEditingMenu implements ActionListener, DisplayRangeChangeListener, StandardDialogListener {
	
	
	public static int ALL_IMAGES_IN_CLICKED_FIGURE=1, CLICKED_IMAGES_ONLY=0;
	/***/
	FigureOrganizingLayerPane givenOrganizer=null;//the targetted figure organizing layer
	private MultichannelDisplayLayer presseddisplay;//the primary target of the actions and options
	private ArrayList<MultiChannelImage> extraWrappers=null;//in some contexts, additional items that are not directly clicked on are included
	private ArrayList<MultichannelDisplayLayer> extraDisplays=null;
	
	boolean updateInsets=true;
	private PanelGraphicInsetDefiner pressedInset;
	
	protected PanelListElement stackSlicePressed;//the panel that is being targetted 
	protected int targetChannelNumber=ChannelUseInstructions.NONE_SELECTED;
	private ChannelEntry entryPress;
	Color colorForColorModeIcon=Color.red;
	
	private int workOn=ALL_IMAGES_IN_CLICKED_FIGURE;
	

	static final String SCALING_COMMAND="Scale", COLOR_MODE_COMMAND="ColorMode",CHANNEL_USE_COMMAND="Channel Use";
	static final String minMaxCommand="MinMax",WLCommand="WinLev";
	static final String orderCommand="order and luts", orderCommand2="Min, Max, order and luts";
	static final String panContentCommand="Panel Content Gui",colorRecolorCommand="Fix Colors";
	static final String renameChanCommand="Add Channel exposures to summary", channelNameCommand="rename channels";
	
	/**constructor called within figure organizing menu*/
	public ChannelPanelEditingMenu(FigureOrganizingLayerPane given, int chanN) {
		this.givenOrganizer=given;
		targetChannelNumber=chanN;
		setPresseddisplay((MultichannelDisplayLayer) given.getPrincipalMultiChannel());
		if (getPresseddisplay()!=null) try {
			colorForColorModeIcon=getPresseddisplay().getMultiChannelImage().getChannelColor(1);
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
		
		setPresseddisplay(pd);
		if(ipg==null) return;
		stackSlicePressed= ipg.getSourcePanel();
		targetChannelNumber=stackSlicePressed.targetChannelNumber;
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
		
		setPressedInset(PanelGraphicInsetDefiner.findInsetWith(ipg));
		if (pd==null &&getPressedInset()!=null) {
			pd=getPressedInset().getSourceDisplay();
		}
		
		
		if (pd!=null) {
	
			setPresseddisplay(pd);
			
			stackSlicePressed= pd.getPanelWithDisplay(imagepanel);
			targetChannelNumber=stackSlicePressed.targetChannelNumber;
		}
		setColorToStackSlice();
	}
	

	
	/**Adds the menu items to the given container*/
	public void addChannelRelevantMenuItems(Container output) {
		addChannelRelevantMenuItems(output, false);
	}
	public void addChannelRelevantMenuItems(Container output, boolean limitVersionOfMenu) {
		addButtonToMenu(output, "Window/Level", WLCommand, IconUtil.createBrightnessIcon(0));
		 addButtonToMenu(output, "Min/Max", minMaxCommand, IconUtil.createBrightnessIcon(0));
		 addButtonToMenu(output, "Change Color Modes", COLOR_MODE_COMMAND, new ColorModeIcon(colorForColorModeIcon));
		 addButtonToMenu(output, "Channel Use Options", CHANNEL_USE_COMMAND, new ChannelUseIcon(this.getPrincipalDisplay().getMultiChannelImage().getChannelEntriesInOrder()));
		
			 try {
				addColorMenus("Recolor", output);
			} catch (Throwable e) {
				IssueLog.logT(e);
			}
			
		 
		 
		 addButtonToMenu(output, "Recolor Channels Automatically", colorRecolorCommand, new ChannelUseIcon(this.getPrincipalDisplay().getMultiChannelImage().getChannelEntriesInOrder(), ChannelUseIcon.VERTICAL_BARS, true));
		 
		 if(!limitVersionOfMenu) {
		 JMenu chanLabelMenu=new JMenu("Channel Label");
		 addButtonToMenu(chanLabelMenu, "Edit Channel Label", channelNameCommand);
		 addButtonToMenu(chanLabelMenu, "Reset Channel Label Names ", renameChanCommand);
		// output.add(chanLabelMenu);
		 }
		 if(!limitVersionOfMenu&&this.getAllMultiChannelImages().size()>1) 
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
		if (arg0.getActionCommand().equals(SCALING_COMMAND)) {
			 showScaleSettingDialog() ;
		}
		

		
		
		
		if (arg0.getActionCommand().equals(COLOR_MODE_COMMAND)) {
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
	getPresseddisplay().getMultiChannelImage().renameBasedOnRealChannelName();
	this.updateAllDisplays();
}
		
		
		if (arg0.getActionCommand().equals(CHANNEL_USE_COMMAND)) {
			
			
			
			if (getPressedInset()!=null) {new PanelStackDisplayOptions(getPresseddisplay(),getPressedInset().getPanelManager().getPanelList(),getPressedInset().getPanelManager(), false).showDialog();;}
			else
			if (getScope()==CLICKED_IMAGES_ONLY) {
				if (getPressedInset()==null)
				getPresseddisplay().showStackOptionsDialog();
				
				
			} else {
				
				PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(getPresseddisplay(),getPresseddisplay().getPanelList(),null, false);
				
				/**adds a list of all the channel displays that are relevant*/
				ArrayList<ImageDisplayLayer> all = getAllDisplays();
				all.remove(getPresseddisplay());
				dialog.addAditionalDisplays(all);
				
				dialog.showDialog();
				
				
			}
		}
		
		
	
		
		if (arg0.getActionCommand().equals(orderCommand2)) {
			setScope(ALL_IMAGES_IN_CLICKED_FIGURE);
			new ChannelOrderAndLutMatching().matchChannels(this.getPressedMultichannel(), this.getAllMultiChannelImages(), 2);
			for(int c=1; c<=this.getPressedMultichannel().nChannels(); c++) {
				minMaxSet(c, getPressedMultichannel().getChannelMin(c),getPressedMultichannel().getChannelMax(c));
			}
		}
		
		if (arg0.getActionCommand().equals(panContentCommand)) {
			AdvancedChannelUseGUI distpla = new AdvancedChannelUseGUI(  getPressedPanelManager(), this.getPressedChannelLabelManager());
			//getPressedPanelManager().getStack().setChannelUpdateMode(true);
			distpla.setVisible(true);
		}
	
		
		 updateAllAfterMenuAction();
		if(undo!=null) { new CurrentFigureSet().addUndo(undo);
							}
	}

	protected CombinedEdit recolorBasedOnRealChannelNames() {
		ArrayList<MultiChannelImage> all = this.getAllMultiChannelImages();
		CombinedEdit undo = ChannelDisplayUndo.createMany(all, this, ChannelDisplayUndo.COLOR_TYPE);
		
		for(MultiChannelImage p: all)
			p.colorBasedOnRealChannelName();
		this.updateAllDisplays();
		undo.establishFinalState();
		return undo;
	}

	protected CombinedEdit changeColorModes() {
		ChannelUseInstructions ins = getPresseddisplay().getPanelList().getChannelUseInstructions();
		if (this.getPressedInset()!=null) {
			 ins =getPressedInset().getPanelManager().getPanelList().getChannelUseInstructions();
			
		}
		CombinedEdit undo=new CombinedEdit();
		undo.addEditListener(new AfterUndoChannel());
		
		undo.addEditToList(new ChannelUseChangeUndo(ins));
		
		int value = ins.channelColorMode;
		if (value==1) {value=0;} else {value=1;}
		ins.channelColorMode=value;
		
		
		
		if (getScope()==ALL_IMAGES_IN_CLICKED_FIGURE && getPressedInset()==null) for(ImageDisplayLayer d: getAllDisplays()) {
			undo.addEditToList(new ChannelUseChangeUndo(d));
			d.getPanelList().getChannelUseInstructions().channelColorMode=value;
		} 
		/**if(getExtraDisplays()!=null&&pressedInset==null)	for(ImageDisplayLayer d:this.getExtraDisplays()) {
			undo.addEditToList(new ChannelUseChangeUndo(d));
			d.getPanelList().getChannelUseInstructions().channelColorMode=value;//this part might be redudent
		}*/
		undo.addEditListener(new AfterUndoChannel());
		undo.establishFinalState();
		this.updateAllDisplays();
		
		return undo;
				
	}

	
	protected CombinedEdit showDisplayRangeDialog(int type) {
		WindowLevelDialog.showWLDialogs(getChannelEntryList(),  getPrincipalMultiChannel(), this, type , ChannelDisplayUndo.createMany(getAllMultiChannelImages(), this));
		return ChannelDisplayUndo.createMany(getAllMultiChannelImages(), this);
	}

	


	public MultiChannelImage getPrincipalMultiChannel() {
		return this.getPresseddisplay().getMultiChannelImage();
	}
	
	public MultichannelDisplayLayer getPrincipalDisplay() {
		return getPresseddisplay();
	}


	public ArrayList<ChannelEntry> getChannelEntryList() {
		if(this.entryPress!=null) {
			ArrayList<ChannelEntry> out=new ArrayList<ChannelEntry>();
			out.add(entryPress);
			return out;
		}
		if (stackSlicePressed==null && getPresseddisplay()!=null) return  getPresseddisplay().getMultiChannelImage().getChannelEntriesInOrder();  
		return this.stackSlicePressed.getChannelEntries();
	}
	
	void updateAllAfterMenuAction() {
		if(getPresseddisplay()!=null)getPresseddisplay().updatePanels();//.getMultichanalWrapper().updateDisplay();
		if (getScope()==ALL_IMAGES_IN_CLICKED_FIGURE) {
			for(ImageDisplayLayer d: getAllDisplays()) {
				d.updatePanels();
			}
		}
		getPresseddisplay().updateDisplay();
	}

	/***/
	public FigureOrganizingLayerPane getCurrentOrganizer() {
		if (givenOrganizer!=null) return givenOrganizer;
		if (getPresseddisplay().getParentLayer() instanceof FigureOrganizingLayerPane) {
			return (FigureOrganizingLayerPane) getPresseddisplay().getParentLayer();
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
		
		ArrayList<MultiChannelImage> wraps = getAllMultiChannelImages() ;
		
		/**The real channel name will be checked against the channel names in each image
		  in the for loop. display ranges will be changed in either those with a match
		  or (if no match), those with the same number*/
		String realName=getPressedMultichannel().getRealChannelName(channelNumber);
		
		StatusPanel.updateStatus("Setting Display Range "+realName+" in c= "+channelNumber+" "+min+", "+max);
		
		setDisplayRange(wraps, channelNumber, realName, min, max);
		updateAllDisplaysWithRealChannel( realName);
		getPresseddisplay().updatePanelsAndLabelsFromSource();
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
		LocalScaleSetterDialog lss = new LocalScaleSetterDialog(getPresseddisplay().getMultiChannelImage(), null);
		lss.showDialog();
	}
	
	/**A scale setting dialog that will update all the image panels and scale bars after an ok press*/
	public class LocalScaleSetterDialog extends ScaleSettingDialog {

		public LocalScaleSetterDialog(HasScaleInfo scaled,
				StandardDialogListener listener) {
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
		this.getPresseddisplay().updatePanels();
		
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
		if(getPresseddisplay()!=null)
		getPresseddisplay().updatePanelsAndLabelsFromSource();//.updatePanels();//.updateMontageFromSource();
		
	}
	
	/**returns all the image display layers being targeted*/
	public ArrayList<ImageDisplayLayer> getAllDisplays() {
		ArrayList<ImageDisplayLayer> output = new ArrayList<ImageDisplayLayer>();
		if (getPresseddisplay()==null) return output;
		output.add(getPresseddisplay());
		if (getCurrentOrganizer()!=null&&this.getScope()==ALL_IMAGES_IN_CLICKED_FIGURE) {
			output = new ArrayList<ImageDisplayLayer>();
			output.addAll( getCurrentOrganizer().getMultiChannelDisplays());
		}
		if(this.getExtraDisplays()!=null) output.addAll(getExtraDisplays());
		
		return output;
		
	}

	
	/**returns the panel manager that is relevant to the clicked image*/
	public PanelManager getPressedPanelManager() {
			
			PanelManager output = getPresseddisplay().getPanelManager();
			if (this.getPressedInset()!=null) {
				output =getPressedInset().getPanelManager();
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
	if (getPresseddisplay()==null) {IssueLog.log("You are not clicking on a figure panel"); return null;}
	return getPresseddisplay().getMultiChannelImage();
}

/**returns the figure that is the primary target*/
public FigureOrganizingLayerPane getPressedFigure() {
	return FigureOrganizingLayerPane.findFigureOrganizer(getPresseddisplay());
}

/**If the multichannel display is not in a figure organized layer pane,
this returns an array with just a single multichannel Display.
Otherwise returns all the multichannel displays that are directly in the figure organizing
pane*/
public ArrayList<MultiChannelImage> getAllMultiChannelImages() {
	ArrayList<MultiChannelImage> output=new ArrayList<MultiChannelImage>();
	output.addAll(getPresseddisplay().getAllSourceImages());
	if (getPresseddisplay().getParentLayer() instanceof FigureOrganizingLayerPane &&this.getScope()==ALL_IMAGES_IN_CLICKED_FIGURE) {
		FigureOrganizingLayerPane pane=(FigureOrganizingLayerPane) getPresseddisplay().getParentLayer();
		return pane.getAllSourceImages();
	}
	if(getExtraWrappers()!=null) {
		output.addAll(getExtraWrappers());
	
	}
	return output;
}

/**returns all the pressed figures*/
public ArrayList<FigureOrganizingLayerPane> getAllFigures() {
	ArrayList<FigureOrganizingLayerPane> output = new  ArrayList<FigureOrganizingLayerPane>();
	output.add(this.getPressedFigure());
	for(MultichannelDisplayLayer e: this.getExtraDisplays()) {
		output.add(FigureOrganizingLayerPane.findFigureOrganizer(e));
	}
	return output;
}

/**returns the channel label manager that is relevant to the clicked image*/
protected ChannelLabelManager getPressedChannelLabelManager() {
	
	ChannelLabelManager lm=getPresseddisplay().getChannelLabelManager();
	if (this.getPressedInset()!=null) {
		lm=getPressedInset().getChannelLabelManager();
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
	
	
	ArrayList<MultiChannelImage> allWrappers = getAllMultiChannelImages();
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
	return targetChannelNumber;
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
	

}

/**Adds a recolor channel menu to the container. the channels in the channel entry list fill appear in that menu*/
public void addChenEntryColorMenus(Container j, ArrayList<ChannelEntry> iFin) {
	JMenu output = new SmartJMenu("Recolor Channel");
	for(int i=0; i<iFin.size(); i++) {
		ChannelEntry channelEntry = iFin.get(i);
		String nameRC = channelEntry.getRealChannelName();
		if (nameRC==null||nameRC.trim().equals("")) nameRC="Chan "+channelEntry.getOriginalChannelIndex();
		ChanReColorer colorer = new ChanReColorer(channelEntry.getOriginalChannelIndex());
		if (iFin.size()>1) {
		SmartJMenu b = ChannelColorJMenu.getStandardColorJMenu(colorer);
		b.setIcon(new ColorIcon(channelEntry.getColor()));
		b.setText(nameRC);
		output.add(b);
		
		}
		else {
			output=ChannelColorJMenu.getStandardColorJMenu(colorer);
			output.setIcon(new ColorIcon(channelEntry.getColor()));
			output.setText("Recolor Channel");
		}
	}
	
	j.add(output);
}

public ArrayList<MultiChannelImage> getExtraWrappers() {
	return extraWrappers;
}

public void setExtraWrappers(ArrayList<MultiChannelImage> extraWrappers) {
	this.extraWrappers = extraWrappers;
}

public ArrayList<MultichannelDisplayLayer> getExtraDisplays() {
	return extraDisplays;
}

public void setExtraDisplays(ArrayList<MultichannelDisplayLayer> extraDisplays) {
	this.extraDisplays = extraDisplays;
}

public int getScope() {
	return workOn;
}

public void setScope(int workOn) {
	this.workOn = workOn;
}

/**updates the display */
public class AfterUndoChannel implements EditListener {
	
	@Override
	public void afterEdit() {
		updateAllDisplays();
		
	}
	
	
}


/**
 Changes whether the given channel is displayed in each of the merged images
 * @return 
 */
public CombinedEdit setChannelExcludedFromMerge(int chaneIndex, boolean excluded) {
	
	if (getPressedInset()!=null) {
		CombinedEdit undo = PanelManagerUndo.createFor(getPressedInset().getPanelManager());
		getPressedInset().getPanelManager().setMergeExcluded(chaneIndex, excluded, false);
		return undo;
	}
	
	ArrayList<ImageDisplayLayer> disp1 = getAllDisplays();
	CombinedEdit output=new CombinedEdit();
	boolean warningHasNotBeenSeen=true;
	for(ImageDisplayLayer d : disp1) {
		output.addEditToList( PanelManagerUndo.createFor(d));
		warningHasNotBeenSeen=d.getPanelManager().setMergeExcluded(chaneIndex, excluded, warningHasNotBeenSeen);
	}
	output.establishFinalState();
	return output;
}


/**WORK IN PROGRESS
Changes whether the given channel is displayed in each of the merged images
* @return 
*/
public CombinedEdit setChannelExcludedFromFigure(int chaneIndex, boolean excluded, boolean mergeToo) {
	boolean alreadyExcluded = this.getPressedFigure().getPrincipalMultiChannel().getPanelList().getChannelUseInstructions().getExcludedChannelPanels().contains(chaneIndex);
	if (excluded&&alreadyExcluded) return null;
	if (!excluded&&!alreadyExcluded) return null;
	CombinedEdit output=null;
	if(excluded && !alreadyExcluded) {
		if (ShowMessage.showOptionalMessage("Channel panel removal menu is a work in progress", true, "Are you sure you want to remove the channel panels? "))
			 {
				output=new ChannelPanelRemover(this.getPressedFigure()).removeChannelPanels(chaneIndex);
				if (mergeToo) output.addEditToList(setChannelExcludedFromMerge(chaneIndex, excluded));
			 }
	}
	
	if(!excluded && alreadyExcluded) {
		if (ShowMessage.showOptionalMessage("Channel panel removal menu is a work in progress", true, "Are you sure you want to remove the channel panels? "))
			 {
				output=new ChannelPanelRemover(this.getPressedFigure()).addChannelPanels(chaneIndex);
				if (mergeToo) output.addEditToList(setChannelExcludedFromMerge(chaneIndex, excluded));
			 }
			 
			 }
	
	return output;
}







public static final int NO_MERGE_CHANNEL_MENU=0, EXCLUDED_CHANNEL_MENU=1, MERGE_WITH_EACH_MENU=2,EXCLUDED_CHANNEL_AND_DONT_MERGE=3;;


public SmartJMenu createChannelMergeMenu(int form) {
	SmartJMenu output = new SmartJMenu("Merged Channels");
	if (form== EXCLUDED_CHANNEL_MENU) output.setText("Exclude Channel Panels ");
	else if (form==MERGE_WITH_EACH_MENU) output.setText("Merge each channel panel with");
	else if (form==EXCLUDED_CHANNEL_AND_DONT_MERGE) output.setText("Exclude Channel");
	ArrayList<ChannelMergeMenuItem> f = createChannelMergeMenuItems(form);
	for(ChannelMergeMenuItem item: f) {output.add(item);}

	return output;
}

/**returns a */
public ArrayList<ChannelMergeMenuItem> createChannelMergeMenuItems(int form) {
	ArrayList<ChannelMergeMenuItem> m =new  ArrayList<ChannelMergeMenuItem> ();
	for(ChannelEntry e: getPresseddisplay().getMultiChannelImage().getChannelEntriesInOrder()) {
		int ignoreAfterC = getPresseddisplay().getPanelList().getChannelUseInstructions().ignoreAfterChannel;
		if (ignoreAfterC!=ChannelUseInstructions.NONE_SELECTED   &e.getOriginalChannelIndex()>ignoreAfterC) continue;
		if (form== EXCLUDED_CHANNEL_MENU)	m.add(new ChannelExcludeMenuItem(e, false));
		else if (form==EXCLUDED_CHANNEL_AND_DONT_MERGE) m.add(new ChannelExcludeMenuItem(e, true));
		else
			if (form== MERGE_WITH_EACH_MENU) 	m.add(new ChannelWithEachMenuItem(e));
			else
		 m.add(new ChannelMergeMenuItem(e));
	}
	return m;
}

public PanelGraphicInsetDefiner getPressedInset() {
	return pressedInset;
}

public void setPressedInset(PanelGraphicInsetDefiner pressedInset) {
	this.pressedInset = pressedInset;
}

public MultichannelDisplayLayer getPresseddisplay() {
	return presseddisplay;
}

public void setPresseddisplay(MultichannelDisplayLayer presseddisplay) {
	this.presseddisplay = presseddisplay;
}

/**Menu item that allows the used to select/deselct which channels belong in the merged image*/
		public class ChannelMergeMenuItem extends BasicChannelEntryMenuItem {
		
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			private ChannelUseInstructions instructions;
			
			
		public 	ChannelMergeMenuItem(ChannelEntry ce) {
					super(ce);
					instructions=getPresseddisplay().getPanelManager().getPanelList().getChannelUseInstructions();
					
					boolean strike=isExcludedChannel();
					super.setSelected(!strike);
					updateFont();
					
					this.addActionListener(new ActionListener() {
				
						@Override
						public void actionPerformed(ActionEvent e) {
							pressAction();
						}				
					});
					
							
							
				}
				
				/**
				 determines if the channel is excluded
				 */
				public boolean isExcludedChannel() {
					if (getPressedInset()!=null) {
						
						return getPressedInset().getPanelManager().getPanelList().getChannelUseInstructions().isMergeExcluded(entry.getOriginalChannelIndex());
					}
					return getPresseddisplay().getPanelManager().getPanelList().getChannelUseInstructions().getNoMergeChannels().contains(entry.getOriginalChannelIndex());
				}
				
				
				/**
				 * 
				 */
				public void pressAction() {
					
					int chaneIndex = entry.getOriginalChannelIndex();
					boolean i = instructions.getNoMergeChannels().contains(chaneIndex);
					CombinedEdit undo = setChannelExcludedFromMerge(chaneIndex, !i);
					this.getUndoManager().addEdit(
						undo	
					);
					
					updateFont();
				}


}
		
		/**Menu item that allows the used to select/deselct which channels belong in the merged image*/
		public class ChannelExcludeMenuItem extends ChannelMergeMenuItem{
		
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private boolean excludeFromMergeAlso=false;
			
			
			
		public 	ChannelExcludeMenuItem(ChannelEntry ce, boolean excludeFromMergeAlso) {
					super(ce);
					this.excludeFromMergeAlso=excludeFromMergeAlso;
				}
				
				/**
				 determines if the channel is excluded
				 */
				public boolean isExcludedChannel() {
					return getPresseddisplay().getPanelManager().getPanelList().getChannelUseInstructions().getExcludedChannelPanels().contains(entry.getOriginalChannelIndex());
				}
				
				
				/**
				 * 
				 */
				public void pressAction() {
					int chaneIndex = entry.getOriginalChannelIndex();
					boolean i = getPresseddisplay().getPanelManager().getPanelList().getChannelUseInstructions().getExcludedChannelPanels().contains(chaneIndex);
					CombinedEdit undo = setChannelExcludedFromFigure(chaneIndex, !i, excludeFromMergeAlso);
					
					this.getUndoManager().addEdit(
						undo	
					);
					
					updateFont();
				}


}
		
		/**Menu item that allows the used to select/deselct which channels belong in the merged image*/
		public class ChannelWithEachMenuItem extends ChannelMergeMenuItem{
		
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			
			
		public 	ChannelWithEachMenuItem(ChannelEntry ce) {
					super(ce);
				}
				
				/**
				 determines if the channel is excluded or included
				 */
				public boolean isExcludedChannel() {
					
					return getPresseddisplay().getPanelManager().getPanelList().getChannelUseInstructions().eachMergeChannel!=entry.getOriginalChannelIndex();
				}
				
				
				/**
				 Changes the channels present
				 */
				public void pressAction() {
					
					ChannelUseInstructions i = getPresseddisplay().getPanelManager().getPanelList().getChannelUseInstructions();
					
					
					int newState=entry.getOriginalChannelIndex();
					if(newState==i.eachMergeChannel) newState=0;
					
					CombinedEdit undo = new CombinedEdit();
							
						
						for(ImageDisplayLayer d: getAllDisplays()) {
							
							i=d.getPanelManager().getChannelUseInstructions();
							ChannelUseChangeUndo edit = new ChannelUseChangeUndo(i);
							undo.addEditToList(edit);
					
					i.eachMergeChannel=newState;
					edit.establishFinalLocations();
					d.getPanelManager().updatePanels();
						}
					updateFont();
					this.getUndoManager().addEdit(
							undo	
						);
					
					if(newState>0)
						ShowMessage.showOptionalMessage("Merge With Each", true, "Each channel panel will now be merged with channel "+newState, "You can select the menu item again to remove the extra channel");
					
				}

				int fontStyle() {
						if(entry.getOriginalChannelIndex()==targetChannelNumber)
						return Font.BOLD;
						else
						return Font.PLAIN;
					}
				
}

}
