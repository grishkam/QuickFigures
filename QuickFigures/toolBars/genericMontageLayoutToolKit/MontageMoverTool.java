package genericMontageLayoutToolKit;
import genericMontageLayoutToolKit.GeneralLayoutEditorTool;
import gridLayout.MontageSpaces;
import logging.IssueLog;

public class MontageMoverTool  extends GeneralLayoutEditorTool implements MontageSpaces{



	{createIconSet( "icons/MontageMoverIcon.jpg", "icons/MontageMoverPressedIcon.jpg", "icons/MontageMoverRolloverIcon.jpg");}

	
	public void performDragEdit(boolean shift) {
	try {
			 getEditor().moveMontageLayout(getCurrentLayout(), getMouseDisplacementX(), getMouseDisplacementY());

		} catch (Throwable t) {IssueLog.log(t);}
	}
	

	@Override
	public String getToolTip() {
		
			return "Adjust Montage Layout Position";
		}
	@Override
	public String getToolName() {
			
			return "Move Figure Layout";
		}


	
}