package multiChannelFigureUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.undo.AbstractUndoableEdit;

import channelMerging.ChannelEntry;
import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelWrapper;
import channelMerging.PanelStackDisplay;
import genericMontageKit.PanelListElement;
import graphicActionToombar.CurrentSetInformerBasic;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDef;
import graphicalObjects_FigureSpecific.PanelManager;
import graphicalObjects_LayerTypes.GraphicLayer;
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
import undo.CompoundEdit2;
import applicationAdapters.HasScaleInfo;
import channelLabels.ChannelLabelManager;


/**Generates menu items and executes operations related to channel color, display range and order*/
public class ChannelSwapperToolBit2 implements ActionListener, DisplayRangeChangeListener, SwingDialogListener {
	
	FigureOrganizingLayerPane givenOrganizer=null;
	protected MultichannelDisplayLayer presseddisplay;
	public ArrayList<MultiChannelWrapper> extraWrappers=null;//in some contexts, additional items that are not directly clicked on are included
	public ArrayList<MultichannelDisplayLayer> extraDisplays=null;
	
	boolean updateInsets=true;
	protected PanelGraphicInsetDef pressedInset;
	protected PanelListElement stackSlicePressed;
	protected int chanNum=0;
	private ChannelEntry entryPress;
	
	public int workOn=1;
	int swapMode=0;
	
	
	public ChannelSwapperToolBit2(FigureOrganizingLayerPane given, int chanN) {
		this.givenOrganizer=given;
		chanNum=chanN;
		presseddisplay=(MultichannelDisplayLayer) given.getPrincipalMultiChannel();
	}

	public ChannelSwapperToolBit2(ImagePanelGraphic ipg, ChannelEntry e) {
		this(ipg);
		this.entryPress=e;
	}
	
	public ChannelSwapperToolBit2(MultichannelDisplayLayer pd, ImagePanelGraphic ipg) {
		
		presseddisplay=pd;
		if(ipg==null) return;
		//ImagePanelGraphic imagepanel = ipg;
		stackSlicePressed= ipg.getSourcePanel();
		chanNum=stackSlicePressed.originalChanNum;
	}
	
	
	
	public ChannelSwapperToolBit2(ImagePanelGraphic ipg) {
		if(ipg==null) return;//dont know whey this is sometimes called
		MultichannelDisplayLayer pd = MultichannelDisplayLayer.findMultiChannelForGraphic(ipg.getParentLayer(), ipg );
		
		
		PanelListElement output=null;
		ImagePanelGraphic imagepanel = ipg;
		
		pressedInset = PanelGraphicInsetDef.findInsetWith(ipg);
		if (pd==null &&pressedInset!=null) {
			pd=pressedInset.getSourceDisplay();
		}
		
		
		if (pd!=null) {
	
			presseddisplay=pd;
		//	boolean direct=presseddisplay.getStack().getPanelGraphics().contains(imagepanel);
			
			
			
			stackSlicePressed= pd.getPanelWithDisplay(imagepanel);
			chanNum=stackSlicePressed.originalChanNum;
		}
		
	}
	
	
	








	static String scalingCommand="Scale";
	static String colorModeCommand="ColorMode";
	static String chanUseCommand="Channel Use";
	static String minMaxCommand="MinMax";
	static String WLCommand="WinLev";
	private static String orderCommand="order and luts";
	private static String orderCommand2="Min, Max, order and luts";
	private String scalingCommand2="Scale of Panels";
	private String panContentCommand="Panel Content Gui";
	private String colorRecolorCommand="Fix Colors";


	private String renameChanCommand="Add Channel exposures to summary";
	private String channelNameCommand="rename channels";
	
	
	public JMenu createJPopup() {
		JMenu output = new JMenu("Adjust");
		 
		 addBasicWL2(output);
		 addButtonToMenu(output, "Set Units For Scale", scalingCommand);
		 addButtonToMenu(output, "Set Bilinear Scaling", scalingCommand2);
		 addButtonToMenu(output, "Change Color Modes", colorModeCommand);
		 addButtonToMenu(output, "Channel Use Options", chanUseCommand);
		 addButtonToMenu(output, "Advanced Channel Use", panContentCommand);
		 addButtonToMenu(output, "Recolor Channels Automatically", colorRecolorCommand);
		 addButtonToMenu(output, "ResetChannel Names (experimental)", renameChanCommand);
		 
		 addButtonToMenu(output, "Match Channel Order and LUT Colors", orderCommand);
		 addButtonToMenu(output, "Match Min, Max, Channel Order and LUT Colors", orderCommand2);
		
		//PanelMenuForMultiChannel allPanels = new PanelMenuForMultiChannel("All Panels", presseddisplay,  getPressedPanelManager().getStack(), this.getPressedPanelManager());
		//output.add(allPanels); 
		
		return output;
	}


	public void addBasicWL2(Container output) {
		addButtonToMenu(output, "Window/Level", WLCommand);
		 addButtonToMenu(output, "Min/Max", minMaxCommand);
		
	}
	
	
	public void addChannelRelevantMenuItems(Container output) {
		addChannelRelevantMenuItems(output, false);
	}
	public void addChannelRelevantMenuItems(Container output, boolean limit) {
		addButtonToMenu(output, "Window/Level", WLCommand);
		 addButtonToMenu(output, "Min/Max", minMaxCommand);
		 addButtonToMenu(output, "Change Color Modes", colorModeCommand);
		 addButtonToMenu(output, "Channel Use Options", chanUseCommand);
		
			 try {
				addColorMenus("Recolor", output);
			} catch (Throwable e) {
				IssueLog.log(e);
			}
			
		 
		 
		 addButtonToMenu(output, "Recolor Channels Automatically", colorRecolorCommand);
	
		 
		 if(!limit) {
		 JMenu chanLabelMenu=new JMenu("Channel Label");
		 addButtonToMenu(chanLabelMenu, "Edit Channel Label", channelNameCommand);
		 addButtonToMenu(chanLabelMenu, "Reset Channel Label Names ", renameChanCommand);
		// output.add(chanLabelMenu);
		 }
		 if(!limit) addButtonToMenu(output, "Match Min, Max, Channel Order and LUT Colors", orderCommand2);
			
	}



	

	private void addColorMenus(String string, Container j) {
		ArrayList<ChannelEntry> list=getChannelEntryList();
		/**if (stackSlicePressed!=null) list=stackSlicePressed.getChannelEntries();
		else
			if(presseddisplay!=null)
			list=presseddisplay.getMultichanalWrapper().getChannelEntriesInOrder();
		*/
		if(list!=null)
		addChenEntryColorMenus(j, list);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		CompoundEdit2 undo = null ;
		
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
	presseddisplay.getMultichanalWrapper().renameBasedOnRealChannelName();
	this.updateAllDisplays();
}
		
		
		if (arg0.getActionCommand().equals(chanUseCommand)) {
			
			
			
			if (pressedInset!=null) {new PanelStackDisplayOptions(presseddisplay,pressedInset.getPanelManager().getPanelList(),pressedInset.getPanelManager(), false).showDialog();;}
			else
			if (workOn==0) {
				if (pressedInset==null)
				presseddisplay.showStackOptionsDialog();
				
				
			} else {
				
				PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(presseddisplay,presseddisplay.getStack(),null, false);
				
				/**adds a list of all the channel displays that are relevant*/
				ArrayList<PanelStackDisplay> all = getAllDisplays();
				all.remove(presseddisplay);
				dialog.addAditionalDisplays(all);
				
				dialog.showDialog();
				
				
			}
		}
		
		
	
		
		if (arg0.getActionCommand().equals(orderCommand2)) {
			workOn=1;
			new ChannelOrderAndLutMatching().matchOrder(this.getPressedWrapper(), this.getAllWrappers(), 2);
			for(int c=1; c<=this.getPressedWrapper().nChannels(); c++) {
				minMaxSet(c, getPressedWrapper().getChannelMin(c),getPressedWrapper().getChannelMax(c));
			}
		}
		
		if (arg0.getActionCommand().equals(panContentCommand)) {
			PanelListDisplayGUI distpla = new PanelListDisplayGUI(  getPressedPanelManager(), this.getPressedChannelLabelManager());
			//getPressedPanelManager().getStack().setChannelUpdateMode(true);
			distpla.setVisible(true);
		}
	
		
		 updateAllAfterMenuAction();
		if(undo!=null) { new CurrentSetInformerBasic().addUndo(undo);
							}
	}

	protected CompoundEdit2 recolorBasedOnRealChannelNames() {
		ArrayList<MultiChannelWrapper> all = this.getAllWrappers();
		CompoundEdit2 undo = ChannelDisplayUndo.createMany(all, this, ChannelDisplayUndo.COLOR_TYPE);
		
		for(MultiChannelWrapper p: all)
			p.colorBasedOnRealChannelName();
		this.updateAllDisplays();
		undo.establishFinalState();
		return undo;
	}

	protected CompoundEdit2 changeColorModes() {
		ChannelUseInstructions ins = presseddisplay.getStack().getChannelUseInstructions();
		if (this.pressedInset!=null) {
			 ins =pressedInset.getPanelManager().getPanelList().getChannelUseInstructions();
			
		}
		CompoundEdit2 undo=new CompoundEdit2();
		undo.addEditToList(new AfterUndoChannel());
		undo.addEditToList(new ChannelUseChangeUndo(ins));
		
		int value = ins.ChannelsInGrayScale;
		if (value==1) {value=0;} else {value=1;}
		ins.ChannelsInGrayScale=value;
		
		
		
		if (workOn==1 && pressedInset==null) for(PanelStackDisplay d: getAllDisplays()) {
			undo.addEditToList(new ChannelUseChangeUndo(d));
			d.getStack().getChannelUseInstructions().ChannelsInGrayScale=value;
		} 
		if(extraDisplays!=null&&pressedInset==null)	for(PanelStackDisplay d:this.extraDisplays) {
			undo.addEditToList(new ChannelUseChangeUndo(d));
			d.getStack().getChannelUseInstructions().ChannelsInGrayScale=value;//this part might be redudent
		}
		undo.addEditToList(new AfterUndoChannel());
		undo.establishFinalState();
		this.updateAllDisplays();
		
		return undo;
				
	}

	protected CompoundEdit2 showDisplayRangeDialog(int type) {
		WindowLevelDialog.showWLDialogs(getChannelEntryList(),  getPrincipalMultiChannel(), this, type , ChannelDisplayUndo.createMany(getAllWrappers(), this));
		return ChannelDisplayUndo.createMany(getAllWrappers(), this);
	}

	


	public MultiChannelWrapper getPrincipalMultiChannel() {
		return this.presseddisplay.getMultichanalWrapper();
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
		if (stackSlicePressed==null && presseddisplay!=null) return  presseddisplay.getMultichanalWrapper().getChannelEntriesInOrder();  
		return this.stackSlicePressed.getChannelEntries();
	}
	
	void updateAllAfterMenuAction() {
		if(presseddisplay!=null)presseddisplay.updatePanels();//.getMultichanalWrapper().updateDisplay();
		if (workOn==1) {
			for(PanelStackDisplay d: getAllDisplays()) {
				d.updatePanels();
			}
		}
		presseddisplay.updateDisplay();
	}

	public FigureOrganizingLayerPane getCurrentOrganizer() {
		if (givenOrganizer!=null) return givenOrganizer;
		if (presseddisplay.getParentLayer() instanceof FigureOrganizingLayerPane) {
			return (FigureOrganizingLayerPane) presseddisplay.getParentLayer();
		}
		return null;
	}
	
	

	@Override
	public void minMaxSet(int chan, double min, double max) {
		// TODO Auto-generated method stub
		
		ArrayList<MultiChannelWrapper> wraps = getAllWrappers() ;
		
		/**The real channel name will be checked against the channel names in each image
		  in the for loop. display ranges will be changed in either those with a match
		  or (if no match), those with the same number*/
		String realName=getPressedWrapper().getRealChannelName(chan);
		
		StatusPanel.updateStatus("Setting Display Range "+realName+" in c= "+chan+" "+min+", "+max);
		
		setDisplayRange(wraps, chan, realName, min, max);
		updateAllDisplaysWithRealChannel( realName);
		presseddisplay.updatePanelsAndLabelsFromSource();
		new CurrentSetInformerBasic().getCurrentlyActiveDisplay().updateDisplay();
	}


	public static void setDisplayRange(ArrayList<MultiChannelWrapper> wraps, int chan, String realName, double min,
			double max) {
		for(MultiChannelWrapper w: wraps) try {
			if (realName!=null &&!realName.equals("null")) {
				chan=getBestMatchToChannel(w, realName, chan);
			} 
			
			w.setChannelMin(chan, min);
			w.setChannelMax(chan, max);
		} catch (Throwable t) {IssueLog.log(t);}
	}
	
	
	
	public void updateAllDisplaysWithRealChannel(String realName) {
		if (realName==null||realName.trim().equals("")) this.updateAllDisplays();
		else {
			for(PanelStackDisplay pd: getAllDisplays() ) {
			pd.updatePanelsWithChannel(realName);
			if (updateInsets) {
				if (this.updateInsets) updateInsetPanels(pd, realName);
			}
			
			}
			
			
		}	
	
	}
	
	
	public void showScaleSettingDialog() {
		localScaleSetter lss = new localScaleSetter(presseddisplay.getMultichanalWrapper(), null);
		lss.showDialog();
	}
	
	public class localScaleSetter extends ScaleSettingDialog {

		public localScaleSetter(HasScaleInfo scaled,
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

	@Override
	public void itemChange(DialogItemChangeEvent event) {
		this.presseddisplay.updatePanels();
		
		this.updateAllDisplays();
		this.updateAllAfterMenuAction();
	}
	
	public ArrayList<JMenuItem> getPopupMenuItems() {	
		
		return new ChannelMatchingMenu();
	}
	
	public class dialogAndAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
			/** for(ChannelEntry c: chans) {
				 double max = sourceDisplayRanges.getChannalMax(sourceDisplayRanges.getIndexOfChannel(c.getRealChannelName()));
				 double min = sourceDisplayRanges.getChannalMin(sourceDisplayRanges.getIndexOfChannel(c.getRealChannelName()));
				 
				for(MultiChannelWrapper i:items) {
					if (i.equals(sourceDisplayRanges)) continue;
					int channum = i.getIndexOfChannel(c.getRealChannelName());
					i.setChannalMax(channum, max);
					i.setChannalMin(channum, min);
					i.updateDisplay();
				}
				
				
			}*/
			 
		}
		
	}
	
	
	/**updates the panels*/
	public void updateAllDisplays() {
		for(PanelStackDisplay pd: getAllDisplays() ) {
			if(pd==null) continue;
			if (this.updateInsets) updateInsetPanels(pd, null);
			pd.updatePanels();
		}
		if(presseddisplay!=null)
		presseddisplay.updatePanelsAndLabelsFromSource();//.updatePanels();//.updateMontageFromSource();
		
	}
	
	public ArrayList<PanelStackDisplay> getAllDisplays() {
		ArrayList<PanelStackDisplay> output = new ArrayList<PanelStackDisplay>();
		if (presseddisplay==null) return output;
		output.add(presseddisplay);
		if (getCurrentOrganizer()!=null&&this.workOn==1) {
			output = new ArrayList<PanelStackDisplay>();
			output.addAll( getCurrentOrganizer().getMultiChannelDisplays());
		}
		if(this.extraDisplays!=null) output.addAll(extraDisplays);
		
		return output;
		
	}
	
public PanelManager getPressedPanelManager() {
		
		PanelManager output = presseddisplay.getPanelManager();
		if (this.pressedInset!=null) {
			output =pressedInset.getPanelManager();
		}
		
		return output;
	}

public void addButtonToMenu(Container pop, String text, String actionCommand) {
	JMenuItem renamer = new JMenuItem(text);
	renamer.setActionCommand(actionCommand);
	renamer.addActionListener(this);
	pop.add(renamer);
	
}



public MultiChannelWrapper getPressedWrapper() {
	//if (clickingOnMultiMode) return m.getMultiChannelWrapper();
	if (presseddisplay==null) {IssueLog.log("You are not clicking on a figure panel"); return null;}
	return presseddisplay.getMultichanalWrapper();
}

/**If the multichannel display is not in a figure organized layer pane,
this returns an array with just a single multichannel Display.
Otherwise returns all the multichannel displays that are directly in the figure organizing
pane*/
public ArrayList<MultiChannelWrapper> getAllWrappers() {
	ArrayList<MultiChannelWrapper> output=new ArrayList<MultiChannelWrapper>();
	output.addAll(presseddisplay.getAllSourceStacks());
	if (presseddisplay.getParentLayer() instanceof FigureOrganizingLayerPane &&this.workOn==1) {
		FigureOrganizingLayerPane pane=(FigureOrganizingLayerPane) presseddisplay.getParentLayer();
		return pane.getAllSourceStacks();
	}
	if(extraWrappers!=null) {
		output.addAll(extraWrappers);
	
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

/**called to update the inset panels*/
private void updateInsetPanels(PanelStackDisplay pd, String name) {
	ArrayList<PanelGraphicInsetDef> insets = getAllInsets(pd);
	for(PanelGraphicInsetDef ins: insets) {
		if(name==null) {
			ins.getPanelManager().updatePanels();
			
		} else
		ins.getPanelManager().updatePanelsWithChannel(name);
	}
}

static ArrayList<PanelGraphicInsetDef> getAllInsets(PanelStackDisplay pd) {
	ArrayList<PanelGraphicInsetDef> out =new ArrayList<PanelGraphicInsetDef>();
	if (pd instanceof GraphicLayer) {
		 GraphicLayer gl=(GraphicLayer) pd;
		 ArrayList<ZoomableGraphic> items = gl.getAllGraphics();
		 for(ZoomableGraphic i : items) {
			 if (i instanceof PanelGraphicInsetDef) {
				 PanelGraphicInsetDef i2=(PanelGraphicInsetDef) i;
				out.add(i2);
			 } 
		 }
	 }
	return out;
	
}




public static PanelGraphicInsetDef findInsetWith(PanelStackDisplay pd, ImagePanelGraphic image) {
	ArrayList<PanelGraphicInsetDef> insets = getAllInsets(pd);
	for(PanelGraphicInsetDef in: insets) {
		if(in.getPanelManager().getPanelList().getPanelGraphics().contains(image)) return in;
		
	}
	return null;
	
}


/**Tries to find the channel index of the channel names realChanName, if it cant, it just returns the chanNum*/
public static int getBestMatchToChannel(MultiChannelWrapper mw, String realChanName, int chanNum) {
	int chan1=mw.getIndexOfChannel(realChanName);
	if (chan1<1||chan1>mw.nChannels()) 
		chan1=chanNum;
	return chan1;
}




 void setTheColor(Color color) {
	Integer chan1 = getSelectedChanNumber();
	setTheColor(color, chan1);
}


public void setTheColor(Color color, Integer chan1) {
	String realName=getPressedWrapper().getRealChannelName(chan1);
	
	
	ArrayList<MultiChannelWrapper> allWrappers = getAllWrappers();
	CompoundEdit2 undo = ChannelDisplayUndo.createMany(allWrappers, this, ChannelDisplayUndo.COLOR_TYPE);
	
	for(MultiChannelWrapper ic: allWrappers) {
		int chan=getBestMatchToChannel(ic, realName, chan1);
		ic.getChannelSwapper().setChannelColor(color, chan);
	}
	updateAllDisplaysWithRealChannel( realName);
	updateAllDisplays();
	try {
		new CurrentSetInformerBasic().getCurrentlyActiveDisplay().updateDisplay();
		new CurrentSetInformerBasic().addUndo(undo);
	} catch (Exception e) {
		IssueLog.log(e);
	}
}


public Integer getSelectedChanNumber() {
	return chanNum;
};  

public class chanColorer implements ColorInputListener {

	int myNum=1;
	public chanColorer(int num) {
		myNum=num;
	}
	@Override
	public void ColorChanged(ColorInputEvent fie) {
		setTheColor(fie.getColor(), myNum);
		
	}
	
	public void addColorMenu(Container b, Container output) {
		 b.add(ColorJMenu.getStandardColorJMenu(this).getComponentAtIndex(0));
		 output.add(b);
	}
	


}
public void addChenEntryColorMenus(Container j, ArrayList<ChannelEntry> iFin) {
	JMenu output = new JMenu("Recolor Channel");
	for(int i=0; i<iFin.size(); i++) {
		String nameRC = iFin.get(i).getRealChannelName();
		if (nameRC==null||nameRC.trim().equals("")) nameRC="Chan "+iFin.get(i).getOriginalChannelIndex();
		chanColorer colorer = new chanColorer(iFin.get(i).getOriginalChannelIndex());
		if (iFin.size()>1)
		colorer.addColorMenu(new JMenu(nameRC),output);
		else colorer.addColorMenu(output,output);
	}
	
	j.add(output);
}


public class AfterUndoChannel extends AbstractUndoableEdit2 {

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
}

}
