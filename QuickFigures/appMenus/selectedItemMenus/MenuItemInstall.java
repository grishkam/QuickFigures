package selectedItemMenus;

import java.awt.Font;

import javax.swing.Icon;

/**This interface is for any class that specifies the traits of a menu item*/
public interface MenuItemInstall {
	/**if the item has a special menu path, this returns a string to help find it*/
	public String getMenuPath();
	/**The menu text that appears in the JMenu for this item command for this item*/
	public String getMenuCommand();
	/**The icon for the item. this may appear either as part of the menu item or on a button*/
	public Icon getIcon();
	/**if the menu item must have a non-default font, returns it*/
	public Font getMenuItemFont();
	
	
	
}
