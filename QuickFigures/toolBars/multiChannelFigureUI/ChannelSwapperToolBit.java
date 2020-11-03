package multiChannelFigureUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelWrapper;
import channelMerging.PanelStackDisplay;
import menuUtil.SmartPopupJMenu;
import objectDialogs.PanelStackDisplayOptions;
import panelGUI.PanelListDisplayGUI;
import popupMenusForComplexObjects.PanelMenuForMultiChannel;
import popupMenusForComplexObjects.ScaleFigureDialog;
import sUnsortedDialogs.ScaleSettingDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.SwingDialogListener;
import undo.ChannelDisplayUndo;
import applicationAdapters.HasScaleInfo;

public class ChannelSwapperToolBit extends BasicImagePanelTool implements ActionListener, DisplayRangeChangeListener, SwingDialogListener {
	
	
	int swapMode=0;
	
	
	static String scalingCommand="Scale";
	static String colorModeCommand="ColorMode";
	static String chanUseCommand="Channel Use";
	static String panCreateCommand="recreate";
	static String minMaxCommand="MinMax";
	static String WLCommand="WinLev";
	private static String orderCommand="order and luts";
	private static String orderCommand2="Min, Max, order and luts";
	private String scalingCommand2="Scale of Panels";
	private String panContentCommand="Panel Content Gui";
	private String colorRecolorCommand="Fix Colors";


	private String renameChanCommand="Add Channel exposures to summary";
	
	
	public JPopupMenu createJPopup() {
		JPopupMenu output = new SmartPopupJMenu();
		 
		 addButtonToMenu(output, "Window/Level", WLCommand);
		 addButtonToMenu(output, "Min/Max", minMaxCommand);
		 addButtonToMenu(output, "Set Units For Scale", scalingCommand);
		 addButtonToMenu(output, "Set Bilinear Scaling", scalingCommand2);
		 addButtonToMenu(output, "Change Color Modes", colorModeCommand);
	
		 
		
		 addButtonToMenu(output, "Match Channel Order and LUT Colors", orderCommand);
		 addButtonToMenu(output, "Match Min, Max, Channel Order and LUT Colors", orderCommand2);
		 
		 addButtonToMenu(output, "Recreate Panels", panCreateCommand);
		 addButtonToMenu(output, "Channel Use Options", chanUseCommand);
		 addButtonToMenu(output, "Advanced Channel Use", panContentCommand);
		 addButtonToMenu(output, "Recolor Channels Automatically", colorRecolorCommand);
		 addButtonToMenu(output, "Reset Channel Names", renameChanCommand);
		 
		PanelMenuForMultiChannel allPanels = new PanelMenuForMultiChannel("All Panels", presseddisplay,  getPressedPanelManager().getStack(), this.getPressedPanelManager());
		//output.add(allPanels); 
		
		return output;
	}
	

	  {createIconSet( "icons/ChannelSwapperToolIcon.jpg",
			"icons/ChannelSwapperToolIconPressed.jpg",
			"icons/ChannelSwapperRollOverIcon.jpg");	};  
	
	
	public void applyReleaseActionToMultiChannel(MultiChannelWrapper mw) {
		if (swapMode==0)
			mw.getChannelSwapper().swapChannelsOfImage(getPressChannelOfMultichannel(), getReleaseChannelOfMultichannel());
	
		
	}
	
	protected void showthePopup(Component source, int x, int y) {
		createJPopup().show(source, x, y);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
	
		
		if (arg0.getActionCommand().equals(minMaxCommand)) {
			WindowLevelDialog.showWLDialogs(this.stackSlicePressed.getChannelEntries(),  this.presseddisplay.getMultichanalWrapper(), this, WindowLevelDialog.MIN_MAX , ChannelDisplayUndo.createMany(this.getAllWrappers(), this));
			
		}
		if (arg0.getActionCommand().equals(WLCommand)) {
			
			WindowLevelDialog.showWLDialogs(this.stackSlicePressed.getChannelEntries(),  this.presseddisplay.getMultichanalWrapper(), this, WindowLevelDialog.WINDOW_LEVEL, ChannelDisplayUndo.createMany(this.getAllWrappers(), this) );
			
		}
		
		if (arg0.getActionCommand().equals(scalingCommand)) {
			 showScaleSettingDialog() ;
		}
		
		if(arg0.getActionCommand().equals(scalingCommand2)) {
			if (this.pressedInset!=null) {
				pressedInset.getMenuSupplier().showScaleDialog();
				return;
			}
			ScaleFigureDialog dialog = new popupMenusForComplexObjects.ScaleFigureDialog(this.getCurrentOrganizer().getMontageLayoutGraphic(), this.presseddisplay.getPanelManager()); ;
			
			dialog.setAdditionalPanelManagers(getAllDisplays());
			
			dialog.showDialog();
			}
		
		
		
		if (arg0.getActionCommand().equals(colorModeCommand)) {
			ChannelUseInstructions ins = presseddisplay.getStack().getChannelUseInstructions();
			if (this.pressedInset!=null) {
				 ins =pressedInset.getPanelManager().getStack().getChannelUseInstructions();
				
			}
			
			int value = ins.ChannelsInGrayScale;
			if (value==1) {value=0;} else {value=1;}
			ins.ChannelsInGrayScale=value;
			
			if (workOn==1 && pressedInset==null) for(PanelStackDisplay d: super.getAllDisplays()) {
				d.getStack().getChannelUseInstructions().ChannelsInGrayScale=value;
			}
			this.updateAllDisplays();
					;
		}
		
if (	arg0.getActionCommand().equals(colorRecolorCommand)) {
		for(MultiChannelWrapper p: getAllWrappers())
		p.colorBasedOnRealChannelName();
			this.updateAllDisplays();
		}

if (	arg0.getActionCommand().equals(renameChanCommand)) {
	presseddisplay.getMultichanalWrapper().renameBasedOnRealChannelName();
	this.updateAllDisplays();
}
		
		
		if (arg0.getActionCommand().equals(chanUseCommand)) {
			
			
			
			if (pressedInset!=null) {new PanelStackDisplayOptions(presseddisplay,pressedInset.getPanelManager().getStack(),pressedInset.getPanelManager(), false).showDialog();;}
			else
			if (workOn==0) {
				if (pressedInset==null)
				presseddisplay.showStackOptionsDialog();
				
				
			} else {
				
				PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(presseddisplay,presseddisplay.getStack(),null, false);
				
				/**adds a list of all the channel displays that are relevant*/
				ArrayList<PanelStackDisplay> all = super.getAllDisplays();
				all.remove(presseddisplay);
				dialog.addAditionalDisplays(all);
				
				dialog.showDialog();
				
				
			}
		}
		
		
		
		if (arg0.getActionCommand().equals(panCreateCommand)) {
			PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(this.presseddisplay, presseddisplay.getStack(),null, true);
			
			dialog.addAditionalDisplays(this.getAllDisplays());
			dialog.setCurrentImageDisplay(this.getImageDisplayWrapperClick());
			dialog.setModal(false);
			dialog.showDialog();
		}
		if (arg0.getActionCommand().equals(orderCommand)) {
			workOn=1;
			new ChannelOrderAndLutMatching().matchOrder(this.getPressedWrapper(), this.getAllWrappers(), 2);
			
		}
		
		if (arg0.getActionCommand().equals(orderCommand2)) {
			workOn=1;
			new ChannelOrderAndLutMatching().matchOrder(this.getPressedWrapper(), this.getAllWrappers(), 2);
			for(int c=1; c<=this.getPressedWrapper().nChannels(); c++) {
				minMaxSet(c, getPressedWrapper().getChannalMin(c),getPressedWrapper().getChannalMax(c));
			}
		}
		
		if (arg0.getActionCommand().equals(panContentCommand)) {
			PanelListDisplayGUI distpla = new PanelListDisplayGUI(  getPressedPanelManager(), this.getPressedChannelLabelManager());
			//getPressedPanelManager().getStack().setChannelUpdateMode(true);
			distpla.setVisible(true);
		}
	
		
		 updateAllAfterMenuAction();
		
	}
	

	
	
	

	@Override
	public void minMaxSet(int chan, double min, double max) {
		ArrayList<MultiChannelWrapper> wraps = getAllWrappers() ;
		
		/**The real channel name will be checked against the channel names in each image
		  in the for loop. display ranges will be changed in either those with a match
		  or (if no match), those with the same number*/
		String realName=getPressedWrapper().getRealChannelName(chan);
		ChannelSwapperToolBit2.setDisplayRange(wraps, chan, realName, min, max);
		updateAllDisplaysWithRealChannel( realName);
		getImageWrapperClick().updateDisplay();
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
	/**
	public ArrayList<JMenuItem> getPopupMenuItems() {	
		
		return new ChannelMatchingMenu();
	}*/
	
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
	
	@Override
	public String getToolTip() {
			return "Swap Channels and Edit Channel Properties";
		}
	
	

}
