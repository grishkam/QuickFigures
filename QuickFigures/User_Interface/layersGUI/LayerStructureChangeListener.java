package layersGUI;


/**Interphase to be called when some multilayer model 
  has on object move between layers*/
public interface LayerStructureChangeListener<Item, Layer extends Item> {
	/**called when an item is added to a graphic container*/
	public void itemsSwappedInContainer( Layer gc, Item z1, Item z2 );
	/**called when an item is added to a graphic container*/
	public void itemRemovedFromContainer( Layer gc, Item z);
	/**called when an item is added to a graphic container*/
	public void itemAddedToContainer( Layer gc, Item z) ;
	
	/***/
	public Layer getSelectedLayer();
	

}
