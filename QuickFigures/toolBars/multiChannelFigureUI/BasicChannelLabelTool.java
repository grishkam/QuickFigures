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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.MultiChannelImage;
import menuUtil.SmartPopupJMenu;
import objectDialogs.MultiTextGraphicSwingDialog;
import popupMenusForComplexObjects.MenuForChannelLabelMultiChannel;

public class BasicChannelLabelTool extends BasicImagePanelTool implements ActionListener{
	
	
	static String renameCommand="Rename";
	static String renameCommand2="RelabelChannels";
	static String alterAll="Alter labels Channels";
	
	
	 {createIconSet( "icons/NameStackSlicesicon.jpg",
				"icons/NameStackSlicesiconPressed.jpg",
				"icons/NameStackSlicesicon.jpg");	};  

				
		/**If called after a mouse press on a multichannel. Does not get called when a popup menu is triggered*/
		protected void afterMousePress(MultiChannelImage mw, int chan1) {
					
					
		ChannelLabelManager lm=getPressedChannelLabelManager();
		
		
		if (stackSlicePressed==null) return; 
		
		if (stackSlicePressed.isTheMerge() && !shiftDown()) {
			lm.showChannelLabelPropDialog();
			
		} else lm.nameChannels(stackSlicePressed.getChannelEntries());
		
	}
	
	
	
	
	
	public JPopupMenu createJPopup() {
		
		MenuForChannelLabelMultiChannel menu = new MenuForChannelLabelMultiChannel("All Channel Labels", presseddisplay, this.getPressedPanelManager().getPanelList(), this.getPressedChannelLabelManager());
		JPopupMenu output = new SmartPopupJMenu();
		
		addChanlabelsMenuItems(output);
		
		 output.add(menu);
		return output;
	}





	protected void addChanlabelsMenuItems(Container output) {
		addButtonToMenu(output, "Alter Label Text", renameCommand2);
		
		addButtonToMenu(output, "Edit All Channel Labels", alterAll);
		
		addButtonToMenu(output, "Rename Image Channel/Stack Slice", renameCommand);
	}
	
	
	public void applyReleaseActionToMultiChannel(MultiChannelImage mw) {
		
		applyReleaseActionToMultiChannel(mw, 1,1);
	}
	
	protected void showthePopup(Component source, int x, int y) {
		createJPopup().show(source, x, y);
		
	}
	
	@Override
	public String getToolTip() {
	
			return "Change Channel Labels and Slice Labels";
		}
	
	


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals(renameCommand)) {
		
		new StackSliceNamingDialog().showNamingDialog(stackSlicePressed.originalIndices, this.presseddisplay.getMultiChannelImage());
		//presseddisplay.updatePanels();
		
		}
		
		if (arg0.getActionCommand().equals(renameCommand2)) {
			
			ChannelLabelManager lm=getPressedChannelLabelManager();
			lm.nameChannels(stackSlicePressed.getChannelEntries());
		
				
			
			}
		
		
if (arg0.getActionCommand().equals(alterAll)) {
			
			ArrayList<ChannelLabelTextGraphic> labels = this.getPressedPanelManager().getPanelList().getChannelLabels();
			MultiTextGraphicSwingDialog mt = new MultiTextGraphicSwingDialog( labels, true);
			mt.showDialog();
			
			}
	}
	
	
}
