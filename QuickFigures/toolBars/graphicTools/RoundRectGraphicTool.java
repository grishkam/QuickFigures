package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.RoundedRectangleGraphic;

public class RoundRectGraphicTool extends RectGraphicTool {
	{model=new RoundedRectangleGraphic(new Rectangle(0,0,0,0));}{getModel().setStrokeColor(Color.black);{super.set=TreeIconWrappingToolIcon.createIconSet(getModel());}}
	
	public RectangularGraphic createShape(Rectangle r) {
		RoundedRectangleGraphic ouput = new RoundedRectangleGraphic(r);
		ouput.setArch(((RoundedRectangleGraphic)getModel()).getArch());
		ouput.setArcw(((RoundedRectangleGraphic)getModel()).getArcw());
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
