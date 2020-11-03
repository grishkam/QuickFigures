package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import graphicalObjects_BasicShapes.CircularGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.RhombusGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import logging.IssueLog;
import utilityClassesForObjects.DefaultPaintProvider;
import utilityClassesForObjects.PaintProvider;
import utilityClassesForObjects.RainbowPaintProvider;
import utilityClassesForObjects.RectangleEdgePosisions;
import utilityClassesForObjects.RectangleEdges;

/**A class for creation of a folder icon object*/
public class BrightNessIconGraphic extends GraphicGroup  implements  RectangleEdgePosisions {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	int form;
	private RhombusGraphic r;
	private CircularGraphic r2;
	private CircularGraphic r3;
	
	ArrayList<ShapeGraphic> spokeDots=new ArrayList<ShapeGraphic>();
	
	public BrightNessIconGraphic(Color folderColor, int open) {
		this.form=open;
		this.setFolderColor(folderColor);
		createItems() ;
		setItemColors();
		
		addItems();
	}
	
	
	
	
	public void createItems() {
		r = new RhombusGraphic();
		r.setRectangle(new Rectangle(0,2,14,10));
		r.setAntialize(true);
		Rectangle rectsize = new Rectangle(4,5,10,10);
		
		
		r2 = CircularGraphic.filledCircle(rectsize);
		r3= CircularGraphic.halfCircle(rectsize);
		r3.setFillColor(Color.white);
		r2.setStrokeWidth(2);
		r2.setStrokeColor(Color.black);
	
		for(int i=0; i<8; i++) {
			ShapeGraphic dot = CircularGraphic.filledCircle(new Rectangle(0,0,2,2));
			if(form==1) dot = RectangularGraphic.filledRect(new Rectangle(-2,0,4,2));
			dot.setLocation(8+8*Math.cos(Math.PI/4*i), 9+8*Math.sin(Math.PI/4*i));
			dot.setFillColor(Color.black);
			dot.setAngle(-Math.PI/4*i);
			spokeDots.add(dot);
		}
		
		 setItemColors() ;
	}
	
	
	/**returns the shape of Rectangle 1. this will depend on the circumstances*/
	private Rectangle getR1rect() {
		 {
			return new Rectangle(0,2,14,10);
		}
	}
	
	public double getR1angle() {
		 {
			return 0;
		}
	}
	
	/**alters r1's properties depending on the condition of the folder.
	   open or closed*/
	public void setR1Dims() {
		r.setRectangle(getR1rect());
		r.setAngleBend(getR1angle());
	}
	
	public void setOpen(int o) {
		this.form=o;
		this.setR1Dims();
		this.setItemColors();
	}
	
	public void setColor(Color c) {
		setFolderColor(c);
		setItemColors();
	}
	public void setItemColors() {
		setR1Dims();
		
		
		 r2.setFillColor(getIconColor());
			r3.setFillColor(Color.white);
			
			
		//	r.setFilled(true);
			PaintProvider pp = r.getFillPaintProvider();
			
			pp.setColor(getIconColor().brighter());
			pp.setColor(1, getIconColor().darker());
			r.setStrokeColor(getIconColor().darker());
			
			if (form==1) {
				pp.setColor(getIconColor().darker());
				//r.setRectangle(new Rectangle(0,4,14,8));
				r.setStrokeColor(getIconColor().darker().darker());
		}
			
			
		
		
			
			
			
	}
	
	public void addItems() {
		
		getTheLayer().add(r2);
		getTheLayer().add(r3);
		for(ShapeGraphic d:spokeDots) {	getTheLayer().add(d);}
	}



	public Color getIconColor() {
		if (iconColor==null) {
			iconColor=Color.black;
		}
		return iconColor;
	}



	public void setFolderColor(Color folderColor) {
		this.iconColor = folderColor;
	}
	
}