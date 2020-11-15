package basicMenusForApp;

import graphicalObjects.GraphicSetDisplayContainer;

/**A layer selector that returns the selected items in whatever set is the currently active one*/
public class SelectedSetLayerSelector extends CurrentSetLayerSelector {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicSetDisplayContainer container;

	public SelectedSetLayerSelector(GraphicSetDisplayContainer cont) {
		this.container=cont;
	} 

	@Override
	public GraphicSetDisplayContainer getGraphicDisplayContainer() {
		return container;
	}

}
