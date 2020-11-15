package addObjectMenus;

import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_LayoutObjects.ObjectDefinedLayoutGraphic;

public class ImagePanelLayoutAdder extends PlasticLayoutAdder {
	public PanelLayoutGraphic getNewLayout() {
		return  new ObjectDefinedLayoutGraphic();
	}
	
	
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "Add Image Panel Dependent Layout";
	}

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Add Image Panel Dependent Layout";
	}
}
