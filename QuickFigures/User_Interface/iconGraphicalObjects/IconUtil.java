package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;

import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_BasicShapes.CircularGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import standardDialog.GraphicObjectDisplayBasic;

/**this class consists of static methods for creating a few of the icons used*/
public class IconUtil {

	public static Icon createFolderIcon(boolean open, Color folderColor) {
		
		return new folderIcon( open,folderColor);
	}
	
	public static class folderIcon extends GraphicObjectDisplayBasic<FolderIconGraphic> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public folderIcon(boolean open, Color c) {
			this.setCurrentDisplayObject(new FolderIconGraphic(c,open));
		}
		
	}

public static Icon createBrightnessIcon(int open, Color folderColor) {
		
		return new BrightnessIcon( open,folderColor);
	}
	
	public static class BrightnessIcon extends GraphicObjectDisplayBasic<BrightNessIconGraphic> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public BrightnessIcon(int open, Color c) {
			this.setCurrentDisplayObject(new BrightNessIconGraphic(c,open));
		}
		
	}
	
	public static GraphicGroup createAllIcon(String all) {
		GraphicGroup gg = new GraphicGroup();
		addRect(gg, new Rectangle(2,2,18,16), new Color(0,0,0,0), null);
		
		TextGraphic z = new TextGraphic(all);
		z.moveLocation(2, 15);
		gg.getTheLayer().add(z);
		
		return gg;
	}
	public static Icon createMoreIcon(String all) {
		
		GraphicGroup gg = new GraphicGroup();
		addRect(gg, new Rectangle(2,2,18,16), new Color(0,0,0,0), null);
		
		RectangularGraphic circleb = CircularGraphic.blankOval(new Rectangle(2,2,18,16), new Color(0,0,0), 0);
		circleb.setStrokeColor(Color.black);
		circleb.setFilled(false);
		circleb.setStrokeWidth(1);
		circleb.moveLocation(-2,-2);
		
		ArrowGraphic z = new ArrowGraphic();
		z.setPoints(new Point(11,9), new Point(12,9));
		z.setArrowTipAngle(80);
		z.setStrokeColor(Color.black);
		z.setArrowHeadSize(5);
		z.setArrowStyle(ArrowGraphic.openHead);
		z.setStrokeWidth(2);
	//	gg.getTheLayer().add(circleb);
		gg.getTheLayer().add(z);
		
		GraphicObjectDisplayBasic<GraphicGroup> object = new GraphicObjectDisplayBasic<GraphicGroup>(gg);
		return object;
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
