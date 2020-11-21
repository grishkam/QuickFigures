package graphicActionToolbar;

import graphicalObjects.FigureDisplayContainer;
import layersGUI.GraphicTreeUI;

public class TreeActionTool extends DisplayActionTool {

	public TreeActionTool() {
		super("show tree", "TreeIconBeta.jpg");
		// TODO Auto-generated constructor stub
	}
	
	protected void perform(FigureDisplayContainer graphic) {
		if (graphic!=null) {
			new GraphicTreeUI(graphic).showTreeForLayerSet(graphic) ;
		}
	}
	
	@Override
	public String getToolTip() {
			return "Show Layers";
		}
	

}
