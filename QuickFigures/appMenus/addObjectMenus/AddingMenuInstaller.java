package addObjectMenus;

/**This interface was written for programmers to have a way to add items to the adding menu
 * @see ObjectAddingMenu. Any objects of this class that are added to the bonusAdders ArrayList 
 * in the adding menu will be called every time the menubar is made*/
public interface AddingMenuInstaller {
	
	/***/
	public void installOntoMenu(ObjectAddingMenu menu);

}
