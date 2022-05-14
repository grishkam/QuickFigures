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
import javax.swing.JPopupMenu;

import channelMerging.MultiChannelDisplayWrapper;
import channelMerging.MultiChannelImage;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import multiChannelFigureUI.BasicChannelLabelTool.ChanelLabelIcon;

/**A tool for chaning the stack slice names of the original image*/
public class BasicChannelNameTool extends BasicImagePanelTool {
	
	
	{this.setIconSet(new  ChanelLabelIcon(0).generateIconSet());}
				
				
	protected void afterMousePress(MultiChannelImage mw, int chan1) {
		
		
		if (this.clickingOnMultiMode=true &&getImageDisplayWrapperClick() instanceof MultiChannelDisplayWrapper) {
			 MultiChannelDisplayWrapper m=( MultiChannelDisplayWrapper) getImageDisplayWrapperClick();;
			if (m==null) {IssueLog.log("are you sure you clicked on the inage m");}
			if (mw==null) {IssueLog.log("are you sure you clicked on the inage mw");}
			 new StackSliceNamingDialog().showNamingDialog(m.getContainedMultiChannel().getStackIndex(m.getCurrentChannel(), m.getCurrentSlice(),m.getCurrentFrame()), m.getContainedMultiChannel());
			}
		else {
			 new StackSliceNamingDialog().showNamingDialog(stackSlicePressed.originalIndices, this.presseddisplay.getMultiChannelImage());
			
		}
		
	}
	
	
	
	private static String choseCommand="Select MultiChannel";
	
	
	public JPopupMenu createJPopup() {
		JPopupMenu output = new SmartPopupJMenu();
		 addButtonToMenu(output, "Match DisplayRanges", choseCommand);
		
		return output;
	}
	
	
	
	public void applyReleaseActionToMultiChannel(MultiChannelImage mw) {
		
		applyReleaseActionToMultiChannel(mw, 1,1);
	}
	
	protected void showthePopup(Component source, int x, int y) {
		createJPopup().show(source, x, y);
		
	}
	
	@Override
	public String getToolTip() {
	
			return "Name Channels and Stack Slices";
		}
}
