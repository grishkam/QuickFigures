package graphicalObjects_LayerTypes;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;

public interface ZoomableGraphicArrayContainer {
	/**returns only the items within this layer directly. this includes sublayer*/
	public ArrayList<ZoomableGraphic> getItemArray();
}
