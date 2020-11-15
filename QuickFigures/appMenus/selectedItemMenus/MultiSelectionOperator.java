package selectedItemMenus;

import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;


import graphicalObjects.ZoomableGraphic;

/**Implementations of this interface perform some task */
public interface MultiSelectionOperator extends Serializable, MenuItemInstall {
	
	/**performs the action.*/
	public void run();
	
	/**Sets the layer selector for this operation*/
	public void setSelector(LayerSelector graphicTreeUI);//sets how selection works
	/**Sets a list of selected items. Note: some implementations of this interface will 
	  call this method themselves*/
	public void setSelection(ArrayList<ZoomableGraphic> array) ;


	/**returns true if any of the selected objects are valid targets*/
	public boolean canUseObjects(LayerSelector graphicTreeUI);
	
	/**returns true if the type of layer selector is compativle with this object
	  */
	public boolean isValidForLayerSelector(LayerSelector graphicTreeUI);
	
	/**Some objects of this class also return a component that provides another way
	  access it. The layer selector of this item must be set before using this*/
	public Component getInputPanel() ;
}
