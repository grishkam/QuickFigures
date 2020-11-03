package multiChannelFigureUI;

import java.awt.Color;
import java.awt.Component;

import channelMerging.MultiChannelWrapper;
import specialMenus.ColorJMenu;
import standardDialog.ColorInputEvent;
import standardDialog.ColorInputListener;

public class LutSwapperTool extends BasicImagePanelTool implements ColorInputListener {
	
	@Override
	public void applyReleaseActionToMultiChannel(MultiChannelWrapper mw, int chan1, int chan2) {
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
		
		for(MultiChannelWrapper ic: super.getAllWrappers()) {
			int chan=getBestMatchToChannel(ic, getRealNameOfPressedChannel(), getPressChannelOfMultichannel());
			ic.getChannelSwapper().setChannelColor(color, chan);
		}
	
		updateAllDisplays();
		this.getImageWrapperClick().updateDisplay();
	};  
	
	protected void showthePopup(Component source, int x, int y) {
		ColorJMenu.getStandardColorJMenu(this).show(source, x,y);
	}

	
	
	
	@Override
	public String getToolTip() {
			
			return "Change Channel Colors";
		}
}
