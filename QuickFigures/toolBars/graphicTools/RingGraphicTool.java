package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.SimpleRing;

public class RingGraphicTool extends CircleGraphicTool {
	public RingGraphicTool(int arc) {
		super(arc);
		SimpleRing simpleRing = new SimpleRing(new Rectangle(0,0,15,15));
		model=simpleRing;
		simpleRing.arc=arc;
		
		model.setStrokeColor(Color.black);
		super.set=TreeIconWrappingToolIcon.createIconSet(model);
		
	}

	{model=new SimpleRing(new Rectangle(0,0,15,15));}{model.setStrokeColor(Color.black);{super.set=TreeIconWrappingToolIcon.createIconSet(model);}}
	
	public RectangularGraphic createShape(Rectangle r) {
		return new  SimpleRing(r, isArc);
	}
	
	@Override
	public String getToolTip() {
			
			return "Draw a Ring";
		}
	
	@Override
	public String getToolName() {
		return "Draw Ring";
	}
}
