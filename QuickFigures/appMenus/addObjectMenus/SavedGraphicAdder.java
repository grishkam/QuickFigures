package addObjectMenus;

import graphicalObjects.GraphicEncoder;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

public class SavedGraphicAdder extends BasicGraphicAdder {
	
	
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
		// TODO Auto-generated method stub
		return "open";
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return "Saved Graphic";
	}
}
