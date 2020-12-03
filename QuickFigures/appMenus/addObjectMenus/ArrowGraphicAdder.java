package addObjectMenus;

import java.awt.Color;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

/**An implementation of graphic adder that adds an arrow*/
public class ArrowGraphicAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrowGraphic model = new ArrowGraphic(); {model.setStrokeColor(Color.darkGray);}
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		ArrowGraphic ag =  getModelForIcon().copy();
		gc.add(ag);;
		return  ag;
	}

	@Override
	public String getCommand() {
		return "Arrow "+unique;
	}

	@Override
	public String getMenuCommand() {
		return "Add Arrow";
	}

	public ArrowGraphic getModelForIcon() {
		return model;
	}
	
	@Override
	public String getMenuPath() {
		return "Shapes";
	}


	
}
