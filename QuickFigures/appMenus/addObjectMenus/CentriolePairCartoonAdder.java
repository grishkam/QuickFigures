package addObjectMenus;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_LayerTypes.CentriolePairCartoon;
import graphicalObjects_LayerTypes.GraphicLayer;
import standardDialog.GraphicDisplayComponent;

/**Adds a group of two rectangles to the image */
public class CentriolePairCartoonAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	CentriolePairCartoon makePair() {
		return new CentriolePairCartoon();
	}
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
	CentriolePairCartoon pp = makePair();
		gc.add(pp);
		return pp;
	}

	@Override
	public String getCommand() {
		return "Centrioles";
	}

	@Override
	public String getMenuCommand() {
		return null;//"Add Centrioles";
	}
	
	protected BasicGraphicalObject getModelForIcon() {
		return  makePair();
	}
	
	public Icon getIcon() {
		BasicGraphicalObject m = getModelForIcon();
		if (m==null)return null;
		return new GraphicDisplayComponent(getModelForIcon(), .7);
	}

}
