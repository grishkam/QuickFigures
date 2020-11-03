package graphicActionToombar;



import applicationAdapters.DisplayedImageWrapper;
import externalToolBar.ActionToolBlank;
import graphicalObjects.GraphicSetDisplayContainer;

public class DisplayActionTool extends ActionToolBlank<DisplayedImageWrapper>{
	CurrentSetInformer setinformer=new CurrentSetInformerBasic();
	
	public DisplayActionTool(String actionCommand, String iconpath) {
		ActionCommand=actionCommand;
		getIconSet().setIcon(0, "icons3/"+iconpath);
	}
	
	protected void perform(GraphicSetDisplayContainer gc) {
		
	}
	
	@Override
	public void performLoadAction() {
		perform(this.setinformer.getCurrentlyActiveOne());
	}
}
