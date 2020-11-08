package graphicalObjects_LayerTypes;


import java.io.Serializable;
import java.util.ArrayList;

import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import layersGUI.LayerStructureChangeListener;
import utilityClassesForObjects.ItemLayer;
import utilityClassesForObjects.Keyed;
import utilityClassesForObjects.Named;
import utilityClassesForObjects.ShowsOptionsDialog;

public interface GraphicLayer extends ItemLayer<ZoomableGraphic>, Named, KnowsParentLayer,Keyed,ZoomableGraphic, ShowsOptionsDialog, Serializable  {

	/***/
	public void setTree(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> tree);
	public LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> getTree();
	public void treeEliminated();
	
	/***/
	public boolean hasItemWithKey(Object key) ;
	public ZoomableGraphic getItemWithKey(Object key) ;
	
	/**Switches the items positions*/
	public void swapItemPositions(ZoomableGraphic z1, ZoomableGraphic z2);
	
	
	
	/**moves the position of z1 to that of z2*/
	public void swapmoveObjectPositionsInArray(ZoomableGraphic z1, ZoomableGraphic z2);
	public void moveItemForward(ZoomableGraphic z1);
	public void moveItemBackward(ZoomableGraphic z1);
	public void setOrder(ArrayList<ZoomableGraphic> oldOrder);
	/**Within this layer, puts object il at the given index. starts from 0*/
	public void moveItemToIndex(ZoomableGraphic il, int index);
	
	/**returns all the drawn graphics, including those inside of sub-layers
	 * but not the sub-layers themselves. does not include those inside of groups*/
	public ArrayList<ZoomableGraphic> getAllGraphics();
	
	/**returns all the sublayers. including those inside other sublayers*/
	public ArrayList<GraphicLayer> getSubLayers();
	
	/**returns the combinations of the get sublayers and getallgraphics commands*/
	public ArrayList<ZoomableGraphic> getObjectsAndSubLayers();
	
	/**returns only the items within this layer directly. this includes sublayer*/
	public ArrayList<ZoomableGraphic> getItemArray();
	
	/**returns all the graphics that have a specified tag. if any implements the tagged
	  interface*/
	public ArrayList<ZoomableGraphic> getGraphics(Object... key);
	
	/**add a graphic. if the item knows its sub-layer id, this should add it to 
	  the correct sublayer if possible*/
	public void add(ZoomableGraphic z);
	/**removes a graphic. if the item is in a sublayer, it should remove the item
	  from the sublayer*/
	public void remove(ZoomableGraphic z);
	
	/**adds the item to this layer. not any sublayer*/
	public void addItemToLayer(ZoomableGraphic z);
	/**removes the item from this layer. not any sublayer*/
	public void removeItemFromLayer(ZoomableGraphic z);
	
	
	/**returns true if this item can accept the given graphic.
	  Called when the user tries to move items between layers*/
	public boolean canAccept(ZoomableGraphic z);
	public boolean canRelease(ZoomableGraphic z);
	
	
	/**returns true is the item is either in this container or a sub-container*/
	public boolean hasItem(ZoomableGraphic z) ;
	public GraphicLayer getSelectedContainer();
	
	public void addLayerStructureChangeListener(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> listener) ;
	public void removeLayerStructureChangeListener(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> listener) ;
	
	public GraphicLayer getTopLevelParentLayer();
	
	
	

}