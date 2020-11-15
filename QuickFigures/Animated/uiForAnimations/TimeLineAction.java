package uiForAnimations;




import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;

public class TimeLineAction  extends BasicMenuItemForObj {
	boolean undo=true;
	
	public TimeLineAction(boolean un) {
		undo=un;
		
		}
	


	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		TimeLineDialog dialog = new TimeLineDialog(diw);
		dialog.showDialog();
	}

	@Override
	public String getCommand() {
		return "Show Timeline";
	}

	@Override
	public String getNameText() {
		return getCommand();
	}

	@Override
	public String getMenuPath() {
		// TODO Auto-generated method stub
		return "Image";
	}

}
