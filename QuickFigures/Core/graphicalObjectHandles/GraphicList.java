package graphicalObjectHandles;

import java.awt.Graphics2D;
import java.util.ArrayList;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import utilityClassesForObjects.LocatedObject2D;

/**draws multiple objects that can be seen by user but not clicked on or otherwise used.
  meant only for showing previews, messages and indicators to the user*/
public class GraphicList implements ZoomableGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<LocatedObject2D> items;

	public GraphicList(ArrayList<LocatedObject2D> o2) {
		this.items=o2;
	}

	@Override
	public GraphicLayer getParentLayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentLayer(GraphicLayer parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
		for(LocatedObject2D z: items) try {
			if (z instanceof ZoomableGraphic) {((ZoomableGraphic) z).draw(graphics, cords);}
		} catch (Throwable r) {}
		
	}

}