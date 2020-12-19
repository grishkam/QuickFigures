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

import channelMerging.MultiChannelImage;
import menuUtil.SmartPopupJMenu;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;

public class LutSwapperTool extends BasicImagePanelTool implements ColorInputListener {
	
	@Override
	public void applyReleaseActionToMultiChannel(MultiChannelImage mw, int chan1, int chan2) {
		if (mw==null) return;
		mw.getChannelSwapper().swapChannelLuts(chan1, chan2);
	}
	
	{createIconSet( "icons/CrayonTool.jpg",
			"icons/CrayonToolPressed.jpg",
			"icons/CrayonToolRollOver.jpg");	}
	
	@Override
	public void ColorChanged(ColorInputEvent fie) {
		setTheColor(fie.getColor());
	}

	private void setTheColor(Color color) {
		
		for(MultiChannelImage ic: super.getAllWrappers()) {
			int chan=getBestMatchToChannel(ic, getRealNameOfPressedChannel(), getPressChannelOfMultichannel());
			ic.getChannelSwapper().setChannelColor(color, chan);
		}
	
		updateAllDisplays();
		this.getImageClicked().updateDisplay();
	};  
	
	protected void showthePopup(Component source, int x, int y) {
		SmartPopupJMenu pop = new SmartPopupJMenu();
		ChannelColorJMenu men = ChannelColorJMenu.getStandardColorJMenu(this);
		pop.add(men);
		pop.show(source, x, y);
		
		
	}

	
	
	
	@Override
	public String getToolTip() {
			
			return "Change Channel Colors";
		}
}
