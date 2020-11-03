package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.CircularGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;

public class CircleGraphicTool extends RectGraphicTool {
	{model=new CircularGraphic(new Rectangle(0,0,15,15));}
	{model.setStrokeColor(Color.black);{super.set=TreeIconWrappingToolIcon.createIconSet(model);}}
	int isArc=0;
	
	
	public CircleGraphicTool(int  arc) {
		
		isArc=arc;
		CircularGraphic mCircle = new CircularGraphic(new Rectangle(0,0,15,15), arc);;
	
		model=mCircle;
		model.setStrokeColor(Color.black);
		super.set=TreeIconWrappingToolIcon.createIconSet(model);
		
	}
	protected RectangularGraphic createNewRect(Rectangle r) {
		CircularGraphic ovalGraphic = new CircularGraphic(r, isArc);

		return ovalGraphic;
	}
	
	@Override
	public String getToolTip() {
			if (isArc>0) return "Draw an Arc";
			return "Draw an Oval";
		}
	
	@Override
	public String getToolName() {
		if (isArc>0) return "Draw Arc";
		return "Draw Oval";
	}
}
