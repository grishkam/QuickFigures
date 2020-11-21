package addObjectMenus;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;

class LayerAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		GraphicLayerPane out = new GraphicLayerPane("New Layer Pane");
		if (!gc.canAccept(out))return null;
		gc.add(out);
		out.showOptionsDialog();
		return out;
		
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "addLayer";
	}

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Add Layer";
	}
	public Icon getIcon() {
		return GraphicLayerPane.createDefaultTreeIcon(false);
	}
	
	
}