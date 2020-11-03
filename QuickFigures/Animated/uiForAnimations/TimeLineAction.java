package uiForAnimations;




import applicationAdapters.DisplayedImageWrapper;
import basicMenusForApp.BasicMenuItemForObj;

public class TimeLineAction  extends BasicMenuItemForObj {
	boolean undo=true;
	
	public TimeLineAction(boolean un) {
		undo=un;
		
		}
	


	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
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
