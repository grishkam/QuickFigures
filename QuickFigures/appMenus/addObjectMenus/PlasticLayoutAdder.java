package addObjectMenus;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_LayoutObjects.PlasticPanelLayoutGraphic;

public class PlasticLayoutAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		GraphicLayerPane l22 = new GraphicLayerPane("Plastic Layout Layer");
		gc.add(l22);
		PanelLayoutGraphic p = getNewLayout() ;
		l22.add(p);
		p.select();
		return l22;
	}
	
	public PanelLayoutGraphic getNewLayout() {
		return  new PlasticPanelLayoutGraphic();
	}

	@Override
	public String getCommand() {
		return "Add Flexible Layout";
	}

	@Override
	public String getMenuCommand() {
		return "Add Flexible Layout";
	}

}
