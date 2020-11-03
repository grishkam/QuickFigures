package addObjectMenus;

import java.awt.Color;
import java.awt.Rectangle;

import graphicalObjects_BasicShapes.CircularGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;

public class OvalGraphicAdder  extends RectangleAdder{
	
	

	private CircularGraphic model=new CircularGraphic(new Rectangle(0,0,50,50)); {
		model.setStrokeColor(Color.BLUE.darker());
		model.setDashes(new float[] {8,12});
		}
	{
		model.setStrokeColor(Color.blue);
	}

	@Override
	public String getCommand() {
		return "Add elipse"+unique;
	}

	@Override
	public String getMessage() {
		return "Add Elipse";
	}
	
	public RectangularGraphic getModelForIcon() {
		return model;
	}


	
}
