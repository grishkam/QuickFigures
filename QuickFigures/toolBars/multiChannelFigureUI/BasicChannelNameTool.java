package multiChannelFigureUI;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JPopupMenu;

import channelMerging.MultiChannelDisplayWrapper;
import channelMerging.MultiChannelImage;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;

public class BasicChannelNameTool extends BasicImagePanelTool {
	
	
	 {createIconSet( "icons/NameStackSlicesicon.jpg",
				"icons/NameStackSlicesiconPressed.jpg",
				"icons/NameStackSlicesicon.jpg");	};  
				
				
				protected void afterMousePress(MultiChannelImage mw, int chan1) {
		
		
		if (this.clickingOnMultiMode=true &&getImageDisplayWrapperClick() instanceof MultiChannelDisplayWrapper) {
			 MultiChannelDisplayWrapper m=( MultiChannelDisplayWrapper) getImageDisplayWrapperClick();;
			if (m==null) {IssueLog.log2("are you sure you clicked on the inage m");}
			if (mw==null) {IssueLog.log2("are you sure you clicked on the inage mw");}
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
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals(choseCommand)) {
			
			
			
		}
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
