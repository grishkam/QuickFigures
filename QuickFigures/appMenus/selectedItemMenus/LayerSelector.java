package selectedItemMenus;

import java.util.ArrayList;

import applicationAdapters.ImageWrapper;
import graphicalObjects.GraphicSetDisplayContainer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

/**An interface for any ui that allows the user to selected items and layers*/
public interface LayerSelector {
	public GraphicLayer getSelectedLayer();
	public ArrayList<ZoomableGraphic> getSelecteditems();
	public GraphicSetDisplayContainer getGraphicDisplayContainer();
	public ImageWrapper getImageWrapper();
}