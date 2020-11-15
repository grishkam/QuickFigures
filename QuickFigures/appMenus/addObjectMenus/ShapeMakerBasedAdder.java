package addObjectMenus;

import java.awt.Color;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.PathGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_LayerTypes.GraphicLayer;
import utilityClassesForObjects.BasicShapeMaker;
import utilityClassesForObjects.CiliaryPocketPathCreator;

public class ShapeMakerBasedAdder extends BasicGraphicAdder {

	BasicShapeMaker shape=new CiliaryPocketPathCreator();
	private String indiName="cilia pocket";
	Color startingColor=Color.green;
	
	public ShapeMakerBasedAdder() {}
	public ShapeMakerBasedAdder(String name, BasicShapeMaker s, Color c) {
		 indiName=name;
		 shape=s;
		 startingColor=c;
	}
	
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		// TODO Auto-generated method stub
		PathGraphic cartoon = BasicShapeMaker.createDefaultCartoon(shape);
		cartoon.setFillColor(startingColor);
		gc.add(cartoon);
		BasicShapeMaker.createUpdatingDialog(cartoon, shape);
		return cartoon;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "Add Cartoon "+shape.getClass().getName();
	}

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "";
	}

	
	protected BasicGraphicalObject getModelForIcon() {
		PathGraphic shape2 = BasicShapeMaker.createDefaultCartoon(shape);
		shape2.setFillColor(startingColor);
		return  shape2;
	}
	
	
}
