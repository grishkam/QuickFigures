package graphicalObjects;

import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.LayerStructureChangeListener;

public interface KnowsTree {
	public void setTree(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>  t) ;
}
