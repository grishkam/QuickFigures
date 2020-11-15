package basicMenusForApp;

import java.io.Serializable;
import java.util.ArrayList;

import applicationAdapters.ImageWrapper;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.GraphicSetDisplayContainer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.LayerStructureChangeListener;
import selectedItemMenus.LayerSelector;
import utilityClasses1.ArraySorter;

/**A layer selector that returns the selected items in whatever set is the currently active one*/
public class CurrentSetLayerSelector  implements LayerSelector , Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public GraphicLayer getSelectedLayer() {
		LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> tree = getGraphicDisplayContainer().getGraphicLayerSet().getTree();
		if (tree!=null)
		return getGraphicDisplayContainer().getGraphicLayerSet().getTree().getSelectedLayer();
		return getGraphicDisplayContainer().getGraphicLayerSet();
	}

	@Override
	public ArrayList<ZoomableGraphic> getSelecteditems() {
		GraphicSetDisplayContainer contain = getGraphicDisplayContainer();
		if (contain==null) return new ArrayList<ZoomableGraphic>();
		ArrayList<ZoomableGraphic> all = contain.getGraphicLayerSet().getAllGraphics();
		ArraySorter.removeNonSelectionItems(all);
		return all;
	}

	@Override
	public GraphicSetDisplayContainer getGraphicDisplayContainer() {
		return new CurrentFigureSet().getCurrentlyActiveOne();
	}

	@Override
	public ImageWrapper getImageWrapper() {
		return getGraphicDisplayContainer().getAsWrapper();
	}
	


}
