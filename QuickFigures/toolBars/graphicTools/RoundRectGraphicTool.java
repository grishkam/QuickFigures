package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.RoundedRectangleGraphic;

public class RoundRectGraphicTool extends RectGraphicTool {
	{model=new RoundedRectangleGraphic(new Rectangle(0,0,0,0));}{model.setStrokeColor(Color.black);{super.set=TreeIconWrappingToolIcon.createIconSet(model);}}
	
	public RectangularGraphic createShape(Rectangle r) {
		RoundedRectangleGraphic ouput = new RoundedRectangleGraphic(r);
		ouput.setArch(((RoundedRectangleGraphic)model).getArch());
		ouput.setArcw(((RoundedRectangleGraphic)model).getArcw());
		return ouput;
	}
	
	@Override
	public String getToolTip() {
			
			return "Draw an Rounded Rectangle";
		}
	
	@Override
	public String getToolName() {
		return "Draw Round Rect";
	}
	
		
}
