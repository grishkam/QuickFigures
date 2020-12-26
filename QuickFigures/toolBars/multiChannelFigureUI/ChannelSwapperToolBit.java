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
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import advancedChannelUseGUI.PanelListDisplayGUI;
import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import channelMerging.MultiChannelImage;
import figureEditDialogs.PanelStackDisplayOptions;
import genericMontageLayoutToolKit.GeneralLayoutToolIcon;
import layout.PanelLayout;
import layout.plasticPanels.PlasticPanelLayout;
import menuUtil.SmartPopupJMenu;
import popupMenusForComplexObjects.ScaleFigureDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import undo.ChannelDisplayUndo;

/**A tool for reordering the channels of the source images*/
public class ChannelSwapperToolBit extends BasicImagePanelTool implements ActionListener, DisplayRangeChangeListener, StandardDialogListener {
	
	static final String SCALING_COMMAND="Scale", COLOR_MODE_COMMAND="ColorMode";
	static final String CHANNEL_USE_COMMAND="Channel Use", RECREATE_PANELS_COMMAND="recreate";
	static final String DISPLAY_RANGE_COMMAND="MinMax", DISPLAY_RANGE_COMMAND_2="WinLev";
	static final String ORDER_AND_COLOR_COMMAND="order and luts", ORDER_DISPLAY_RANGE_AND_COLOR="Min, Max, order and luts";
	static final String SCALE_PANELS_COMMAND="Scale of Panels",
			PANEL_CONTENT_GUI_SHOWING_COMMAND="Panel Content Gui",
			AUTO_COLOR_COMMAND="Fix Colors",
			RENAME_CHANNEL_COMMAND="rename channel command";
	
	
	public JPopupMenu createJPopup() {
		JPopupMenu output = new SmartPopupJMenu();
		 
		 addButtonToMenu(output, "Window/Level", DISPLAY_RANGE_COMMAND_2);
		 addButtonToMenu(output, "Min/Max", DISPLAY_RANGE_COMMAND);
		 addButtonToMenu(output, "Set Units For Scale", SCALING_COMMAND);
		 addButtonToMenu(output, "Set Bilinear Scaling", SCALE_PANELS_COMMAND);
		 addButtonToMenu(output, "Change Color Modes ", COLOR_MODE_COMMAND);
	
		 
		
		 addButtonToMenu(output, "Match Channel Order and LUT Colors", ORDER_AND_COLOR_COMMAND);
		 addButtonToMenu(output, "Match Min, Max, Channel Order and LUT Colors", ORDER_DISPLAY_RANGE_AND_COLOR);
		 
		 addButtonToMenu(output, "Recreate Panels", RECREATE_PANELS_COMMAND);
		 addButtonToMenu(output, "Channel Use Options", CHANNEL_USE_COMMAND);
		 addButtonToMenu(output, "Advanced Channel Use", PANEL_CONTENT_GUI_SHOWING_COMMAND);
		 addButtonToMenu(output, "Recolor Channels Automatically", AUTO_COLOR_COMMAND);
		 addButtonToMenu(output, "Reset Channel Names", RENAME_CHANNEL_COMMAND);
		
		return output;
	}
	
	
	public void applyReleaseActionToMultiChannel(MultiChannelImage mw) {
			mw.getChannelSwapper().swapChannelsOfImage(getPressChannelOfMultichannel(), getReleaseChannelOfMultichannel());	
	}
	
	protected void showthePopup(Component source, int x, int y) {
		createJPopup().show(source, x, y);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
	
		
		if (arg0.getActionCommand().equals(DISPLAY_RANGE_COMMAND)) {
			WindowLevelDialog.showWLDialogs(this.stackSlicePressed.getChannelEntries(),  this.presseddisplay.getMultiChannelImage(), this, WindowLevelDialog.MIN_MAX , ChannelDisplayUndo.createMany(this.getAllWrappers(), this));
			
		}
		if (arg0.getActionCommand().equals(DISPLAY_RANGE_COMMAND_2)) {
			
			WindowLevelDialog.showWLDialogs(this.stackSlicePressed.getChannelEntries(),  this.presseddisplay.getMultiChannelImage(), this, WindowLevelDialog.WINDOW_LEVEL, ChannelDisplayUndo.createMany(this.getAllWrappers(), this) );
			
		}

		
		if(arg0.getActionCommand().equals(SCALE_PANELS_COMMAND)) {
			if (this.pressedInset!=null) {
				pressedInset.getMenuSupplier().showScaleDialog();
				return;
			}
			ScaleFigureDialog dialog = new popupMenusForComplexObjects.ScaleFigureDialog(this.getCurrentOrganizer().getMontageLayoutGraphic(), this.presseddisplay.getPanelManager()); ;
			
			dialog.setAdditionalPanelManagers(getAllDisplays());
			
			dialog.showDialog();
			}
		
		
		
		if (arg0.getActionCommand().equals(COLOR_MODE_COMMAND)) {
			ChannelUseInstructions ins = presseddisplay.getPanelList().getChannelUseInstructions();
			if (this.pressedInset!=null) {
				 ins =pressedInset.getPanelManager().getPanelList().getChannelUseInstructions();
				
			}
			
			int value = ins.channelColorMode;
			if (value==1) {value=0;} else {value=1;}
			ins.channelColorMode=value;
			
			if (workOn==ALL_IMAGES_IN_FIGURE && pressedInset==null) for(ImageDisplayLayer d: super.getAllDisplays()) {
				d.getPanelList().getChannelUseInstructions().channelColorMode=value;
			}
			this.updateAllDisplays();
					;
		}
		
if (	arg0.getActionCommand().equals(AUTO_COLOR_COMMAND)) {
		for(MultiChannelImage p: getAllWrappers())
		p.colorBasedOnRealChannelName();
			this.updateAllDisplays();
		}

if (	arg0.getActionCommand().equals(RENAME_CHANNEL_COMMAND)) {
	presseddisplay.getMultiChannelImage().renameBasedOnRealChannelName();
	this.updateAllDisplays();
}
		
		
		if (arg0.getActionCommand().equals(CHANNEL_USE_COMMAND)) {
			
			
			
			if (pressedInset!=null) {new PanelStackDisplayOptions(presseddisplay,pressedInset.getPanelManager().getPanelList(),pressedInset.getPanelManager(), false).showDialog();;}
			else
			if (workOn==SELECTED_IMAGE_ONLY) {
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
		
		
		
		if (arg0.getActionCommand().equals(RECREATE_PANELS_COMMAND)) {
			PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(this.presseddisplay, presseddisplay.getPanelList(),null, true);
			
			dialog.addAditionalDisplays(this.getAllDisplays());
			dialog.setCurrentImageDisplay(this.getImageDisplayWrapperClick());
			dialog.setModal(false);
			dialog.showDialog();
		}
		if (arg0.getActionCommand().equals(ORDER_AND_COLOR_COMMAND)) {
			workOn=ALL_IMAGES_IN_FIGURE;
			new ChannelOrderAndLutMatching().matchChannels(this.getPressedWrapper(), this.getAllWrappers(), 2);
			
		}
		
		if (arg0.getActionCommand().equals(ORDER_DISPLAY_RANGE_AND_COLOR)) {
			workOn=ALL_IMAGES_IN_FIGURE;
			new ChannelOrderAndLutMatching().matchChannels(this.getPressedWrapper(), this.getAllWrappers(), 2);
			for(int c=1; c<=this.getPressedWrapper().nChannels(); c++) {
				minMaxSet(c, getPressedWrapper().getChannelMin(c),getPressedWrapper().getChannelMax(c));
			}
		}
		
		if (arg0.getActionCommand().equals(PANEL_CONTENT_GUI_SHOWING_COMMAND)) {
			PanelListDisplayGUI distpla = new PanelListDisplayGUI(  getPressedPanelManager(), this.getPressedChannelLabelManager());
			distpla.setVisible(true);
		}
	
		
		 updateAllAfterMenuAction();
		
	}
	

	
	
	
	/**called after the display range for the given channel number is set*/
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
	
	
	

	
	
	


	@Override
	public void itemChange(DialogItemChangeEvent event) {
		this.presseddisplay.updatePanels();
		
		this.updateAllDisplays();
		this.updateAllAfterMenuAction();
	}

	
	@Override
	public String getToolTip() {
			return "Swap Channels and Edit Channel Properties";
		}
	
	{this.setIconSet(new  SwapperIcon(0).generateIconSet());}
	public static class SwapperIcon extends GeneralLayoutToolIcon {

		/**
		 * @param type
		 */
		public SwapperIcon(int type) {
			super(type);
			super.paintBoundry=false;
			super.panelColor=new Color[] {Color.red, Color.green, Color.blue};
			if(type!=NORMAL_ICON_TYPE) {
				super.panelColor=new Color[] {Color.blue, Color.green, Color.red};
			}
		}
		
		/**
		creates a layout for drawing and icon
		 */
		protected PanelLayout createSimpleIconLayout( int type) {
			int xLoc=3;
			int yLoc=3;
			int size=11;
			Rectangle r1 = new Rectangle(xLoc, yLoc, size, size);
			Rectangle r2 = new Rectangle(xLoc+3, yLoc+ 4, size, size);
			Rectangle r3 = new Rectangle(xLoc+6, yLoc+8, size, size);
			
			PlasticPanelLayout layout2 = new PlasticPanelLayout(r1, r2, r3);
			
			return layout2;
		}
		
		/**
		 alters the color for the stroke of the panels
		 */
		protected Color derivePanelStrokeColor(Color panelColor2) {
			return panelColor2.darker().darker();
		}
		
		/**given the base color of a panel, returns the fill color used to give the panel a light tint
		 * @param panelColor2
		 * @return
		 */
		protected Color deriveFillColor(Color panelColor2) {
			return panelColor2;
		}
		
		/**
		 * @param type
		 * @return
		 */
		@Override
		protected
		GeneralLayoutToolIcon generateAnother(int type) {
			return new SwapperIcon(type);
		}
		
		public GeneralLayoutToolIcon copy(int type) {
			GeneralLayoutToolIcon another = generateAnother(type);
			return another;
		}
	}
	
	

}
