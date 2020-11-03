package genericMontageLayoutToolKit;
import genericMontageLayoutToolKit.GeneralLayoutEditorTool;
import gridLayout.MontageSpaces;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;


public class PanelSizeAdjusterTool extends GeneralLayoutEditorTool implements ActionListener, MontageSpaces {
	int mode=1;
	

	@Override
	public
	void performDragEdit(boolean b) {
		if (mode==1) {
			 getEditor().augmentPanelHeight(getCurrentLayout(), getYDisplaceMent() , getRowIndexClick());
			 getEditor().augmentPanelWidth(getCurrentLayout(), getXDisplaceMent() , getColIndexClick());
		}
		
	}
	
	{createIconSet("icons/PanelSizeAdjusterToolIcon.jpg","icons/PanelSizeAdjusterToolIconPressed.jpg","icons/PanelSizeAdjusterToolIconRollover.jpg");}
	


	public void onActionPerformed(Object sour, String st) {
		
	}
	
	public ArrayList<JMenuItem> getPopupMenuItems() {
		//return 	new MontageEditCommandMenu( currentlyInFocusWindowImage().createLayout()).getPanelSizeList();
		return null;
	}
	
	@Override
	public String getToolTip() {
		
			return "Adjust Montage Layout Panel Size";
		}
	
	@Override
	public String getToolName() {
		
			return "Panel Size AdjustMentTool";
		}

}
