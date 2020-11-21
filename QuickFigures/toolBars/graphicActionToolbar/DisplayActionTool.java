package graphicActionToolbar;



import applicationAdapters.DisplayedImage;
import externalToolBar.ActionToolBlank;
import graphicalObjects.FigureDisplayContainer;

/**An action tool that targets a single object*/
public class DisplayActionTool extends ActionToolBlank<DisplayedImage>{
	CurrentSetInformer setinformer=new CurrentFigureSet();
	
	/**Initializes the tool's icons*/
	public DisplayActionTool(String actionCommand, String iconpath) {
		ActionCommand=actionCommand;
		getIconSet().setIcon(0, "icons3/"+iconpath);
	}
	
	protected void perform(FigureDisplayContainer gc) {
		
	}
	
	@Override
	public void performLoadAction() {
		perform(this.setinformer.getCurrentlyActiveOne());
	}
}
