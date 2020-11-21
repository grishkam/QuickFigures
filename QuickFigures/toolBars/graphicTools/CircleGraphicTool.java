package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.CircularGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;

public class CircleGraphicTool extends RectGraphicTool {
	public static final int SIMPLE_CIRCLE = 0;

	{model=new CircularGraphic(new Rectangle(0,0,15,15));}
	{getModel().setStrokeColor(Color.black);{super.set=TreeIconWrappingToolIcon.createIconSet(getModel());}}
	int isArc=SIMPLE_CIRCLE;
	
	
	public CircleGraphicTool() {this(0);}
	public CircleGraphicTool(int  arc) {
		
		isArc=arc;
		CircularGraphic mCircle = new CircularGraphic(new Rectangle(0,0,15,15), arc);;
	
		model=mCircle;
		getModel().setStrokeColor(Color.black);
		super.set=TreeIconWrappingToolIcon.createIconSet(getModel());
		
	}
	public RectangularGraphic createShape(Rectangle r) {
		CircularGraphic ovalGraphic = new CircularGraphic(r, isArc);

		return ovalGraphic;
	}
	
	
	/**returns the name of the model shape (default is rectangle)*/
	public String getShapeName() {
		if (isArc>0) return "Arc";
		return "Oval";
	}
}
