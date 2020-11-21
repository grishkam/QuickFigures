package basicMenusForApp;

import graphicalObjects.FigureDisplayContainer;

/**A layer selector that returns the selected items in whatever set is the currently active one*/
public class SelectedSetLayerSelector extends CurrentSetLayerSelector {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FigureDisplayContainer container;

	public SelectedSetLayerSelector(FigureDisplayContainer cont) {
		this.container=cont;
	} 

	@Override
	public FigureDisplayContainer getGraphicDisplayContainer() {
		return container;
	}

}
