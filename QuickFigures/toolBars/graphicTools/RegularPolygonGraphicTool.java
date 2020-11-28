package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.RegularPolygonGraphic;

/**A tool bit for drawing regular polygons*/
public class RegularPolygonGraphicTool extends RectGraphicTool {
	
	private RegularPolygonGraphic m;

	public  RegularPolygonGraphicTool(int nVertex) {
		this(new RegularPolygonGraphic(new Rectangle(0,0,10,10), nVertex));

	}
	
	public  RegularPolygonGraphicTool(RegularPolygonGraphic m) {
		this.m=m;
		model=m;
		getModel().setStrokeColor(Color.black);
		super.set=TreeIconWrappingToolIcon.createIconSet(getModel());
	}
	
	
	public RectangularGraphic createShape(Rectangle r) {
		RectangularGraphic out = getModel().copy();
		out.setRectangle(r);
		return out;
	}
	
	
	/**returns the name of the model shape */
	public String getShapeName() {
		if(m!=null) return ""+m.getPolygonType();
		return "Shape";
	}
	
		
}
