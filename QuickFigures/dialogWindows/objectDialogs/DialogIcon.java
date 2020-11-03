package objectDialogs;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.Icon;

import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import standardDialog.GraphicDisplayComponent;

public class DialogIcon {
	
	
	public static Icon getIcon() {
		return new GraphicDisplayComponent(createIcon() );
	}
	
	static GraphicGroup createIcon() {
		GraphicGroup gg = new GraphicGroup();
		addRect(gg, new Rectangle(2,2,18,16), new Color(0,0,0,0), null);
		
		addRect(gg, new Rectangle(2,2,16,14), Color.BLACK, null);
		addRect(gg, new Rectangle(2,2,16,3), Color.BLACK, Color.gray);
		
		
		addRect(gg, new Rectangle(5,8,11,1), Color.DARK_GRAY, null);
		addRect(gg, new Rectangle(5,12,11,1), Color.DARK_GRAY, null);
		
		return gg;
	}
	

	
	public static ShapeGraphic addRect(GraphicGroup g, Rectangle r, Color c, Color cFill) {
		ShapeGraphic out = RectangularGraphic.blankRect(r, c);
		if (cFill!=null) {out.setFillColor(cFill); out.setFilled(true);}
		out.setAntialize(true);
		out.setStrokeWidth(1);
		
		out.makeDashLess();
		g.getTheLayer().add(out);
		return out;
	}

}
