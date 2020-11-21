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
		
		getModel().setStrokeColor(Color.black);
		super.set=TreeIconWrappingToolIcon.createIconSet(getModel());
		
	}

	{model=new SimpleRing(new Rectangle(0,0,15,15));}{getModel().setStrokeColor(Color.black);{super.set=TreeIconWrappingToolIcon.createIconSet(getModel());}}
	
	public RectangularGraphic createShape(Rectangle r) {
		return new  SimpleRing(r, isArc);
	}
	
	/**returns the name of the model shape */
	public String getShapeName() {
		return "Ring";
	}
	
	
}
