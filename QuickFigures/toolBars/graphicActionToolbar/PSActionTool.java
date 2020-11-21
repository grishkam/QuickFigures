package graphicActionToolbar;

import graphicalObjects.FigureDisplayContainer;
import illustratorScripts.AdobeScriptMaker;
import illustratorScripts.ZIllustratorScriptGenerator;

public class PSActionTool extends DisplayActionTool {
	AdobeScriptMaker sm=	new AdobeScriptMaker();

	public PSActionTool() {
		super("SendTOIL", "PSILicon.jpg");
		// TODO Auto-generated constructor stub
	}

	
	
	
	protected void perform(FigureDisplayContainer graphic) {
		if (graphic!=null) {
			
		sm.sendWrapperToills(graphic.getAsWrapper(), true);
		 ZIllustratorScriptGenerator.instance.execute();
			
		}
	}
	

	@Override
	public String getToolTip() {
			return "Create Figure using Illustrator Script";
		}
	
}
