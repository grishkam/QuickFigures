package graphicalObjects;

import java.io.IOException;
import java.util.ArrayList;

import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.LayerStructureChangeListener;
import utilityClasses1.ArraySorter;

public class LayerStructureChangeListenerList extends ArrayList<LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>> implements LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void itemsSwappedInContainer(GraphicLayer gc, ZoomableGraphic z1,
			ZoomableGraphic z2) {
		for(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> ar:this) {
		if (ar==null) continue;
			ar.itemsSwappedInContainer(gc, z1, z2);
		}
		
	}

	@Override
	public void itemRemovedFromContainer(GraphicLayer gc, ZoomableGraphic z) {
		for(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> ar:this) {
			if (ar==null) continue;
			ar.itemRemovedFromContainer(gc, z);
		}
	}

	@Override
	public void itemAddedToContainer(GraphicLayer gc, ZoomableGraphic z) {
		for(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> ar:this) {
			if (ar==null) continue;
			ar.itemAddedToContainer(gc, z);
		}
		
	}

	@Override
	public GraphicLayer getSelectedLayer() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException {
		ArraySorter.removeDeadItems(this);
		ArraySorter.removeNonSerialiazble(this);
		out.defaultWriteObject();
	}

	
	
	
}
