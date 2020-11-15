package addObjectMenus;

import java.awt.Color;
import java.awt.Rectangle;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

public class RectangleAdder extends BasicGraphicAdder {

	private RectangularGraphic model=new RectangularGraphic(new Rectangle(0,0,50,50));
	{
		 model.setStrokeColor(Color.blue);
	}
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		RectangularGraphic rg = getModelForIcon().copy();
		
		gc.add(rg);
		rg.showOptionsDialog();
		return rg;
	}
	
	

	@Override
	public String getCommand() {
		return "Add rect"+unique;
	}

	@Override
	public String getMenuCommand() {
		return "Add Rectangle";
	}

	public RectangularGraphic getModelForIcon() {
		return model;
	}

	public void setModel(RectangularGraphic model) {
		this.model = model;
	}
}
