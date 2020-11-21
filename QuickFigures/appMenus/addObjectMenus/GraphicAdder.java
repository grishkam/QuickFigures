package addObjectMenus;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import selectedItemMenus.LayerSelector;
import selectedItemMenus.MenuItemInstall;

/**This interface is for items that are included in the adding menu*/
public interface GraphicAdder extends MenuItemInstall {
		/**Adds an object to the layer and returns it*/
		public ZoomableGraphic add(GraphicLayer gc);
		public String getCommand();
		public String getMenuCommand();
		public Icon getIcon();

		/**Certain implementations of this use a layer selector to obtain more inforamtion*/
		
		/**returns a keyboard shortcut*/
		public Character getKey();
		
		/**sets the selection system*/
		public void setSelector(LayerSelector selector);
		public void run();
	
	
}
