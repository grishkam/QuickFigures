package genericMontageLayoutToolKit;
import gridLayout.LayoutSpaces;

import java.util.ArrayList;

import javax.swing.JMenuItem;


public class BorderAdjusterTool extends GeneralLayoutEditorTool implements LayoutSpaces {


	public void performDragEdit(boolean shift) {
		
			
			if (getRowIndexClick() > 1 && getMouseDisplacementY() != 0 && getRowIndexClick() <= getCurrentLayout().nRows()) {
				
				 getEditor().expandBorderY2(getCurrentLayout(), getMouseDisplacementY());
				 
					}
		
			if (getColIndexClick() > 1 && getColIndexClick() <= getCurrentLayout().nColumns() && getMouseDisplacementX() != 0) {
				//IssueLog.log("Attempting to expand border");	
				getEditor().expandBorderX2(getCurrentLayout(), getMouseDisplacementX());
			}

	}
	

		 
	public ArrayList<JMenuItem> getPopupMenuItems() {	
	//IssueLog.log("showing menu for "+currentlyInFocusWindowImage());
		/**return new MontageEditCommandMenu(
				 currentlyInFocusWindowImage().createLayout()
				).getBorderList();*/
		
		return null;
	}


	{createIconSet("icons/Montage_EditorToolIcon.jpg", "icons/Montage_EditorToolIconPressed.jpg", "icons/Montage_EditorToolRollOverIcon.jpg");}
	
	@Override
	public String getToolTip() {
			
			return "Adjust Border Between Panels";
		}
	
	@Override
	public String getToolName() {
			
			return "Adjust Border Between Panels";
		}
	
}
