package selectedItemMenus;

import javax.swing.Icon;

import graphicalObjects_BasicShapes.TextGraphic;
import objectDialogs.ShapeGraphicOptionsSwingDialog;

public class TextBackGroundOptionsSyncer extends BasicMultiSelectionOperator{

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Set Text Background Options";
	}
	

	@Override
	public void run() {
		ShapeGraphicOptionsSwingDialog mt = new ShapeGraphicOptionsSwingDialog(getAllArray(), true);
		mt.showDialog();

	}
	
	
	public Icon getIcon() {
		return TextGraphic.createImageIcon();
	}
	
	public String getMenuPath() {
		
		return "Text";
	}

}
