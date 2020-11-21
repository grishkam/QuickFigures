package addObjectMenus;

import graphicalObjects.GraphicEncoder;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

public class SavedGraphicAdder extends BasicGraphicAdder {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		
		GraphicEncoder ag = new GraphicEncoder(gc);
		
		ZoomableGraphic ob = ag.readFromUserSelectedFile();
	
		gc.add(ob);
		
		if (selector.getGraphicDisplayContainer()!=null)selector.getGraphicDisplayContainer().onItemLoad(ob);
		return ob;
	}

	@Override
	public String getCommand() {
		return "open";
	}

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Saved Graphic";
	}
}
