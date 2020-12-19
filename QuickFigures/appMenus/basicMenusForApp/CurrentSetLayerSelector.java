/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package basicMenusForApp;

import java.io.Serializable;
import java.util.ArrayList;

import applicationAdapters.ImageWrapper;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.FigureDisplayContainer;
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
		LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> tree = getGraphicDisplayContainer().getTopLevelLayer().getTree();
		if (tree!=null)
		return getGraphicDisplayContainer().getTopLevelLayer().getTree().getSelectedLayer();
		return getGraphicDisplayContainer().getTopLevelLayer();
	}

	@Override
	public ArrayList<ZoomableGraphic> getSelecteditems() {
		FigureDisplayContainer contain = getGraphicDisplayContainer();
		if (contain==null) return new ArrayList<ZoomableGraphic>();
		ArrayList<ZoomableGraphic> all = contain.getTopLevelLayer().getAllGraphics();
		ArraySorter.removeNonSelectionItems(all);
		return all;
	}

	@Override
	public FigureDisplayContainer getGraphicDisplayContainer() {
		return new CurrentFigureSet().getCurrentlyActiveOne();
	}

	@Override
	public ImageWrapper getImageWrapper() {
		return getGraphicDisplayContainer().getAsWrapper();
	}
	


}
