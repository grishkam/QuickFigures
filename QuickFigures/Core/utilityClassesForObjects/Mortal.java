package utilityClassesForObjects;

/**classes that implement this interface may perform some action when the window containing them 
 * is closed. When an item is 'dead' no longer needed, some methods may remove it from many array lists
   in a complex program. */
public interface Mortal {
	public void kill();
	public boolean isDead();
}
