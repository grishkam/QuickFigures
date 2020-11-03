package selectedItemMenus;

import java.awt.Component;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import menuUtil.SmartPopupJMenu;

public interface MultiSelectionOperator extends Serializable {
	
	public String getMenuCommand();
	public void setSelection(ArrayList<ZoomableGraphic> array) ;
	public void run();
	//public void setDisPlay(GraphicSetDisplayContainer cont);
	
	public Icon getIcon();
	public void setSelector(LayerSelector graphicTreeUI);
	
	/**if the item has a special menu path, this returns a string to help find it*/
	public String getMenuPath();
	
	public boolean canUseObjects(LayerSelector graphicTreeUI);
	
	public boolean isValidForLayerSelector(LayerSelector graphicTreeUI);
	public Font getMenuItemFont();
	
	public Component getInputPanel() ;
}
