package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.RegularPolygonGraphic;

public class RegularPolygonGraphicTool extends RectGraphicTool {
	
	private RegularPolygonGraphic m;

	public  RegularPolygonGraphicTool(int nVertex) {
		this(new RegularPolygonGraphic(new Rectangle(0,0,10,10), nVertex));

	}
	
	public  RegularPolygonGraphicTool(RegularPolygonGraphic m) {
		this.m=m;
		model=m;
		model.setStrokeColor(Color.black);
		super.set=TreeIconWrappingToolIcon.createIconSet(model);
	}
	
	
	public RectangularGraphic createShape(Rectangle r) {
		RectangularGraphic out = model.copy();
		out.setRectangle(r);
		return out;
	}
	
	@Override
	public String getToolTip() {
			if(m!=null) return "Draw "+m.getPolygonType();
			return "Draw Polygon";
		}
	
	@Override
	public String getToolName() {
		if(m!=null) return "Draw "+m.getPolygonType();
		return "Draw Shape";
	}
	
		
}
