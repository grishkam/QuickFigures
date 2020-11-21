package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.CrossGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.RightTriangleGraphic;
import graphicalObjects_BasicShapes.SimpleLineGraphic;

public class RightTriangleGraphicTool extends RectGraphicTool {
	private int type;

	void innitiateModel() {
		RightTriangleGraphic model1=new RightTriangleGraphic(new Rectangle(0,0,0,0));
		if (type>9) {
			 model1=new SimpleLineGraphic(new Rectangle(0,0,0,0));
		}
		model=model1;
		model1.setType(type);
		if (type>9&&type<20) model1.setType(type-10);
		
		if (type==20) {
			 model1=new CrossGraphic(new Rectangle(0,0,0,0));
			 model=model1;
		}
		getModel().setStrokeColor(Color.black);
		{super.set=TreeIconWrappingToolIcon.createIconSet(getModel());}
	}
	
	public RightTriangleGraphicTool() {}
	public RightTriangleGraphicTool(int type) {
		this.type=type;
		/**model=new RightTriangleGraphic(new Rectangle(0,0,0,0));}
		{model.setStrokeColor(Color.black);
		{super.set=TreeIconWrappingToolIcon.createIconSet(model);}*/
		innitiateModel();
		if(getModel() instanceof RightTriangleGraphic) {
			RightTriangleGraphic m=(RightTriangleGraphic) getModel();
			m.setType(type);
		}
	}
	
	public RectangularGraphic createShape(Rectangle r) {
		RightTriangleGraphic output = new RightTriangleGraphic(r);
		if (type>9) output= new SimpleLineGraphic(r);
		output.setType(type);
		if (type>9) output.setType(type-10);
		if (type==20) output= new CrossGraphic(r);
		return output;
	}

	/**returns the name of the model shape (default is rectangle)*/
	public String getShapeName() {
		if (type==20) 	return "Cross";
		if (type>9) 	return "Simple Line";
		return "Right Triangle";
	}
	
		
}
