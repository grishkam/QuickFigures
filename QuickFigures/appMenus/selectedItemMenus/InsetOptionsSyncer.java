package selectedItemMenus;

import javax.swing.Icon;

import graphicalObjects_BasicShapes.TextGraphic;
import objectDialogs.TextInsetsDialog;

public class InsetOptionsSyncer extends BasicMultiSelectionOperator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Set Insets";
	}
	

	@Override
	public void run() {
		TextInsetsDialog mt = new TextInsetsDialog(getAllArray(), true);
		mt.showDialog();

	}
	
	public Icon getIcon() {
		return TextGraphic.createImageIcon();
	}
	
	public String getMenuPath() {
		
		return "Text";
	}

}
