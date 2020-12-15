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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import advancedChannelUseGUI.PanelListDisplayGUI;
import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelImage;
import figureEditDialogs.PanelStackDisplayOptions;
import channelMerging.ImageDisplayLayer;
import menuUtil.SmartPopupJMenu;
import popupMenusForComplexObjects.ScaleFigureDialog;
import sUnsortedDialogs.ScaleSettingDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import undo.ChannelDisplayUndo;
import applicationAdapters.HasScaleInfo;

public class ChannelSwapperToolBit extends BasicImagePanelTool implements ActionListener, DisplayRangeChangeListener, StandardDialogListener {
	
	
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
		 addButtonToMenu(output, "Change Color Modes ", colorModeCommand);
	
		 
		
		 addButtonToMenu(output, "Match Channel Order and LUT Colors", orderCommand);
		 addButtonToMenu(output, "Match Min, Max, Channel Order and LUT Colors", orderCommand2);
		 
		 addButtonToMenu(output, "Recreate Panels", panCreateCommand);
		 addButtonToMenu(output, "Channel Use Options", chanUseCommand);
		 addButtonToMenu(output, "Advanced Channel Use", panContentCommand);
		 addButtonToMenu(output, "Recolor Channels Automatically", colorRecolorCommand);
		 addButtonToMenu(output, "Reset Channel Names", renameChanCommand);
		 
		//PanelMenuForMultiChannel allPanels = new PanelMenuForMultiChannel("All Panels", presseddisplay,  getPressedPanelManager().getPanelList(), this.getPressedPanelManager());
		//output.add(allPanels); 
		
		return output;
	}
	

	  {createIconSet( "icons/ChannelSwapperToolIcon.jpg",
			"icons/ChannelSwapperToolIconPressed.jpg",
			"icons/ChannelSwapperRollOverIcon.jpg");	};  
	
	
	public void applyReleaseActionToMultiChannel(MultiChannelImage mw) {
		if (swapMode==0)
			mw.getChannelSwapper().swapChannelsOfImage(getPressChannelOfMultichannel(), getReleaseChannelOfMultichannel());
	
		
	}
	
	protected void showthePopup(Component source, int x, int y) {
		createJPopup().show(source, x, y);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
	
		
		if (arg0.getActionCommand().equals(minMaxCommand)) {
			WindowLevelDialog.showWLDialogs(this.stackSlicePressed.getChannelEntries(),  this.presseddisplay.getMultiChannelImage(), this, WindowLevelDialog.MIN_MAX , ChannelDisplayUndo.createMany(this.getAllWrappers(), this));
			
		}
		if (arg0.getActionCommand().equals(WLCommand)) {
			
			WindowLevelDialog.showWLDialogs(this.stackSlicePressed.getChannelEntries(),  this.presseddisplay.getMultiChannelImage(), this, WindowLevelDialog.WINDOW_LEVEL, ChannelDisplayUndo.createMany(this.getAllWrappers(), this) );
			
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
			ChannelUseInstructions ins = presseddisplay.getPanelList().getChannelUseInstructions();
			if (this.pressedInset!=null) {
				 ins =pressedInset.getPanelManager().getPanelList().getChannelUseInstructions();
				
			}
			
			int value = ins.channelColorMode;
			if (value==1) {value=0;} else {value=1;}
			ins.channelColorMode=value;
			
			if (workOn==1 && pressedInset==null) for(ImageDisplayLayer d: super.getAllDisplays()) {
				d.getPanelList().getChannelUseInstructions().channelColorMode=value;
			}
			this.updateAllDisplays();
					;
		}
		
if (	arg0.getActionCommand().equals(colorRecolorCommand)) {
		for(MultiChannelImage p: getAllWrappers())
		p.colorBasedOnRealChannelName();
			this.updateAllDisplays();
		}

if (	arg0.getActionCommand().equals(renameChanCommand)) {
	presseddisplay.getMultiChannelImage().renameBasedOnRealChannelName();
	this.updateAllDisplays();
}
		
		
		if (arg0.getActionCommand().equals(chanUseCommand)) {
			
			
			
			if (pressedInset!=null) {new PanelStackDisplayOptions(presseddisplay,pressedInset.getPanelManager().getPanelList(),pressedInset.getPanelManager(), false).showDialog();;}
			else
			if (workOn==0) {
				if (pressedInset==null)
				presseddisplay.showStackOptionsDialog();
				
				
			} else {
				
				PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(presseddisplay,presseddisplay.getPanelList(),null, false);
				
				/**adds a list of all the channel displays that are relevant*/
				ArrayList<ImageDisplayLayer> all = super.getAllDisplays();
				all.remove(presseddisplay);
				dialog.addAditionalDisplays(all);
				
				dialog.showDialog();
				
				
			}
		}
		
		
		
		if (arg0.getActionCommand().equals(panCreateCommand)) {
			PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(this.presseddisplay, presseddisplay.getPanelList(),null, true);
			
			dialog.addAditionalDisplays(this.getAllDisplays());
			dialog.setCurrentImageDisplay(this.getImageDisplayWrapperClick());
			dialog.setModal(false);
			dialog.showDialog();
		}
		if (arg0.getActionCommand().equals(orderCommand)) {
			workOn=1;
			new ChannelOrderAndLutMatching().matchChannels(this.getPressedWrapper(), this.getAllWrappers(), 2);
			
		}
		
		if (arg0.getActionCommand().equals(orderCommand2)) {
			workOn=1;
			new ChannelOrderAndLutMatching().matchChannels(this.getPressedWrapper(), this.getAllWrappers(), 2);
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
		
	}
	

	
	
	

	@Override
	public void minMaxSet(int chan, double min, double max) {
		ArrayList<MultiChannelImage> wraps = getAllWrappers() ;
		
		/**The real channel name will be checked against the channel names in each image
		  in the for loop. display ranges will be changed in either those with a match
		  or (if no match), those with the same number*/
		String realName=getPressedWrapper().getRealChannelName(chan);
		ChannelPanelEditingMenu.setDisplayRange(wraps, chan, realName, min, max);
		updateAllDisplaysWithRealChannel( realName);
		getImageClicked().updateDisplay();
	}
	
	
	

	
	
	public void showScaleSettingDialog() {
		localScaleSetter lss = new localScaleSetter(presseddisplay.getMultiChannelImage(), null);
		lss.showDialog();
	}
	
	public class localScaleSetter extends ScaleSettingDialog {

		public localScaleSetter(HasScaleInfo scaled,
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
